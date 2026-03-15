const fs = require("fs");
const fsp = require("fs/promises");
const path = require("path");
const { execFileSync } = require("child_process");
const { openAsBlob } = require("node:fs");
const {
  DEFAULT_BASE_URL,
  createPlanResult,
  createDbConnection,
  hasExecuteFlag,
  login,
  requestJson,
  sendForm,
  getJson,
  writeReportBundle,
} = require("./lib/testEnv");
const { getBenefitNoticeDataset } = require("./lib/benefitNoticeDataset");

const BASE_URL = DEFAULT_BASE_URL;
const NOTICE_MONTH = process.env.NOTICE_TEST_MONTH || "2030-02";
const DATASET = getBenefitNoticeDataset(NOTICE_MONTH);
const REPORT_DIR = path.resolve(__dirname, "reports", "benefit-notice");
const FIXTURE_ID_CARDS = DATASET.fixtureIdCards;

const PLAN = [
  "清理预到龄通知专项旧测试数据",
  "导入预到龄通知专项 SQL 样本",
  "经办人登录并生成通知批次",
  "按通知月份与批次号回查通知主表与明细",
  "进入待遇核定录入身份证、银行卡和补贴信息",
  "上传用户ID.zip 并校验材料解压与预览路径",
  "提交待遇核定进入复核状态",
  "复核人按批次检索并批量审核通过",
  "生成支付计划并校验支付落库结果",
  "输出 JSON 与 Markdown 测试报告",
];

function buildMarkdown(result) {
  const lines = [
    "# 预到龄通知闭环测试计划输出",
    "",
    "## 基本信息",
    `- 测试批次：\`${result.batchNo}\``,
    `- 模式：\`${result.mode}\``,
    `- 后端地址：\`${result.baseUrl}\``,
    `- 通知月份：\`${result.noticeMonth}\``,
    "",
    "## 计划步骤",
    ...result.plan.map((item, index) => `${index + 1}. ${item}`),
    "",
  ];

  if (result.mode === "execute") {
    lines.push("## 执行摘要");
    lines.push(`- 生成结果：\`${result.execution.generateResult?.data?.msg || ""}\``);
    lines.push(`- 提交结果：\`${result.execution.submitResp?.data?.msg || ""}\``);
    lines.push(`- 批量审核结果：\`${result.execution.batchApproveResp?.data?.msg || ""}\``);
    lines.push(`- 支付计划结果：\`${result.execution.paymentResp?.data?.msg || ""}\``);
    lines.push("");
  } else {
    lines.push("## 当前结论");
    lines.push("- 当前脚本默认仅输出 dry-run 计划，不执行真实接口和数据库写入。");
    lines.push("- 如需进入真实执行阶段，请显式追加 `--execute`。");
    lines.push("");
  }

  return lines.join("\n");
}

async function applyFixtures(conn) {
  const sql = await fsp.readFile(DATASET.fixtureSqlPath, "utf8");
  await conn.query(sql);
}

async function cleanupOldRecords(conn) {
  const [personRows] = await conn.query(
    `SELECT id FROM shebao_subsidy_person WHERE id_card_no IN (${FIXTURE_ID_CARDS.map(() => "?").join(",")})`,
    FIXTURE_ID_CARDS
  );
  const personIds = personRows.map((row) => row.id);
  if (personIds.length > 0) {
    const [villageOfficialRows] = await conn.query(
      `SELECT id FROM shebao_village_official WHERE subsidy_person_id IN (${personIds.map(() => "?").join(",")})`,
      personIds
    ).catch(() => [[]]);
    const villageOfficialIds = (villageOfficialRows || []).map((row) => row.id);
    if (villageOfficialIds.length > 0) {
      await conn.query(
        `DELETE FROM shebao_village_official_position WHERE village_official_id IN (${villageOfficialIds.map(() => "?").join(",")})`,
        villageOfficialIds
      ).catch(() => {});
    }
    await conn.query(
      `DELETE FROM shebao_village_official WHERE subsidy_person_id IN (${personIds.map(() => "?").join(",")})`,
      personIds
    ).catch(() => {});
    await conn.query(
      `DELETE FROM shebao_demolition_resident WHERE subsidy_person_id IN (${personIds.map(() => "?").join(",")})`,
      personIds
    ).catch(() => {});
    await conn.query(
      `DELETE FROM shebao_land_loss_resident WHERE subsidy_person_id IN (${personIds.map(() => "?").join(",")})`,
      personIds
    ).catch(() => {});
    await conn.query(
      `DELETE FROM shebao_benefit_attachment WHERE subsidy_person_id IN (${personIds.map(() => "?").join(",")})`,
      personIds
    ).catch(() => {});
    await conn.query(
      `DELETE FROM shebao_benefit_notice_detail WHERE subsidy_person_id IN (${personIds.map(() => "?").join(",")})`,
      personIds
    ).catch(() => {});
    await conn.query(
      `DELETE FROM benefit_determination WHERE subsidy_person_id IN (${personIds.map(() => "?").join(",")})`,
      personIds
    ).catch(() => {});
    await conn.query(
      `DELETE FROM shebao_subsidy_distribution WHERE subsidy_person_id IN (${personIds.map(() => "?").join(",")})`,
      personIds
    ).catch(() => {});
  }
  await conn.query(`DELETE FROM shebao_benefit_notice_batch WHERE notice_month = ?`, [NOTICE_MONTH]).catch(() => {});
}

async function createZipForUser(userId) {
  const tempDir = path.resolve(__dirname, "tmp", "notice-flow");
  await fsp.mkdir(tempDir, { recursive: true });
  const imagePath = path.join(tempDir, `${userId}.png`);
  const zipPath = path.join(tempDir, `${userId}.zip`);
  const pngBase64 =
    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9Wm8WQAAAABJRU5ErkJggg==";
  await fsp.writeFile(imagePath, Buffer.from(pngBase64, "base64"));
  if (fs.existsSync(zipPath)) {
    await fsp.unlink(zipPath);
  }
  execFileSync("powershell", [
    "-NoProfile",
    "-Command",
    `Compress-Archive -Path "${imagePath}" -DestinationPath "${zipPath}" -Force`,
  ]);
  return zipPath;
}

async function executeFlow() {
  const conn = await createDbConnection();
  try {
    await cleanupOldRecords(conn);
    await applyFixtures(conn);

    const operatorToken = await login("operator01", "Test@12345");
    const reviewerToken = await login("reviewer01", "Test@12345");

    const generateResult = await requestJson(
      "POST",
      `${BASE_URL}/shebao/benefit/notice/generate`,
      operatorToken,
      { noticeMonth: NOTICE_MONTH, ageThreshold: 60 }
    );

    const batchList = await getJson(
      `${BASE_URL}/shebao/benefit/notice/batch/list?noticeMonth=${NOTICE_MONTH}&pageNum=1&pageSize=20`,
      operatorToken
    );
    const batch = (batchList.data.rows || [])[0];
    if (!batch || !batch.batchNo) {
      throw new Error(`batch not found for ${NOTICE_MONTH}: ${JSON.stringify(batchList.data)}`);
    }

    const detailList = await getJson(
      `${BASE_URL}/shebao/benefit/notice/detail/list?batchNo=${encodeURIComponent(batch.batchNo)}`,
      operatorToken
    );
    const details = detailList.data.rows || [];
    const targetDetail = details.find((item) => item.name === DATASET.primaryTarget.name);
    if (!targetDetail || !targetDetail.determinationId) {
      throw new Error(`target detail missing: ${JSON.stringify(details)}`);
    }

    const determinationResp = await getJson(
      `${BASE_URL}/shebao/benefit/determination/${targetDetail.determinationId}`,
      operatorToken
    );
    const determination = determinationResp.data.data;

    const updateResp = await requestJson(
      "PUT",
      `${BASE_URL}/shebao/benefit/determination`,
      operatorToken,
      {
        id: determination.id,
        subsidyPersonId: determination.subsidyPersonId,
        noticeBatchNo: determination.noticeBatchNo,
        noticeDetailId: determination.noticeDetailId,
        subsidyType: determination.subsidyType,
        idCardNo: DATASET.primaryTarget.idCardNo,
        bankName: "中国银行",
        bankAccount: DATASET.primaryTarget.bankAccount,
        bankAccountName: DATASET.primaryTarget.bankAccountName,
        subsidyStandard: 500,
        benefitStartYear: DATASET.primaryTarget.benefitStartYear,
        benefitStartMonth: DATASET.primaryTarget.benefitStartMonth,
      }
    );

    const zipPath = await createZipForUser(determination.subsidyPersonId);
    const uploadForm = new FormData();
    uploadForm.set("determinationId", String(determination.id));
    uploadForm.set(
      "file",
      await openAsBlob(zipPath, { type: "application/zip" }),
      `${determination.subsidyPersonId}.zip`
    );
    const uploadResp = await sendForm(
      `${BASE_URL}/shebao/benefit/determination/attachment/upload`,
      operatorToken,
      uploadForm
    );

    const submitResp = await requestJson(
      "POST",
      `${BASE_URL}/shebao/benefit/determination/submit/${determination.id}`,
      operatorToken,
      {}
    );

    const batchApproveResp = await requestJson(
      "POST",
      `${BASE_URL}/shebao/benefit/review/batchApprove`,
      reviewerToken,
      { noticeBatchNo: batch.batchNo, ids: [determination.id], remark: "闭环自动验证通过" }
    );

    const paymentResp = await requestJson(
      "POST",
      `${BASE_URL}/shebao/payment/plan/generateByNoticeBatch`,
      operatorToken,
      { noticeBatchNo: batch.batchNo }
    );

    const [batchRows] = await conn.query(
      `SELECT batch_no, notice_month, total_count, pending_review_count, approved_count, rejected_count, batch_status
       FROM shebao_benefit_notice_batch WHERE batch_no = ?`,
      [batch.batchNo]
    );
    const [detailRows] = await conn.query(
      `SELECT subsidy_person_id, name, determination_status, material_status, review_status
       FROM shebao_benefit_notice_detail WHERE batch_no = ? ORDER BY subsidy_person_id`,
      [batch.batchNo]
    );
    const [determinationRows] = await conn.query(
      `SELECT id, notice_batch_no, approval_status, bank_name, bank_account, material_status, payment_plan_generated
       FROM benefit_determination WHERE notice_batch_no = ? ORDER BY id`,
      [batch.batchNo]
    );
    const [distributionRows] = await conn.query(
      `SELECT subsidy_person_id, subsidy_type, subsidy_record_id, distribution_status, remark
       FROM shebao_subsidy_distribution WHERE subsidy_record_id = ? ORDER BY id DESC`,
      [determination.id]
    );

    return {
      noticeMonth: NOTICE_MONTH,
      generateResult,
      batch,
      updateResp,
      uploadResp,
      submitResp,
      batchApproveResp,
      paymentResp,
      batchRows,
      detailRows,
      determinationRows,
      distributionRows,
    };
  } finally {
    await conn.end();
  }
}

async function main() {
  const execute = hasExecuteFlag();
  const result = createPlanResult({
    prefix: "NOTICE-FLOW",
    mode: execute ? "execute" : "dry-run",
    baseUrl: BASE_URL,
    extra: {
      noticeMonth: NOTICE_MONTH,
      plan: PLAN,
    },
  });

  if (execute) {
    result.execution = await executeFlow();
  }

  await writeReportBundle({
    reportDir: REPORT_DIR,
    fileStem: result.batchNo,
    jsonContent: result,
    markdownContent: buildMarkdown(result),
  });

  console.log(JSON.stringify(result, null, 2));
}

main().catch((err) => {
  console.error(err.stack || err.message);
  process.exit(1);
});
