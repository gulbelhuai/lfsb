package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务账户对象 finance_account
 *
 * @author ruoyi
 * @date 2025-01-20
 */
@Data
@TableName("finance_account")
public class FinanceAccount implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 账户名称 */
    private String accountName;

    /** 账户编号(对应表列 account_no) */
    @TableField("account_no")
    private String accountCode;

    /** 补贴类型(对应表列 subsidy_type) */
    @TableField("subsidy_type")
    private String accountType;

    /** 银行名称 */
    private String bankName;

    /** 银行账号(表中无此列，仅用于扩展) */
    @TableField(exist = false)
    private String bankAccount;

    /** 账户余额 */
    private BigDecimal balance;

    /** 状态（0正常 1停用） */
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /** 备注 */
    private String remark;
}
