const path = require("path");
const {
  DEFAULT_BASE_URL,
  dbConfig,
  fileExists,
  nowStamp,
  writeJsonReport,
  writeMarkdownReport,
} = require("./lib/testEnv");

const ROOT = path.resolve(__dirname, "..");
const REPORT_DIR = path.resolve(__dirname, "reports");

const REQUIRED_FILES = [
  "sql/payment_closure_minimal_upgrade_20260315.sql",
  "spec/支付工作详细设计文档.md",
  "spec/支付工作使用说明.md",
  "tests/支付工作测试用例.md",
  "ruoyi-ui/playwright.config.js",
];

const RECOMMENDED_COMMANDS = [
  'mvn compile -DskipTests',
  'node .\\tests\\支付工作闭环准备检查.js',
  'node .\\tests\\验证支付工作闭环.js --dry-run',
  'cd ruoyi-ui && npm run test:e2e',
];

function buildMarkdown(result) {
  return [
    "# 支付工作测试准备检查报告",
    "",
    "## 基本信息",
    `- 检查批次：\`${result.batchNo}\``,
    `- 计划后端地址：\`${result.baseUrl}\``,
    `- 计划前端地址：\`${result.frontendUrl}\``,
    `- 数据库：\`${result.database.database}\`@\`${result.database.host}:${result.database.port}\``,
    "",
    "## 文件检查",
    ...result.files.map((item) => `- [${item.exists ? "x" : " "}] \`${item.path}\``),
    "",
    "## 环境变量检查",
    ...result.envChecks.map((item) => `- ${item.name}: ${item.ok ? "已配置" : "缺失/默认值"}`),
    "",
    "## 推荐执行命令",
    ...result.commands.map((item) => `- \`${item}\``),
    "",
    "## 结论",
    `- 当前仅完成测试计划与准备检查，未实际执行任何测试。`,
    `- ${result.ready ? "静态准备项基本齐全，可进入人工确认或后续真实执行。" : "仍存在准备缺口，建议先补齐缺失项再执行测试。"}`,
    "",
  ].join("\n");
}

async function main() {
  const database = dbConfig();
  const envChecks = [
    { name: "MYSQL_HOST", ok: !!process.env.MYSQL_HOST },
    { name: "MYSQL_PORT", ok: !!process.env.MYSQL_PORT },
    { name: "MYSQL_DB", ok: !!process.env.MYSQL_DB },
    { name: "MYSQL_USER", ok: !!process.env.MYSQL_USER },
    { name: "MYSQL_PWD", ok: !!process.env.MYSQL_PWD },
    { name: "VUE_APP_BASE_API", ok: !!process.env.VUE_APP_BASE_API },
  ];

  const files = REQUIRED_FILES.map((relativePath) => ({
    path: relativePath,
    exists: fileExists(path.resolve(ROOT, relativePath)),
  }));

  const batchNo = `PAY-PREP-${nowStamp()}`;
  const result = {
    batchNo,
    generatedAt: new Date().toISOString(),
    baseUrl: DEFAULT_BASE_URL,
    frontendUrl: process.env.PLAYWRIGHT_BASE_URL || "http://127.0.0.1:81",
    database: {
      host: database.host,
      port: database.port,
      database: database.database,
      user: database.user,
    },
    files,
    envChecks,
    commands: RECOMMENDED_COMMANDS,
    ready: files.every((item) => item.exists),
  };

  const jsonPath = path.resolve(REPORT_DIR, `${batchNo}.json`);
  const markdownPath = path.resolve(REPORT_DIR, `${batchNo}.md`);
  await writeJsonReport(jsonPath, result);
  await writeMarkdownReport(markdownPath, buildMarkdown(result));

  console.log(JSON.stringify(result, null, 2));
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
