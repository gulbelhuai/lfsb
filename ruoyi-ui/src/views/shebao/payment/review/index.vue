<template>
  <div class="app-container">
    <el-form :model="queryParams" size="small" :inline="true">
      <el-form-item label="业务期">
        <el-date-picker v-model="queryParams.businessPeriod" type="month" value-format="yyyy-MM" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="dataList" border>
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column label="核定方式" prop="determinationType">
        <template slot-scope="scope">{{ scope.row.determinationType === 'second' ? '二次发放' : '正常发放' }}</template>
      </el-table-column>
      <el-table-column label="业务期" prop="businessPeriod" width="100" />
      <el-table-column label="批次号" prop="batchNo" width="130" show-overflow-tooltip />
      <el-table-column label="发放人次" prop="totalCount" width="100" />
      <el-table-column label="总金额" prop="totalAmount" width="120" />
      <el-table-column label="发放机构" prop="grantOrg" width="120">
        <template slot-scope="scope"><dict-tag :options="dict.type.shebao_grant_org" :value="scope.row.grantOrg" /></template>
      </el-table-column>
      <el-table-column label="经办人" prop="operatorName" width="120" />
      <el-table-column label="经办时间" prop="operatorTime" width="170" />
      <el-table-column label="审批状态" prop="approvalStatus" width="120">
        <template slot-scope="scope">{{ statusLabel(scope.row.approvalStatus) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="openDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.approvalStatus === 'pending_review'" type="text" size="mini" @click="changeStatus(scope.row, 'pending_approve')">复核通过</el-button>
          <el-button v-if="scope.row.approvalStatus === 'pending_review'" type="text" size="mini" @click="changeStatus(scope.row, 'review_rejected')">复核驳回</el-button>
          <el-button v-if="['pending_approve','review_rejected'].includes(scope.row.approvalStatus)" type="text" size="mini" @click="changeStatus(scope.row, 'pending_review')">撤销复核</el-button>
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
import { paymentPlanStatusLabel, promptPlanAction } from '../plan/planUiShared'
import PlanDetailDialog from '../plan/PlanDetailDialog'

export default {
  name: 'PaymentReview',
  components: { PlanDetailDialog },
  dicts: ['shebao_grant_org'],
  data() {
    return {
      loading: false,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, businessPeriod: null, approvalStatus: 'pending_review' },
      detailOpen: false,
      currentPlan: null,
      loadingActionKey: ''
    }
  },
  computed: {
    detailActions() {
      return [
        { key: 'reviewPass', label: '复核通过', type: 'success', statuses: ['pending_review'] },
        { key: 'reviewReject', label: '复核驳回', type: 'danger', statuses: ['pending_review'] },
        { key: 'undoReview', label: '撤销复核', statuses: ['pending_approve', 'review_rejected'] }
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
      if (actionKey === 'reviewPass') return this.changeStatus(row, 'pending_approve')
      if (actionKey === 'reviewReject') return this.changeStatus(row, 'review_rejected')
      if (actionKey === 'undoReview') return this.changeStatus(row, 'pending_review')
    },
    changeStatus(row, targetStatus) {
      const actionKeyMap = {
        pending_approve: 'reviewPass',
        review_rejected: 'reviewReject',
        pending_review: 'undoReview'
      }
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
      const map = {
        land_loss: '失地',
        land_loss_resident: '失地',
        demolition: '拆迁',
        demolition_resident: '拆迁',
        village_official: '村干部'
      }
      return map[val] || val
    },
    statusLabel(v) {
      return paymentPlanStatusLabel(v)
    }
  }
}
</script>
