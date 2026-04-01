package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 人员信息变更申请 person_key_info_modify
 * basic：基本信息变更（2级：复核通过即生效）；key：关键信息变更（3级：复核→审批）
 *
 * @author ruoyi
 */
@Data
@TableName("person_key_info_modify")
public class PersonKeyInfoModify {

    /** 主键ID */
    private Long id;
    /** 被补贴人ID */
    private Long subsidyPersonId;
    /** 变更类型 basic / key */
    private String modifyType;
    /** 姓名 */
    private String name;
    /** 变更前姓名 */
    private String oldName;
    /** 身份证号 */
    private String idCardNo;
    /** 变更前身份证号 */
    private String oldIdCardNo;
    /** 户籍所在地 */
    private String householdRegistration;
    /** 变更前户籍所在地 */
    private String oldHouseholdRegistration;
    /** 所属街道办ID */
    private Long streetOfficeId;
    /** 变更前所属街道办ID */
    private Long oldStreetOfficeId;
    /** 所属村委会ID */
    private Long villageCommitteeId;
    /** 变更前所属村委会ID */
    private Long oldVillageCommitteeId;
    /** 家庭住址 */
    private String homeAddress;
    /** 变更前家庭住址 */
    private String oldHomeAddress;
    /** 联系电话 */
    private String phone;
    /** 变更前联系电话 */
    private String oldPhone;
    /** 审批状态 draft-草稿 pending_review-待复核 pending_approve-待审批 approved-已通过 rejected-已驳回 */
    private String approvalStatus;
    /** 提交人 */
    private String submitBy;
    /** 提交时间 */
    private LocalDateTime submitTime;
    /** 复核人 */
    private String reviewBy;
    /** 复核时间 */
    private LocalDateTime reviewTime;
    /** 复核意见 */
    private String reviewRemark;
    /** 审批人 */
    private String approveBy;
    /** 审批时间 */
    private LocalDateTime approveTime;
    /** 审批意见 */
    private String approveRemark;
    /** 驳回原因 */
    private String rejectReason;
    /** 删除标志 */
    private String delFlag;
    /** 创建者 */
    private String createBy;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新者 */
    private String updateBy;
    /** 更新时间 */
    private LocalDateTime updateTime;
    /** 备注 */
    private String remark;
}
