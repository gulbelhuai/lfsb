const { test, expect } = require("@playwright/test");

test.describe("支付工作闭环流程", () => {
  test.skip(true, "当前只完成流程骨架准备，待角色登录态、测试数据和银行样例文件固化后启用。");

  test("PAY-GEN-03 到 PAY-IMP-03 主流程", async ({ page }) => {
    await page.goto("/shebao/payment/plan");
    await expect(page.getByTestId("payment-plan-preview")).toBeVisible();
    await expect(page.getByTestId("payment-plan-generate")).toBeVisible();
    await expect(page.getByTestId("payment-plan-create-batch")).toBeVisible();

    await page.goto("/shebao/payment/review");
    await expect(page.locator(".app-container")).toBeVisible();

    await page.goto("/shebao/payment/approve");
    await expect(page.locator(".app-container")).toBeVisible();

    await page.goto("/shebao/finance/batch");
    await expect(page.locator(".app-container")).toBeVisible();

    await page.goto("/shebao/finance/bank");
    await expect(page.locator(".app-container")).toBeVisible();
  });
});
