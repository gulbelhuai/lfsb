export const PAYMENT_PLAN_STATUS_LABELS = {
  draft: '草稿',
  pending_review: '待复核',
  pending_approve: '待审批',
  approved: '审批通过',
  review_rejected: '复核驳回',
  approve_rejected: '审批驳回'
}

export function paymentPlanStatusLabel(status) {
  return PAYMENT_PLAN_STATUS_LABELS[status] || status
}

export const PAYMENT_PLAN_ACTION_META = {
  pending_review: { title: '提交', reject: false },
  draft: { title: '撤回', reject: false },
  pending_approve: { title: '复核通过', reject: false },
  review_rejected: { title: '复核驳回', reject: true },
  approved: { title: '审批通过', reject: false },
  approve_rejected: { title: '审批驳回', reject: true }
}

export function promptPlanAction(vm, targetStatus) {
  const meta = PAYMENT_PLAN_ACTION_META[targetStatus] || { title: '确认操作', reject: false }
  return vm.$prompt('请输入备注说明', `确认${meta.title}`, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPlaceholder: meta.reject ? '驳回时备注必填' : '备注选填',
    inputValidator: (v) => {
      if (!meta.reject) return true
      return (v && v.trim()) ? true : '驳回时备注必填'
    }
  }).then(({ value }) => value)
}
