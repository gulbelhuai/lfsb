import request from '@/utils/request'

// 查询村级单位列表
export function listVillage(query) {
  return request({
    url: '/shebao/village/list',
    method: 'get',
    params: query
  })
}

// 查询村级单位详细
export function getVillage(id) {
  return request({
    url: '/shebao/village/' + id,
    method: 'get'
  })
}

// 新增村级单位
export function addVillage(data) {
  return request({
    url: '/shebao/village',
    method: 'post',
    data: data
  })
}

// 修改村级单位
export function updateVillage(data) {
  return request({
    url: '/shebao/village',
    method: 'put',
    data: data
  })
}

// 删除村级单位
export function delVillage(id) {
  return request({
    url: '/shebao/village/' + id,
    method: 'delete'
  })
}

// 村级单位状态修改
export function changeVillageStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/shebao/village/changeStatus',
    method: 'put',
    data: data
  })
}

// 获取村级单位选择框列表
export function optionSelectVillage() {
  return request({
    url: '/shebao/village/optionSelect',
    method: 'get'
  })
}

// 获取省级行政区划列表
export function getProvinces() {
  return request({
    url: '/shebao/village/provinces',
    method: 'get'
  })
}

// 获取市级行政区划列表
export function getCities(provinceCode) {
  return request({
    url: '/shebao/village/cities/' + provinceCode,
    method: 'get'
  })
}

// 获取县级行政区划列表
export function getCounties(cityCode) {
  return request({
    url: '/shebao/village/counties/' + cityCode,
    method: 'get'
  })
}

// 获取乡镇级行政区划列表
export function getTownships(countyCode) {
  if (!countyCode || countyCode.trim() === '') {
    return request({
      url: '/shebao/village/townships',
      method: 'get'
    })
  }
  return request({
    url: '/shebao/village/townships/' + countyCode,
    method: 'get'
  })
}

// 根据父级编码获取下级行政区划
export function getChildrenByParentCode(parentCode) {
  return request({
    url: '/shebao/village/children/' + parentCode,
    method: 'get'
  })
}

// 根据编码获取行政区划信息
export function getDivisionByCode(divisionCode) {
  return request({
    url: '/shebao/village/division/' + divisionCode,
    method: 'get'
  })
}
