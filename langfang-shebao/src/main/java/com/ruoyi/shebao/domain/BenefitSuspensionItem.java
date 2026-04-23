package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 待遇暂停追回明细表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_benefit_suspension_item")
public class BenefitSuspensionItem extends BaseDomain
{
    /** 暂停主表ID */
    private Long suspensionId;

    /** 核定子表ID */
    private Long determinationItemId;

    /** 补贴类型 */
    private String subsidyType;

    /** 享受开始年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date benefitStartMonth;

    /** 补贴标准 */
    private BigDecimal subsidyStandard;

    /** 是否需要追回（0否 1是） */
    private String needRecover;

    /** 追回开始年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date recoverStartMonth;

    /** 追回结束年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date recoverEndMonth;

    /** 追回月数 */
    private Integer recoverMonths;

    /** 追回金额 */
    private BigDecimal recoverAmount;

    /** 状态（0正常 1停用） */
    private String status;
}
