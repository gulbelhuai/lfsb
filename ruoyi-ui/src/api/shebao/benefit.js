import request from '@/utils/request'

// 查询预到龄通知列表
export function listBenefitNotice(query) {
  return request({
    url: '/shebao/benefit/notice/list',
    method: 'get',
    params: query
  })
}

// 生成预到龄通知
export function generateBenefitNotice(data) {
  return request({
    url: '/shebao/benefit/notice/generate',
    method: 'post',
    data: data
  })
}

// 查询待遇核定列表
export function listBenefitDetermination(query) {
  return request({
    url: '/shebao/benefit/determination/list',
    method: 'get',
    params: query
  })
}

// 单个待遇核定
export function addBenefitDetermination(data) {
  return request({
    url: '/shebao/benefit/determination',
    method: 'post',
    data: data
  })
}

// 批量待遇核定
export function batchBenefitDetermination(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/shebao/benefit/determination/batch',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 提交待遇核定
export function submitBenefitDetermination(id) {
  return request({
    url: `/shebao/benefit/determination/submit/${id}`,
    method: 'post'
  })
}

// 查询待遇核定审核列表
export function listBenefitReview(query) {
  return request({
    url: '/shebao/benefit/review/list',
    method: 'get',
    params: query
  })
}

// 审核通过
export function reviewBenefitPass(id, remark) {
  return request({
    url: `/shebao/benefit/review/approve/${id}`,
    method: 'post',
    params: { remark }
  })
}

// 审核驳回
export function reviewBenefitReject(id, remark) {
  return request({
    url: `/shebao/benefit/review/reject/${id}`,
    method: 'post',
    params: { reason: remark }
  })
}
