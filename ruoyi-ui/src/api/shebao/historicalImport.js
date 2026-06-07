import request from '@/utils/request'

export function listHistoricalImport(query) {
  return request({
    url: '/shebao/historicalImport/list',
    method: 'get',
    params: query
  })
}

export function listHistoricalImportSubsidyTypes() {
  return request({
    url: '/shebao/historicalImport/subsidyTypes',
    method: 'get'
  })
}
