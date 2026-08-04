<template>
  <div class="app-container">
    <!-- 账户概览 -->
    <el-card class="box-card">
      <div slot="header"><span>账户概览</span></div>
      <el-row :gutter="12" v-loading="overviewLoading" class="overview-row">
        <el-col :xs="12" :sm="8" :md="6" :lg="6" v-for="account in accountList" :key="account.id" class="overview-col">
          <el-card shadow="hover" class="account-card" :body-style="{ padding: '12px 10px' }">
            <div class="account-name">{{ accountDisplayName(account.accountType) }}</div>
            <div class="account-balance">{{ formatMoney(account.balance) }} 元</div>
            <el-button
              type="primary"
              size="mini"
              plain
              v-hasPermi="['shebao:finance:account:allocate']"
              @click="openAllocate(account)"
            >财政拨款</el-button>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!overviewLoading && accountList.length === 0" description="暂无财务账户" />
    </el-card>

    <!-- 账户明细 -->
    <el-card class="box-card mt20">
      <div slot="header"><span>账户明细</span></div>

      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
        <el-form-item label="账户名称">
          <el-select v-model="queryParams.accountId" clearable placeholder="全部" style="width: 220px">
            <el-option
              v-for="item in accountList"
              :key="item.id"
              :label="accountDisplayName(item.accountType)"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="交易类型">
          <el-select v-model="queryParams.transactionType" clearable placeholder="全部" style="width: 140px">
            <el-option
              v-for="item in transactionTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="交易日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="amount-summary">金额汇总（元）：{{ formatSignedAmount(amountSum) }}</div>

      <el-table v-loading="loading" :data="detailList" border>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column label="账户名称" prop="accountName" min-width="160" show-overflow-tooltip />
        <el-table-column label="批次号" prop="batchNo" width="130" show-overflow-tooltip>
          <template slot-scope="scope">{{ scope.row.batchNo || '—' }}</template>
        </el-table-column>
        <el-table-column label="交易类型" prop="transactionType" width="110">
          <template slot-scope="scope">
            <el-tag :type="transactionTagType(scope.row.transactionType)" size="small">
              {{ transactionTypeLabel(scope.row.transactionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="交易金额(元)" prop="amount" width="130" align="right">
          <template slot-scope="scope">
            <span :class="amountClass(scope.row.amount)">{{ formatSignedAmount(scope.row.amount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="余额(元)" prop="balance" width="120" align="right">
          <template slot-scope="scope">{{ formatMoney(scope.row.balance) }}</template>
        </el-table-column>
        <el-table-column label="交易时间" prop="transactionTime" width="170" />
        <el-table-column label="备注" prop="remark" min-width="160" show-overflow-tooltip />
      </el-table>

      <pagination
        v-show="detailTotal > 0"
        :total="detailTotal"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getDetailList"
      />
    </el-card>

    <!-- 财政拨款 -->
    <el-dialog title="财政拨款" :visible.sync="allocateOpen" width="480px" :close-on-click-modal="false">
      <el-form ref="allocateForm" :model="allocateForm" :rules="allocateRules" label-width="100px">
        <el-form-item label="账户名称">
          <el-input :value="allocateForm.accountName" disabled />
        </el-form-item>
        <el-form-item label="当前余额">
          <el-input :value="formatMoney(allocateForm.currentBalance) + ' 元'" disabled />
        </el-form-item>
        <el-form-item label="拨款金额" prop="amount">
          <el-input v-model="allocateForm.amount" placeholder="请输入大于0的金额">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="allocateForm.remark" type="textarea" placeholder="选填" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" :loading="allocateLoading" @click="submitAllocate">确 定</el-button>
        <el-button @click="allocateOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getFinanceAccountOverview,
  listFinanceAccountTransactions,
  fiscalAllocationAccount
} from '@/api/shebao/finance'
import { paymentPlanSubsidyTypeLabel } from '../../payment/plan/planUiShared'

const TRANSACTION_TYPE_OPTIONS = [
  { value: 'fiscal_allocation', label: '财政拨款' },
  { value: 'subsidy_distribution', label: '补贴发放' },
  { value: 'benefit_recovery', label: '待遇追回' }
]

export default {
  name: 'FinanceAccount',
  data() {
    const validateAmount = (rule, value, callback) => {
      if (value === '' || value === null || value === undefined) {
        callback(new Error('请输入拨款金额'))
        return
      }
      const num = Number(value)
      if (Number.isNaN(num) || num <= 0) {
        callback(new Error('拨款金额须大于0'))
        return
      }
      callback()
    }
    return {
      overviewLoading: false,
      loading: false,
      detailTotal: 0,
      amountSum: 0,
      accountList: [],
      detailList: [],
      dateRange: [],
      transactionTypeOptions: TRANSACTION_TYPE_OPTIONS,
      allocateOpen: false,
      allocateLoading: false,
      allocateForm: {
        accountId: null,
        accountName: '',
        currentBalance: 0,
        amount: '',
        remark: ''
      },
      allocateRules: {
        amount: [{ required: true, validator: validateAmount, trigger: 'blur' }]
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        accountId: null,
        transactionType: null,
        transactionDateStart: null,
        transactionDateEnd: null
      }
    }
  },
  created() {
    this.loadOverview().then(() => this.getDetailList())
  },
  methods: {
    loadOverview() {
      this.overviewLoading = true
      return getFinanceAccountOverview().then(res => {
        this.accountList = res.data || []
      }).finally(() => {
        this.overviewLoading = false
      })
    },
    getDetailList() {
      this.loading = true
      if (this.dateRange && this.dateRange.length === 2) {
        this.queryParams.transactionDateStart = this.dateRange[0]
        this.queryParams.transactionDateEnd = this.dateRange[1]
      } else {
        this.queryParams.transactionDateStart = null
        this.queryParams.transactionDateEnd = null
      }
      listFinanceAccountTransactions(this.queryParams).then(res => {
        this.detailList = res.rows || []
        this.detailTotal = res.total || 0
        this.amountSum = res.amountSum == null ? 0 : res.amountSum
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getDetailList()
    },
    resetQuery() {
      this.dateRange = []
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        accountId: null,
        transactionType: null,
        transactionDateStart: null,
        transactionDateEnd: null
      }
      this.getDetailList()
    },
    openAllocate(account) {
      this.allocateForm = {
        accountId: account.id,
        accountName: this.accountDisplayName(account.accountType),
        currentBalance: account.balance,
        amount: '',
        remark: ''
      }
      this.allocateOpen = true
      this.$nextTick(() => {
        if (this.$refs.allocateForm) {
          this.$refs.allocateForm.clearValidate()
        }
      })
    },
    submitAllocate() {
      this.$refs.allocateForm.validate(valid => {
        if (!valid) return
        this.allocateLoading = true
        fiscalAllocationAccount(this.allocateForm.accountId, {
          amount: Number(this.allocateForm.amount),
          remark: this.allocateForm.remark
        }).then(() => {
          this.$modal.msgSuccess('财政拨款成功')
          this.allocateOpen = false
          this.loadOverview().then(() => this.getDetailList())
        }).finally(() => {
          this.allocateLoading = false
        })
      })
    },
    accountDisplayName(type) {
      const label = paymentPlanSubsidyTypeLabel(type)
      if (!label || label === '—') return '账户'
      return label + '账户'
    },
    transactionTypeLabel(type) {
      const hit = this.transactionTypeOptions.find(o => o.value === type)
      return (hit && hit.label) || type || '—'
    },
    transactionTagType(type) {
      if (type === 'fiscal_allocation') return 'success'
      if (type === 'subsidy_distribution') return 'danger'
      if (type === 'benefit_recovery') return 'warning'
      return 'info'
    },
    formatMoney(val) {
      if (val === null || val === undefined || val === '') return '0.00'
      const num = Number(val)
      if (Number.isNaN(num)) return val
      return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    },
    formatSignedAmount(val) {
      if (val === null || val === undefined || val === '') return '—'
      const num = Number(val)
      if (Number.isNaN(num)) return val
      const formatted = this.formatMoney(Math.abs(num))
      if (num > 0) return '+' + formatted
      if (num < 0) return '-' + formatted
      return formatted
    },
    amountClass(val) {
      const num = Number(val)
      if (num > 0) return 'amount-in'
      if (num < 0) return 'amount-out'
      return ''
    }
  }
}
</script>

<style scoped>
.overview-row {
  margin-bottom: 0;
}
.overview-col {
  margin-bottom: 12px;
}
.account-card {
  text-align: center;
}
.account-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
  font-weight: 500;
  line-height: 1.3;
}
.account-balance {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}
.amount-summary {
  margin: 0 0 12px;
  padding: 8px 12px;
  background: #f5f7fa;
  color: #303133;
  font-size: 14px;
  border-radius: 4px;
}
.amount-in {
  color: #67C23A;
  font-weight: 500;
}
.amount-out {
  color: #F56C6C;
  font-weight: 500;
}
.mt20 {
  margin-top: 20px;
}
</style>
