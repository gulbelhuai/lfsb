import request from '@/utils/request'

// 查询街道办事处列表
export function listStreetOffice(query) {
  return request({
    url: '/shebao/streetOffice/list',
    method: 'get',
    params: query
  })
}

// 获取街道办事处下拉列表
export function getStreetOfficeSelectList() {
  return request({
    url: '/shebao/streetOffice/selectList',
    method: 'get'
  })
}

// 查询街道办事处详细
export function getStreetOffice(id) {
  return request({
    url: '/shebao/streetOffice/' + id,
    method: 'get'
  })
}

// 新增街道办事处
export function addStreetOffice(data) {
  return request({
    url: '/shebao/streetOffice',
    method: 'post',
    data: data
  })
}

// 修改街道办事处
export function updateStreetOffice(data) {
  return request({
    url: '/shebao/streetOffice',
    method: 'put',
    data: data
  })
}

// 删除街道办事处
export function delStreetOffice(id) {
  return request({
    url: '/shebao/streetOffice/' + id,
    method: 'delete'
  })
}
