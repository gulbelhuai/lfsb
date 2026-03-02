package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

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

    /** 所属村委会编码 */
    private String villageCode;

    /** 累计任职年限-最小值 */
    private BigDecimal totalServiceYearsMin;

    /** 累计任职年限-最大值 */
    private BigDecimal totalServiceYearsMax;

    /** 是否违法乱纪或判刑（0否 1是） */
    private String hasViolation;

    /** 状态（0正常 1停用） */
    private String status;
}
