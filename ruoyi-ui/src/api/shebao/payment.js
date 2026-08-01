import request from '@/utils/request'

// 查询支付计划列表
export function listPaymentPlan(query) {
  return request({
    url: '/shebao/payment/plan/list',
    method: 'get',
    params: query
  })
}

// 生成支付计划
export function generatePaymentPlan(data) {
  return request({
    url: '/shebao/payment/plan/generate',
    method: 'post',
    data: data
  })
}

// 保存/提交支付计划
export function savePaymentPlan(data) {
  return request({
    url: '/shebao/payment/plan/save',
    method: 'post',
    data: data
  })
}

// 预览支付计划
export function previewPaymentPlan(data) {
  return request({
    url: '/shebao/payment/plan/preview',
    method: 'post',
    data: data
  })
}

// 汇总详情
export function getPaymentPlanSummary(id) {
  return request({
    url: `/shebao/payment/plan/${id}/summary`,
    method: 'get'
  })
}

// 明细详情
export function getPaymentPlanDetail(id, query) {
  return request({
    url: `/shebao/payment/plan/${id}/detail`,
    method: 'get',
    params: query
  })
}

// 审核记录
export function getPaymentPlanAudit(id) {
  return request({
    url: `/shebao/payment/plan/${id}/audit`,
    method: 'get'
  })
}

// 状态变更（提交等）
export function changePaymentPlanStatus(id, data) {
  return request({
    url: `/shebao/payment/plan/${id}/status`,
    method: 'post',
    data: data
  })
}

/** 撤销支付计划（删除计划及明细） */
export function revokePaymentPlan(id) {
  return request({
    url: `/shebao/payment/plan/${id}/revoke`,
    method: 'post'
  })
}

/** 支付计划上传财务（进入待财务） */
export function uploadPlanToFinance(id) {
  return request({
    url: `/shebao/payment/plan/${id}/finance-submit`,
    method: 'post'
  })
}

// 兼容旧方法名
export const getPaymentStatistics = previewPaymentPlan

