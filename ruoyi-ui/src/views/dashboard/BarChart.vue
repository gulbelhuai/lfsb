<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'
import resize from './mixins/resize'

require('echarts/theme/macarons') // echarts theme

const animationDuration = 6000

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '350px'
    },
    chartData: {
      type: Object,
      default: () => ({
        categories: [],
        series: []
      })
    }
  },
  data() {
    return {
      chart: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        if (this.chart) {
          this.setOptions(val)
        }
      }
    }
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      this.setOptions(this.chartData)
    },
    setOptions({ categories = [], series = [] }) {
      if (!this.chart) {
        return
      }
      this.chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function(params) {
            let result = params[0].name + '<br/>'
            params.forEach(function(item) {
              result += item.seriesName + ': ' + item.value + '人<br/>'
            })
            return result
          }
        },
        grid: {
          top: '10%',
          left: '3%',
          right: '4%',
          bottom: categories.length > 0 && categories.some(cat => cat && cat.length > 6) ? '20%' : '10%',
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          data: categories,
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            rotate: 30,
            interval: 0
          }
        }],
        yAxis: [{
          type: 'value',
          axisTick: {
            show: false
          },
          name: '人数'
        }],
        series: series.map((item, index) => ({
          name: item.name,
          type: 'bar',
          barWidth: '60%',
          data: item.data,
          animationDuration,
          itemStyle: {
            color: item.color || ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc'][index % 9]
          }
        }))
      })
    }
  }
}
</script>
