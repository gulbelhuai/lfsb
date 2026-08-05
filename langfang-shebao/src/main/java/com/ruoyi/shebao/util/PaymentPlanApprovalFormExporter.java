package com.ruoyi.shebao.util;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.PaymentPlan;
import com.ruoyi.shebao.dto.PaymentPlanSummaryResp;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 财务批次审批单（廊坊开发区xx补贴发放汇总表）导出
 */
public final class PaymentPlanApprovalFormExporter
{
    private PaymentPlanApprovalFormExporter()
    {
    }

    public static byte[] build(PaymentPlan plan, List<PaymentPlanSummaryResp> summaryList) throws Exception
    {
        if (summaryList == null || summaryList.isEmpty())
        {
            throw new ServiceException("该批次无汇总数据，无法导出审批单");
        }
        String shortType = subsidyShort(plan.getSubsidyType());
        if (StringUtils.isEmpty(shortType))
        {
            throw new ServiceException("不支持的补贴类型，无法导出审批单");
        }

        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            Sheet sheet = wb.createSheet("签字");
            sheet.setColumnWidth(0, 9 * 256);
            sheet.setColumnWidth(1, (int) (30.25 * 256));
            sheet.setColumnWidth(2, (int) (17.93 * 256));
            sheet.setColumnWidth(3, (int) (25.88 * 256));

            CellStyle titleStyle = createStyle(wb, 18, true, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
            CellStyle headerStyle = createStyle(wb, 12, false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
            CellStyle dataStyle = createStyle(wb, 12, false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
            CellStyle totalStyle = createStyle(wb, 12, false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
            CellStyle signStyle = createStyle(wb, 11, false, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
            CellStyle dateStyle = createStyle(wb, 12, false, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, false);
            DataFormat dataFormat = wb.createDataFormat();
            dateStyle.setDataFormat(dataFormat.getFormat("yyyy\"年\"m\"月\"d\"日\""));

            // 标题
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(60);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("廊坊开发区" + shortType + "补贴发放汇总表");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            // 空行（与模板一致，保持默认行高）
            Row blankRow = sheet.createRow(1);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 2));

            // 表头
            Row header = sheet.createRow(2);
            header.setHeightInPoints(26);
            String[] headers = {"序号", "村（居）委会", "发放人数", "应发金额汇总"};
            for (int i = 0; i < headers.length; i++)
            {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int dataStart = 3;
            for (int i = 0; i < summaryList.size(); i++)
            {
                PaymentPlanSummaryResp s = summaryList.get(i);
                if (StringUtils.isEmpty(s.getVillageName()))
                {
                    throw new ServiceException("汇总存在空村委会，无法导出审批单，请检查批次明细数据");
                }
                String villageCol = buildVillageLabel(s.getVillageName(), s.getBusinessPeriod(), s.getSubsidyType());
                Row row = sheet.createRow(dataStart + i);
                row.setHeightInPoints(26);
                Cell c0 = row.createCell(0);
                c0.setCellValue(i + 1);
                c0.setCellStyle(dataStyle);
                Cell c1 = row.createCell(1);
                c1.setCellValue(villageCol);
                c1.setCellStyle(dataStyle);
                Cell c2 = row.createCell(2);
                c2.setCellValue(s.getTotalCount() == null ? 0 : s.getTotalCount());
                c2.setCellStyle(dataStyle);
                Cell c3 = row.createCell(3);
                c3.setCellValue(amountDouble(s.getTotalAmount()));
                c3.setCellStyle(dataStyle);
            }

            int totalRowIdx = dataStart + summaryList.size();
            int dataEnd = totalRowIdx - 1;
            Row totalRow = sheet.createRow(totalRowIdx);
            totalRow.setHeightInPoints(26);
            Cell totalLabel = totalRow.createCell(0);
            totalLabel.setCellValue("合计");
            totalLabel.setCellStyle(totalStyle);
            totalRow.createCell(1).setCellStyle(totalStyle);
            sheet.addMergedRegion(new CellRangeAddress(totalRowIdx, totalRowIdx, 0, 1));
            Cell sumCount = totalRow.createCell(2);
            sumCount.setCellFormula("SUM(C" + (dataStart + 1) + ":C" + (dataEnd + 1) + ")");
            sumCount.setCellStyle(totalStyle);
            Cell sumAmount = totalRow.createCell(3);
            sumAmount.setCellFormula("SUM(D" + (dataStart + 1) + ":D" + (dataEnd + 1) + ")");
            sumAmount.setCellStyle(totalStyle);

            int signRowIdx = totalRowIdx + 1;
            Row signRow = sheet.createRow(signRowIdx);
            signRow.setHeightInPoints(26);
            Cell signCell = signRow.createCell(0);
            signCell.setCellValue("经办人：                 复核人：                    负责人：                     ");
            signCell.setCellStyle(signStyle);
            sheet.addMergedRegion(new CellRangeAddress(signRowIdx, signRowIdx, 0, 3));

            int dateRowIdx = signRowIdx + 1;
            Row dateRow = sheet.createRow(dateRowIdx);
            dateRow.setHeightInPoints(26);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue(java.sql.Date.valueOf(LocalDate.now()));
            dateCell.setCellStyle(dateStyle);
            sheet.addMergedRegion(new CellRangeAddress(dateRowIdx, dateRowIdx, 0, 3));

            wb.write(out);
            return out.toByteArray();
        }
    }

    private static String buildVillageLabel(String villageName, String businessPeriod, String subsidyType)
    {
        return safe(villageName) + monthOf(businessPeriod) + "月" + subsidyShort(subsidyType);
    }

    private static String subsidyShort(String subsidyType)
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

    private static String monthOf(String businessPeriod)
    {
        if (StringUtils.isEmpty(businessPeriod) || !businessPeriod.contains("-"))
        {
            throw new ServiceException("汇总业务期无效，无法导出审批单");
        }
        String[] parts = businessPeriod.split("-");
        if (parts.length < 2)
        {
            throw new ServiceException("汇总业务期无效，无法导出审批单");
        }
        try
        {
            return String.valueOf(Integer.parseInt(parts[1]));
        }
        catch (NumberFormatException e)
        {
            throw new ServiceException("汇总业务期月份无效，无法导出审批单");
        }
    }

    private static double amountDouble(BigDecimal amount)
    {
        return amount == null ? 0D : amount.doubleValue();
    }

    private static String safe(String v)
    {
        return v == null ? "" : v.trim();
    }

    private static CellStyle createStyle(XSSFWorkbook wb, int fontSize, boolean bold,
                                         HorizontalAlignment hAlign, VerticalAlignment vAlign, boolean border)
    {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) fontSize);
        font.setBold(bold);
        style.setFont(font);
        style.setAlignment(hAlign);
        style.setVerticalAlignment(vAlign);
        if (border)
        {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }
        return style;
    }
}
