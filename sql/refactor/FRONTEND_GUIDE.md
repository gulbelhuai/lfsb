# 前端开发指南

**项目**: 廊坊社保管理系统
**前端框架**: Vue 2.x + Element UI
**日期**: 2026-01-19

---

## 📋 目录结构

```
ruoyi-ui/
├── src/
│   ├── api/                    # API接口
│   │   └── shebao/
│   │       ├── person.js       # 人员信息API
│   │       ├── benefit.js      # 待遇核定API
│   │       ├── payment.js      # 支付结算API
│   │       └── finance.js      # 财务管理API
│   ├── views/                  # 页面组件
│   │   └── shebao/
│   │       ├── person/         # 人员信息管理
│   │       ├── benefit/        # 待遇核定管理
│   │       ├── management/     # 待遇管理
│   │       ├── payment/        # 支付结算
│   │       ├── finance/        # 财务管理
│   │       └── audit/          # 审计报表
│   └── components/             # 公共组件
│       └── Shebao/
│           ├── ApprovalStatus.vue      # 审批状态组件
│           ├── ApprovalHistory.vue     # 审批历史组件
│           └── ApprovalButtons.vue     # 审批按钮组件
```

---

## 🔐 权限控制

### 1. 路由权限

在 `router/index.js` 中配置路由元信息：

```javascript
{
  path: '/shebao/person/landloss',
  component: () => import('@/views/shebao/person/landloss/index'),
  name: 'LandLoss',
  meta: {
    title: '失地居民登记',
    icon: 'peoples',
    permissions: ['shebao:person:landloss:list']
  }
}
```

### 2. 按钮权限

使用 `v-hasPermi` 指令控制按钮显示：

```vue
<template>
  <el-button
    v-hasPermi="['shebao:person:landloss:add']"
    type="primary"
    @click="handleAdd">
    新增
  </el-button>

  <el-button
    v-hasPermi="['shebao:person:landloss:edit']"
    type="success"
    @click="handleEdit">
    修改
  </el-button>

  <el-button
    v-hasPermi="['shebao:person:landloss:remove']"
    type="danger"
    @click="handleDelete">
    删除
  </el-button>
</template>
```

### 3. 角色权限

```javascript
// 在 permission.js 中配置权限守卫
router.beforeEach((to, from, next) => {
  const permissions = store.getters.permissions
  const roles = store.getters.roles

  if (to.meta.permissions) {
    const hasPermission = permissions.some(permission => {
      return to.meta.permissions.includes(permission)
    })

    if (hasPermission) {
      next()
    } else {
      next({ path: '/401' })
    }
  } else {
    next()
  }
})
```

---

## 🎨 审批状态组件

### ApprovalStatus.vue - 审批状态标签

```vue
<template>
  <el-tag :type="getStatusType(status)" size="medium">
    {{ getStatusText(status) }}
  </el-tag>
</template>

<script>
export default {
  name: 'ApprovalStatus',
  props: {
    status: {
      type: String,
      required: true
    }
  },
  methods: {
    getStatusType(status) {
      const typeMap = {
        'draft': 'info',
        'pending_review': 'warning',
        'pending_approve': 'primary',
        'pending_finance': 'success',
        'approved': 'success',
        'distributed': 'success',
        'rejected': 'danger',
        'completed': 'success'
      }
      return typeMap[status] || 'info'
    },
    getStatusText(status) {
      const textMap = {
        'draft': '草稿',
        'pending_review': '待复核',
        'pending_approve': '待审批',
        'pending_finance': '待财务',
        'approved': '已通过',
        'distributed': '已发放',
        'rejected': '已驳回',
        'completed': '已完成'
      }
      return textMap[status] || '未知'
    }
  }
}
</script>
```

### ApprovalHistory.vue - 审批历史时间轴

```vue
<template>
  <el-timeline>
    <el-timeline-item
      v-for="(item, index) in history"
      :key="index"
      :timestamp="item.createTime"
      :type="getItemType(item.operationType)"
      placement="top">
      <el-card>
        <h4>{{ getOperationText(item.operationType) }}</h4>
        <p>操作人：{{ item.operatorName }}</p>
        <p>当前状态：{{ getStatusText(item.currentStatus) }}</p>
        <p v-if="item.operationRemark">备注：{{ item.operationRemark }}</p>
      </el-card>
    </el-timeline-item>
  </el-timeline>
</template>

<script>
export default {
  name: 'ApprovalHistory',
  props: {
    history: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    getItemType(operationType) {
      const typeMap = {
        'submit': 'primary',
        'review': 'success',
        'approve': 'success',
        'reject': 'danger',
        'cancel': 'info'
      }
      return typeMap[operationType] || 'primary'
    },
    getOperationText(operationType) {
      const textMap = {
        'submit': '提交审核',
        'review': '复核',
        'approve': '审批',
        'reject': '驳回',
        'cancel': '撤销'
      }
      return textMap[operationType] || '操作'
    },
    getStatusText(status) {
      const textMap = {
        'draft': '草稿',
        'pending_review': '待复核',
        'pending_approve': '待审批',
        'pending_finance': '待财务',
        'approved': '已通过',
        'rejected': '已驳回'
      }
      return textMap[status] || '未知'
    }
  }
}
</script>
```

### ApprovalButtons.vue - 审批按钮组

```vue
<template>
  <div class="approval-buttons">
    <!-- 提交审核 -->
    <el-button
      v-if="canSubmit"
      v-hasPermi="submitPermission"
      type="primary"
      @click="handleSubmit">
      提交审核
    </el-button>

    <!-- 复核通过 -->
    <el-button
      v-if="canReview"
      v-hasPermi="reviewPermission"
      type="success"
      @click="handleReview(true)">
      复核通过
    </el-button>

    <!-- 复核驳回 -->
    <el-button
      v-if="canReview"
      v-hasPermi="reviewPermission"
      type="warning"
      @click="handleReview(false)">
      复核驳回
    </el-button>

    <!-- 审批通过 -->
    <el-button
      v-if="canApprove"
      v-hasPermi="approvePermission"
      type="success"
      @click="handleApprove(true)">
      审批通过
    </el-button>

    <!-- 审批驳回 -->
    <el-button
      v-if="canApprove"
      v-hasPermi="approvePermission"
      type="danger"
      @click="handleApprove(false)">
      审批驳回
    </el-button>
  </div>
</template>

<script>
export default {
  name: 'ApprovalButtons',
  props: {
    status: {
      type: String,
      required: true
    },
    businessType: {
      type: String,
      required: true
    },
    submitPermission: {
      type: Array,
      default: () => []
    },
    reviewPermission: {
      type: Array,
      default: () => []
    },
    approvePermission: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    canSubmit() {
      return ['draft', 'rejected'].includes(this.status)
    },
    canReview() {
      return this.status === 'pending_review'
    },
    canApprove() {
      return this.status === 'pending_approve'
    }
  },
  methods: {
    handleSubmit() {
      this.$emit('submit')
    },
    handleReview(approved) {
      this.$emit('review', approved)
    },
    handleApprove(approved) {
      this.$emit('approve', approved)
    }
  }
}
</script>
```

---

## 📝 页面示例

### 人员登记列表页

```vue
<template>
  <div class="app-container">
    <!-- 查询条件 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input v-model="queryParams.idCardNo" placeholder="请输入身份证号" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-plus"
          v-hasPermi="['shebao:person:registration:add']"
          @click="handleAdd">
          新增
        </el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="身份证号" prop="idCardNo" />
      <el-table-column label="补贴类型" prop="subsidyType" />
      <el-table-column label="审批状态" align="center">
        <template slot-scope="scope">
          <approval-status :status="scope.row.approvalStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            v-hasPermi="['shebao:person:registration:edit']"
            @click="handleEdit(scope.row)">
            修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)">
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList" />
  </div>
</template>

<script>
import { listPersonRegistration } from '@/api/shebao/person'
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'

export default {
  name: 'PersonRegistration',
  components: {
    ApprovalStatus
  },
  data() {
    return {
      loading: false,
      dataList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPersonRegistration(this.queryParams).then(response => {
        this.dataList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleAdd() {
      this.$router.push({ name: 'PersonRegistrationAdd' })
    },
    handleEdit(row) {
      this.$router.push({ name: 'PersonRegistrationEdit', params: { id: row.id } })
    },
    handleView(row) {
      this.$router.push({ name: 'PersonRegistrationDetail', params: { id: row.id } })
    }
  }
}
</script>
```

---

## 🌐 API调用示例

### person.js - 人员信息API

```javascript
import request from '@/utils/request'

// 查询人员登记列表
export function listPersonRegistration(query) {
  return request({
    url: '/shebao/person/registration/list',
    method: 'get',
    params: query
  })
}

// 新增人员登记
export function addPersonRegistration(data) {
  return request({
    url: '/shebao/person/registration',
    method: 'post',
    data: data
  })
}

// 修改人员登记
export function updatePersonRegistration(data) {
  return request({
    url: '/shebao/person/registration',
    method: 'put',
    data: data
  })
}

// 提交审核
export function submitPersonRegistration(personId, remark) {
  return request({
    url: `/shebao/person/registration/submit/${personId}`,
    method: 'post',
    data: remark
  })
}

// 查询审批历史
export function getApprovalHistory(personId) {
  return request({
    url: `/shebao/person/review/history/${personId}`,
    method: 'get'
  })
}
```

---

## 📌 开发注意事项

### 1. 权限命名规范
- 格式：`模块:功能:操作`
- 示例：`shebao:person:landloss:list`

### 2. 状态管理
- 使用 Vuex 管理用户权限和角色
- 在登录后获取权限列表并存储

### 3. 组件复用
- 审批状态、审批历史等组件全局注册
- 统一的API请求方式和错误处理

### 4. 路由懒加载
- 使用 `import()` 实现路由懒加载
- 提高首屏加载速度

### 5. 样式规范
- 遵循 Element UI 设计规范
- 统一的颜色、字体、间距

---

## 🔄 下一步工作

1. ⏳ 根据菜单配置创建对应的Vue组件
2. ⏳ 实现完整的增删改查页面
3. ⏳ 集成审批状态组件
4. ⏳ 测试权限控制功能
5. ⏳ 优化用户体验

---

**文档生成时间**: 2026-01-19
**维护人员**: 开发团队
