package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 行政区划查询请求
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdministrativeDivisionListReq extends PageReq
{
    /** 行政区划编码 */
    private String divisionCode;

    /** 行政区划名称 */
    private String divisionName;

    /** 父级编码 */
    private String parentCode;

    /** 行政级别（1省 2市 3县 4乡镇 5村） */
    private Integer divisionLevel;

    /** 全路径名称 */
    private String fullName;

    /** 状态（0正常 1停用） */
    private String status;
}
