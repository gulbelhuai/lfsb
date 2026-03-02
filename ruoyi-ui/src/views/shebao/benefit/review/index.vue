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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="补贴类型" prop="subsidyType" />
      <el-table-column label="月补贴标准(元)" prop="monthlyAmount" />
      <el-table-column label="开始月份" prop="startMonth" />
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleView(scope.row)">详情</el-button>
          <el-button size="mini" type="success" @click="handleReview(scope.row, true)">通过</el-button>
          <el-button size="mini" type="danger" @click="handleReview(scope.row, false)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listBenefitReview, reviewBenefitPass, reviewBenefitReject } from '@/api/shebao/benefit'

export default {
  name: 'BenefitReview',
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      queryParams: { pageNum: 1, pageSize: 10, name: null, subsidyType: null, approvalStatus: 'pending_review' }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listBenefitReview(this.queryParams).then(response => {
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
      this.$router.push({ name: 'BenefitDetail', params: { id: row.id } })
    },
    handleReview(row, approved) {
      const func = approved ? reviewBenefitPass : reviewBenefitReject
      const msg = approved ? '通过' : '驳回'
      this.$prompt(`请输入${msg}意见`, '审核', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        return func(row.id, value)
      }).then(() => {
        this.$modal.msgSuccess(`${msg}成功`)
        this.getList()
      })
    }
  }
}
</script>
