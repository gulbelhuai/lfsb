#!/usr/bin/env bash
#
# 廊坊社保后端 - 本地构建并部署到生产服务器
# 用法:
#   ./.cursor/shell/backend_deploy.sh              # 完整部署（构建 + 上传 + 重启）
#   ./.cursor/shell/backend_deploy.sh --skip-build   # 跳过构建，仅上传最新包并重启
#   ./.cursor/shell/backend_deploy.sh --dry-run      # 仅打印将要执行的步骤
#
set -euo pipefail

# ── 可配置项（按需修改）────────────────────────────────────────────
REMOTE_USER="${DEPLOY_USER:-root}"
REMOTE_HOST="${DEPLOY_HOST:-xxest.com}"
REMOTE_DIR="${DEPLOY_DIR:-/data/web/shebao.xxest.com/server}"
REMOTE_JAR_NAME="${DEPLOY_JAR_NAME:-ruoyi-admin.jar}"
APP_PORT="${DEPLOY_APP_PORT:-8087}"
STARTUP_TIMEOUT="${DEPLOY_STARTUP_TIMEOUT:-120}"   # 等待启动成功的秒数
SHUTDOWN_TIMEOUT="${DEPLOY_SHUTDOWN_TIMEOUT:-30}"  # 等待旧进程退出的秒数
PUBLIC_API_URL="${DEPLOY_PUBLIC_API_URL:-https://shebao.xxest.com/api/}"
API_WELCOME_KEYWORD="${DEPLOY_API_WELCOME_KEYWORD:-欢迎使用县级养老补贴发放系统后台管理框架}"
API_VERIFY_TIMEOUT="${DEPLOY_API_VERIFY_TIMEOUT:-30}"  # 公网 API 验证超时（秒）

# ── 内部变量 ──────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
TARGET_DIR="${PROJECT_ROOT}/ruoyi-admin/target"
JAR_GLOB="shebao-server-*.jar"
REMOTE="${REMOTE_USER}@${REMOTE_HOST}"

SKIP_BUILD=false
DRY_RUN=false

# ── 颜色与输出 ────────────────────────────────────────────────────
if [[ -t 1 ]]; then
  C_RESET='\033[0m'
  C_BOLD='\033[1m'
  C_GREEN='\033[0;32m'
  C_YELLOW='\033[0;33m'
  C_RED='\033[0;31m'
  C_CYAN='\033[0;36m'
else
  C_RESET='' C_BOLD='' C_GREEN='' C_YELLOW='' C_RED='' C_CYAN=''
fi

STEP=0

log_info()  { printf '%b\n' "${C_CYAN}[INFO]${C_RESET} $*"; }
log_ok()    { printf '%b\n' "${C_GREEN}[OK]${C_RESET}   $*"; }
log_warn()  { printf '%b\n' "${C_YELLOW}[WARN]${C_RESET} $*"; }
log_error() { printf '%b\n' "${C_RED}[ERROR]${C_RESET} $*" >&2; }

step() {
  STEP=$((STEP + 1))
  printf '\n%b\n' "${C_BOLD}━━━ 步骤 ${STEP}: $* ━━━${C_RESET}"
}

die() {
  log_error "$*"
  exit 1
}

run() {
  log_info "执行: $*"
  if [[ "${DRY_RUN}" == true ]]; then
    return 0
  fi
  "$@"
}

run_ssh() {
  log_info "远程执行: $*"
  if [[ "${DRY_RUN}" == true ]]; then
    return 0
  fi
  ssh -o BatchMode=yes -o ConnectTimeout=15 "${REMOTE}" "$@"
}

usage() {
  cat <<EOF
用法: $(basename "$0") [选项]

选项:
  --skip-build   跳过 Maven 构建，使用 target 目录下最新的 ${JAR_GLOB}
  --dry-run      只显示步骤，不实际执行
  -h, --help     显示此帮助

环境变量（可选覆盖默认配置）:
  DEPLOY_USER / DEPLOY_HOST / DEPLOY_DIR
  DEPLOY_JAR_NAME / DEPLOY_APP_PORT
  DEPLOY_STARTUP_TIMEOUT / DEPLOY_SHUTDOWN_TIMEOUT
  DEPLOY_PUBLIC_API_URL / DEPLOY_API_WELCOME_KEYWORD
EOF
}

parse_args() {
  while [[ $# -gt 0 ]]; do
    case "$1" in
      --skip-build) SKIP_BUILD=true; shift ;;
      --dry-run)    DRY_RUN=true; shift ;;
      -h|--help)    usage; exit 0 ;;
      *) die "未知参数: $1（使用 --help 查看用法）" ;;
    esac
  done
}

# ── 前置检查 ──────────────────────────────────────────────────────
check_prerequisites() {
  step "检查本地环境"

  [[ -f "${PROJECT_ROOT}/pom.xml" ]] \
    || die "未找到项目根 pom.xml，脚本目录可能放错了位置: ${PROJECT_ROOT}"

  [[ -d "${PROJECT_ROOT}/ruoyi-admin" ]] \
    || die "未找到 ruoyi-admin 模块: ${PROJECT_ROOT}/ruoyi-admin"

  local missing=()
  for cmd in mvn scp ssh curl; do
    command -v "${cmd}" >/dev/null 2>&1 || missing+=("${cmd}")
  done
  [[ ${#missing[@]} -eq 0 ]] || die "缺少命令: ${missing[*]}"

  if [[ "${SKIP_BUILD}" == false ]]; then
    if ! java -version 2>&1 | grep -qE 'version "1[7-9]|version "[2-9][0-9]'; then
      log_warn "当前 Java 可能不是 17+，本项目需要 JDK 17 构建"
      java -version 2>&1 || true
    fi
  fi

  if [[ "${DRY_RUN}" == false ]]; then
    run_ssh "test -d '${REMOTE_DIR}'" \
      || die "远程目录不存在或 SSH 无法连接: ${REMOTE}:${REMOTE_DIR}"
    run_ssh "test -f '${REMOTE_DIR}/startup.sh' && test -f '${REMOTE_DIR}/shutdown.sh'" \
      || die "远程缺少 startup.sh / shutdown.sh: ${REMOTE_DIR}"
  fi

  log_ok "环境检查通过"
  log_info "项目根目录: ${PROJECT_ROOT}"
  log_info "部署目标:   ${REMOTE}:${REMOTE_DIR}"
}

# ── 构建 ──────────────────────────────────────────────────────────
build_project() {
  if [[ "${SKIP_BUILD}" == true ]]; then
    step "跳过构建 (--skip-build)"
    return 0
  fi

  step "Maven 构建 (clean package, 跳过测试)"
  (
    cd "${PROJECT_ROOT}"
    run mvn -DskipTests=true clean package
  ) || die "Maven 构建失败"
  log_ok "构建完成"
}

# ── 定位产物 jar ──────────────────────────────────────────────────
find_built_jar() {
  step "定位构建产物"

  local jars=()
  local jar

  # shellcheck disable=SC2012
  for jar in $(ls -t "${TARGET_DIR}"/${JAR_GLOB} 2>/dev/null); do
    # 排除 spring-boot repackage 前的原始 jar
    [[ "${jar}" == *.jar.original ]] && continue
    jars+=("${jar}")
  done

  [[ ${#jars[@]} -gt 0 ]] || die "未找到 ${TARGET_DIR}/${JAR_GLOB}，请先执行构建"

  BUILT_JAR="${jars[0]}"
  JAR_BASENAME="$(basename "${BUILT_JAR}")"
  JAR_SIZE="$(du -h "${BUILT_JAR}" | awk '{print $1}')"

  log_ok "将部署: ${JAR_BASENAME} (${JAR_SIZE})"
  log_info "完整路径: ${BUILT_JAR}"
}

# ── 上传 ──────────────────────────────────────────────────────────
upload_jar() {
  step "上传到服务器"
  run scp -o BatchMode=yes -o ConnectTimeout=15 \
    "${BUILT_JAR}" "${REMOTE}:${REMOTE_DIR}/${JAR_BASENAME}" \
    || die "SCP 上传失败"
  log_ok "已上传至 ${REMOTE}:${REMOTE_DIR}/${JAR_BASENAME}"
}

# ── 远程部署与重启 ────────────────────────────────────────────────
remote_deploy() {
  step "远程替换 jar 并重启服务"

  local remote_script
  remote_script=$(cat <<'REMOTE_EOF'
set -euo pipefail

REMOTE_DIR="$1"
JAR_BASENAME="$2"
REMOTE_JAR_NAME="$3"
SHUTDOWN_TIMEOUT="$4"

# 仅匹配 Java 进程，避免 pgrep -f 误匹配当前 shell 的参数
is_java_app_running() {
  ps -ef | grep '[j]ava' | grep -q "${REMOTE_JAR_NAME}"
}

cd "${REMOTE_DIR}"

# 备份当前运行中的 jar
if [[ -f "${REMOTE_JAR_NAME}" ]]; then
  backup="${REMOTE_JAR_NAME}.bak.$(date +%Y%m%d-%H%M%S)"
  cp -p "${REMOTE_JAR_NAME}" "${backup}"
  echo "[remote] 已备份旧包: ${backup}"
fi

# 替换为新包
cp -f "${JAR_BASENAME}" "${REMOTE_JAR_NAME}"
echo "[remote] 已更新: ${REMOTE_JAR_NAME} <- ${JAR_BASENAME}"

# 停止旧进程
if [[ -f shutdown.sh ]]; then
  echo "[remote] 正在停止服务..."
  sh shutdown.sh || true

  # 等待进程退出
  elapsed=0
  while [[ ${elapsed} -lt ${SHUTDOWN_TIMEOUT} ]]; do
    if ! is_java_app_running; then
      break
    fi
    sleep 1
    elapsed=$((elapsed + 1))
  done

  if is_java_app_running; then
    echo "[remote][ERROR] 旧进程未在 ${SHUTDOWN_TIMEOUT}s 内退出" >&2
    exit 1
  fi
  echo "[remote] 旧进程已停止"
else
  echo "[remote][ERROR] 未找到 shutdown.sh" >&2
  exit 1
fi

# 启动新进程（内联 startup.sh 核心逻辑，避免 tail -f 阻塞或 timeout 误杀 Java 子进程）
JAVA_CMD="/data/jdk/jdk-17.0.2/bin/java"
JVM_OPTS="-Dname=${REMOTE_JAR_NAME} -Duser.timezone=Asia/Shanghai -Xms256m -Xmx256m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=128m -XX:+HeapDumpOnOutOfMemoryError"
TIMESTAMP="$(date +"%Y-%m-%d_%H-%M-%S")"
LOG_FILE="${REMOTE_DIR}/console_${TIMESTAMP}.log"

if [[ ! -f "${REMOTE_DIR}/.env" ]]; then
  echo "[remote][ERROR] 未找到 .env 文件: ${REMOTE_DIR}/.env" >&2
  exit 1
fi

if is_java_app_running; then
  echo "[remote][ERROR] 服务仍在运行，无法启动" >&2
  exit 1
fi

echo "[remote] 正在启动服务..."
set -a
# shellcheck disable=SC1091
source "${REMOTE_DIR}/.env"
set +a

nohup ${JAVA_CMD} ${JVM_OPTS} -jar "${REMOTE_DIR}/${REMOTE_JAR_NAME}" > "${LOG_FILE}" 2>&1 &
disown

sleep 1
if ! is_java_app_running; then
  echo "[remote][ERROR] Java 进程未能启动" >&2
  [[ -f "${LOG_FILE}" ]] && tail -20 "${LOG_FILE}"
  exit 1
fi

echo "[remote] Start ${REMOTE_JAR_NAME} success..."
echo "[remote] Log path: ${LOG_FILE}"
echo "[remote] LOG_FILE=${LOG_FILE}"
REMOTE_EOF
)

  if [[ "${DRY_RUN}" == true ]]; then
    log_info "（dry-run）将远程: 备份 → 替换 jar → shutdown → startup"
    return 0
  fi

  local remote_output
  remote_output="$(run_ssh bash -s -- "${REMOTE_DIR}" "${JAR_BASENAME}" "${REMOTE_JAR_NAME}" "${SHUTDOWN_TIMEOUT}" <<< "${remote_script}")" \
    || die "远程部署失败"

  echo "${remote_output}"

  REMOTE_LOG_FILE="$(echo "${remote_output}" | grep '^\[remote\] LOG_FILE=' | tail -1 | sed 's/^\[remote\] LOG_FILE=//')"
  [[ -n "${REMOTE_LOG_FILE}" ]] || log_warn "未获取到远程日志路径，启动检测可能不准确"
}

# ── 启动成功检测 ──────────────────────────────────────────────────
wait_for_startup() {
  step "检测服务是否启动成功 (最多 ${STARTUP_TIMEOUT}s)"

  if [[ "${DRY_RUN}" == true ]]; then
    log_info "（dry-run）将轮询日志中的 'Started RuoYiApplication'"
    return 0
  fi

  local check_script
  check_script=$(cat <<'CHECK_EOF'
set -euo pipefail

REMOTE_DIR="$1"
REMOTE_JAR_NAME="$2"
APP_PORT="$3"
STARTUP_TIMEOUT="$4"
LOG_FILE="$5"

is_java_app_running() {
  ps -ef | grep '[j]ava' | grep -q "${REMOTE_JAR_NAME}"
}

success_patterns='Started RuoYiApplication|Tomcat started on port'
fail_patterns='APPLICATION FAILED TO START|Error creating bean|Address already in use|BindException'

seen_process=false
elapsed=0
while [[ ${elapsed} -lt ${STARTUP_TIMEOUT} ]]; do
  if is_java_app_running; then
    seen_process=true
  elif [[ "${seen_process}" == true ]]; then
    echo "[check][ERROR] Java 进程已退出，启动可能失败"
    if [[ -n "${LOG_FILE}" && -f "${LOG_FILE}" ]]; then
      echo "----- 日志末尾 (最后 30 行) -----"
      tail -n 30 "${LOG_FILE}"
    fi
    exit 1
  fi

  if [[ -n "${LOG_FILE}" && -f "${LOG_FILE}" ]]; then
    if grep -qE "${fail_patterns}" "${LOG_FILE}" 2>/dev/null; then
      echo "[check][ERROR] 日志中出现启动失败特征"
      echo "----- 相关日志 -----"
      grep -E "${fail_patterns}" "${LOG_FILE}" | tail -5
      echo "----- 日志末尾 (最后 30 行) -----"
      tail -n 30 "${LOG_FILE}"
      exit 1
    fi

    if grep -qE "${success_patterns}" "${LOG_FILE}" 2>/dev/null; then
      started_line="$(grep -E 'Started RuoYiApplication' "${LOG_FILE}" | tail -1)"
      echo "[check][OK] ${started_line:-服务已启动}"
      # 额外检测端口（若 ss 可用）
      if command -v ss >/dev/null 2>&1; then
        if ss -lnt | grep -q ":${APP_PORT} "; then
          echo "[check][OK] 端口 ${APP_PORT} 已在监听"
        else
          echo "[check][WARN] 日志显示已启动，但端口 ${APP_PORT} 暂未监听"
        fi
      fi
      exit 0
    fi
  fi

  sleep 3
  elapsed=$((elapsed + 3))
  echo "[check] 等待启动... (${elapsed}/${STARTUP_TIMEOUT}s)"
done

echo "[check][ERROR] 超时 (${STARTUP_TIMEOUT}s) 未检测到启动成功"
if [[ -n "${LOG_FILE}" && -f "${LOG_FILE}" ]]; then
  echo "----- 日志末尾 (最后 40 行) -----"
  tail -n 40 "${LOG_FILE}"
fi
exit 1
CHECK_EOF
)

  run_ssh bash -s -- \
    "${REMOTE_DIR}" "${REMOTE_JAR_NAME}" "${APP_PORT}" "${STARTUP_TIMEOUT}" "${REMOTE_LOG_FILE:-}" \
    <<< "${check_script}" \
    || die "服务启动检测失败，请 SSH 登录服务器查看日志"

  log_ok "部署成功，服务已正常运行"
}

# ── 公网 API 验证 ─────────────────────────────────────────────────
verify_public_api() {
  step "验证公网 API 可访问 (最多 ${API_VERIFY_TIMEOUT}s)"

  if [[ "${DRY_RUN}" == true ]]; then
    log_info "（dry-run）将请求 ${PUBLIC_API_URL}，检查是否包含「${API_WELCOME_KEYWORD}」"
    return 0
  fi

  local elapsed=0
  local body=""

  while [[ ${elapsed} -lt ${API_VERIFY_TIMEOUT} ]]; do
    if body="$(curl -fsSL --max-time 15 -H 'Cache-Control: no-cache' "${PUBLIC_API_URL}" 2>/dev/null)"; then
      if echo "${body}" | grep -q "${API_WELCOME_KEYWORD}"; then
        log_ok "公网 API 正常: ${PUBLIC_API_URL}"
        return 0
      fi
      log_warn "API 有响应，但未包含预期欢迎语"
      log_info "响应内容: ${body}"
      die "公网 API 验证失败"
    fi

    sleep 3
    elapsed=$((elapsed + 3))
    log_info "等待 API 可访问... (${elapsed}/${API_VERIFY_TIMEOUT}s)"
  done

  die "公网 API 验证超时: ${PUBLIC_API_URL}"
}

# ── 主流程 ────────────────────────────────────────────────────────
main() {
  parse_args "$@"

  printf '%b\n' "${C_BOLD}廊坊社保后端部署${C_RESET}"
  log_info "时间: $(date '+%Y-%m-%d %H:%M:%S')"
  [[ "${DRY_RUN}" == true ]] && log_warn "DRY-RUN 模式：不会实际执行命令"

  check_prerequisites
  build_project
  find_built_jar
  upload_jar
  remote_deploy
  wait_for_startup
  verify_public_api

  printf '\n%b\n' "${C_GREEN}${C_BOLD}全部完成！${C_RESET}"
  log_info "部署包: ${JAR_BASENAME}"
  log_info "服务地址: ${PUBLIC_API_URL}"
  if [[ -n "${REMOTE_LOG_FILE:-}" ]]; then
    log_info "远程日志: ${REMOTE}:${REMOTE_LOG_FILE}"
  fi
}

main "$@"
