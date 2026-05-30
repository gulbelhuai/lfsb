package com.ruoyi.shebao.dto;

import lombok.Data;

/**
 * 银行发放失败导入行
 */
@Data
public class PaymentPlanBankFailureRow
{
    /** 身份证号 */
    private String idCardNo;
    /** 失败原因 */
    private String reason;
}
