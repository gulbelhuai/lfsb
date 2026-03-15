const fs = require("fs");
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

async function main() {
  const config = {
    host: pickEnv("MYSQL_HOST", "DB_HOST", "DATABASE_HOST") || "127.0.0.1",
    port: Number(pickEnv("MYSQL_PORT", "DB_PORT") || 3306),
    user: pickEnv("MYSQL_USER", "DB_USER", "DATABASE_USER") || "root",
    password: pickEnv("MYSQL_PWD", "MYSQL_PASSWORD", "DB_PASSWORD") || "",
    database: pickEnv("MYSQL_DB", "DB_NAME", "DATABASE_NAME") || "lfpm",
    charset: "utf8mb4",
    multipleStatements: true
  };

  const sqlPath = path.resolve(__dirname, "预到龄发放通知测试数据.sql");
  const sql = fs.readFileSync(sqlPath, "utf8");

  const conn = await mysql.createConnection(config);
  const [result] = await conn.query(sql);
  console.log("notice fixtures applied");
  if (Array.isArray(result) && result.length > 0) {
    console.log(JSON.stringify(result[result.length - 1], null, 2));
  }
  await conn.end();
}

main().catch((err) => {
  console.error(err.stack || err.message);
  process.exit(1);
});
