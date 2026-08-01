package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.dto.DistributionRecordExportResp;
import com.ruoyi.shebao.dto.DistributionRecordListReq;
import com.ruoyi.shebao.dto.ResidentPaymentDetailResp;

import java.util.List;

/**
 * 补贴发放记录（财务通过的支付计划明细）查询
 */
public interface DistributionRecordService
{
    Page<ResidentPaymentDetailResp> selectList(DistributionRecordListReq req);

    List<DistributionRecordExportResp> selectExportList(DistributionRecordListReq req);
}
