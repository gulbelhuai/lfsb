const fs = require("fs");
const fsp = require("fs/promises");
const path = require("path");
const { spawnSync } = require("child_process");

require("../../sql/node_modules/dotenv").config({
  path: path.resolve(__dirname, "..", "..", ".env"),
});

const mysql = require("../../sql/node_modules/mysql2/promise");

const DEFAULT_BASE_URL = process.env.TEST_BASE_URL || "http://localhost:8087/api";

function dbConfig() {
  return {
    host: process.env.MYSQL_HOST || "127.0.0.1",
    port: Number(process.env.MYSQL_PORT || 3306),
    user: process.env.MYSQL_USER || "root",
    password: process.env.MYSQL_PWD || "",
    database: process.env.MYSQL_DB || "lfpm",
    charset: "utf8mb4",
    multipleStatements: true,
  };
}

async function createDbConnection() {
  return mysql.createConnection(dbConfig());
}

async function login(username, password, baseUrl = DEFAULT_BASE_URL) {
  const response = await fetch(`${baseUrl}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });
  const data = await response.json();
  if (!response.ok || data.code !== 200 || !data.token) {
    throw new Error(`login failed for ${username}: ${JSON.stringify(data)}`);
  }
  return data.token;
}

async function requestJson(method, url, token, body) {
  const response = await fetch(url, {
    method,
    headers: {
      Authorization: token ? `Bearer ${token}` : undefined,
      "Content-Type": "application/json",
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  });
  const data = await response.json();
  return { status: response.status, data };
}

async function getJson(url, token) {
  const response = await fetch(url, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
  const data = await response.json();
  return { status: response.status, data };
}

async function sendForm(url, token, formData) {
  const response = await fetch(url, {
    method: "POST",
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    body: formData,
  });
  const data = await response.json();
  return { status: response.status, data };
}

async function ensureDir(dirPath) {
  await fsp.mkdir(dirPath, { recursive: true });
}

function nowStamp() {
  const now = new Date();
  const pad = (value) => String(value).padStart(2, "0");
  return `${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}-${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`;
}

async function writeJsonReport(filePath, content) {
  await ensureDir(path.dirname(filePath));
  await fsp.writeFile(filePath, JSON.stringify(content, null, 2), "utf8");
}

async function writeMarkdownReport(filePath, content) {
  await ensureDir(path.dirname(filePath));
  await fsp.writeFile(filePath, content, "utf8");
}

function fileExists(filePath) {
  return fs.existsSync(filePath);
}

function hasExecuteFlag(argv = process.argv) {
  return argv.includes("--execute");
}

function createPlanResult({ prefix, mode, baseUrl = DEFAULT_BASE_URL, extra = {} }) {
  return {
    batchNo: `${prefix}-${nowStamp()}`,
    generatedAt: new Date().toISOString(),
    mode,
    baseUrl,
    ...extra,
  };
}

async function writeReportBundle({ reportDir, fileStem, jsonContent, markdownContent }) {
  const jsonPath = path.resolve(reportDir, `${fileStem}.json`);
  const markdownPath = path.resolve(reportDir, `${fileStem}.md`);
  await writeJsonReport(jsonPath, jsonContent);
  await writeMarkdownReport(markdownPath, markdownContent);
  return { jsonPath, markdownPath };
}

function formatCommandStep(step, rootDir) {
  return {
    name: step.name,
    command: [step.command, ...(step.args || [])].join(" "),
    cwd: step.cwd || rootDir,
  };
}

function printStepPlan(steps, rootDir) {
  console.log(
    JSON.stringify(
      {
        mode: "dry-run",
        steps: steps.map((step) => formatCommandStep(step, rootDir)),
      },
      null,
      2
    )
  );
}

function executeCommandStep(step, rootDir) {
  return spawnSync(step.command, step.args || [], {
    cwd: step.cwd || rootDir,
    stdio: "inherit",
    shell: true,
  });
}

function runCommandSteps(steps, rootDir) {
  for (const step of steps) {
    const result = executeCommandStep(step, rootDir);
    if (result.status !== 0) {
      process.exit(result.status || 1);
    }
  }
}

module.exports = {
  createPlanResult,
  DEFAULT_BASE_URL,
  createDbConnection,
  dbConfig,
  executeCommandStep,
  fileExists,
  formatCommandStep,
  getJson,
  hasExecuteFlag,
  login,
  nowStamp,
  printStepPlan,
  requestJson,
  runCommandSteps,
  sendForm,
  writeReportBundle,
  writeJsonReport,
  writeMarkdownReport,
};
