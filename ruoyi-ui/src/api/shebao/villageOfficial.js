import request from '@/utils/request'

// 查询村干部信息列表
export function listVillageOfficial(query) {
  return request({
    url: '/shebao/villageOfficial/list',
    method: 'get',
    params: query
  })
}

// 查询村干部信息详细
export function getVillageOfficial(id) {
  return request({
    url: '/shebao/villageOfficial/' + id,
    method: 'get'
  })
}

// 根据身份证号获取表单数据（智能填充基础信息）
export function getFormDataByIdCardNo(idCardNo) {
  return request({
    url: '/shebao/villageOfficial/getFormDataByIdCardNo',
    method: 'get',
    params: { idCardNo }
  })
}

// 计算任职年限与补贴标准（不保存）
export function calculateVillageOfficialBenefit(data) {
  return request({
    url: '/shebao/villageOfficial/calculateBenefit',
    method: 'post',
    data: data,
    headers: { repeatSubmit: false }
  })
}

// 新增村干部信息
export function addVillageOfficial(data) {
  return request({
    url: '/shebao/villageOfficial',
    method: 'post',
    data: data
  })
}

// 修改村干部信息
export function updateVillageOfficial(data) {
  return request({
    url: '/shebao/villageOfficial',
    method: 'put',
    data: data
  })
}

// 删除村干部信息
export function delVillageOfficial(id) {
  return request({
    url: '/shebao/villageOfficial/' + id,
    method: 'delete'
  })
}

// 导出村干部信息
export function exportVillageOfficial(query) {
  return request({
    url: '/shebao/villageOfficial/export',
    method: 'post',
    data: query
  })
}
