<template>
  <div class="app-container">
    <el-alert title="失败处理说明" type="info" :closable="false" class="mb20">
      <div>展示「银行发放」中导入失败的数据（支付计划明细标记为发放失败）。后续信息更正、重新发放、人工处理功能待实现。</div>
    </el-alert>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="业务期">
        <el-date-picker v-model="queryParams.businessPeriod" type="month" value-format="yyyy-MM" placeholder="选择业务期" clearable />
      </el-form-item>
      <el-form-item label="补贴类型">
        <el-select v-model="queryParams.subsidyType" clearable placeholder="全部">
          <el-option v-for="o in subsidyTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="queryParams.personName" clearable placeholder="模糊查询" />
      </el-form-item>
      <el-form-item label="批次号">
        <el-input v-model="queryParams.batchNo" clearable placeholder="精确查询" />
      </el-form-item>
      <el-form-item label="失败原因">
        <el-input v-model="queryParams.failReason" clearable placeholder="模糊查询" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList" border>
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column label="批次号" prop="batchNo" width="130" show-overflow-tooltip />
      <el-table-column label="业务期" prop="businessPeriod" width="100" />
      <el-table-column label="补贴类型" prop="subsidyType" width="120">
        <template slot-scope="scope">{{ subsidyTypeLabel(scope.row.subsidyType) }}</template>
      </el-table-column>
      <el-table-column label="姓名" prop="personName" width="100" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" show-overflow-tooltip />
      <el-table-column label="应发金额(元)" prop="distributionAmount" width="110" />
      <el-table-column label="开户名" prop="accountName" width="100" show-overflow-tooltip />
      <el-table-column label="银行账号" prop="bankAccount" width="180" show-overflow-tooltip />
      <el-table-column label="发放机构" prop="grantOrg" width="120">
        <template slot-scope="scope">
          <dict-tag :options="grantOrgOptions" :value="scope.row.grantOrg" />
        </template>
      </el-table-column>
      <el-table-column label="街道" prop="streetName" width="100" show-overflow-tooltip />
      <el-table-column label="村委会" prop="villageName" width="100" show-overflow-tooltip />
      <el-table-column label="失败原因" prop="failReason" min-width="140" show-overflow-tooltip />
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listFailureRecords } from '@/api/shebao/finance'
import { listOpeningBankSelect } from '@/api/shebao/openingBank'
import { paymentPlanSubsidyTypeLabel, PAYMENT_PLAN_SUBSIDY_TYPE_OPTIONS } from '../../payment/plan/planUiShared'

export default {
  name: 'FinanceFailure',
  data() {
    return {
      loading: false,
      total: 0,
      dataList: [],
      grantOrgOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        personName: null,
        batchNo: null,
        businessPeriod: null,
        subsidyType: null,
        failReason: null
      }
    }
  },
  computed: {
    subsidyTypeOptions() {
      return PAYMENT_PLAN_SUBSIDY_TYPE_OPTIONS
    }
  },
  created() {
    this.loadGrantOrgOptions()
    this.getList()
  },
  methods: {
    loadGrantOrgOptions() {
      listOpeningBankSelect().then(res => {
        this.grantOrgOptions = res.data || []
      }).catch(() => { this.grantOrgOptions = [] })
    },
    getList() {
      this.loading = true
      listFailureRecords(this.queryParams).then(response => {
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
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        personName: null,
        batchNo: null,
        businessPeriod: null,
        subsidyType: null,
        failReason: null
      }
      this.getList()
    },
    subsidyTypeLabel(val) {
      return paymentPlanSubsidyTypeLabel(val)
    }
  }
}
</script>

<style scoped>
.mb20 {
  margin-bottom: 20px;
}
</style>
