package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 待遇暂停详情
 */
@Data
public class BenefitSuspensionDetailResp
{
    private Long id;
    private Long determinationId;
    private Long subsidyPersonId;
    private String name;
    private String idCardNo;
    private String streetOfficeName;
    private String villageCommitteeName;
    @JsonFormat(pattern = "yyyy-MM")
    private Date pauseMonth;
    private String pauseReason;
    private String remark;
    private List<TreatmentItem> treatmentItems;
    private List<RecoverItem> recoverItems;

    @Data
    public static class TreatmentItem
    {
        private Long determinationItemId;
        private String subsidyType;
        @JsonFormat(pattern = "yyyy-MM")
        private Date benefitStartMonth;
        /** 0正常 1暂停 */
        private String benefitStatus;
        private BigDecimal subsidyStandard;
    }

    @Data
    public static class RecoverItem
    {
        private Long determinationItemId;
        private String subsidyType;
        /** 0否 1是 */
        private String needRecover;
        @JsonFormat(pattern = "yyyy-MM")
        private Date recoverStartMonth;
        @JsonFormat(pattern = "yyyy-MM")
        private Date recoverEndMonth;
        private Integer recoverMonths;
        private BigDecimal recoverAmount;
    }
}
