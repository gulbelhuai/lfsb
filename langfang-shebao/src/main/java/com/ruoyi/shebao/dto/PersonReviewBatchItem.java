package com.ruoyi.shebao.dto;

import lombok.Data;

/**
 * 人员登记批量复核项
 */
@Data
public class PersonReviewBatchItem
{
    /** 补贴类型 */
    private String subsidyType;

    /** 补贴子表主键 */
    private Long recordId;
}
