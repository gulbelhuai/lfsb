package com.ruoyi.shebao.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PaymentPlanPreviewResp
{
    private String determinationType;
    private String businessPeriod;
    private String operatorName;
    private String operatorTime;
    private Integer totalCount = 0;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private List<PaymentPlanSummaryResp> summaryList = new ArrayList<>();
    private List<PaymentPlanDetailResp> detailList = new ArrayList<>();
}
