import request from '@/utils/request'

export function listOpeningBank(query) {
  return request({
    url: '/shebao/openingBank/list',
    method: 'get',
    params: query
  })
}

/** 下拉：仅正常状态，形态 { value, label } */
export function listOpeningBankSelect() {
  return request({
    url: '/shebao/openingBank/selectList',
    method: 'get'
  })
}

export function getOpeningBank(id) {
  return request({
    url: '/shebao/openingBank/' + id,
    method: 'get'
  })
}

export function addOpeningBank(data) {
  return request({
    url: '/shebao/openingBank',
    method: 'post',
    data: data
  })
}

export function updateOpeningBank(data) {
  return request({
    url: '/shebao/openingBank',
    method: 'put',
    data: data
  })
}

export function delOpeningBank(id) {
  return request({
    url: '/shebao/openingBank/' + id,
    method: 'delete'
  })
}
