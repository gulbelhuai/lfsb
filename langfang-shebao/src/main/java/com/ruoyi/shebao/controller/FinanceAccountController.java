package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.domain.FinanceAccount;
import com.ruoyi.shebao.service.IFinanceAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 财务账户管理Controller
 *
 * @author ruoyi
 * @date 2025-01-20
 */
@RestController
@RequestMapping("/shebao/finance/account")
public class FinanceAccountController extends BaseController
{
    @Autowired
    private IFinanceAccountService financeAccountService;

    /**
     * 查询财务账户列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:account:list')")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(required = false) String accountName,
                          @RequestParam(required = false) String accountType,
                          @RequestParam(required = false) String status,
                          @RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize)
    {
        // TODO: 实现财务账户查询逻辑
        // 暂时返回所有账户
        List<FinanceAccount> list = financeAccountService.list();
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(list.size());
        return AjaxResult.success(rspData);
    }

    /**
     * 获取财务账户详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:account:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(financeAccountService.getById(id));
    }

    /**
     * 新增财务账户
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:account:add')")
    @Log(title = "财务账户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinanceAccount financeAccount)
    {
        return toAjax(financeAccountService.save(financeAccount));
    }

    /**
     * 修改财务账户
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:account:edit')")
    @Log(title = "财务账户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinanceAccount financeAccount)
    {
        return toAjax(financeAccountService.updateById(financeAccount));
    }

    /**
     * 删除财务账户
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:account:remove')")
    @Log(title = "财务账户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        for (Long id : ids) {
            financeAccountService.removeById(id);
        }
        return AjaxResult.success();
    }

    /**
     * 获取可用账户列表（用于下拉选择）
     */
    @GetMapping("/selectList")
    public AjaxResult selectList()
    {
        List<FinanceAccount> list = financeAccountService.lambdaQuery()
                .eq(FinanceAccount::getStatus, "0")
                .orderByAsc(FinanceAccount::getAccountName)
                .list();
        return AjaxResult.success(list);
    }
}
