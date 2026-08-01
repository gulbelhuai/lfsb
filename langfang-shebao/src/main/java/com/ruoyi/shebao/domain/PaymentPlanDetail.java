package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 支付计划明细表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_payment_plan_detail")
public class PaymentPlanDetail extends BaseEntity
{
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long planId;

    private Long determinationId;

    private Long determinationItemId;

    private String subsidyType;

    private String streetName;

    private String villageName;

    private String personName;

    private String idCardNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate businessPeriod;

    private String paymentMonth;

    /** 当月金额 */
    private BigDecimal monthlyAmount;

    /** 补发金额 */
    private BigDecimal supplementAmount;

    /** 补发所属期起始 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate supplementStartMonth;

    /** 补发所属期终止 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate supplementEndMonth;

    /** 补发来源(determination/resume) */
    private String supplementSource;

    /** 补发来源明细ID */
    private Long supplementSourceId;

    private BigDecimal distributionAmount;

    private String grantOrg;

    private String accountName;

    private String bankAccount;

    private String relationToInsured;

    /** 发放结果(success成功/failed失败) */
    private String distributionResult;

    /** 发放失败原因 */
    private String failReason;

    private String delFlag;
}
