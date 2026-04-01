package com.ruoyi.shebao.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 人员信息变更申请列表响应
 *
 * @author ruoyi
 */
@Data
public class PersonKeyInfoModifyListResp {

    private Long id;
    private Long subsidyPersonId;
    /** basic / key */
    private String modifyType;
    private String name;
    private String oldName;
    private String idCardNo;
    private String oldIdCardNo;
    /** 户籍所在地 */
    private String householdRegistration;
    private String oldHouseholdRegistration;
    private Long streetOfficeId;
    /** 所属街道办名称 */
    private String streetOfficeName;
    private Long oldStreetOfficeId;
    private String oldStreetOfficeName;
    private Long villageCommitteeId;
    /** 所属村委会名称 */
    private String villageCommitteeName;
    private Long oldVillageCommitteeId;
    private String oldVillageCommitteeName;
    private String homeAddress;
    private String oldHomeAddress;
    private String phone;
    private String oldPhone;
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
