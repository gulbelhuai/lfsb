package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预到龄通知批次对象 shebao_benefit_notice_batch
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_benefit_notice_batch")
public class BenefitNoticeBatch extends BaseDomain
{
    private String batchNo;

    private String noticeMonth;

    private String retirementMonth;

    private Integer ageThreshold;

    private Integer totalCount;

    private Integer pendingReviewCount;

    private Integer approvedCount;

    private Integer rejectedCount;

    private String batchStatus;
}
