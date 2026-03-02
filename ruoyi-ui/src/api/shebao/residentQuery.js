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

// 获取居民发放记录
export function getResidentDistributionList(queryParams) {
  return request({
    url: '/shebao/residentQuery/getResidentDistributionList',
    method: 'get',
    params: queryParams
  })
}
