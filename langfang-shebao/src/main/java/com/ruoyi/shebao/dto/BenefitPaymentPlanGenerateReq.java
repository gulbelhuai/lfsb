package com.ruoyi.shebao.dto;

import lombok.Data;

/**
 * 按通知批次生成支付计划请求
 */
@Data
public class BenefitPaymentPlanGenerateReq
{
    private String noticeBatchNo;
}
