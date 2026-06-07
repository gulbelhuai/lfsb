package com.ruoyi.shebao.service.historicalimport;

/**
 * 历史数据导入相关文件命名
 */
public final class HistoricalImportFileNames
{
    private HistoricalImportFileNames()
    {
    }

    public static String templateDownloadName(String subsidyTypeLabel)
    {
        return String.format("历史数据录入模板_%s_%d.xlsx", subsidyTypeLabel, System.currentTimeMillis());
    }

    public static String failureFileName(String subsidyTypeLabel)
    {
        return String.format("导入失败记录_%s_%d.xlsx", subsidyTypeLabel, System.currentTimeMillis());
    }
}
