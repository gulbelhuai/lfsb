const path = require("path");

require("../sql/node_modules/dotenv").config({ path: path.resolve(__dirname, "..", ".env") });
const mysql = require("../sql/node_modules/mysql2/promise");

function pickEnv(...keys) {
  for (const key of keys) {
    if (process.env[key]) {
      return process.env[key];
    }
  }
  return undefined;
}

function dbConfig() {
  return {
    host: pickEnv("MYSQL_HOST", "DB_HOST", "DATABASE_HOST") || "127.0.0.1",
    port: Number(pickEnv("MYSQL_PORT", "DB_PORT") || 3306),
    user: pickEnv("MYSQL_USER", "DB_USER", "DATABASE_USER") || "root",
    password: pickEnv("MYSQL_PWD", "MYSQL_PASSWORD", "DB_PASSWORD") || "",
    database: pickEnv("MYSQL_DB", "DB_NAME", "DATABASE_NAME") || "lfpm",
    charset: "utf8mb4"
  };
}

async function login(username, password) {
  const response = await fetch("http://localhost:8087/api/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  });
  const data = await response.json();
  if (!response.ok || data.code !== 200 || !data.token) {
    throw new Error(`login failed for ${username}: ${JSON.stringify(data)}`);
  }
  return data.token;
}

async function getJson(url, token) {
  const response = await fetch(url, {
    headers: { Authorization: `Bearer ${token}` }
  });
  return response.json();
}

async function postJson(url, token, body) {
  const response = await fetch(url, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(body)
  });
  const data = await response.json();
  return { status: response.status, data };
}

async function main() {
  const operatorToken = await login("operator01", "Test@12345");
  const reviewerToken = await login("reviewer01", "Test@12345");

  const defaultList = await getJson("http://localhost:8087/api/shebao/benefit/notice/list?pageNum=1&pageSize=20", operatorToken);
  const juneList = await getJson("http://localhost:8087/api/shebao/benefit/notice/list?noticeMonth=2026-06&pageNum=1&pageSize=20", operatorToken);
  const generateResult = await postJson("http://localhost:8087/api/shebao/benefit/notice/generate", operatorToken, {
    noticeMonth: "2026-06",
    ageThreshold: 60
  });
  const reviewerGenerate = await postJson("http://localhost:8087/api/shebao/benefit/notice/generate", reviewerToken, {
    noticeMonth: "2026-06",
    ageThreshold: 60
  });

  const conn = await mysql.createConnection(dbConfig());
  const [sampleRows] = await conn.query(
    `SELECT name, id_card_no, approval_status, del_flag, is_alive, remark
       FROM shebao_subsidy_person
      WHERE remark LIKE 'BN-%'
      ORDER BY remark`
  );
  const [noticeTables] = await conn.query(
    `SELECT table_name
       FROM information_schema.tables
      WHERE table_schema = DATABASE()
        AND table_name LIKE 'shebao%notice%'`
  );
  await conn.end();

  const result = {
    defaultListRows: defaultList.rows || [],
    juneListRows: juneList.rows || [],
    defaultListNames: (defaultList.rows || []).map((item) => item.name),
    juneListNames: (juneList.rows || []).map((item) => item.name),
    defaultTotal: defaultList.total,
    juneTotal: juneList.total,
    generateResult,
    reviewerGenerate,
    sampleRows,
    noticeTables
  };

  console.log(JSON.stringify(result, null, 2));
}

main().catch((err) => {
  console.error(err.stack || err.message);
  process.exit(1);
});
