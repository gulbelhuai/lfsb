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
    /** 当月金额 */
    private BigDecimal monthlyAmount;
    /** 补发金额 */
    private BigDecimal supplementAmount;
    /** 补发所属期起始 yyyy-MM */
    private String supplementStartMonth;
    /** 补发所属期终止 yyyy-MM */
    private String supplementEndMonth;
    /** 补发来源 determination/resume */
    private String supplementSource;
    private Long supplementSourceId;
    /** 发放金额（当月+补发） */
    private BigDecimal distributionAmount;
    private String grantOrg;
    private String accountName;
    private String bankAccount;
    private String relationToInsured;
    /** 发放结果(success成功/failed失败) */
    private String distributionResult;
    /** 发放失败原因 */
    private String failReason;
}
