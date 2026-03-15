<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="批次号" prop="batchNo">
        <el-input v-model="queryParams.batchNo" placeholder="请输入批次号" clearable />
      </el-form-item>
      <el-form-item label="补贴类型" prop="subsidyType">
        <el-select v-model="queryParams.subsidyType" placeholder="请选择" clearable>
          <el-option label="失地居民" value="1" />
          <el-option label="被征地居民" value="2" />
          <el-option label="拆迁居民" value="3" />
          <el-option label="村干部" value="4" />
          <el-option label="教师" value="5" />
        </el-select>
      </el-form-item>
      <el-form-item label="审批状态" prop="approvalStatus">
        <el-select v-model="queryParams.approvalStatus" placeholder="请选择" clearable>
          <el-option label="待复核" value="pending_review" />
          <el-option label="待审批" value="pending_approve" />
          <el-option label="已通过" value="approved" />
          <el-option label="待财务" value="pending_finance" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="批次号" prop="batchNo" width="160" />
      <el-table-column label="补贴类型" prop="subsidyType" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.subsidy_type" :value="scope.row.subsidyType"/>
        </template>
      </el-table-column>
      <el-table-column label="发放月份" prop="paymentMonth" width="100" />
      <el-table-column label="总人数" prop="totalCount" width="80" />
      <el-table-column label="总金额(元)" prop="totalAmount" width="120" />
      <el-table-column label="审批状态" align="center" width="100">
        <template slot-scope="scope">
          <approval-status :status="scope.row.approvalStatus" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="250">
        <template slot-scope="scope">
          <el-button size="mini" type="text" :data-testid="`finance-batch-view-${scope.row.batchNo}`" @click="handleView(scope.row)">详情</el-button>
          <el-button size="mini" type="text" :data-testid="`finance-batch-generate-${scope.row.batchNo}`" @click="handleGenerateFile(scope.row)" v-if="scope.row.approvalStatus === 'pending_finance'">生成银行文件</el-button>
          <el-button size="mini" type="text" :data-testid="`finance-batch-submit-${scope.row.batchNo}`" @click="handleSubmitBank(scope.row)" v-if="scope.row.approvalStatus === 'pending_finance'">提交银行</el-button>
          <el-button size="mini" type="text" :data-testid="`finance-batch-import-${scope.row.batchNo}`" @click="handleImportResult(scope.row)" v-if="scope.row.bankSubmitTime">导入结果</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 批次详情对话框 -->
    <el-dialog title="批次详情" :visible.sync="detailOpen" width="1200px">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="批次号">{{ detailData.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="补贴类型">
          <dict-tag :options="dict.type.subsidy_type" :value="detailData.subsidyType"/>
        </el-descriptions-item>
        <el-descriptions-item label="发放月份">{{ detailData.paymentMonth }}</el-descriptions-item>
        <el-descriptions-item label="总人数">{{ detailData.totalCount }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ detailData.totalAmount }}元</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <approval-status :status="detailData.approvalStatus" />
        </el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">发放明细</el-divider>
      <el-table :data="detailList" max-height="400">
        <el-table-column type="index" label="序号" width="50" />
        <el-table-column label="姓名" prop="name" width="100" />
        <el-table-column label="身份证号" prop="idCardNo" width="180" />
        <el-table-column label="应发金额" prop="distributionAmount" width="100" />
        <el-table-column label="银行账号" prop="bankAccountNo" width="180" />
        <el-table-column label="发放状态" prop="paymentStatus" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.paymentStatus === 'success'" type="success">成功</el-tag>
            <el-tag v-else-if="scope.row.paymentStatus === 'failed'" type="danger">失败</el-tag>
            <el-tag v-else type="info">待发放</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-divider content-position="left">审批历史</el-divider>
      <approval-history :history="approvalHistory" />
    </el-dialog>

    <!-- 导入结果对话框 -->
    <el-dialog title="导入发放结果" :visible.sync="importOpen" width="400px">
      <el-upload
        class="upload-demo"
        drag
        :action="uploadUrl"
        :headers="uploadHeaders"
        :data="{ batchNo: currentBatchNo }"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        accept=".xlsx,.xls">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">上传银行发放结果Excel文件</div>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script>
import { listBatch, getBatchDetail, generateBankFile, submitToBank } from '@/api/shebao/finance'
import { getApprovalHistory } from '@/api/shebao/approval'
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'
import ApprovalHistory from '@/components/Shebao/ApprovalHistory'
import { getToken } from '@/utils/auth'

export default {
  name: 'FinanceBatch',
  dicts: ['subsidy_type'],
  components: { ApprovalStatus, ApprovalHistory },
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      detailOpen: false,
      importOpen: false,
      detailData: {},
      detailList: [],
      approvalHistory: [],
      currentBatchNo: null,
      uploadUrl: process.env.VUE_APP_BASE_API + '/shebao/finance/bank/import',
      uploadHeaders: { Authorization: 'Bearer ' + getToken() },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        batchNo: null,
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
      listBatch(this.queryParams).then(response => {
        this.dataList = response.rows
        this.total = response.total
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
    handleView(row) {
      this.detailOpen = true
      // 获取批次详情
      getBatchDetail(row.batchNo).then(response => {
        this.detailData = response.data.batch || row
        this.detailList = response.data.details || []
      })
      // 获取审批历史
      getApprovalHistory('payment_batch', row.id).then(response => {
        this.approvalHistory = response.data
      })
    },
    handleGenerateFile(row) {
      this.$modal.confirm('是否生成银行发放文件？').then(() => {
        return generateBankFile(row.batchNo)
      }).then(data => {
        // 下载文件
        const blob = new Blob([data], { type: 'text/plain;charset=utf-8' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `bank_file_${row.batchNo}.txt`
        link.click()
        window.URL.revokeObjectURL(url)
        this.$modal.msgSuccess('文件生成成功')
      })
    },
    handleSubmitBank(row) {
      this.$modal.confirm('是否确认提交银行发放？').then(() => {
        return submitToBank({ batchNo: row.batchNo })
      }).then(() => {
        this.$modal.msgSuccess('提交成功')
        this.getList()
      })
    },
    handleImportResult(row) {
      this.currentBatchNo = row.batchNo
      this.importOpen = true
    },
    handleImportSuccess(response) {
      if (response.code !== 200) {
        this.$modal.msgError(response.msg || '导入失败')
        return
      }
      this.$modal.msgSuccess('导入成功')
      this.importOpen = false
      this.getList()
    },
    handleImportError() {
      this.$modal.msgError('导入失败')
    }
  }
}
</script>
