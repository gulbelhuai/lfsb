package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 被征地参保补贴表单DTO（包含基础信息）
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
public class ExpropriateeSubsidyFormDto
{
    /** 被征地参保补贴ID */
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

    /** 家庭住址 */
    private String homeAddress;

    /** 前端展示：籍贯（与 householdRegistration 同步） */
    private String nativePlace;

    /** 前端展示：村委会区划/编码（用于 DivisionSelector） */
    private String villageCode;

    /** 前端展示：村委会名称 */
    private String villageName;

    /** 前端展示：户别编号（基础表已移除时可留空） */
    private String householdNo;

    /** 是否已领取职工养老保险待遇（0未领取 1已领取），存于被征地补贴表 */
    private String hasEmployeePension;

    // ===== 被征地参保补贴特有信息字段 =====
    /** 征地批次 */
    private String landRequisitionBatch;

    /** 征地时所在村街 */
    private String villageStreet;

    /** 基准日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate baseDate;

    /** 职工身份缴纳职工养老月数 */
    private Integer employeePensionMonths;

    /** 灵活就业身份缴纳职工养老保险月数 */
    private Integer flexibleEmploymentMonths;

    /** 已领取困难人员社会保险补贴年限 */
    private BigDecimal difficultySubsidyYears;

    /** 截至基准日的年龄 */
    private Integer ageAtBaseDate;

    /** 被征地参保补贴年限 */
    private BigDecimal subsidyYears;

    /** 补贴金额 */
    private BigDecimal subsidyAmount;

    /** 选择参加城乡居保（0否 1是） */
    private String joinUrbanRuralInsurance;

    /** 选择参加职工养老（0否 1是） */
    private String joinEmployeePension;

    /** 状态（0正常 1停用） */
    private String status;

    /** 备注 */
    private String remark;
}
