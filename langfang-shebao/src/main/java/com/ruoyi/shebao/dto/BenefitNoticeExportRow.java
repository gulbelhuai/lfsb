package com.ruoyi.shebao.dto;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 预到龄通知导出行
 */
@Data
public class BenefitNoticeExportRow
{
    @Excel(name = "通知批次号")
    private String batchNo;

    @Excel(name = "通知月份")
    private String noticeMonth;

    @Excel(name = "到龄月份")
    private String retirementMonth;

    @Excel(name = "用户ID")
    private Long subsidyPersonId;

    @Excel(name = "用户编号")
    private String userCode;

    @Excel(name = "姓名")
    private String name;

    @Excel(name = "身份证号")
    private String idCardNo;

    @Excel(name = "补贴类型")
    private String subsidyType;

    @Excel(name = "到龄日期")
    private String retirementDate;

    @Excel(name = "核定状态")
    private String determinationStatus;
}
