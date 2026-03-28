package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 村干部信息查询请求
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VillageOfficialListReq extends PageReq
{
    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 用户编号 */
    private String userCode;

    /** 所属街道办ID */
    private Long streetOfficeId;

    /** 所属村委会ID */
    private Long villageCommitteeId;

    /** 是否违法乱纪或判刑（0否 1是） */
    private String hasViolation;

    /** 审批状态（draft/pending_review/approved/rejected） */
    private String approvalStatus;
}
