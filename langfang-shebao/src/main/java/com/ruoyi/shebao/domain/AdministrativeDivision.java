package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 行政区划对象 shebao_administrative_division
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_administrative_division")
public class AdministrativeDivision extends BaseDomain
{

    /** 行政区划编码 */
    @Excel(name = "行政区划编码")
    private String divisionCode;

    /** 行政区划名称 */
    @Excel(name = "行政区划名称")
    private String divisionName;

    /** 父级编码 */
    @Excel(name = "父级编码")
    private String parentCode;

    /** 行政级别（1省 2市 3县 4乡镇 5村） */
    @Excel(name = "行政级别", readConverterExp = "1=省,2=市,3=县,4=乡镇,5=村")
    private Integer divisionLevel;

    /** 全路径编码，用/分隔 */
    @Excel(name = "全路径编码")
    private String fullCode;

    /** 全路径名称，用/分隔 */
    @Excel(name = "全路径名称")
    private String fullName;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactPerson;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 详细地址 */
    @Excel(name = "详细地址")
    private String address;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 子级行政区划（不存储到数据库，仅用于树形结构） */
    @TableField(exist = false)
    private List<AdministrativeDivision> children;
}
