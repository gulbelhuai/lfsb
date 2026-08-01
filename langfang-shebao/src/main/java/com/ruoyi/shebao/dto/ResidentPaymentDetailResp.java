package com.ruoyi.shebao.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 居民查询：预发放 / 发放记录（数据源为支付计划明细）
 */
@Data
public class ResidentPaymentDetailResp
{
    private Long id;
    private Long planId;
    /** 支付计划批次号 */
    private String batchNo;
    /** 发放类型 normal/second */
    private String determinationType;
    private Long subsidyPersonId;
    private String subsidyType;
    private String streetName;
    private String villageName;
    private String personName;
    private String idCardNo;
    /** 业务期 yyyy-MM */
    private String businessPeriod;
    private BigDecimal monthlyAmount;
    private BigDecimal supplementAmount;
    /** 补发起始 yyyy-MM */
    private String supplementStartMonth;
    /** 补发终止 yyyy-MM */
    private String supplementEndMonth;
    private BigDecimal distributionAmount;
    /** 发放日期 yyyy-MM-dd（财务通过日） */
    private String distributionDate;
    private String grantOrg;
    private String accountName;
    private String bankAccount;
    private String relationToInsured;
    /**
     * 发放状态（仅发放记录使用）：distributing=发放中 / paid=已发放 / failed=发放失败
     */
    private String payStatus;
    /** 失败原因 */
    private String failReason;
    /** 计划银行发放状态 pending/submitted/completed */
    private String planDistributionStatus;
    /** 明细银行结果 success/failed */
    private String distributionResult;
}
