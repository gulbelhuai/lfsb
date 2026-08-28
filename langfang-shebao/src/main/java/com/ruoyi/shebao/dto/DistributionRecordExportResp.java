package com.ruoyi.shebao.dto;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 补贴发放记录导出（列与列表一致）
 */
@Data
public class DistributionRecordExportResp
{
    @Excel(name = "支付计划批次", sort = 1)
    private String batchNo;

    @Excel(name = "发放类型", sort = 2, readConverterExp = "normal=正常发放,second=二次发放")
    private String determinationType;

    @Excel(name = "补贴类型", sort = 3, readConverterExp = "land_loss=失地补贴,land_loss_resident=失地补贴,expropriatee=被征地补贴,expropriatee_subsidy=被征地补贴,demolition=拆迁补贴,demolition_resident=拆迁补贴,village_official=村干部补贴,teacher=教师补贴,teacher_subsidy=教师补贴")
    private String subsidyType;

    @Excel(name = "街道", sort = 4)
    private String streetName;

    @Excel(name = "村委会", sort = 5)
    private String villageName;

    @Excel(name = "姓名", sort = 6)
    private String personName;

    @Excel(name = "身份证号", sort = 7)
    private String idCardNo;

    @Excel(name = "业务期", sort = 8)
    private String businessPeriod;

    /** 原始业务期（二次明细关联源行，查询 join） */
    private String originalBusinessPeriod;

    /** 源行重发状态 */
    private String retryStatus;

    @Excel(name = "补发起始", sort = 9)
    private String supplementStartMonth;

    @Excel(name = "补发终止", sort = 10)
    private String supplementEndMonth;

    @Excel(name = "发放金额", sort = 11, scale = 2)
    private BigDecimal distributionAmount;

    @Excel(name = "发放日期", sort = 12)
    private String distributionDate;

    @Excel(name = "发放状态", sort = 13, readConverterExp = "distributing=发放中,paid=已发放,failed=发放失败,retry_success=重发成功")
    private String payStatus;

    @Excel(name = "失败原因", sort = 14)
    private String failReason;

    @Excel(name = "发放机构", sort = 15, handler = com.ruoyi.shebao.util.OpeningBankExcelHandler.class)
    private String grantOrg;

    @Excel(name = "开户名", sort = 16)
    private String accountName;

    @Excel(name = "银行账号", sort = 17)
    private String bankAccount;

    @Excel(name = "与参保人关系", sort = 18)
    private String relationToInsured;
}
