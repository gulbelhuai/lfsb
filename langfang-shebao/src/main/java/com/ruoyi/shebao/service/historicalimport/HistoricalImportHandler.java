package com.ruoyi.shebao.service.historicalimport;

import com.ruoyi.shebao.dto.historicalimport.HistoricalImportResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 历史数据导入处理器
 */
public interface HistoricalImportHandler
{
    String subsidyType();

    void exportTemplate(jakarta.servlet.http.HttpServletResponse response) throws Exception;

    List<?> parseRows(MultipartFile file) throws Exception;

    HistoricalImportResult process(List<?> rows, String fileName, String sourceFilePath);
}
