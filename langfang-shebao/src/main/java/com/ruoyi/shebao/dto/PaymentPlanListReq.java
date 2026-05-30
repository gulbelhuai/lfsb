package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 支付计划主表列表查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PaymentPlanListReq extends PageReq
{
    private String determinationType;

    private String businessPeriod;

    /** 补贴类型 */
    private String subsidyType;

    private String approvalStatus;

    /** 发放状态(pending/submitted/completed) */
    private String distributionStatus;

    private String operatorName;

    /** 按批次号精确查询 */
    private String batchNo;

    /** 财务状态筛选 */
    private String financeStatus;

    /** 仅查询已进入财务流程的记录(finance_status 非空) */
    private Boolean financeEnteredOnly;

    public LocalDate getBusinessPeriodDate()
    {
        if (businessPeriod == null || businessPeriod.isBlank())
        {
            return null;
        }
        return LocalDate.parse(businessPeriod + "-01");
    }
}
