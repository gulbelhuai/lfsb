package com.ruoyi.shebao.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 人员关键信息修改申请表单/详情（含街道办、村委会名称）
 *
 * @author ruoyi
 */
@Data
public class PersonKeyInfoModifyFormDto {

    private Long id;
    private Long subsidyPersonId;
    private String name;
    private String idCardNo;
    /** 户籍所在地 */
    private String householdRegistration;
    private Long streetOfficeId;
    private String streetOfficeName;
    private Long villageCommitteeId;
    private String villageCommitteeName;
    private String approvalStatus;
    private String submitBy;
    private LocalDateTime submitTime;
    private String reviewBy;
    private LocalDateTime reviewTime;
    private String approveBy;
    private LocalDateTime approveTime;
    private String rejectReason;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
