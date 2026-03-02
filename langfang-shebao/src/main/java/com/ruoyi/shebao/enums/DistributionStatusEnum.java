package com.ruoyi.shebao.enums;

import lombok.Getter;

/**
 * 发放状态枚举
 * 
 * @author ruoyi
 */
@Getter
public enum DistributionStatusEnum {
    
    NOT_DISTRIBUTED("0", "未发放"),
    PENDING_REVIEW("1", "待审核"),
    PENDING_DISTRIBUTION("2", "待发放"),
    REJECTED("3", "已拒绝"),
    DISTRIBUTED("4", "已发放");
    
    /** 状态编码 */
    private final String code;
    
    /** 状态名称 */
    private final String name;
    
    DistributionStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    /**
     * 根据编码获取枚举
     */
    public static DistributionStatusEnum getByCode(String code) {
        for (DistributionStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
