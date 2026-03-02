import request from '@/utils/request'

// 查询社保参数列表
export function listShebaoConfig(query) {
  return request({
    url: '/shebao/config/list',
    method: 'get',
    params: query
  })
}

// 查询社保参数详细
export function getShebaoConfig(configId) {
  return request({
    url: '/shebao/config/' + configId,
    method: 'get'
  })
}

// 新增社保参数配置
export function addShebaoConfig(data) {
  return request({
    url: '/shebao/config',
    method: 'post',
    data: data
  })
}

// 修改社保参数配置
export function updateShebaoConfig(data) {
  return request({
    url: '/shebao/config',
    method: 'put',
    data: data
  })
}

// 删除社保参数配置
export function delShebaoConfig(configId) {
  return request({
    url: '/shebao/config/' + configId,
    method: 'delete'
  })
}
