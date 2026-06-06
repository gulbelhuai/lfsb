package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务账户明细 finance_account_transaction
 */
@Data
@TableName("finance_account_transaction")
public class FinanceAccountTransaction implements Serializable
{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long accountId;

    private String accountName;

    private String batchNo;

    /**
     * 关联业务ID：补贴发放=发放记录ID，待遇追回=追回记录ID，财政拨款为空
     */
    private Long businessId;

    /** fiscal_allocation / subsidy_distribution / benefit_recovery */
    private String transactionType;

    /** 正数=收入，负数=支出 */
    private BigDecimal amount;

    private BigDecimal balance;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionTime;

    private String remark;

    private String delFlag;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
