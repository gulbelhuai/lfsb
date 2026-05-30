package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务账户明细列表行
 */
@Data
public class FinanceAccountTransactionListResp
{
    private Long id;
    private Long accountId;
    private String accountName;
    private String batchNo;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balance;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionTime;

    private String remark;
}
