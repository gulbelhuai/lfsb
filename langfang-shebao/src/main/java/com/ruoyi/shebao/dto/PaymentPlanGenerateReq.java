package com.ruoyi.shebao.dto;

import lombok.Data;

@Data
public class PaymentPlanGenerateReq
{
    private Long planId;
    private String determinationType;
    private String businessPeriod;
    /** 补贴类型 */
    private String subsidyType;
    /** 保存动作：draft/pending_review */
    private String targetStatus;
    private String remark;
}
