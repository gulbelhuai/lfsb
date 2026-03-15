package com.ruoyi.shebao.dto;

import lombok.Data;

/**
 * 预到龄通知生成请求
 */
@Data
public class BenefitNoticeGenerateReq
{
    private String noticeMonth;

    private Integer ageThreshold;
}
