package com.ruoyi.shebao.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 居民基本信息DTO
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Data
public class ResidentBasicInfo
{
    /** 居民ID */
    private Long id;

    /** 姓名 */
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
}
