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

// 兼容旧方法名
export const getPaymentStatistics = previewPaymentPlan

// 创建发放批次
export function createBatch(data) {
  return request({
    url: '/shebao/payment/batch/create',
    method: 'post',
    data: data
  })
}

// 查询批次列表
export function listBatch(query) {
  return request({
    url: '/shebao/payment/batch/list',
    method: 'get',
    params: query
  })
}

// 查询批次详情
export function getBatchDetail(batchNo) {
  return request({
    url: `/shebao/payment/batch/detail/${batchNo}`,
    method: 'get'
  })
}

// 批次复核
export function reviewBatch(id, approved, remark) {
  const action = approved ? 'approve' : 'reject'
  return request({
    url: `/shebao/payment/review/${action}/${id}`,
    method: 'post',
    params: { remark }
  })
}

// 批次审批
export function approveBatch(id, approved, remark) {
  const action = approved ? 'approve' : 'reject'
  return request({
    url: `/shebao/payment/approve/${action}/${id}`,
    method: 'post',
    params: { remark }
  })
}

// 上传财务系统
export function uploadToFinance(id) {
  return request({
    url: `/shebao/payment/batch/upload/${id}`,
    method: 'post'
  })
}
