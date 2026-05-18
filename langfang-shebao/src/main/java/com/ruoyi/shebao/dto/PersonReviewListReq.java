package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 人员登记复核列表查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PersonReviewListReq extends PageReq
{
    private String name;
    private String idCardNo;
    /** 补贴类型：land_loss_resident / expropriatee / demolition_resident / village_official */
    private String subsidyType;
    /** 审批状态，默认 pending_review */
    private String approvalStatus;
}
