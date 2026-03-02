package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 街道办事处信息查询请求
 * 
 * @author ruoyi
 * @date 2025-10-18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StreetOfficeListReq extends PageReq
{
    /** 街道办编码 */
    private String streetCode;

    /** 街道办名称 */
    private String streetName;

    /** 状态（0正常 1停用） */
    private String status;
}
