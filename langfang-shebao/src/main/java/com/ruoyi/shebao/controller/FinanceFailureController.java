package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.shebao.dto.PaymentPlanFailureListReq;
import com.ruoyi.shebao.dto.PaymentPlanFailureListResp;
import com.ruoyi.shebao.service.PaymentPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 失败处理：基于支付计划明细中银行发放失败记录（distribution_result=failed）
 */
@RestController
@RequestMapping("/shebao/finance/failure")
public class FinanceFailureController extends BaseController
{
    @Autowired
    private PaymentPlanService paymentPlanService;

    /**
     * 查询银行发放失败明细列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:failure:list')")
    @GetMapping("/list")
    public TableDataInfo list(PaymentPlanFailureListReq req)
    {
        if (req.getPageNum() == null)
        {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null)
        {
            req.setPageSize(10);
        }
        Page<PaymentPlanFailureListResp> page = paymentPlanService.selectFailureList(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }
}
