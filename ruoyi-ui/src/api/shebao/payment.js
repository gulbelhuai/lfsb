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

// 删除支付计划
export function deletePaymentPlan(ids) {
  return request({
    url: '/shebao/payment/plan/' + ids,
    method: 'delete'
  })
}

// 获取支付计划统计
export function getPaymentStatistics(data) {
  return request({
    url: '/shebao/payment/plan/statistics',
    method: 'post',
    data: data
  })
}

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
export function reviewBatch(id, data) {
  return request({
    url: `/shebao/payment/batch/${id}/review`,
    method: 'post',
    data: data
  })
}

// 批次审批
export function approveBatch(id, data) {
  return request({
    url: `/shebao/payment/batch/${id}/approve`,
    method: 'post',
    data: data
  })
}

// 上传财务系统
export function uploadToFinance(id) {
  return request({
    url: `/shebao/payment/batch/${id}/upload`,
    method: 'post'
  })
}
