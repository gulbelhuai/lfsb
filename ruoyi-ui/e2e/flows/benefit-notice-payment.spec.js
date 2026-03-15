const { test, expect } = require('@playwright/test');
const {
  byTestId,
  clickByTestId,
  confirmDialog,
  expectMessage,
  fillByTestId,
  loginWithRealUser,
  openPath
} = require('../support/benefit-notice');

const runRealE2E = process.env.RUN_REAL_E2E === 'true';
const reviewerUser = process.env.PLAYWRIGHT_REVIEWER_USER || 'reviewer01';
const reviewerPassword = process.env.PLAYWRIGHT_REVIEWER_PASSWORD || 'Test@12345';
const noticeBatchNo = process.env.PLAYWRIGHT_NOTICE_BATCH_NO || 'NT';

test.describe('预到龄通知流程 - 进入支付计划与按批次生成', () => {
  test.skip(!runRealE2E, '默认仅提供真实流程骨架，显式设置 RUN_REAL_E2E=true 后再执行');

  test('NOTICE-FLOW-08', async ({ page }) => {
    await loginWithRealUser(page, reviewerUser, reviewerPassword);
    await openPath(page, '/benefit/review', 'benefit-review-page');

    await fillByTestId(page, 'review-query-batch-no', noticeBatchNo);
    await clickByTestId(page, 'review-query-search');
    await expect(byTestId(page, 'review-table')).toContainText(noticeBatchNo);

    await clickByTestId(page, 'review-go-payment-plan');
    await expect(page).toHaveURL(/\/payment\/plan/);
    await expect(byTestId(page, 'payment-plan-page')).toBeVisible();

    await fillByTestId(page, 'payment-plan-notice-batch-no', noticeBatchNo);
    await clickByTestId(page, 'payment-plan-generate-by-notice-batch');
    await confirmDialog(page);
    await expectMessage(page, /成功|已存在|没有可生成/);
  });
});
