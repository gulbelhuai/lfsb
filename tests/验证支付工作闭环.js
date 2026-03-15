const path = require("path");
const {
  DEFAULT_BASE_URL,
  createPlanResult,
  hasExecuteFlag,
  writeReportBundle,
} = require("./lib/testEnv");

const REPORT_DIR = path.resolve(__dirname, "reports");

const PLAN = [
  "准备支付闭环专用测试数据",
  "经办人登录并预览支付统计",
  "生成支付计划并创建支付批次",
  "复核人通过支付批次",
  "审批人通过支付批次",
  "财务生成银行文件并提交银行",
  "导入银行结果文件",
  "校验失败处理与二次发放入口",
  "回查数据库状态与页面状态一致性",
];

function buildMarkdown(result) {
  return [
    "# 支付工作闭环测试计划输出",
    "",
    "## 基本信息",
    `- 测试批次：\`${result.batchNo}\``,
    `- 模式：\`${result.mode}\``,
    `- 后端地址：\`${result.baseUrl}\``,
    "",
    "## 计划步骤",
    ...result.plan.map((item, index) => `${index + 1}. ${item}`),
    "",
    "## 当前结论",
    "- 当前脚本默认仅输出 dry-run 计划，不执行真实接口和数据库写入。",
    "- 如需进入真实执行阶段，可在确认环境、账号、样例文件齐备后再补充执行逻辑。",
    "",
  ].join("\n");
}

async function main() {
  const execute = hasExecuteFlag();
  const result = createPlanResult({
    prefix: "PAY-FLOW",
    mode: execute ? "execute" : "dry-run",
    baseUrl: DEFAULT_BASE_URL,
    extra: { plan: PLAN },
  });

  await writeReportBundle({
    reportDir: REPORT_DIR,
    fileStem: result.batchNo,
    jsonContent: result,
    markdownContent: buildMarkdown(result),
  });

  if (execute) {
    throw new Error("支付工作真实闭环执行逻辑仍待补充，当前版本只完成准备工作。");
  }

  console.log(JSON.stringify(result, null, 2));
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
