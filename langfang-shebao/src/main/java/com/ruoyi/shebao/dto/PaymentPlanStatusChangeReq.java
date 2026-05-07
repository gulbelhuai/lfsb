package com.ruoyi.shebao.dto;

import lombok.Data;

@Data
public class PaymentPlanStatusChangeReq
{
    private String targetStatus;
    private String remark;
}
