const { test, expect } = require('@playwright/test');

const captchaResponse = {
  code: 200,
  msg: '操作成功',
  captchaEnabled: true,
  img: 'R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==',
  uuid: 'test-uuid'
};

function buildLoginSuccessResponse(username) {
  return {
    code: 200,
    msg: '操作成功',
    token: `mock-token-${username}`
  };
}

function buildGetInfoResponse(username, roleKey) {
  return {
    code: 200,
    msg: '操作成功',
    user: {
      userId: 1,
      userName: username,
      nickName: username,
      avatar: ''
    },
    roles: [roleKey],
    permissions: ['*:*:*'],
    isDefaultModifyPwd: false,
    isPasswordExpired: false
  };
}

function buildRoutersResponse(routes) {
  return {
    code: 200,
    msg: '操作成功',
    data: routes
  };
}

function menuGroup(path, title, children) {
  return {
    name: title,
    path,
    component: 'Layout',
    alwaysShow: true,
    meta: { title, icon: 'dashboard' },
    children
  };
}

function menuItem(path, title, component) {
  return {
    name: title,
    path,
    component,
    meta: { title, icon: 'form' }
  };
}

async function mockLoginSuccess(page, username, roleKey, routes) {
  await page.route('**/dev-api/captchaImage', route => {
    route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(captchaResponse) });
  });
  await page.route('**/dev-api/login', route => {
    route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(buildLoginSuccessResponse(username)) });
  });
  await page.route('**/dev-api/getInfo', route => {
    route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(buildGetInfoResponse(username, roleKey)) });
  });
  await page.route('**/dev-api/getRouters', route => {
    route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(buildRoutersResponse(routes)) });
  });
}

const roleCases = [
  {
    name: '超级管理员',
    username: 'admin',
    password: 'gofjoo-zyrboH-1',
    roleKey: 'admin',
    visible: ['系统管理', '用户管理', '角色管理'],
    hidden: ['失败处理'],
    routes: [
      menuGroup('/system', '系统管理', [
        menuItem('user', '用户管理', 'system/user/index'),
        menuItem('role', '角色管理', 'system/role/index')
      ])
    ]
  },
  {
    name: '经办人',
    username: 'operator01',
    password: 'Test@12345',
    roleKey: 'operator',
    visible: ['基础数据', '人员信息管理', '待遇核定管理', '补贴发放'],
    hidden: ['财务管理', '统计管理'],
    routes: [
      menuGroup('/shebao/base', '基础数据', [
        menuItem('street', '街道办管理', 'shebao/streetOffice/index')
      ]),
      menuGroup('/shebao/person', '人员信息管理', [
        menuItem('landloss', '失地居民登记', 'shebao/person/landloss/index')
      ]),
      menuGroup('/shebao/benefit', '待遇核定管理', [
        menuItem('determination', '待遇核定', 'shebao/benefit/determination/index')
      ]),
      menuGroup('/shebao/distribution', '补贴发放', [
        menuItem('distribution', '补贴发放记录', 'shebao/subsidyDistribution/index')
      ])
    ]
  },
  {
    name: '复核人',
    username: 'reviewer01',
    password: 'Test@12345',
    roleKey: 'reviewer',
    visible: ['基础数据', '人员信息管理', '待遇核定管理', '支付结算'],
    hidden: ['财务管理', '统计管理'],
    routes: [
      menuGroup('/shebao/base', '基础数据', [
        menuItem('street', '街道办管理', 'shebao/streetOffice/index')
      ]),
      menuGroup('/shebao/person', '人员信息管理', [
        menuItem('review', '人员登记复核', 'shebao/person/review/index')
      ]),
      menuGroup('/shebao/benefit', '待遇核定管理', [
        menuItem('review', '核定审核', 'shebao/benefit/review/index')
      ]),
      menuGroup('/shebao/payment', '支付结算', [
        menuItem('paymentReview', '支付计划复核', 'shebao/payment/review/index')
      ])
    ]
  },
  {
    name: '审批人',
    username: 'approver01',
    password: 'Test@12345',
    roleKey: 'approver',
    visible: ['待遇管理', '支付结算'],
    hidden: ['财务管理', '统计管理', '系统管理'],
    routes: [
      menuGroup('/shebao/management', '待遇管理', [
        menuItem('suspension', '待遇暂停恢复', 'shebao/management/suspension/index')
      ]),
      menuGroup('/shebao/payment', '支付结算', [
        menuItem('approve', '支付计划审批', 'shebao/payment/approve/index')
      ])
    ]
  },
  {
    name: '财务人员',
    username: 'finance01',
    password: 'Test@12345',
    roleKey: 'finance',
    visible: ['补贴发放', '财务管理'],
    hidden: ['统计管理', '系统管理'],
    routes: [
      menuGroup('/shebao/distribution', '补贴发放', [
        menuItem('bank', '银行发放', 'shebao/finance/bank/index')
      ]),
      menuGroup('/shebao/finance', '财务管理', [
        menuItem('batch', '财务批次管理', 'shebao/finance/batch/index'),
        menuItem('failure', '失败处理', 'shebao/finance/failure/index')
      ])
    ]
  },
  {
    name: '审计员',
    username: 'auditor01',
    password: 'Test@12345',
    roleKey: 'auditor',
    visible: ['补贴发放', '统计管理'],
    hidden: ['财务管理', '系统管理'],
    routes: [
      menuGroup('/shebao/distribution', '补贴发放', [
        menuItem('detail', '发放明细', 'shebao/audit/detail/index')
      ]),
      menuGroup('/shebao/audit', '统计管理', [
        menuItem('statistics', '统计汇总', 'shebao/audit/statistics/index'),
        menuItem('approval', '审批记录', 'shebao/audit/approval/index')
      ])
    ]
  }
];

for (const roleCase of roleCases) {
  test(`角色菜单验收：${roleCase.name}`, async ({ page }) => {
    await mockLoginSuccess(page, roleCase.username, roleCase.roleKey, roleCase.routes);

    await page.goto('/login');
    await page.locator('input[placeholder="账号"]').fill(roleCase.username);
    await page.locator('input[placeholder="密码"]').fill(roleCase.password);
    await page.locator('input[placeholder="验证码"]').fill('1234');
    await page.getByRole('button', { name: '登 录' }).click();

    await expect(page).toHaveURL(/\/($|index)/);
    for (const text of roleCase.visible) {
      await expect(page.locator('.sidebar-container')).toContainText(text);
    }
    for (const text of roleCase.hidden) {
      await expect(page.locator('.sidebar-container')).not.toContainText(text);
    }

    const cookies = await page.context().cookies();
    expect(cookies).toEqual(
      expect.arrayContaining([expect.objectContaining({ name: 'Admin-Token', value: `mock-token-${roleCase.username}` })])
    );
  });
}

test('模拟登录成功后应进入首页', async ({ page }) => {
  await mockLoginSuccess(page, 'admin', 'admin', []);

  await page.goto('/login');
  await page.locator('input[placeholder="账号"]').fill('admin');
  await page.locator('input[placeholder="密码"]').fill('gofjoo-zyrboH-1');
  await page.locator('input[placeholder="验证码"]').fill('1234');
  await page.getByRole('button', { name: '登 录' }).click();

  await expect(page).toHaveURL(/\/($|index)/);
  await expect(page.locator('.sidebar-container')).toContainText('首页');
  const cookies = await page.context().cookies();
  expect(cookies).toEqual(
    expect.arrayContaining([expect.objectContaining({ name: 'Admin-Token', value: 'mock-token-admin' })])
  );
});

test('模拟登录失败时应停留在登录页', async ({ page }) => {
  await page.route('**/dev-api/captchaImage', route => {
    route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(captchaResponse) });
  });
  await page.route('**/dev-api/login', route => {
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ code: 500, msg: '账号或密码错误' })
    });
  });

  await page.goto('/login');
  await page.locator('input[placeholder="账号"]').fill('admin');
  await page.locator('input[placeholder="密码"]').fill('wrong-password');
  await page.locator('input[placeholder="验证码"]').fill('1234');
  await page.getByRole('button', { name: '登 录' }).click();

  await expect(page).toHaveURL(/\/login/);
  await expect(page.getByText('账号或密码错误')).toBeVisible();
});
