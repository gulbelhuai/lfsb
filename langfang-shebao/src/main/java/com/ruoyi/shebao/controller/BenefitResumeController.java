package com.ruoyi.shebao.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.BenefitResumeCreateReq;
import com.ruoyi.shebao.dto.BenefitResumeListReq;
import com.ruoyi.shebao.service.BenefitResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 待遇恢复
 */
@RestController
@RequestMapping("/shebao/management/resume")
@RequiredArgsConstructor
public class BenefitResumeController extends BaseController
{
    private final BenefitResumeService benefitResumeService;

    @PreAuthorize("@ss.hasPermi('shebao:management:resume:list')")
    @GetMapping("/list")
    public TableDataInfo list(BenefitResumeListReq req)
    {
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        var page = benefitResumeService.list(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    @PreAuthorize("@ss.hasPermi('shebao:management:resume:add')")
    @GetMapping("/candidate")
    public AjaxResult candidate(@RequestParam String idCardNo)
    {
        return AjaxResult.success(benefitResumeService.findCandidateByIdCardNo(idCardNo));
    }

    @PreAuthorize("@ss.hasPermi('shebao:management:resume:add')")
    @Log(title = "待遇恢复", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Valid @RequestBody BenefitResumeCreateReq req)
    {
        return AjaxResult.success(benefitResumeService.create(req));
    }

    @PreAuthorize("@ss.hasPermi('shebao:management:resume:query')")
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable Long id)
    {
        return AjaxResult.success(benefitResumeService.getDetail(id));
    }
}
