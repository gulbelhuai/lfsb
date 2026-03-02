package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 支付计划列表查询请求
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PaymentPlanListReq extends PageReq
{
    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 补贴类型 */
    private String subsidyType;

    /** 发放状态 */
    private String distributionStatus;

    /** 发放日期开始 */
    private LocalDate distributionDateStart;

    /** 发放日期结束 */
    private LocalDate distributionDateEnd;
}
