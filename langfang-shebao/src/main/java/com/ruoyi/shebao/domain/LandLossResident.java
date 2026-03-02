package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 失地居民信息对象 shebao_land_loss_resident
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_land_loss_resident")
public class LandLossResident extends BaseDomain
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID（关联shebao_subsidy_person.id） */
    @Excel(name = "被补贴人ID")
    private Long subsidyPersonId;

    /** 征地时间 */
    @Excel(name = "征地时间", dateFormat = "yyyy-MM-dd")
    private LocalDate landRequisitionTime;

    /** 完成补偿时间 */
    @Excel(name = "完成补偿时间", dateFormat = "yyyy-MM-dd")
    private LocalDate compensationCompleteTime;

    /** 认定为失地居民时间 */
    @Excel(name = "认定为失地居民时间", dateFormat = "yyyy-MM-dd")
    private LocalDate recognitionTime;
}
