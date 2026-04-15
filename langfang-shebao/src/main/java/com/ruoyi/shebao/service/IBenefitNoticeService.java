package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.dto.BenefitNoticeBatchListReq;
import com.ruoyi.shebao.dto.BenefitNoticeBatchResp;
import com.ruoyi.shebao.dto.BenefitNoticeExportRow;

import java.util.List;

public interface IBenefitNoticeService
{
    Page<BenefitNoticeBatchResp> selectPage(BenefitNoticeBatchListReq req);

    List<BenefitNoticeExportRow> selectExportRows(BenefitNoticeBatchListReq req);
}
