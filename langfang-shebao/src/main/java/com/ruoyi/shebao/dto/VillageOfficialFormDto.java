package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 村干部信息表单DTO（包含基础信息和任职信息）
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
public class VillageOfficialFormDto
{
    /** 村干部信息ID */
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

    /** 前端展示口径：籍贯 */
    private String nativePlace;

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

    /** 前端展示口径：村委会区划编码 */
    private String villageCode;

    /** 前端展示口径：村委会名称 */
    private String villageName;

    /** 用户编号（格式：001-002-0001） */
    private String userCode;

    /** 前端展示口径：户别编号 */
    private String householdNo;

    /** 前端展示口径：是否已领职工养老保险 */
    private String hasEmployeePension;

    // ===== 村干部特有信息字段 =====
    /** 累计任职年限 */
    private BigDecimal totalServiceYears;

    /** 村干部补贴标准（金额，元） */
    private BigDecimal subsidyAmount;

    /** 是否违法乱纪或判刑（0否 1是） */
    private String hasViolation;

    /** 认定时所在村街 */
    private String villageStreet;

    /** 状态（0正常 1停用） */
    private String status;

    /** 备注 */
    private String remark;

    // ===== 任职信息列表 =====
    /** 任职信息列表 */
    private List<VillageOfficialPositionDto> positionList;

    /**
     * 任职信息DTO
     */
    @Data
    public static class VillageOfficialPositionDto
    {

        public static final String POSITION_SECRETARY_AND_DIRECTOR = "1";
        public static final String POSITION_SECRETARY_OR_DIRECTOR = "2";
        public static final String POSITION_TWO_COMMITTEES = "3";

        /** 任职信息ID */
        private Long id;

        /** 村干部信息ID */
        private Long villageOfficialId;

        /** 任职职位（1书记兼主任 2书记或主任 3村"两委"其他成员） */
        private String position;

        /** 上任时间 */
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        /** 卸任时间 */
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        /** 任职年限：整年数 + 余下天数/365，两位小数（服务端会按起止日期重算） */
        private BigDecimal serviceYears;

        /** 状态（0正常 1停用） */
        private String status;

        /** 备注 */
        private String remark;
    }
}
