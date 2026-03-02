import request from '@/utils/request'

// 查询失地居民信息列表
export function listLandLossResident(query) {
  return request({
    url: '/shebao/landLossResident/list',
    method: 'get',
    params: query
  })
}

// 查询失地居民信息详细
export function getLandLossResident(id) {
  return request({
    url: '/shebao/landLossResident/' + id,
    method: 'get'
  })
}

// 根据身份证号获取表单数据（智能填充基础信息）
export function getFormDataByIdCardNo(idCardNo) {
  return request({
    url: '/shebao/landLossResident/getFormDataByIdCardNo',
    method: 'get',
    params: { idCardNo }
  })
}

// 新增失地居民信息
export function addLandLossResident(data) {
  return request({
    url: '/shebao/landLossResident',
    method: 'post',
    data: data
  })
}

// 修改失地居民信息
export function updateLandLossResident(data) {
  return request({
    url: '/shebao/landLossResident',
    method: 'put',
    data: data
  })
}

// 删除失地居民信息
export function delLandLossResident(id) {
  return request({
    url: '/shebao/landLossResident/' + id,
    method: 'delete'
  })
}

// 导出失地居民信息
export function exportLandLossResident(query) {
  return request({
    url: '/shebao/landLossResident/export',
    method: 'post',
    data: query
  })
}
