# ⚡ 廊坊社保管理系统快速启动指南

> 本指南帮助您快速在本地启动和测试系统

---

## 🚀 一键启动（最快方式）

### Windows

创建 `start-all.bat`：

```bat
@echo off
echo ========================================
echo 廊坊社保管理系统 - 一键启动
echo ========================================

echo.
echo [1/4] 启动Redis...
start "Redis" redis-server

echo [2/4] 启动MySQL...
echo MySQL已在远程运行，跳过...

echo [3/4] 启动后端...
cd ruoyi-admin
start "Backend" mvn spring-boot:run
cd ..

echo [4/4] 启动前端...
cd ruoyi-ui
start "Frontend" npm run dev
cd ..

echo.
echo ========================================
echo 启动完成！
echo 前端地址: http://localhost:80
echo 后端地址: http://localhost:8080
echo API文档: http://localhost:8080/swagger-ui/index.html
echo ========================================
pause
```

### Linux/Mac

创建 `start-all.sh`：

```bash
#!/bin/bash

echo "========================================"
echo "廊坊社保管理系统 - 一键启动"
echo "========================================"

echo ""
echo "[1/4] 启动Redis..."
redis-server &

echo "[2/4] 启动MySQL..."
echo "MySQL已在远程运行，跳过..."

echo "[3/4] 启动后端..."
cd ruoyi-admin
mvn spring-boot:run &
cd ..

echo "[4/4] 启动前端..."
cd ruoyi-ui
npm run dev &
cd ..

echo ""
echo "========================================"
echo "启动完成！"
echo "前端地址: http://localhost:80"
echo "后端地址: http://localhost:8080"
echo "API文档: http://localhost:8080/swagger-ui/index.html"
echo "========================================"
```

然后执行：

```bash
chmod +x start-all.sh
./start-all.sh
```

---

## 📝 分步启动（推荐）

### 步骤1: 启动Redis

```bash
# Windows
redis-server

# Linux/Mac
redis-server
```

验证：

```bash
redis-cli ping
# 应返回: PONG
```

### 步骤2: 确认MySQL连接

```bash
mysql -h www.htmisoft.net -P 36522 -u root -pinfini_rag_flow lfpm -e "SELECT 'MySQL连接成功' as status;"
```

### 步骤3: 启动后端

```bash
cd ruoyi-admin

# 方式1: Maven启动（推荐开发环境）
mvn spring-boot:run

# 方式2: JAR包启动
mvn clean package -Dmaven.test.skip=true
java -jar target/ruoyi-admin.jar
```

等待看到以下日志表示启动成功：

```
  _____                  __     _____
 |  __ \                 \ \   / /__ \
 | |__) |_   _  ___  _   _\ \_/ /   ) |
 |  _  /| | | |/ _ \| | | |\   /   / /
 | | \ \| |_| | (_) | |_| | | |   / /_
 |_|  \_\\__,_|\___/ \__, | |_|  |____|
                      __/ |
                     |___/
:: Spring Boot ::               (v3.5.4)

2026-01-19 10:00:00.000  INFO --- [main] Application started in 15.234 seconds
```

访问: http://localhost:8080

### 步骤4: 启动前端

```bash
cd ruoyi-ui

# 首次启动需要安装依赖
npm install

# 启动开发服务器
npm run dev
```

等待看到以下输出表示启动成功：

```
 DONE  Compiled successfully in 12345ms

  App running at:
  - Local:   http://localhost:80
  - Network: http://192.168.1.100:80
```

访问: http://localhost:80

---

## 🔑 测试账号

### 系统管理员

| 账号 | 密码 | 说明 |
|------|------|------|
| admin | admin123 | 超级管理员，拥有所有权限 |

### 业务角色账号

| 角色 | 账号 | 密码 | 说明 |
|------|------|------|------|
| 经办人 | jingban1 | admin123 | 村居委会操作员 |
| 复核人 | fuhe1 | admin123 | 街道办复核员 |
| 审批人 | shenpi1 | admin123 | 市级审批员 |
| 待遇管理员 | daiyu1 | admin123 | 待遇管理员 |
| 财务操作员 | caiwu1 | admin123 | 财务操作员 |
| 审计查询员 | shenji1 | admin123 | 审计查询员 |

---

## 🧪 快速测试

### 1. 登录系统

1. 访问: http://localhost:80
2. 输入账号: `jingban1`
3. 输入密码: `admin123`
4. 点击"登录"

### 2. 测试人员登记流程

#### 作为经办人（jingban1）
1. 点击左侧菜单"人员信息管理" > "失地居民登记"
2. 点击"新增"按钮
3. 填写表单信息
4. 点击"提交"

#### 作为复核人（fuhe1）
1. 退出当前账号
2. 使用账号 `fuhe1` 登录
3. 点击"人员信息管理" > "人员登记审核"
4. 找到待复核的记录
5. 点击"通过"或"驳回"

#### 作为审批人（shenpi1）
1. 退出当前账号
2. 使用账号 `shenpi1` 登录
3. 点击"人员信息管理" > "人员登记审核"
4. 找到待审批的记录
5. 点击"通过"或"驳回"

### 3. 测试待遇核定流程

#### 作为经办人（jingban1）
1. 点击"待遇核定管理" > "待遇核定"
2. 选择人员，点击"核定"
3. 填写待遇信息
4. 点击"提交"

#### 作为复核人和审批人
同上，在对应的审核页面操作

### 4. 测试支付计划流程

#### 作为系统（自动生成）
1. 使用管理员账号登录
2. 点击"支付计划管理" > "支付计划生成"
3. 选择月份和补贴类型
4. 点击"生成计划"

#### 作为复核人、审批人
在各自的审核页面完成审核

---

## 📊 查看系统状态

### 后端健康检查

```bash
# 健康状态
curl http://localhost:8080/actuator/health

# 应返回:
# {"status":"UP"}
```

### 数据库连接检查

```bash
mysql -h www.htmisoft.net -P 36522 -u root -pinfini_rag_flow lfpm -e "SELECT COUNT(*) as user_count FROM sys_user;"
```

### Redis连接检查

```bash
redis-cli
> ping
PONG
> keys *
> exit
```

---

## 🔧 常用开发命令

### 后端

```bash
# 编译
mvn clean compile

# 打包（跳过测试）
mvn clean package -Dmaven.test.skip=true

# 运行测试
mvn test

# 清理
mvn clean
```

### 前端

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 打包生产版本
npm run build:prod

# 预览生产版本
npm run preview

# 代码检查
npm run lint

# 代码格式化
npm run lint:fix
```

---

## 📱 API测试

### 使用VS Code REST Client

1. 安装VS Code扩展: REST Client
2. 打开 `tests/api_test.http`
3. 点击"Send Request"执行测试

### 使用Swagger UI

访问: http://localhost:8080/swagger-ui/index.html

---

## 🐛 常见问题

### 问题1: 后端启动报"端口被占用"

```bash
# Windows
netstat -ano | findstr 8080
taskkill /PID <进程ID> /F

# Linux/Mac
lsof -i:8080
kill -9 <PID>
```

### 问题2: 前端启动报"端口被占用"

```bash
# Windows
netstat -ano | findstr 80
taskkill /PID <进程ID> /F

# Linux/Mac
lsof -i:80
kill -9 <PID>
```

### 问题3: Redis连接失败

```bash
# 检查Redis是否运行
redis-cli ping

# 如果未运行，启动Redis
redis-server
```

### 问题4: MySQL连接失败

```bash
# 测试连接
mysql -h www.htmisoft.net -P 36522 -u root -pinfini_rag_flow lfpm

# 如果无法连接，检查网络和防火墙
ping www.htmisoft.net
telnet www.htmisoft.net 36522
```

### 问题5: 前端页面空白

1. 清除浏览器缓存
2. 检查浏览器控制台是否有错误
3. 检查后端是否正常运行
4. 检查 `ruoyi-ui/.env.development` 中的 API 地址

---

## 🛑 停止服务

### Windows

```bat
# 手动停止（Ctrl+C关闭各个窗口）

# 或使用命令
taskkill /F /IM java.exe
taskkill /F /IM node.exe
taskkill /F /IM redis-server.exe
```

### Linux/Mac

```bash
# 停止后端
pkill -f spring-boot

# 停止前端
pkill -f "npm run dev"

# 停止Redis
redis-cli shutdown
```

---

## 📚 下一步

- 📖 阅读 [项目交付文档](PROJECT_DELIVERY.md)
- 📖 阅读 [前端开发指南](sql/refactor/FRONTEND_GUIDE.md)
- 📖 阅读 [部署指南](DEPLOYMENT_GUIDE.md)
- 🧪 执行 [API测试脚本](tests/api_test.http)

---

## 🎉 完成！

系统已成功启动，现在您可以：

1. ✅ 使用测试账号登录
2. ✅ 测试各个功能模块
3. ✅ 查看API文档
4. ✅ 进行二次开发

**祝您使用愉快！** 🎊
