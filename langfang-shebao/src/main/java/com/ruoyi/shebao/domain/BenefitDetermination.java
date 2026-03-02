package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

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

    /** 到龄年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date eligibleMonth;

    /** 补贴享受开始年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date benefitStartMonth;

    /** 发放银行ID */
    private Long bankId;

    /** 银行名称 */
    private String bankName;

    /** 银行账号 */
    private String bankAccount;

    /** 补贴标准 */
    private BigDecimal subsidyStandard;

    /** 补发月数 */
    private Integer benefitMonths;

    /** 补发金额 */
    private BigDecimal benefitAmount;

    /** 补发类型(1应补发年月 2应补发金额) */
    private String supplementType;

    /** 补发开始年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date supplementStartMonth;

    /** 补发终止年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date supplementEndMonth;

    /** 补发金额 */
    private BigDecimal supplementAmount;

    /** 附件路径 */
    private String filePath;

    /** 审批状态 */
    private String approvalStatus;

    /** 提交人 */
    private String submitBy;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /** 复核人 */
    private String reviewBy;

    /** 复核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    /** 复核意见 */
    private String reviewRemark;

    /** 驳回原因 */
    private String rejectReason;

    /** 删除标志(0正常 2删除) */
    private String delFlag;
}
