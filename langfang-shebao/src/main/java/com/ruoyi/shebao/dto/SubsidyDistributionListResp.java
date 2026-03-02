package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 补贴发放记录列表响应
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
@Data
public class SubsidyDistributionListResp
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCardNo;

    /** 用户编号 */
    @Excel(name = "用户编号")
    private String userCode;

    /** 所属街道办 */
    @Excel(name = "所属街道办")
    private String streetOfficeName;

    /** 所属村委会 */
    @Excel(name = "所属村委会")
    private String villageCommitteeName;

    /** 补贴类型 */
    @Excel(name = "补贴类型", readConverterExp = "1=失地居民补贴,2=被征地居民补贴,3=拆迁居民补贴,4=村干部补贴")
    private String subsidyType;

    /** 补贴类型名称 */
    @Excel(name = "补贴类型名称")
    private String subsidyTypeName;

    /** 补贴身份记录ID */
    private Long subsidyRecordId;

    /** 发放金额 */
    @Excel(name = "发放金额")
    private BigDecimal distributionAmount;

    /** 发放日期 */
    @Excel(name = "发放日期", dateFormat = "yyyy-MM-dd")
    private LocalDate distributionDate;

    /** 发放状态 */
    @Excel(name = "发放状态", readConverterExp = "0=未发放,1=待审核,2=待发放,3=已拒绝,4=已发放")
    private String distributionStatus;

    /** 发放状态名称 */
    @Excel(name = "发放状态名称")
    private String distributionStatusName;

    /** 审核说明 */
    private String reviewRemark;

    /** 创建时间 */
    @Excel(name = "提交日期", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
