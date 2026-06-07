package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.HistoricalImportBatch;
import com.ruoyi.shebao.dto.historicalimport.HistoricalImportBatchListResp;
import org.apache.ibatis.annotations.Param;

public interface HistoricalImportBatchMapper extends BaseMapper<HistoricalImportBatch>
{
    Page<HistoricalImportBatchListResp> selectBatchList(Page<HistoricalImportBatchListResp> page);
}
