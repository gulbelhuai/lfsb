package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 被征地参保补贴列表响应
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
public class ExpropriateeSubsidyListResp
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 姓名（来自基础信息） */
    private String name;

    /** 身份证号（来自基础信息） */
    private String idCardNo;

    /** 性别（来自基础信息） */
    private String gender;

    /** 联系电话（来自基础信息） */
    private String phone;

    /** 所属村委会名称（来自基础信息） */
    private String villageName;

    /** 征地批次 */
    private String landRequisitionBatch;

    /** 征地时所在村街 */
    private String villageStreet;

    /** 基准日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate baseDate;

    /** 截至基准日的年龄 */
    private Integer ageAtBaseDate;

    /** 被征地参保补贴年限 */
    private BigDecimal subsidyYears;

    /** 补贴金额 */
    private BigDecimal subsidyAmount;

    /** 选择参加城乡居保（0否 1是） */
    private String joinUrbanRuralInsurance;

    /** 选择参加职工养老（0否 1是） */
    private String joinEmployeePension;

    /** 状态（0正常 1停用） */
    private String status;

    /** 审批状态（draft/pending_review/approved/rejected） */
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
