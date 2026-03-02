<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'
import resize from './mixins/resize'

require('echarts/theme/macarons') // echarts theme

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
      type: Array,
      default: () => []
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
    setOptions(data) {
      this.chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c}人 ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '5%',
          data: data.length > 0 ? data.map(item => item.name) : []
        },
        series: data.length > 0 ? [
          {
            name: '补贴类型分布',
            type: 'pie',
            roseType: 'radius',
            radius: [20, 80],
            center: ['50%', '45%'],
            data: data,
            animationEasing: 'cubicInOut',
            animationDuration: 2600,
            label: {
              show: true,
              formatter: '{b}: {c}人\n({d}%)'
            }
          }
        ] : []
      })
    }
  }
}
</script>
