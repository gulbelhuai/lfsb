package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 行政区划列表响应
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
public class AdministrativeDivisionListResp
{
    /** id */
    private Long id;

    /** 行政区划编码 */
    private String divisionCode;

    /** 行政区划名称 */
    private String divisionName;

    /** 父级编码 */
    private String parentCode;

    /** 行政级别（1省 2市 3县 4乡镇 5村） */
    private Integer divisionLevel;

    /** 全路径编码 */
    private String fullCode;

    /** 全路径名称 */
    private String fullName;

    /** 联系人 */
    private String contactPerson;

    /** 联系电话 */
    private String contactPhone;

    /** 详细地址 */
    private String address;

    /** 排序 */
    private Integer sortOrder;

    /** 状态（0正常 1停用） */
    private String status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /** 备注 */
    private String remark;
}
