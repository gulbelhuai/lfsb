import request from '@/utils/request'

/** 财务批次管理：支付计划列表（已进入财务流程） */
export function listFinanceBatch(query) {
  return request({
    url: '/shebao/finance/batch/list',
    method: 'get',
    params: query
  })
}

export function financeBatchPass(id, data) {
  return request({
    url: `/shebao/finance/batch/${id}/finance-pass`,
    method: 'post',
    data: data || {}
  })
}

export function financeBatchReject(id, data) {
  return request({
    url: `/shebao/finance/batch/${id}/finance-reject`,
    method: 'post',
    data
  })
}

export function financeBatchReviewPass(id, data) {
  return request({
    url: `/shebao/finance/batch/${id}/review-pass`,
    method: 'post',
    data: data || {}
  })
}

export function financeBatchReviewReject(id, data) {
  return request({
    url: `/shebao/finance/batch/${id}/review-reject`,
    method: 'post',
    data
  })
}

export function financeBatchApprovePass(id, data) {
  return request({
    url: `/shebao/finance/batch/${id}/approve-pass`,
    method: 'post',
    data: data || {}
  })
}

export function financeBatchApproveReject(id, data) {
  return request({
    url: `/shebao/finance/batch/${id}/approve-reject`,
    method: 'post',
    data
  })
}

/** 银行发放：财务已通过的支付计划列表 */
export function listBankBatch(query) {
  return request({
    url: '/shebao/finance/bank/list',
    method: 'get',
    params: query
  })
}

/** 银行发放：该批次涉及的代发银行(langfang/boc) */
export function getBankExports(id) {
  return request({
    url: `/shebao/finance/bank/${id}/banks`,
    method: 'get'
  })
}

/** 银行发放：导出某代发银行的代发文件 */
export function exportBankFile(id, bank) {
  return request({
    url: `/shebao/finance/bank/${id}/export`,
    method: 'get',
    params: { bank },
    responseType: 'blob'
  })
}

/** 银行发放：提交银行 */
export function submitBankDistribution(id) {
  return request({
    url: `/shebao/finance/bank/${id}/submit`,
    method: 'post'
  })
}

/** 银行发放：下载失败数据导入模板 */
export function downloadFailTemplate() {
  return request({
    url: '/shebao/finance/bank/import-fail/template',
    method: 'get',
    responseType: 'blob'
  })
}

/** 银行发放：标记已完成 */
export function completeBankDistribution(id) {
  return request({
    url: `/shebao/finance/bank/${id}/complete`,
    method: 'post'
  })
}

/** 待遇追回列表 */
export function listBenefitRecovery(query) {
  return request({
    url: '/shebao/finance/recovery/list',
    method: 'get',
    params: query
  })
}

/** 待遇追回-确认已追回 */
export function confirmBenefitRecovery(id) {
  return request({
    url: `/shebao/finance/recovery/${id}/confirm`,
    method: 'post'
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

// 查询财务账户概览
export function getFinanceAccountOverview() {
  return request({
    url: '/shebao/finance/account/overview',
    method: 'get'
  })
}

// 查询财务账户（兼容）
export function listFinanceAccount(query) {
  return request({
    url: '/shebao/finance/account/list',
    method: 'get',
    params: query
  })
}

// 财务账户下拉
export function listFinanceAccountSelect() {
  return request({
    url: '/shebao/finance/account/selectList',
    method: 'get'
  })
}

// 账户明细列表
export function listFinanceAccountTransactions(query) {
  return request({
    url: '/shebao/finance/account/transaction/list',
    method: 'get',
    params: query
  })
}

// 财政拨款
export function fiscalAllocationAccount(id, data) {
  return request({
    url: `/shebao/finance/account/${id}/fiscal-allocation`,
    method: 'post',
    data: data || {}
  })
}

// 查询账户余额
export function getAccountBalance(accountType) {
  return request({
    url: `/shebao/finance/account/balance/${accountType}`,
    method: 'get'
  })
}
