package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 补贴发放记录查询请求
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SubsidyDistributionListReq extends PageReq
{
    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 补贴类型 */
    private String subsidyType;

    /** 发放日期-开始 */
    private LocalDate distributionDateStart;

    /** 发放日期-结束 */
    private LocalDate distributionDateEnd;

    /** 所属村委会ID */
    private Long villageCommitteeId;

    /** 发放状态 */
    private String distributionStatus;

    /** 状态（0正常 1停用） */
    private String status;
}

