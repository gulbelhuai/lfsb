package com.ruoyi.shebao.enums;

import lombok.Getter;

/**
 * 历史数据导入补贴类型
 */
@Getter
public enum HistoricalImportSubsidyType
{
    LAND_LOSS_RESIDENT("land_loss_resident", "失地居民补贴", true),
    EXPROPRIATEE("expropriatee", "被征地农民补贴", false),
    DEMOLITION_RESIDENT("demolition_resident", "拆迁居民补贴", false),
    VILLAGE_OFFICIAL("village_official", "村干部补贴", false),
    TEACHER_SUBSIDY("teacher_subsidy", "教龄补助", false);

    private final String code;
    private final String label;
    private final boolean supported;

    HistoricalImportSubsidyType(String code, String label, boolean supported)
    {
        this.code = code;
        this.label = label;
        this.supported = supported;
    }

    public static HistoricalImportSubsidyType fromCode(String code)
    {
        for (HistoricalImportSubsidyType type : values())
        {
            if (type.code.equals(code))
            {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的补贴类型：" + code);
    }
}
