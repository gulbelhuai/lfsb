const { test, expect } = require("@playwright/test");

test.describe("支付工作异常与边界", () => {
  test.skip(true, "当前只完成异常场景骨架准备，待错误样例和权限账号固化后启用。");

  test("PAY-GEN-05 PAY-IMP-04 PAY-AUTH-01 异常入口", async ({ page }) => {
    await page.goto("/shebao/payment/plan");
    await expect(page.getByTestId("payment-plan-generate")).toBeVisible();

    await page.goto("/shebao/finance/bank");
    await expect(page.locator(".app-container")).toBeVisible();

    await page.goto("/shebao/finance/failure");
    await expect(page.locator(".app-container")).toBeVisible();
  });
});
