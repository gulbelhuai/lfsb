package com.ruoyi.shebao.dto;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

@Data
public class OpeningBankListResp
{
    private Long id;
    @Excel(name = "开户行编码")
    private String code;
    @Excel(name = "简称")
    private String shortName;
    @Excel(name = "全称")
    private String fullName;
    @Excel(name = "行号")
    private String bankNo;
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
    private String remark;
}
