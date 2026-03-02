package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 发放审核记录对象 shebao_distribution_review
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_distribution_review")
public class DistributionReview extends BaseDomain
{
    /** 主键ID */
    private Long id;

    /** 发放记录ID（关联shebao_subsidy_distribution.id） */
    @Excel(name = "发放记录ID")
    private Long distributionId;

    /** 操作类型（1提交审核 2同意 3拒绝 4发放） */
    @Excel(name = "操作类型", readConverterExp = "1=提交审核,2=同意,3=拒绝,4=发放")
    private String operationType;

    /** 操作说明 */
    @Excel(name = "操作说明")
    private String operationRemark;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operatorName;

    /** 操作时间 */
    @Excel(name = "操作时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
}

