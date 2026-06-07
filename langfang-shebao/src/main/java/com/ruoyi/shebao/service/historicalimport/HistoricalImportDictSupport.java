package com.ruoyi.shebao.service.historicalimport;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 历史导入字典/选项校验
 */
public final class HistoricalImportDictSupport
{
    private HistoricalImportDictSupport()
    {
    }

    public static String requireDictByLabelOrValue(String dictType, String raw, String fieldName)
    {
        if (StringUtils.isBlank(raw))
        {
            throw new ServiceException(fieldName + "不能为空");
        }
        String trimmed = raw.trim();
        if (StringUtils.isNotBlank(DictUtils.getDictLabel(dictType, trimmed)))
        {
            return trimmed;
        }
        String value = DictUtils.getDictValue(dictType, trimmed);
        if (StringUtils.isBlank(value))
        {
            throw new ServiceException(fieldName + "不在字典范围内：" + trimmed);
        }
        return value;
    }

    public static String requireYesNo(String raw, String fieldName)
    {
        if (StringUtils.isBlank(raw))
        {
            throw new ServiceException(fieldName + "不能为空");
        }
        String trimmed = raw.trim();
        if ("0".equals(trimmed) || "否".equals(trimmed))
        {
            return "0";
        }
        if ("1".equals(trimmed) || "是".equals(trimmed))
        {
            return "1";
        }
        throw new ServiceException(fieldName + "只能填写：是/否（或0/1）");
    }

    public static String requireNotBlank(String raw, String fieldName)
    {
        if (StringUtils.isBlank(raw))
        {
            throw new ServiceException(fieldName + "不能为空");
        }
        return raw.trim();
    }
}
