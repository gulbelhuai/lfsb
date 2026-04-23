package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 待遇核定明细（每种补贴一行）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("benefit_determination_item")
public class BenefitDeterminationItem extends BaseDomain
{
    /** 核定主表ID */
    private Long determinationId;

    /** 补贴类型：land_loss / demolition / village_official */
    private String subsidyType;

    /** 认定时所在村街 */
    private String villageStreet;

    /** 征地/拆迁时间（yyyy-MM-dd），村干部为空 */
    private java.time.LocalDate eventDate;

    /** 补贴标准（月标准/或村干部计算后的标准） */
    private BigDecimal subsidyStandard;

    /** 享受开始年月 */
    private Integer benefitStartYear;

    /** 享受开始月 */
    private Integer benefitStartMonth;

    /** 补发月数 */
    private Integer benefitMonths;

    /** 补发金额 */
    private BigDecimal benefitAmount;

    /** 待遇状态（0正常 1暂停） */
    private String benefitStatus;

    /** 暂停开始年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date pauseStartMonth;
}

