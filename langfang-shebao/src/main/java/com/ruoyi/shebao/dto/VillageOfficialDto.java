package com.ruoyi.shebao.dto;

import lombok.Data;

/**
 * 村干部补贴DTO
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Data
public class VillageOfficialDto
{
    /** 总任职年限 */
    private Integer totalServiceYears;

    /** 是否有违纪 */
    private String hasViolation;

    /** 备注 */
    private String remark;
}
