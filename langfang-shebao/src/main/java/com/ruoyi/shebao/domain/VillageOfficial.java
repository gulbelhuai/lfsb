package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 村干部信息对象 shebao_village_official
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_village_official")
public class VillageOfficial extends BaseDomain
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID（关联shebao_subsidy_person.id） */
    @Excel(name = "被补贴人ID")
    private Long subsidyPersonId;

    /** 累计任职年限 */
    @Excel(name = "累计任职年限")
    private BigDecimal totalServiceYears;

    /** 村干部补贴标准（金额，元） */
    @Excel(name = "村干部补贴标准")
    private BigDecimal subsidyAmount;

    /** 是否违法乱纪或判刑（0否 1是） */
    @Excel(name = "是否违法乱纪或判刑", readConverterExp = "0=否,1=是")
    private String hasViolation;

    /** 认定时所在村街 */
    @Excel(name = "认定时所在村街")
    private String villageStreet;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
}
