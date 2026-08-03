package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 补贴发放记录（全局）查询：财务审核通过的支付计划明细
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DistributionRecordListReq extends PageReq
{
    /** 业务期 yyyy-MM */
    private String businessPeriod;

    /** 发放类型：normal=正常 / second=二次 */
    private String determinationType;

    /** 补贴类型 */
    private String subsidyType;

    /** 发放机构（开户行 code） */
    private String grantOrg;

    /** 身份证号 */
    private String idCardNo;

    /** 姓名（模糊） */
    private String personName;

    /** 批次号 */
    private String batchNo;

    /** 派生发放状态：distributing / paid / failed */
    private String payStatus;
}
