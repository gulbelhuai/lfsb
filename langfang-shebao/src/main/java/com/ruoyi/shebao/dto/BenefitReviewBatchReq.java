package com.ruoyi.shebao.dto;

import lombok.Data;

import java.util.List;

/**
 * 待遇核定批量复核请求
 */
@Data
public class BenefitReviewBatchReq
{
    private List<Long> ids;

    private String remark;
}
