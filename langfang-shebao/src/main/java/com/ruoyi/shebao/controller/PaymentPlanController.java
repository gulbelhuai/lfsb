package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.dto.*;
import com.ruoyi.shebao.service.PaymentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
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
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:list') or @ss.hasPermi('shebao:payment:batch:upload')")
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
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:query') or @ss.hasPermi('shebao:finance:batch:list') or @ss.hasPermi('shebao:finance:bank:list')")
    @GetMapping(value = "/{id}/summary")
    public AjaxResult getSummary(@PathVariable("id") Long id)
    {
        List<PaymentPlanSummaryResp> list = paymentPlanService.selectSummaryByPlanId(id);
        return AjaxResult.success(list);
    }

    /**
     * 详情-明细
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:query') or @ss.hasPermi('shebao:finance:batch:list') or @ss.hasPermi('shebao:finance:bank:list')")
    @GetMapping(value = "/{id}/detail")
    public TableDataInfo getDetail(@PathVariable("id") Long id,
                                   @RequestParam(required = false) Integer pageNum,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) String personName,
                                   @RequestParam(required = false) String idCardNo)
    {
        Page<PaymentPlanDetailResp> page = paymentPlanService.selectDetailByPlanId(id, pageNum, pageSize, personName, idCardNo);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    /**
     * 导出支付计划明细（列与详情-明细表一致）
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:list') or @ss.hasPermi('shebao:payment:plan:query')")
    @Log(title = "支付计划明细导出", businessType = BusinessType.EXPORT)
    @PostMapping("/{id}/detail/export")
    public void exportDetail(HttpServletResponse response, @PathVariable("id") Long id)
    {
        List<PaymentPlanDetailExportResp> list = paymentPlanService.selectDetailExportByPlanId(id);
        ExcelUtil<PaymentPlanDetailExportResp> util = new ExcelUtil<>(PaymentPlanDetailExportResp.class);
        util.exportExcel(response, list, "支付计划明细");
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

    /**
     * 保存或提交
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:generate')")
    @Log(title = "支付计划", businessType = BusinessType.UPDATE)
    @PostMapping("/save")
    public AjaxResult save(@RequestBody PaymentPlanGenerateReq req)
    {
        return AjaxResult.success("操作成功", paymentPlanService.saveOrSubmit(req));
    }

    /**
     * 状态变更（提交等）
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:generate')")
    @Log(title = "支付计划状态变更", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/status")
    public AjaxResult changeStatus(@PathVariable("id") Long id, @RequestBody PaymentPlanStatusChangeReq req)
    {
        return toAjax(paymentPlanService.changeStatus(id, req));
    }

    /**
     * 撤销：删除支付计划及明细，需重新生成并提交
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:generate')")
    @Log(title = "支付计划撤销", businessType = BusinessType.DELETE)
    @PostMapping("/{id}/revoke")
    public AjaxResult revoke(@PathVariable("id") Long id)
    {
        return toAjax(paymentPlanService.revoke(id));
    }

    @PreAuthorize("@ss.hasPermi('shebao:payment:batch:upload')")
    @Log(title = "支付计划上传财务", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/finance-submit")
    public AjaxResult submitFinance(@PathVariable("id") Long id)
    {
        return toAjax(paymentPlanService.submitFinanceUpload(id));
    }

    /**
     * 详情-审核记录
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:query') or @ss.hasPermi('shebao:finance:batch:list') or @ss.hasPermi('shebao:finance:bank:list')")
    @GetMapping(value = "/{id}/audit")
    public AjaxResult getAudit(@PathVariable("id") Long id)
    {
        return AjaxResult.success(paymentPlanService.selectAuditByPlanId(id));
    }
}
