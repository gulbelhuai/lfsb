<template>
  <div class="approval-history">
    <el-timeline v-if="history && history.length > 0">
      <el-timeline-item
        v-for="(item, index) in history"
        :key="index"
        :timestamp="item.createTime"
        :type="getItemType(item.operationType)"
        placement="top">
        <el-card>
          <h4>{{ getOperationText(item.operationType) }}</h4>
          <p>操作人：{{ item.operatorName }}</p>
          <p>当前状态：{{ getStatusText(item.currentStatus) }}</p>
          <p v-if="item.operationRemark" style="color: #606266;">备注：{{ item.operationRemark }}</p>
        </el-card>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-else description="暂无审批记录" :image-size="60"></el-empty>
  </div>
</template>

<script>
export default {
  name: 'ApprovalHistory',
  props: {
    history: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    getItemType(operationType) {
      const typeMap = {
        'submit': 'primary',
        'review': 'success',
        'approve': 'success',
        'reject': 'danger',
        'cancel': 'info'
      }
      return typeMap[operationType] || 'primary'
    },
    getOperationText(operationType) {
      const textMap = {
        'submit': '提交审核',
        'review': '复核通过',
        'approve': '审批通过',
        'reject': '驳回',
        'cancel': '撤销'
      }
      return textMap[operationType] || '操作'
    },
    getStatusText(status) {
      const textMap = {
        'draft': '草稿',
        'pending_review': '待复核',
        'pending_approve': '待审批',
        'pending_finance': '待财务',
        'approved': '已通过',
        'rejected': '已驳回',
        'completed': '已完成'
      }
      return textMap[status] || '未知'
    }
  }
}
</script>

<style scoped>
.approval-history {
  padding: 20px 0;
}
</style>
