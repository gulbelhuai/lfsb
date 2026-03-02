package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 拆迁居民信息表单DTO（包含基础信息）
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
public class DemolitionResidentFormDto
{
    /** 拆迁居民信息ID */
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

    /** 用户编号（格式：001-002-0001） */
    private String userCode;

    // ===== 拆迁居民特有信息字段 =====
    /** 拆迁事由 */
    private String demolitionReason;

    /** 拆迁时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate demolitionTime;

    /** 认定为拆迁居民时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate recognitionTime;

    /** 备注 */
    private String remark;
}
