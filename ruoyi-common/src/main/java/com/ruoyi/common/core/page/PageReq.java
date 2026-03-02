package com.ruoyi.common.core.page;

import lombok.Data;

/**
 * 分页数据
 * 
 * @author ruoyi
 */
@Data
public class PageReq
{
    /** 当前记录起始索引 */
    private Integer pageNum;

    /** 每页显示记录数 */
    private Integer pageSize;
}
