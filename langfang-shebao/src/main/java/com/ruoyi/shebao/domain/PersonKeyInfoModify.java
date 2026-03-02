package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 人员关键信息修改申请对象 person_key_info_modify
 * 关键信息：姓名、身份证号、户籍所在地、所属街道办、所属村委会；经办-复核-审批三级
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
    /** 姓名 */
    private String name;
    /** 身份证号 */
    private String idCardNo;
    /** 户籍所在地 */
    private String householdRegistration;
    /** 所属街道办ID */
    private Long streetOfficeId;
    /** 所属村委会ID */
    private Long villageCommitteeId;
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
    /** 审批人 */
    private String approveBy;
    /** 审批时间 */
    private LocalDateTime approveTime;
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
