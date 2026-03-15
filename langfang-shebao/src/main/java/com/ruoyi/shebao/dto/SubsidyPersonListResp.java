package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 被补贴人基础信息列表响应
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
public class SubsidyPersonListResp
{
    /** 用户编号 */
    @Excel(name = "用户编号", sort = 1)
    private Long id;

    /** 姓名 */
    @Excel(name = "姓名", sort = 2)
    private String name;

    /** 性别（1男 2女） */
    @Excel(name = "性别", sort = 3, readConverterExp = "1=男,2=女")
    private String gender;

    /** 身份证号 */
    @Excel(name = "身份证号", sort = 4, cellType = Excel.ColumnType.TEXT)
    private String idCardNo;

    /** 生日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生日", sort = 5, dateFormat = "yyyy-MM-dd")
    private LocalDate birthday;

    /** 户籍所在地 */
    @Excel(name = "户籍所在地", sort = 6)
    private String householdRegistration;

    /** 家庭住址 */
    @Excel(name = "家庭住址", sort = 7)
    private String homeAddress;

    /** 联系电话 */
    @Excel(name = "联系电话", sort = 8, cellType = Excel.ColumnType.TEXT)
    private String phone;

    /** 是否健在（0否 1是） */
    @Excel(name = "是否健在", sort = 9, readConverterExp = "0=否,1=是")
    private String isAlive;

    /** 死亡时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "死亡时间", sort = 10, dateFormat = "yyyy-MM-dd")
    private LocalDate deathDate;

    /** 是否村合作经济组织成员（0否 1是） */
    @Excel(name = "是否村合作经济组织成员", sort = 11, readConverterExp = "0=否,1=是")
    private String isVillageCoopMember;

    /** 所属街道办ID */
    @Excel(name = "所属街道办ID", sort = 12)
    private Long streetOfficeId;

    /** 所属街道办名称 */
    @Excel(name = "所属街道办名称", sort = 13)
    private String streetOfficeName;

    /** 所属村委会ID */
    @Excel(name = "所属村委会ID", sort = 14)
    private Long villageCommitteeId;

    /** 所属村委会名称 */
    @Excel(name = "所属村委会名称", sort = 15)
    private String villageCommitteeName;

    /** 用户编号（格式：001-002-0001） */
    @Excel(name = "用户编号", sort = 16, cellType = Excel.ColumnType.TEXT)
    private String userCode;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", sort = 17, readConverterExp = "0=正常,1=停用")
    private String status;

    /** 审批状态 */
    private String approvalStatus;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", sort = 18, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", sort = 19, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /** 备注 */
    @Excel(name = "备注", sort = 20)
    private String remark;
}
