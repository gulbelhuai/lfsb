package com.ruoyi.shebao.dto;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 预到龄通知导出行
 */
@Data
public class BenefitNoticeExportRow
{
    @Excel(name = "街道办")
    private String streetOfficeName;

    @Excel(name = "村委会")
    private String villageCommitteeName;

    @Excel(name = "姓名")
    private String name;

    @Excel(name = "身份证号")
    private String idCardNo;

    @Excel(name = "出生日期")
    private String birthday;

    @Excel(name = "到龄年月")
    private String retirementMonth;

    @Excel(name = "参保状态")
    private String subsidyStatus;

    @Excel(name = "补贴类型")
    private String subsidyType;
}
