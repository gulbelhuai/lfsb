package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 预到龄通知明细对象 shebao_benefit_notice_detail
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_benefit_notice_detail")
public class BenefitNoticeDetail extends BaseDomain
{
    private Long noticeBatchId;

    private String batchNo;

    private Long subsidyPersonId;

    private String userCode;

    private String name;

    private String idCardNo;

    private String subsidyType;

    private LocalDate birthday;

    private LocalDate retirementDate;

    private Long determinationId;

    private String determinationStatus;

    private String materialStatus;

    private String reviewStatus;
}
