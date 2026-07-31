package com.ruoyi.shebao.dto;

import lombok.Data;

@Data
public class PaymentPlanPreviewReq
{
    /** normal/second */
    private String determinationType;
    /** yyyy-MM */
    private String businessPeriod;
    /** 补贴类型 */
    private String subsidyType;
    /** 重算时排除的支付计划ID（避免本计划明细被防重挡住） */
    private Long excludePlanId;
}
