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

// 查询待复核人员详情
export function getPersonReviewDetail(id) {
  return request({
    url: '/shebao/person/review/' + id,
    method: 'get'
  })
}

// 复核通过
export function reviewPersonPass(id, remark) {
  return request({
    url: `/shebao/person/review/approve/${id}`,
    method: 'post',
    params: { remark }
  })
}

// 复核驳回
export function reviewPersonReject(id, remark) {
  return request({
    url: `/shebao/person/review/reject/${id}`,
    method: 'post',
    params: { reason: remark }
  })
}

// 是否存在未办结的人员信息变更（data 为 true 表示不可再发起）
export function checkPersonModifyUnfinished(subsidyPersonId, excludeModifyId) {
  const params = { subsidyPersonId }
  if (excludeModifyId != null && excludeModifyId !== '') {
    params.excludeModifyId = excludeModifyId
  }
  return request({
    url: '/shebao/person/modify/checkUnfinished',
    method: 'get',
    params
  })
}

// 人员信息变更申请列表（含 basic / key）
export function listPersonModify(query) {
  return request({
    url: '/shebao/person/modify/list',
    method: 'get',
    params: query
  })
}

// 人员信息变更申请详情
export function getPersonModify(id) {
  return request({
    url: '/shebao/person/modify/' + id,
    method: 'get'
  })
}

// 新增/保存草稿（modifyType: basic | key）
export function savePersonModify(data) {
  return request({
    url: '/shebao/person/modify',
    method: data.id ? 'put' : 'post',
    data: data
  })
}

// 提交（草稿 -> 待复核）
export function submitPersonModify(id) {
  return request({
    url: '/shebao/person/modify/submit/' + id,
    method: 'post'
  })
}

// 复核：basic 通过即已通过；key 通过 -> 待审批
export function reviewPersonModify(id, approved, remark) {
  return request({
    url: '/shebao/person/modify/review/' + id,
    method: 'post',
    params: { approved, remark }
  })
}

// 审批（仅 key：待审批 -> 已通过；驳回 -> 已驳回）
export function approvePersonModify(id, approved, remark) {
  return request({
    url: '/shebao/person/modify/approve/' + id,
    method: 'post',
    params: { approved, remark }
  })
}
