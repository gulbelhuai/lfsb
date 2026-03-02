<template>
  <div class="app-container">
    <!-- 生成条件 -->
    <el-card class="box-card mb20">
      <div slot="header"><span>生成支付计划</span></div>
      <el-form :model="generateForm" ref="generateForm" label-width="120px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="补贴类型" prop="subsidyType">
              <el-select v-model="generateForm.subsidyType" placeholder="请选择">
                <el-option label="失地居民" value="land_loss_resident" />
                <el-option label="被征地居民" value="expropriatee" />
                <el-option label="拆迁居民" value="demolition_resident" />
                <el-option label="村干部" value="village_official" />
                <el-option label="教师" value="teacher" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="发放月份" prop="paymentMonth">
              <el-date-picker v-model="generateForm.paymentMonth" type="month" placeholder="选择月份" value-format="yyyy-MM" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="街道办事处">
              <el-select v-model="generateForm.streetOfficeId" placeholder="全部" clearable>
                <el-option v-for="item in streetOfficeList" :key="item.id" :label="item.streetName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-button type="primary" icon="el-icon-plus" @click="handleGenerate">生成计划</el-button>
            <el-button type="success" icon="el-icon-document" @click="handlePreview">预览统计</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 查询条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable />
      </el-form-item>
      <el-form-item label="发放月份" prop="paymentMonth">
        <el-date-picker v-model="queryParams.paymentMonth" type="month" placeholder="选择月份" value-format="yyyy-MM" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-finished" size="mini" @click="handleCreateBatch" :disabled="!hasSelected" v-hasPermi="['shebao:payment:batch:create']">创建批次</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" @click="handleDelete" :disabled="!hasSelected" v-hasPermi="['shebao:payment:plan:remove']">删除</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="姓名" prop="name" width="100" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="补贴类型" prop="subsidyType" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.subsidy_type" :value="scope.row.subsidyType"/>
        </template>
      </el-table-column>
      <el-table-column label="发放月份" prop="paymentMonth" width="100" />
      <el-table-column label="应发金额(元)" prop="payableAmount" width="120" />
      <el-table-column label="银行账号" prop="bankAccountNo" width="180" />
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.batchNo">已入批次</el-tag>
          <el-tag type="info" v-else>未入批次</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 统计预览对话框 -->
    <el-dialog title="支付计划统计" :visible.sync="statisticsOpen" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="总人数">{{ statistics.totalCount }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ statistics.totalAmount }}元</el-descriptions-item>
        <el-descriptions-item label="失地居民">{{ statistics.landLossCount }}人</el-descriptions-item>
        <el-descriptions-item label="被征地居民">{{ statistics.expropriateeCount }}人</el-descriptions-item>
        <el-descriptions-item label="拆迁居民">{{ statistics.demolitionCount }}人</el-descriptions-item>
        <el-descriptions-item label="村干部">{{ statistics.officialCount }}人</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listPaymentPlan, generatePaymentPlan, deletePaymentPlan, getPaymentStatistics, createBatch } from '@/api/shebao/payment'
import { listStreetOffice } from '@/api/shebao/streetOffice'

export default {
  name: 'PaymentPlan',
  dicts: ['subsidy_type'],
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      selectedIds: [],
      hasSelected: false,
      statisticsOpen: false,
      statistics: {},
      streetOfficeList: [],
      generateForm: {
        subsidyType: null,
        paymentMonth: null,
        streetOfficeId: null
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        paymentMonth: null
      }
    }
  },
  created() {
    this.getList()
    this.getStreetOfficeList()
  },
  methods: {
    getList() {
      this.loading = true
      listPaymentPlan(this.queryParams).then(response => {
        this.dataList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getStreetOfficeList() {
      listStreetOffice().then(response => {
        this.streetOfficeList = response.data
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
    handleGenerate() {
      this.$refs['generateForm'].validate(valid => {
        if (valid) {
          this.$modal.confirm('是否确认生成支付计划？').then(() => {
            return generatePaymentPlan(this.generateForm)
          }).then(() => {
            this.$modal.msgSuccess('生成成功')
            this.getList()
          })
        }
      })
    },
    handlePreview() {
      getPaymentStatistics(this.generateForm).then(response => {
        this.statistics = response.data
        this.statisticsOpen = true
      })
    },
    handleSelectionChange(selection) {
      this.selectedIds = selection.map(item => item.id)
      this.hasSelected = selection.length > 0
    },
    handleCreateBatch() {
      if (this.selectedIds.length === 0) {
        this.$modal.msgWarning('请选择要创建批次的记录')
        return
      }
      this.$modal.confirm('是否确认创建发放批次？').then(() => {
        return createBatch({ planIds: this.selectedIds })
      }).then(() => {
        this.$modal.msgSuccess('批次创建成功')
        this.getList()
      })
    },
    handleDelete() {
      if (this.selectedIds.length === 0) {
        this.$modal.msgWarning('请选择要删除的记录')
        return
      }
      this.$modal.confirm('是否确认删除选中的支付计划？').then(() => {
        return deletePaymentPlan(this.selectedIds.join(','))
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      })
    }
  }
}
</script>
