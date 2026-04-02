package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员注销登记表单DTO
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@Data
public class PersonCancelFormDto
{
    /** 注销登记ID */
    private Long id;

    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 用户编号 */
    private String userCode;

    /** 注销时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cancelTime;

    /** 兼容旧字段：死亡时间（与注销时间同义） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deathDate;

    /** 注销原因（字典 cancel_reason） */
    private String cancelReason;

    /** 审核状态（pending_review/approved/rejected） */
    private String approvalStatus;

    /** 驳回原因 */
    private String rejectReason;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 备注 */
    private String remark;
}

