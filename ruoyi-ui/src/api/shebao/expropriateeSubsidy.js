import request from '@/utils/request'

// 查询被征地参保补贴列表
export function listExpropriateeSubsidy(query) {
  return request({
    url: '/shebao/expropriateeSubsidy/list',
    method: 'get',
    params: query
  })
}

// 查询被征地参保补贴详细
export function getExpropriateeSubsidy(id) {
  return request({
    url: '/shebao/expropriateeSubsidy/' + id,
    method: 'get'
  })
}

// 根据身份证号获取表单数据（智能填充基础信息）
export function getFormDataByIdCardNo(idCardNo) {
  return request({
    url: '/shebao/expropriateeSubsidy/getFormDataByIdCardNo',
    method: 'get',
    params: { idCardNo }
  })
}

// 新增被征地参保补贴
export function addExpropriateeSubsidy(data) {
  return request({
    url: '/shebao/expropriateeSubsidy',
    method: 'post',
    data: data
  })
}

// 修改被征地参保补贴
export function updateExpropriateeSubsidy(data) {
  return request({
    url: '/shebao/expropriateeSubsidy',
    method: 'put',
    data: data
  })
}

// 删除被征地参保补贴
export function delExpropriateeSubsidy(id) {
  return request({
    url: '/shebao/expropriateeSubsidy/' + id,
    method: 'delete'
  })
}

// 导出被征地参保补贴
export function exportExpropriateeSubsidy(query) {
  return request({
    url: '/shebao/expropriateeSubsidy/export',
    method: 'post',
    data: query
  })
}

// 计算补贴数据
export function calculateSubsidy(data) {
  return request({
    url: '/shebao/expropriateeSubsidy/calculateSubsidy',
    method: 'post',
    data: data
  })
}
