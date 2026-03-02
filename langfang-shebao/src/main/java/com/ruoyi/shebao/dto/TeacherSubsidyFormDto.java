package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 教龄补助表单DTO（包含基础信息）
 *
 * 对应概要设计：人员信息管理 → 教龄补助登记（原“教师补贴”）。
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@Data
public class TeacherSubsidyFormDto
{
    /** 教龄补助ID */
    private Long id;

    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 基础信息是否已存在 */
    private Boolean personExists;

    // ===== 基础信息字段 =====
    /** 姓名 */
    private String name;

    /** 性别（1男 2女） */
    private String gender;

    /** 身份证号 */
    private String idCardNo;

    /** 生日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /** 户籍所在地 */
    private String householdRegistration;

    /** 家庭住址 */
    private String homeAddress;

    /** 联系电话 */
    private String phone;

    /** 是否健在（0否 1是） */
    private String isAlive;

    /** 死亡时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deathDate;

    /** 是否村合作经济组织成员（0否 1是） */
    private String isVillageCoopMember;

    /** 所属街道办ID */
    private Long streetOfficeId;

    /** 所属村委会ID */
    private Long villageCommitteeId;

    /** 用户编号（格式：0010020001） */
    private String userCode;

    // ===== 教龄补助特有信息字段 =====
    /** 学校名称 */
    private String schoolName;

    /** 任教年限 */
    private Integer teachingYears;

    /** 状态（0正常 1停用） */
    private String status;

    /** 备注 */
    private String remark;
}

