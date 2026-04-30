package com.ruoyi.shebao.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentPlanDetailResp
{
    private Long id;
    private Long planId;
    private Long determinationId;
    private Long determinationItemId;
    private String subsidyType;
    private String streetName;
    private String villageName;
    private String personName;
    private String idCardNo;
    private String businessPeriod;
    private String paymentMonth;
    private BigDecimal distributionAmount;
    private String grantOrg;
    private String accountName;
    private String bankAccount;
    private String relationToInsured;
}
