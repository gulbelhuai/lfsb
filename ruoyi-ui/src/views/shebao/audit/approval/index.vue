<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="业务类型">
        <el-select v-model="queryParams.businessType" clearable>
          <el-option label="人员登记" value="person_registration" />
          <el-option label="待遇核定" value="benefit_determination" />
          <el-option label="支付批次" value="payment_batch" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作人"><el-input v-model="queryParams.operatorName" clearable /></el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="业务类型" prop="businessType" />
      <el-table-column label="业务ID" prop="businessId" />
      <el-table-column label="操作类型" prop="operationType">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.operationType === 'submit'" type="primary">提交</el-tag>
          <el-tag v-else-if="scope.row.operationType === 'review'" type="success">复核</el-tag>
          <el-tag v-else-if="scope.row.operationType === 'approve'" type="success">审批</el-tag>
          <el-tag v-else-if="scope.row.operationType === 'reject'" type="danger">驳回</el-tag>
          <el-tag v-else>其他</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="当前状态" prop="currentStatus" />
      <el-table-column label="操作人" prop="operatorName" />
      <el-table-column label="操作时间" prop="createTime" width="160" />
      <el-table-column label="操作说明" prop="operationRemark" show-overflow-tooltip />
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listApprovalLog } from '@/api/shebao/approval'

export default {
  name: 'AuditApproval',
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, businessType: null, operatorName: null }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listApprovalLog(this.queryParams).then(response => {
        this.dataList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    }
  }
}
</script>
