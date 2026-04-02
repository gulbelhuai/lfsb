package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.dto.PersonCancelFormDto;
import com.ruoyi.shebao.dto.PersonCancelListReq;
import com.ruoyi.shebao.dto.PersonCancelListResp;
import com.ruoyi.shebao.service.PersonCancelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 人员注销登记Controller（独立列表+增删改）
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@RestController
@RequestMapping("/shebao/personCancel")
public class PersonCancelController extends BaseController
{
    @Autowired
    private PersonCancelService personCancelService;

    /**
     * 查询人员注销登记列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:cancel:list')")
    @GetMapping("/list")
    public AjaxResult list(PersonCancelListReq req)
    {
        Page<PersonCancelListResp> page = personCancelService.selectPersonCancelList(req);
        // 转换为若依框架的TableDataInfo格式：{rows: [], total: 0}
        return AjaxResult.success()
                .put("rows", page.getRecords())
                .put("total", page.getTotal());
    }

    /**
     * 获取人员注销登记详情
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:cancel:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(personCancelService.selectPersonCancelFormById(id));
    }

    /**
     * 新增人员注销登记
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:cancel:add')")
    @Log(title = "人员注销登记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PersonCancelFormDto formDto)
    {
        return toAjax(personCancelService.insertPersonCancel(formDto));
    }

    /**
     * 修改人员注销登记
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:cancel:edit')")
    @Log(title = "人员注销登记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PersonCancelFormDto formDto)
    {
        return toAjax(personCancelService.updatePersonCancel(formDto));
    }

    /**
     * 复核（通过/不通过）
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:cancel:review')")
    @Log(title = "人员注销登记复核", businessType = BusinessType.UPDATE)
    @PostMapping("/review/{id}")
    public AjaxResult review(@PathVariable Long id, @RequestParam Boolean approved, @RequestParam(required = false) String remark)
    {
        return toAjax(personCancelService.review(id, approved, remark));
    }

    /**
     * 删除人员注销登记
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:cancel:remove')")
    @Log(title = "人员注销登记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(personCancelService.deletePersonCancelByIds(ids));
    }

    /**
     * 导出人员注销登记列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:cancel:export')")
    @Log(title = "人员注销登记", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PersonCancelListReq req)
    {
        List<PersonCancelListResp> list = personCancelService.selectPersonCancelListNoPage(req);
        ExcelUtil<PersonCancelListResp> util = new ExcelUtil<PersonCancelListResp>(PersonCancelListResp.class);
        util.exportExcel(response, list, "人员注销登记数据");
    }
}

