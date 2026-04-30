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
 * 支付计划汇总表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_payment_plan_summary")
public class PaymentPlanSummary extends BaseEntity
{
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long planId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate businessPeriod;

    private String subsidyType;

    private String grantOrg;

    private Integer totalCount;

    private BigDecimal totalAmount;

    private String delFlag;
}
