package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 村委会信息列表响应
 * 
 * @author ruoyi
 * @date 2025-10-18
 */
@Data
public class VillageCommitteeListResp
{
    /** 主键ID */
    private Long id;

    /** 所属街道办ID */
    private Long streetOfficeId;

    /** 所属街道办名称 */
    @Excel(name = "所属街道办")
    private String streetOfficeName;

    /** 村委会编码 */
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

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortOrder;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 创建时间 */
    @Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Excel(name = "更新时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;
}
