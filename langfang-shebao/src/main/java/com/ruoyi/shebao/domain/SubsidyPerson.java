package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 被补贴人基础信息对象 shebao_subsidy_person
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_subsidy_person")
public class SubsidyPerson extends BaseDomain
{
    /** 用户编号（系统生成，自增主键） */
    private Long id;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 性别（1男 2女） */
    @Excel(name = "性别", readConverterExp = "1=男,2=女")
    private String gender;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCardNo;

    /** 生日 */
    @Excel(name = "生日", dateFormat = "yyyy-MM-dd")
    private LocalDate birthday;

    /** 户籍所在地 */
    @Excel(name = "户籍所在地")
    private String householdRegistration;

    /** 家庭住址 */
    @Excel(name = "家庭住址")
    private String homeAddress;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String phone;

    /** 是否健在（0否 1是） */
    @Excel(name = "是否健在", readConverterExp = "0=否,1=是")
    private String isAlive;

    /** 死亡时间 */
    @Excel(name = "死亡时间", dateFormat = "yyyy-MM-dd")
    private LocalDate deathDate;

    /** 是否村合作经济组织成员（0否 1是） */
    @Excel(name = "是否村合作经济组织成员", readConverterExp = "0=否,1=是")
    private String isVillageCoopMember;

    /** 所属街道办ID */
    @Excel(name = "所属街道办ID")
    private Long streetOfficeId;

    /** 所属村委会ID */
    @Excel(name = "所属村委会ID")
    private Long villageCommitteeId;

    /** 用户编号（格式：001-002-0001） */
    @Excel(name = "用户编号")
    private String userCode;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
}
