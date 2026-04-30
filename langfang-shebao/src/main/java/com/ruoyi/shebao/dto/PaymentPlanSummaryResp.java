package com.ruoyi.shebao.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentPlanSummaryResp
{
    private Long id;
    private Long planId;
    private String businessPeriod;
    private String subsidyType;
    private String grantOrg;
    private Integer totalCount;
    private BigDecimal totalAmount;
}
