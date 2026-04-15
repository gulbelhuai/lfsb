package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 预到龄通知查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BenefitNoticeBatchListReq extends PageReq
{
    /** 预到龄年月（yyyy-MM） */
    private String noticeMonth;

    /** 身份证号 */
    private String idCardNo;
}
