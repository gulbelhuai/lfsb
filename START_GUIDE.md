# 廊坊社保管理系统 - 快速启动指南

**系统版本**: v2.0
**更新日期**: 2026-01-19

---

## 📋 目录

1. [环境要求](#环境要求)
2. [数据库配置](#数据库配置)
3. [后端启动](#后端启动)
4. [前端启动](#前端启动)
5. [登录测试](#登录测试)
6. [功能测试](#功能测试)
7. [常见问题](#常见问题)

---

## 🔧 环境要求

### 必需环境

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | 后端运行环境 |
| Maven | 3.6+ | 后端构建工具 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存 |
| Node.js | 14+ | 前端运行环境 |
| npm | 6+ | 前端包管理工具 |

### 检查环境

```bash
# 检查JDK版本
java -version

# 检查Maven版本
mvn -version

# 检查MySQL版本
mysql --version

# 检查Redis状态
redis-cli ping

# 检查Node.js版本
node -v

# 检查npm版本
npm -v
```

---

## 🗄️ 数据库配置

### 1. 执行SQL脚本

```bash
# 进入项目目录
cd f:\project\lf\langfang-shebao

# 执行SQL脚本（按顺序）
mysql -h www.htmisoft.net -P 36522 -u root -pinfini_rag_flow lfpm -e "source sql/refactor/01_create_tables.sql"
mysql -h www.htmisoft.net -P 36522 -u root -pinfini_rag_flow lfpm -e "source sql/refactor/02_alter_existing_tables.sql"
mysql -h www.htmisoft.net -P 36522 -u root -pinfini_rag_flow lfpm -e "source sql/refactor/03_init_data.sql"
mysql -h www.htmisoft.net -P 36522 -u root -pinfini_rag_flow lfpm -e "source sql/refactor/04_menu_config.sql"
```

### 2. 验证数据

```bash
# 验证表创建
mysql -h www.htmisoft.net -P 36522 -u root -pinfini_rag_flow lfpm -e "SHOW TABLES LIKE '%approval%'"

# 验证角色数据
mysql -h www.htmisoft.net -P 36522 -u root -pinfini_rag_flow lfpm -e "SELECT role_name, role_key FROM sys_role WHERE role_key IN ('operator', 'reviewer', 'approver', 'finance', 'auditor')"

# 验证菜单数据
mysql -h www.htmisoft.net -P 36522 -u root -pinfini_rag_flow lfpm -e "SELECT COUNT(*) FROM sys_menu WHERE path IN ('person', 'benefit', 'management', 'payment', 'finance', 'audit')"
```

---

## 🚀 后端启动

### 1. 配置文件

编辑 `ruoyi-admin/src/main/resources/application.yml`:

```yaml
# 数据源配置
spring:
  datasource:
    url: jdbc:mysql://www.htmisoft.net:36522/lfpm?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: infini_rag_flow

  # Redis配置
  redis:
    host: 127.0.0.1
    port: 6379
    password:
    database: 0
```

### 2. 编译打包

```bash
# 清理并打包
mvn clean package -DskipTests

# 或者只编译不打包
mvn clean compile
```

### 3. 启动后端

```bash
# 方式1：使用Maven启动（开发环境）
cd ruoyi-admin
mvn spring-boot:run

# 方式2：使用jar包启动（生产环境）
java -jar ruoyi-admin/target/ruoyi-admin.jar

# 方式3：使用start.bat启动（Windows）
start.bat
```

### 4. 验证后端

访问：http://localhost:8080

看到若依欢迎页面表示后端启动成功！

---

## 🌐 前端启动

### 1. 安装依赖

```bash
# 进入前端目录
cd ruoyi-ui

# 安装依赖（首次运行）
npm install

# 或使用淘宝镜像
npm install --registry=https://registry.npmmirror.com
```

### 2. 配置API地址

编辑 `ruoyi-ui/.env.development`:

```bash
# 开发环境配置
ENV = 'development'

# 若依管理系统/开发环境
VUE_APP_BASE_API = 'http://localhost:8080'
```

### 3. 启动前端

```bash
# 开发环境启动
npm run dev

# 构建生产环境
npm run build:prod
```

### 4. 访问系统

浏览器访问：http://localhost:80

---

## 🔐 登录测试

### 测试账号

| 角色 | 账号 | 密码 | 权限范围 |
|------|------|------|----------|
| 系统管理员 | admin | admin123 | 全部权限 |
| 经办人 | jingbanren | admin123 | 业务创建 |
| 复核人 | fuhe | admin123 | 业务复核 |
| 审批人 | shenpi | admin123 | 业务审批 |
| 财务人员 | caiwu | admin123 | 财务处理 |
| 审计员 | shenji | admin123 | 查询审计 |

### 登录步骤

1. 打开浏览器访问 http://localhost:80
2. 输入账号和密码
3. 点击"登录"按钮
4. 查看左侧菜单是否正常显示

---

## 🧪 功能测试

### 1. 配置角色菜单

**重要**：首次启动需要配置角色菜单关联！

1. 使用 `admin` 账号登录
2. 进入【系统管理】→【角色管理】
3. 为每个角色分配菜单权限：
   - **经办人**: 勾选人员信息管理、待遇核定管理、支付计划生成等相关菜单
   - **复核人**: 勾选人员登记复核、核定审核、支付计划复核等相关菜单
   - **审批人**: 勾选信息修改审批、支付计划审批、上传财务系统等相关菜单
   - **财务人员**: 勾选财务管理全部菜单
   - **审计员**: 勾选审计报表全部菜单
4. 点击"确定"保存

### 2. 测试人员登记流程

#### Step 1: 经办人创建登记

1. 使用 `jingbanren` 账号登录
2. 进入【人员信息管理】→【失地居民登记】
3. 点击"新增"按钮
4. 填写人员信息
5. 点击"确定"保存（状态：草稿）
6. 点击"提交"按钮（状态：待复核）

#### Step 2: 复核人复核

1. 退出登录，使用 `fuhe` 账号登录
2. 进入【人员信息管理】→【人员登记复核】
3. 找到刚才提交的记录
4. 点击"复核通过"或"复核驳回"

#### Step 3: 查看审批历史

1. 点击记录的"详情"按钮
2. 查看下方的"审批历史"时间轴
3. 验证每步操作都有记录

### 3. 测试API接口

使用提供的 `tests/api_test.http` 文件测试：

1. 在VS Code中安装 **REST Client** 插件
2. 打开 `tests/api_test.http` 文件
3. 先执行登录请求获取token
4. 将token填入 `@token` 变量
5. 依次测试各个API接口

### 4. 测试支付发放流程

#### Step 1: 生成支付计划

1. 使用 `jingbanren` 登录
2. 进入【支付结算】→【支付计划生成】
3. 选择查询条件
4. 点击"生成计划"
5. 点击"创建批次"

#### Step 2: 3级审批

1. 使用 `fuhe` 登录，进行"批次复核"
2. 使用 `shenpi` 登录，进行"批次审批"
3. 使用 `shenpi` 登录，进行"上传财务系统"

#### Step 3: 财务处理

1. 使用 `caiwu` 登录
2. 进入【财务管理】→【批次管理】
3. 找到批次，点击"生成银行文件"
4. 点击"提交银行发放"
5. 上传发放结果Excel文件
6. 处理失败记录

---

## 🐛 常见问题

### Q1: 后端启动失败，提示数据库连接错误

**A**: 检查以下几点：
1. MySQL服务是否启动
2. 数据库连接信息是否正确
3. 防火墙是否允许3306端口
4. 数据库用户权限是否足够

### Q2: 前端启动失败，提示端口被占用

**A**:
```bash
# 查找占用端口的进程
netstat -ano | findstr :80

# 结束进程
taskkill /PID <进程号> /F

# 或修改端口
# 编辑 ruoyi-ui/vue.config.js
devServer: {
  port: 8081  # 改为其他端口
}
```

### Q3: 登录后看不到菜单

**A**:
1. 检查是否已配置角色菜单关联
2. 使用admin登录，进入【系统管理】→【角色管理】
3. 为对应角色分配菜单权限

### Q4: API请求提示401未授权

**A**:
1. 检查token是否过期
2. 重新登录获取新token
3. 检查请求头是否正确携带token

### Q5: 菜单配置后仍看不到

**A**:
1. 清除浏览器缓存
2. 退出登录重新登录
3. 检查角色菜单关联表 sys_role_menu

### Q6: Excel导入失败

**A**:
1. 检查Excel格式是否正确
2. 检查必填字段是否填写
3. 检查数据格式是否符合要求
4. 查看后端日志定位具体错误

---

## 📊 系统监控

### 后端日志

```bash
# 查看实时日志
tail -f ruoyi-admin/logs/sys-info.log

# Windows查看日志
type ruoyi-admin\logs\sys-info.log
```

### 性能监控

访问：http://localhost:8080/druid/login.html
- 用户名：admin
- 密码：123456

### API文档

访问：http://localhost:8080/doc.html

---

## 🎯 下一步工作

### 立即可做

- [x] 数据库初始化
- [x] 后端启动
- [x] 前端启动
- [ ] 配置角色菜单
- [ ] 测试各角色登录
- [ ] 测试审批流程

### 短期任务（1-2周）

- [ ] 开发剩余前端页面（30+页面）
- [ ] API接口联调测试
- [ ] 完善Excel导入导出
- [ ] 添加文件上传功能
- [ ] 用户培训

### 中期任务（1个月）

- [ ] 补充单元测试
- [ ] 性能优化
- [ ] 数据迁移
- [ ] 生产环境部署

---

## 📞 技术支持

### 文档资源

| 文档 | 路径 |
|------|------|
| 部署指南 | `sql/refactor/README.md` |
| 验证报告 | `sql/refactor/DEPLOYMENT_VERIFICATION.md` |
| 前端指南 | `sql/refactor/FRONTEND_GUIDE.md` |
| API测试 | `tests/api_test.http` |

### 项目地址

- 代码仓库：`f:\project\lf\langfang-shebao`
- 数据库：www.htmisoft.net:36522/lfpm
- 前端地址：http://localhost:80
- 后端地址：http://localhost:8080

---

## ✅ 启动检查清单

### 环境检查

- [ ] JDK 17+ 已安装
- [ ] Maven 3.6+ 已安装
- [ ] MySQL 8.0+ 已安装并启动
- [ ] Redis 6.0+ 已安装并启动
- [ ] Node.js 14+ 已安装
- [ ] npm 6+ 已安装

### 数据库检查

- [ ] 4个SQL脚本已执行
- [ ] 5张核心表已创建
- [ ] 6个角色已初始化
- [ ] 6个测试用户已创建
- [ ] 6个一级菜单已配置
- [ ] 30个子菜单已配置

### 后端检查

- [ ] 配置文件已修改
- [ ] Maven依赖已下载
- [ ] 项目编译成功
- [ ] 后端启动成功
- [ ] 访问8080端口正常

### 前端检查

- [ ] npm依赖已安装
- [ ] API地址已配置
- [ ] 前端启动成功
- [ ] 访问80端口正常
- [ ] 登录页面显示正常

### 功能检查

- [ ] admin账号登录成功
- [ ] 6个测试账号登录成功
- [ ] 角色菜单已配置
- [ ] 各角色菜单显示正常
- [ ] 人员登记功能正常
- [ ] 审批流程正常

---

**最后更新**: 2026-01-19
**文档版本**: v1.0
**系统状态**: ✅ 可启动测试
