package com.ruoyi.shebao.dto;

import lombok.Data;

/**
 * 居民详细信息DTO
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Data
public class ResidentDetailInfoDto
{
    /** 居民基本信息 */
    private ResidentBasicInfo residentInfo;

    /** 补贴信息 */
    private SubsidyInfoDto subsidyInfo;
}
