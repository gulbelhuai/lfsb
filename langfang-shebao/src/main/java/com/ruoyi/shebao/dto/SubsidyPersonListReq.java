package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 被补贴人基础信息查询请求
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SubsidyPersonListReq extends PageReq
{
    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 性别（1男 2女） */
    private String gender;

    /** 联系电话 */
    private String phone;

    /** 户籍所在地 */
    private String householdRegistration;

    /** 家庭住址 */
    private String homeAddress;

    /** 是否健在（0否 1是） */
    private String isAlive;

    /** 是否村合作经济组织成员（0否 1是） */
    private String isVillageCoopMember;

    /** 所属街道办ID */
    private Long streetOfficeId;

    /** 所属村委会ID */
    private Long villageCommitteeId;

    /** 用户编号 */
    private String userCode;

    /** 状态（0正常 1停用） */
    private String status;
}
