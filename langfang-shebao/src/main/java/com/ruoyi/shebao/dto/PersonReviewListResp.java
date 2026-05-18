package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员登记复核列表行（按补贴子记录维度）
 */
@Data
public class PersonReviewListResp
{
    /** 补贴子表主键 */
    private Long id;

    private Long subsidyPersonId;

    private String subsidyType;

    private String userCode;

    private String name;

    private String idCardNo;

    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private String streetOfficeName;

    private String villageCommitteeName;

    private String phone;

    private String approvalStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;
}
