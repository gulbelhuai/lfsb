import request from '@/utils/request'

// 查询批次列表
export function listBatch(query) {
  return request({
    url: '/shebao/finance/batch/list',
    method: 'get',
    params: query
  })
}

// 查询批次详情
export function getBatchDetail(batchNo) {
  return request({
    url: `/shebao/finance/batch/detail/${batchNo}`,
    method: 'get'
  })
}

// 生成银行文件
export function generateBankFile(batchNo) {
  return request({
    url: `/shebao/finance/bank/file/${batchNo}`,
    method: 'get',
    responseType: 'blob'
  })
}

// 提交银行发放
export function submitToBank(data) {
  return request({
    url: '/shebao/finance/bank/submit',
    method: 'post',
    data: data
  })
}

// 导入发放结果
export function importPaymentResult(formData) {
  return request({
    url: '/shebao/finance/bank/import',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 查询失败记录
export function listFailureRecords(query) {
  return request({
    url: '/shebao/finance/failure/list',
    method: 'get',
    params: query
  })
}

// 失败处理
export function handleFailure(data) {
  return request({
    url: '/shebao/finance/failure/handle',
    method: 'post',
    data: data
  })
}

// 查询财务账户
export function listFinanceAccount(query) {
  return request({
    url: '/shebao/finance/account/list',
    method: 'get',
    params: query
  })
}

// 查询账户余额
export function getAccountBalance(accountType) {
  return request({
    url: `/shebao/finance/account/balance/${accountType}`,
    method: 'get'
  })
}
