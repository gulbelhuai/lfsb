package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 待遇暂停候选人员信息
 */
@Data
public class BenefitSuspensionCandidateResp
{
    private Long determinationId;
    private Long subsidyPersonId;
    private String name;
    private String idCardNo;
    private String streetOfficeName;
    private String villageCommitteeName;
    private List<TreatmentItem> treatmentItems;

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
}
