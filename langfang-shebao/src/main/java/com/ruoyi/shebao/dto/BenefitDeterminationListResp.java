package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 待遇核定列表响应
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@Data
public class BenefitDeterminationListResp
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 用户编码 */
    private String userCode;

    /** 补贴类型 */
    private String subsidyType;

    /** 发放机构 */
    private String grantOrg;

    /** 开户名 */
    private String accountName;

    /** 与参保人关系 */
    private String relationToInsured;

    /** 到龄年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date eligibleMonth;

    /** 补贴享受开始年月 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date benefitStartMonth;

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

    /** 审批状态 */
    private String approvalStatus;

    /** 材料状态 */
    private String materialStatus;

    /** 是否已进入支付计划 */
    private String paymentPlanGenerated;

    /** 提交人 */
    private String submitBy;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
