package com.ruoyi.shebao.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 可发放补贴DTO
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
@Data
public class AvailableSubsidyDto
{
    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 性别 */
    private String gender;

    /** 生日 */
    private java.time.LocalDate birthday;

    /** 户籍所在地 */
    private String householdRegistration;

    /** 家庭住址 */
    private String homeAddress;

    /** 联系电话 */
    private String phone;

    /** 是否健在 */
    private String isAlive;

    /** 死亡时间 */
    private java.time.LocalDate deathDate;

    /** 是否村合作经济组织成员 */
    private String isVillageCoopMember;

    /** 用户编号 */
    private String userCode;

    /** 所属街道办名称 */
    private String streetOfficeName;

    /** 所属村委会名称 */
    private String villageCommitteeName;

    /** 可发放的补贴类型列表 */
    private List<SubsidyTypeInfo> subsidyTypes;

    /**
     * 补贴类型信息
     */
    @Data
    public static class SubsidyTypeInfo
    {
        /** 补贴类型 */
        private String subsidyType;

        /** 补贴类型名称 */
        private String subsidyTypeName;

        /** 可发放的记录列表 */
        private List<SubsidyRecordInfo> records;
    }

    /**
     * 补贴记录信息
     */
    @Data
    public static class SubsidyRecordInfo
    {
        /** 补贴身份记录ID */
        private Long subsidyRecordId;

        /** 补贴金额 */
        private BigDecimal subsidyAmount;

        /** 发放状态 */
        private String distributionStatus;

        /** 发放状态名称 */
        private String distributionStatusName;

        /** 是否可选择（只有未发放状态才可选择） */
        private Boolean selectable;

        /** 补贴详细信息（JSON格式，用于展示） */
        private Object subsidyDetail;
    }
}
