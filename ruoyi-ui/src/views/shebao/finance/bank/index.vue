<template>
  <div class="app-container">
    <el-alert title="银行发放流程" type="info" :closable="false" class="mb20">
      <div>1. 选择批次 → 2. 生成银行文件 → 3. 提交银行发放 → 4. 导入发放结果</div>
    </el-alert>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="批次号"><el-input v-model="queryParams.batchNo" clearable /></el-form-item>
      <el-form-item label="审批状态">
        <el-select v-model="queryParams.approvalStatus" clearable>
          <el-option label="待财务" value="pending_finance" />
          <el-option label="已提交银行" value="submitted_bank" />
          <el-option label="部分失败" value="partial_failed" />
          <el-option label="已完成" value="completed" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="批次号" prop="batchNo" width="160" />
      <el-table-column label="补贴类型" prop="subsidyType" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.subsidy_type" :value="scope.row.subsidyType"/>
        </template>
      </el-table-column>
      <el-table-column label="发放月份" prop="paymentMonth" />
      <el-table-column label="总人数" prop="totalCount" />
      <el-table-column label="总金额(元)" prop="totalAmount" />
      <el-table-column label="发放状态" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.bankSubmitTime" type="success">已提交</el-tag>
          <el-tag v-else type="warning">待发放</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="300">
        <template slot-scope="scope">
          <el-button size="mini" type="text" :data-testid="`finance-bank-generate-${scope.row.batchNo}`" @click="handleGenerateFile(scope.row)">生成文件</el-button>
          <el-button size="mini" type="text" :data-testid="`finance-bank-submit-${scope.row.batchNo}`" @click="handleSubmitBank(scope.row)" v-if="!scope.row.bankSubmitTime">提交银行</el-button>
          <el-button size="mini" type="text" :data-testid="`finance-bank-import-${scope.row.batchNo}`" @click="handleImportResult(scope.row)" v-if="scope.row.bankSubmitTime">导入结果</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 导入结果对话框 -->
    <el-dialog title="导入发放结果" :visible.sync="importOpen" width="500px">
      <el-upload
        drag
        :action="uploadUrl"
        :headers="uploadHeaders"
        :data="{ batchNo: currentBatchNo }"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        accept=".xlsx,.xls">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将Excel文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">请上传银行发放结果Excel文件</div>
      </el-upload>
      <div slot="footer">
        <el-button @click="importOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBatch, generateBankFile, submitToBank } from '@/api/shebao/finance'
import { getToken } from '@/utils/auth'

export default {
  name: 'FinanceBank',
  dicts: ['subsidy_type'],
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      selectedRows: [],
      importOpen: false,
      currentBatchNo: null,
      uploadUrl: process.env.VUE_APP_BASE_API + '/shebao/finance/bank/import',
      uploadHeaders: { Authorization: 'Bearer ' + getToken() },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        batchNo: null,
        approvalStatus: 'pending_finance'
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
    handleSelectionChange(selection) {
      this.selectedRows = selection
    },
    handleGenerateFile(row) {
      this.$modal.confirm('是否生成银行发放文件？').then(() => {
        return generateBankFile(row.batchNo)
      }).then(data => {
        const blob = new Blob([data], { type: 'text/plain;charset=utf-8' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `bank_${row.batchNo}.txt`
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
