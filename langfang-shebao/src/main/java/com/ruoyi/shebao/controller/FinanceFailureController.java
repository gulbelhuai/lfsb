package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.SubsidyDistributionListReq;
import com.ruoyi.shebao.dto.SubsidyDistributionListResp;
import com.ruoyi.shebao.service.SubsidyDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 失败处理Controller
 *
 * @author ruoyi
 * @date 2025-01-20
 */
@RestController
@RequestMapping("/shebao/finance/failure")
public class FinanceFailureController extends BaseController
{
    @Autowired
    private SubsidyDistributionService subsidyDistributionService;

    /**
     * 查询失败记录列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:failure:list')")
    @GetMapping("/list")
    public AjaxResult list(SubsidyDistributionListReq req)
    {
        // 设置分页参数默认值
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        // 默认查询失败状态
        if (req.getDistributionStatus() == null || req.getDistributionStatus().isEmpty()) {
            req.setDistributionStatus("failed");
        }
        Page<SubsidyDistributionListResp> page = subsidyDistributionService.selectSubsidyDistributionList(req);
        return AjaxResult.success(page);
    }

    /**
     * 获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:failure:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(subsidyDistributionService.selectSubsidyDistributionFormById(id));
    }

    /**
     * 重新发放
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:failure:retry')")
    @Log(title = "失败重发", businessType = BusinessType.UPDATE)
    @PostMapping("/retry/{id}")
    public AjaxResult retry(@PathVariable Long id)
    {
        // TODO: 实现重新发放逻辑
        return AjaxResult.success("已提交重新发放");
    }

    /**
     * 批量重新发放
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:failure:batchRetry')")
    @Log(title = "批量失败重发", businessType = BusinessType.UPDATE)
    @PostMapping("/batchRetry")
    public AjaxResult batchRetry(@RequestBody Long[] ids)
    {
        // TODO: 实现批量重新发放逻辑
        return AjaxResult.success("已提交批量重新发放，共" + ids.length + "条");
    }

    /**
     * 标记为人工处理
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:failure:manual')")
    @Log(title = "标记人工处理", businessType = BusinessType.UPDATE)
    @PostMapping("/manual/{id}")
    public AjaxResult markAsManual(@PathVariable Long id, @RequestParam String remark)
    {
        // TODO: 实现标记人工处理逻辑
        return AjaxResult.success("已标记为人工处理");
    }
}
