package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 开户行（发放机构） shebao_opening_bank
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_opening_bank")
public class OpeningBank extends BaseDomain
{
    private Long id;

    /** 开户行编码（业务关联键） */
    @Excel(name = "开户行编码")
    @NotBlank(message = "开户行编码不能为空")
    @Size(max = 64, message = "开户行编码长度不能超过64个字符")
    private String code;

    /** 简称 */
    @Excel(name = "简称")
    @NotBlank(message = "简称不能为空")
    @Size(max = 100, message = "简称长度不能超过100个字符")
    private String shortName;

    /** 全称 */
    @Excel(name = "全称")
    @NotBlank(message = "全称不能为空")
    @Size(max = 200, message = "全称长度不能超过200个字符")
    private String fullName;

    /** 行号 */
    @Excel(name = "行号")
    @NotBlank(message = "行号不能为空")
    @Size(max = 32, message = "行号长度不能超过32个字符")
    private String bankNo;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
}
