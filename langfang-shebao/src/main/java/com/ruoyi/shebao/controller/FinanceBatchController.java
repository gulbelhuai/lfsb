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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 财务批次管理Controller
 *
 * @author ruoyi
 * @date 2025-01-20
 */
@RestController
@RequestMapping("/shebao/finance/batch")
public class FinanceBatchController extends BaseController
{
    @Autowired
    private IDistributionBatchService distributionBatchService;

    /**
     * 查询批次列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:batch:list')")
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
        Page<DistributionBatch> page = distributionBatchService.selectDistributionBatchList(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    /**
     * 获取批次详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:batch:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(distributionBatchService.getById(id));
    }

    /**
     * 提交银行发放
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:batch:submit')")
    @Log(title = "提交银行发放", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{id}")
    public AjaxResult submitToBank(@PathVariable Long id)
    {
        // TODO: 实现提交银行发放逻辑
        return AjaxResult.success("已提交银行发放");
    }

    /**
     * 导入银行发放结果
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:batch:import')")
    @Log(title = "导入发放结果", businessType = BusinessType.IMPORT)
    @PostMapping("/importResult/{id}")
    public AjaxResult importResult(@PathVariable Long id)
    {
        // TODO: 实现导入银行结果逻辑
        return AjaxResult.success("导入成功");
    }

    /**
     * 完成批次
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:batch:complete')")
    @Log(title = "完成批次", businessType = BusinessType.UPDATE)
    @PostMapping("/complete/{id}")
    public AjaxResult complete(@PathVariable Long id)
    {
        // TODO: 实现完成批次逻辑
        return AjaxResult.success("批次已完成");
    }
}
