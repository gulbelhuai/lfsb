package com.ruoyi.shebao.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BenefitResumeCreateReq
{
    @NotNull
    private Long determinationId;

    @NotNull
    private Long subsidyPersonId;

    @NotBlank
    private String idCardNo;

    @NotEmpty
    @Valid
    private List<Item> items;

    @Data
    public static class Item
    {
        @NotNull
        private Long determinationItemId;

        /** 0否 1是 */
        @NotBlank
        private String needResume;

        private String resumeReason;

        /** yyyy-MM */
        @NotBlank
        private String resumeMonth;

        private String remark;
    }
}
