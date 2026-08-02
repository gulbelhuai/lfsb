const path = require("path");
const {
  createDbConnection,
  nowStamp,
  writeJsonReport,
  writeMarkdownReport,
} = require("./lib/testEnv");

const REPORT_DIR = path.resolve(__dirname, "reports", "benefit-notice");
const noticeMonth = process.env.NOTICE_TEST_MONTH || "2030-02";

function buildMarkdown(result) {
  return [
    "# 预到龄通知数据回查结果",
    "",
    "## 基本信息",
    `- 回查批次：\`${result.batchNo}\``,
    `- 通知月份（查询条件）：\`${result.noticeMonth}\``,
    "",
    "## 数据摘要",
    `- 符合预到龄条件人数（即时口径粗算）：${result.summary.eligiblePersonCount}`,
    `- 支付计划记录数（业务期/批次模糊匹配）：${result.summary.paymentPlanCount}`,
    "",
    "## 当前说明",
    "- 预到龄为即时查询导出，**无通知批次落库**。",
    "- 本脚本仅做数据库回查与报告输出，不修改业务数据。",
    "",
  ].join("\n");
}

async function main() {
  const batchNo = `NOTICE-CHECK-${nowStamp()}`;
  const conn = await createDbConnection();
  try {
    const [[eligiblePersonCountRow]] = await conn.query(
      `SELECT COUNT(*) AS count
         FROM shebao_subsidy_person sp
        WHERE sp.del_flag = '0'
          AND sp.birthday IS NOT NULL
          AND sp.subsidy_status = '0'
          AND sp.person_status = '0'
          AND DATE_FORMAT(DATE_ADD(sp.birthday, INTERVAL 60 YEAR), '%Y-%m') <= ?`,
      [noticeMonth]
    );
    const [[paymentPlanCountRow]] = await conn.query(
      `SELECT COUNT(*) AS count FROM shebao_payment_plan
       WHERE del_flag = '0' AND CAST(business_period AS CHAR) LIKE ?`,
      [`%${noticeMonth}%`]
    );

    const result = {
      batchNo,
      generatedAt: new Date().toISOString(),
      noticeMonth,
      summary: {
        eligiblePersonCount: eligiblePersonCountRow.count,
        paymentPlanCount: paymentPlanCountRow.count,
      },
    };

    await writeJsonReport(REPORT_DIR, `NOTICE-CHECK-${nowStamp()}.json`, result);
    await writeMarkdownReport(REPORT_DIR, `NOTICE-CHECK-${nowStamp()}.md`, buildMarkdown(result));
    console.log(JSON.stringify(result, null, 2));
  } finally {
    await conn.end();
  }
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
