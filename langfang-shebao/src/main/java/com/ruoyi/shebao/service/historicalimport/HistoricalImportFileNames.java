package com.ruoyi.shebao.service.historicalimport;

import com.ruoyi.common.utils.StringUtils;

/**
 * 历史数据导入相关文件命名
 */
public final class HistoricalImportFileNames
{
    private static final String FAILURE_STORED_PREFIX = "failure_";
    private static final String SOURCE_STORED_PREFIX = "import_";
    private static final String FAILURE_STORED_SUFFIX = ".xlsx";

    private HistoricalImportFileNames()
    {
    }

    public static String templateDownloadName(String subsidyTypeLabel)
    {
        return String.format("历史数据录入模板_%s_%d.xlsx", subsidyTypeLabel, System.currentTimeMillis());
    }

    /** 磁盘存储文件名（仅 ASCII，避免生产环境 Path 编码问题） */
    public static String storedFailureFileName()
    {
        return storedFailureFileName(System.currentTimeMillis());
    }

    public static String storedFailureFileName(long timestamp)
    {
        return FAILURE_STORED_PREFIX + timestamp + FAILURE_STORED_SUFFIX;
    }

    /** 导入源文件磁盘存储名（仅 ASCII） */
    public static String storedSourceFileName(String originalFileName)
    {
        return storedSourceFileName(System.currentTimeMillis(), resolveExtension(originalFileName));
    }

    public static String storedSourceFileName(long timestamp, String extension)
    {
        String ext = StringUtils.isBlank(extension) ? ".xlsx" : extension;
        if (!ext.startsWith("."))
        {
            ext = "." + ext;
        }
        return SOURCE_STORED_PREFIX + timestamp + ext.toLowerCase();
    }

    public static String resolveExtension(String originalFileName)
    {
        if (StringUtils.isBlank(originalFileName))
        {
            return ".xlsx";
        }
        int dot = originalFileName.lastIndexOf('.');
        if (dot < 0 || dot == originalFileName.length() - 1)
        {
            return ".xlsx";
        }
        return originalFileName.substring(dot);
    }

    /** 失败记录下载展示名 */
    public static String failureDownloadName(String subsidyTypeLabel, long timestamp)
    {
        return String.format("导入失败记录_%s_%d.xlsx", subsidyTypeLabel, timestamp);
    }

    public static String failureDownloadName(String subsidyTypeLabel)
    {
        return failureDownloadName(subsidyTypeLabel, System.currentTimeMillis());
    }

    /**
     * 根据库内路径解析失败记录下载文件名（不依赖 Path.of，兼容历史中文文件名）。
     */
    public static String resolveFailureDownloadName(String failureFilePath, String subsidyTypeLabel)
    {
        String storedName = extractPathFileName(failureFilePath);
        if (StringUtils.isBlank(storedName))
        {
            return null;
        }
        Long timestamp = extractFailureTimestamp(storedName);
        if (timestamp != null && StringUtils.isNotBlank(subsidyTypeLabel))
        {
            return failureDownloadName(subsidyTypeLabel, timestamp);
        }
        return storedName;
    }

    public static String extractPathFileName(String failureFilePath)
    {
        if (StringUtils.isBlank(failureFilePath))
        {
            return null;
        }
        String normalized = failureFilePath.replace('\\', '/');
        int lastSlash = normalized.lastIndexOf('/');
        return lastSlash >= 0 ? normalized.substring(lastSlash + 1) : normalized;
    }

    public static Long extractFailureTimestamp(String fileName)
    {
        if (StringUtils.isBlank(fileName))
        {
            return null;
        }
        if (fileName.startsWith(FAILURE_STORED_PREFIX) && fileName.endsWith(FAILURE_STORED_SUFFIX))
        {
            String ts = fileName.substring(FAILURE_STORED_PREFIX.length(), fileName.length() - FAILURE_STORED_SUFFIX.length());
            if (StringUtils.isNumeric(ts))
            {
                return Long.parseLong(ts);
            }
        }
        int underscore = fileName.lastIndexOf('_');
        int dot = fileName.lastIndexOf('.');
        if (underscore >= 0 && dot > underscore)
        {
            String ts = fileName.substring(underscore + 1, dot);
            if (StringUtils.isNumeric(ts))
            {
                return Long.parseLong(ts);
            }
        }
        return null;
    }
}
