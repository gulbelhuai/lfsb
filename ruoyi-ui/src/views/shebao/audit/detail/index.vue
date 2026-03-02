<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="姓名"><el-input v-model="queryParams.name" clearable /></el-form-item>
      <el-form-item label="补贴类型">
        <el-select v-model="queryParams.subsidyType" clearable>
          <el-option label="失地居民" value="land_loss_resident" />
          <el-option label="被征地居民" value="expropriatee" />
        </el-select>
      </el-form-item>
      <el-form-item label="发放月份">
        <el-date-picker v-model="queryParams.paymentMonth" type="month" value-format="yyyy-MM" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button type="warning" icon="el-icon-download" size="mini" @click="handleExport">导出</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList" show-summary :summary-method="getSummaries">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="批次号" prop="batchNo" width="160" />
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="补贴类型" prop="subsidyType" />
      <el-table-column label="发放月份" prop="paymentMonth" />
      <el-table-column label="应发金额(元)" prop="payableAmount" />
      <el-table-column label="实发金额(元)" prop="actualAmount" />
      <el-table-column label="发放状态" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.paymentStatus === 'success'" type="success">成功</el-tag>
          <el-tag v-else-if="scope.row.paymentStatus === 'failed'" type="danger">失败</el-tag>
          <el-tag v-else type="info">待发放</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发放时间" prop="paymentTime" width="160" />
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
export default {
  name: 'AuditDetail',
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, name: null, subsidyType: null, paymentMonth: null }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      // TODO: 实现API调用
      this.loading = false
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleExport() {
      this.$modal.confirm('是否确认导出发放明细？').then(() => {
        // TODO: 实现导出功能
        this.$modal.msgSuccess('导出成功')
      })
    },
    getSummaries(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '合计'
          return
        }
        if (column.property === 'payableAmount' || column.property === 'actualAmount') {
          const values = data.map(item => Number(item[column.property]))
          if (!values.every(value => isNaN(value))) {
            sums[index] = values.reduce((prev, curr) => {
              const value = Number(curr)
              if (!isNaN(value)) {
                return prev + curr
              } else {
                return prev
              }
            }, 0)
            sums[index] += ' 元'
          }
        }
      })
      return sums
    }
  }
}
</script>
