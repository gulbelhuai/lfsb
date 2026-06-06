package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FinanceBenefitRecoveryListReq extends PageReq
{
    /** 补贴人姓名（模糊） */
    private String personName;

    /** 补贴人身份证号（模糊） */
    private String idCardNo;

    /** 补贴类型 */
    private String subsidyType;

    /** 追回状态（0未追回 1已追回） */
    private String recoveryStatus;
}
