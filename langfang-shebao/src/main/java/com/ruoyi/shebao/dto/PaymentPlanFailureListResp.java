package com.ruoyi.shebao.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 银行发放失败明细列表行
 */
@Data
public class PaymentPlanFailureListResp
{
    /** 明细ID */
    private Long id;

    private Long planId;

    private String batchNo;

    private String businessPeriod;

    private String subsidyType;

    private String personName;

    private String idCardNo;

    private BigDecimal distributionAmount;

    private String bankAccount;

    private String accountName;

    private String grantOrg;

    private String failReason;

    private String villageName;

    private String streetName;

    private String paymentMonth;
}
