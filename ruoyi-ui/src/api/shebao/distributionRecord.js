import request from '@/utils/request'

/** 补贴发放记录列表（财务通过的支付计划明细） */
export function listDistributionRecord(query) {
  return request({
    url: '/shebao/distribution/record/list',
    method: 'get',
    params: query
  })
}

/** 导出发放记录 */
export function exportDistributionRecord(query) {
  return request({
    url: '/shebao/distribution/record/export',
    method: 'post',
    params: query
  })
}
