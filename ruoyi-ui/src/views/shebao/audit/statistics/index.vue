<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-title">总发放人数</div>
          <div class="stat-value">{{ statistics.totalPersons }}人</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-title">总发放金额</div>
          <div class="stat-value">{{ statistics.totalAmount }}元</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-title">当月发放</div>
          <div class="stat-value">{{ statistics.monthlyAmount }}元</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-title">失败记录</div>
          <div class="stat-value">{{ statistics.failedCount }}条</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt20">
      <el-col :span="12">
        <el-card>
          <div slot="header"><span>补贴类型分布</span></div>
          <div id="subsidyTypeChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header"><span>月度发放趋势</span></div>
          <div id="monthlyTrendChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt20">
      <el-col :span="24">
        <el-card>
          <div slot="header"><span>按街道办事处统计</span></div>
          <el-table :data="streetStatistics" show-summary>
            <el-table-column label="街道办事处" prop="streetName" />
            <el-table-column label="人数" prop="personCount" />
            <el-table-column label="金额(元)" prop="amount" />
            <el-table-column label="占比" prop="percentage">
              <template slot-scope="scope">
                {{ scope.row.percentage }}%
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'AuditStatistics',
  data() {
    return {
      statistics: {
        totalPersons: 0,
        totalAmount: 0,
        monthlyAmount: 0,
        failedCount: 0
      },
      streetStatistics: []
    }
  },
  created() {
    this.loadStatistics()
  },
  methods: {
    loadStatistics() {
      // TODO: 实现统计数据加载
      // TODO: 实现ECharts图表
    }
  }
}
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}
.mt20 {
  margin-top: 20px;
}
</style>
