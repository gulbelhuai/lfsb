const path = require('path');
const { test, expect } = require('@playwright/test');
const {
  byTestId,
  clickByTestId,
  clickFirstByPrefix,
  expectMessage,
  fillByTestId,
  loginWithRealUser,
  openPath,
  setUploadFileByTestId
} = require('../support/benefit-notice');

const runRealE2E = process.env.RUN_REAL_E2E === 'true';
const operatorUser = process.env.PLAYWRIGHT_OPERATOR_USER || 'operator01';
const operatorPassword = process.env.PLAYWRIGHT_OPERATOR_PASSWORD || 'Test@12345';
const expectedBatchKeyword = process.env.PLAYWRIGHT_EXPECT_BATCH_KEYWORD || 'NT';
const targetIdCard = process.env.PLAYWRIGHT_NOTICE_ID_CARD || '130101197005050011';
const targetName = process.env.PLAYWRIGHT_NOTICE_NAME || '闭环正样本甲';
const zipPath = process.env.PLAYWRIGHT_NOTICE_ZIP_PATH || path.resolve(__dirname, '../../../tests/tmp/notice-flow/1000048.zip');

test.describe('预到龄通知流程 - 核定录入与材料上传', () => {
  test.skip(!runRealE2E, '默认仅提供真实流程骨架，显式设置 RUN_REAL_E2E=true 后再执行');

  test('NOTICE-FLOW-04 NOTICE-FLOW-05 NOTICE-FLOW-06', async ({ page }) => {
    await loginWithRealUser(page, operatorUser, operatorPassword);
    await openPath(page, '/benefit/determination', 'benefit-determination-page');

    await fillByTestId(page, 'determination-query-batch-no', expectedBatchKeyword);
    await clickByTestId(page, 'determination-query-search');
    await expect(byTestId(page, 'determination-table')).toContainText(expectedBatchKeyword);

    await clickFirstByPrefix(page, 'determination-edit-');
    await expect(byTestId(page, 'determination-edit-dialog')).toBeVisible();

    await fillByTestId(page, 'determination-form-id-card', targetIdCard);
    await fillByTestId(page, 'determination-form-bank-name', '中国银行');
    await fillByTestId(page, 'determination-form-bank-account', '6222000000000001');
    await fillByTestId(page, 'determination-form-bank-account-name', targetName);

    await setUploadFileByTestId(page, 'determination-attachment-upload', zipPath);
    await clickByTestId(page, 'determination-attachment-submit');
    await expectMessage(page, /上传成功|操作成功/);

    await clickByTestId(page, 'determination-form-save');
    await expectMessage(page, /保存成功|操作成功/);

    await clickFirstByPrefix(page, 'determination-submit-');
    await page.getByRole('button', { name: '确 定' }).click();
    await expectMessage(page, /提交成功|操作成功/);
  });
});
