const fs = require('fs/promises');
const path = require('path');
const { test, expect } = require('@playwright/test');
const { loginWithRealUser } = require('../support/benefit-notice');

const operatorUser = process.env.PLAYWRIGHT_OPERATOR_USER || 'operator01';
const operatorPassword = process.env.PLAYWRIGHT_OPERATOR_PASSWORD || 'Test@12345';
const reviewerUser = process.env.PLAYWRIGHT_REVIEWER_USER || 'reviewer01';
const reviewerPassword = process.env.PLAYWRIGHT_REVIEWER_PASSWORD || 'Test@12345';
const reviewRemark = process.env.PLAYWRIGHT_REVIEW_REMARK || 'UI造数复核通过';

function stamp() {
  const now = new Date();
  const pad = (v) => String(v).padStart(2, '0');
  return `${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`;
}

function reportPath(batchId) {
  return path.resolve(__dirname, '../../../tests/reports/benefit-notice', `UI-SAMPLE-PREP-${batchId}.json`);
}

async function writeReport(batchId, payload) {
  const target = reportPath(batchId);
  await fs.mkdir(path.dirname(target), { recursive: true });
  await fs.writeFile(target, JSON.stringify(payload, null, 2), 'utf8');
  return target;
}

async function waitForMessage(page) {
  const message = page.locator('.el-message').last();
  await expect(message).toBeVisible({ timeout: 15000 });
  return (await message.textContent()) || '';
}

async function selectFirstDropdownOption(page) {
  const option = page.locator('.el-select-dropdown:visible .el-select-dropdown__item:not(.is-disabled)').first();
  await expect(option).toBeVisible({ timeout: 10000 });
  const text = (await option.textContent())?.trim();
  await option.click();
  return text;
}

async function fillFormInputByLabel(container, label, value) {
  const formItem = container.locator('.el-form-item').filter({
    has: container.locator('.el-form-item__label', { hasText: label })
  }).first();
  const input = formItem.locator('input, textarea').first();
  await expect(input).toBeVisible({ timeout: 10000 });
  await input.click();
  await input.fill(String(value));
}

async function fillInputByPlaceholder(container, placeholder, value) {
  const input = container.locator(`input[placeholder="${placeholder}"], textarea[placeholder="${placeholder}"]`).first();
  await expect(input).toBeVisible({ timeout: 10000 });
  await input.click();
  await input.fill(String(value));
}

async function fillDateByPlaceholder(page, container, placeholder, value) {
  const input = container.locator(`input[placeholder="${placeholder}"]`).first();
  await expect(input).toBeVisible({ timeout: 10000 });
  await input.click();
  await input.fill(String(value));
  await input.press('Enter');
  await page.keyboard.press('Escape');
}

async function selectFormOptionByLabel(page, container, label) {
  const formItem = container.locator('.el-form-item').filter({
    has: container.locator('.el-form-item__label', { hasText: label })
  }).first();
  const input = formItem.locator('.el-input__inner').first();
  await expect(input).toBeVisible({ timeout: 10000 });
  await input.click();
  return selectFirstDropdownOption(page);
}

async function selectByPlaceholder(page, container, placeholder) {
  const input = container.locator(`input[placeholder="${placeholder}"]`).first();
  await expect(input).toBeVisible({ timeout: 10000 });
  await input.click();
  return selectFirstDropdownOption(page);
}

async function chooseRadioByLabel(container, label, text) {
  const formItem = container.locator('.el-form-item').filter({
    has: container.locator('.el-form-item__label', { hasText: label })
  }).first();
  const radio = formItem.locator('.el-radio').filter({ hasText: text }).first();
  await expect(radio).toBeVisible({ timeout: 10000 });
  await radio.click();
}

async function clickRadioText(container, text, index = 0) {
  const radio = container.locator('.el-radio').filter({ hasText: text }).nth(index);
  await expect(radio).toBeVisible({ timeout: 10000 });
  await radio.click();
}

async function openVisibleDialog(page) {
  const dialog = page.locator('.el-dialog__wrapper:visible').last();
  await expect(dialog).toBeVisible({ timeout: 10000 });
  return dialog;
}

async function searchByName(page, name) {
  const input = page.locator('form').first().locator('input[placeholder*="姓名"]').first();
  await expect(input).toBeVisible({ timeout: 10000 });
  await input.fill(name);
  await page.getByRole('button', { name: '搜索' }).first().click();
}

async function createLandLossSample(page, sample) {
  await page.goto('/person/lost-land');
  await expect(page.getByRole('button', { name: '新增' })).toBeVisible();
  await page.getByRole('button', { name: '新增' }).click();
  const dialog = await openVisibleDialog(page);
  await fillInputByPlaceholder(dialog, '请输入18位身份证号', sample.idCardNo);
  await fillInputByPlaceholder(dialog, '请输入姓名', sample.name);
  await clickRadioText(dialog, '男');
  await fillDateByPlaceholder(page, dialog, '选择生日', sample.birthday);
  await selectByPlaceholder(page, dialog, '请选择街道办');
  await selectByPlaceholder(page, dialog, '请选择村委会');
  await fillInputByPlaceholder(dialog, '请输入户籍所在地', sample.householdRegistration);
  await fillInputByPlaceholder(dialog, '请输入家庭住址', sample.homeAddress);
  await clickRadioText(dialog, '是', 0);
  await fillInputByPlaceholder(dialog, '请输入联系电话', sample.phone);
  await clickRadioText(dialog, '是', 1);
  await fillDateByPlaceholder(page, dialog, '选择征地时间', '2024-03-01');
  await fillDateByPlaceholder(page, dialog, '选择完成补偿时间', '2024-03-15');
  await fillDateByPlaceholder(page, dialog, '选择认定时间', '2024-03-20');
  await fillInputByPlaceholder(dialog, '请输入备注', 'UI预到龄样本-失地');
  await dialog.getByRole('button', { name: '确 定' }).click();
  const message = await waitForMessage(page);
  await expect(page.locator('.el-dialog__wrapper:visible')).toHaveCount(0);
  await searchByName(page, sample.name);
  await expect(page.locator('.el-table')).toContainText(sample.name);
  return { message };
}

async function createDemolitionSample(page, sample) {
  await page.goto('/person/demolition');
  await expect(page.getByRole('button', { name: '新增' })).toBeVisible();
  await page.getByRole('button', { name: '新增' }).click();
  const dialog = await openVisibleDialog(page);
  await fillInputByPlaceholder(dialog, '请输入18位身份证号', sample.idCardNo);
  await dialog.locator('input').nth(0).blur();
  await fillInputByPlaceholder(dialog, '请输入姓名', sample.name);
  await clickRadioText(dialog, '男');
  await fillDateByPlaceholder(page, dialog, '选择生日', sample.birthday);
  await selectByPlaceholder(page, dialog, '请选择街道办');
  await selectByPlaceholder(page, dialog, '请选择村委会');
  await fillInputByPlaceholder(dialog, '请输入户籍所在地', sample.householdRegistration);
  await fillInputByPlaceholder(dialog, '请输入家庭住址', sample.homeAddress);
  await clickRadioText(dialog, '是', 0);
  await fillInputByPlaceholder(dialog, '请输入联系电话', sample.phone);
  await clickRadioText(dialog, '是', 1);
  await fillInputByPlaceholder(dialog, '请输入拆迁事由', 'UI预到龄测试拆迁');
  await fillDateByPlaceholder(page, dialog, '选择拆迁时间', '2024-06-18');
  await fillDateByPlaceholder(page, dialog, '选择认定时间', '2024-06-20');
  await fillInputByPlaceholder(dialog, '请输入备注', 'UI预到龄样本-拆迁');
  await dialog.getByRole('button', { name: '确 定' }).click();
  const message = await waitForMessage(page);
  await expect(page.locator('.el-dialog__wrapper:visible')).toHaveCount(0);
  await searchByName(page, sample.name);
  await expect(page.locator('.el-table')).toContainText(sample.name);
  return { message };
}

async function createVillageOfficialSample(page, sample) {
  await page.goto('/person/village-official');
  await expect(page.getByRole('button', { name: '新增' })).toBeVisible();
  await page.getByRole('button', { name: '新增' }).click();
  const dialog = await openVisibleDialog(page);
  await fillInputByPlaceholder(dialog, '请输入18位身份证号', sample.idCardNo);
  await dialog.locator('input').nth(0).blur();
  await fillInputByPlaceholder(dialog, '请输入姓名', sample.name);
  await clickRadioText(dialog, '男');
  await fillDateByPlaceholder(page, dialog, '选择生日', sample.birthday);
  await fillInputByPlaceholder(dialog, '请输入籍贯', sample.householdRegistration);
  await fillInputByPlaceholder(dialog, '请输入联系电话', sample.phone);
  await fillInputByPlaceholder(dialog, '请输入家庭住址', sample.homeAddress);
  await selectByPlaceholder(page, dialog, '请选择所属村委会');
  await fillInputByPlaceholder(dialog, '请输入户别编号', `HB-${sample.idCardNo.slice(-4)}`);
  await clickRadioText(dialog, '未领取');
  await fillFormInputByLabel(dialog, '累计任职年限', '8');
  await clickRadioText(dialog, '否');
  await clickRadioText(dialog, '正常');
  await dialog.getByRole('button', { name: '添加任职' }).click();
  const row = dialog.locator('.el-table__body-wrapper tbody tr').first();
  await row.locator('.el-select .el-input__inner').first().click();
  await selectFirstDropdownOption(page);
  const dateInputs = row.locator('input');
  await dateInputs.nth(0).fill('2015-01-01');
  await dateInputs.nth(1).fill('2029-12-31');
  await row.locator('.el-select .el-input__inner').nth(1).click();
  await selectFirstDropdownOption(page);
  await row.locator('input[placeholder="请输入备注"]').fill('UI预到龄任职信息');
  await dialog.getByRole('button', { name: '确 定' }).click();
  const message = await waitForMessage(page);
  await expect(page.locator('.el-dialog__wrapper:visible')).toHaveCount(0);
  await searchByName(page, sample.name);
  await expect(page.locator('.el-table')).toContainText(sample.name);
  return { message };
}

async function approveSampleInReview(page, sample) {
  await page.goto('/person/review');
  await searchByName(page, sample.name);
  const subsidyTypeInput = page.locator('form').first().locator('.el-form-item').filter({
    has: page.locator('.el-form-item__label', { hasText: '补贴类型' })
  }).first().locator('.el-input__inner').first();
  await subsidyTypeInput.click();
  const desired = page.locator('.el-select-dropdown:visible .el-select-dropdown__item').filter({ hasText: sample.reviewTypeLabel }).first();
  await expect(desired).toBeVisible({ timeout: 10000 });
  await desired.click();
  await page.getByRole('button', { name: '搜索' }).first().click();
  const table = page.locator('.el-table');
  await expect(table).toContainText(sample.name, { timeout: 15000 });
  await table.getByRole('button', { name: '通过' }).first().click();
  const dialog = await openVisibleDialog(page);
  await fillFormInputByLabel(dialog, '复核意见', reviewRemark);
  await dialog.getByRole('button', { name: '确 定' }).click();
  const message = await waitForMessage(page);
  return { message };
}

test.describe('预到龄专项前置 UI 造数', () => {
  test.setTimeout(8 * 60 * 1000);

  test('通过UI准备失地/拆迁/村干部并复核通过', async ({ page }) => {
    const batchId = stamp();
    const samples = [
      {
        key: 'landloss',
        name: `UI预到龄失地A-${batchId}`,
        idCardNo: '130101197006050061',
        birthday: '1970-06-05',
        phone: '13910010001',
        householdRegistration: '廊坊市测试街道失地样本',
        homeAddress: '廊坊市测试街道失地样本1号',
        reviewTypeLabel: '失地居民'
      },
      {
        key: 'demolition',
        name: `UI预到龄拆迁A-${batchId}`,
        idCardNo: '130101197006180077',
        birthday: '1970-06-18',
        phone: '13910010002',
        householdRegistration: '廊坊市测试街道拆迁样本',
        homeAddress: '廊坊市测试街道拆迁样本2号',
        reviewTypeLabel: '拆迁居民'
      },
      {
        key: 'official',
        name: `UI预到龄村干A-${batchId}`,
        idCardNo: '13010119700625008X',
        birthday: '1970-06-25',
        phone: '13910010003',
        householdRegistration: '廊坊市测试街道村干样本',
        homeAddress: '廊坊市测试街道村干样本3号',
        reviewTypeLabel: '村干部'
      }
    ];

    const result = {
      batchId,
      generatedAt: new Date().toISOString(),
      samples
    };

    try {
      await loginWithRealUser(page, operatorUser, operatorPassword);
      result.landloss = await createLandLossSample(page, samples[0]);
      result.demolition = await createDemolitionSample(page, samples[1]);
      result.official = await createVillageOfficialSample(page, samples[2]);
      await loginWithRealUser(page, reviewerUser, reviewerPassword);
      result.review = {};
      for (const sample of samples) {
        result.review[sample.key] = await approveSampleInReview(page, sample);
      }
      result.status = 'success';
    } catch (error) {
      result.status = 'failed';
      result.error = error.stack || error.message;
      throw error;
    } finally {
      result.reportPath = await writeReport(batchId, result);
      console.log(JSON.stringify(result, null, 2));
    }
  });
});
