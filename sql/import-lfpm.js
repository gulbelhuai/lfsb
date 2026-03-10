/**
 * 导入 lfpm.sql 到数据库，导入前清空数据库
 * 使用方式: node import-lfpm.js
 */
require('dotenv').config({ path: require('path').join(__dirname, '..', '.env') });
const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');

const config = {
  host: process.env.MYSQL_HOST || '192.168.88.48',
  port: parseInt(process.env.MYSQL_PORT || '5455', 10),
  user: process.env.MYSQL_USER || 'root',
  password: process.env.MYSQL_PWD || 'infini_rag_flow',
  database: process.env.MYSQL_DB || 'lfpm',
  multipleStatements: true
};

async function main() {
  const sqlPath = path.join(__dirname, 'lfpm.sql');
  if (!fs.existsSync(sqlPath)) {
    console.error('未找到 lfpm.sql');
    process.exit(1);
  }
  const sqlContent = fs.readFileSync(sqlPath, 'utf8');

  console.log('连接数据库...', { host: config.host, port: config.port, database: config.database });
  const conn = await mysql.createConnection({
    ...config,
    database: undefined
  });

  try {
    console.log('清空数据库...');
    await conn.query(`DROP DATABASE IF EXISTS \`${config.database}\``);
    await conn.query(`CREATE DATABASE \`${config.database}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci`);
    await conn.query(`USE \`${config.database}\``);

    console.log('导入 sql/lfpm.sql ...');
    await conn.query(sqlContent);
    console.log('导入完成');
  } catch (err) {
    console.error('导入失败:', err.message);
    process.exit(1);
  } finally {
    await conn.end();
  }
}

main();
