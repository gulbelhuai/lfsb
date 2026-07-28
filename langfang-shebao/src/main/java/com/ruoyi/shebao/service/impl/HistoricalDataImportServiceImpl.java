package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.shebao.domain.HistoricalImportBatch;
import com.ruoyi.shebao.dto.historicalimport.HistoricalImportBatchListResp;
import com.ruoyi.shebao.dto.historicalimport.HistoricalImportResult;
import com.ruoyi.shebao.enums.HistoricalImportSubsidyType;
import com.ruoyi.shebao.mapper.HistoricalImportBatchMapper;
import com.ruoyi.shebao.service.HistoricalDataImportService;
import com.ruoyi.shebao.service.historicalimport.HistoricalImportFileNames;
import com.ruoyi.shebao.service.historicalimport.HistoricalImportHandler;
import com.ruoyi.shebao.service.historicalimport.HistoricalImportTemplateExporter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoricalDataImportServiceImpl implements HistoricalDataImportService
{
    private final HistoricalImportBatchMapper historicalImportBatchMapper;
    private final List<HistoricalImportHandler> handlers;
    private final HistoricalImportTemplateExporter historicalImportTemplateExporter;

    @Override
    public Page<HistoricalImportBatchListResp> selectBatchList(long pageNum, long pageSize)
    {
        Page<HistoricalImportBatchListResp> page = new Page<>(pageNum, pageSize);
        Page<HistoricalImportBatchListResp> result = historicalImportBatchMapper.selectBatchList(page);
        if (result.getRecords() != null)
        {
            for (HistoricalImportBatchListResp row : result.getRecords())
            {
                try
                {
                    row.setSubsidyTypeLabel(HistoricalImportSubsidyType.fromCode(row.getSubsidyType()).getLabel());
                }
                catch (Exception ignored)
                {
                    row.setSubsidyTypeLabel(row.getSubsidyType());
                }
                if (StringUtils.isNotBlank(row.getFailureFilePath()))
                {
                    row.setFailureFileName(HistoricalImportFileNames.resolveFailureDownloadName(
                            row.getFailureFilePath(), row.getSubsidyTypeLabel()));
                }
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> listSubsidyTypes()
    {
        return Arrays.stream(HistoricalImportSubsidyType.values())
                .map(type -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("code", type.getCode());
                    item.put("label", type.getLabel());
                    item.put("supported", type.isSupported());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void downloadTemplate(String subsidyType, HttpServletResponse response) throws Exception
    {
        HistoricalImportSubsidyType type = HistoricalImportSubsidyType.fromCode(subsidyType);
        if (!type.isSupported())
        {
            throw new ServiceException(type.getLabel() + "历史数据导入尚未支持");
        }
        FileUtils.setAttachmentResponseHeader(response, HistoricalImportFileNames.templateDownloadName(type.getLabel()));
        resolveHandler(subsidyType).exportTemplate(response);
    }

    @Override
    public HistoricalImportResult importData(String subsidyType, MultipartFile file) throws Exception
    {
        if (file == null || file.isEmpty())
        {
            throw new ServiceException("请选择导入文件");
        }
        String originalName = file.getOriginalFilename();
        if (StringUtils.isBlank(originalName)
                || (!StringUtils.endsWithIgnoreCase(originalName, ".xlsx")
                && !StringUtils.endsWithIgnoreCase(originalName, ".xls")))
        {
            throw new ServiceException("仅支持 Excel 文件（.xlsx / .xls）");
        }

        HistoricalImportHandler handler = resolveHandler(subsidyType);
        List<?> rows;
        try
        {
            rows = handler.parseRows(file);
        }
        catch (Exception ex)
        {
            throw new ServiceException("导入文件格式错误或模板结构不正确：" + ex.getMessage());
        }
        if (CollectionUtils.isEmpty(rows))
        {
            throw new ServiceException("导入文件无有效数据，请检查模板与内容");
        }

        String sourceFilePath;
        try
        {
            sourceFilePath = historicalImportTemplateExporter.saveImportSourceFile(file.getBytes(), originalName);
        }
        catch (Exception ex)
        {
            throw new ServiceException("保存导入文件失败：" + ex.getMessage());
        }
        return handler.process(rows, originalName, sourceFilePath);
    }

    @Override
    public void downloadFailureFile(Long batchId, HttpServletResponse response) throws Exception
    {
        HistoricalImportBatch batch = historicalImportBatchMapper.selectById(batchId);
        if (batch == null || !"0".equals(batch.getDelFlag()))
        {
            throw new ServiceException("导入记录不存在");
        }
        if (StringUtils.isBlank(batch.getFailureFilePath()))
        {
            throw new ServiceException("该批次无失败记录文件");
        }
        File file = resolveProfileFile(batch.getFailureFilePath());
        if (file == null || !file.exists())
        {
            throw new ServiceException("失败记录文件不存在或已被清理");
        }
        String subsidyTypeLabel = HistoricalImportSubsidyType.fromCode(batch.getSubsidyType()).getLabel();
        String downloadName = HistoricalImportFileNames.resolveFailureDownloadName(
                batch.getFailureFilePath(), subsidyTypeLabel);
        if (StringUtils.isBlank(downloadName))
        {
            downloadName = file.getName();
        }
        FileUtils.setAttachmentResponseHeader(response, downloadName);
        try (FileInputStream input = new FileInputStream(file))
        {
            input.transferTo(response.getOutputStream());
        }
    }

    @Override
    public void downloadSourceFile(Long batchId, HttpServletResponse response) throws Exception
    {
        HistoricalImportBatch batch = historicalImportBatchMapper.selectById(batchId);
        if (batch == null || !"0".equals(batch.getDelFlag()))
        {
            throw new ServiceException("导入记录不存在");
        }
        if (StringUtils.isBlank(batch.getSourceFilePath()))
        {
            throw new ServiceException("该批次无导入源文件");
        }
        File file = resolveProfileFile(batch.getSourceFilePath());
        if (file == null || !file.exists())
        {
            throw new ServiceException("导入源文件不存在或已被清理");
        }
        String downloadName = StringUtils.isNotBlank(batch.getFileName()) ? batch.getFileName() : file.getName();
        FileUtils.setAttachmentResponseHeader(response, downloadName);
        try (FileInputStream input = new FileInputStream(file))
        {
            input.transferTo(response.getOutputStream());
        }
    }

    private File resolveProfileFile(String profileRelativePath)
    {
        if (StringUtils.isBlank(profileRelativePath))
        {
            return null;
        }
        String relative = profileRelativePath.replace("/profile/", "").replace("/", File.separator);
        return new File(RuoYiConfig.getProfile(), relative);
    }

    private HistoricalImportHandler resolveHandler(String subsidyType)
    {
        HistoricalImportSubsidyType type = HistoricalImportSubsidyType.fromCode(subsidyType);
        if (!type.isSupported())
        {
            throw new ServiceException(type.getLabel() + "历史数据导入尚未支持");
        }
        Map<String, HistoricalImportHandler> handlerMap = handlers.stream()
                .collect(Collectors.toMap(HistoricalImportHandler::subsidyType, Function.identity(), (a, b) -> a));
        HistoricalImportHandler handler = handlerMap.get(subsidyType);
        if (handler == null)
        {
            throw new ServiceException("未找到对应导入处理器");
        }
        return handler;
    }
}
