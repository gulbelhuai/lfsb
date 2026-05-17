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

export const PAYMENT_PLAN_FINANCE_STATUS_LABELS = {
  pending_finance: '待财务',
  finance_pending_review: '待复核',
  finance_pending_approve: '待审批',
  finance_approved: '已通过',
  finance_rejected: '已驳回'
}

export function paymentPlanFinanceStatusLabel(status) {
  if (!status) return ''
  return PAYMENT_PLAN_FINANCE_STATUS_LABELS[status] || status
}

export function paymentPlanAuditStageLabel(stage) {
  if (stage === 'finance') return '财务审核'
  if (stage === 'subsidy') return '补贴审核'
  return stage || '—'
}

const FINANCE_OPERATION_STATUSES = new Set([
  'pending_finance',
  'finance_pending_review',
  'finance_pending_approve',
  'finance_approved',
  'finance_rejected'
])

export function isPaymentPlanFinanceOperationStatus(status) {
  return status != null && FINANCE_OPERATION_STATUSES.has(status)
}

/** 审核记录行：操作状态展示（补贴侧为审批状态文案，财务侧为财务状态文案） */
export function paymentPlanAuditOperationLabel(row) {
  if (!row) return ''
  if (row.approvalStage === 'finance' || (!row.approvalStage && isPaymentPlanFinanceOperationStatus(row.operationStatus))) {
    return paymentPlanFinanceStatusLabel(row.operationStatus)
  }
  return paymentPlanStatusLabel(row.operationStatus)
}

/** 审核记录行：审批阶段展示（兼容无 approval_stage 的旧数据） */
export function paymentPlanAuditStageLabelFromRow(row) {
  if (!row) return '—'
  if (row.approvalStage) return paymentPlanAuditStageLabel(row.approvalStage)
  if (isPaymentPlanFinanceOperationStatus(row.operationStatus)) return '财务审核'
  return '补贴审核'
}

export const PAYMENT_PLAN_ACTION_META = {
  pending_review: { title: '提交', reject: false },
  draft: { title: '撤回', reject: false },
  pending_approve: { title: '复核通过', reject: false },
  review_rejected: { title: '复核驳回', reject: true },
  approved: { title: '审批通过', reject: false },
  approve_rejected: { title: '审批驳回', reject: true }
}

export const FINANCE_BATCH_ACTION_META = {
  financePass: { title: '财务通过', reject: false },
  financeReject: { title: '财务驳回', reject: true },
  reviewPass: { title: '复核通过', reject: false },
  reviewReject: { title: '复核驳回', reject: true },
  approvePass: { title: '审批通过', reject: false },
  approveReject: { title: '审批驳回', reject: true }
}

export function promptFinanceBatchAction(vm, actionKey) {
  const meta = FINANCE_BATCH_ACTION_META[actionKey] || { title: '确认操作', reject: false }
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
