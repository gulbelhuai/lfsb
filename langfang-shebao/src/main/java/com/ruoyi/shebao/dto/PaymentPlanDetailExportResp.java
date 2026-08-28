package com.ruoyi.shebao.dto;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付计划明细导出（列与详情-明细表一致）
 */
@Data
public class PaymentPlanDetailExportResp
{
    @Excel(name = "补贴类型", sort = 1, readConverterExp = "land_loss=失地补贴,land_loss_resident=失地补贴,expropriatee=被征地补贴,expropriatee_subsidy=被征地补贴,demolition=拆迁补贴,demolition_resident=拆迁补贴,village_official=村干部补贴,teacher=教师补贴,teacher_subsidy=教师补贴")
    private String subsidyType;

    @Excel(name = "街道", sort = 2)
    private String streetName;

    @Excel(name = "村委会", sort = 3)
    private String villageName;

    @Excel(name = "姓名", sort = 4)
    private String personName;

    @Excel(name = "身份证号", sort = 5)
    private String idCardNo;

    @Excel(name = "业务期", sort = 6)
    private String businessPeriod;

    @Excel(name = "原始业务期", sort = 7)
    private String originalBusinessPeriod;

    @Excel(name = "补发起始", sort = 8)
    private String supplementStartMonth;

    @Excel(name = "补发终止", sort = 9)
    private String supplementEndMonth;

    @Excel(name = "发放金额", sort = 10, scale = 2)
    private BigDecimal distributionAmount;

    @Excel(name = "发放机构", sort = 11, handler = com.ruoyi.shebao.util.OpeningBankExcelHandler.class)
    private String grantOrg;

    @Excel(name = "开户名", sort = 12)
    private String accountName;

    @Excel(name = "银行账号", sort = 13)
    private String bankAccount;

    @Excel(name = "与参保人关系", sort = 14)
    private String relationToInsured;
}
