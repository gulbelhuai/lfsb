package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.*;
import com.ruoyi.shebao.service.PaymentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付计划生成Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/payment/plan")
public class PaymentPlanController extends BaseController
{
    @Autowired
    private PaymentPlanService paymentPlanService;

    /**
     * 查询支付计划列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:list')")
    @GetMapping("/list")
    public TableDataInfo list(PaymentPlanListReq req)
    {
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        Page<PaymentPlanListResp> page = paymentPlanService.selectPaymentPlanList(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    /**
     * 详情-汇总
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:query')")
    @GetMapping(value = "/{id}/summary")
    public AjaxResult getSummary(@PathVariable("id") Long id)
    {
        List<PaymentPlanSummaryResp> list = paymentPlanService.selectSummaryByPlanId(id);
        return AjaxResult.success(list);
    }

    /**
     * 详情-明细
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:query')")
    @GetMapping(value = "/{id}/detail")
    public TableDataInfo getDetail(@PathVariable("id") Long id,
                                   @RequestParam(required = false) Integer pageNum,
                                   @RequestParam(required = false) Integer pageSize)
    {
        Page<PaymentPlanDetailResp> page = paymentPlanService.selectDetailByPlanId(id, pageNum, pageSize);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    /**
     * 预览
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:generate')")
    @PostMapping("/preview")
    public AjaxResult preview(@RequestBody PaymentPlanPreviewReq req)
    {
        return AjaxResult.success(paymentPlanService.preview(req));
    }

    /**
     * 生成
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:generate')")
    @Log(title = "支付计划", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody PaymentPlanGenerateReq req)
    {
        return AjaxResult.success("操作成功", paymentPlanService.generate(req));
    }
}
