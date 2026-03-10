package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 待遇核定对象 benefit_determination
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("benefit_determination")
public class BenefitDetermination extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 补贴类型 */
    private String subsidyType;

    /** 到龄-年 */
    private Integer eligibleYear;

    /** 到龄-月 */
    private Integer eligibleMonth;

    /** 补贴享受开始-年 */
    private Integer benefitStartYear;

    /** 补贴享受开始-月 */
    private Integer benefitStartMonth;

    /** 发放银行ID */
    private Long bankId;

    /** 银行账号 */
    private String bankAccount;

    /** 银行户名 */
    private String bankAccountName;

    /** 补贴标准 */
    private BigDecimal subsidyStandard;

    /** 补发月数 */
    private Integer benefitMonths;

    /** 补发金额 */
    private BigDecimal benefitAmount;

    /** 审批状态 */
    private String approvalStatus;

    /** 审批批次号 */
    private String approvalBatchNo;

    /** 删除标志(0正常 2删除) */
    private String delFlag;

    @TableField(exist = false, select = false)
    private String submitBy;

    @TableField(exist = false, select = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    @TableField(exist = false, select = false)
    private String reviewBy;

    @TableField(exist = false, select = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    @TableField(exist = false, select = false)
    private String reviewRemark;

    @TableField(exist = false, select = false)
    private String rejectReason;

    /** 银行名称 */
    private String bankName;

    @TableField(exist = false, select = false)
    private String idCardNo;

    @TableField(exist = false, select = false)
    private String supplementType;

    @TableField(exist = false, select = false)
    private Date supplementStartMonth;

    @TableField(exist = false, select = false)
    private Date supplementEndMonth;

    @TableField(exist = false, select = false)
    private BigDecimal supplementAmount;

    @TableField(exist = false, select = false)
    private String filePath;
}
