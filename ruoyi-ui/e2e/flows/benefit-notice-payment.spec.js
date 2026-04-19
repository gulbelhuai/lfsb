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
const paymentMonth = process.env.PLAYWRIGHT_PAYMENT_MONTH || '2026-04';

test.describe('预到龄通知流程 - 进入支付计划并按条件生成', () => {
  test.skip(!runRealE2E, '默认仅提供真实流程骨架，显式设置 RUN_REAL_E2E=true 后再执行');

  test('NOTICE-FLOW-08', async ({ page }) => {
    await loginWithRealUser(page, reviewerUser, reviewerPassword);
    await openPath(page, '/benefit/review', 'benefit-review-page');

    await clickByTestId(page, 'review-go-payment-plan');
    await expect(page).toHaveURL(/\/payment\/plan/);
    await expect(byTestId(page, 'payment-plan-page')).toBeVisible();

    await fillByTestId(page, 'payment-plan-subsidy-type', '1');
    await fillByTestId(page, 'payment-plan-payment-month', paymentMonth);
    await clickByTestId(page, 'payment-plan-preview');
    await expect(byTestId(page, 'payment-plan-statistics-dialog')).toBeVisible();
    await page.getByRole('button', { name: '确认生成支付计划' }).click();
    await confirmDialog(page);
    await expectMessage(page, /成功|已存在|没有可生成/);
  });
});
