const path = require("path");
const {
  createDbConnection,
  nowStamp,
  writeJsonReport,
  writeMarkdownReport,
} = require("./lib/testEnv");

const REPORT_DIR = path.resolve(__dirname, "reports", "benefit-notice");
const noticeMonth = process.env.NOTICE_TEST_MONTH || "2030-02";
const noticeBatchNo = process.env.NOTICE_BATCH_NO || "";

function buildMarkdown(result) {
  return [
    "# 预到龄通知数据回查结果",
    "",
    "## 基本信息",
    `- 回查批次：\`${result.batchNo}\``,
    `- 通知月份：\`${result.noticeMonth}\``,
    `- 通知批次号：\`${result.noticeBatchNo || "未指定"}\``,
    "",
    "## 数据摘要",
    `- 通知主表记录数：${result.summary.noticeBatchCount}`,
    `- 通知明细记录数：${result.summary.noticeDetailCount}`,
    `- 核定记录数：${result.summary.determinationCount}`,
    `- 已审核通过数：${result.summary.approvedDeterminationCount}`,
    `- 已生成支付计划数：${result.summary.paymentGeneratedCount}`,
    `- 支付计划记录数：${result.summary.paymentPlanCount}`,
    "",
    "## 当前说明",
    "- 当前脚本仅做数据库回查与报告输出，不修改业务数据。",
    "- 若未指定 `NOTICE_BATCH_NO`，则按通知月份汇总统计。",
    "",
  ].join("\n");
}

async function main() {
  const batchNo = `NOTICE-CHECK-${nowStamp()}`;
  const conn = await createDbConnection();
  try {
    const whereSql = noticeBatchNo ? "batch_no = ?" : "notice_month = ?";
    const determinationWhereSql = noticeBatchNo ? "notice_batch_no = ?" : "notice_batch_no IN (SELECT batch_no FROM shebao_benefit_notice_batch WHERE notice_month = ?)";
    const bind = [noticeBatchNo || noticeMonth];

    const [[noticeBatchCountRow]] = await conn.query(
      `SELECT COUNT(*) AS count FROM shebao_benefit_notice_batch WHERE ${whereSql}`,
      bind
    );
    const [[noticeDetailCountRow]] = await conn.query(
      `SELECT COUNT(*) AS count FROM shebao_benefit_notice_detail WHERE batch_no IN (
         SELECT batch_no FROM shebao_benefit_notice_batch WHERE ${whereSql}
       )`,
      bind
    );
    const [[determinationCountRow]] = await conn.query(
      `SELECT COUNT(*) AS count FROM benefit_determination WHERE ${determinationWhereSql}`,
      bind
    );
    const [[approvedDeterminationCountRow]] = await conn.query(
      `SELECT COUNT(*) AS count FROM benefit_determination
       WHERE ${determinationWhereSql} AND approval_status = 'approved'`,
      bind
    );
    const [[paymentGeneratedCountRow]] = await conn.query(
      `SELECT COUNT(*) AS count FROM benefit_determination
       WHERE ${determinationWhereSql} AND payment_plan_generated = '1'`,
      bind
    );
    const [[paymentPlanCountRow]] = await conn.query(
      `SELECT COUNT(*) AS count FROM shebao_subsidy_distribution
       WHERE remark LIKE ?`,
      [`%${noticeBatchNo || noticeMonth}%`]
    );

    const result = {
      batchNo,
      generatedAt: new Date().toISOString(),
      noticeMonth,
      noticeBatchNo,
      summary: {
        noticeBatchCount: noticeBatchCountRow.count,
        noticeDetailCount: noticeDetailCountRow.count,
        determinationCount: determinationCountRow.count,
        approvedDeterminationCount: approvedDeterminationCountRow.count,
        paymentGeneratedCount: paymentGeneratedCountRow.count,
        paymentPlanCount: paymentPlanCountRow.count,
      },
    };

    const jsonPath = path.resolve(REPORT_DIR, `${batchNo}.json`);
    const markdownPath = path.resolve(REPORT_DIR, `${batchNo}.md`);
    await writeJsonReport(jsonPath, result);
    await writeMarkdownReport(markdownPath, buildMarkdown(result));

    console.log(JSON.stringify(result, null, 2));
  } finally {
    await conn.end();
  }
}

main().catch((error) => {
  console.error(error.stack || error.message);
  process.exit(1);
});
