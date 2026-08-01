import request from '@/utils/request'

// 搜索居民
export function searchResidents(keyword) {
  return request({
    url: '/shebao/residentQuery/searchResidents',
    method: 'get',
    params: { keyword }
  })
}

// 获取居民详细信息
export function getResidentDetailInfo(keyword, subsidyPersonId) {
  return request({
    url: '/shebao/residentQuery/getResidentDetailInfo',
    method: 'get',
    params: {
      keyword,
      subsidyPersonId
    }
  })
}

// 获取居民预发放记录（支付计划明细，未财务通过）
export function getResidentPreDistributionList(queryParams) {
  return request({
    url: '/shebao/residentQuery/getResidentPreDistributionList',
    method: 'get',
    params: queryParams
  })
}

// 获取居民发放记录（支付计划明细，已财务通过）
export function getResidentDistributionList(queryParams) {
  return request({
    url: '/shebao/residentQuery/getResidentDistributionList',
    method: 'get',
    params: queryParams
  })
}
