import request from '@/utils/request'

// 查询人员注销登记列表
export function listPersonCancel(query) {
  return request({
    url: '/shebao/personCancel/list',
    method: 'get',
    params: query
  })
}

// 查询人员注销登记详情
export function getPersonCancel(id) {
  return request({
    url: '/shebao/personCancel/' + id,
    method: 'get'
  })
}

// 新增人员注销登记
export function addPersonCancel(data) {
  return request({
    url: '/shebao/personCancel',
    method: 'post',
    data
  })
}

// 修改人员注销登记
export function updatePersonCancel(data) {
  return request({
    url: '/shebao/personCancel',
    method: 'put',
    data
  })
}

// 删除人员注销登记
export function delPersonCancel(ids) {
  return request({
    url: '/shebao/personCancel/' + ids,
    method: 'delete'
  })
}

// 复核人员注销登记（通过/驳回）
export function reviewPersonCancel(id, approved, remark) {
  return request({
    url: `/shebao/personCancel/review/${id}`,
    method: 'post',
    params: { approved, remark }
  })
}

