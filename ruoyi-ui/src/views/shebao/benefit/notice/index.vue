<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="预到龄年月" prop="noticeMonth">
        <el-date-picker
          v-model="queryParams.noticeMonth"
          type="month"
          value-format="yyyy-MM"
          placeholder="请选择预到龄年月"
          clearable
          @blur="handleNoticeMonthBlur"
        />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input
          v-model="queryParams.idCardNo"
          placeholder="请输入身份证号"
          clearable
          maxlength="18"
          @blur="handleIdCardBlur"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">查询</el-button>
        <el-button type="warning" icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['shebao:benefit:notice:list']">导出</el-button>
      </el-form-item>
    </el-form>

    <el-table
      class="rx-table--compact"
      v-loading="loading"
      :data="dataList"
      data-testid="benefit-notice-table"
    >
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column label="街道办" align="center" prop="streetOfficeName" min-width="140" />
      <el-table-column label="村委会" align="center" prop="villageCommitteeName" min-width="140" />
      <el-table-column label="姓名" align="center" prop="name" width="100" />
      <el-table-column label="身份证号" align="center" prop="idCardNo" width="190" />
      <el-table-column label="出生日期" align="center" prop="birthday" width="120" />
      <el-table-column label="到龄年月" align="center" prop="retirementMonth" width="100" />
      <el-table-column label="参保状态" align="center" prop="subsidyStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.shebao_subsidy_status" :value="scope.row.subsidyStatus" />
        </template>
      </el-table-column>
      <el-table-column label="补贴类型" align="center" prop="subsidyType" min-width="150" />
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listBenefitNotice } from '@/api/shebao/benefit'

export default {
  name: 'BenefitNotice',
  dicts: ['shebao_subsidy_status'],
  data() {
    return {
      loading: false,
      total: 0,
      dataList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeMonth: null,
        idCardNo: null
      }
    }
  },
  methods: {
    getList() {
      if (!this.validateQuery()) {
        return
      }
      this.loading = true
      listBenefitNotice(this.queryParams).then(response => {
        this.dataList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleExport() {
      if (!this.validateQuery()) {
        return
      }
      this.download('shebao/benefit/notice/export', {
        noticeMonth: this.queryParams.noticeMonth,
        idCardNo: this.queryParams.idCardNo
      }, `benefit_notice_${new Date().getTime()}.xlsx`)
    },
    handleNoticeMonthBlur() {
      if (this.queryParams.noticeMonth) {
        this.queryParams.idCardNo = null
      }
    },
    handleIdCardBlur() {
      if (this.queryParams.idCardNo) {
        this.queryParams.idCardNo = this.queryParams.idCardNo.trim().toUpperCase()
        this.queryParams.noticeMonth = null
      }
    },
    validateQuery() {
      const hasNoticeMonth = !!this.queryParams.noticeMonth
      const hasIdCardNo = !!this.queryParams.idCardNo
      if (hasNoticeMonth === hasIdCardNo) {
        this.$modal.msgWarning('“预到龄年月”和“身份证号”必须二选一填写')
        return false
      }
      return true
    }
  }
}
</script>
