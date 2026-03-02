<template>
  <el-tag :type="getStatusType(status)" size="small">
    {{ getStatusText(status) }}
  </el-tag>
</template>

<script>
export default {
  name: 'ApprovalStatus',
  props: {
    status: {
      type: String,
      required: true
    }
  },
  methods: {
    getStatusType(status) {
      const typeMap = {
        'draft': 'info',
        'pending_review': 'warning',
        'pending_approve': 'primary',
        'pending_finance': 'success',
        'approved': 'success',
        'distributed': 'success',
        'rejected': 'danger',
        'completed': 'success'
      }
      return typeMap[status] || 'info'
    },
    getStatusText(status) {
      const textMap = {
        'draft': '草稿',
        'pending_review': '待复核',
        'pending_approve': '待审批',
        'pending_finance': '待财务',
        'approved': '已通过',
        'distributed': '已发放',
        'rejected': '已驳回',
        'completed': '已完成'
      }
      return textMap[status] || '未知'
    }
  }
}
</script>
