package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 预到龄通知明细响应
 */
@Data
public class BenefitNoticeDetailResp
{
    private Long id;

    private String batchNo;

    private Long subsidyPersonId;

    private String userCode;

    private String name;

    private String idCardNo;

    private String subsidyType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date retirementDate;

    private Long determinationId;

    private String determinationStatus;

    private String materialStatus;

    private String reviewStatus;

    private String bankName;

    private String bankAccount;
}
