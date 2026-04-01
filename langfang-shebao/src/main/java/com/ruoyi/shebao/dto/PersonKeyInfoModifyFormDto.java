package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 人员信息变更表单/详情（含街道办、村委会名称）
 *
 * @author ruoyi
 */
@Data
public class PersonKeyInfoModifyFormDto {

    private Long id;
    private Long subsidyPersonId;
    /** basic-基本信息变更(2级) key-关键信息变更(3级) */
    private String modifyType;
    private String name;
    private String oldName;
    private String idCardNo;
    private String oldIdCardNo;
    /** 户籍所在地 */
    private String householdRegistration;
    private String oldHouseholdRegistration;
    private Long streetOfficeId;
    private String streetOfficeName;
    private Long oldStreetOfficeId;
    private String oldStreetOfficeName;
    private Long villageCommitteeId;
    private String villageCommitteeName;
    private Long oldVillageCommitteeId;
    private String oldVillageCommitteeName;
    /** 家庭住址 */
    private String homeAddress;
    private String oldHomeAddress;
    /** 联系电话 */
    private String phone;
    private String oldPhone;
    private String approvalStatus;
    private String submitBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime submitTime;
    private String reviewBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime reviewTime;
    private String reviewRemark;
    private String approveBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime approveTime;
    private String approveRemark;
    private String rejectReason;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
