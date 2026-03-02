<template>
  <div class="app-container">
    <el-alert title="待遇认证说明" type="info" :closable="false" class="mb20">
      <div>每年4月和10月需要进行待遇认证，未认证人员将自动暂停发放</div>
    </el-alert>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="姓名"><el-input v-model="queryParams.name" clearable /></el-form-item>
      <el-form-item label="认证状态">
        <el-select v-model="queryParams.certificationStatus" clearable>
          <el-option label="未认证" value="0" />
          <el-option label="已认证" value="1" />
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
      <el-table-column label="上次认证日期" prop="lastCertificationDate" />
      <el-table-column label="认证状态" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.certificationStatus === '1'" type="success">已认证</el-tag>
          <el-tag v-else type="warning">未认证</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="handleCertify(scope.row)" v-if="scope.row.certificationStatus === '0'">认证</el-button>
          <el-button size="mini" type="text" @click="handleView(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
export default {
  name: 'BenefitCertification',
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, name: null, certificationStatus: null }
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
    handleCertify(row) {
      this.$modal.confirm('是否确认该人员通过认证？').then(() => {
        // TODO: 实现API调用
        this.$modal.msgSuccess('认证成功')
        this.getList()
      })
    },
    handleView(row) {
      this.$router.push({ name: 'CertificationDetail', params: { id: row.id } })
    }
  }
}
</script>
