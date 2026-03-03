package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.BenefitDeterminationListReq;
import com.ruoyi.shebao.dto.BenefitDeterminationListResp;
import com.ruoyi.shebao.service.IBenefitDeterminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 待遇核定复核Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/benefit/review")
public class BenefitReviewController extends BaseController
{
    @Autowired
    private IBenefitDeterminationService benefitDeterminationService;

    /**
     * 查询待复核列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:review:list')")
    @GetMapping("/list")
    public TableDataInfo list(BenefitDeterminationListReq req)
    {
        if (req.getPageNum() == null) req.setPageNum(1);
        if (req.getPageSize() == null) req.setPageSize(10);
        if (req.getApprovalStatus() == null) req.setApprovalStatus("pending_review");
        Page<BenefitDeterminationListResp> page = benefitDeterminationService.selectBenefitDeterminationList(req);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 复核通过
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:review:approve')")
    @Log(title = "待遇核定复核", businessType = BusinessType.UPDATE)
    @PostMapping("/approve/{id}")
    public AjaxResult approve(@PathVariable Long id, @RequestParam(required = false) String remark)
    {
        return toAjax(benefitDeterminationService.approveReview(id, remark));
    }

    /**
     * 复核驳回
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:review:reject')")
    @Log(title = "待遇核定复核", businessType = BusinessType.UPDATE)
    @PostMapping("/reject/{id}")
    public AjaxResult reject(@PathVariable Long id, @RequestParam String reason)
    {
        return toAjax(benefitDeterminationService.rejectReview(id, reason));
    }
}
