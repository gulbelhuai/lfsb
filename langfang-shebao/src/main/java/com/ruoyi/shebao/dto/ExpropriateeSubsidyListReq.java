package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 被征地参保补贴查询请求
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpropriateeSubsidyListReq extends PageReq
{
    /** 用户编号 */
    private String userCode;

    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 所属街道办ID */
    private Long streetOfficeId;

    /** 所属村委会ID */
    private Long villageCommitteeId;

    /** 审批状态（draft/pending_review/approved/rejected） */
    private String approvalStatus;

    /** 征地批次 */
    private String landRequisitionBatch;

    /** 基准日-开始 */
    private LocalDate baseDateStart;

    /** 基准日-结束 */
    private LocalDate baseDateEnd;

    /** 所属村委会编码 */
    private String villageCode;

    /** 选择参加城乡居保（0否 1是） */
    private String joinUrbanRuralInsurance;

    /** 选择参加职工养老（0否 1是） */
    private String joinEmployeePension;

    /** 状态（0正常 1停用） */
    private String status;
}
