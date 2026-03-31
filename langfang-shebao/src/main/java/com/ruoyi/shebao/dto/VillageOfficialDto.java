package com.ruoyi.shebao.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 村干部补贴DTO
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Data
public class VillageOfficialDto
{
    /** 累计任职年限 */
    private BigDecimal totalServiceYears;

    /** 村干部补贴标准（元） */
    private BigDecimal subsidyAmount;

    /** 是否有违纪 */
    private String hasViolation;

    /** 认定时所在村街 */
    private String villageStreet;

    /** 备注 */
    private String remark;

    /** 提交时间 */
    private LocalDateTime createTime;
}
