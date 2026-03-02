package com.ruoyi.shebao.enums;

import lombok.Getter;

/**
 * 补贴类型枚举
 * 
 * @author ruoyi
 */
@Getter
public enum SubsidyTypeEnum {
    
    LAND_LOSS("1", "失地居民补贴", "shebao_land_loss_resident"),
    EXPROPRIATEE("2", "被征地居民补贴", "shebao_expropriatee_subsidy"),
    DEMOLITION("3", "拆迁居民补贴", "shebao_demolition_resident"),
    VILLAGE_OFFICIAL("4", "村干部补贴", "shebao_village_official"),
    TEACHER("5", "教龄补助", "shebao_teacher_subsidy");
    
    /** 补贴类型编码 */
    private final String code;
    
    /** 补贴类型名称 */
    private final String name;
    
    /** 对应的表名 */
    private final String tableName;
    
    SubsidyTypeEnum(String code, String name, String tableName) {
        this.code = code;
        this.name = name;
        this.tableName = tableName;
    }
    
    /**
     * 根据编码获取枚举
     */
    public static SubsidyTypeEnum getByCode(String code) {
        for (SubsidyTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 根据表名获取枚举
     */
    public static SubsidyTypeEnum getByTableName(String tableName) {
        for (SubsidyTypeEnum type : values()) {
            if (type.getTableName().equals(tableName)) {
                return type;
            }
        }
        return null;
    }
}
