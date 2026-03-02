import request from '@/utils/request'

// 查询教龄补助登记列表
export function listTeacherSubsidy(query) {
  return request({
    url: '/shebao/teacherSubsidy/list',
    method: 'get',
    params: query
  })
}

// 查询教龄补助登记详情（表单）
export function getTeacherSubsidy(id) {
  return request({
    url: '/shebao/teacherSubsidy/' + id,
    method: 'get'
  })
}

// 根据身份证号智能回填表单数据
export function getTeacherSubsidyFormDataByIdCardNo(idCardNo) {
  return request({
    url: '/shebao/teacherSubsidy/getFormDataByIdCardNo',
    method: 'get',
    params: { idCardNo }
  })
}

// 新增教龄补助登记
export function addTeacherSubsidy(data) {
  return request({
    url: '/shebao/teacherSubsidy',
    method: 'post',
    data
  })
}

// 修改教龄补助登记
export function updateTeacherSubsidy(data) {
  return request({
    url: '/shebao/teacherSubsidy',
    method: 'put',
    data
  })
}

// 删除教龄补助登记
export function delTeacherSubsidy(ids) {
  return request({
    url: '/shebao/teacherSubsidy/' + ids,
    method: 'delete'
  })
}

// 提交审核（预留）
export function submitTeacherSubsidy(id, remark) {
  return request({
    url: `/shebao/teacherSubsidy/submit/${id}`,
    method: 'post',
    data: remark
  })
}

