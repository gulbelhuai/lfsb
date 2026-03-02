# 🚀 廊坊社保管理系统部署指南

## 📋 部署前准备

### 环境要求

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | Java运行环境 |
| Maven | 3.6+ | 项目构建工具 |
| MySQL | 8.0+ | 数据库 |
| Redis | 5.0+ | 缓存服务 |
| Node.js | 14+ | 前端构建环境 |
| Nginx | 1.18+ | 反向代理（生产环境） |

---

## 📦 一、数据库部署

### 1.1 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS lfpm
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;
```

### 1.2 执行SQL脚本

按顺序执行以下脚本：

```bash
# 连接数据库
mysql -h www.htmisoft.net -P 36522 -u root -pinfini_rag_flow lfpm

# 1. 创建审批核心表
source sql/refactor/01_create_tables.sql

# 2. 修改现有表结构
source sql/refactor/02_alter_existing_tables.sql

# 3. 初始化角色、用户、字典
source sql/refactor/03_init_data.sql

# 4. 配置菜单权限
source sql/refactor/04_menu_config.sql
```

### 1.3 验证数据

```sql
-- 检查新增的表
SHOW TABLES LIKE '%approval%';
SHOW TABLES LIKE '%benefit%';
SHOW TABLES LIKE '%finance%';

-- 检查角色和用户
SELECT role_name FROM sys_role WHERE role_key LIKE '%shebao%';
SELECT user_name, nick_name FROM sys_user WHERE dept_id = 200;

-- 检查菜单
SELECT menu_name, path, component FROM sys_menu WHERE menu_name LIKE '%社保%';
```

---

## 🖥️ 二、后端部署

### 2.1 配置文件修改

编辑 `ruoyi-admin/src/main/resources/application.yml`：

```yaml
# 数据源配置
spring:
  datasource:
    url: jdbc:mysql://www.htmisoft.net:36522/lfpm?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: infini_rag_flow

  # Redis配置
  redis:
    host: localhost
    port: 6379
    password:
    database: 0

# 服务器配置
server:
  port: 8080
```

### 2.2 编译打包

#### 开发环境启动

```bash
# 方式1: 使用Maven
mvn clean install
cd ruoyi-admin
mvn spring-boot:run

# 方式2: 使用IDE
# 打开 RuoYiApplication.java
# 右键 -> Run 'RuoYiApplication'
```

#### 生产环境打包

```bash
# 打包
mvn clean package -Dmaven.test.skip=true

# 生成的jar包位置
ls -lh ruoyi-admin/target/ruoyi-admin.jar
```

### 2.3 启动后端服务

#### Linux/Mac

```bash
# 启动
nohup java -jar ruoyi-admin/target/ruoyi-admin.jar > ruoyi.log 2>&1 &

# 查看日志
tail -f ruoyi.log

# 停止
ps aux | grep ruoyi-admin
kill -9 <PID>
```

#### Windows

```powershell
# 启动
java -jar ruoyi-admin\target\ruoyi-admin.jar

# 或创建启动脚本 start.bat
@echo off
java -jar ruoyi-admin\target\ruoyi-admin.jar
pause
```

### 2.4 验证后端服务

访问以下地址验证：

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# API文档
http://localhost:8080/swagger-ui/index.html

# 登录接口测试
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## 🎨 三、前端部署

### 3.1 安装依赖

```bash
cd ruoyi-ui
npm install
```

如果安装慢，使用淘宝镜像：

```bash
npm config set registry https://registry.npmmirror.com
npm install
```

### 3.2 配置后端地址

编辑 `ruoyi-ui/.env.development`（开发环境）：

```properties
# 开发环境配置
VUE_APP_BASE_API = 'http://localhost:8080'
```

编辑 `ruoyi-ui/.env.production`（生产环境）：

```properties
# 生产环境配置
VUE_APP_BASE_API = 'http://your-domain.com/api'
```

### 3.3 启动开发服务器

```bash
npm run dev
```

访问: http://localhost:80

### 3.4 生产环境打包

```bash
# 打包
npm run build:prod

# 打包后的文件位置
ls -lh dist/
```

---

## 🌐 四、Nginx部署（生产环境）

### 4.1 安装Nginx

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install nginx

# CentOS/RHEL
sudo yum install nginx
```

### 4.2 配置Nginx

创建配置文件 `/etc/nginx/conf.d/langfang-shebao.conf`：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /usr/share/nginx/html/ruoyi-ui;
        index index.html index.htm;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 文件上传大小限制
    client_max_body_size 50M;
}
```

### 4.3 部署前端文件

```bash
# 复制打包后的文件到Nginx目录
sudo mkdir -p /usr/share/nginx/html/ruoyi-ui
sudo cp -r dist/* /usr/share/nginx/html/ruoyi-ui/

# 设置权限
sudo chown -R nginx:nginx /usr/share/nginx/html/ruoyi-ui
```

### 4.4 启动Nginx

```bash
# 测试配置
sudo nginx -t

# 启动/重启Nginx
sudo systemctl start nginx
sudo systemctl restart nginx

# 开机自启
sudo systemctl enable nginx
```

---

## 🔐 五、安全配置（生产环境）

### 5.1 修改默认密码

登录系统后，立即修改所有默认密码：

```sql
-- 修改admin密码（使用加密后的密码）
UPDATE sys_user SET password = '$2a$10$新的加密密码' WHERE user_name = 'admin';

-- 修改测试账号密码
UPDATE sys_user SET password = '$2a$10$新的加密密码'
WHERE user_name IN ('jingban1', 'jingban2', 'fuhe1', 'fuhe2', 'shenpi1', 'shenpi2');
```

### 5.2 配置防火墙

```bash
# 开放必要端口
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload

# 或使用ufw（Ubuntu）
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

### 5.3 配置HTTPS（推荐）

使用Let's Encrypt免费证书：

```bash
# 安装certbot
sudo apt install certbot python3-certbot-nginx

# 获取证书并自动配置Nginx
sudo certbot --nginx -d your-domain.com

# 证书自动续期
sudo certbot renew --dry-run
```

---

## 🔧 六、Redis配置

### 6.1 安装Redis

```bash
# Ubuntu/Debian
sudo apt install redis-server

# CentOS/RHEL
sudo yum install redis
```

### 6.2 配置Redis

编辑 `/etc/redis/redis.conf`：

```properties
# 绑定地址（仅本机访问）
bind 127.0.0.1

# 端口
port 6379

# 密码（可选，建议设置）
requirepass your_redis_password

# 持久化
save 900 1
save 300 10
save 60 10000
```

### 6.3 启动Redis

```bash
# 启动Redis
sudo systemctl start redis

# 开机自启
sudo systemctl enable redis

# 测试连接
redis-cli ping
# 返回: PONG
```

---

## 📊 七、监控和日志

### 7.1 后端日志

日志位置：`ruoyi-admin/logs/`

```bash
# 查看实时日志
tail -f ruoyi-admin/logs/sys-info.log

# 查看错误日志
tail -f ruoyi-admin/logs/sys-error.log
```

### 7.2 Nginx日志

```bash
# 访问日志
tail -f /var/log/nginx/access.log

# 错误日志
tail -f /var/log/nginx/error.log
```

### 7.3 系统监控

安装和配置监控工具（可选）：

```bash
# 安装htop
sudo apt install htop

# 查看系统资源
htop

# 查看Java进程
jps -l
```

---

## 🧪 八、部署验证

### 8.1 功能测试清单

- [ ] 登录功能正常
- [ ] 菜单权限正确显示
- [ ] 人员登记功能正常
- [ ] 审批流程正常
- [ ] 待遇核定功能正常
- [ ] 支付计划生成正常
- [ ] 财务管理功能正常
- [ ] 审计查询功能正常
- [ ] 文件上传下载正常
- [ ] Excel导入导出正常

### 8.2 性能测试

```bash
# 使用ab（Apache Bench）进行简单压力测试
ab -n 1000 -c 10 http://localhost:8080/system/user/list

# 使用JMeter进行更复杂的测试
# 下载并配置JMeter测试计划
```

---

## 🔄 九、数据备份

### 9.1 数据库备份

创建备份脚本 `backup.sh`：

```bash
#!/bin/bash
# 数据库备份脚本

# 配置
DB_HOST="www.htmisoft.net"
DB_PORT="36522"
DB_USER="root"
DB_PASS="infini_rag_flow"
DB_NAME="lfpm"
BACKUP_DIR="/backup/mysql"
DATE=$(date +%Y%m%d_%H%M%S)

# 创建备份目录
mkdir -p $BACKUP_DIR

# 备份
mysqldump -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS $DB_NAME > $BACKUP_DIR/lfpm_$DATE.sql

# 压缩
gzip $BACKUP_DIR/lfpm_$DATE.sql

# 删除7天前的备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "备份完成: lfpm_$DATE.sql.gz"
```

设置定时任务：

```bash
# 编辑crontab
crontab -e

# 每天凌晨2点备份
0 2 * * * /path/to/backup.sh
```

### 9.2 Redis备份

```bash
# 手动备份
redis-cli SAVE
cp /var/lib/redis/dump.rdb /backup/redis/dump_$(date +%Y%m%d).rdb

# 配置自动持久化（redis.conf）
save 900 1
save 300 10
save 60 10000
```

---

## ⚠️ 十、常见问题

### 问题1: 后端启动失败
**原因**:
- MySQL连接失败
- Redis连接失败
- 端口被占用

**解决**:
```bash
# 检查MySQL连接
mysql -h www.htmisoft.net -P 36522 -u root -p

# 检查Redis
redis-cli ping

# 检查端口占用
netstat -tulpn | grep 8080
```

### 问题2: 前端无法访问后端
**原因**:
- 跨域问题
- 代理配置错误

**解决**:
检查 `ruoyi-admin/src/main/java/com/ruoyi/framework/config/SecurityConfig.java` 中的CORS配置

### 问题3: 菜单不显示
**原因**:
- 菜单未配置
- 角色权限未分配

**解决**:
```sql
-- 检查菜单
SELECT * FROM sys_menu WHERE menu_name LIKE '%社保%';

-- 检查角色菜单关联
SELECT * FROM sys_role_menu WHERE role_id IN (SELECT role_id FROM sys_role WHERE role_key LIKE '%shebao%');
```

---

## 📚 相关文档

- **系统启动指南**: `START_GUIDE.md`
- **前端路由集成**: `ruoyi-ui/ROUTER_INTEGRATION_GUIDE.md`
- **项目交付文档**: `PROJECT_DELIVERY.md`
- **API测试脚本**: `tests/api_test.http`

---

## 🎉 部署完成检查清单

- [ ] MySQL数据库部署完成
- [ ] SQL脚本全部执行
- [ ] Redis服务启动正常
- [ ] 后端服务启动正常
- [ ] 前端服务启动正常
- [ ] Nginx配置完成（生产环境）
- [ ] 防火墙配置完成
- [ ] HTTPS配置完成（可选）
- [ ] 数据备份脚本配置
- [ ] 功能测试通过
- [ ] 性能测试通过
- [ ] 文档已交付

**部署完成！** ✅

---

**文档版本**: v1.0
**更新日期**: 2026年1月19日
