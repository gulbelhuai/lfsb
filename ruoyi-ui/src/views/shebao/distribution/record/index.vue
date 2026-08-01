<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="业务期" prop="businessPeriod">
        <el-date-picker
          v-model="queryParams.businessPeriod"
          type="month"
          value-format="yyyy-MM"
          placeholder="业务期"
          clearable
          style="width: 140px"
        />
      </el-form-item>
      <el-form-item label="发放类型" prop="determinationType">
        <el-select v-model="queryParams.determinationType" placeholder="全部" clearable style="width: 120px">
          <el-option label="正常发放" value="normal" />
          <el-option label="二次发放" value="second" />
        </el-select>
      </el-form-item>
      <el-form-item label="补贴类型" prop="subsidyType">
        <el-select v-model="queryParams.subsidyType" placeholder="全部" clearable style="width: 140px">
          <el-option
            v-for="item in subsidyTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="发放机构" prop="grantOrg">
        <el-select v-model="queryParams.grantOrg" placeholder="全部" clearable style="width: 140px">
          <el-option
            v-for="dict in dict.type.shebao_grant_org"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="身份证号" prop="idCardNo">
        <el-input v-model="queryParams.idCardNo" placeholder="身份证号" clearable style="width: 180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="姓名" prop="personName">
        <el-input v-model="queryParams.personName" placeholder="姓名" clearable style="width: 120px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="批次号" prop="batchNo">
        <el-input v-model="queryParams.batchNo" placeholder="批次号" clearable style="width: 140px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="发放状态" prop="payStatus">
        <el-select v-model="queryParams.payStatus" placeholder="全部" clearable style="width: 120px">
          <el-option label="发放中" value="distributing" />
          <el-option label="已发放" value="paid" />
          <el-option label="发放失败" value="failed" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['shebao:distribution:record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" border size="small">
      <el-table-column type="index" label="序号" width="55" align="center" />
      <el-table-column label="支付计划批次" prop="batchNo" width="130" show-overflow-tooltip />
      <el-table-column label="发放类型" prop="determinationType" width="90" align="center">
        <template slot-scope="scope">{{ determinationTypeLabel(scope.row.determinationType) }}</template>
      </el-table-column>
      <el-table-column label="补贴类型" prop="subsidyType" width="110" align="center">
        <template slot-scope="scope">{{ paymentPlanSubsidyTypeLabel(scope.row.subsidyType) }}</template>
      </el-table-column>
      <el-table-column label="街道" prop="streetName" width="100" show-overflow-tooltip />
      <el-table-column label="村委会" prop="villageName" width="100" show-overflow-tooltip />
      <el-table-column label="姓名" prop="personName" width="90" />
      <el-table-column label="身份证号" prop="idCardNo" width="170" />
      <el-table-column label="业务期" prop="businessPeriod" width="90" align="center" />
      <el-table-column label="补发起始" prop="supplementStartMonth" width="90" align="center">
        <template slot-scope="scope">{{ scope.row.supplementStartMonth || '-' }}</template>
      </el-table-column>
      <el-table-column label="补发终止" prop="supplementEndMonth" width="90" align="center">
        <template slot-scope="scope">{{ scope.row.supplementEndMonth || '-' }}</template>
      </el-table-column>
      <el-table-column label="发放金额" prop="distributionAmount" width="100" align="center">
        <template slot-scope="scope">{{ formatAmount(scope.row.distributionAmount) }}</template>
      </el-table-column>
      <el-table-column label="发放日期" prop="distributionDate" width="110" align="center">
        <template slot-scope="scope">{{ scope.row.distributionDate || '-' }}</template>
      </el-table-column>
      <el-table-column label="发放状态" prop="payStatus" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.payStatus === 'distributing'" type="warning" size="small">发放中</el-tag>
          <el-tag v-else-if="scope.row.payStatus === 'paid'" type="success" size="small">已发放</el-tag>
          <el-tag v-else-if="scope.row.payStatus === 'failed'" type="danger" size="small">发放失败</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="失败原因" prop="failReason" min-width="120" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.failReason || '-' }}</template>
      </el-table-column>
      <el-table-column label="发放机构" prop="grantOrg" width="110">
        <template slot-scope="scope">{{ paymentPlanGrantOrgLabels(scope.row.grantOrg, dict.type.shebao_grant_org) }}</template>
      </el-table-column>
      <el-table-column label="开户名" prop="accountName" width="90" />
      <el-table-column label="银行账号" prop="bankAccount" width="160" show-overflow-tooltip />
      <el-table-column label="与参保人关系" prop="relationToInsured" width="110" />
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
import { listDistributionRecord } from '@/api/shebao/distributionRecord'
import {
  PAYMENT_PLAN_SUBSIDY_TYPE_OPTIONS,
  paymentPlanSubsidyTypeLabel,
  paymentPlanGrantOrgLabels
} from '@/views/shebao/payment/plan/planUiShared'

export default {
  name: 'DistributionRecord',
  dicts: ['shebao_grant_org'],
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      dataList: [],
      subsidyTypeOptions: PAYMENT_PLAN_SUBSIDY_TYPE_OPTIONS,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        businessPeriod: null,
        determinationType: null,
        subsidyType: null,
        grantOrg: null,
        idCardNo: null,
        personName: null,
        batchNo: null,
        payStatus: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    paymentPlanSubsidyTypeLabel,
    paymentPlanGrantOrgLabels,
    determinationTypeLabel(type) {
      if (type === 'second') return '二次发放'
      if (type === 'normal') return '正常发放'
      return type || '-'
    },
    formatAmount(val) {
      if (val === null || val === undefined || val === '') return '-'
      const n = Number(val)
      return Number.isNaN(n) ? val : n.toFixed(2)
    },
    getList() {
      this.loading = true
      listDistributionRecord(this.queryParams).then(res => {
        this.dataList = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
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
    handleExport() {
      this.download('shebao/distribution/record/export', {
        ...this.queryParams
      }, `补贴发放记录_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
