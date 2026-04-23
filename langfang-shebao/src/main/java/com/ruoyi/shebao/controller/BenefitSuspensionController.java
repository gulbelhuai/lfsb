package com.ruoyi.shebao.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.BenefitSuspensionCreateReq;
import com.ruoyi.shebao.dto.BenefitSuspensionListReq;
import com.ruoyi.shebao.service.BenefitSuspensionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class BenefitSuspensionController extends BaseController
{
    private final BenefitSuspensionService benefitSuspensionService;

    /**
     * 查询待遇暂停记录列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:suspension:list')")
    @GetMapping("/list")
    public TableDataInfo list(BenefitSuspensionListReq req)
    {
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        var page = benefitSuspensionService.list(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    /**
     * 按身份证查询待遇人员（仅已通过待遇核定）
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:suspension:add')")
    @GetMapping("/candidate")
    public AjaxResult candidate(@RequestParam String idCardNo)
    {
        return AjaxResult.success(benefitSuspensionService.findCandidateByIdCardNo(idCardNo));
    }

    /**
     * 新增待遇暂停记录
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:suspension:add')")
    @Log(title = "待遇暂停", businessType = BusinessType.INSERT)
    @PostMapping("/pause")
    public AjaxResult pause(@Valid @RequestBody BenefitSuspensionCreateReq req)
    {
        return AjaxResult.success(benefitSuspensionService.create(req));
    }

    /**
     * 获取待遇暂停详情
     */
    @PreAuthorize("@ss.hasPermi('shebao:management:suspension:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(benefitSuspensionService.getDetail(id));
    }
}
