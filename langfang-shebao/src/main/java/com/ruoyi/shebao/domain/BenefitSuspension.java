package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 待遇暂停主表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_benefit_suspension")
public class BenefitSuspension extends BaseDomain
{
    /** 待遇核定主表ID */
    private Long determinationId;

    /** 人员ID */
    private Long subsidyPersonId;

    /** 身份证号快照 */
    private String idCardNo;

    /** 暂停年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date pauseMonth;

    /** 暂停原因（字典：pause_reason） */
    private String pauseReason;

    /** 状态（0正常 1停用） */
    private String status;
}
