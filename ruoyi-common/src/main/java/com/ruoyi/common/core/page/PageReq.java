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

    public int pageNumOrDefault()
    {
        return pageNumOrDefault(1);
    }

    public int pageSizeOrDefault()
    {
        return pageSizeOrDefault(10);
    }

    public int pageNumOrDefault(int defaultPageNum)
    {
        return pageNum != null && pageNum > 0 ? pageNum : defaultPageNum;
    }

    public int pageSizeOrDefault(int defaultPageSize)
    {
        return pageSize != null && pageSize > 0 ? pageSize : defaultPageSize;
    }
}
