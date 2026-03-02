<template>
  <div class="dashboard-editor-container">
    <!-- 统计面板 -->
    <panel-group :panel-data="panelData" @handleSetLineChartData="handleSetLineChartData" />

    <!-- 补贴发放趋势图 -->
    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;border-radius:4px;box-shadow:0 2px 12px 0 rgba(0, 0, 0, 0.1);min-height:450px;">
      <div class="chart-title">补贴发放趋势（近6个月）</div>
      <line-chart :chart-data="lineChartData" />
    </el-row>

    <!-- 补贴类型分布和街道办发放统计 -->
    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="12">
        <div class="chart-wrapper">
          <div class="chart-title">补贴类型分布</div>
          <pie-chart :chart-data="pieChartData" />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12">
        <div class="chart-wrapper">
          <div class="chart-title">各街道办发放统计</div>
          <bar-chart :chart-data="barChartData" />
        </div>
      </el-col>
    </el-row>

    <!-- 发放状态统计 -->
    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;margin-top:32px;border-radius:4px;box-shadow:0 2px 12px 0 rgba(0, 0, 0, 0.1);min-height:400px;">
      <div class="chart-title">发放状态统计</div>
      <bar-chart :chart-data="statusChartData" />
    </el-row>
  </div>
</template>

<script>
import PanelGroup from './dashboard/PanelGroup'
import LineChart from './dashboard/LineChart'
import PieChart from './dashboard/PieChart'
import BarChart from './dashboard/BarChart'

export default {
  name: 'Index',
  components: {
    PanelGroup,
    LineChart,
    PieChart,
    BarChart
  },
  data() {
    return {
      // 统计面板数据
      panelData: {
        totalPersons: 0,
        totalAmount: 0,
        pendingReview: 0,
        distributed: 0
      },
      // 折线图数据 - 补贴发放趋势（近6个月）
      lineChartData: {
        months: [],
        actualData: [],
        expectedData: []
      },
      // 饼图数据 - 补贴类型分布
      pieChartData: [],
      // 柱状图数据 - 各街道办发放统计
      barChartData: {
        categories: [],
        series: []
      },
      // 柱状图数据 - 发放状态统计
      statusChartData: {
        categories: [],
        series: []
      }
    }
  },
  created() {
    this.loadDashboardData()
  },
  methods: {
    // 加载仪表盘数据
    loadDashboardData() {
      // 模拟数据加载，实际应该调用API
      this.loadPanelData()
      this.loadLineChartData()
      this.loadPieChartData()
      this.loadBarChartData()
      this.loadStatusChartData()
    },
    // 加载统计面板数据
    loadPanelData() {
      // Mock数据
      this.panelData = {
        totalPersons: 12580,        // 总被补贴人数
        totalAmount: 2856.8,        // 补贴总额（万元）
        pendingReview: 156,         // 待审核数量
        distributed: 11234          // 已发放数量
      }
    },
    // 加载折线图数据 - 补贴发放趋势
    loadLineChartData() {
      // 获取近6个月的数据
      const months = []
      const actualData = []
      const expectedData = []
      
      const now = new Date()
      for (let i = 5; i >= 0; i--) {
        const date = new Date(now.getFullYear(), now.getMonth() - i, 1)
        months.push(`${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`)
        // Mock数据
        actualData.push(Math.floor(Math.random() * 200) + 150)
        expectedData.push(Math.floor(Math.random() * 200) + 180)
      }
      
      this.lineChartData = {
        months,
        actualData,
        expectedData
      }
    },
    // 加载饼图数据 - 补贴类型分布
    loadPieChartData() {
      // Mock数据 - 4种补贴类型
      this.pieChartData = [
        { value: 4520, name: '失地居民补贴' },
        { value: 3280, name: '被征地居民补贴' },
        { value: 2850, name: '拆迁居民补贴' },
        { value: 1930, name: '村干部补贴' }
      ]
    },
    // 加载柱状图数据 - 各街道办发放统计
    loadBarChartData() {
      // Mock数据
      const offices = ['安次区街道办', '广阳区街道办', '开发区街道办', '固安县街道办', '永清县街道办', '香河县街道办']
      const data = offices.map(() => Math.floor(Math.random() * 500) + 200)
      
      this.barChartData = {
        categories: offices,
        series: [{
          name: '发放人数',
          data: data
        }]
      }
    },
    // 加载发放状态统计
    loadStatusChartData() {
      // Mock数据
      this.statusChartData = {
        categories: ['待审核', '待发放', '已拒绝', '已发放'],
        series: [{
          name: '数量',
          data: [156, 234, 45, 11234]
        }]
      }
    },
    handleSetLineChartData(type) {
      // 点击统计面板时，可以切换不同的图表视图
      // 这里保持默认的补贴发放趋势图
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    min-height: 400px;
  }

  .chart-title {
    font-size: 16px;
    font-weight: 500;
    color: #303133;
    padding: 16px 16px 8px;
    border-bottom: 1px solid #ebeef5;
    margin-bottom: 16px;
  }
}

@media (max-width:1024px) {
  .dashboard-editor-container {
    padding: 16px;
  }
  
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
