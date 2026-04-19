package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 待遇核定-身份证查询准备信息
 */
@Data
public class BenefitDeterminationPrepareResp
{
    /** 已核定提示：true 表示已存在通过的核定记录 */
    private boolean alreadyDetermined;

    private String alreadyDeterminedMsg;

    private PersonInfo person;

    private List<SubsidyInfo> subsidies;

    /** 社保相关信息（来自人员表 + 被征地参保补贴记录等） */
    private SocialInsuranceInfo socialInsurance;

    @Data
    public static class PersonInfo
    {
        private Long subsidyPersonId;
        private String name;
        private String gender;

        /** 出生年月 yyyy-MM */
        private String birthMonth;

        private String idCardNo;
        private String streetOfficeName;
        private String villageCommitteeName;
        private String householdRegistration;
        private String homeAddress;
        private String phone;
    }

    @Data
    public static class SubsidyInfo
    {
        /** land_loss / demolition / village_official */
        private String subsidyType;

        /** 认定时所在村街 */
        private String villageStreet;

        /** 征地/拆迁时间（村干部为空） */
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate eventDate;

        /** 补贴标准 */
        private BigDecimal subsidyStandard;

        /** 默认享受开始年月（yyyy-MM） */
        private String defaultStartMonth;
    }

    @Data
    public static class SocialInsuranceInfo
    {
        /** 参保状态（人员表 subsidy_status） */
        private String subsidyStatus;

        /** 人员状态（人员表 person_status） */
        private String personStatus;

        /** 以下取自被征地参保补贴（若存在） */
        private String joinUrbanRuralInsurance;
        private String joinEmployeePension;
        private String hasEmployeePension;
        private Integer employeePensionMonths;
        private Integer flexibleEmploymentMonths;
        private Integer difficultySubsidyMonths;
    }
}

