package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.dto.historicalimport.HistoricalImportBatchListResp;
import com.ruoyi.shebao.dto.historicalimport.HistoricalImportResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface HistoricalDataImportService
{
    Page<HistoricalImportBatchListResp> selectBatchList(long pageNum, long pageSize);

    List<Map<String, Object>> listSubsidyTypes();

    void downloadTemplate(String subsidyType, HttpServletResponse response) throws Exception;

    HistoricalImportResult importData(String subsidyType, MultipartFile file) throws Exception;

    void downloadFailureFile(Long batchId, HttpServletResponse response) throws Exception;
}
