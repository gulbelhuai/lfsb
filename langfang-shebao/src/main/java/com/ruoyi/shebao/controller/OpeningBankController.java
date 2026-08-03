package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.domain.OpeningBank;
import com.ruoyi.shebao.dto.OpeningBankListReq;
import com.ruoyi.shebao.dto.OpeningBankListResp;
import com.ruoyi.shebao.service.OpeningBankService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开户行（发放机构基础数据）
 */
@RestController
@RequestMapping("/shebao/openingBank")
public class OpeningBankController extends BaseController
{
    @Autowired
    private OpeningBankService openingBankService;

    @PreAuthorize("@ss.hasPermi('shebao:openingBank:list')")
    @GetMapping("/list")
    public AjaxResult list(OpeningBankListReq req)
    {
        if (req.getPageNum() == null)
        {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null)
        {
            req.setPageSize(10);
        }
        return AjaxResult.success(openingBankService.selectOpeningBankList(req));
    }

    /** 下拉选项：兼容字典形态 value/label，仅正常状态 */
    @GetMapping("/selectList")
    public AjaxResult selectList()
    {
        List<OpeningBank> banks = openingBankService.selectActiveList();
        List<Map<String, Object>> options = new ArrayList<>();
        for (OpeningBank bank : banks)
        {
            Map<String, Object> opt = new HashMap<>();
            opt.put("value", bank.getCode());
            opt.put("label", bank.getShortName());
            opt.put("code", bank.getCode());
            opt.put("shortName", bank.getShortName());
            opt.put("fullName", bank.getFullName());
            opt.put("bankNo", bank.getBankNo());
            options.add(opt);
        }
        return AjaxResult.success(options);
    }

    @PreAuthorize("@ss.hasPermi('shebao:openingBank:export')")
    @Log(title = "开户行", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OpeningBankListReq req)
    {
        req.setPageNum(1);
        req.setPageSize(Integer.MAX_VALUE);
        Page<OpeningBankListResp> page = openingBankService.selectOpeningBankList(req);
        ExcelUtil<OpeningBankListResp> util = new ExcelUtil<>(OpeningBankListResp.class);
        util.exportExcel(response, page.getRecords(), "开户行数据");
    }

    @PreAuthorize("@ss.hasPermi('shebao:openingBank:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(openingBankService.selectOpeningBankById(id));
    }

    @PreAuthorize("@ss.hasPermi('shebao:openingBank:add')")
    @Log(title = "开户行", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody OpeningBank bank)
    {
        if (!"0".equals(openingBankService.checkCodeUnique(bank)))
        {
            return AjaxResult.error("新增开户行失败，编码已存在");
        }
        return toAjax(openingBankService.insertOpeningBank(bank));
    }

    @PreAuthorize("@ss.hasPermi('shebao:openingBank:edit')")
    @Log(title = "开户行", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody OpeningBank bank)
    {
        return toAjax(openingBankService.updateOpeningBank(bank));
    }

    @PreAuthorize("@ss.hasPermi('shebao:openingBank:remove')")
    @Log(title = "开户行", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(openingBankService.deleteOpeningBankByIds(ids));
    }
}
