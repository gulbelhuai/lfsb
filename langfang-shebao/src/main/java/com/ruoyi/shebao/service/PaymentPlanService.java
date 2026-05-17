package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.dto.*;

import java.util.List;

public interface PaymentPlanService
{
    Page<PaymentPlanListResp> selectPaymentPlanList(PaymentPlanListReq req);

    PaymentPlanPreviewResp preview(PaymentPlanPreviewReq req);

    Long generate(PaymentPlanGenerateReq req);
    Long saveOrSubmit(PaymentPlanGenerateReq req);
    int changeStatus(Long planId, PaymentPlanStatusChangeReq req);

    List<PaymentPlanSummaryResp> selectSummaryByPlanId(Long planId);
    List<PaymentPlanAuditResp> selectAuditByPlanId(Long planId);

    Page<PaymentPlanDetailResp> selectDetailByPlanId(Long planId, Integer pageNum, Integer pageSize);

    /** 上传财务：将财务状态置为待财务并记财务审核流水 */
    int submitFinanceUpload(Long planId);

    int financePass(Long planId, PaymentPlanFinanceStatusChangeReq req);

    int financeReject(Long planId, PaymentPlanFinanceStatusChangeReq req);

    int financeReviewPass(Long planId, PaymentPlanFinanceStatusChangeReq req);

    int financeReviewReject(Long planId, PaymentPlanFinanceStatusChangeReq req);

    int financeApprovePass(Long planId, PaymentPlanFinanceStatusChangeReq req);

    int financeApproveReject(Long planId, PaymentPlanFinanceStatusChangeReq req);
}
