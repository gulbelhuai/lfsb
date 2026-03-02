package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 补贴发放记录对象 shebao_subsidy_distribution
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_subsidy_distribution")
public class SubsidyDistribution extends BaseDomain
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID（关联shebao_subsidy_person.id） */
    @Excel(name = "被补贴人ID")
    private Long subsidyPersonId;

    /** 补贴类型（1失地居民补贴 2被征地居民补贴 3拆迁居民补贴 4村干部补贴 5教龄补助） */
    @Excel(name = "补贴类型", readConverterExp = "1=失地居民补贴,2=被征地居民补贴,3=拆迁居民补贴,4=村干部补贴,5=教龄补助")
    private String subsidyType;

    /** 补贴身份记录ID（关联具体补贴类型表的ID） */
    @Excel(name = "补贴身份记录ID")
    private Long subsidyRecordId;

    /** 发放金额 */
    @Excel(name = "发放金额")
    private BigDecimal distributionAmount;

    /** 发放日期 */
    @Excel(name = "发放日期", dateFormat = "yyyy-MM-dd")
    private LocalDate distributionDate;

    /** 发放状态（0未发放 1待审核 2待发放 3已拒绝 4已发放） */
    @Excel(name = "发放状态", readConverterExp = "0=未发放,1=待审核,2=待发放,3=已拒绝,4=已发放")
    private String distributionStatus;

    /** 审核说明 */
    @Excel(name = "审核说明")
    private String reviewRemark;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
}
