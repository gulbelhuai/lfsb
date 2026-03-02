# 廊坊社保管理系统

> 基于若依框架的县级养老补贴发放管理系统

[![项目状态](https://img.shields.io/badge/状态-开发完成-brightgreen)]()
[![技术栈](https://img.shields.io/badge/技术栈-Spring%20Boot%20%2B%20Vue-blue)]()
[![版本](https://img.shields.io/badge/版本-v1.0-orange)]()

---

## 📖 项目简介

廊坊社保管理系统是一个用于管理县级养老补贴发放的综合管理平台，支持多种补贴类型的人员登记、待遇核定、支付计划生成、银行发放和审计查询等完整业务流程。

### 核心特性

- ✅ **多角色权限控制** - 支持6种用户角色，细粒度权限管理
- ✅ **多级审批流程** - 支持2级和3级审批，自动流转
- ✅ **完整审计追踪** - 操作日志、审批历史全记录
- ✅ **失败重试机制** - 自动二次、三次发放，降低失败率
- ✅ **统计报表** - 多维度数据统计和可视化展示

---

## 🚀 快速开始

### 最快5分钟启动系统

```bash
# 1. 启动Redis
redis-server

# 2. 启动后端（新终端）
cd ruoyi-admin
mvn spring-boot:run

# 3. 启动前端（新终端）
cd ruoyi-ui
npm install
npm run dev

# 4. 访问系统
浏览器打开: http://localhost:80
账号: admin  密码: admin123
```

详细步骤请参考: [快速启动指南](QUICK_START.md)

---

## 📚 文档导航

### 🎯 快速入门
- [快速启动指南](QUICK_START.md) - 5分钟快速启动系统
- [系统启动指南](START_GUIDE.md) - 详细的启动说明
- [待完成任务](PENDING_TASKS.md) - 当前待完成的任务

### 📐 设计文档
- [现有系统分析](spec/现有系统分析.md) - 原系统分析
- [概要设计文档](spec/概要设计文档.md) - 系统设计文档
- [前端开发指南](sql/refactor/FRONTEND_GUIDE.md) - 前端开发规范

### 🛠️ 部署文档
- [部署指南](DEPLOYMENT_GUIDE.md) - 生产环境部署
- [路由集成指南](ruoyi-ui/ROUTER_INTEGRATION_GUIDE.md) - 前端路由配置

### 🧪 测试文档
- [测试计划](TESTING_PLAN.md) - 完整的测试计划
- [测试场景](tests/test_scenarios.md) - 详细的测试步骤
- [API测试脚本](tests/api_test.http) - REST Client测试

### 📊 项目总结
- [项目交付文档](PROJECT_DELIVERY.md) - 交付物清单
- [前端完成总结](FRONTEND_COMPLETE.md) - 前端开发总结
- [项目最终总结](PROJECT_FINAL_SUMMARY.md) - 完整项目总结

---

## 🏗️ 技术架构

### 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.4 | 后端框架 |
| MyBatis-Plus | 3.5.6 | ORM框架 |
| Spring Security | - | 安全框架 |
| Redis | 5.0+ | 缓存 |
| MySQL | 8.0 | 数据库 |
| Vue.js | 2.x | 前端框架 |
| Element UI | - | UI组件库 |

### 项目结构

```
langfang-shebao/
├── ruoyi-admin/                 # 后端主模块
│   └── src/main/java/
│       └── com/ruoyi/
│           └── shebao/         # 社保业务模块
│               ├── controller/  # 控制器层
│               ├── service/     # 服务层
│               ├── domain/      # 实体类
│               ├── mapper/      # 数据访问层
│               ├── dto/         # 数据传输对象
│               ├── enums/       # 枚举类
│               └── statemachine/# 状态机
├── ruoyi-ui/                    # 前端项目
│   └── src/
│       ├── api/shebao/         # API服务
│       ├── views/shebao/       # 业务页面（25个）
│       └── components/Shebao/  # 公共组件（2个）
├── sql/                         # SQL脚本
│   └── refactor/
│       ├── 01_create_tables.sql       # 创建核心表
│       ├── 02_alter_existing_tables.sql # 修改现有表
│       ├── 03_init_data.sql           # 初始化数据
│       └── 04_menu_config.sql         # 菜单配置
├── spec/                        # 设计文档
├── tests/                       # 测试文件
└── docs/                        # 项目文档
```

---

## 👥 用户角色

系统支持6种用户角色，每种角色拥有不同的权限和菜单：

| 角色 | 账号 | 密码 | 主要职责 |
|------|------|------|----------|
| 系统管理员 | admin | admin123 | 系统管理、配置 |
| 经办人 | jingban1 | admin123 | 人员登记、待遇核定申请 |
| 复核人 | fuhe1 | admin123 | 登记复核、核定复核 |
| 审批人 | shenpi1 | admin123 | 最终审批、上传财务 |
| 待遇管理员 | daiyu1 | admin123 | 待遇管理、认证 |
| 财务操作员 | caiwu1 | admin123 | 批次管理、银行发放 |
| 审计查询员 | shenji1 | admin123 | 日志查询、统计报表 |

更多测试账号请参考: [项目交付文档](PROJECT_DELIVERY.md)

---

## 📦 功能模块

### 1. 人员信息管理（7个功能）
- 失地居民登记
- 被征地居民登记
- 拆迁居民登记
- 村干部登记
- 教师补贴登记
- 人员登记审核
- 人员信息修改审核

### 2. 待遇核定管理（3个功能）
- 预到龄通知生成
- 待遇核定
- 待遇核定复核

### 3. 待遇管理（3个功能）
- 发放信息修改
- 待遇暂停/恢复
- 待遇认证

### 4. 支付计划管理（4个功能）
- 支付计划生成
- 支付计划复核
- 支付计划审批
- 上传财务系统

### 5. 财务管理（4个功能）
- 批次管理
- 银行发放
- 失败处理
- 财务账户管理

### 6. 审计管理（4个功能）
- 操作日志
- 审批历史
- 发放明细
- 统计报表

---

## 🔄 核心业务流程

### 人员登记审批流程（2级）

```
[经办人] 提交登记
    ↓
[复核人] 复核 ─→ 驳回（退回经办人）
    ↓
[审批人] 审批 ─→ 驳回（退回经办人）
    ↓
  已通过
```

### 支付计划审批流程（3级）

```
[系统] 自动生成支付计划
    ↓
[复核人] 复核 ─→ 驳回
    ↓
[审批人] 审批 ─→ 驳回
    ↓
[审批人] 上传财务系统
    ↓
[财务操作员] 提交银行发放
```

---

## 📊 项目统计

### 代码量统计

| 类别 | 数量 |
|------|------|
| Java后端文件 | 60+ |
| Vue前端文件 | 37 |
| SQL脚本 | 4个 |
| 项目文档 | 15+ |

### 功能统计

| 类别 | 数量 |
|------|------|
| 功能模块 | 6个 |
| 业务页面 | 25个 |
| 用户角色 | 6种 |
| 测试账号 | 13个 |
| 菜单项 | 36个 |

---

## 🧪 测试

### 运行API测试

使用VS Code REST Client插件：

1. 安装 REST Client 扩展
2. 打开 `tests/api_test.http`
3. 点击 "Send Request" 执行测试

### 运行端到端测试

参考 [测试场景](tests/test_scenarios.md) 执行完整业务流程测试。

---

## 📝 开发指南

### 后端开发

```bash
# 编译
mvn clean compile

# 打包
mvn clean package -Dmaven.test.skip=true

# 运行
cd ruoyi-admin
mvn spring-boot:run
```

### 前端开发

```bash
# 安装依赖
cd ruoyi-ui
npm install

# 启动开发服务器
npm run dev

# 打包生产版本
npm run build:prod
```

---

## 🐛 常见问题

### 1. 后端启动失败

**问题**: 端口被占用

**解决**:
```bash
# Windows
netstat -ano | findstr 8080
taskkill /PID <进程ID> /F

# Linux/Mac
lsof -i:8080
kill -9 <PID>
```

### 2. MySQL连接失败

**问题**: 无法连接到数据库

**解决**:
```bash
# 测试连接
mysql -h www.htmisoft.net -P 36522 -u root -pinfini_rag_flow lfpm
```

### 3. Redis连接失败

**问题**: Redis未启动

**解决**:
```bash
# 启动Redis
redis-server

# 测试连接
redis-cli ping
```

更多问题请参考: [快速启动指南](QUICK_START.md)

---

## 📅 版本历史

### v1.0 (2026-01-19)

**新增功能**:
- ✅ 6大功能模块
- ✅ 25个业务页面
- ✅ 多角色权限控制
- ✅ 多级审批流程
- ✅ 完整审计追踪

**技术改进**:
- ✅ 引入状态机模式
- ✅ 前后端完全分离
- ✅ RESTful API设计
- ✅ 组件化前端架构

---

## 🤝 贡献

欢迎提交Issue和Pull Request！

---

## 📄 许可证

本项目基于若依框架开发，遵循MIT许可证。

---

## 📞 联系我们

如有问题或建议，请联系项目组。

---

## 🎉 致谢

感谢若依框架（RuoYi）提供的优秀基础框架！

---

**当前状态**: ✅ 开发完成，测试准备就绪
**最后更新**: 2026年1月19日
