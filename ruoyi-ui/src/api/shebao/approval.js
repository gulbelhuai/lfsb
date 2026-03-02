import request from '@/utils/request'

// 查询审批历史
export function getApprovalHistory(businessType, businessId) {
  return request({
    url: `/shebao/approval/history/${businessType}/${businessId}`,
    method: 'get'
  })
}

// 查询审批记录列表
export function listApprovalLog(query) {
  return request({
    url: '/shebao/approval/log/list',
    method: 'get',
    params: query
  })
}
