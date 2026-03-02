import request from '@/utils/request'

// 查询拆迁居民信息列表
export function listDemolitionResident(query) {
  return request({
    url: '/shebao/demolitionResident/list',
    method: 'get',
    params: query
  })
}

// 查询拆迁居民信息详细
export function getDemolitionResident(id) {
  return request({
    url: '/shebao/demolitionResident/' + id,
    method: 'get'
  })
}

// 根据身份证号获取表单数据（智能填充基础信息）
export function getFormDataByIdCardNo(idCardNo) {
  return request({
    url: '/shebao/demolitionResident/getFormDataByIdCardNo',
    method: 'get',
    params: { idCardNo }
  })
}

// 新增拆迁居民信息
export function addDemolitionResident(data) {
  return request({
    url: '/shebao/demolitionResident',
    method: 'post',
    data: data
  })
}

// 修改拆迁居民信息
export function updateDemolitionResident(data) {
  return request({
    url: '/shebao/demolitionResident',
    method: 'put',
    data: data
  })
}

// 删除拆迁居民信息
export function delDemolitionResident(id) {
  return request({
    url: '/shebao/demolitionResident/' + id,
    method: 'delete'
  })
}

// 导出拆迁居民信息
export function exportDemolitionResident(query) {
  return request({
    url: '/shebao/demolitionResident/export',
    method: 'post',
    data: query
  })
}
