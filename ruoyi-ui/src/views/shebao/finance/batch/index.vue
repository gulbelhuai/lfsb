<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="业务期">
        <el-date-picker v-model="queryParams.businessPeriod" type="month" value-format="yyyy-MM" placeholder="选择业务期" />
      </el-form-item>
      <el-form-item label="核定方式">
        <el-select v-model="queryParams.determinationType" clearable placeholder="全部">
          <el-option label="正常发放" value="normal" />
          <el-option label="二次发放" value="second" />
        </el-select>
      </el-form-item>
      <el-form-item label="财务状态">
        <el-select v-model="queryParams.financeStatus" clearable placeholder="全部">
          <el-option v-for="item in financeStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="批次号">
        <el-input v-model="queryParams.batchNo" clearable placeholder="精确查询" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList" border>
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column label="核定方式" prop="determinationType" width="100">
        <template slot-scope="scope">{{ determinationTypeText(scope.row.determinationType) }}</template>
      </el-table-column>
      <el-table-column label="业务期" prop="businessPeriod" width="100" />
      <el-table-column label="批次号" prop="batchNo" width="130" show-overflow-tooltip />
      <el-table-column label="发放人次" prop="totalCount" width="100" />
      <el-table-column label="总金额" prop="totalAmount" width="120" />
      <el-table-column label="发放机构" prop="grantOrg" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.shebao_grant_org" :value="scope.row.grantOrg" />
        </template>
      </el-table-column>
      <el-table-column label="经办人" prop="operatorName" width="100" />
      <el-table-column label="经办时间" prop="operatorTime" width="170" />
      <el-table-column label="补贴审批" prop="approvalStatus" width="110">
        <template slot-scope="scope">{{ subsidyStatusLabel(scope.row.approvalStatus) }}</template>
      </el-table-column>
      <el-table-column label="财务状态" prop="financeStatus" width="100">
        <template slot-scope="scope">{{ financeStatusLabel(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="openDetail(scope.row)">详情</el-button>
          <el-button
            v-if="scope.row.financeStatus === 'pending_finance'"
            v-hasPermi="['shebao:finance:batch:financePass']"
            type="text"
            size="mini"
            @click="doAction(scope.row, 'financePass')"
          >财务通过</el-button>
          <el-button
            v-if="scope.row.financeStatus === 'pending_finance'"
            v-hasPermi="['shebao:finance:batch:financeReject']"
            type="text"
            size="mini"
            @click="doAction(scope.row, 'financeReject')"
          >财务驳回</el-button>
          <el-button
            v-if="scope.row.financeStatus === 'finance_pending_review'"
            v-hasPermi="['shebao:finance:batch:reviewPass']"
            type="text"
            size="mini"
            @click="doAction(scope.row, 'reviewPass')"
          >复核通过</el-button>
          <el-button
            v-if="scope.row.financeStatus === 'finance_pending_review'"
            v-hasPermi="['shebao:finance:batch:reviewReject']"
            type="text"
            size="mini"
            @click="doAction(scope.row, 'reviewReject')"
          >复核驳回</el-button>
          <el-button
            v-if="scope.row.financeStatus === 'finance_pending_approve'"
            v-hasPermi="['shebao:finance:batch:approvePass']"
            type="text"
            size="mini"
            @click="doAction(scope.row, 'approvePass')"
          >审批通过</el-button>
          <el-button
            v-if="scope.row.financeStatus === 'finance_pending_approve'"
            v-hasPermi="['shebao:finance:batch:approveReject']"
            type="text"
            size="mini"
            @click="doAction(scope.row, 'approveReject')"
          >审批驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <plan-detail-dialog
      :visible.sync="detailOpen"
      :current-plan="currentPlan"
      :grant-org-options="dict.type.shebao_grant_org || []"
      :subsidy-type-formatter="subsidyTypeLabel"
      :status-formatter="financeStatusLabelForAudit"
      status-field="financeStatus"
      :fetch-summary="fetchSummary"
      :fetch-detail="fetchDetail"
      :fetch-audit="fetchAudit"
      :action-buttons="detailActions"
      :loading-action-key="loadingActionKey"
      @action-click="handleDetailAction"
    />
  </div>
</template>

<script>
import {
  listFinanceBatch,
  financeBatchPass,
  financeBatchReject,
  financeBatchReviewPass,
  financeBatchReviewReject,
  financeBatchApprovePass,
  financeBatchApproveReject
} from '@/api/shebao/finance'
import { getPaymentPlanSummary, getPaymentPlanDetail, getPaymentPlanAudit } from '@/api/shebao/payment'
import {
  paymentPlanStatusLabel,
  paymentPlanFinanceStatusLabel,
  PAYMENT_PLAN_FINANCE_STATUS_LABELS,
  promptFinanceBatchAction,
  paymentPlanAuditOperationLabel
} from '../../payment/plan/planUiShared'
import PlanDetailDialog from '../../payment/plan/PlanDetailDialog'

function currentMonth() {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  return `${y}-${m}`
}

const ACTION_API = {
  financePass: financeBatchPass,
  financeReject: financeBatchReject,
  reviewPass: financeBatchReviewPass,
  reviewReject: financeBatchReviewReject,
  approvePass: financeBatchApprovePass,
  approveReject: financeBatchApproveReject
}

const ACTION_NEXT_STATUS = {
  financePass: 'finance_pending_review',
  financeReject: 'finance_rejected',
  reviewPass: 'finance_pending_approve',
  reviewReject: 'finance_rejected',
  approvePass: 'finance_approved',
  approveReject: 'finance_rejected'
}

export default {
  name: 'FinanceBatch',
  components: { PlanDetailDialog },
  dicts: ['shebao_grant_org'],
  data() {
    return {
      loading: false,
      total: 0,
      dataList: [],
      detailOpen: false,
      currentPlan: null,
      loadingActionKey: '',
      financeStatusOptions: Object.keys(PAYMENT_PLAN_FINANCE_STATUS_LABELS).map(value => ({
        value,
        label: PAYMENT_PLAN_FINANCE_STATUS_LABELS[value]
      })),
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        businessPeriod: null,
        determinationType: null,
        financeStatus: null,
        batchNo: null
      }
    }
  },
  computed: {
    detailActions() {
      return [
        { key: 'financePass', label: '财务通过', type: 'success', statuses: ['pending_finance'], perm: 'shebao:finance:batch:financePass' },
        { key: 'financeReject', label: '财务驳回', type: 'danger', statuses: ['pending_finance'], perm: 'shebao:finance:batch:financeReject' },
        { key: 'reviewPass', label: '复核通过', type: 'success', statuses: ['finance_pending_review'], perm: 'shebao:finance:batch:reviewPass' },
        { key: 'reviewReject', label: '复核驳回', type: 'danger', statuses: ['finance_pending_review'], perm: 'shebao:finance:batch:reviewReject' },
        { key: 'approvePass', label: '审批通过', type: 'success', statuses: ['finance_pending_approve'], perm: 'shebao:finance:batch:approvePass' },
        { key: 'approveReject', label: '审批驳回', type: 'danger', statuses: ['finance_pending_approve'], perm: 'shebao:finance:batch:approveReject' }
      ]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listFinanceBatch(this.queryParams).then(res => {
        this.dataList = res.rows || []
        this.total = res.total || 0
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
        businessPeriod: currentMonth(),
        determinationType: null,
        financeStatus: null,
        batchNo: null
      }
      this.getList()
    },
    openDetail(row) {
      this.currentPlan = { ...row }
      this.detailOpen = true
    },
    fetchSummary(planId) {
      return getPaymentPlanSummary(planId).then(res => res.data || [])
    },
    fetchDetail(planId) {
      return getPaymentPlanDetail(planId, { pageNum: 1, pageSize: 1000 }).then(res => res.rows || [])
    },
    fetchAudit(planId) {
      return getPaymentPlanAudit(planId).then(res => res.data || [])
    },
    handleDetailAction(actionKey, row) {
      this.doAction(row, actionKey)
    },
    doAction(row, actionKey) {
      const api = ACTION_API[actionKey]
      if (!api) return
      promptFinanceBatchAction(this, actionKey).then(remark => {
        this.loadingActionKey = actionKey
        return api(row.id, { remark })
      }).then(() => {
        this.$modal.msgSuccess('操作成功')
        const next = ACTION_NEXT_STATUS[actionKey]
        if (this.detailOpen && this.currentPlan && this.currentPlan.id === row.id) {
          this.currentPlan.financeStatus = next
        }
        this.getList()
      }).finally(() => {
        this.loadingActionKey = ''
      })
    },
    determinationTypeText(val) {
      return val === 'second' ? '二次发放' : '正常发放'
    },
    subsidyStatusLabel(val) {
      return paymentPlanStatusLabel(val)
    },
    financeStatusLabel(row) {
      const s = row.financeStatus || row.finance_status
      return s ? paymentPlanFinanceStatusLabel(s) : '—'
    },
    financeStatusLabelForAudit(statusOrRow) {
      if (statusOrRow && typeof statusOrRow === 'object') {
        return paymentPlanAuditOperationLabel(statusOrRow)
      }
      return paymentPlanFinanceStatusLabel(statusOrRow)
    },
    subsidyTypeLabel(val) {
      const map = {
        land_loss: '失地',
        land_loss_resident: '失地',
        demolition: '拆迁',
        demolition_resident: '拆迁',
        village_official: '村干部'
      }
      return map[val] || val
    }
  }
}
</script>
