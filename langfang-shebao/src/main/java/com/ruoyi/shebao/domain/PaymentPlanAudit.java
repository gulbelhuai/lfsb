package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_payment_plan_audit")
public class PaymentPlanAudit extends BaseEntity
{
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private String operationStatus;
    /** subsidy=补贴审核, finance=财务审核 */
    private String approvalStage;
    private String operatorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operationTime;
    private String delFlag;
}
