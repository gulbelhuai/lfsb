## Cursor Cloud specific instructions

### 系统概述

廊坊社保管理系统 — 基于 RuoYi (若依) 框架的县级养老补贴发放管理平台。后端 Java 17 + Spring Boot 3.5.4 多模块 Maven 项目，前端 Vue 2.x + Element UI。

### 必需服务

| 服务 | 端口 | 说明 |
|------|------|------|
| MySQL 8.0 | 3306 | 本地数据库，库名 `lfpm`，用 `sql/lfpm.sql` 初始化 |
| Redis | 6379 | 缓存/会话，database index 5 |
| Spring Boot 后端 | 8087 | context-path `/api` |
| Vue 前端 dev server | 1024 | 端口 80 需 root，容器内自动 fallback 到 1024 |

### 启动顺序

1. **MySQL**: `sudo mysqld --user=mysql --daemonize --pid-file=/var/run/mysqld/mysqld.pid`
2. **Redis**: `sudo redis-server --daemonize yes`（或 `redis-cli ping` 确认已运行）
3. **后端**: 从项目根目录，设置环境变量后运行：
   ```
   export MYSQL_HOST=127.0.0.1 MYSQL_PORT=3306 MYSQL_DB=lfpm MYSQL_USER=root MYSQL_PWD=root
   export REDIS_HOST=localhost REDIS_PWD=""
   export LOG_PATH=/tmp/langfang-shebao/logs PROFILE_PATH=/tmp/langfang-shebao/uploadPath
   mkdir -p $LOG_PATH $PROFILE_PATH
   cd ruoyi-admin && mvn spring-boot:run
   ```
4. **前端**: `cd ruoyi-ui && npm run dev`

### 重要注意事项 (gotchas)

- **远程 MySQL 不可用**: `.env` 中配置的 `www.htmisoft.net:36522` 在 Cloud VM 环境下因 IP 白名单限制无法连接，必须使用本地 MySQL。
- **MySQL skip-name-resolve**: 本地 MySQL 需配置 `skip-name-resolve`（已写入 `/etc/mysql/mysql.conf.d/skip-name-resolve.cnf`），否则 `127.0.0.1` 连接会超时。同时需确保 `root@127.0.0.1` 用户存在且使用 `mysql_native_password` 认证。
- **LOG_PATH / PROFILE_PATH**: `.env` 中这两个变量指向 Windows 路径，Linux 下必须覆盖为有效路径（如 `/tmp/langfang-shebao/logs`），否则 logback 无法创建日志文件导致启动失败。
- **Maven 多模块**: 首次构建需从根目录运行 `mvn install -DskipTests`，否则 `ruoyi-admin` 的 `spring-boot:run` 无法解析同项目模块依赖。
- **前端端口**: `vue.config.js` 配置端口 80，但非 root 环境会 fallback 到 1024。代理目标 `http://localhost:8087/api`。
- **登录密码**: admin 用户密码不是默认的 `admin123`，请参考项目文档或 Secrets 中的配置。
- **lint/build 命令**: 前端 `cd ruoyi-ui && npm run lint`（暂未配置 lint script）；后端 `mvn compile -DskipTests`；打包 `mvn package -DskipTests`。
