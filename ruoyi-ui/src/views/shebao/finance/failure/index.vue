<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="姓名"><el-input v-model="queryParams.name" clearable /></el-form-item>
      <el-form-item label="批次号"><el-input v-model="queryParams.batchNo" clearable /></el-form-item>
      <el-form-item label="失败原因">
        <el-select v-model="queryParams.failureReason" clearable>
          <el-option label="账号错误" value="account_error" />
          <el-option label="余额不足" value="insufficient_balance" />
          <el-option label="银行系统问题" value="bank_system_error" />
          <el-option label="其他" value="other" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="批次号" prop="batchNo" width="160" />
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="应发金额(元)" prop="distributionAmount" />
      <el-table-column label="银行账号" prop="bankAccountNo" width="180" />
      <el-table-column label="失败原因" prop="failureReason" width="120" />
      <el-table-column label="发放次数" prop="retryCount" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.retryCount === 0">首次</el-tag>
          <el-tag type="warning" v-else-if="scope.row.retryCount === 1">二次</el-tag>
          <el-tag type="danger" v-else>三次</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="260">
        <template slot-scope="scope">
          <el-button size="mini" type="text" :data-testid="`finance-failure-correct-${scope.row.id}`" @click="handleCorrect(scope.row)">信息更正</el-button>
          <el-button size="mini" type="text" :data-testid="`finance-failure-retry-${scope.row.id}`" @click="handleRetry(scope.row)" v-if="scope.row.retryCount < 2">重新发放</el-button>
          <el-button size="mini" type="text" :data-testid="`finance-failure-manual-${scope.row.id}`" @click="handleManual(scope.row)" v-else>人工处理</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 信息更正对话框 -->
    <el-dialog title="信息更正" :visible.sync="correctOpen" width="500px">
      <el-form ref="correctForm" :model="correctForm" label-width="120px">
        <el-form-item label="姓名">
          <el-input v-model="correctForm.name" disabled />
        </el-form-item>
        <el-form-item label="原银行账号">
          <el-input v-model="correctForm.oldBankAccountNo" disabled />
        </el-form-item>
        <el-form-item label="新银行账号" prop="bankAccountNo">
          <el-input v-model="correctForm.bankAccountNo" placeholder="请输入正确的银行账号" />
        </el-form-item>
        <el-form-item label="更正说明">
          <el-input v-model="correctForm.remark" type="textarea" placeholder="请输入更正说明" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitCorrect">确 定</el-button>
        <el-button @click="correctOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFailureRecords, handleFailure } from '@/api/shebao/finance'

export default {
  name: 'FinanceFailure',
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      correctOpen: false,
      correctForm: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        batchNo: null,
        failureReason: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listFailureRecords(this.queryParams).then(response => {
        this.dataList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleCorrect(row) {
      this.correctForm = {
        distributionId: row.id,
        name: row.name,
        oldBankAccountNo: row.bankAccountNo,
        bankAccountNo: '',
        handleType: 'correct',
        remark: ''
      }
      this.correctOpen = true
    },
    submitCorrect() {
      handleFailure(this.correctForm).then(() => {
        this.$modal.msgSuccess('信息更正成功')
        this.correctOpen = false
        this.getList()
      })
    },
    handleRetry(row) {
      const retryMsg = row.retryCount === 0 ? '二次发放' : '三次发放'
      this.$modal.confirm(`是否确认${retryMsg}？`).then(() => {
        return handleFailure({
          distributionId: row.id,
          handleType: 'retry',
          remark: retryMsg
        })
      }).then(() => {
        this.$modal.msgSuccess('提交成功')
        this.getList()
      })
    },
    handleManual(row) {
      this.$prompt('请输入人工处理说明', '人工处理', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '请输入处理说明'
      }).then(({ value }) => {
        return handleFailure({
          distributionId: row.id,
          handleType: 'manual',
          remark: value
        })
      }).then(() => {
        this.$modal.msgSuccess('已转人工处理')
        this.getList()
      })
    }
  }
}
</script>
