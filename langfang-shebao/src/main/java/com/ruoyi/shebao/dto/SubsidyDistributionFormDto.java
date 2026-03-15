package com.ruoyi.shebao.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 补贴发放记录表单DTO
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
@Data
public class SubsidyDistributionFormDto
{
    /** 主键ID */
    private Long id;

    /** 批次号 */
    private String batchNo;

    /** 批次类型 */
    private String batchType;

    /** 审批状态 */
    private String approvalStatus;

    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 被补贴人姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 性别 */
    private String gender;

    /** 生日 */
    private LocalDate birthday;

    /** 户籍所在地 */
    private String householdRegistration;

    /** 家庭住址 */
    private String homeAddress;

    /** 联系电话 */
    private String phone;

    /** 是否健在 */
    private String isAlive;

    /** 死亡时间 */
    private LocalDate deathDate;

    /** 是否村合作经济组织成员 */
    private String isVillageCoopMember;

    /** 用户编号 */
    private String userCode;

    /** 所属街道办ID */
    private Long streetOfficeId;

    /** 所属村委会ID */
    private Long villageCommitteeId;

    /** 所属街道办名称 */
    private String streetOfficeName;

    /** 所属村委会名称 */
    private String villageCommitteeName;

    /** 补贴类型 */
    private String subsidyType;

    /** 补贴身份记录ID */
    private Long subsidyRecordId;

    /** 发放金额 */
    private BigDecimal distributionAmount;

    /** 发放月份 */
    private String paymentMonth;

    /** 银行账号 */
    private String bankAccountNo;

    /** 发放日期 */
    private LocalDate distributionDate;

    /** 发放状态 */
    private String distributionStatus;

    /** 审核说明 */
    private String reviewRemark;

    /** 状态 */
    private String status;

    /** 备注 */
    private String remark;
}

