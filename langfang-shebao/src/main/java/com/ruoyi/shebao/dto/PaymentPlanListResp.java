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
    /** 补贴类型 */
    private String subsidyType;
    /** 批次号 */
    private String batchNo;
    private Integer totalCount;
    private BigDecimal totalAmount;
    private String operatorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operatorTime;
    private String approvalStatus;
    /** 财务状态 */
    private String financeStatus;
    /** 汇总涉及的补贴类型（展示用） */
    private String subsidyTypes;
    private String grantOrg;
}
