package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 待遇核定列表查询请求
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BenefitDeterminationListReq extends PageReq
{
    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 补贴类型 */
    private String subsidyType;

    /** 审批状态 */
    private String approvalStatus;

    /** 材料状态 */
    private String materialStatus;

    /** 到龄年月开始 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date eligibleMonthStart;

    /** 到龄年月结束 */
    @JsonFormat(pattern = "yyyy-MM")
    private Date eligibleMonthEnd;
}
