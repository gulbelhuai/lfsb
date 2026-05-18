/**
 * 补贴登记：基础信息字段是否只读
 * - 修改模式：全部只读（含身份证号）
 * - 新增且人员已存在：基础信息只读，身份证号仍可输入后触发带出
 */
export function isSubsidyBasicInfoReadonly(form) {
  if (!form) {
    return false
  }
  if (form.id != null && form.id !== '') {
    return true
  }
  return !!form.personExists
}

export function isSubsidyIdCardReadonly(form) {
  return form && form.id != null && form.id !== ''
}

/** 补贴登记是否已通过复核（列表行或 approvalStatus 字符串） */
export function isSubsidyRecordApproved(rowOrStatus) {
  const status = typeof rowOrStatus === 'string' ? rowOrStatus : rowOrStatus && rowOrStatus.approvalStatus
  return status === 'approved'
}

/** 查看模式或基础信息只读 */
export function isSubsidyBasicFieldDisabled(form, isView) {
  return !!isView || isSubsidyBasicInfoReadonly(form)
}

/** 查看模式或身份证号只读 */
export function isSubsidyIdCardFieldDisabled(form, isView) {
  return !!isView || isSubsidyIdCardReadonly(form)
}

/** 补贴业务字段只读（查看模式） */
export function isSubsidyFieldDisabled(isView) {
  return !!isView
}
