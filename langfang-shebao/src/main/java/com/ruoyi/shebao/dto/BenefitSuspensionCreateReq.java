package com.ruoyi.shebao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 待遇暂停新增请求
 */
@Data
public class BenefitSuspensionCreateReq
{
    @NotNull
    private Long determinationId;

    @NotNull
    private Long subsidyPersonId;

    @NotBlank
    private String idCardNo;

    /** 格式 yyyy-MM */
    @NotBlank
    private String pauseMonth;

    /** 字典：pause_reason */
    @NotBlank
    private String pauseReason;

    private String remark;

    /** 选择暂停的核定子项ID */
    @NotEmpty
    private List<Long> determinationItemIds;
}
