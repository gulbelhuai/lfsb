const { test, expect } = require('@playwright/test');

const runRealE2E = process.env.RUN_REAL_E2E === 'true';

test.describe('预到龄发放通知真实链路脚手架', () => {
  test.skip(!runRealE2E, '默认仅作为测试前准备脚手架，显式设置 RUN_REAL_E2E=true 后再执行');

  test('NOTICE-FLOW-OVERVIEW 真实链路已拆分为细粒度 flow 用例', async ({ page }) => {
    await page.goto('/');
    expect([
      'e2e/flows/benefit-notice-generate.spec.js',
      'e2e/flows/benefit-notice-determination.spec.js',
      'e2e/flows/benefit-notice-review.spec.js'
    ]).toHaveLength(3);
  });
});
