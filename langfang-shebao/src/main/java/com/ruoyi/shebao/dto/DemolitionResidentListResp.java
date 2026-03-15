package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 拆迁居民信息列表响应
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
public class DemolitionResidentListResp
{
    /** 主键ID */
    @Excel(name = "主键ID", sort = 1)
    private Long id;

    /** 被补贴人ID */
    @Excel(name = "被补贴人ID", sort = 2)
    private Long subsidyPersonId;

    /** 姓名（来自基础信息） */
    @Excel(name = "姓名", sort = 3)
    private String name;

    /** 身份证号（来自基础信息） */
    @Excel(name = "身份证号", sort = 4, cellType = Excel.ColumnType.TEXT)
    private String idCardNo;

    /** 性别（（来自基础信息 1男 2女） */
    @Excel(name = "性别", sort = 5, readConverterExp = "1=男,2=女")
    private String gender;

    /** 出生日期（来自基础信息） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", sort = 6, dateFormat = "yyyy-MM-dd")
    private LocalDate birthday;

    /** 联系电话（来自基础信息） */
    @Excel(name = "联系电话", sort = 7, cellType = Excel.ColumnType.TEXT)
    private String phone;

    /** 是否健在（来自基础信息）（0否 1是） */
    @Excel(name = "是否健在", sort = 8, readConverterExp = "0=否,1=是")
    private String isAlive;

    /** 死亡时间（来自基础信息） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "死亡时间", sort = 9, dateFormat = "yyyy-MM-dd")
    private LocalDate deathDate;

    /** 是否村合作经济组织成员（来自基础信息）（0否 1是） */
    @Excel(name = "是否村合作经济组织成员", sort = 10, readConverterExp = "0=否,1=是")
    private String isVillageCoopMember;

    /** 所属街道办ID（来自基础信息） */
    private Long streetOfficeId;

    /** 所属街道办名称（来自基础信息） */
    @Excel(name = "所属街道办", sort = 12)
    private String streetOfficeName;

    /** 所属村委会ID（来自基础信息） */
    private Long villageCommitteeId;

    /** 所属村委会名称（来自基础信息） */
    @Excel(name = "所属村委会", sort = 14)
    private String villageCommitteeName;

    /** 用户编号（来自基础信息） */
    @Excel(name = "用户编号", sort = 15, cellType = Excel.ColumnType.TEXT)
    private String userCode;

    /** 审批状态 */
    private String approvalStatus;

    /** 拆迁事由 */
    @Excel(name = "拆迁事由", sort = 16)
    private String demolitionReason;

    /** 拆迁时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "拆迁时间", sort = 17, dateFormat = "yyyy-MM-dd")
    private LocalDate demolitionTime;

    /** 认定为拆迁居民时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "认定为拆迁居民时间", sort = 18, dateFormat = "yyyy-MM-dd")
    private LocalDate recognitionTime;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", sort = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", sort = 21, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /** 备注 */
    @Excel(name = "备注", sort = 22)
    private String remark;
}
