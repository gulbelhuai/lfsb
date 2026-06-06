package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.FinanceBenefitRecoveryListReq;
import com.ruoyi.shebao.dto.FinanceBenefitRecoveryListResp;
import com.ruoyi.shebao.service.FinanceBenefitRecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 财务管理-待遇追回
 */
@RestController
@RequestMapping("/shebao/finance/recovery")
@RequiredArgsConstructor
public class FinanceBenefitRecoveryController extends BaseController
{
    private final FinanceBenefitRecoveryService financeBenefitRecoveryService;

    @PreAuthorize("@ss.hasPermi('shebao:finance:recovery:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinanceBenefitRecoveryListReq req)
    {
        if (req.getPageNum() == null)
        {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null)
        {
            req.setPageSize(10);
        }
        Page<FinanceBenefitRecoveryListResp> page = financeBenefitRecoveryService.list(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    @PreAuthorize("@ss.hasPermi('shebao:finance:recovery:confirm')")
    @Log(title = "待遇追回-确认已追回", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/confirm")
    public AjaxResult confirm(@PathVariable Long id)
    {
        financeBenefitRecoveryService.confirmRecovered(id);
        return AjaxResult.success();
    }
}
