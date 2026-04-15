package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.dto.BenefitNoticeBatchListReq;
import com.ruoyi.shebao.dto.BenefitNoticeBatchResp;
import com.ruoyi.shebao.dto.BenefitNoticeExportRow;
import com.ruoyi.shebao.mapper.BenefitNoticeBatchMapper;
import com.ruoyi.shebao.service.IBenefitNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BenefitNoticeServiceImpl implements IBenefitNoticeService
{
    private final BenefitNoticeBatchMapper benefitNoticeBatchMapper;

    @Override
    public Page<BenefitNoticeBatchResp> selectPage(BenefitNoticeBatchListReq req)
    {
        Page<BenefitNoticeBatchResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return benefitNoticeBatchMapper.selectPage(page, req);
    }

    @Override
    public List<BenefitNoticeExportRow> selectExportRows(BenefitNoticeBatchListReq req)
    {
        return benefitNoticeBatchMapper.selectExportRows(req);
    }
}
