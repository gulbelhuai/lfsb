<template>
  <div class="app-container">
    <el-card class="mb20">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
        <el-form-item label="业务期">
          <el-date-picker v-model="queryParams.businessPeriod" type="month" value-format="yyyy-MM" placeholder="选择业务期" />
        </el-form-item>
        <el-form-item label="补贴类型">
          <el-select v-model="queryParams.subsidyType" clearable placeholder="全部">
            <el-option v-for="o in subsidyTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="核定方式">
          <el-select v-model="queryParams.determinationType" clearable placeholder="全部">
            <el-option label="正常发放" value="normal" />
            <el-option label="二次发放" value="second" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="queryParams.approvalStatus" clearable placeholder="全部">
            <el-option label="草稿" value="draft" />
            <el-option label="待复核" value="pending_review" />
            <el-option label="待审批" value="pending_approve" />
            <el-option label="审批通过" value="approved" />
            <el-option label="复核驳回" value="review_rejected" />
            <el-option label="审批驳回" value="approve_rejected" />
            <el-option label="财务驳回" value="finance_rejected" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="mini" @click="handleQuery">搜索</el-button>
          <el-button size="mini" @click="resetQuery">重置</el-button>
          <el-button type="success" size="mini" @click="openAddDialog" v-hasPermi="['shebao:payment:plan:generate']">添加</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="dataList" border>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column label="批次号" prop="batchNo" width="130" show-overflow-tooltip />
        <el-table-column label="业务期" prop="businessPeriod" width="100" />
        <el-table-column label="补贴类型" prop="subsidyType" width="120">
          <template slot-scope="scope">{{ subsidyTypeText(scope.row.subsidyType) }}</template>
        </el-table-column>
        <el-table-column label="核定方式" prop="determinationType" width="100">
          <template slot-scope="scope">{{ determinationTypeText(scope.row.determinationType) }}</template>
        </el-table-column>
        <el-table-column label="发放人次" prop="totalCount" width="100" />
        <el-table-column label="总金额" prop="totalAmount" width="120" />
        <el-table-column label="经办人" prop="operatorName" width="100" />
        <el-table-column label="经办时间" prop="operatorTime" width="170" />
        <el-table-column label="审批状态" prop="approvalStatus" width="120">
          <template slot-scope="scope">{{ approvalStatusText(scope.row.approvalStatus) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="openDetailDialog(scope.row)">详情</el-button>
            <el-button
              v-if="canSubmit(scope.row.approvalStatus)"
              type="text"
              size="mini"
              @click="handleSubmit(scope.row)"
            >提交</el-button>
            <el-button
              v-if="canRevoke(scope.row)"
              type="text"
              size="mini"
              @click="handleWithdraw(scope.row)"
            >撤销</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog title="生成支付计划" :visible.sync="addOpen" width="1200px" :close-on-click-modal="false">
      <el-form :model="addForm" inline label-width="90px" class="mb10">
        <el-form-item label="业务期">
          <el-date-picker v-model="addForm.businessPeriod" type="month" value-format="yyyy-MM" disabled />
        </el-form-item>
        <el-form-item label="经办人">
          <el-input v-model="addForm.operatorName" disabled />
        </el-form-item>
        <el-form-item label="经办时间">
          <el-input v-model="addForm.operatorTime" disabled />
        </el-form-item>
        <el-form-item label="补贴类型">
          <el-select v-model="addForm.subsidyType" placeholder="请选择补贴类型" @change="handleRecalculate">
            <el-option v-for="o in subsidyTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="核定方式">
          <el-select v-model="addForm.determinationType" @change="handleRecalculate">
            <el-option label="正常发放" value="normal" />
            <el-option label="二次发放" value="second" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-tabs v-model="addTabName">
        <el-tab-pane label="汇总表" name="summary">
          <el-table v-loading="previewLoading" :data="preview.summaryList" border>
            <el-table-column label="业务期" prop="businessPeriod" width="100" />
            <el-table-column label="补贴类型" prop="subsidyType">
              <template slot-scope="scope">{{ subsidyTypeText(scope.row.subsidyType) }}</template>
            </el-table-column>
            <el-table-column label="发放机构" prop="grantOrg">
              <template slot-scope="scope">{{ grantOrgLabel(scope.row.grantOrg) }}</template>
            </el-table-column>
            <el-table-column label="发放人次" prop="totalCount" width="100" />
            <el-table-column label="总金额" prop="totalAmount" width="120" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="明细表" name="detail">
          <el-table v-loading="previewLoading" :data="preview.detailList" border height="360">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column label="补贴类型" prop="subsidyType" width="120">
              <template slot-scope="scope">{{ subsidyTypeText(scope.row.subsidyType) }}</template>
            </el-table-column>
            <el-table-column label="街道" prop="streetName" width="120" />
            <el-table-column label="村委会" prop="villageName" width="120" />
            <el-table-column label="姓名" prop="personName" width="100" />
            <el-table-column label="身份证号" prop="idCardNo" width="180" />
            <el-table-column label="业务期" prop="businessPeriod" width="100" />
            <el-table-column label="补发起始" prop="supplementStartMonth" width="100">
              <template slot-scope="scope">{{ scope.row.supplementStartMonth || '-' }}</template>
            </el-table-column>
            <el-table-column label="补发终止" prop="supplementEndMonth" width="100">
              <template slot-scope="scope">{{ scope.row.supplementEndMonth || '-' }}</template>
            </el-table-column>
            <el-table-column label="发放金额" prop="distributionAmount" width="100" />
            <el-table-column label="发放机构" prop="grantOrg" width="120">
              <template slot-scope="scope">{{ grantOrgLabel(scope.row.grantOrg) }}</template>
            </el-table-column>
            <el-table-column label="开户名" prop="accountName" width="100" />
            <el-table-column label="银行账号" prop="bankAccount" width="180" />
            <el-table-column label="与参保人关系" prop="relationToInsured" width="120" />
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <div slot="footer">
        <el-button type="primary" :loading="saveLoading" @click="handleSave">保存</el-button>
        <el-button type="success" :loading="submitLoading" @click="handleSubmitFromAdd">提交</el-button>
        <el-button @click="handleRecalculate">重算</el-button>
        <el-button @click="addOpen=false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="支付计划详情" :visible.sync="detailOpen" width="1200px">
      <div v-if="currentPlan" class="plan-batch-meta">批次号：{{ planDetailBatchNo }}</div>
      <el-tabs v-model="detailTabName" @tab-click="loadDetailTab">
        <el-tab-pane label="汇总表" name="summary">
          <el-table :data="detailSummaryList" border>
            <el-table-column label="业务期" prop="businessPeriod" width="100" />
            <el-table-column label="补贴类型" prop="subsidyType">
              <template slot-scope="scope">{{ subsidyTypeText(scope.row.subsidyType) }}</template>
            </el-table-column>
            <el-table-column label="发放机构" prop="grantOrg">
              <template slot-scope="scope">{{ grantOrgLabel(scope.row.grantOrg) }}</template>
            </el-table-column>
            <el-table-column label="发放人次" prop="totalCount" width="100" />
            <el-table-column label="总金额" prop="totalAmount" width="120" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="明细表" name="detail">
          <el-table v-loading="detailLoading" :data="detailDataList" border height="360">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column label="补贴类型" prop="subsidyType" width="120">
              <template slot-scope="scope">{{ subsidyTypeText(scope.row.subsidyType) }}</template>
            </el-table-column>
            <el-table-column label="街道" prop="streetName" width="120" />
            <el-table-column label="村委会" prop="villageName" width="120" />
            <el-table-column label="姓名" prop="personName" width="100" />
            <el-table-column label="身份证号" prop="idCardNo" width="180" />
            <el-table-column label="业务期" prop="businessPeriod" width="100" />
            <el-table-column label="补发起始" prop="supplementStartMonth" width="100">
              <template slot-scope="scope">{{ scope.row.supplementStartMonth || '-' }}</template>
            </el-table-column>
            <el-table-column label="补发终止" prop="supplementEndMonth" width="100">
              <template slot-scope="scope">{{ scope.row.supplementEndMonth || '-' }}</template>
            </el-table-column>
            <el-table-column label="发放金额" prop="distributionAmount" width="100" />
            <el-table-column label="发放机构" prop="grantOrg" width="120">
              <template slot-scope="scope">{{ grantOrgLabel(scope.row.grantOrg) }}</template>
            </el-table-column>
            <el-table-column label="开户名" prop="accountName" width="100" />
            <el-table-column label="银行账号" prop="bankAccount" width="180" />
            <el-table-column label="与参保人关系" prop="relationToInsured" width="120" />
          </el-table>
          <pagination v-show="detailTotal>0" :total="detailTotal" :page.sync="detailQuery.pageNum" :limit.sync="detailQuery.pageSize" @pagination="loadDetailData" />
        </el-tab-pane>
        <el-tab-pane label="审核记录" name="audit">
          <el-table :data="detailAuditList" border>
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
      <div slot="footer" v-if="canEditInDetail">
        <el-button type="primary" :loading="saveLoading" @click="handleSaveFromDetail">保存</el-button>
        <el-button type="success" :loading="submitLoading" @click="handleSubmitFromDetail">提交</el-button>
        <el-button @click="handleRecalculateDetail">重算</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listPaymentPlan,
  previewPaymentPlan,
  savePaymentPlan,
  getPaymentPlanSummary,
  getPaymentPlanDetail,
  getPaymentPlanAudit,
  changePaymentPlanStatus,
  revokePaymentPlan
} from '@/api/shebao/payment'
import { selectDictLabel } from '@/utils/ruoyi'
import {
  paymentPlanStatusLabel,
  promptPlanAction,
  paymentPlanAuditOperationLabel,
  paymentPlanAuditStageLabelFromRow,
  paymentPlanSubsidyTypeLabel,
  PAYMENT_PLAN_SUBSIDY_TYPE_OPTIONS
} from './planUiShared'

function defaultBusinessPeriod() {
  const now = new Date()
  const y = now.getFullYear()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  return `${y}-${m}`
}

export default {
  name: 'PaymentPlan',
  dicts: ['shebao_grant_org'],
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      previewLoading: false,
      saveLoading: false,
      submitLoading: false,
      addOpen: false,
      detailOpen: false,
      addTabName: 'summary',
      detailTabName: 'summary',
      currentPlanId: null,
      preview: {
        summaryList: [],
        detailList: []
      },
      detailSummaryList: [],
      detailDataList: [],
      detailAuditList: [],
      detailLoading: false,
      detailTotal: 0,
      currentPlan: null,
      addForm: {
        businessPeriod: '',
        operatorName: '',
        operatorTime: '',
        subsidyType: '',
        determinationType: 'normal'
      },
      detailQuery: {
        pageNum: 1,
        pageSize: 10
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        determinationType: null,
        businessPeriod: defaultBusinessPeriod(),
        subsidyType: null,
        approvalStatus: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPaymentPlan(this.queryParams).then(response => {
        this.dataList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.determinationType = null
      this.queryParams.businessPeriod = defaultBusinessPeriod()
      this.queryParams.subsidyType = null
      this.queryParams.approvalStatus = null
      this.handleQuery()
    },
    openAddDialog() {
      const now = new Date()
      const y = now.getFullYear()
      const m = String(now.getMonth() + 1).padStart(2, '0')
      this.addForm.businessPeriod = `${y}-${m}`
      this.addForm.operatorName = this.$store.getters.nickName || this.$store.getters.name || ''
      this.addForm.operatorTime = this.parseTime(now, '{y}-{m}-{d} {h}:{i}:{s}')
      this.addForm.subsidyType = ''
      this.addForm.determinationType = 'normal'
      this.addTabName = 'summary'
      this.addOpen = true
      this.preview = { summaryList: [], detailList: [] }
    },
    handleRecalculate() {
      if (!this.addForm.subsidyType) {
        this.$modal.msgWarning('请先选择补贴类型')
        return
      }
      this.previewLoading = true
      previewPaymentPlan({
        determinationType: this.addForm.determinationType,
        businessPeriod: this.addForm.businessPeriod,
        subsidyType: this.addForm.subsidyType
      }).then(response => {
        this.preview = response.data || { summaryList: [], detailList: [] }
        if (this.preview.operatorName) {
          this.addForm.operatorName = this.preview.operatorName
        }
        if (this.preview.operatorTime) {
          this.addForm.operatorTime = this.preview.operatorTime
        }
      }).finally(() => {
        this.previewLoading = false
      })
    },
    handleSave() {
      if (!this.addForm.subsidyType) {
        this.$modal.msgWarning('请先选择补贴类型')
        return
      }
      this.saveLoading = true
      savePaymentPlan({
        determinationType: this.addForm.determinationType,
        businessPeriod: this.addForm.businessPeriod,
        subsidyType: this.addForm.subsidyType,
        targetStatus: 'draft'
      }).then(response => {
        this.$modal.msgSuccess('保存成功，计划ID：' + response.data)
        this.addOpen = false
        this.getList()
      }).finally(() => {
        this.saveLoading = false
      })
    },
    handleSubmitFromAdd() {
      if (!this.addForm.subsidyType) {
        this.$modal.msgWarning('请先选择补贴类型')
        return
      }
      promptPlanAction(this, 'pending_review').then((value) => {
        this.submitLoading = true
        return savePaymentPlan({
          determinationType: this.addForm.determinationType,
          businessPeriod: this.addForm.businessPeriod,
          subsidyType: this.addForm.subsidyType,
          targetStatus: 'pending_review',
          remark: value
        })
      }).then(response => {
        this.$modal.msgSuccess('提交成功，计划ID：' + response.data)
        this.addOpen = false
        this.getList()
      }).finally(() => {
        this.submitLoading = false
      })
    },
    openDetailDialog(row) {
      this.currentPlanId = row.id
      this.currentPlan = { ...row }
      this.detailOpen = true
      this.detailTabName = 'summary'
      this.detailQuery.pageNum = 1
      this.detailAuditList = []
      this.loadSummaryData()
    },
    loadDetailTab(tab) {
      if (tab.name === 'summary') {
        this.loadSummaryData()
      } else if (tab.name === 'detail') {
        this.loadDetailData()
      } else {
        this.loadAuditData()
      }
    },
    loadSummaryData() {
      getPaymentPlanSummary(this.currentPlanId).then(response => {
        this.detailSummaryList = response.data || []
      })
    },
    loadDetailData() {
      this.detailLoading = true
      getPaymentPlanDetail(this.currentPlanId, this.detailQuery).then(response => {
        this.detailDataList = response.rows || []
        this.detailTotal = response.total || 0
      }).finally(() => {
        this.detailLoading = false
      })
    },
    loadAuditData() {
      getPaymentPlanAudit(this.currentPlanId).then(response => {
        this.detailAuditList = response.data || []
      })
    },
    handleSubmit(row) {
      promptPlanAction(this, 'pending_review').then((value) => {
        return changePaymentPlanStatus(row.id, { targetStatus: 'pending_review', remark: value })
      }).then(() => {
        this.$modal.msgSuccess('提交成功')
        this.getList()
      })
    },
    handleWithdraw(row) {
      this.$modal.confirm('确认撤销该支付计划？撤销后将删除计划及明细，需重新生成并提交。').then(() => {
        return revokePaymentPlan(row.id)
      }).then(() => {
        this.$modal.msgSuccess('撤销成功')
        this.getList()
      }).catch(() => {})
    },
    handleSaveFromDetail() {
      this.saveLoading = true
      savePaymentPlan({
        planId: this.currentPlanId,
        determinationType: this.currentPlan.determinationType,
        businessPeriod: this.currentPlan.businessPeriod,
        subsidyType: this.currentPlan.subsidyType,
        targetStatus: 'draft'
      }).then(() => {
        this.$modal.msgSuccess('保存成功')
        this.currentPlan.approvalStatus = 'draft'
        this.currentPlan.financeStatus = null
        this.getList()
        this.loadSummaryData()
      }).finally(() => {
        this.saveLoading = false
      })
    },
    handleSubmitFromDetail() {
      promptPlanAction(this, 'pending_review').then((value) => {
        this.submitLoading = true
        return savePaymentPlan({
          planId: this.currentPlanId,
          determinationType: this.currentPlan.determinationType,
          businessPeriod: this.currentPlan.businessPeriod,
          subsidyType: this.currentPlan.subsidyType,
          targetStatus: 'pending_review',
          remark: value
        })
      }).then(() => {
        this.$modal.msgSuccess('提交成功')
        this.currentPlan.approvalStatus = 'pending_review'
        this.currentPlan.financeStatus = null
        this.getList()
        this.loadSummaryData()
        this.loadAuditData()
      }).finally(() => {
        this.submitLoading = false
      })
    },
    handleRecalculateDetail() {
      this.detailLoading = true
      previewPaymentPlan({
        determinationType: this.currentPlan.determinationType,
        businessPeriod: this.currentPlan.businessPeriod,
        subsidyType: this.currentPlan.subsidyType,
        excludePlanId: this.currentPlanId
      }).then(response => {
        const previewData = response.data || { summaryList: [], detailList: [] }
        this.detailSummaryList = previewData.summaryList || []
        this.detailDataList = previewData.detailList || []
        this.detailTotal = this.detailDataList.length
      }).finally(() => {
        this.detailLoading = false
      })
    },
    canSubmit(status) {
      return ['draft', 'review_rejected', 'approve_rejected', 'finance_rejected'].includes(status)
    },
    canEditStatus(status) {
      return ['draft', 'pending_review', 'review_rejected', 'approve_rejected', 'finance_rejected'].includes(status)
    },
    isFinanceApproved(plan) {
      if (!plan) return false
      const fs = plan.financeStatus || plan.finance_status
      return fs === 'finance_approved'
    },
    canRevoke(row) {
      if (!row) return false
      const status = row.approvalStatus
      if (['draft', 'review_rejected', 'approve_rejected', 'finance_rejected'].includes(status)) {
        return true
      }
      if (status === 'pending_review') {
        const fs = row.financeStatus || row.finance_status
        return !fs
      }
      return false
    },
    determinationTypeText(val) {
      return val === 'second' ? '二次发放' : '正常发放'
    },
    approvalStatusText(val) {
      return paymentPlanStatusLabel(val)
    },
    paymentPlanAuditOperationLabel,
    paymentPlanAuditStageLabelFromRow,
    subsidyTypeText(val) {
      return paymentPlanSubsidyTypeLabel(val)
    },
    grantOrgLabel(val) {
      return selectDictLabel(this.dict.type.shebao_grant_org || [], val) || val || '-'
    }
  },
  computed: {
    subsidyTypeOptions() {
      return PAYMENT_PLAN_SUBSIDY_TYPE_OPTIONS
    },
    canEditInDetail() {
      return this.currentPlan
        && this.canEditStatus(this.currentPlan.approvalStatus)
        && !this.isFinanceApproved(this.currentPlan)
    },
    planDetailBatchNo() {
      if (!this.currentPlan) return '—'
      return this.currentPlan.batchNo || this.currentPlan.batch_no || '—'
    }
  }
}
</script>

<style scoped>
.mb20 {
  margin-bottom: 20px;
}
.plan-batch-meta {
  margin-bottom: 12px;
  font-size: 13px;
  color: #606266;
}
</style>

