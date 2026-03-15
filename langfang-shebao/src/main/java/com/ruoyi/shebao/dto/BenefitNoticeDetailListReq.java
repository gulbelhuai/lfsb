package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预到龄通知明细查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BenefitNoticeDetailListReq extends PageReq
{
    private String batchNo;

    private String name;

    private String determinationStatus;
}
