package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.domain.DistributionBatch;
import com.ruoyi.shebao.dto.DistributionBatchListReq;
import com.ruoyi.shebao.service.IDistributionBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 支付计划复核Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/payment/review")
public class PaymentReviewController extends BaseController
{
    @Autowired
    private IDistributionBatchService distributionBatchService;

    /**
     * 查询待复核列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:review:list')")
    @GetMapping("/list")
    public TableDataInfo list(DistributionBatchListReq req)
    {
        // 设置分页参数默认值
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        // 默认查询待复核状态
        if (req.getStatus() == null) {
            req.setStatus("pending_review");
        }
        Page<DistributionBatch> page = distributionBatchService.selectDistributionBatchList(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    /**
     * 复核通过
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:review:approve')")
    @Log(title = "支付计划复核", businessType = BusinessType.UPDATE)
    @PostMapping("/approve/{id}")
    public AjaxResult approve(@PathVariable Long id, @RequestParam(required = false) String remark)
    {
        return toAjax(distributionBatchService.approveReview(id, remark));
    }

    /**
     * 复核驳回
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:review:reject')")
    @Log(title = "支付计划复核", businessType = BusinessType.UPDATE)
    @PostMapping("/reject/{id}")
    public AjaxResult reject(@PathVariable Long id, @RequestParam String reason)
    {
        return toAjax(distributionBatchService.rejectReview(id, reason));
    }
}
