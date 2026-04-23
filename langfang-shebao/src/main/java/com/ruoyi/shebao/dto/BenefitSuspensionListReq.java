package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 待遇暂停列表查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BenefitSuspensionListReq extends PageReq
{
    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 暂停原因 */
    private String pauseReason;
}
