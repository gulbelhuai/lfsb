package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.BenefitNoticeBatch;
import com.ruoyi.shebao.dto.BenefitNoticeBatchListReq;
import com.ruoyi.shebao.dto.BenefitNoticeBatchResp;
import com.ruoyi.shebao.dto.BenefitNoticeExportRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BenefitNoticeBatchMapper extends BaseMapper<BenefitNoticeBatch>
{
    Page<BenefitNoticeBatchResp> selectPage(Page<BenefitNoticeBatchResp> page, @Param("req") BenefitNoticeBatchListReq req);

    List<BenefitNoticeExportRow> selectExportRows(@Param("req") BenefitNoticeBatchListReq req);
}
