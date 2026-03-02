package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 村级单位查询请求
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VillageListReq extends PageReq
{
    /** 村名 */
    private String villageName;

    /** 村编码 */
    private String villageCode;

    /** 父级编码 */
    private String parentCode;

    /** 全路径名称 */
    private String fullName;

    /** 状态（0正常 1停用） */
    private String status;
}
