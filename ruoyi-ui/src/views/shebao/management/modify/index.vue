<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="姓名"><el-input v-model="queryParams.name" clearable /></el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="dataList">
      <el-table-column type="index" label="序号" width="50" />
      <el-table-column label="姓名" prop="name" />
      <el-table-column label="身份证号" prop="idCardNo" width="180" />
      <el-table-column label="当前月补贴(元)" prop="currentMonthlyAmount" />
      <el-table-column label="当前银行账号" prop="currentBankAccountNo" width="180" />
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleModify(scope.row)">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="修改发放信息" :visible.sync="modifyOpen" width="600px">
      <el-form :model="modifyForm" label-width="120px">
        <el-form-item label="姓名">
          <el-input v-model="modifyForm.name" disabled />
        </el-form-item>
        <el-form-item label="月补贴标准(元)">
          <el-input-number v-model="modifyForm.monthlyAmount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="银行名称">
          <el-input v-model="modifyForm.bankName" />
        </el-form-item>
        <el-form-item label="银行账号">
          <el-input v-model="modifyForm.bankAccountNo" />
        </el-form-item>
        <el-form-item label="修改原因">
          <el-input v-model="modifyForm.reason" type="textarea" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitModify">提 交</el-button>
        <el-button @click="modifyOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'BenefitModify',
  data() {
    return {
      loading: true,
      total: 0,
      dataList: [],
      modifyOpen: false,
      modifyForm: {},
      queryParams: { pageNum: 1, pageSize: 10, name: null }
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
    handleModify(row) {
      this.modifyForm = { ...row }
      this.modifyOpen = true
    },
    submitModify() {
      // TODO: 实现API调用
      this.$modal.msgSuccess('提交成功')
      this.modifyOpen = false
      this.getList()
    }
  }
}
</script>
