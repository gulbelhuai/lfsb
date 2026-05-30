package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 银行发放失败明细列表查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PaymentPlanFailureListReq extends PageReq
{
    /** 姓名（模糊） */
    private String personName;

    /** 批次号（精确） */
    private String batchNo;

    /** 业务期 yyyy-MM */
    private String businessPeriod;

    /** 补贴类型 */
    private String subsidyType;

    /** 失败原因（模糊） */
    private String failReason;

    public LocalDate getBusinessPeriodDate()
    {
        if (businessPeriod == null || businessPeriod.isBlank())
        {
            return null;
        }
        return LocalDate.parse(businessPeriod + "-01");
    }
}
