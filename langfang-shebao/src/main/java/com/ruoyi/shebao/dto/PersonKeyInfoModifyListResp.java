package com.ruoyi.shebao.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 人员关键信息修改申请列表响应
 *
 * @author ruoyi
 */
@Data
public class PersonKeyInfoModifyListResp {

    private Long id;
    private Long subsidyPersonId;
    private String name;
    private String idCardNo;
    /** 户籍所在地 */
    private String householdRegistration;
    private Long streetOfficeId;
    /** 所属街道办名称 */
    private String streetOfficeName;
    private Long villageCommitteeId;
    /** 所属村委会名称 */
    private String villageCommitteeName;
    private String approvalStatus;
    private String submitBy;
    private LocalDateTime submitTime;
    private String reviewBy;
    private LocalDateTime reviewTime;
    private String approveBy;
    private LocalDateTime approveTime;
    private String rejectReason;
    private LocalDateTime createTime;
}
