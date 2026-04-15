package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 预到龄通知列表响应
 */
@Data
public class BenefitNoticeBatchResp
{
    private String streetOfficeName;

    private String villageCommitteeName;

    private String name;

    private String idCardNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private String retirementMonth;

    private String subsidyStatus;

    private String subsidyType;
}
