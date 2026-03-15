const { test, expect } = require("@playwright/test");

test.describe("预到龄发放通知异常与边界", () => {
  test.skip(true, "当前只完成异常场景骨架准备，待错误样例、边界数据和权限账号固化后启用。");

  test("NOTICE-EX-01 至 NOTICE-EX-05 异常入口", async ({ page }) => {
    await page.goto("/benefit/notice");
    await expect(page.getByTestId("notice-generate-submit")).toBeVisible();

    await page.goto("/benefit/determination");
    await expect(page.getByTestId("determination-batch-import-open")).toBeVisible();
    await expect(page.getByTestId("determination-download-template")).toBeVisible();

    await page.goto("/benefit/review");
    await expect(page.getByTestId("review-batch-approve")).toBeVisible();
  });
});
