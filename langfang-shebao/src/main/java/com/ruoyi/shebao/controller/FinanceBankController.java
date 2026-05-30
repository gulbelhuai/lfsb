package com.ruoyi.shebao.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.PaymentPlan;
import com.ruoyi.shebao.domain.PaymentPlanDetail;
import com.ruoyi.shebao.dto.PaymentPlanBankFailureRow;
import com.ruoyi.shebao.dto.PaymentPlanListReq;
import com.ruoyi.shebao.dto.PaymentPlanListResp;
import com.ruoyi.shebao.service.PaymentPlanService;
import com.ruoyi.shebao.service.impl.PaymentPlanServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 银行发放：基于批次管理(财务已通过)的支付计划进行代发文件导出、提交银行、导入失败数据、标记完成
 */
@RestController
@RequestMapping("/shebao/finance/bank")
public class FinanceBankController extends BaseController
{
    @Autowired
    private PaymentPlanService paymentPlanService;

    /** 廊坊银行代发模板表头 */
    private static final String[] LF_HEADERS = {
            "报盘日期", "本行单位账号", "报盘批号", "收款协议号", "付款协议号", "发生额方向",
            "单位名称", "单位地址", "业务类型号", "业务种类", "金额", "接收行行号",
            "对方开户行行号", "对方账号", "证件号码", "对方户名", "对方地址", "附言", "回执期限"
    };
    private static final String LF_UNIT_ACCOUNT = "31307060000120111000648";
    private static final String LF_UNIT_NAME = "廊坊经济技术开发区社会保险事业管理所";

    /** 银行发放列表：财务已通过的支付计划 */
    @PreAuthorize("@ss.hasPermi('shebao:finance:bank:list')")
    @GetMapping("/list")
    public TableDataInfo list(PaymentPlanListReq req)
    {
        if (req.getPageNum() == null)
        {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null)
        {
            req.setPageSize(10);
        }
        req.setFinanceStatus("finance_approved");
        Page<PaymentPlanListResp> page = paymentPlanService.selectPaymentPlanList(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    /** 该批次涉及的代发银行列表 */
    @PreAuthorize("@ss.hasPermi('shebao:finance:bank:export')")
    @GetMapping("/{id}/banks")
    public AjaxResult banks(@PathVariable("id") Long id)
    {
        return AjaxResult.success(paymentPlanService.selectAvailableBanks(id));
    }

    /** 导出某代发银行的代发文件 */
    @PreAuthorize("@ss.hasPermi('shebao:finance:bank:export')")
    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> export(@PathVariable("id") Long id, @RequestParam("bank") String bank) throws Exception
    {
        PaymentPlan plan = paymentPlanService.getBankPlan(id);
        List<PaymentPlanDetail> details = paymentPlanService.selectDetailsForBank(id, bank);
        if (details.isEmpty())
        {
            throw new ServiceException("该批次没有该发放机构的明细数据");
        }
        byte[] content;
        String fileName;
        if (PaymentPlanServiceImpl.BANK_LANGFANG.equals(bank))
        {
            content = buildLangfangXls(details);
            fileName = "廊坊银行代发_" + safe(plan.getBatchNo()) + ".xls";
        }
        else if (PaymentPlanServiceImpl.BANK_BOC.equals(bank))
        {
            content = buildBocCsv(details);
            fileName = "中国银行代发_" + safe(plan.getBatchNo()) + ".csv";
        }
        else
        {
            throw new ServiceException("不支持的代发银行");
        }
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    /** 提交银行（待发放 → 已提交银行） */
    @PreAuthorize("@ss.hasPermi('shebao:finance:bank:submit')")
    @Log(title = "银行发放-提交银行", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/submit")
    public AjaxResult submit(@PathVariable("id") Long id)
    {
        return toAjax(paymentPlanService.submitToBank(id));
    }

    /** 下载失败数据导入模板 */
    @PreAuthorize("@ss.hasPermi('shebao:finance:bank:importFail')")
    @GetMapping("/import-fail/template")
    public ResponseEntity<byte[]> importFailTemplate() throws Exception
    {
        byte[] content = buildFailTemplate();
        String encoded = URLEncoder.encode("发放失败导入模板.xls", StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    /** 导入失败数据（身份证号 + 失败原因） */
    @PreAuthorize("@ss.hasPermi('shebao:finance:bank:importFail')")
    @Log(title = "银行发放-导入失败数据", businessType = BusinessType.IMPORT)
    @PostMapping("/{id}/import-fail")
    public AjaxResult importFail(@PathVariable("id") Long id, @RequestPart("file") MultipartFile file) throws Exception
    {
        if (file == null || file.isEmpty())
        {
            return AjaxResult.error("请上传失败数据文件");
        }
        int matched = paymentPlanService.importBankFailures(id, parseFailureRows(file));
        return AjaxResult.success("已标记发放失败记录数：" + matched, matched);
    }

    /** 标记已完成 */
    @PreAuthorize("@ss.hasPermi('shebao:finance:bank:complete')")
    @Log(title = "银行发放-标记已完成", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/complete")
    public AjaxResult complete(@PathVariable("id") Long id)
    {
        return toAjax(paymentPlanService.completeDistribution(id));
    }

    // ------------------------------------------------------------------
    // 导出文件构建
    // ------------------------------------------------------------------

    private byte[] buildLangfangXls(List<PaymentPlanDetail> details) throws Exception
    {
        try (HSSFWorkbook wb = new HSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            Sheet sheet = wb.createSheet("发放明细");
            // 标题行（按补贴类型生成，与原模板格式一致）
            String subsidyShort = subsidyShort(details.get(0).getSubsidyType());
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("廊坊开发区" + subsidyShort + "补贴发放明细表");
            // 空行
            sheet.createRow(1);
            // 表头
            Row header = sheet.createRow(2);
            for (int i = 0; i < LF_HEADERS.length; i++)
            {
                header.createCell(i).setCellValue(LF_HEADERS[i]);
            }
            int rowIdx = 3;
            for (PaymentPlanDetail d : details)
            {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue("");                                  // 报盘日期
                row.createCell(1).setCellValue(LF_UNIT_ACCOUNT);                     // 本行单位账号
                row.createCell(2).setCellValue("");                                  // 报盘批号
                row.createCell(3).setCellValue("");                                  // 收款协议号
                row.createCell(4).setCellValue("");                                  // 付款协议号
                row.createCell(5).setCellValue("C");                                 // 发生额方向
                row.createCell(6).setCellValue(LF_UNIT_NAME);                        // 单位名称
                row.createCell(7).setCellValue("");                                  // 单位地址
                row.createCell(8).setCellValue("E101");                              // 业务类型号
                row.createCell(9).setCellValue("09001");                             // 业务种类
                row.createCell(10).setCellValue(amountText(d.getDistributionAmount())); // 金额
                row.createCell(11).setCellValue("");                                 // 接收行行号
                row.createCell(12).setCellValue("");                                 // 对方开户行行号
                row.createCell(13).setCellValue(safe(d.getBankAccount()));           // 对方账号
                row.createCell(14).setCellValue(safe(d.getIdCardNo()));              // 证件号码
                row.createCell(15).setCellValue(safe(d.getAccountName()));           // 对方户名
                row.createCell(16).setCellValue("");                                 // 对方地址
                row.createCell(17).setCellValue(langfangNote(d));                    // 附言
                row.createCell(18).setCellValue("");                                 // 回执期限
            }
            wb.write(out);
            return out.toByteArray();
        }
    }

    private byte[] buildFailTemplate() throws Exception
    {
        try (HSSFWorkbook wb = new HSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            Sheet sheet = wb.createSheet("失败数据");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("身份证号");
            header.createCell(1).setCellValue("失败原因");
            Row sample = sheet.createRow(1);
            sample.createCell(0).setCellValue("110101199001011234");
            sample.createCell(1).setCellValue("账号错误");
            sheet.setColumnWidth(0, 24 * 256);
            sheet.setColumnWidth(1, 30 * 256);
            wb.write(out);
            return out.toByteArray();
        }
    }

    private byte[] buildBocCsv(List<PaymentPlanDetail> details)
    {
        StringBuilder sb = new StringBuilder();
        // 表头信息行（保持原模板格式）
        sb.append("\"业务类型:\t\",\"C1-人民币/外币行内代付\t\",\"转出账号:\t\",\"9031360018534008\t\",\"币种:\t\",\"CNY-人民币\t\",\"业务摘要:\t\",\"EE-补贴\t\",,\n");
        sb.append(",,,,,,,,,\n");
        // 列头
        sb.append("序号,转入账号,转入名称,金额,转入行省行,证件类型,证件号码,备注,定期储蓄类别,错误标识\n");
        int seq = 1;
        for (PaymentPlanDetail d : details)
        {
            sb.append(seq++).append(',')
              .append(csv(d.getBankAccount())).append(',')
              .append(csv(d.getAccountName())).append(',')
              .append(csv(amountText(d.getDistributionAmount()))).append(',')
              .append("13-河北").append(',')
              .append("01-身份证").append(',')
              .append(csv(d.getIdCardNo())).append(',')
              .append(csv(bocNote(d))).append(',')
              .append(',')
              .append('\n');
        }
        // 中国银行代发常用 GBK 编码
        Charset gbk;
        try
        {
            gbk = Charset.forName("GBK");
        }
        catch (Exception e)
        {
            gbk = StandardCharsets.UTF_8;
        }
        return sb.toString().getBytes(gbk);
    }

    private String langfangNote(PaymentPlanDetail d)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(safe(d.getVillageName()));
        String month = monthOf(d.getPaymentMonth());
        if (!month.isEmpty())
        {
            sb.append(month).append("月");
        }
        sb.append(subsidyShort(d.getSubsidyType()));
        return sb.toString();
    }

    private String bocNote(PaymentPlanDetail d)
    {
        return safe(d.getVillageName()) + subsidyShort(d.getSubsidyType()) + "参保补贴";
    }

    private String subsidyShort(String subsidyType)
    {
        if (subsidyType == null)
        {
            return "";
        }
        switch (subsidyType)
        {
            case "land_loss":
            case "land_loss_resident":
                return "失地";
            case "expropriatee":
            case "expropriatee_subsidy":
                return "被征地";
            case "demolition":
            case "demolition_resident":
                return "拆迁";
            case "village_official":
                return "村干部";
            case "teacher":
            case "teacher_subsidy":
                return "教师";
            default:
                return "";
        }
    }

    private String monthOf(String paymentMonth)
    {
        if (StringUtils.isEmpty(paymentMonth) || !paymentMonth.contains("-"))
        {
            return "";
        }
        String[] parts = paymentMonth.split("-");
        if (parts.length < 2)
        {
            return "";
        }
        try
        {
            return String.valueOf(Integer.parseInt(parts[1]));
        }
        catch (NumberFormatException e)
        {
            return parts[1];
        }
    }

    private String amountText(BigDecimal amount)
    {
        if (amount == null)
        {
            return "0";
        }
        return amount.stripTrailingZeros().toPlainString();
    }

    private String csv(String value)
    {
        if (value == null)
        {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n"))
        {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String safe(String value)
    {
        return value == null ? "" : value;
    }

    // ------------------------------------------------------------------
    // 失败数据导入解析（身份证号 + 失败原因）
    // ------------------------------------------------------------------

    private List<PaymentPlanBankFailureRow> parseFailureRows(MultipartFile file) throws Exception
    {
        List<PaymentPlanBankFailureRow> rows = new ArrayList<>();
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
            int idCardCol = matchColumn(headers, "身份证号", "证件号码", "idcardno", "身份证");
            int reasonCol = matchColumn(headers, "失败原因", "原因", "备注", "reason");
            for (int i = 1; i <= sheet.getLastRowNum(); i++)
            {
                Row row = sheet.getRow(i);
                if (row == null)
                {
                    continue;
                }
                String idCard = idCardCol >= 0 ? formatter.formatCellValue(row.getCell(idCardCol)) : "";
                String reason = reasonCol >= 0 ? formatter.formatCellValue(row.getCell(reasonCol)) : "";
                if (StringUtils.isNotEmpty(idCard))
                {
                    PaymentPlanBankFailureRow item = new PaymentPlanBankFailureRow();
                    item.setIdCardNo(idCard.trim());
                    item.setReason(reason == null ? "" : reason.trim());
                    rows.add(item);
                }
            }
        }
        if (rows.isEmpty())
        {
            throw new ServiceException("未解析到有效的失败数据（需包含身份证号列）");
        }
        return rows;
    }

    private int matchColumn(Map<Integer, String> headers, String... keys)
    {
        Set<String> normalizedKeys = new HashSet<>();
        for (String key : keys)
        {
            normalizedKeys.add(normalizeHeader(key));
        }
        for (Map.Entry<Integer, String> entry : headers.entrySet())
        {
            if (normalizedKeys.contains(entry.getValue()))
            {
                return entry.getKey();
            }
        }
        return -1;
    }

    private String normalizeHeader(String header)
    {
        return header == null ? "" : header.replace(" ", "").trim().toLowerCase(Locale.ROOT);
    }
}
