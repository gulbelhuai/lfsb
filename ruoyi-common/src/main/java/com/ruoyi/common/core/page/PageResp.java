package com.ruoyi.common.core.page;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

@Data
@Accessors(chain = true)
public class PageResp<T> {


    public PageResp(long current, long size, long total) {
        this.total = total;
        this.size = size;
        this.current = current;
    }

    public static <T> PageResp<T> of(long current, long size, long total) {
        return new PageResp<>(current, size, total);
    }

    /**
     * 总数
     */
    protected long total = 0;
    /**
     * 每页显示条数，默认 10
     */
    protected long size = 10;
    /**
     * 当前页
     */
    protected long current = 1;
    /**
     * 当前页列表内容
     */
    protected List<T> records = Collections.emptyList();


}
