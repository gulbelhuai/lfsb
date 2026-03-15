const { test, expect } = require("@playwright/test");

test.describe("支付工作页面冒烟", () => {
  test.skip(true, "当前只完成用例骨架准备，待本地真实账号和 storageState 就绪后启用。");

  test("PAY-UI-01 至 PAY-UI-06 页面可访问", async ({ page }) => {
    const pages = [
      "/shebao/payment/plan",
      "/shebao/payment/review",
      "/shebao/payment/approve",
      "/shebao/finance/batch",
      "/shebao/finance/bank",
      "/shebao/finance/failure",
    ];

    for (const route of pages) {
      await page.goto(route);
      await expect(page.locator(".app-container")).toBeVisible();
    }
  });
});
