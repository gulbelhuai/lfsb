package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.LandLossResidentListReq;
import com.ruoyi.shebao.dto.LandLossResidentListResp;
import com.ruoyi.shebao.service.LandLossResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 人员登记复核Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/person/review")
public class PersonReviewController extends BaseController
{
    @Autowired
    private LandLossResidentService landLossResidentService;

    /**
     * 查询待复核人员列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:review:list')")
    @GetMapping("/list")
    public AjaxResult list(LandLossResidentListReq req)
    {
        // 设置分页参数默认值
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        // 默认查询待复核状态
        if (req.getStatus() == null || req.getStatus().isEmpty()) {
            req.setStatus("pending_review");
        }
        Page<LandLossResidentListResp> page = landLossResidentService.selectLandLossResidentList(req);
        return AjaxResult.success(page);
    }

    /**
     * 复核通过
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:review:approve')")
    @Log(title = "人员登记复核", businessType = BusinessType.UPDATE)
    @PostMapping("/approve/{id}")
    public AjaxResult approve(@PathVariable Long id, @RequestParam(required = false) String remark)
    {
        // TODO: 实现复核通过逻辑
        return AjaxResult.success("复核通过");
    }

    /**
     * 复核驳回
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:review:reject')")
    @Log(title = "人员登记复核", businessType = BusinessType.UPDATE)
    @PostMapping("/reject/{id}")
    public AjaxResult reject(@PathVariable Long id, @RequestParam String reason)
    {
        // TODO: 实现复核驳回逻辑
        return AjaxResult.error("复核驳回");
    }

    /**
     * 获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:review:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(landLossResidentService.selectLandLossResidentFormById(id));
    }
}
