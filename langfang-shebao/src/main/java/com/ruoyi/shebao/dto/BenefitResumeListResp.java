package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BenefitResumeListResp
{
    private Long id;
    private String name;
    private String idCardNo;
    private String subsidyTypes;
    private String resumedSubsidyTypes;
    @JsonFormat(pattern = "yyyy-MM")
    private Date resumeMonth;
    private String resumeReason;
}
