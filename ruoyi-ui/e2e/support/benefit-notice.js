const { expect } = require('@playwright/test');
const apiBaseUrl = process.env.PLAYWRIGHT_API_BASE_URL || 'http://localhost:8087/api';
const appBaseUrl = process.env.PLAYWRIGHT_BASE_URL || 'http://127.0.0.1:81';

function byTestId(page, testId) {
  return page.locator(`[data-testid="${testId}"]`);
}

function firstByPrefix(page, prefix) {
  return page.locator(`[data-testid^="${prefix}"]`).first();
}

async function fillByTestId(page, testId, value) {
  const root = byTestId(page, testId);
  const input = root.locator('input, textarea').first();
  await expect(input).toBeVisible();
  await input.click();
  await input.fill(String(value));
}

async function clickByTestId(page, testId) {
  const target = byTestId(page, testId);
  await expect(target).toBeVisible();
  await target.click();
}

async function clickFirstByPrefix(page, prefix) {
  const target = firstByPrefix(page, prefix);
  await expect(target).toBeVisible();
  await target.click();
}

async function expectMessage(page, pattern) {
  await expect(page.locator('.el-message')).toContainText(pattern);
}

async function confirmDialog(page) {
  await page.getByRole('button', { name: '确 定' }).click();
}

async function promptDialog(page, value) {
  const input = page.locator('.el-message-box__input input').first();
  await expect(input).toBeVisible();
  await input.fill(value);
  await page.getByRole('button', { name: '确 定' }).click();
}

async function setUploadFileByTestId(page, testId, filePath) {
  const input = byTestId(page, testId).locator('input[type="file"]').first();
  await input.setInputFiles(filePath);
}

async function loginWithRealUser(page, username, password) {
  const response = await page.request.post(`${apiBaseUrl}/login`, {
    data: { username, password }
  });
  const data = await response.json();
  expect(data.code).toBe(200);
  expect(data.token).toBeTruthy();

  const appUrl = new URL(appBaseUrl);
  await page.context().addCookies([
    {
      name: 'Admin-Token',
      value: data.token,
      domain: appUrl.hostname,
      path: '/',
      httpOnly: false,
      secure: false,
      sameSite: 'Lax'
    }
  ]);

  await page.goto('/');
  await expect(page).not.toHaveURL(/\/login/);
}

async function openPath(page, path, pageTestId) {
  await page.goto(path);
  await expect(byTestId(page, pageTestId)).toBeVisible();
}

module.exports = {
  byTestId,
  clickByTestId,
  clickFirstByPrefix,
  confirmDialog,
  expectMessage,
  fillByTestId,
  firstByPrefix,
  loginWithRealUser,
  openPath,
  promptDialog,
  setUploadFileByTestId
};
