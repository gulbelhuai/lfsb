package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.historicalimport.HistoricalImportBatchListResp;
import com.ruoyi.shebao.dto.historicalimport.HistoricalImportResult;
import com.ruoyi.shebao.service.HistoricalDataImportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/shebao/historicalImport")
@RequiredArgsConstructor
public class HistoricalDataImportController extends BaseController
{
    private final HistoricalDataImportService historicalDataImportService;

    @PreAuthorize("@ss.hasPermi('shebao:historicalImport:list')")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(defaultValue = "1") long pageNum,
                           @RequestParam(defaultValue = "10") long pageSize)
    {
        Page<HistoricalImportBatchListResp> page = historicalDataImportService.selectBatchList(pageNum, pageSize);
        return AjaxResult.success(page);
    }

    @PreAuthorize("@ss.hasPermi('shebao:historicalImport:list')")
    @GetMapping("/subsidyTypes")
    public AjaxResult subsidyTypes()
    {
        return AjaxResult.success(historicalDataImportService.listSubsidyTypes());
    }

    @PreAuthorize("@ss.hasPermi('shebao:historicalImport:import')")
    @PostMapping("/importTemplate")
    public void importTemplate(@RequestParam String subsidyType, HttpServletResponse response) throws Exception
    {
        historicalDataImportService.downloadTemplate(subsidyType, response);
    }

    @PreAuthorize("@ss.hasPermi('shebao:historicalImport:import')")
    @Log(title = "历史数据导入", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(@RequestParam String subsidyType, @RequestParam("file") MultipartFile file) throws Exception
    {
        HistoricalImportResult result = historicalDataImportService.importData(subsidyType, file);
        return AjaxResult.success(result.getMessage(), result);
    }

    @PreAuthorize("@ss.hasPermi('shebao:historicalImport:list')")
    @PostMapping("/failureFile/{batchId}")
    public void failureFile(@PathVariable Long batchId, HttpServletResponse response) throws Exception
    {
        historicalDataImportService.downloadFailureFile(batchId, response);
    }

    @PreAuthorize("@ss.hasPermi('shebao:historicalImport:list')")
    @PostMapping("/sourceFile/{batchId}")
    public void sourceFile(@PathVariable Long batchId, HttpServletResponse response) throws Exception
    {
        historicalDataImportService.downloadSourceFile(batchId, response);
    }
}
