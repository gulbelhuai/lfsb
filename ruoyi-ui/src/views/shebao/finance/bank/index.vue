<template>
  <div class="app-container">
    <el-alert title="银行发放说明" type="info" :closable="false" class="mb20">
      <div>导出银行代发文件（本批次全部明细）→ 已提交银行 → 导入失败数据 → 确认无误后标记「已完成」。</div>
    </el-alert>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="业务期">
        <el-date-picker v-model="queryParams.businessPeriod" type="month" value-format="yyyy-MM" placeholder="选择业务期" />
      </el-form-item>
      <el-form-item label="补贴类型">
        <el-select v-model="queryParams.subsidyType" clearable placeholder="全部">
          <el-option v-for="o in subsidyTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="发放状态">
        <el-select v-model="queryParams.distributionStatus" clearable placeholder="全部">
          <el-option label="待发放" value="pending" />
          <el-option label="已提交银行" value="submitted" />
          <el-option label="已完成" value="completed" />
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
      <el-table-column label="批次号" prop="batchNo" width="130" show-overflow-tooltip />
      <el-table-column label="业务期" prop="businessPeriod" width="100" />
      <el-table-column label="补贴类型" prop="subsidyType" width="120">
        <template slot-scope="scope">{{ subsidyTypeLabel(scope.row.subsidyType) }}</template>
      </el-table-column>
      <el-table-column label="核定方式" prop="determinationType" width="100">
        <template slot-scope="scope">{{ determinationTypeText(scope.row.determinationType) }}</template>
      </el-table-column>
      <el-table-column label="发放人次" prop="totalCount" width="90" />
      <el-table-column label="总金额" prop="totalAmount" width="110" />
      <el-table-column label="经办人" prop="operatorName" width="100" />
      <el-table-column label="发放状态" prop="distributionStatus" width="110">
        <template slot-scope="scope">
          <el-tag :type="distTagType(scope.row.distributionStatus)">{{ distributionStatusLabel(scope.row.distributionStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="340" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="openDetail(scope.row)">详情</el-button>
          <el-button
            type="text"
            size="mini"
            v-hasPermi="['shebao:finance:bank:export']"
            @click="handleExport(scope.row)"
          >银行导出</el-button>
          <el-button
            v-if="distOf(scope.row) === 'pending'"
            type="text"
            size="mini"
            v-hasPermi="['shebao:finance:bank:submit']"
            @click="handleSubmitBank(scope.row)"
          >已提交银行</el-button>
          <el-button
            v-if="distOf(scope.row) === 'submitted'"
            type="text"
            size="mini"
            v-hasPermi="['shebao:finance:bank:importFail']"
            @click="handleImportFail(scope.row)"
          >导入失败数据</el-button>
          <el-button
            v-if="distOf(scope.row) === 'submitted'"
            type="text"
            size="mini"
            v-hasPermi="['shebao:finance:bank:complete']"
            @click="handleComplete(scope.row)"
          >已完成</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <plan-detail-dialog
      :visible.sync="detailOpen"
      :current-plan="currentPlan"
      :grant-org-options="grantOrgOptions"
      :subsidy-type-formatter="subsidyTypeLabel"
      :status-formatter="distributionStatusLabel"
      status-field="distributionStatus"
      :show-distribution-result="true"
      :fetch-summary="fetchSummary"
      :fetch-detail="fetchDetail"
      :fetch-audit="fetchAudit"
    />

    <!-- 导入失败数据 -->
    <el-dialog title="导入发放失败数据" :visible.sync="importOpen" width="500px">
      <el-alert type="warning" :closable="false" class="mb10">
        <div>请上传包含「身份证号」「失败原因」列的 Excel 文件，匹配到的明细将记为发放失败。</div>
      </el-alert>
      <div class="mb10">
        <el-button type="text" icon="el-icon-download" @click="handleDownloadTemplate">下载导入模板</el-button>
      </div>
      <el-upload
        drag
        :action="uploadUrl"
        :headers="uploadHeaders"
        name="file"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        accept=".xlsx,.xls">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">支持 .xls / .xlsx</div>
      </el-upload>
      <div slot="footer">
        <el-button @click="importOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listBankBatch,
  exportBankFile,
  submitBankDistribution,
  completeBankDistribution,
  downloadFailTemplate
} from '@/api/shebao/finance'
import { getPaymentPlanSummary, getPaymentPlanDetail, getPaymentPlanAudit } from '@/api/shebao/payment'
import { listOpeningBankSelect } from '@/api/shebao/openingBank'
import {
  paymentPlanSubsidyTypeLabel,
  paymentPlanDistributionStatusLabel,
  PAYMENT_PLAN_SUBSIDY_TYPE_OPTIONS
} from '../../payment/plan/planUiShared'
import PlanDetailDialog from '../../payment/plan/PlanDetailDialog'
import { getToken } from '@/utils/auth'

function currentMonth() {
  const d = new Date()
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  return `${y}-${m}`
}

export default {
  name: 'FinanceBank',
  components: { PlanDetailDialog },
  data() {
    return {
      loading: false,
      total: 0,
      dataList: [],
      grantOrgOptions: [],
      detailOpen: false,
      currentPlan: null,
      importOpen: false,
      currentId: null,
      uploadHeaders: { Authorization: 'Bearer ' + getToken() },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        businessPeriod: currentMonth(),
        subsidyType: null,
        distributionStatus: null,
        batchNo: null
      }
    }
  },
  computed: {
    subsidyTypeOptions() {
      return PAYMENT_PLAN_SUBSIDY_TYPE_OPTIONS
    },
    uploadUrl() {
      return process.env.VUE_APP_BASE_API + `/shebao/finance/bank/${this.currentId}/import-fail`
    }
  },
  created() {
    this.loadGrantOrgOptions()
    this.getList()
  },
  methods: {
    loadGrantOrgOptions() {
      listOpeningBankSelect().then(res => {
        this.grantOrgOptions = res.data || []
      }).catch(() => { this.grantOrgOptions = [] })
    },
    getList() {
      this.loading = true
      listBankBatch(this.queryParams).then(res => {
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
        subsidyType: null,
        distributionStatus: null,
        batchNo: null
      }
      this.getList()
    },
    distOf(row) {
      return row.distributionStatus || 'pending'
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
    handleExport(row) {
      exportBankFile(row.id).then(data => {
        const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `银行代发_${row.batchNo || row.id}.xlsx`
        link.click()
        window.URL.revokeObjectURL(url)
      })
    },
    handleSubmitBank(row) {
      this.$modal.confirm('确认将该批次提交银行？提交后发放状态将变为「已提交银行」。').then(() => {
        return submitBankDistribution(row.id)
      }).then(() => {
        this.$modal.msgSuccess('提交成功')
        this.getList()
      })
    },
    handleImportFail(row) {
      this.currentId = row.id
      this.importOpen = true
    },
    handleDownloadTemplate() {
      downloadFailTemplate().then(data => {
        const blob = new Blob([data], { type: 'application/vnd.ms-excel' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '发放失败导入模板.xls'
        link.click()
        window.URL.revokeObjectURL(url)
      })
    },
    handleImportSuccess(response) {
      if (response.code !== 200) {
        this.$modal.msgError(response.msg || '导入失败')
        return
      }
      this.$modal.msgSuccess(response.msg || '导入成功')
      this.importOpen = false
      this.getList()
    },
    handleImportError() {
      this.$modal.msgError('导入失败')
    },
    handleComplete(row) {
      this.$modal.confirm('确认标记该批次已完成？该批次所有未失败的明细将记为发放成功。').then(() => {
        return completeBankDistribution(row.id)
      }).then(() => {
        this.$modal.msgSuccess('已标记完成')
        this.getList()
      })
    },
    determinationTypeText(val) {
      return val === 'second' ? '二次发放' : '正常发放'
    },
    subsidyTypeLabel(val) {
      return paymentPlanSubsidyTypeLabel(val)
    },
    distributionStatusLabel(val) {
      return paymentPlanDistributionStatusLabel(val)
    },
    distTagType(val) {
      const s = val || 'pending'
      if (s === 'completed') return 'success'
      if (s === 'submitted') return 'warning'
      return 'info'
    }
  }
}
</script>

<style scoped>
.mb20 {
  margin-bottom: 20px;
}
.mb10 {
  margin-bottom: 10px;
}
</style>
