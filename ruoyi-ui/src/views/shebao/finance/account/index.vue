<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header"><span>财务账户概览</span></div>
          <el-row :gutter="20">
            <el-col :span="8" v-for="account in accountList" :key="account.accountType">
              <el-card shadow="hover" class="account-card">
                <div class="account-type">{{ getAccountTypeName(account.accountType) }}</div>
                <div class="account-balance">{{ account.balance }} 元</div>
                <div class="account-info">
                  <span>账户：{{ account.accountCode }}</span>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="box-card mt20">
      <div slot="header"><span>账户明细</span></div>

      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
        <el-form-item label="账户类型">
          <el-select v-model="queryParams.accountType" clearable>
            <el-option label="失地居民补贴" value="land_loss_resident" />
            <el-option label="被征地居民补贴" value="expropriatee" />
            <el-option label="拆迁居民补贴" value="demolition_resident" />
            <el-option label="村干部补贴" value="village_official" />
            <el-option label="教龄补助" value="teacher" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="queryParams.startDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="queryParams.endDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="detailList">
        <el-table-column type="index" label="序号" width="50" />
        <el-table-column label="账户类型" prop="accountType" width="150">
          <template slot-scope="scope">
            {{ getAccountTypeName(scope.row.accountType) }}
          </template>
        </el-table-column>
        <el-table-column label="批次号" prop="batchNo" width="160" />
        <el-table-column label="交易类型" prop="transactionType" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.transactionType === 'pay'" type="danger">支出</el-tag>
            <el-tag v-else type="success">收入</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="交易金额(元)" prop="amount" width="120" />
        <el-table-column label="余额(元)" prop="balance" width="120" />
        <el-table-column label="交易时间" prop="transactionTime" width="160" />
        <el-table-column label="备注" prop="remark" />
      </el-table>

      <pagination v-show="detailTotal>0" :total="detailTotal" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getDetailList" />
    </el-card>
  </div>
</template>

<script>
import { listFinanceAccount, getAccountBalance } from '@/api/shebao/finance'

export default {
  name: 'FinanceAccount',
  data() {
    return {
      loading: true,
      detailTotal: 0,
      accountList: [],
      detailList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        accountType: null,
        startDate: null,
        endDate: null
      }
    }
  },
  created() {
    this.getAccountList()
    this.getDetailList()
  },
  methods: {
    getAccountList() {
      listFinanceAccount().then(response => {
        this.accountList = response.data
      })
    },
    getDetailList() {
      this.loading = true
      // TODO: 实现账户明细查询API
      this.loading = false
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getDetailList()
    },
    getAccountTypeName(type) {
      const typeMap = {
        'land_loss_resident': '失地居民补贴',
        'expropriatee': '被征地居民补贴',
        'demolition_resident': '拆迁居民补贴',
        'village_official': '村干部补贴',
        'teacher': '教师补贴'
      }
      return typeMap[type] || type
    }
  }
}
</script>

<style scoped>
.account-card {
  text-align: center;
  margin-bottom: 20px;
}
.account-type {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}
.account-balance {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}
.account-info {
  font-size: 12px;
  color: #909399;
}
.mt20 {
  margin-top: 20px;
}
</style>
