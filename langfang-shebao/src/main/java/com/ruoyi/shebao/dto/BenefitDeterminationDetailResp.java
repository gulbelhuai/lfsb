package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 待遇核定详情响应
 */
@Data
public class BenefitDeterminationDetailResp
{
    private Long id;

    private Long subsidyPersonId;

    private String noticeBatchNo;

    private Long noticeDetailId;

    private String name;

    private String userCode;

    private String idCardNo;

    private String subsidyType;

    private Integer eligibleYear;

    private Integer eligibleMonth;

    private Integer benefitStartYear;

    private Integer benefitStartMonth;

    private String bankName;

    private String bankAccount;

    private String bankAccountName;

    private BigDecimal subsidyStandard;

    private Integer benefitMonths;

    private BigDecimal benefitAmount;

    private String approvalStatus;

    private String materialStatus;

    private String materialZipPath;

    private List<String> materialImagePaths;

    private String submitBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    private String reviewBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    private String reviewRemark;
}
