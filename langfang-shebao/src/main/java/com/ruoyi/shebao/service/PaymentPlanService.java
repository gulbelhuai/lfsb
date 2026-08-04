package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.PaymentPlan;
import com.ruoyi.shebao.domain.PaymentPlanDetail;
import com.ruoyi.shebao.dto.*;

import java.util.List;

public interface PaymentPlanService
{
    Page<PaymentPlanListResp> selectPaymentPlanList(PaymentPlanListReq req);

    PaymentPlanPreviewResp preview(PaymentPlanPreviewReq req);

    Long generate(PaymentPlanGenerateReq req);
    Long saveOrSubmit(PaymentPlanGenerateReq req);
    int changeStatus(Long planId, PaymentPlanStatusChangeReq req);

    /** 撤销：物理删除支付计划及明细/汇总/审核记录；草稿/驳回/待复核(未进财务)可撤销 */
    int revoke(Long planId);

    List<PaymentPlanSummaryResp> selectSummaryByPlanId(Long planId);
    List<PaymentPlanAuditResp> selectAuditByPlanId(Long planId);

    Page<PaymentPlanDetailResp> selectDetailByPlanId(Long planId, Integer pageNum, Integer pageSize,
                                                     String personName, String idCardNo);

    /** 导出：查询计划全部明细（字段与详情-明细表一致） */
    List<PaymentPlanDetailExportResp> selectDetailExportByPlanId(Long planId);

    /** 上传财务：将财务状态置为待财务并记财务审核流水 */
    int submitFinanceUpload(Long planId);

    int financePass(Long planId, PaymentPlanFinanceStatusChangeReq req);

    int financeReject(Long planId, PaymentPlanFinanceStatusChangeReq req);

    int financeReviewPass(Long planId, PaymentPlanFinanceStatusChangeReq req);

    int financeReviewReject(Long planId, PaymentPlanFinanceStatusChangeReq req);

    int financeApprovePass(Long planId, PaymentPlanFinanceStatusChangeReq req);

    int financeApproveReject(Long planId, PaymentPlanFinanceStatusChangeReq req);

    /** 银行发放：加载批次（校验存在） */
    PaymentPlan getBankPlan(Long planId);

    /** 银行发放：本批次全部明细（统一廊坊模板导出） */
    List<PaymentPlanDetail> selectBankExportDetails(Long planId);

    /** 银行发放：提交银行（待发放 → 已提交银行） */
    int submitToBank(Long planId);

    /** 银行发放：导入失败数据，按身份证号标记明细发放失败 */
    int importBankFailures(Long planId, List<PaymentPlanBankFailureRow> rows);

    /** 银行发放：标记已完成（未失败明细记为成功，批次置已完成） */
    int completeDistribution(Long planId);

    /** 失败处理：查询银行发放失败的支付计划明细 */
    Page<PaymentPlanFailureListResp> selectFailureList(PaymentPlanFailureListReq req);
}
