package com.ruoyi.shebao.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;

    /** 选择暂停的核定子项 */
    @NotEmpty
    @Valid
    private List<Item> items;

    @Data
    public static class Item
    {
        @NotNull
        private Long determinationItemId;

        /** 追回终止年月 yyyy-MM，可选；空则按系统默认（当前月前一月） */
        private String recoverEndMonth;
    }
}
