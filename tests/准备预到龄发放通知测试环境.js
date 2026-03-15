const fs = require('fs');
const fsp = require('fs/promises');
const path = require('path');
const { getBenefitNoticeDataset } = require('./lib/benefitNoticeDataset');

require('../sql/node_modules/dotenv').config({ path: path.resolve(__dirname, '..', '.env') });
const mysql = require('../sql/node_modules/mysql2/promise');

const NOTICE_MONTH = process.env.NOTICE_TEST_MONTH || '2030-02';
const DATASET = getBenefitNoticeDataset(NOTICE_MONTH);
const OUTPUT_DIR = path.resolve(__dirname, 'reports', 'benefit-notice');
const TMP_DIR = path.resolve(__dirname, 'tmp', 'notice-flow');
const FIXTURE_SQL_PATH = DATASET.fixtureSqlPath;
const SUMMARY_PATH = path.resolve(OUTPUT_DIR, '预到龄发放通知测试准备结果.json');

function dbConfig() {
  return {
    host: process.env.MYSQL_HOST || '127.0.0.1',
    port: Number(process.env.MYSQL_PORT || 3306),
    user: process.env.MYSQL_USER || 'root',
    password: process.env.MYSQL_PWD || '',
    database: process.env.MYSQL_DB || 'lfpm',
    charset: 'utf8mb4'
  };
}

async function ensureDirectories() {
  await fsp.mkdir(OUTPUT_DIR, { recursive: true });
  await fsp.mkdir(TMP_DIR, { recursive: true });
}

async function collectDatabaseSummary(conn) {
  const [tableRows] = await conn.query(
    `SELECT table_name
       FROM information_schema.tables
      WHERE table_schema = DATABASE()
        AND table_name IN (
          'shebao_benefit_notice_batch',
          'shebao_benefit_notice_detail',
          'shebao_benefit_attachment',
          'benefit_determination',
          'shebao_subsidy_distribution'
        )
      ORDER BY table_name`
  );

  const [userRows] = await conn.query(
    `SELECT user_name, status
       FROM sys_user
      WHERE user_name IN ('operator01', 'reviewer01')
      ORDER BY user_name`
  );

  const [fixtureRows] = await conn.query(
    `SELECT COUNT(*) AS fixtureCount
       FROM shebao_subsidy_person
      WHERE id_card_no IN (${DATASET.fixtureIdCards.map(() => '?').join(',')})`,
    DATASET.fixtureIdCards
  );

  return {
    tables: tableRows.map(item => item.table_name),
    users: userRows,
    fixtureCount: fixtureRows[0] ? fixtureRows[0].fixtureCount : 0
  };
}

async function main() {
  await ensureDirectories();
  const conn = await mysql.createConnection(dbConfig());
  try {
    const summary = {
      generatedAt: new Date().toISOString(),
      environment: {
        apiBaseUrl: process.env.PLAYWRIGHT_BASE_URL || 'http://127.0.0.1:81',
        noticeMonth: NOTICE_MONTH,
        mysqlHost: process.env.MYSQL_HOST || '127.0.0.1',
        mysqlPort: Number(process.env.MYSQL_PORT || 3306),
        mysqlDb: process.env.MYSQL_DB || 'lfpm',
        redisHost: process.env.REDIS_HOST || '127.0.0.1',
        profilePath: process.env.PROFILE_PATH || '',
        logPath: process.env.LOG_PATH || ''
      },
      files: {
        fixtureSqlExists: fs.existsSync(FIXTURE_SQL_PATH),
        fixtureSqlPath: FIXTURE_SQL_PATH,
        outputDir: OUTPUT_DIR,
        tmpDir: TMP_DIR
      },
      database: await collectDatabaseSummary(conn),
      nextSteps: [
        '确认主测试环境后端与前端已启动',
        '如需导入示例数据，执行 tests/导入预到龄测试数据.js 或使用专项 SQL',
        '如需执行真实 UI 自动化，设置 RUN_REAL_E2E=true 后运行 ruoyi-ui 的 Playwright 用例',
        '执行后将报告统一输出到 tests/reports 目录'
      ]
    };

    await fsp.writeFile(SUMMARY_PATH, JSON.stringify(summary, null, 2));
    console.log(`benefit-notice test preparation summary generated: ${SUMMARY_PATH}`);
  } finally {
    await conn.end();
  }
}

main().catch(err => {
  console.error(err.stack || err.message);
  process.exit(1);
});
