<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="批次号"><el-input v-model="queryParams.batchNo" clearable /></el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="批次号" prop="batchNo" width="160" />
      <el-table-column label="补贴类型" prop="subsidyType" />
      <el-table-column label="发放月份" prop="paymentMonth" />
      <el-table-column label="总人数" prop="totalCount" />
      <el-table-column label="总金额(元)" prop="totalAmount" />
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" :data-testid="`payment-review-view-${scope.row.batchNo}`" @click="handleView(scope.row)">详情</el-button>
          <el-button size="mini" type="success" :data-testid="`payment-review-approve-${scope.row.batchNo}`" @click="handleReview(scope.row, true)">通过</el-button>
          <el-button size="mini" type="danger" :data-testid="`payment-review-reject-${scope.row.batchNo}`" @click="handleReview(scope.row, false)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listBatch, reviewBatch } from '@/api/shebao/payment'

export default {
  name: 'PaymentReview',
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, batchNo: null, approvalStatus: 'pending_review' }
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
    handleView(row) {
      this.$alert(
        `批次号：${row.batchNo || '-'}\n补贴类型：${row.subsidyType || '-'}\n总人数：${row.totalCount || 0}\n总金额：${row.totalAmount || 0}`,
        '批次摘要',
        { confirmButtonText: '关闭' }
      )
    },
    handleReview(row, approved) {
      const msg = approved ? '通过' : '驳回'
      this.$prompt(`请输入${msg}意见`, '复核', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        return reviewBatch(row.id, approved, value)
      }).then(() => {
        this.$modal.msgSuccess(`${msg}成功`)
        this.getList()
      })
    }
  }
}
</script>
