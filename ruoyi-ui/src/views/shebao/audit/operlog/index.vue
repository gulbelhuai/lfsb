<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="操作人员"><el-input v-model="queryParams.operName" clearable /></el-form-item>
      <el-form-item label="操作模块"><el-input v-model="queryParams.title" clearable /></el-form-item>
      <el-form-item label="操作时间">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="操作模块" prop="title" />
      <el-table-column label="操作类型" prop="businessType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_oper_type" :value="scope.row.businessType"/>
        </template>
      </el-table-column>
      <el-table-column label="请求方法" prop="method" />
      <el-table-column label="操作人员" prop="operName" />
      <el-table-column label="操作地址" prop="operIp" width="130" />
      <el-table-column label="操作时间" prop="operTime" width="160" />
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { list as listOperLog } from '@/api/monitor/operlog'

export default {
  name: 'AuditOperlog',
  dicts: ['sys_oper_type'],
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      dateRange: [],
      queryParams: { pageNum: 1, pageSize: 10, operName: null, title: null }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listOperLog(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
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
      this.$router.push({ name: 'OperlogDetail', params: { id: row.operId } })
    }
  }
}
</script>
