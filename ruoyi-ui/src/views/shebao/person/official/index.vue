<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="姓名"><el-input v-model="queryParams.name" clearable /></el-form-item>
      <el-form-item label="职务"><el-input v-model="queryParams.position" clearable /></el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="用户编号" prop="userCode" />
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="身份证号" prop="idCardNo" />
      <el-table-column label="职务" prop="position" />
      <el-table-column label="任职开始时间" prop="startDate" />
      <el-table-column label="审批状态" align="center">
        <template slot-scope="scope">
          <approval-status :status="scope.row.approvalStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">详情</el-button>
          <el-button size="mini" type="text" @click="handleUpdate(scope.row)" v-if="scope.row.approvalStatus === 'draft'">修改</el-button>
          <el-button size="mini" type="text" @click="handleSubmit(scope.row)" v-if="scope.row.approvalStatus === 'draft'">提交</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listPersonRegistration, submitPersonRegistration } from '@/api/shebao/person'
import ApprovalStatus from '@/components/Shebao/ApprovalStatus'

export default {
  name: 'PersonOfficial',
  components: { ApprovalStatus },
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, subsidyType: 'village_official', position: null }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPersonRegistration(this.queryParams).then(response => {
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
    handleAdd() {
      this.$router.push({ name: 'PersonOfficialAdd' })
    },
    handleUpdate(row) {
      this.$router.push({ name: 'PersonOfficialEdit', params: { id: row.id } })
    },
    handleView(row) {
      this.$router.push({ name: 'PersonDetail', params: { id: row.id } })
    },
    handleSubmit(row) {
      this.$modal.confirm('是否确认提交？').then(() => {
        return submitPersonRegistration(row.id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('提交成功')
      })
    }
  }
}
</script>
