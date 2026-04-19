<template>
  <div class="app-container" data-testid="payment-plan-page">
    <!-- 流程步骤指引 -->
    <el-alert
      title="支付计划生成流程"
      type="info"
      :closable="false"
      class="mb20">
      <div class="step-guide">
        <el-steps :active="currentStep" finish-status="success" simple>
          <el-step title="1. 设置生成条件" />
          <el-step title="2. 预览统计" />
          <el-step title="3. 生成支付计划" />
          <el-step title="4. 创建批次" />
        </el-steps>
      </div>
    </el-alert>

    <!-- 生成条件 -->
    <el-card class="box-card mb20">
      <div slot="header">
        <span><i class="el-icon-setting"></i> 生成条件设置</span>
        <el-tag type="info" size="mini" class="ml10">步骤1</el-tag>
      </div>
      <el-form :model="generateForm" ref="generateForm" label-width="120px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="补贴类型" prop="subsidyType" required>
              <el-select v-model="generateForm.subsidyType" placeholder="请选择补贴类型" data-testid="payment-plan-subsidy-type" style="width: 100%">
                <el-option label="失地居民" value="1" />
                <el-option label="被征地居民" value="2" />
                <el-option label="拆迁居民" value="3" />
                <el-option label="村干部" value="4" />
                <el-option label="教师" value="5" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="发放月份" prop="paymentMonth" required>
              <el-date-picker v-model="generateForm.paymentMonth" type="month" placeholder="选择发放月份" value-format="yyyy-MM" data-testid="payment-plan-payment-month" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="街道办事处">
              <el-select v-model="generateForm.streetOfficeId" placeholder="全部（可留空）" clearable data-testid="payment-plan-street-office" style="width: 100%">
                <el-option v-for="item in streetOfficeList" :key="item.id" :label="item.streetName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-divider content-position="left">按条件批量生成</el-divider>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <div class="generate-methods">
              <div class="method-item">
                <el-card shadow="hover">
                  <div slot="header" class="clearfix">
                    <span>按条件批量生成</span>
                  </div>
                  <p class="tip-text">根据上方条件筛选符合发放条件的人员，批量生成支付计划</p>
                  <el-button type="primary" icon="el-icon-view" data-testid="payment-plan-preview" @click="handlePreview" :loading="previewLoading">
                    <span class="btn-text">预览统计</span>
                  </el-button>
                  <el-button type="success" icon="el-icon-plus" data-testid="payment-plan-generate" @click="handleGenerate" :disabled="!previewDataReady" :loading="generateLoading">
                    <span class="btn-text">生成支付计划</span>
                  </el-button>
                  <div v-if="previewDataReady" class="preview-summary">
                    <el-tag type="success" size="small">已预览：{{ statistics.totalCount }}人，{{ statistics.totalAmount }}元</el-tag>
                  </div>
                </el-card>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 查询条件 -->
    <el-card class="box-card mb20">
      <div slot="header">
        <span><i class="el-icon-search"></i> 已生成支付计划查询</span>
        <el-tag type="info" size="mini" class="ml10">步骤3-4</el-tag>
      </div>
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable data-testid="payment-plan-query-name" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCardNo">
          <el-input v-model="queryParams.idCardNo" placeholder="请输入身份证号" clearable />
        </el-form-item>
        <el-form-item label="补贴类型" prop="subsidyType">
          <el-select v-model="queryParams.subsidyType" placeholder="全部" clearable>
            <el-option label="失地居民" value="1" />
            <el-option label="被征地居民" value="2" />
            <el-option label="拆迁居民" value="3" />
            <el-option label="村干部" value="4" />
            <el-option label="教师" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="发放月份" prop="paymentMonth">
          <el-date-picker v-model="queryParams.paymentMonth" type="month" placeholder="选择月份" value-format="yyyy-MM" data-testid="payment-plan-query-month" />
        </el-form-item>
        <el-form-item label="批次状态" prop="batchStatus">
          <el-select v-model="queryParams.batchStatus" placeholder="全部" clearable>
            <el-option label="已入批次" value="in_batch" />
            <el-option label="未入批次" value="not_in_batch" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery" data-testid="payment-plan-query-search">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery" data-testid="payment-plan-query-reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-finished" size="mini" data-testid="payment-plan-create-batch" @click="handleCreateBatch" :disabled="!hasSelected" v-hasPermi="['shebao:payment:batch:create']">
            创建批次 ({{ selectedIds.length }})
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="el-icon-delete" size="mini" @click="handleDelete" :disabled="!hasSelected" v-hasPermi="['shebao:payment:plan:remove']">
            删除 ({{ selectedIds.length }})
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="el-icon-upload2" size="mini" @click="handleExport" :disabled="total === 0">
            导出
          </el-button>
        </el-col>
      </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange" data-testid="payment-plan-table" border>
      <el-table-column type="selection" width="55" fixed="left" />
      <el-table-column type="index" label="序号" width="60" fixed="left" />
      <el-table-column label="姓名" prop="name" width="100" show-overflow-tooltip />
      <el-table-column label="身份证号" prop="idCardNo" width="180" show-overflow-tooltip />
      <el-table-column label="补贴类型" prop="subsidyType" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.subsidy_type" :value="scope.row.subsidyType"/>
        </template>
      </el-table-column>
      <el-table-column label="发放月份" prop="paymentMonth" width="100" />
      <el-table-column label="应发金额(元)" prop="distributionAmount" width="120" align="right">
        <template slot-scope="scope">
          {{ scope.row.distributionAmount ? scope.row.distributionAmount.toFixed(2) : '0.00' }}
        </template>
      </el-table-column>
      <el-table-column label="开户行" prop="bankName" width="150" show-overflow-tooltip />
      <el-table-column label="银行账号" prop="bankAccountNo" width="180" show-overflow-tooltip />
      <el-table-column label="批次号" prop="batchNo" width="150" show-overflow-tooltip />
      <el-table-column label="批次状态" prop="approvalStatus" width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.batchNo" size="small">
            {{ getBatchStatusText(scope.row.approvalStatus) }}
          </el-tag>
          <el-tag type="info" size="small" v-else>未入批次</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="银行结果" prop="bankResultStatus" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.bankResultStatus === 'success'" type="success" size="small">成功</el-tag>
          <el-tag v-else-if="scope.row.bankResultStatus === 'failed'" type="danger" size="small">失败</el-tag>
          <el-tag v-else-if="scope.row.bankResultStatus" type="info" size="small">{{ scope.row.bankResultStatus }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" />
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </el-card>

    <!-- 统计预览对话框 -->
    <el-dialog title="支付计划统计预览" :visible.sync="statisticsOpen" width="700px" data-testid="payment-plan-statistics-dialog" :close-on-click-modal="false">
      <div v-if="statistics.totalCount !== undefined">
        <el-alert
          title="统计信息"
          type="success"
          :closable="false"
          class="mb20">
          <div class="statistics-summary">
            <span class="summary-item">总计：<strong>{{ statistics.totalCount }}</strong> 人</span>
            <span class="summary-divider">|</span>
            <span class="summary-item">总金额：<strong class="text-primary">{{ statistics.totalAmount }}</strong> 元</span>
          </div>
        </el-alert>

        <el-divider content-position="left">按补贴类型分布</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="失地居民">
            <span class="count-text">{{ statistics.landLossCount || 0 }}</span> 人
          </el-descriptions-item>
          <el-descriptions-item label="被征地居民">
            <span class="count-text">{{ statistics.expropriateeCount || 0 }}</span> 人
          </el-descriptions-item>
          <el-descriptions-item label="拆迁居民">
            <span class="count-text">{{ statistics.demolitionCount || 0 }}</span> 人
          </el-descriptions-item>
          <el-descriptions-item label="村干部">
            <span class="count-text">{{ statistics.officialCount || 0 }}</span> 人
          </el-descriptions-item>
          <el-descriptions-item label="教师">
            <span class="count-text">{{ statistics.teacherCount || 0 }}</span> 人
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">按街道分布</el-divider>
        <div v-if="statistics.streetDistribution && statistics.streetDistribution.length > 0">
          <el-table :data="statistics.streetDistribution" border size="small">
            <el-table-column prop="streetName" label="街道" width="200" />
            <el-table-column prop="count" label="人数" width="100" align="center" />
            <el-table-column prop="amount" label="金额" align="right" />
          </el-table>
        </div>
        <div v-else class="text-center text-gray">
          暂无街道分布数据
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="statisticsOpen = false">关 闭</el-button>
        <el-button type="primary" @click="handleGenerateFromPreview" :loading="generateLoading">
          <i class="el-icon-plus"></i> 确认生成支付计划
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPaymentPlan, generatePaymentPlan, deletePaymentPlan, getPaymentStatistics, createBatch } from '@/api/shebao/payment'
import { listStreetOffice } from '@/api/shebao/streetOffice'

export default {
  name: 'PaymentPlan',
  dicts: ['subsidy_type'],
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      selectedIds: [],
      hasSelected: false,
      statisticsOpen: false,
      statistics: {},
      streetOfficeList: [],
      previewLoading: false,
      generateLoading: false,
      previewDataReady: false,
      currentStep: 0,
      generateForm: {
        subsidyType: null,
        paymentMonth: null,
        streetOfficeId: null
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        idCardNo: null,
        subsidyType: null,
        paymentMonth: null,
        batchStatus: null
      }
    }
  },
  created() {
    this.getList()
    this.getStreetOfficeList()
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
    getStreetOfficeList() {
      listStreetOffice().then(response => {
        this.streetOfficeList = response.data
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
    validateGenerateForm() {
      if (!this.generateForm.subsidyType) {
        this.$modal.msgWarning('请选择补贴类型')
        return false
      }
      if (!this.generateForm.paymentMonth) {
        this.$modal.msgWarning('请选择发放月份')
        return false
      }
      return true
    },
    handlePreview() {
      if (!this.validateGenerateForm()) {
        return
      }
      this.previewLoading = true
      getPaymentStatistics(this.generateForm).then(response => {
        this.statistics = response.data
        this.statisticsOpen = true
        this.currentStep = 1
        this.previewDataReady = true
        this.$modal.msgSuccess('预览成功，共 ' + this.statistics.totalCount + ' 人')
      }).catch(() => {
        this.currentStep = 0
      }).finally(() => {
        this.previewLoading = false
      })
    },
    handleGenerateFromPreview() {
      this.statisticsOpen = false
      this.handleGenerate()
    },
    handleGenerate() {
      if (!this.validateGenerateForm()) {
        return
      }
      this.$modal.confirm('是否确认生成支付计划？生成后将创建支付记录，请确认条件无误。', '确认生成', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.generateLoading = true
        return generatePaymentPlan(this.generateForm)
      }).then(response => {
        const count = response.data || 0
        this.$modal.msgSuccess(`支付计划生成成功，共生成 ${count} 条记录`)
        this.currentStep = 2
        this.previewDataReady = false
        this.getList()
        this.resetGenerateForm()
      }).catch(error => {
        if (error.msg) {
          this.$modal.msgError(error.msg)
        }
        this.currentStep = 1
      }).finally(() => {
        this.generateLoading = false
      })
    },
    resetGenerateForm() {
      this.generateForm.subsidyType = null
      this.generateForm.paymentMonth = null
      this.generateForm.streetOfficeId = null
      this.currentStep = 0
      this.previewDataReady = false
    },
    handleSelectionChange(selection) {
      this.selectedIds = selection.map(item => item.id)
      this.hasSelected = selection.length > 0
      if (selection.length > 0) {
        this.currentStep = 3
      }
    },
    handleCreateBatch() {
      if (this.selectedIds.length === 0) {
        this.$modal.msgWarning('请选择要创建批次的记录')
        return
      }
      this.$modal.confirm(`是否确认创建发放批次？共 ${this.selectedIds.length} 条记录`).then(() => {
        this.generateLoading = true
        return createBatch({ planIds: this.selectedIds })
      }).then(() => {
        this.$modal.msgSuccess('批次创建成功，已进入待复核状态')
        this.selectedIds = []
        this.hasSelected = false
        this.getList()
        this.currentStep = 2
      }).finally(() => {
        this.generateLoading = false
      })
    },
    handleDelete() {
      if (this.selectedIds.length === 0) {
        this.$modal.msgWarning('请选择要删除的记录')
        return
      }
      this.$modal.confirm(`是否确认删除选中的 ${this.selectedIds.length} 条支付计划？`).then(() => {
        return deletePaymentPlan(this.selectedIds.join(','))
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.selectedIds = []
        this.hasSelected = false
        this.getList()
      })
    },
    handleExport() {
      this.$modal.msgInfo('导出功能开发中...')
    },
    getBatchStatusText(status) {
      const statusMap = {
        'draft': '草稿',
        'pending_review': '待复核',
        'pending_approve': '待审批',
        'pending_finance': '待财务',
        'submitted_bank': '已提交银行',
        'distributed': '已发放',
        'partial_failed': '部分失败',
        'completed': '已完成',
        'rejected': '已驳回'
      }
      return statusMap[status] || status
    }
  }
}
</script>

<style scoped>
.step-guide {
  margin-top: 10px;
}

.generate-methods {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.method-item {
  flex: 1;
}

.tip-text {
  color: #909399;
  font-size: 13px;
  margin: 10px 0;
  line-height: 1.6;
}

.btn-text {
  margin-left: 5px;
}

.preview-summary {
  margin-top: 15px;
}

.statistics-summary {
  font-size: 16px;
}

.summary-item {
  margin: 0 10px;
}

.summary-divider {
  color: #DCDFE6;
  margin: 0 5px;
}

.count-text {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.text-primary {
  color: #409EFF;
}

.text-center {
  text-align: center;
}

.text-gray {
  color: #909399;
}

.mb10 {
  margin-bottom: 10px;
}

.ml10 {
  margin-left: 10px;
}

.mb20 {
  margin-bottom: 20px;
}
</style>

