import request from '@/utils/request'

// 查询补贴发放记录列表
export function listSubsidyDistribution(query) {
  return request({
    url: '/shebao/subsidyDistribution/list',
    method: 'get',
    params: query
  })
}

// 查询补贴发放记录详细
export function getSubsidyDistribution(id) {
  return request({
    url: '/shebao/subsidyDistribution/' + id,
    method: 'get'
  })
}

// 自动搜索居民（下拉列表）
export function searchResidents(keyword) {
  return request({
    url: '/shebao/subsidyDistribution/searchResidents',
    method: 'get',
    params: { keyword }
  })
}

// 根据居民ID查询可发放的补贴
export function getAvailableSubsidies(subsidyPersonId) {
  return request({
    url: '/shebao/subsidyDistribution/getAvailableSubsidiesByPersonId',
    method: 'get',
    params: { subsidyPersonId }
  })
}

// 查询居民最近的发放记录
export function getRecentDistributions(subsidyPersonId, limit) {
  return request({
    url: `/shebao/subsidyDistribution/recent/${subsidyPersonId}`,
    method: 'get',
    params: { limit }
  })
}

// 新增补贴发放记录
export function addSubsidyDistribution(data) {
  return request({
    url: '/shebao/subsidyDistribution',
    method: 'post',
    data: data
  })
}

// 审核通过
export function approveDistribution(id, remark) {
  return request({
    url: '/shebao/subsidyDistribution/approve',
    method: 'put',
    data: { id, remark }
  })
}

// 审核驳回
export function rejectDistribution(id, remark) {
  return request({
    url: '/shebao/subsidyDistribution/reject',
    method: 'put',
    data: { id, remark }
  })
}

// 发放补贴
export function distributeSubsidy(id, remark) {
  return request({
    url: '/shebao/subsidyDistribution/distribute',
    method: 'put',
    data: { id, remark }
  })
}

// 重新提交
export function resubmitDistribution(id) {
  return request({
    url: '/shebao/subsidyDistribution/resubmit/' + id,
    method: 'put'
  })
}

// 删除补贴发放记录
export function delSubsidyDistribution(id) {
  return request({
    url: '/shebao/subsidyDistribution/' + id,
    method: 'delete'
  })
}

// 导出补贴发放记录
export function exportSubsidyDistribution(query) {
  return request({
    url: '/shebao/subsidyDistribution/export',
    method: 'post',
    params: query
  })
}

