const { test, expect } = require('@playwright/test');
const {
  byTestId,
  clickByTestId,
  clickFirstByPrefix,
  confirmDialog,
  expectMessage,
  fillByTestId,
  loginWithRealUser,
  openPath
} = require('../support/benefit-notice');

const runRealE2E = process.env.RUN_REAL_E2E === 'true';
const operatorUser = process.env.PLAYWRIGHT_OPERATOR_USER || 'operator01';
const operatorPassword = process.env.PLAYWRIGHT_OPERATOR_PASSWORD || 'Test@12345';
const noticeMonth = process.env.PLAYWRIGHT_NOTICE_MONTH || '2030-02';
const expectedBatchKeyword = process.env.PLAYWRIGHT_EXPECT_BATCH_KEYWORD || 'NT';

test.describe('预到龄通知流程 - 生成与跳转', () => {
  test.skip(!runRealE2E, '默认仅提供真实流程骨架，显式设置 RUN_REAL_E2E=true 后再执行');

  test('NOTICE-FLOW-01 NOTICE-FLOW-02 NOTICE-FLOW-03', async ({ page }) => {
    await loginWithRealUser(page, operatorUser, operatorPassword);
    await openPath(page, '/benefit/notice', 'benefit-notice-page');

    await fillByTestId(page, 'notice-generate-month', noticeMonth);
    await clickByTestId(page, 'notice-generate-submit');
    await confirmDialog(page);
    await expectMessage(page, /成功|已存在/);

    await fillByTestId(page, 'notice-query-month', noticeMonth);
    await clickByTestId(page, 'notice-query-search');
    await expect(byTestId(page, 'notice-batch-table')).toContainText(expectedBatchKeyword);

    await clickFirstByPrefix(page, 'notice-view-');
    await expect(byTestId(page, 'notice-detail-dialog')).toBeVisible();
    await expect(byTestId(page, 'notice-detail-table')).toContainText(/姓名|用户ID|身份证号/);

    await page.keyboard.press('Escape');
    await clickFirstByPrefix(page, 'notice-go-determination-');
    await expect(page).toHaveURL(/\/benefit\/determination/);
    await expect(byTestId(page, 'benefit-determination-page')).toBeVisible();
  });
});
