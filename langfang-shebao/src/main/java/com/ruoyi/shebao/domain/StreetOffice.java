package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 街道办事处信息对象 shebao_street_office
 * 
 * @author ruoyi
 * @date 2025-10-18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_street_office")
public class StreetOffice extends BaseDomain
{
    /** 主键ID */
    private Long id;

    /** 街道办编码（3位数字，如：001） */
    @Excel(name = "街道办编码")
    private String streetCode;

    /** 街道办名称 */
    @Excel(name = "街道办名称")
    private String streetName;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactPerson;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 详细地址 */
    @Excel(name = "详细地址")
    private String address;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
}