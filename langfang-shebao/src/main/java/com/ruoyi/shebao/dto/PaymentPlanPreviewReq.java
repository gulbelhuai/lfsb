package com.ruoyi.shebao.dto;

import lombok.Data;

@Data
public class PaymentPlanPreviewReq
{
    /** normal/second */
    private String determinationType;
    /** yyyy-MM */
    private String businessPeriod;
}
