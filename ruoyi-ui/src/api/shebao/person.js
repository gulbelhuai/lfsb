import request from '@/utils/request'

// 查询人员登记列表
export function listPersonRegistration(query) {
  return request({
    url: '/shebao/person/registration/list',
    method: 'get',
    params: query
  })
}

// 查询人员登记详细
export function getPersonRegistration(id) {
  return request({
    url: '/shebao/person/registration/' + id,
    method: 'get'
  })
}

// 新增人员登记
export function addPersonRegistration(data) {
  return request({
    url: '/shebao/person/registration',
    method: 'post',
    data: data
  })
}

// 修改人员登记
export function updatePersonRegistration(data) {
  return request({
    url: '/shebao/person/registration',
    method: 'put',
    data: data
  })
}

// 删除人员登记
export function delPersonRegistration(id) {
  return request({
    url: '/shebao/person/registration/' + id,
    method: 'delete'
  })
}

// 提交审核
export function submitPersonRegistration(id, remark) {
  return request({
    url: `/shebao/person/registration/submit/${id}`,
    method: 'post',
    data: remark
  })
}

// 查询待复核列表
export function listPersonReview(query) {
  return request({
    url: '/shebao/person/review/list',
    method: 'get',
    params: query
  })
}

// 复核通过
export function reviewPersonPass(id, remark) {
  return request({
    url: `/shebao/person/review/${id}/pass`,
    method: 'post',
    data: remark
  })
}

// 复核驳回
export function reviewPersonReject(id, remark) {
  return request({
    url: `/shebao/person/review/${id}/reject`,
    method: 'post',
    data: remark
  })
}

// 查询关键信息修改申请列表
export function listPersonModify(query) {
  return request({
    url: '/shebao/person/modify/list',
    method: 'get',
    params: query
  })
}

// 获取关键信息修改申请详情
export function getPersonModify(id) {
  return request({
    url: '/shebao/person/modify/' + id,
    method: 'get'
  })
}

// 新增/保存草稿（关键信息修改）
export function savePersonModify(data) {
  return request({
    url: '/shebao/person/modify',
    method: data.id ? 'put' : 'post',
    data: data
  })
}

// 提交关键信息修改（经办人：草稿 -> 待复核）
export function submitPersonModify(id) {
  return request({
    url: '/shebao/person/modify/submit/' + id,
    method: 'post'
  })
}

// 复核关键信息修改（复核人：待复核 -> 待审批 或 驳回）
export function reviewPersonModify(id, approved, remark) {
  return request({
    url: '/shebao/person/modify/review/' + id,
    method: 'post',
    params: { approved, remark }
  })
}

// 审批关键信息修改（审批人：待审批 -> 已通过 或 驳回）
export function approvePersonModify(id, approved, remark) {
  return request({
    url: '/shebao/person/modify/approve/' + id,
    method: 'post',
    params: { approved, remark }
  })
}
