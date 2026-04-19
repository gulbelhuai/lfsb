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

    /** 到龄-年 */
    private Integer eligibleYear;

    /** 到龄-月 */
    private Integer eligibleMonth;

    /**
     * 发放机构（字典：shebao_grant_org）
     */
    private String grantOrg;

    /**
     * 开户名（默认当前用户姓名，可人工修改）
     */
    private String accountName;

    /**
     * 与参保人关系（默认：本人）
     */
    private String relationToInsured;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 发放备注
     */
    private String grantRemark;

    /** 审批状态 */
    private String approvalStatus;

    /** 审批批次号 */
    private String approvalBatchNo;

    /** 身份证号快照 */
    private String idCardNo;

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

    /** 材料ZIP路径 */
    private String materialZipPath;

    /** 材料解压目录 */
    private String materialExtractDir;

    /** 材料图片路径 */
    private String materialImagePaths;

    /** 材料状态 */
    private String materialStatus;

    /** 是否已进入支付计划 */
    private String paymentPlanGenerated;

    /** 删除标志(0正常 2删除) */
    private String delFlag;

    @TableField(exist = false, select = false)
    private String rejectReason;

    /** 银行名称 */
    @TableField(exist = false, select = false)
    private String bankName;

    /**
     * 以下字段迁移到明细表（为兼容历史代码/支付计划查询，保留为非持久化字段）
     */
    @TableField(exist = false, select = false)
    private String subsidyType;

    @TableField(exist = false, select = false)
    private Integer benefitStartYear;

    @TableField(exist = false, select = false)
    private Integer benefitStartMonth;

    @TableField(exist = false, select = false)
    private BigDecimal subsidyStandard;

    @TableField(exist = false, select = false)
    private Integer benefitMonths;

    @TableField(exist = false, select = false)
    private BigDecimal benefitAmount;

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

    /**
     * 支付计划查询用：核定明细行 ID（不落主表）
     */
    @TableField(exist = false, select = false)
    private Long determinationItemId;
}
