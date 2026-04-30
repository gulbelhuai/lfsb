<template>
  <div class="app-container">
    <el-card class="mb20">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
        <el-form-item label="核定方式">
          <el-select v-model="queryParams.determinationType" clearable placeholder="全部">
            <el-option label="正常发放" value="normal" />
            <el-option label="二次发放" value="second" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务期">
          <el-date-picker v-model="queryParams.businessPeriod" type="month" value-format="yyyy-MM" placeholder="选择业务期" />
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="queryParams.approvalStatus" clearable placeholder="全部">
            <el-option label="待复核" value="pending_review" />
            <el-option label="待审批" value="pending_approve" />
            <el-option label="已驳回" value="rejected" />
            <el-option label="已完成" value="completed" />
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
        <el-table-column label="核定方式" prop="determinationType">
          <template slot-scope="scope">{{ determinationTypeText(scope.row.determinationType) }}</template>
        </el-table-column>
        <el-table-column label="业务期" prop="businessPeriod" width="100" />
        <el-table-column label="发放人次" prop="totalCount" width="100" />
        <el-table-column label="总金额" prop="totalAmount" width="120" />
        <el-table-column label="经办人" prop="operatorName" width="100" />
        <el-table-column label="经办时间" prop="operatorTime" width="170" />
        <el-table-column label="审批状态" prop="approvalStatus" width="120">
          <template slot-scope="scope">{{ approvalStatusText(scope.row.approvalStatus) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="openDetailDialog(scope.row)">详情</el-button>
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
            <el-table-column label="发放月份" prop="paymentMonth" width="100" />
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
        <el-button @click="handleRecalculate">重算</el-button>
        <el-button @click="addOpen=false">取消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="支付计划详情" :visible.sync="detailOpen" width="1200px">
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
            <el-table-column label="发放月份" prop="paymentMonth" width="100" />
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
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script>
import { listPaymentPlan, previewPaymentPlan, generatePaymentPlan, getPaymentPlanSummary, getPaymentPlanDetail } from '@/api/shebao/payment'
import { selectDictLabel } from '@/utils/ruoyi'

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
      detailLoading: false,
      detailTotal: 0,
      addForm: {
        businessPeriod: '',
        operatorName: '',
        operatorTime: '',
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
        businessPeriod: null,
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
      this.resetForm('queryForm')
      this.handleQuery()
    },
    openAddDialog() {
      const now = new Date()
      const y = now.getFullYear()
      const m = String(now.getMonth() + 1).padStart(2, '0')
      this.addForm.businessPeriod = `${y}-${m}`
      this.addForm.operatorName = this.$store.getters.nickName || this.$store.getters.name || ''
      this.addForm.operatorTime = this.parseTime(now, '{y}-{m}-{d} {h}:{i}:{s}')
      this.addForm.determinationType = 'normal'
      this.addTabName = 'summary'
      this.addOpen = true
      this.preview = { summaryList: [], detailList: [] }
      this.handleRecalculate()
    },
    handleRecalculate() {
      this.previewLoading = true
      previewPaymentPlan({
        determinationType: this.addForm.determinationType,
        businessPeriod: this.addForm.businessPeriod
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
      this.saveLoading = true
      generatePaymentPlan({
        determinationType: this.addForm.determinationType,
        businessPeriod: this.addForm.businessPeriod
      }).then(response => {
        this.$modal.msgSuccess('保存成功，计划ID：' + response.data)
        this.addOpen = false
        this.getList()
      }).finally(() => {
        this.saveLoading = false
      })
    },
    openDetailDialog(row) {
      this.currentPlanId = row.id
      this.detailOpen = true
      this.detailTabName = 'summary'
      this.detailQuery.pageNum = 1
      this.loadSummaryData()
    },
    loadDetailTab(tab) {
      if (tab.name === 'summary') {
        this.loadSummaryData()
      } else {
        this.loadDetailData()
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
    determinationTypeText(val) {
      return val === 'second' ? '二次发放' : '正常发放'
    },
    approvalStatusText(val) {
      const map = {
        pending_review: '待复核',
        pending_approve: '待审批',
        rejected: '已驳回',
        completed: '已完成'
      }
      return map[val] || val
    },
    subsidyTypeText(val) {
      const map = {
        land_loss: '失地',
        land_loss_resident: '失地',
        demolition: '拆迁',
        demolition_resident: '拆迁',
        village_official: '村干部'
      }
      return map[val] || val
    },
    grantOrgLabel(val) {
      return selectDictLabel(this.dict.type.shebao_grant_org || [], val) || val || '-'
    }
  }
}
</script>

<style scoped>
.mb20 {
  margin-bottom: 20px;
}
</style>

