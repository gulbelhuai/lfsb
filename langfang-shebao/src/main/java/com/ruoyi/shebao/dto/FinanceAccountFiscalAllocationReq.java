package com.ruoyi.shebao.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 财政拨款请求
 */
@Data
public class FinanceAccountFiscalAllocationReq
{
    /** 拨款金额，须大于0 */
    private BigDecimal amount;

    /** 备注 */
    private String remark;
}
