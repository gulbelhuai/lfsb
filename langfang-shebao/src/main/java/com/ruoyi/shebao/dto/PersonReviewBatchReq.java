package com.ruoyi.shebao.dto;

import lombok.Data;

import java.util.List;

/**
 * 人员登记批量复核请求
 */
@Data
public class PersonReviewBatchReq
{
    private List<PersonReviewBatchItem> items;

    /** 复核意见 / 不通过原因 */
    private String remark;
}
