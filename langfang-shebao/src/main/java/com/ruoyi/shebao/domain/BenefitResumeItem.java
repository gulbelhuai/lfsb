package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 待遇恢复明细表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_benefit_resume_item")
public class BenefitResumeItem extends BaseDomain
{
    /** 恢复主表ID */
    private Long resumeId;

    /** 核定子表ID */
    private Long determinationItemId;

    /** 补贴类型 */
    private String subsidyType;

    /** 暂停年月（快照） */
    @JsonFormat(pattern = "yyyy-MM")
    private Date pauseStartMonth;

    /** 是否需要恢复（0否 1是） */
    private String needResume;

    /** 恢复原因 */
    private String resumeReason;

    /** 恢复年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date resumeMonth;

    /** 补发开始年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date supplementStartMonth;

    /** 补发终止年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date supplementEndMonth;

    /** 补发月数 */
    private Integer supplementMonths;

    /** 补发金额 */
    private BigDecimal supplementAmount;

    /** 补贴标准（快照） */
    private BigDecimal subsidyStandard;

    /** 状态（0正常 1停用） */
    private String status;
}
