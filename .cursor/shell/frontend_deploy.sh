#!/usr/bin/env bash
#
# 廊坊社保前端 - 本地构建并部署到生产服务器
# 用法:
#   ./.cursor/shell/frontend_deploy.sh              # 完整部署（构建 + 上传 + 验证）
#   ./.cursor/shell/frontend_deploy.sh --skip-build # 跳过构建，仅上传 dist 并验证
#   ./.cursor/shell/frontend_deploy.sh --dry-run    # 仅打印将要执行的步骤
#
set -euo pipefail

# ── 可配置项（按需修改）────────────────────────────────────────────
REMOTE_USER="${DEPLOY_USER:-root}"
REMOTE_HOST="${DEPLOY_HOST:-xxest.com}"
REMOTE_WEB_ROOT="${DEPLOY_WEB_ROOT:-/data/web/shebao.xxest.com}"
REMOTE_DIST_DIR="${DEPLOY_DIST_DIR:-${REMOTE_WEB_ROOT}/dist}"

FRONTEND_URL="${DEPLOY_FRONTEND_URL:-https://shebao.xxest.com/}"

NODE_VERSION="${DEPLOY_NODE_VERSION:-v18.20.8}"
NPM_BUILD_SCRIPT="${DEPLOY_NPM_BUILD_SCRIPT:-build:prod}"

VERIFY_TIMEOUT="${DEPLOY_VERIFY_TIMEOUT:-60}"   # 等待线上资源更新的秒数
VERIFY_INTERVAL="${DEPLOY_VERIFY_INTERVAL:-3}"

# ── 内部变量 ──────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
UI_DIR="${PROJECT_ROOT}/ruoyi-ui"
DIST_DIR="${UI_DIR}/dist"
PACKAGE_JSON="${UI_DIR}/package.json"
REMOTE="${REMOTE_USER}@${REMOTE_HOST}"

SKIP_BUILD=false
DRY_RUN=false
EXPECTED_APP_JS=""
EXPECTED_VERSION=""
DIST_FILE_COUNT=0

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
  --skip-build   跳过 npm 构建，直接上传已有 dist 目录
  --dry-run      只显示步骤，不实际执行
  -h, --help     显示此帮助

环境变量（可选覆盖默认配置）:
  DEPLOY_USER / DEPLOY_HOST / DEPLOY_WEB_ROOT / DEPLOY_DIST_DIR
  DEPLOY_FRONTEND_URL / DEPLOY_NODE_VERSION / DEPLOY_VERIFY_TIMEOUT
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

load_nvm() {
  export NVM_DIR="${NVM_DIR:-$HOME/.nvm}"
  if [[ -s "${NVM_DIR}/nvm.sh" ]]; then
    # shellcheck disable=SC1091
    source "${NVM_DIR}/nvm.sh"
    return 0
  fi
  return 1
}

read_package_version() {
  [[ -f "${PACKAGE_JSON}" ]] || die "未找到 ${PACKAGE_JSON}"
  if command -v node >/dev/null 2>&1; then
    EXPECTED_VERSION="$(node -pe "require('${PACKAGE_JSON}').version")"
  else
    EXPECTED_VERSION="$(grep -m1 '"version"' "${PACKAGE_JSON}" | sed -E 's/.*"version"[[:space:]]*:[[:space:]]*"([^"]+)".*/\1/')"
  fi
  [[ -n "${EXPECTED_VERSION}" ]] || die "无法从 package.json 读取版本号"
}

extract_app_js_from_index() {
  local index_file="$1"
  [[ -f "${index_file}" ]] || die "未找到 ${index_file}"
  EXPECTED_APP_JS="$(grep -Eo 'static/js/app\.[a-f0-9]+\.js' "${index_file}" | head -1 || true)"
  [[ -n "${EXPECTED_APP_JS}" ]] || die "无法从 index.html 解析 app.js 资源指纹"
}

http_get() {
  local url="$1"
  curl -fsSL --max-time 15 -H 'Cache-Control: no-cache' -H 'Pragma: no-cache' "${url}"
}

http_status() {
  local url="$1"
  curl -sS -o /dev/null -w '%{http_code}' --max-time 15 -H 'Cache-Control: no-cache' "${url}"
}

# ── 前置检查 ──────────────────────────────────────────────────────
check_prerequisites() {
  step "检查本地环境"

  [[ -d "${UI_DIR}" ]] || die "未找到前端目录: ${UI_DIR}"
  read_package_version

  local missing=()
  for cmd in scp ssh curl; do
    command -v "${cmd}" >/dev/null 2>&1 || missing+=("${cmd}")
  done
  [[ ${#missing[@]} -eq 0 ]] || die "缺少命令: ${missing[*]}"

  if [[ "${SKIP_BUILD}" == false ]]; then
    command -v npm >/dev/null 2>&1 || die "缺少 npm，请先安装 Node.js"
    if ! load_nvm; then
      log_warn "未找到 nvm，将使用当前 shell 中的 node/npm"
    fi
  fi

  if [[ "${DRY_RUN}" == false ]]; then
    run_ssh "test -d '${REMOTE_DIST_DIR}'" \
      || die "远程目录不存在或 SSH 无法连接: ${REMOTE}:${REMOTE_DIST_DIR}"
  fi

  log_ok "环境检查通过"
  log_info "项目版本: ${EXPECTED_VERSION}"
  log_info "前端目录: ${UI_DIR}"
  log_info "部署目标: ${REMOTE}:${REMOTE_DIST_DIR}"
  log_info "访问地址: ${FRONTEND_URL}"
}

# ── 构建 ──────────────────────────────────────────────────────────
build_frontend() {
  if [[ "${SKIP_BUILD}" == true ]]; then
    step "跳过构建 (--skip-build)"
    return 0
  fi

  step "npm 构建 (${NPM_BUILD_SCRIPT}, Node ${NODE_VERSION})"
  (
    cd "${UI_DIR}"
    if load_nvm; then
      run nvm use "${NODE_VERSION}"
      log_info "Node: $(node -v), npm: $(npm -v)"
    else
      log_warn "使用当前 Node: $(node -v 2>/dev/null || echo unknown)"
    fi
    [[ -d node_modules ]] || die "未找到 node_modules，请先在 ruoyi-ui 目录执行 npm install"
    run npm run "${NPM_BUILD_SCRIPT}"
  ) || die "npm 构建失败"
  log_ok "构建完成"
}

# ── 检查 dist ─────────────────────────────────────────────────────
validate_dist() {
  step "检查构建产物"

  [[ -d "${DIST_DIR}" ]] || die "未找到 dist 目录: ${DIST_DIR}，请先构建"
  [[ -f "${DIST_DIR}/index.html" ]] || die "dist 中缺少 index.html"

  extract_app_js_from_index "${DIST_DIR}/index.html"
  DIST_FILE_COUNT="$(find "${DIST_DIR}" -type f | wc -l | tr -d ' ')"

  log_ok "dist 就绪: ${DIST_FILE_COUNT} 个文件"
  log_info "资源指纹: ${EXPECTED_APP_JS}"
}

# ── 上传 ──────────────────────────────────────────────────────────
upload_dist() {
  step "上传到服务器"

  if [[ "${DRY_RUN}" == true ]]; then
    log_info "（dry-run）scp -r ${DIST_DIR}/ -> ${REMOTE}:${REMOTE_DIST_DIR}/"
    return 0
  fi

  # 上传到 dist 目录（nginx root 指向此路径）
  run scp -r -o BatchMode=yes -o ConnectTimeout=15 \
    "${DIST_DIR}/." "${REMOTE}:${REMOTE_DIST_DIR}/" \
    || die "SCP 上传失败"

  log_ok "已上传至 ${REMOTE}:${REMOTE_DIST_DIR}/"
}

# ── 部署验证 ──────────────────────────────────────────────────────
verify_deployment() {
  step "验证线上前端 (最多 ${VERIFY_TIMEOUT}s)"

  if [[ "${DRY_RUN}" == true ]]; then
    log_info "（dry-run）将验证 ${FRONTEND_URL} 包含 ${EXPECTED_APP_JS}"
    return 0
  fi

  local elapsed=0
  local live_index live_app_status

  while [[ ${elapsed} -lt ${VERIFY_TIMEOUT} ]]; do
    if live_index="$(http_get "${FRONTEND_URL}" 2>/dev/null)"; then
      if echo "${live_index}" | grep -q "${EXPECTED_APP_JS}"; then
        live_app_status="$(http_status "${FRONTEND_URL%/}/${EXPECTED_APP_JS}" 2>/dev/null || echo "000")"
        if [[ "${live_app_status}" == "200" ]]; then
          log_ok "前端已更新: ${EXPECTED_APP_JS} (HTTP 200)"
          log_ok "部署验证通过 (版本 ${EXPECTED_VERSION}, 资源 ${EXPECTED_APP_JS})"
          return 0
        fi
        log_warn "index.html 已更新，但 ${EXPECTED_APP_JS} 返回 HTTP ${live_app_status}"
      fi
    fi

    sleep "${VERIFY_INTERVAL}"
    elapsed=$((elapsed + VERIFY_INTERVAL))
    log_info "等待线上生效... (${elapsed}/${VERIFY_TIMEOUT}s)"
  done

  log_error "前端验证超时，未检测到资源指纹: ${EXPECTED_APP_JS}"
  die "请手动访问 ${FRONTEND_URL} 排查"
}

# ── 主流程 ────────────────────────────────────────────────────────
main() {
  parse_args "$@"

  printf '%b\n' "${C_BOLD}廊坊社保前端部署${C_RESET}"
  log_info "时间: $(date '+%Y-%m-%d %H:%M:%S')"
  [[ "${DRY_RUN}" == true ]] && log_warn "DRY-RUN 模式：不会实际执行命令"

  check_prerequisites
  build_frontend
  validate_dist
  upload_dist
  verify_deployment

  printf '\n%b\n' "${C_GREEN}${C_BOLD}全部完成！${C_RESET}"
  log_info "版本: ${EXPECTED_VERSION}"
  log_info "资源: ${EXPECTED_APP_JS}"
  log_info "前端: ${FRONTEND_URL}"
}

main "$@"
