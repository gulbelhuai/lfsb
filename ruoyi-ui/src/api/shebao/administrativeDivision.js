import request from '@/utils/request'

// 查询行政区划列表
export function listAdministrativeDivision(query) {
  return request({
    url: '/shebao/administrativeDivision/list',
    method: 'get',
    params: query
  })
}

// 查询行政区划详细
export function getAdministrativeDivision(id) {
  return request({
    url: '/shebao/administrativeDivision/' + id,
    method: 'get'
  })
}

// 新增行政区划
export function addAdministrativeDivision(data) {
  return request({
    url: '/shebao/administrativeDivision',
    method: 'post',
    data: data
  })
}

// 修改行政区划
export function updateAdministrativeDivision(data) {
  return request({
    url: '/shebao/administrativeDivision',
    method: 'put',
    data: data
  })
}

// 删除行政区划
export function delAdministrativeDivision(id) {
  return request({
    url: '/shebao/administrativeDivision/' + id,
    method: 'delete'
  })
}

// 行政区划状态修改
export function changeAdministrativeDivisionStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/shebao/administrativeDivision/changeStatus',
    method: 'put',
    data: data
  })
}

// 获取行政区划选择框列表
export function getAdministrativeDivisionOptions(query) {
  return request({
    url: '/shebao/administrativeDivision/optionSelect',
    method: 'get',
    params: query
  })
}

// 导出行政区划
export function exportAdministrativeDivision(query) {
  return request({
    url: '/shebao/administrativeDivision/export',
    method: 'post',
    data: query
  })
}

// 获取行政区划树形结构（用于组件选择）
export function getDivisionTree(query) {
  return request({
    url: '/shebao/administrativeDivision/tree',
    method: 'get',
    params: query
  })
}

// 获取指定父级下的直接子级行政区划（用于级联选择）
export function getDirectChildren(query) {
  return request({
    url: '/shebao/administrativeDivision/children',
    method: 'get',
    params: query
  })
}
