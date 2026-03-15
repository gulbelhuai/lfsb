package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 发放批次列表查询请求
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DistributionBatchListReq extends PageReq
{
    /** 批次号 */
    private String batchNo;

    /** 补贴类型 */
    private String subsidyType;

    /** 批次类型 */
    private Integer batchType;

    /** 状态 */
    private String status;

    /** 创建时间开始 */
    private LocalDate createTimeStart;

    /** 创建时间结束 */
    private LocalDate createTimeEnd;

    public String getApprovalStatus()
    {
        return status;
    }

    public void setApprovalStatus(String approvalStatus)
    {
        this.status = approvalStatus;
    }
}
