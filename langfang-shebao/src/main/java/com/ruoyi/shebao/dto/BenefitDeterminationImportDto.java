package com.ruoyi.shebao.dto;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 待遇核定批量导入模板
 */
@Data
public class BenefitDeterminationImportDto
{
    @Excel(name = "通知批次号")
    private String noticeBatchNo;

    @Excel(name = "用户ID")
    private Long subsidyPersonId;

    @Excel(name = "身份证号")
    private String idCardNo;

    @Excel(name = "银行名称")
    private String bankName;

    @Excel(name = "银行卡号")
    private String bankAccount;

    @Excel(name = "账户名")
    private String bankAccountName;

    @Excel(name = "补贴标准")
    private String subsidyStandard;

    @Excel(name = "享受开始年月")
    private String startMonth;
}
