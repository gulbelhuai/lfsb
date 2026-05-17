<template>
  <div class="app-container">
    <el-alert title="上传财务系统说明" type="info" :closable="false" class="mb20">
      <div>请输入支付计划批次号查询；审批通过且尚未进入财务流程的计划可点击「上传财务」，确认后将进入「待财务」状态。</div>
    </el-alert>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="批次号"><el-input v-model="queryParams.batchNo" clearable placeholder="精确查询" /></el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList" border>
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="批次号" prop="batchNo" width="130" show-overflow-tooltip />
      <el-table-column label="补贴类型" min-width="140" show-overflow-tooltip>
        <template slot-scope="scope">{{ subsidyTypesText(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="业务期" prop="businessPeriod" width="100" />
      <el-table-column label="总人数" prop="totalCount" width="90" />
      <el-table-column label="总金额(元)" prop="totalAmount" width="110" />
      <el-table-column label="审批状态" prop="approvalStatus" width="110">
        <template slot-scope="scope">{{ paymentPlanStatusLabel(scope.row.approvalStatus) }}</template>
      </el-table-column>
      <el-table-column label="财务状态" prop="financeStatus" width="110">
        <template slot-scope="scope">{{ financeStatusCell(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">详情</el-button>
          <el-button
            v-if="canUploadFinance(scope.row)"
            size="mini"
            type="primary"
            @click="handleUploadFinance(scope.row)"
          >上传财务</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listPaymentPlan, uploadPlanToFinance } from '@/api/shebao/payment'
import { paymentPlanStatusLabel, paymentPlanFinanceStatusLabel } from '../plan/planUiShared'

const SUBSIDY_LABEL = {
  land_loss: '失地',
  land_loss_resident: '失地居民',
  demolition: '拆迁',
  demolition_resident: '拆迁居民',
  village_official: '村干部'
}

export default {
  name: 'PaymentUpload',
  data() {
    return {
      loading: false,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, batchNo: null }
    }
  },
  methods: {
    paymentPlanStatusLabel,
    financeStatusCell(row) {
      const s = row.financeStatus || row.finance_status
      if (!s) return '—'
      return paymentPlanFinanceStatusLabel(s)
    },
    subsidyTypesText(row) {
      const raw = row.subsidyTypes || row.subsidy_types
      if (!raw) return '—'
      return String(raw)
        .split(',')
        .map(t => SUBSIDY_LABEL[t.trim()] || t.trim())
        .filter(Boolean)
        .join('、') || '—'
    },
    canUploadFinance(row) {
      if (!row || row.approvalStatus !== 'approved') return false
      const fs = row.financeStatus || row.finance_status
      return !fs
    },
    getList() {
      const bn = (this.queryParams.batchNo || '').trim()
      if (!bn) {
        this.dataList = []
        this.total = 0
        this.loading = false
        return
      }
      this.loading = true
      listPaymentPlan({ ...this.queryParams, batchNo: bn })
        .then(response => {
          this.dataList = response.rows || []
          this.total = response.total || 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleView(row) {
      const fs = row.financeStatus || row.finance_status
      this.$alert(
        `批次号：${row.batchNo || '-'}\n业务期：${row.businessPeriod || '-'}\n补贴类型：${this.subsidyTypesText(row)}\n总人数：${row.totalCount || 0}\n总金额：${row.totalAmount || 0}\n审批状态：${paymentPlanStatusLabel(row.approvalStatus)}\n财务状态：${fs ? paymentPlanFinanceStatusLabel(fs) : '未进入'}`,
        '支付计划摘要',
        { confirmButtonText: '关闭' }
      )
    },
    handleUploadFinance(row) {
      this.$modal.confirm('确认将该支付计划上传至财务流程？确认后财务状态将变为「待财务」。').then(() => {
        return uploadPlanToFinance(row.id)
      }).then(() => {
        this.$modal.msgSuccess('操作成功')
        this.getList()
      })
    }
  }
}
</script>

<style scoped>
.mb20 {
  margin-bottom: 20px;
}
</style>
