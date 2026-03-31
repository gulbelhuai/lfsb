package com.ruoyi.shebao.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 被征地居民补贴DTO
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Data
public class ExpropriateeSubsidyDto
{
    /** 征地批次 */
    private String landRequisitionBatch;

    /** 征地时所在村街 */
    private String villageStreet;

    /** 基准时间 */
    private LocalDate baseDate;

    /** 职工养老月数 */
    private Integer employeePensionMonths;

    /** 灵活就业月数 */
    private Integer flexibleEmploymentMonths;

    /** 困难补贴年限 */
    private Integer difficultySubsidyYears;

    /** 基准时年龄 */
    private Integer ageAtBaseDate;

    /** 补贴年限 */
    private Integer subsidyYears;

    /** 补贴金额 */
    private BigDecimal subsidyAmount;

    /** 参加城乡保险 */
    private String joinUrbanRuralInsurance;

    /** 参加职工养老 */
    private String joinEmployeePension;

    /** 已领取职工养老待遇 */
    private String hasEmployeePension;

    /** 备注 */
    private String remark;

    /** 提交时间 */
    private LocalDateTime createTime;
}
