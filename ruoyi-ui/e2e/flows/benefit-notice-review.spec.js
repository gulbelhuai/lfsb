const { test, expect } = require('@playwright/test');
const {
  byTestId,
  clickByTestId,
  clickFirstByPrefix,
  expectMessage,
  fillByTestId,
  loginWithRealUser,
  openPath,
  promptDialog
} = require('../support/benefit-notice');

const runRealE2E = process.env.RUN_REAL_E2E === 'true';
const reviewerUser = process.env.PLAYWRIGHT_REVIEWER_USER || 'reviewer01';
const reviewerPassword = process.env.PLAYWRIGHT_REVIEWER_PASSWORD || 'Test@12345';
const expectedBatchKeyword = process.env.PLAYWRIGHT_EXPECT_BATCH_KEYWORD || 'NT';
const reviewRemark = process.env.PLAYWRIGHT_REVIEW_REMARK || '自动化复核通过';

test.describe('预到龄通知流程 - 复核批量通过', () => {
  test.skip(!runRealE2E, '默认仅提供真实流程骨架，显式设置 RUN_REAL_E2E=true 后再执行');

  test('NOTICE-FLOW-07', async ({ page }) => {
    await loginWithRealUser(page, reviewerUser, reviewerPassword);
    await openPath(page, '/benefit/review', 'benefit-review-page');

    await fillByTestId(page, 'review-query-batch-no', expectedBatchKeyword);
    await clickByTestId(page, 'review-query-search');
    await expect(byTestId(page, 'review-table')).toContainText(expectedBatchKeyword);

    await clickFirstByPrefix(page, 'review-view-');
    await expect(byTestId(page, 'review-detail-dialog')).toBeVisible();
    await page.keyboard.press('Escape');

    const firstRowCheckbox = page.locator('.el-table__body-wrapper tbody .el-checkbox').first();
    await expect(firstRowCheckbox).toBeVisible();
    await firstRowCheckbox.click();

    await clickByTestId(page, 'review-batch-approve');
    await promptDialog(page, reviewRemark);
    await expectMessage(page, /批量审核通过成功|操作成功/);
  });
});
