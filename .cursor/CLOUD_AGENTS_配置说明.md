# 当前项目 Cloud Agents 配置说明

根据 Cursor 设置中的 Cloud Agents 界面，可按以下方式为当前项目配置云代理。

---

## 已自动配置

项目中已创建 **`.cursor/environment.json`**，云代理将：

- **install**：先执行 `mvn compile -DskipTests` 编译后端，再在 `ruoyi-ui` 下执行 `npm install` 安装前端依赖。
- **terminals**：
  - **Run Vue dev**：启动前端开发服务（`npm run dev`）。
  - **Run Spring Boot**：启动后端服务（`mvn spring-boot:run -pl ruoyi-admin`）。

如需数据库等环境变量，请在 [Dashboard → Secrets](https://cursor.com/dashboard?tab=cloud-agents) 中配置，不要写进本文件。

---

## 如何运行 Cloud Agent

配置好后，可以这样启动并运行云代理：

### 方式一：在 Cursor 里发给云端（最常用）

1. 打开 **Composer**（Ctrl/Cmd + I 或侧边栏的 Agent 入口）。
2. 在输入框里**先输入 `&`，再写你的需求**，例如：
   - `& 修复登录接口 500 错误`
   - `& 在 langfang-shebao 模块下实现 xxx 功能`
3. 发送后，这条对话会**推送到 Cloud Agent**，在云端执行；你可以关掉电脑，任务继续跑。
4. 在 **[cursor.com/agents](https://cursor.com/agents)** 网页或手机端可查看进度、继续对话或下发新指令。

### 方式二：Plan 模式再发到云端

1. 在 Composer 里用 **Plan 模式**（或 `/plan`）先和模型把方案讨论清楚。
2. 方案确定后，选择**将方案发送到云端执行**，由 Cloud Agent 在云端按方案改代码。

### 方式三：从其他入口启动

- **Slack**：若已连接 Slack，可在 Slack 里发起 Cloud Agent 任务。
- **Cursor CLI**：在终端用 Cursor CLI 时，同样在消息前加 `&` 即可把对话交给 Cloud Agent。

### 查看与管理

- 所有 Cloud Agent 对话与状态：**[cursor.com/agents](https://cursor.com/agents)**  
- 团队/密钥/默认仓库等设置：**[cursor.com/dashboard](https://cursor.com/dashboard?tab=cloud-agents)** → Cloud Agents 标签

---

## 一、入口与全局设置

1. **管理设置（Manage Settings）**  
   点击 **Open** 打开 [cursor.com](https://cursor.com/dashboard?tab=cloud-agents)，可：
   - 连接 GitHub/GitLab
   - 管理团队与用户设置

2. **连接 Slack（可选）**  
   点击 **Connect** 可将 Cloud Agents 与 Slack 集成，在 Slack 中与云代理协作。

---

## 二、工作区配置（Workspace Configuration）

> “Configure environment settings and secrets” —— 配置环境变量和密钥。

工作区级配置通常通过以下两种方式之一完成：

- 在 **Dashboard** 的 Cloud Agents 里配置该工作区/仓库对应的环境与密钥；或  
- 在项目内使用 **`.cursor/environment.json`**（见下文）定义运行环境与安装/启动命令。

密钥、API Key、数据库连接等敏感信息建议在 **Dashboard → Cloud Agents → Secrets** 中配置，不要写进仓库。

---

## 三、个人配置（Personal Configuration）

界面中 “These settings will be used for new cloud agents” 的选项会影响**新创建的**云代理：

| 配置项 | 说明 |
|--------|------|
| **Sharing** | 存储位置（如 Stored in database） |
| **Usage-Based Pricing** | 按使用量计费（如 Enabled） |
| **GitHub Access** | 需为 Verified，云代理才能访问仓库 |
| **Base Environment** | 基础系统（如 Using Default Ubuntu） |
| **Runtime Configuration** | 运行时配置，可在 Dashboard 或 `environment.json` 中设置 |
| **Secrets** | 在 Dashboard 的 Secrets 标签页添加，会以环境变量形式提供给云代理 |

---

## 四、项目内环境配置（推荐）

在项目根目录下创建 **`.cursor/environment.json`**，可指定：

- **安装命令**：依赖安装（如 Maven、npm），在每次机器启动/更新时执行  
- **启动命令**：需要常驻的服务（如后端、前端 dev server）  
- **（可选）Dockerfile**：自定义系统依赖、编译器版本等

### 本项目示例（若依 + Vue 前端）

当前为 **Java (Maven) + Vue (ruoyi-ui)** 项目，可参考：

```json
{
  "install": "cd ruoyi-ui && npm install",
  "terminals": [
    {
      "name": "Run Vue dev",
      "command": "cd ruoyi-ui && npm run dev"
    }
  ]
}
```

- 若需同时安装 Java 依赖，可把 `install` 改为多条命令或使用脚本。  
- `install` 在**项目根目录**执行；路径以项目根为基准。  
- 更多选项（如 `build.dockerfile`、`start`）见官方文档：[Setup | Cursor Docs](https://cursor.com/docs/cloud-agent/setup)。

---

## 五、密钥与安全

- **推荐**：在 Cursor 设置中打开 [Dashboard → Cloud Agents → Secrets](https://cursor.com/dashboard?tab=cloud-agents)，以键值对添加密钥。  
- 密钥会以**环境变量**形式注入到云代理，且可标记为 **Redacted**，避免被提交或写入对话。  
- 不要把密钥写在 `environment.json` 或提交到仓库。

---

## 六、简要步骤小结

1. 在设置中打开 **Cloud Agents**，确保 **GitHub Access** 为 **Verified**。  
2. 点击 **Open** 进入 Dashboard，在 **Secrets** 中配置项目所需环境变量/密钥。  
3. 在项目根目录创建 **`.cursor/environment.json`**（可选），填写 `install`、`terminals` 等，使云代理能安装依赖并启动前后端。  
4. 工作区/团队级默认分支、默认仓库、网络策略等可在 Dashboard 的 **Defaults / Network access / Security** 中配置。

按以上步骤即可完成当前项目的 Cloud Agents 配置；新创建的云代理会使用这些环境与密钥。
