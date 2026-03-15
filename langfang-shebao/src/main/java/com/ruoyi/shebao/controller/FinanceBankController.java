package com.ruoyi.shebao.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.shebao.service.IDistributionBatchService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/shebao/finance/bank")
public class FinanceBankController extends BaseController
{
    @Autowired
    private IDistributionBatchService distributionBatchService;

    @PreAuthorize("@ss.hasPermi('shebao:finance:bank:file:*')")
    @GetMapping("/file/{batchNo}")
    public ResponseEntity<byte[]> generateFile(@PathVariable String batchNo)
    {
        byte[] content = distributionBatchService.generateBankFile(batchNo);
        String fileName = URLEncoder.encode("bank_" + batchNo + ".txt", StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    @PreAuthorize("@ss.hasPermi('shebao:finance:bank:submit:*')")
    @Log(title = "提交银行发放", businessType = BusinessType.UPDATE)
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody Map<String, Object> params)
    {
        String batchNo = params.get("batchNo") == null ? null : params.get("batchNo").toString();
        return toAjax(distributionBatchService.submitToBank(batchNo));
    }

    @PreAuthorize("@ss.hasPermi('shebao:finance:bank:import:*')")
    @Log(title = "导入银行发放结果", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importResult(@RequestParam String batchNo, @RequestPart("file") MultipartFile file) throws Exception
    {
        if (file == null || file.isEmpty())
        {
            return AjaxResult.error("请上传银行结果文件");
        }
        return AjaxResult.success(distributionBatchService.importBankResult(batchNo, parseImportRows(file)));
    }

    private List<Map<String, String>> parseImportRows(MultipartFile file) throws Exception
    {
        List<Map<String, String>> rows = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream))
        {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() < 2)
            {
                throw new ServiceException("导入文件为空或缺少数据行");
            }
            Row headerRow = sheet.getRow(0);
            Map<Integer, String> headers = new HashMap<>();
            DataFormatter formatter = new DataFormatter();
            for (Cell cell : headerRow)
            {
                headers.put(cell.getColumnIndex(), normalizeHeader(formatter.formatCellValue(cell)));
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++)
            {
                Row row = sheet.getRow(i);
                if (row == null)
                {
                    continue;
                }
                Map<String, String> item = new HashMap<>();
                boolean hasValue = false;
                for (Map.Entry<Integer, String> entry : headers.entrySet())
                {
                    Cell cell = row.getCell(entry.getKey());
                    String value = formatter.formatCellValue(cell);
                    if (value != null && !value.trim().isEmpty())
                    {
                        hasValue = true;
                    }
                    item.put(entry.getValue(), value == null ? "" : value.trim());
                }
                if (hasValue)
                {
                    rows.add(adaptImportRow(item));
                }
            }
        }
        return rows;
    }

    private String normalizeHeader(String header)
    {
        return header == null ? "" : header.replace(" ", "").trim().toLowerCase(Locale.ROOT);
    }

    private Map<String, String> adaptImportRow(Map<String, String> row)
    {
        Map<String, String> result = new HashMap<>();
        result.put("id", firstNonBlank(row, "记录id", "id", "distributionid", "发放记录id"));
        result.put("name", firstNonBlank(row, "姓名", "name"));
        result.put("idCardNo", firstNonBlank(row, "身份证号", "idcardno", "证件号码"));
        result.put("result", firstNonBlank(row, "结果", "发放结果", "状态", "result"));
        result.put("reason", firstNonBlank(row, "失败原因", "原因", "备注", "reason"));
        return result;
    }

    private String firstNonBlank(Map<String, String> row, String... keys)
    {
        for (String key : keys)
        {
            String value = row.get(normalizeHeader(key));
            if (value != null && !value.isBlank())
            {
                return value;
            }
        }
        return "";
    }
}
