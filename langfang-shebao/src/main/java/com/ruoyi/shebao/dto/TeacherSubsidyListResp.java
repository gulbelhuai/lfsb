package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教龄补助列表响应
 *
 * 对应概要设计：人员信息管理 → 教龄补助登记（原“教师补贴”）。
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@Data
public class TeacherSubsidyListResp
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 用户编号（来自基础信息） */
    private String userCode;

    /** 姓名（来自基础信息） */
    private String name;

    /** 身份证号（来自基础信息） */
    private String idCardNo;

    /** 所属街道办ID（来自基础信息） */
    private Long streetOfficeId;

    /** 所属村委会ID（来自基础信息） */
    private Long villageCommitteeId;

    /** 学校名称 */
    private String schoolName;

    /** 任教年限 */
    private Integer teachingYears;

    /** 审批状态（来自被补贴人基础信息） */
    private String approvalStatus;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /** 备注 */
    private String remark;
}

