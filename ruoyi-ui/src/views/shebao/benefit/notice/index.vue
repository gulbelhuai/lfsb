<template>
  <div class="app-container">
    <el-card class="box-card mb20">
      <div slot="header"><span>生成预到龄通知</span></div>
      <el-form :model="generateForm" label-width="120px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="通知月份">
              <el-date-picker v-model="generateForm.noticeMonth" type="month" placeholder="选择月份" value-format="yyyy-MM" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="年龄阈值">
              <el-input-number v-model="generateForm.ageThreshold" :min="50" :max="70" />
              <span class="ml10">岁（预计3个月后到达）</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-button type="primary" @click="handleGenerate">生成通知</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="通知月份">
        <el-date-picker v-model="queryParams.noticeMonth" type="month" value-format="yyyy-MM" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="当前年龄" prop="currentAge" />
      <el-table-column label="到龄日期" prop="retirementDate" />
      <el-table-column label="通知月份" prop="noticeMonth" />
      <el-table-column label="通知状态" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.notified" type="success">已通知</el-tag>
          <el-tag v-else type="warning">待通知</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listBenefitNotice, generateBenefitNotice } from '@/api/shebao/benefit'

export default {
  name: 'BenefitNotice',
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      generateForm: { noticeMonth: null, ageThreshold: 60 },
      queryParams: { pageNum: 1, pageSize: 10, noticeMonth: null }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listBenefitNotice(this.queryParams).then(response => {
        this.dataList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleGenerate() {
      this.$modal.confirm('是否确认生成预到龄通知？').then(() => {
        return generateBenefitNotice(this.generateForm)
      }).then(() => {
        this.$modal.msgSuccess('生成成功')
        this.getList()
      })
    }
  }
}
</script>
