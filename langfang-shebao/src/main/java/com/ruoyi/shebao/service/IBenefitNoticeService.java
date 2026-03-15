package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.BenefitNoticeBatch;
import com.ruoyi.shebao.dto.BenefitNoticeBatchListReq;
import com.ruoyi.shebao.dto.BenefitNoticeBatchResp;
import com.ruoyi.shebao.dto.BenefitNoticeDetailListReq;
import com.ruoyi.shebao.dto.BenefitNoticeDetailResp;
import com.ruoyi.shebao.dto.BenefitNoticeExportRow;
import com.ruoyi.shebao.dto.BenefitNoticeGenerateReq;

import java.util.List;

public interface IBenefitNoticeService
{
    Page<BenefitNoticeBatchResp> selectBatchPage(BenefitNoticeBatchListReq req);

    BenefitNoticeBatchResp selectBatchByBatchNo(String batchNo);

    List<BenefitNoticeDetailResp> selectDetailList(BenefitNoticeDetailListReq req);

    BenefitNoticeBatch generateNoticeBatch(BenefitNoticeGenerateReq req);

    List<BenefitNoticeExportRow> selectExportRows(String noticeMonth, String batchNo);

    void refreshBatchStatistics(String batchNo);
}
