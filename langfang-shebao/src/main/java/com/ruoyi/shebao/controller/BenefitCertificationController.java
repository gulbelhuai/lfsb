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
 * 待遇认证Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/management/certification")
public class BenefitCertificationController extends BaseController
{
    @Autowired
    private LandLossResidentService landLossResidentService;

    /**
     * 查询待认证人员列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:certification:list')")
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
     * 提交认证
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:certification:submit')")
    @Log(title = "待遇认证", businessType = BusinessType.INSERT)
    @PostMapping("/submit/{id}")
    public AjaxResult submit(@PathVariable Long id,
                            @RequestParam(required = false) String certificationDate,
                            @RequestParam(required = false) String remark)
    {
        // TODO: 实现待遇认证提交逻辑
        return AjaxResult.success("认证成功");
    }

    /**
     * 批量认证
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:certification:batch')")
    @Log(title = "批量待遇认证", businessType = BusinessType.UPDATE)
    @PostMapping("/batchSubmit")
    public AjaxResult batchSubmit(@RequestBody Long[] ids)
    {
        // TODO: 实现批量认证逻辑
        return AjaxResult.success("批量认证成功，共" + ids.length + "人");
    }

    /**
     * 获取认证记录
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:certification:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(landLossResidentService.selectLandLossResidentFormById(id));
    }

    /**
     * 获取认证历史
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:certification:history')")
    @GetMapping("/history/{id}")
    public AjaxResult getHistory(@PathVariable Long id)
    {
        // TODO: 实现获取认证历史逻辑
        return AjaxResult.success();
    }
}
