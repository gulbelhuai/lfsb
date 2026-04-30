package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentPlanListResp
{
    private Long id;
    private String determinationType;
    private String businessPeriod;
    private Integer totalCount;
    private BigDecimal totalAmount;
    private String operatorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operatorTime;
    private String approvalStatus;
}
