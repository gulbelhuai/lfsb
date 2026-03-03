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

/**
 * 发放批次对象 distribution_batch
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("distribution_batch")
public class DistributionBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 批次号(格式:YYYYMMDD-XXX) */
    private String batchNo;

    /** 补贴类型(1失地 2被征地 3拆迁 4村干部 5教师) */
    private String subsidyType;

    /** 批次类型 */
    private String batchType;

    /** 发放年 */
    private Integer distributionYear;

    /** 发放月 */
    private Integer distributionMonth;

    /** 总人数 */
    private Integer totalCount;

    /** 总金额 */
    private BigDecimal totalAmount;

    /** 成功人数 */
    private Integer successCount;

    /** 成功金额 */
    private BigDecimal successAmount;

    /** 失败人数 */
    private Integer failCount;

    /** 失败金额 */
    private BigDecimal failAmount;

    /** 状态(draft/pending_review/pending_approve/pending_finance/submitted_bank/distributed) */
    @TableField("approval_status")
    private String status;

    /** 财务上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date financeUploadTime;

    /** 银行提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bankSubmitTime;

    /** 银行回盘时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bankResultTime;

    /** 删除标志 */
    private String delFlag;

    @TableField(exist = false)
    private String submitBy;
    @TableField(exist = false)
    private Date submitTime;
    @TableField(exist = false)
    private String reviewBy;
    @TableField(exist = false)
    private Date reviewTime;
    @TableField(exist = false)
    private String approveBy;
    @TableField(exist = false)
    private Date approveTime;
}
