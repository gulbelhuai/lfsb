const path = require("path");
const { hasExecuteFlag, printStepPlan, runCommandSteps } = require("./lib/testEnv");

const ROOT = path.resolve(__dirname, "..");

const STEPS = [
  { name: "预到龄测试环境准备检查", command: "node", args: [".\\tests\\准备预到龄发放通知测试环境.js"] },
  { name: "预到龄通知闭环计划输出", command: "node", args: [".\\tests\\验证预到龄通知闭环.js"] },
  { name: "预到龄通知结果回查", command: "node", args: [".\\tests\\回查预到龄通知结果.js"] },
  { name: "后端构建校验", command: "mvn", args: ["compile", "-DskipTests"] },
  { name: "UI 冒烟骨架", command: "npm", args: ["run", "test:e2e:benefit-notice:smoke"], cwd: path.resolve(ROOT, "ruoyi-ui") },
  { name: "UI 流程骨架", command: "npm", args: ["run", "test:e2e:benefit-notice"], cwd: path.resolve(ROOT, "ruoyi-ui") },
  { name: "UI 异常骨架", command: "npm", args: ["run", "test:e2e:benefit-notice:exceptions"], cwd: path.resolve(ROOT, "ruoyi-ui") },
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
