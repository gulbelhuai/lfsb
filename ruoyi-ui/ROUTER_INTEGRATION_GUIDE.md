# 前端路由集成指南

## 📋 说明

本文档说明如何将社保管理系统的路由集成到若依框架的前端项目中。

---

## 🔧 集成步骤

### 1. 导入路由配置文件

将 `src/router/shebao.js` 中的路由配置添加到若依框架的路由系统。

#### 方法A: 静态路由（开发测试用）

编辑 `src/router/index.js`，在 `constantRoutes` 中添加：

```javascript
import Layout from '@/layout'

export const constantRoutes = [
  // ... 其他路由 ...

  // 社保管理系统路由
  {
    path: '/shebao',
    component: Layout,
    redirect: '/shebao/person',
    name: 'Shebao',
    meta: { title: '社保管理', icon: 'peoples' },
    children: [
      // 人员信息管理
      {
        path: 'person/landloss',
        component: () => import('@/views/shebao/person/landloss/index'),
        name: 'LandLossRegistration',
        meta: { title: '失地居民登记', icon: 'form' }
      },
      {
        path: 'person/expropriatee',
        component: () => import('@/views/shebao/person/expropriatee/index'),
        name: 'ExpropriateeRegistration',
        meta: { title: '被征地居民登记', icon: 'form' }
      },
      // ... 其他路由 ...
    ]
  }
]
```

#### 方法B: 动态路由（推荐生产环境）

若依框架默认使用**动态路由**，路由信息从后端 `sys_menu` 表获取。

**无需修改前端路由文件**，只需确保：
1. ✅ 后端 `sys_menu` 表已配置菜单（通过 `04_menu_config.sql` 完成）
2. ✅ 菜单的 `component` 字段指向正确的 Vue 文件路径
3. ✅ 用户角色已关联对应菜单（通过 `sys_role_menu` 完成）

---

## 📂 路由文件结构

```
ruoyi-ui/src/
├── router/
│   ├── index.js              # 主路由文件（若依框架）
│   └── shebao.js             # 社保路由配置（参考用）
├── views/
│   └── shebao/
│       ├── index.vue         # 社保模块首页
│       ├── person/           # 人员管理（7个页面）
│       ├── benefit/          # 待遇核定（3个页面）
│       ├── management/       # 待遇管理（3个页面）
│       ├── payment/          # 支付计划（4个页面）
│       ├── finance/          # 财务管理（4个页面）
│       └── audit/            # 审计管理（4个页面）
└── api/
    └── shebao/
        ├── person.js         # 人员管理API
        ├── benefit.js        # 待遇核定API
        ├── payment.js        # 支付计划API
        ├── finance.js        # 财务管理API
        └── approval.js       # 审批流程API
```

---

## 🗂️ 菜单配置（sys_menu）

后端菜单配置已通过 `sql/refactor/04_menu_config.sql` 完成。

### 菜单层级结构

```
社保管理 (根菜单)
├── 人员信息管理 (parent_id指向根菜单)
│   ├── 失地居民登记
│   ├── 被征地居民登记
│   ├── 拆迁居民登记
│   ├── 村干部登记
│   ├── 教师补贴登记
│   ├── 人员登记审核
│   └── 人员信息修改审核
├── 待遇核定管理
│   ├── 预到龄通知生成
│   ├── 待遇核定
│   └── 待遇核定复核
├── 待遇管理
│   ├── 发放信息修改
│   ├── 待遇暂停/恢复
│   └── 待遇认证
├── 支付计划管理
│   ├── 支付计划生成
│   ├── 支付计划复核
│   ├── 支付计划审批
│   └── 上传财务系统
├── 财务管理
│   ├── 批次管理
│   ├── 银行发放
│   ├── 失败处理
│   └── 财务账户管理
└── 审计管理
    ├── 操作日志
    ├── 审批历史
    ├── 发放明细
    └── 统计报表
```

### 菜单字段说明

| 字段 | 说明 | 示例 |
|------|------|------|
| menu_name | 菜单名称 | `失地居民登记` |
| parent_id | 父菜单ID | `100`（指向"人员信息管理"） |
| path | 路由路径 | `person/landloss` |
| component | Vue组件路径 | `shebao/person/landloss/index` |
| perms | 权限标识 | `shebao:person:add` |
| menu_type | 菜单类型 | `C`（菜单）、`F`（按钮） |
| visible | 是否可见 | `0`（显示）、`1`（隐藏） |

---

## 🔐 权限控制

### 1. 角色权限配置

已通过 `04_menu_config.sql` 完成6个角色的菜单权限配置：

| 角色 | 角色标识 | 可见菜单 |
|------|----------|----------|
| 经办人 | `jingbanren` | 人员登记、待遇核定申请 |
| 复核人 | `fuheren` | 人员登记复核、待遇核定复核、支付计划复核 |
| 审批人 | `shenpiren` | 审批、上传财务系统 |
| 待遇管理员 | `daiyuguanliyuan` | 待遇管理全部功能 |
| 财务操作员 | `caiwucaozuoyuan` | 财务管理全部功能 |
| 审计查询员 | `shenjichaxunyuan` | 审计管理全部功能 |

### 2. 前端权限判断

#### 2.1 路由级别权限（自动）
若依框架会根据用户角色自动过滤菜单和路由。

#### 2.2 按钮级别权限（手动）
在Vue组件中使用 `v-hasPermi` 指令：

```vue
<template>
  <!-- 只有拥有 shebao:person:add 权限的用户才能看到此按钮 -->
  <el-button
    v-hasPermi="['shebao:person:add']"
    type="primary"
    @click="handleAdd">
    新增
  </el-button>

  <!-- 只有拥有 shebao:person:edit 权限的用户才能看到此按钮 -->
  <el-button
    v-hasPermi="['shebao:person:edit']"
    type="text"
    @click="handleEdit(row)">
    修改
  </el-button>

  <!-- 只有拥有 shebao:person:remove 权限的用户才能看到此按钮 -->
  <el-button
    v-hasPermi="['shebao:person:remove']"
    type="text"
    @click="handleDelete(row)">
    删除
  </el-button>
</template>
```

#### 2.3 JS代码权限判断

```javascript
export default {
  methods: {
    handleOperation() {
      // 检查是否有权限
      if (this.checkPermi(['shebao:person:add'])) {
        // 执行操作
      } else {
        this.$modal.msgError('无权限执行此操作')
      }
    }
  }
}
```

---

## 🎨 组件引入

### 公共组件

```javascript
// 引入审批状态组件
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'

// 引入审批历史组件
import ApprovalHistory from '@/components/Shebao/ApprovalHistory'

export default {
  components: {
    ApprovalStatus,
    ApprovalHistory
  }
}
```

### API引入

```javascript
// 引入人员管理API
import {
  listPerson,
  getPerson,
  addPerson,
  updatePerson,
  deletePerson
} from '@/api/shebao/person'

// 引入审批API
import {
  submitApproval,
  reviewApproval,
  approveApproval
} from '@/api/shebao/approval'
```

---

## 📦 字典数据配置

### 使用字典数据

在Vue组件中使用 `dicts` 选项加载字典：

```vue
<script>
export default {
  name: 'PersonList',
  dicts: [
    'shebao_subsidy_type',      // 补贴类型
    'shebao_approval_status',   // 审批状态
    'shebao_payment_status'     // 发放状态
  ],
  data() {
    return {
      // ...
    }
  }
}
</script>
```

在模板中使用：

```vue
<template>
  <!-- 字典标签 -->
  <dict-tag :options="dict.type.shebao_approval_status" :value="row.approvalStatus"/>

  <!-- 字典下拉框 -->
  <el-select v-model="form.subsidyType">
    <el-option
      v-for="dict in dict.type.shebao_subsidy_type"
      :key="dict.value"
      :label="dict.label"
      :value="dict.value"
    />
  </el-select>
</template>
```

### 字典类型列表

已通过 `03_init_data.sql` 初始化的字典：

| 字典类型 | 说明 |
|----------|------|
| `shebao_subsidy_type` | 补贴类型 |
| `shebao_approval_status` | 审批状态 |
| `shebao_payment_status` | 发放状态 |
| `shebao_business_type` | 业务类型 |
| `shebao_operation_type` | 操作类型 |
| `shebao_certification_status` | 认证状态 |
| `shebao_suspension_status` | 暂停状态 |

---

## 🧪 路由测试

### 1. 启动前端项目

```bash
cd ruoyi-ui
npm install
npm run dev
```

### 2. 登录系统

访问: http://localhost:80

使用测试账号登录（参考 `PROJECT_DELIVERY.md` 中的测试账号清单）

### 3. 验证路由

登录后检查：
- ✅ 左侧菜单是否显示"社保管理"及子菜单
- ✅ 点击菜单是否能正确跳转
- ✅ 不同角色登录看到的菜单是否符合权限配置

---

## ⚠️ 常见问题

### 问题1: 菜单不显示
**原因**:
- 用户角色未关联菜单
- 菜单的 `visible` 字段为 `1`（隐藏）

**解决**:
- 检查 `sys_role_menu` 表
- 检查 `sys_menu` 表的 `visible` 字段

### 问题2: 页面404
**原因**:
- `component` 字段路径错误
- Vue文件不存在

**解决**:
- 检查 `sys_menu` 表的 `component` 字段
- 确认 Vue 文件路径正确

### 问题3: 权限按钮不显示
**原因**:
- 未配置按钮权限
- `perms` 字段为空

**解决**:
- 在 `sys_menu` 表中添加按钮类型菜单（`menu_type='F'`）
- 设置 `perms` 权限标识

---

## 📚 相关文档

- **菜单配置SQL**: `sql/refactor/04_menu_config.sql`
- **前端开发指南**: `sql/refactor/FRONTEND_GUIDE.md`
- **项目交付文档**: `PROJECT_DELIVERY.md`
- **系统启动指南**: `START_GUIDE.md`

---

## 🎉 完成检查清单

- [ ] 路由文件已创建 (`src/router/shebao.js`)
- [ ] 首页已创建 (`src/views/shebao/index.vue`)
- [ ] 25个业务页面已创建
- [ ] 2个公共组件已创建
- [ ] 7个API服务文件已创建
- [ ] 数据库菜单已配置（`04_menu_config.sql`）
- [ ] 角色权限已配置（`04_menu_config.sql`）
- [ ] 测试账号已创建（`03_init_data.sql`）

**所有路由配置和页面开发已完成！** ✅
