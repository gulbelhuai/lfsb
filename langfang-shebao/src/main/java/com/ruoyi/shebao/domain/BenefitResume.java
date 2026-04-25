package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 待遇恢复主表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_benefit_resume")
public class BenefitResume extends BaseDomain
{
    /** 待遇核定主表ID */
    private Long determinationId;

    /** 人员ID */
    private Long subsidyPersonId;

    /** 身份证号快照 */
    private String idCardNo;

    /** 恢复年月（主记录时间） */
    @JsonFormat(pattern = "yyyy-MM")
    private Date resumeMonth;

    /** 恢复原因（列表展示，汇总） */
    private String resumeReason;

    /** 状态（0正常 1停用） */
    private String status;
}
