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
 * 待遇暂停/恢复Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/management/suspension")
public class BenefitSuspensionController extends BaseController
{
    @Autowired
    private LandLossResidentService landLossResidentService;

    /**
     * 查询人员列表（用于暂停/恢复）
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:suspension:list')")
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
        Page<LandLossResidentListResp> page = landLossResidentService.selectLandLossResidentList(req);
        return AjaxResult.success(page);
    }

    /**
     * 暂停待遇
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:suspension:suspend')")
    @Log(title = "待遇暂停", businessType = BusinessType.UPDATE)
    @PostMapping("/suspend/{id}")
    public AjaxResult suspend(@PathVariable Long id,
                             @RequestParam String reason,
                             @RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate)
    {
        // TODO: 实现待遇暂停逻辑
        return AjaxResult.success("待遇已暂停");
    }

    /**
     * 恢复待遇
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:suspension:resume')")
    @Log(title = "待遇恢复", businessType = BusinessType.UPDATE)
    @PostMapping("/resume/{id}")
    public AjaxResult resume(@PathVariable Long id, @RequestParam(required = false) String remark)
    {
        // TODO: 实现待遇恢复逻辑
        return AjaxResult.success("待遇已恢复");
    }

    /**
     * 获取暂停记录
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:suspension:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(landLossResidentService.selectLandLossResidentFormById(id));
    }
}
