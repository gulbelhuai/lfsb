package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.dto.BenefitNoticeBatchListReq;
import com.ruoyi.shebao.dto.BenefitNoticeBatchResp;
import com.ruoyi.shebao.dto.BenefitNoticeExportRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预到龄通知：即时查询人员，不落批次表。
 */
@Mapper
public interface BenefitNoticeBatchMapper
{
    Page<BenefitNoticeBatchResp> selectPage(Page<BenefitNoticeBatchResp> page, @Param("req") BenefitNoticeBatchListReq req);

    List<BenefitNoticeExportRow> selectExportRows(@Param("req") BenefitNoticeBatchListReq req);
}
