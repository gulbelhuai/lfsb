package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.shebao.dto.BenefitDeterminationListReq;
import com.ruoyi.shebao.dto.BenefitDeterminationListResp;
import com.ruoyi.shebao.service.IBenefitDeterminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 预到龄发放通知生成Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/benefit/notice")
public class BenefitNoticeController extends BaseController
{
    @Autowired
    private IBenefitDeterminationService benefitDeterminationService;

    /**
     * 查询预到龄人员列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:notice:list')")
    @GetMapping("/list")
    public AjaxResult list(BenefitDeterminationListReq req)
    {
        // 设置分页参数默认值
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        Page<BenefitDeterminationListResp> page = benefitDeterminationService.selectBenefitDeterminationList(req);
        return AjaxResult.success(page);
    }

    /**
     * 生成待遇核定记录
     */
    @PreAuthorize("@ss.hasPermi('shebao:benefit:notice:generate')")
    @PostMapping("/generate")
    public AjaxResult generate(@RequestParam Long personId)
    {
        // TODO: 实现生成逻辑
        return AjaxResult.success("生成成功");
    }
}
