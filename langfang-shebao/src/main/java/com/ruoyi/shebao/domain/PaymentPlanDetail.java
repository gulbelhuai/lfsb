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

    private BigDecimal distributionAmount;

    private String grantOrg;

    private String accountName;

    private String bankAccount;

    private String relationToInsured;

    private String delFlag;
}
