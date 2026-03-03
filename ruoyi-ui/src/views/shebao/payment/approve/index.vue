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
          <el-button size="mini" type="text" @click="handleView(scope.row)">详情</el-button>
          <el-button size="mini" type="success" @click="handleApprove(scope.row, true)">通过</el-button>
          <el-button size="mini" type="danger" @click="handleApprove(scope.row, false)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listBatch, approveBatch } from '@/api/shebao/payment'

export default {
  name: 'PaymentApprove',
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, batchNo: null, approvalStatus: 'pending_approve' }
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
      this.$router.push({ name: 'BatchDetail', params: { id: row.id } })
    },
    handleApprove(row, approved) {
      const msg = approved ? '通过' : '驳回'
      this.$prompt(`请输入${msg}意见`, '审批', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        return approveBatch(row.id, approved, value)
      }).then(() => {
        this.$modal.msgSuccess(`${msg}成功`)
        this.getList()
      })
    }
  }
}
</script>
