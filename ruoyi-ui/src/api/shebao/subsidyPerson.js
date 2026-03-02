import request from '@/utils/request'

// 查询被补贴人基础信息列表
export function listSubsidyPerson(query) {
  return request({
    url: '/shebao/subsidyPerson/list',
    method: 'get',
    params: query
  })
}

// 查询被补贴人基础信息详细
export function getSubsidyPerson(id) {
  return request({
    url: '/shebao/subsidyPerson/' + id,
    method: 'get'
  })
}

// 新增被补贴人基础信息
export function addSubsidyPerson(data) {
  return request({
    url: '/shebao/subsidyPerson',
    method: 'post',
    data: data
  })
}

// 修改被补贴人基础信息
export function updateSubsidyPerson(data) {
  return request({
    url: '/shebao/subsidyPerson',
    method: 'put',
    data: data
  })
}

// 删除被补贴人基础信息
export function delSubsidyPerson(id) {
  return request({
    url: '/shebao/subsidyPerson/' + id,
    method: 'delete'
  })
}

// 被补贴人状态修改
export function changeSubsidyPersonStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/shebao/subsidyPerson/changeStatus',
    method: 'put',
    data: data
  })
}

// 校验身份证号
export function checkIdCardNoUnique(data) {
  return request({
    url: '/shebao/subsidyPerson/checkIdCardNoUnique',
    method: 'get',
    params: data
  })
}

// 导出被补贴人基础信息
export function exportSubsidyPerson(query) {
  return request({
    url: '/shebao/subsidyPerson/export',
    method: 'post',
    data: query
  })
}

// 生成用户编号
export function generateUserCode(streetOfficeId, villageCommitteeId) {
  return request({
    url: '/shebao/subsidyPerson/generateUserCode',
    method: 'post',
    params: {
      streetOfficeId,
      villageCommitteeId
    }
  })
}

// 校验用户编号是否唯一
export function checkUserCodeUnique(userCode, id) {
  return request({
    url: '/shebao/subsidyPerson/checkUserCodeUnique',
    method: 'get',
    params: {
      userCode,
      id
    }
  })
}

// 根据身份证号查询被补贴人基础信息
export function getSubsidyPersonByIdCardNo(idCardNo, includeCancel = false) {
  return request({
    url: '/shebao/subsidyPerson/getByIdCardNo',
    method: 'get',
    params: {
      idCardNo,
      includeCancel
    }
  })
}

// 人员注销登记（标记死亡）
export function cancelSubsidyPerson(data) {
  return request({
    url: '/shebao/subsidyPerson/cancel',
    method: 'post',
    data: data
  })
}
