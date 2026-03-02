package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 拆迁居民信息对象 shebao_demolition_resident
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_demolition_resident")
public class DemolitionResident extends BaseDomain
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID（关联shebao_subsidy_person.id） */
    @Excel(name = "被补贴人ID")
    private Long subsidyPersonId;

    /** 拆迁事由 */
    @Excel(name = "拆迁事由")
    private String demolitionReason;

    /** 拆迁时间 */
    @Excel(name = "拆迁时间", dateFormat = "yyyy-MM-dd")
    private LocalDate demolitionTime;

    /** 认定为拆迁居民时间 */
    @Excel(name = "认定为拆迁居民时间", dateFormat = "yyyy-MM-dd")
    private LocalDate recognitionTime;
}
