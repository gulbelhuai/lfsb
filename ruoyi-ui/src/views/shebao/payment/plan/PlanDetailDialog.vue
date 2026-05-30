<template>
  <el-dialog :title="title" :visible.sync="visibleInner" width="1200px" @close="onClose">
    <div v-if="currentPlan" class="plan-batch-meta">批次号：{{ planBatchNoText }}</div>
    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="汇总表" name="summary">
        <el-table :data="summaryList" border>
          <el-table-column label="业务期" prop="businessPeriod" width="100" />
          <el-table-column label="补贴类型" prop="subsidyType">
            <template slot-scope="scope">{{ subsidyTypeFormatter(scope.row.subsidyType) }}</template>
          </el-table-column>
          <el-table-column label="发放机构" prop="grantOrg">
            <template slot-scope="scope"><dict-tag :options="grantOrgOptions" :value="scope.row.grantOrg" /></template>
          </el-table-column>
          <el-table-column label="发放人次" prop="totalCount" width="100" />
          <el-table-column label="总金额" prop="totalAmount" width="120" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="明细表" name="detail">
        <el-table v-loading="detailLoading" :data="detailList" border height="360">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column label="补贴类型" prop="subsidyType" width="120">
            <template slot-scope="scope">{{ subsidyTypeFormatter(scope.row.subsidyType) }}</template>
          </el-table-column>
          <el-table-column label="街道" prop="streetName" width="120" />
          <el-table-column label="村委会" prop="villageName" width="120" />
          <el-table-column label="姓名" prop="personName" width="100" />
          <el-table-column label="身份证号" prop="idCardNo" width="180" />
          <el-table-column label="业务期" prop="businessPeriod" width="100" />
          <el-table-column label="发放月份" prop="paymentMonth" width="100" />
          <el-table-column label="发放金额" prop="distributionAmount" width="100" />
          <el-table-column label="发放机构" prop="grantOrg" width="120">
            <template slot-scope="scope"><dict-tag :options="grantOrgOptions" :value="scope.row.grantOrg" /></template>
          </el-table-column>
          <el-table-column label="开户名" prop="accountName" width="100" />
          <el-table-column label="银行账号" prop="bankAccount" width="180" />
          <el-table-column label="与参保人关系" prop="relationToInsured" width="120" />
          <el-table-column v-if="showDistributionResult" label="发放结果" prop="distributionResult" width="100">
            <template slot-scope="scope">{{ distributionResultLabel(scope.row.distributionResult) }}</template>
          </el-table-column>
          <el-table-column v-if="showDistributionResult" label="失败原因" prop="failReason" min-width="140" show-overflow-tooltip />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="审核记录" name="audit">
        <el-table :data="auditList" border>
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column label="审批阶段" prop="approvalStage" width="100">
            <template slot-scope="scope">{{ paymentPlanAuditStageLabelFromRow(scope.row) }}</template>
          </el-table-column>
          <el-table-column label="操作状态" prop="operationStatus" width="120">
            <template slot-scope="scope">{{ paymentPlanAuditOperationLabel(scope.row) }}</template>
          </el-table-column>
          <el-table-column label="操作人" prop="operatorName" width="120" />
          <el-table-column label="操作时间" prop="operationTime" width="180" />
          <el-table-column label="备注" prop="remark" />
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <div slot="footer">
      <template v-for="btn in visibleActions">
        <el-button
          v-if="btn.perm"
          :key="btn.key"
          v-hasPermi="[btn.perm]"
          :type="btn.type || 'default'"
          :loading="loadingActionKey === btn.key"
          @click="$emit('action-click', btn.key, currentPlan)"
        >{{ btn.label }}</el-button>
        <el-button
          v-else
          :key="btn.key + '-noperm'"
          :type="btn.type || 'default'"
          :loading="loadingActionKey === btn.key"
          @click="$emit('action-click', btn.key, currentPlan)"
        >{{ btn.label }}</el-button>
      </template>
    </div>
  </el-dialog>
</template>

<script>
import { paymentPlanAuditOperationLabel, paymentPlanAuditStageLabelFromRow } from './planUiShared'

export default {
  name: 'PlanDetailDialog',
  props: {
    visible: { type: Boolean, required: true },
    title: { type: String, default: '支付计划详情' },
    currentPlan: { type: Object, default: null },
    grantOrgOptions: { type: Array, default: () => [] },
    actionButtons: { type: Array, default: () => [] },
    loadingActionKey: { type: String, default: '' },
    subsidyTypeFormatter: { type: Function, required: true },
    statusFormatter: { type: Function, required: true },
    /** 用于控制详情底部按钮可见性的状态字段，默认 approvalStatus */
    statusField: { type: String, default: 'approvalStatus' },
    showDistributionResult: { type: Boolean, default: false },
    fetchSummary: { type: Function, required: true },
    fetchDetail: { type: Function, required: true },
    fetchAudit: { type: Function, required: true }
  },
  data() {
    return {
      visibleInner: false,
      activeTab: 'summary',
      summaryList: [],
      detailList: [],
      auditList: [],
      detailLoading: false
    }
  },
  computed: {
    visibleActions() {
      if (!this.currentPlan) return []
      const field = this.statusField || 'approvalStatus'
      const status = this.currentPlan[field] || this.currentPlan[field === 'financeStatus' ? 'finance_status' : 'approval_status'] || ''
      return (this.actionButtons || []).filter(btn => !btn.statuses || btn.statuses.includes(status))
    },
    planBatchNoText() {
      if (!this.currentPlan) return '—'
      return this.currentPlan.batchNo || this.currentPlan.batch_no || '—'
    }
  },
  watch: {
    visible: {
      immediate: true,
      handler(v) {
        this.visibleInner = v
        if (v) {
          this.activeTab = 'summary'
          this.loadSummary()
        }
      }
    },
    visibleInner(v) {
      if (!v) this.$emit('update:visible', false)
    }
  },
  methods: {
    paymentPlanAuditOperationLabel,
    paymentPlanAuditStageLabelFromRow,
    distributionResultLabel(val) {
      if (val === 'success') return '成功'
      if (val === 'failed') return '失败'
      return '—'
    },
    onClose() {
      this.$emit('update:visible', false)
    },
    handleTabChange(tab) {
      if (tab.name === 'summary') this.loadSummary()
      if (tab.name === 'detail') this.loadDetail()
      if (tab.name === 'audit') this.loadAudit()
    },
    loadSummary() {
      if (!this.currentPlan) return
      this.fetchSummary(this.currentPlan.id).then(list => {
        this.summaryList = list || []
      })
    },
    loadDetail() {
      if (!this.currentPlan) return
      this.detailLoading = true
      this.fetchDetail(this.currentPlan.id).then(list => {
        this.detailList = list || []
      }).finally(() => {
        this.detailLoading = false
      })
    },
    loadAudit() {
      if (!this.currentPlan) return
      this.fetchAudit(this.currentPlan.id).then(list => {
        this.auditList = list || []
      })
    },
    reloadAudit() {
      this.loadAudit()
    }
  }
}
</script>

<style scoped>
.plan-batch-meta {
  margin-bottom: 12px;
  font-size: 13px;
  color: #606266;
}
</style>
