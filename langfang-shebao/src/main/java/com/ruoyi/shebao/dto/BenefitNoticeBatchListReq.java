package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预到龄通知批次列表查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BenefitNoticeBatchListReq extends PageReq
{
    private String noticeMonth;

    private String batchNo;

    private String batchStatus;
}
