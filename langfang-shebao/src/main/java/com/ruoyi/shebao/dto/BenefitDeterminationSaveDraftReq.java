package com.ruoyi.shebao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 待遇核定-保存草稿请求
 */
@Data
public class BenefitDeterminationSaveDraftReq
{
    /** 为空表示新增草稿；不为空表示修改草稿 */
    private Long id;

    @NotNull
    private Long subsidyPersonId;

    @NotBlank
    private String idCardNo;

    /** 发放机构（字典：shebao_grant_org） */
    private String grantOrg;

    /** 开户名 */
    private String accountName;

    /** 与参保人关系 */
    private String relationToInsured;

    /** 银行账号 */
    private String bankAccount;

    /** 备注 */
    private String grantRemark;

    @NotEmpty
    private List<Item> items;

    @Data
    public static class Item
    {
        /** land_loss / demolition / village_official */
        @NotBlank
        private String subsidyType;

        /** 享受开始年月 yyyy-MM */
        @NotBlank
        private String startMonth;

        /** 认定时所在村街（用于展示/打印快照） */
        private String villageStreet;

        /** 征地/拆迁时间 yyyy-MM-dd（村干部可空） */
        private String eventDate;
    }
}

