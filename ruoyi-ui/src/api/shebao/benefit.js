import request from '@/utils/request'

// 查询预到龄通知批次列表
export function listBenefitNoticeBatch(query) {
  return request({
    url: '/shebao/benefit/notice/batch/list',
    method: 'get',
    params: query
  })
}

// 查询预到龄通知批次详情
export function getBenefitNoticeBatch(batchNo) {
  return request({
    url: `/shebao/benefit/notice/batch/${batchNo}`,
    method: 'get'
  })
}

// 查询预到龄通知明细
export function listBenefitNoticeDetail(query) {
  return request({
    url: '/shebao/benefit/notice/detail/list',
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

// 查询待遇核定详情
export function getBenefitDetermination(id) {
  return request({
    url: `/shebao/benefit/determination/${id}`,
    method: 'get'
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

// 更新待遇核定
export function updateBenefitDetermination(data) {
  return request({
    url: '/shebao/benefit/determination',
    method: 'put',
    data: data
  })
}

// 批量待遇核定
export function batchBenefitDetermination(data) {
  const formData = new FormData()
  formData.append('file', data.file)
  if (data.noticeBatchNo) {
    formData.append('noticeBatchNo', data.noticeBatchNo)
  }
  ;(data.attachments || []).forEach(file => formData.append('attachments', file))
  return request({
    url: '/shebao/benefit/determination/batch',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 上传待遇核定材料
export function uploadBenefitAttachment(determinationId, file) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('determinationId', determinationId)
  return request({
    url: '/shebao/benefit/determination/attachment/upload',
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

// 批量审核通过
export function batchApproveBenefitReview(data) {
  return request({
    url: '/shebao/benefit/review/batchApprove',
    method: 'post',
    data
  })
}
