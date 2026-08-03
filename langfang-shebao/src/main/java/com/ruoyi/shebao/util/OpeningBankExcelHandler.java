package com.ruoyi.shebao.util;

import com.ruoyi.common.utils.poi.ExcelHandlerAdapter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Excel 导出：开户行编码 → 简称（支持逗号/顿号多值）
 */
public class OpeningBankExcelHandler implements ExcelHandlerAdapter
{
    @Override
    public Object format(Object value, String[] args, Cell cell, Workbook wb)
    {
        if (value == null)
        {
            return "";
        }
        String raw = String.valueOf(value).trim();
        if (raw.isEmpty())
        {
            return "";
        }
        if (!raw.contains(",") && !raw.contains("、"))
        {
            return OpeningBankUtils.getShortNameOrCode(raw);
        }
        return Arrays.stream(raw.split("[,、]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(OpeningBankUtils::getShortNameOrCode)
                .collect(Collectors.joining("、"));
    }
}
