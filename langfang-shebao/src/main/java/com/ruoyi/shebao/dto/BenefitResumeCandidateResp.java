package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class BenefitResumeCandidateResp
{
    private Long determinationId;
    private Long subsidyPersonId;
    private String name;
    private String idCardNo;
    private String streetOfficeName;
    private String villageCommitteeName;
    private List<ResumeItem> resumeItems;

    @Data
    public static class ResumeItem
    {
        private Long determinationItemId;
        private String subsidyType;
        @JsonFormat(pattern = "yyyy-MM")
        private Date pauseStartMonth;
        private String needResume;
        private String resumeReason;
        @JsonFormat(pattern = "yyyy-MM")
        private Date resumeMonth;
        @JsonFormat(pattern = "yyyy-MM")
        private Date supplementStartMonth;
        @JsonFormat(pattern = "yyyy-MM")
        private Date supplementEndMonth;
        private String remark;
        private Integer supplementMonths;
        private BigDecimal supplementAmount;
        private BigDecimal subsidyStandard;
    }
}
