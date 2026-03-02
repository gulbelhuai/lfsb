package com.ruoyi.shebao.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 拆迁居民补贴DTO
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Data
public class DemolitionResidentDto
{
    /** 认定时间 */
    private LocalDate recognitionTime;

    /** 拆迁时间 */
    private LocalDate demolitionTime;

    /** 拆迁事由 */
    private String demolitionReason;

    /** 备注 */
    private String remark;
}
