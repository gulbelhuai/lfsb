<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="姓名"><el-input v-model="queryParams.name" clearable /></el-form-item>
      <el-form-item label="暂停状态">
        <el-select v-model="queryParams.suspensionStatus" clearable>
          <el-option label="正常发放" value="0" />
          <el-option label="已暂停" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="补贴类型" prop="subsidyType" />
      <el-table-column label="暂停状态" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.suspensionStatus === '1'" type="danger">已暂停</el-tag>
          <el-tag v-else type="success">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="暂停原因" prop="suspensionReason" />
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="danger" @click="handleSuspend(scope.row)" v-if="scope.row.suspensionStatus === '0'">暂停</el-button>
          <el-button size="mini" type="success" @click="handleResume(scope.row)" v-else>恢复</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
export default {
  name: 'BenefitSuspension',
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, name: null, suspensionStatus: null }
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
    handleSuspend(row) {
      this.$prompt('请输入暂停原因', '暂停待遇', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        // TODO: 实现API调用
        this.$modal.msgSuccess('暂停成功')
        this.getList()
      })
    },
    handleResume(row) {
      this.$modal.confirm('是否确认恢复待遇发放？').then(() => {
        // TODO: 实现API调用
        this.$modal.msgSuccess('恢复成功')
        this.getList()
      })
    }
  }
}
</script>
