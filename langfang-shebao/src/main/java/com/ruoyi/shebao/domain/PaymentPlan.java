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
import java.util.Date;

/**
 * 支付计划主表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_payment_plan")
public class PaymentPlan extends BaseEntity
{
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 核定方式(normal/second) */
    private String determinationType;

    /** 业务期(月初日期) */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate businessPeriod;

    /** 发放人次 */
    private Integer totalCount;

    /** 总金额 */
    private BigDecimal totalAmount;

    /** 经办人 */
    private String operatorName;

    /** 经办时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operatorTime;

    /** 审批状态 */
    private String approvalStatus;

    /** 删除标志 */
    private String delFlag;
}
