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

    private String approvalStatus;

    private String operatorName;

    public LocalDate getBusinessPeriodDate()
    {
        if (businessPeriod == null || businessPeriod.isBlank())
        {
            return null;
        }
        return LocalDate.parse(businessPeriod + "-01");
    }
}
