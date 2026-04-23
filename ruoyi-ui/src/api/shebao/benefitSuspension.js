import request from '@/utils/request'

// 查询待遇暂停列表
export function listBenefitSuspension(query) {
  return request({
    url: '/shebao/management/suspension/list',
    method: 'get',
    params: query
  })
}

// 按身份证查询待遇人员
export function getSuspensionCandidate(idCardNo) {
  return request({
    url: '/shebao/management/suspension/candidate',
    method: 'get',
    params: { idCardNo }
  })
}

// 新增待遇暂停
export function addBenefitSuspension(data) {
  return request({
    url: '/shebao/management/suspension/pause',
    method: 'post',
    data
  })
}

// 查询待遇暂停详情
export function getBenefitSuspensionDetail(id) {
  return request({
    url: '/shebao/management/suspension/' + id,
    method: 'get'
  })
}
