package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 村级单位对象 shebao_village
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_village")
public class Village extends BaseDomain
{

    /** 村编码（关联行政区划表） */
    @Excel(name = "村编码")
    private String villageCode;

    /** 村名 */
    @Excel(name = "村名")
    private String villageName;

    /** 父级编码（乡镇编码） */
    @Excel(name = "父级编码")
    private String parentCode;

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
}
