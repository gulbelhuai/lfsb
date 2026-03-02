package com.ruoyi.shebao.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 失地居民补贴DTO
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Data
public class LandLossResidentDto
{
    /** 认定时间 */
    private LocalDate recognitionTime;

    /** 征地时间 */
    private LocalDate landRequisitionTime;

    /** 完成补偿时间 */
    private LocalDate compensationCompleteTime;

    /** 备注 */
    private String remark;
}
