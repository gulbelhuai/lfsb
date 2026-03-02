package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 待遇核定表（导出/打印）DTO
 */
@Data
public class BenefitDeterminationPrintDto {

    @Excel(name = "用户编号")
    private String userCode;

    @Excel(name = "姓名")
    private String name;

    @Excel(name = "身份证号")
    private String idCardNo;

    @Excel(name = "补贴类型")
    private String subsidyType;

    /** 到龄年月 */
    @Excel(name = "到龄年月", dateFormat = "yyyy-MM")
    @JsonFormat(pattern = "yyyy-MM")
    private Date eligibleMonth;

    /** 补贴享受开始年月 */
    @Excel(name = "享受开始年月", dateFormat = "yyyy-MM")
    @JsonFormat(pattern = "yyyy-MM")
    private Date benefitStartMonth;

    @Excel(name = "银行名称")
    private String bankName;

    @Excel(name = "银行账号")
    private String bankAccount;

    @Excel(name = "月补贴标准(元)")
    private BigDecimal subsidyStandard;

    @Excel(name = "补发月数")
    private Integer benefitMonths;

    @Excel(name = "补发金额(元)")
    private BigDecimal benefitAmount;

    @Excel(name = "核定创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}

