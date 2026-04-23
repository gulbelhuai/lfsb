package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 待遇暂停列表返回
 */
@Data
public class BenefitSuspensionListResp
{
    private Long id;
    private String name;
    private String idCardNo;
    /** 人员全部补贴类型 */
    private String subsidyTypes;
    /** 本次暂停补贴类型 */
    private String pausedSubsidyTypes;
    @JsonFormat(pattern = "yyyy-MM")
    private Date pauseMonth;
    private String pauseReason;
}
