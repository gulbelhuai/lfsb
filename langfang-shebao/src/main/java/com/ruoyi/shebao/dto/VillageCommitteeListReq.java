package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 村委会信息查询请求
 * 
 * @author ruoyi
 * @date 2025-10-18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VillageCommitteeListReq extends PageReq
{
    /** 所属街道办ID */
    private Long streetOfficeId;

    /** 村委会编码 */
    private String villageCode;

    /** 村委会名称 */
    private String villageName;

    /** 状态（0正常 1停用） */
    private String status;
}
