package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 被征地参保补贴账户对象 shebao_expropriatee_subsidy
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_expropriatee_subsidy")
public class ExpropriateeSubsidy extends BaseDomain
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID（关联shebao_subsidy_person.id） */
    @Excel(name = "被补贴人ID")
    private Long subsidyPersonId;

    /** 征地批次 */
    @Excel(name = "征地批次")
    private String landRequisitionBatch;

    /** 征地时所在村街 */
    @Excel(name = "征地时所在村街")
    private String villageStreet;

    /** 基准日 */
    @Excel(name = "基准日", dateFormat = "yyyy-MM-dd")
    private LocalDate baseDate;

    /** 职工身份缴纳职工养老月数 */
    @Excel(name = "职工身份缴纳职工养老月数")
    private Integer employeePensionMonths;

    /** 灵活就业身份缴纳职工养老保险月数 */
    @Excel(name = "灵活就业身份缴纳职工养老保险月数")
    private Integer flexibleEmploymentMonths;

    /** 已领取困难人员社会保险补贴月数 */
    @Excel(name = "已领取困难人员社会保险补贴月数")
    private BigDecimal difficultySubsidyMonths;

    /** 截至基准日的年龄 */
    @Excel(name = "截至基准日的年龄")
    private Integer ageAtBaseDate;

    /** 被征地参保补贴年限 */
    @Excel(name = "被征地参保补贴年限")
    private BigDecimal subsidyYears;

    /** 补贴金额 */
    @Excel(name = "补贴金额")
    private BigDecimal subsidyAmount;

    /** 选择参加城乡居保（0否 1是） */
    @Excel(name = "选择参加城乡居保", readConverterExp = "0=否,1=是")
    private String joinUrbanRuralInsurance;

    /** 选择参加职工养老（0否 1是） */
    @Excel(name = "选择参加职工养老", readConverterExp = "0=否,1=是")
    private String joinEmployeePension;

    /** 是否已领取职工养老保险待遇（0未领取 1已领取） */
    @Excel(name = "职工养老保险", readConverterExp = "0=未领取,1=已领取")
    private String hasEmployeePension;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
}
