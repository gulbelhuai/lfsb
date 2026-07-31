<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true">
      <el-form-item label="业务期">
        <el-date-picker v-model="queryParams.businessPeriod" type="month" value-format="yyyy-MM" />
      </el-form-item>
      <el-form-item label="补贴类型">
        <el-select v-model="queryParams.subsidyType" clearable placeholder="全部">
          <el-option v-for="o in subsidyTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="dataList" border>
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column label="批次号" prop="batchNo" width="130" show-overflow-tooltip />
      <el-table-column label="业务期" prop="businessPeriod" width="100" />
      <el-table-column label="补贴类型" prop="subsidyType" width="120">
        <template slot-scope="scope">{{ subsidyTypeLabel(scope.row.subsidyType) }}</template>
      </el-table-column>
      <el-table-column label="核定方式" prop="determinationType" width="100">
        <template slot-scope="scope">{{ scope.row.determinationType === 'second' ? '二次发放' : '正常发放' }}</template>
      </el-table-column>
      <el-table-column label="发放人次" prop="totalCount" width="100" />
      <el-table-column label="总金额" prop="totalAmount" width="120" />
      <el-table-column label="发放机构" prop="grantOrg" min-width="160" show-overflow-tooltip>
        <template slot-scope="scope">{{ grantOrgLabels(scope.row.grantOrg) }}</template>
      </el-table-column>
      <el-table-column label="经办人" prop="operatorName" width="120" />
      <el-table-column label="经办时间" prop="operatorTime" width="170" />
      <el-table-column label="审批状态" prop="approvalStatus" width="120">
        <template slot-scope="scope">{{ statusLabel(scope.row.approvalStatus) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="openDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.approvalStatus === 'pending_approve'" type="text" size="mini" @click="changeStatus(scope.row, 'approved')">审批通过</el-button>
          <el-button v-if="scope.row.approvalStatus === 'pending_approve'" type="text" size="mini" @click="changeStatus(scope.row, 'approve_rejected')">审批驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <plan-detail-dialog
      :visible.sync="detailOpen"
      :current-plan="currentPlan"
      :grant-org-options="dict.type.shebao_grant_org || []"
      :subsidy-type-formatter="subsidyTypeLabel"
      :status-formatter="statusLabel"
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
import { listPaymentPlan, getPaymentPlanSummary, getPaymentPlanDetail, getPaymentPlanAudit, changePaymentPlanStatus } from '@/api/shebao/payment'
import { paymentPlanStatusLabel, promptPlanAction, paymentPlanSubsidyTypeLabel, paymentPlanGrantOrgLabels, PAYMENT_PLAN_SUBSIDY_TYPE_OPTIONS } from '../plan/planUiShared'
import PlanDetailDialog from '../plan/PlanDetailDialog'

export default {
  name: 'PaymentApprove',
  components: { PlanDetailDialog },
  dicts: ['shebao_grant_org'],
  data() {
    return {
      loading: false,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, businessPeriod: null, subsidyType: null, approvalStatus: 'pending_approve' },
      detailOpen: false,
      currentPlan: null,
      loadingActionKey: ''
    }
  },
  computed: {
    subsidyTypeOptions() {
      return PAYMENT_PLAN_SUBSIDY_TYPE_OPTIONS
    },
    detailActions() {
      return [
        { key: 'approvePass', label: '审批通过', type: 'success', statuses: ['pending_approve'] },
        { key: 'approveReject', label: '审批驳回', type: 'danger', statuses: ['pending_approve'] }
      ]
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.loading = true
      listPaymentPlan(this.queryParams).then(res => {
        this.dataList = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
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
      if (actionKey === 'approvePass') return this.changeStatus(row, 'approved')
      if (actionKey === 'approveReject') return this.changeStatus(row, 'approve_rejected')
    },
    changeStatus(row, targetStatus) {
      const actionKeyMap = { approved: 'approvePass', approve_rejected: 'approveReject' }
      promptPlanAction(this, targetStatus).then((value) => {
        this.loadingActionKey = actionKeyMap[targetStatus] || ''
        return changePaymentPlanStatus(row.id, { targetStatus, remark: value })
      }).then(() => {
        this.$modal.msgSuccess('操作成功')
        this.getList()
        if (this.detailOpen && this.currentPlan && this.currentPlan.id === row.id) {
          this.currentPlan.approvalStatus = targetStatus
        }
      }).finally(() => {
        this.loadingActionKey = ''
      })
    },
    subsidyTypeLabel(val) {
      return paymentPlanSubsidyTypeLabel(val)
    },
    grantOrgLabels(val) {
      return paymentPlanGrantOrgLabels(val, this.dict.type.shebao_grant_org)
    },
    statusLabel(v) {
      return paymentPlanStatusLabel(v)
    }
  }
}
</script>
