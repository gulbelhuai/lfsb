package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 预到龄通知批次响应
 */
@Data
public class BenefitNoticeBatchResp
{
    private Long id;

    private String batchNo;

    private String noticeMonth;

    private String retirementMonth;

    private Integer ageThreshold;

    private Integer totalCount;

    private Integer pendingReviewCount;

    private Integer approvedCount;

    private Integer rejectedCount;

    private String batchStatus;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
