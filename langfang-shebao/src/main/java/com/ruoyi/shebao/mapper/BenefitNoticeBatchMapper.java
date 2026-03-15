package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.BenefitNoticeBatch;
import com.ruoyi.shebao.dto.BenefitNoticeBatchListReq;
import com.ruoyi.shebao.dto.BenefitNoticeBatchResp;
import com.ruoyi.shebao.dto.BenefitNoticeDetailListReq;
import com.ruoyi.shebao.dto.BenefitNoticeDetailResp;
import com.ruoyi.shebao.dto.BenefitNoticeExportRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BenefitNoticeBatchMapper extends BaseMapper<BenefitNoticeBatch>
{
    Page<BenefitNoticeBatchResp> selectBatchPage(Page<BenefitNoticeBatchResp> page, @Param("req") BenefitNoticeBatchListReq req);

    BenefitNoticeBatchResp selectBatchByBatchNo(@Param("batchNo") String batchNo);

    List<BenefitNoticeDetailResp> selectDetailList(@Param("req") BenefitNoticeDetailListReq req);

    List<BenefitNoticeExportRow> selectExportRows(@Param("noticeMonth") String noticeMonth, @Param("batchNo") String batchNo);
}
