package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.FinanceAccountFiscalAllocationReq;
import com.ruoyi.shebao.dto.FinanceAccountTransactionListReq;
import com.ruoyi.shebao.dto.FinanceAccountTransactionListResp;
import com.ruoyi.shebao.service.IFinanceAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 财务账户管理
 */
@RestController
@RequestMapping("/shebao/finance/account")
public class FinanceAccountController extends BaseController
{
    @Autowired
    private IFinanceAccountService financeAccountService;

    /**
     * 账户概览：按补贴类型展示各账户名称与余额
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:account:list')")
    @GetMapping("/overview")
    public AjaxResult overview()
    {
        return AjaxResult.success(financeAccountService.selectOverviewAccounts());
    }

    /**
     * 账户明细列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:account:list')")
    @GetMapping("/transaction/list")
    public TableDataInfo transactionList(FinanceAccountTransactionListReq req)
    {
        if (req.getPageNum() == null)
        {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null)
        {
            req.setPageSize(10);
        }
        Page<FinanceAccountTransactionListResp> page = financeAccountService.selectTransactionList(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    /**
     * 财政拨款
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:account:allocate')")
    @Log(title = "财务账户-财政拨款", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/fiscal-allocation")
    public AjaxResult fiscalAllocation(@PathVariable Long id, @RequestBody FinanceAccountFiscalAllocationReq req)
    {
        return toAjax(financeAccountService.fiscalAllocation(id, req));
    }

    /**
     * 查询财务账户列表（兼容旧接口）
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:account:list')")
    @GetMapping("/list")
    public AjaxResult list()
    {
        List<?> list = financeAccountService.selectOverviewAccounts();
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(list.size());
        return AjaxResult.success(rspData);
    }

    /**
     * 查询账户余额
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:account:list')")
    @GetMapping("/balance/{accountType}")
    public AjaxResult getBalance(@PathVariable String accountType)
    {
        var account = financeAccountService.lambdaQuery()
                .eq(com.ruoyi.shebao.domain.FinanceAccount::getAccountType, accountType)
                .eq(com.ruoyi.shebao.domain.FinanceAccount::getDelFlag, "0")
                .eq(com.ruoyi.shebao.domain.FinanceAccount::getStatus, "1")
                .last("limit 1")
                .one();
        return AjaxResult.success(account == null ? BigDecimal.ZERO : account.getBalance());
    }

    /**
     * 获取可用账户列表（下拉选择）
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:account:list')")
    @GetMapping("/selectList")
    public AjaxResult selectList()
    {
        return AjaxResult.success(financeAccountService.selectOverviewAccounts());
    }
}
