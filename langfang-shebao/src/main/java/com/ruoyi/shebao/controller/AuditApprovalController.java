package com.ruoyi.shebao.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * 审批历史Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/audit/approval")
public class AuditApprovalController extends BaseController
{
    /**
     * 查询审批历史列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:audit:approval:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String businessType,
                             @RequestParam(required = false) Long businessId,
                             @RequestParam(required = false) String approvalStatus,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize)
    {
        // TODO: 实现审批历史查询逻辑
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(new ArrayList<>());
        rspData.setTotal(0);
        return rspData;
    }

    /**
     * 根据业务ID查询审批历史
     */
    @PreAuthorize("@ss.hasPermi('shebao:audit:approval:query')")
    @GetMapping("/business/{businessType}/{businessId}")
    public AjaxResult getByBusinessId(@PathVariable String businessType, @PathVariable Long businessId)
    {
        // TODO: 实现根据业务ID查询审批历史
        return AjaxResult.success(new ArrayList<>());
    }
}
