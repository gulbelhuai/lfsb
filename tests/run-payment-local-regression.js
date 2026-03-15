const path = require("path");
const { hasExecuteFlag, printStepPlan, runCommandSteps } = require("./lib/testEnv");

const ROOT = path.resolve(__dirname, "..");

const STEPS = [
  { name: "支付准备检查", command: "node", args: [".\\tests\\支付工作闭环准备检查.js"] },
  { name: "支付闭环 dry-run", command: "node", args: [".\\tests\\验证支付工作闭环.js", "--dry-run"] },
  { name: "后端构建校验", command: "mvn", args: ["compile", "-DskipTests"] },
  { name: "UI 自动化", command: "npm", args: ["run", "test:e2e"], cwd: path.resolve(ROOT, "ruoyi-ui") },
];

function main() {
  const execute = hasExecuteFlag();
  if (!execute) {
    printStepPlan(STEPS, ROOT);
    return;
  }

  runCommandSteps(STEPS, ROOT);
}

main();
