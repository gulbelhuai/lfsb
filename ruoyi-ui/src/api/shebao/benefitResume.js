import request from '@/utils/request'

export function listBenefitResume(query) {
  return request({
    url: '/shebao/management/resume/list',
    method: 'get',
    params: query
  })
}

export function getResumeCandidate(idCardNo) {
  return request({
    url: '/shebao/management/resume/candidate',
    method: 'get',
    params: { idCardNo }
  })
}

export function addBenefitResume(data) {
  return request({
    url: '/shebao/management/resume',
    method: 'post',
    data
  })
}

export function getBenefitResumeDetail(id) {
  return request({
    url: '/shebao/management/resume/' + id,
    method: 'get'
  })
}
