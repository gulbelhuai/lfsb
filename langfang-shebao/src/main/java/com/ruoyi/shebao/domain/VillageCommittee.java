package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 村委会信息对象 shebao_village_committee
 * 
 * @author ruoyi
 * @date 2025-10-18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_village_committee")
public class VillageCommittee extends BaseDomain
{
    /** 主键ID */
    private Long id;

    /** 所属街道办ID */
    @Excel(name = "所属街道办ID")
    private Long streetOfficeId;

    /** 村委会编码（3位数字，如：002） */
    @Excel(name = "村委会编码")
    private String villageCode;

    /** 村委会名称 */
    @Excel(name = "村委会名称")
    private String villageName;

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
