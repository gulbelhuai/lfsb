package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 财务账户明细列表查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FinanceAccountTransactionListReq extends PageReq
{
    /** 账户ID */
    private Long accountId;

    /** 交易类型：fiscal_allocation / subsidy_distribution / benefit_recovery */
    private String transactionType;

    /** 交易日期起 yyyy-MM-dd */
    private String transactionDateStart;

    /** 交易日期止 yyyy-MM-dd（含当天） */
    private String transactionDateEnd;

    public LocalDateTime getTransactionTimeStart()
    {
        if (transactionDateStart == null || transactionDateStart.isBlank())
        {
            return null;
        }
        return LocalDate.parse(transactionDateStart).atStartOfDay();
    }

    public LocalDateTime getTransactionTimeEnd()
    {
        if (transactionDateEnd == null || transactionDateEnd.isBlank())
        {
            return null;
        }
        return LocalDate.parse(transactionDateEnd).plusDays(1).atStartOfDay().minusNanos(1);
    }
}
