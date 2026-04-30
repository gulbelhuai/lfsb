package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.dto.*;

import java.util.List;

public interface PaymentPlanService
{
    Page<PaymentPlanListResp> selectPaymentPlanList(PaymentPlanListReq req);

    PaymentPlanPreviewResp preview(PaymentPlanPreviewReq req);

    Long generate(PaymentPlanGenerateReq req);

    List<PaymentPlanSummaryResp> selectSummaryByPlanId(Long planId);

    Page<PaymentPlanDetailResp> selectDetailByPlanId(Long planId, Integer pageNum, Integer pageSize);
}
