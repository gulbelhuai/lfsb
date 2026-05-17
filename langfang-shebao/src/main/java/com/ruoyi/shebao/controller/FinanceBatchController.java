package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.PaymentPlanFinanceStatusChangeReq;
import com.ruoyi.shebao.dto.PaymentPlanListReq;
import com.ruoyi.shebao.dto.PaymentPlanListResp;
import com.ruoyi.shebao.service.PaymentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 财务批次管理（基于支付计划）
 */
@RestController
@RequestMapping("/shebao/finance/batch")
public class FinanceBatchController extends BaseController
{
    @Autowired
    private PaymentPlanService paymentPlanService;

    /**
     * 查询已进入财务流程的支付计划列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:batch:list')")
    @GetMapping("/list")
    public TableDataInfo list(PaymentPlanListReq req)
    {
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        req.setFinanceEnteredOnly(true);
        Page<PaymentPlanListResp> page = paymentPlanService.selectPaymentPlanList(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    @PreAuthorize("@ss.hasPermi('shebao:finance:batch:financePass')")
    @Log(title = "财务批次-财务通过", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/finance-pass")
    public AjaxResult financePass(@PathVariable Long id, @RequestBody(required = false) PaymentPlanFinanceStatusChangeReq req)
    {
        return toAjax(paymentPlanService.financePass(id, req));
    }

    @PreAuthorize("@ss.hasPermi('shebao:finance:batch:financeReject')")
    @Log(title = "财务批次-财务驳回", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/finance-reject")
    public AjaxResult financeReject(@PathVariable Long id, @RequestBody PaymentPlanFinanceStatusChangeReq req)
    {
        return toAjax(paymentPlanService.financeReject(id, req));
    }

    @PreAuthorize("@ss.hasPermi('shebao:finance:batch:reviewPass')")
    @Log(title = "财务批次-复核通过", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/review-pass")
    public AjaxResult reviewPass(@PathVariable Long id, @RequestBody(required = false) PaymentPlanFinanceStatusChangeReq req)
    {
        return toAjax(paymentPlanService.financeReviewPass(id, req));
    }

    @PreAuthorize("@ss.hasPermi('shebao:finance:batch:reviewReject')")
    @Log(title = "财务批次-复核驳回", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/review-reject")
    public AjaxResult reviewReject(@PathVariable Long id, @RequestBody PaymentPlanFinanceStatusChangeReq req)
    {
        return toAjax(paymentPlanService.financeReviewReject(id, req));
    }

    @PreAuthorize("@ss.hasPermi('shebao:finance:batch:approvePass')")
    @Log(title = "财务批次-审批通过", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/approve-pass")
    public AjaxResult approvePass(@PathVariable Long id, @RequestBody(required = false) PaymentPlanFinanceStatusChangeReq req)
    {
        return toAjax(paymentPlanService.financeApprovePass(id, req));
    }

    @PreAuthorize("@ss.hasPermi('shebao:finance:batch:approveReject')")
    @Log(title = "财务批次-审批驳回", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/approve-reject")
    public AjaxResult approveReject(@PathVariable Long id, @RequestBody PaymentPlanFinanceStatusChangeReq req)
    {
        return toAjax(paymentPlanService.financeApproveReject(id, req));
    }
}
