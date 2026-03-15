const { test, expect } = require("@playwright/test");

test.describe("预到龄发放通知页面冒烟", () => {
  test.skip(true, "当前只完成页面冒烟骨架，待 storageState 与真实账号统一后启用。");

  test("NOTICE-UI-01 至 NOTICE-UI-03 关键页面可访问", async ({ page }) => {
    const pages = [
      { path: "/benefit/notice", testId: "benefit-notice-page" },
      { path: "/benefit/determination", testId: "benefit-determination-page" },
      { path: "/benefit/review", testId: "benefit-review-page" },
    ];

    for (const item of pages) {
      await page.goto(item.path);
      await expect(page.getByTestId(item.testId)).toBeVisible();
    }
  });
});
