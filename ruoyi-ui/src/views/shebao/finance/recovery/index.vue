<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="姓名">
        <el-input v-model="queryParams.personName" clearable placeholder="模糊查询" />
      </el-form-item>
      <el-form-item label="身份证号">
        <el-input v-model="queryParams.idCardNo" clearable placeholder="模糊查询" />
      </el-form-item>
      <el-form-item label="补贴类型">
        <el-select v-model="queryParams.subsidyType" clearable placeholder="全部">
          <el-option v-for="o in subsidyTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="追回状态">
        <el-select v-model="queryParams.recoveryStatus" clearable placeholder="全部">
          <el-option label="未追回" value="0" />
          <el-option label="已追回" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList" border>
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column label="姓名" prop="personName" width="100" show-overflow-tooltip />
      <el-table-column label="身份证号" prop="idCardNo" width="180" show-overflow-tooltip />
      <el-table-column label="补贴类型" prop="subsidyType" width="120">
        <template slot-scope="scope">{{ subsidyTypeLabel(scope.row.subsidyType) }}</template>
      </el-table-column>
      <el-table-column label="追回开始年月" prop="recoverStartMonth" width="120">
        <template slot-scope="scope">{{ formatMonth(scope.row.recoverStartMonth) }}</template>
      </el-table-column>
      <el-table-column label="追回终止年月" prop="recoverEndMonth" width="120">
        <template slot-scope="scope">{{ formatMonth(scope.row.recoverEndMonth) }}</template>
      </el-table-column>
      <el-table-column label="需追回金额(元)" prop="recoverAmount" width="120">
        <template slot-scope="scope">{{ formatMoney(scope.row.recoverAmount) }}</template>
      </el-table-column>
      <el-table-column label="录入时间" prop="createTime" width="160" show-overflow-tooltip />
      <el-table-column label="追回状态" prop="recoveryStatus" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.recoveryStatus === '1' ? 'success' : 'warning'" size="small">
            {{ recoveryStatusLabel(scope.row.recoveryStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="追回时间" prop="recoveryTime" width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="110" >
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.recoveryStatus !== '1'"
            v-hasPermi="['shebao:finance:recovery:confirm']"
            type="text"
            size="mini"
            @click="handleConfirm(scope.row)"
          >已追回</el-button>
          <span v-else class="text-muted">—</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listBenefitRecovery, confirmBenefitRecovery } from '@/api/shebao/finance'
import { paymentPlanSubsidyTypeLabel, PAYMENT_PLAN_SUBSIDY_TYPE_OPTIONS } from '../../payment/plan/planUiShared'

export default {
  name: 'FinanceBenefitRecovery',
  data() {
    return {
      loading: false,
      total: 0,
      dataList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        personName: null,
        idCardNo: null,
        subsidyType: null,
        recoveryStatus: null
      }
    }
  },
  computed: {
    subsidyTypeOptions() {
      return PAYMENT_PLAN_SUBSIDY_TYPE_OPTIONS
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listBenefitRecovery(this.queryParams).then(response => {
        this.dataList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        personName: null,
        idCardNo: null,
        subsidyType: null,
        recoveryStatus: null
      }
      this.getList()
    },
    handleConfirm(row) {
      const name = row.personName || ''
      const amount = this.formatMoney(row.recoverAmount)
      this.$modal.confirm(`确认将「${name}」的待遇追回标记为已追回？需追回金额 ${amount} 元将计入对应补贴账户。`).then(() => {
        return confirmBenefitRecovery(row.id)
      }).then(() => {
        this.$modal.msgSuccess('已追回')
        this.getList()
      }).catch(() => {})
    },
    subsidyTypeLabel(val) {
      return paymentPlanSubsidyTypeLabel(val)
    },
    recoveryStatusLabel(status) {
      return status === '1' ? '已追回' : '未追回'
    },
    formatMonth(val) {
      if (!val) return '—'
      const s = String(val)
      return s.length >= 7 ? s.substring(0, 7) : s
    },
    formatMoney(val) {
      if (val === null || val === undefined || val === '') return '0.00'
      const num = Number(val)
      if (Number.isNaN(num)) return val
      return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    }
  }
}
</script>

<style scoped>
.text-muted {
  color: #909399;
  font-size: 12px;
}
</style>
