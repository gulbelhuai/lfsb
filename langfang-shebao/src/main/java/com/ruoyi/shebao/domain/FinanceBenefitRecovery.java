package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 财务-待遇追回记录
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_finance_benefit_recovery")
public class FinanceBenefitRecovery extends BaseDomain
{
    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 身份证号快照 */
    private String idCardNo;

    /** 补贴类型 */
    private String subsidyType;

    /** 关联暂停主表ID */
    private Long suspensionId;

    /** 关联暂停明细ID */
    private Long suspensionItemId;

    /** 追回开始年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date recoverStartMonth;

    /** 追回终止年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date recoverEndMonth;

    /** 需追回金额 */
    private BigDecimal recoverAmount;

    /** 追回状态（0未追回 1已追回） */
    private String recoveryStatus;

    /** 追回时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recoveryTime;

    /** 关联财务账户明细ID */
    private Long accountTransactionId;
}
