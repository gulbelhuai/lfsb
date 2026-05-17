package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PaymentPlanAuditResp
{
    private Long id;
    private Long planId;
    private String operationStatus;
    private String approvalStage;
    private String operatorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;
    private String remark;
}
