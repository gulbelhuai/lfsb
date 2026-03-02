import request from '@/utils/request'

// 查询村委会列表
export function listVillageCommittee(query) {
  return request({
    url: '/system/villageCommittee/list',
    method: 'get',
    params: query
  })
}

// 根据街道办ID获取村委会下拉列表
export function getVillageCommitteeByStreetOffice(streetOfficeId) {
  return request({
    url: '/system/villageCommittee/selectList/' + streetOfficeId,
    method: 'get'
  })
}

// 获取所有村委会下拉列表
export function getVillageCommitteeSelectList() {
  return request({
    url: '/system/villageCommittee/selectList',
    method: 'get'
  })
}

// 查询村委会详细
export function getVillageCommittee(id) {
  return request({
    url: '/system/villageCommittee/' + id,
    method: 'get'
  })
}

// 新增村委会
export function addVillageCommittee(data) {
  return request({
    url: '/system/villageCommittee',
    method: 'post',
    data: data
  })
}

// 修改村委会
export function updateVillageCommittee(data) {
  return request({
    url: '/system/villageCommittee',
    method: 'put',
    data: data
  })
}

// 删除村委会
export function delVillageCommittee(id) {
  return request({
    url: '/system/villageCommittee/' + id,
    method: 'delete'
  })
}
