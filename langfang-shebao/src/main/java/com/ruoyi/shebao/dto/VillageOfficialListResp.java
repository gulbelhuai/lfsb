package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 村干部信息列表响应
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
public class VillageOfficialListResp
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 姓名（来自基础信息） */
    private String name;

    /** 用户编号（来自基础信息） */
    private String userCode;

    /** 身份证号（来自基础信息） */
    private String idCardNo;

    /** 性别（来自基础信息） */
    private String gender;

    /** 联系电话（来自基础信息） */
    private String phone;

    /** 所属街道办名称（来自基础信息） */
    private String streetOfficeName;

    /** 所属村委会名称（来自基础信息） */
    private String villageName;

    /** 认定时所在村街 */
    private String villageStreet;

    /** 村干部补贴标准（金额，元） */
    private BigDecimal subsidyAmount;

    /** 是否违法乱纪或判刑（0否 1是） */
    private String hasViolation;

    /** 状态（0正常 1停用） */
    private String status;

    /** 审批状态 */
    private String approvalStatus;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /** 备注 */
    private String remark;
}
