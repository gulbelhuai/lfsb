package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.domain.StreetOffice;
import com.ruoyi.shebao.dto.StreetOfficeListReq;
import com.ruoyi.shebao.dto.StreetOfficeListResp;
import com.ruoyi.shebao.service.StreetOfficeService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 街道办事处信息Controller
 *
 * @author ruoyi
 * @date 2025-10-18
 */
@RestController
@RequestMapping({"/shebao/streetOffice"})
public class StreetOfficeController extends BaseController
{
    @Autowired
    private StreetOfficeService streetOfficeService;

    /**
     * 查询街道办事处信息列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:streetOffice:list')")
    @GetMapping("/list")
    public AjaxResult list(StreetOfficeListReq req)
    {
        // 设置分页参数默认值
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        Page<StreetOfficeListResp> page = streetOfficeService.selectStreetOfficeList(req);
        return AjaxResult.success(page);
    }

    /**
     * 导出街道办事处信息列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:streetOffice:export')")
    @Log(title = "街道办事处信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StreetOfficeListReq req)
    {
        req.setPageNum(1);
        req.setPageSize(Integer.MAX_VALUE);
        Page<StreetOfficeListResp> page = streetOfficeService.selectStreetOfficeList(req);
        ExcelUtil<StreetOfficeListResp> util = new ExcelUtil<StreetOfficeListResp>(StreetOfficeListResp.class);
        util.exportExcel(response, page.getRecords(), "街道办事处信息数据");
    }

    /**
     * 获取街道办事处信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:streetOffice:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(streetOfficeService.selectStreetOfficeById(id));
    }

    /**
     * 新增街道办事处信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:streetOffice:add')")
    @Log(title = "街道办事处信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody StreetOffice streetOffice)
    {
        if (!"0".equals(streetOfficeService.checkStreetCodeUnique(streetOffice)))
        {
            return AjaxResult.error("新增街道办'" + streetOffice.getStreetName() + "'失败，街道办编码已存在");
        }
        if (!"0".equals(streetOfficeService.checkStreetNameUnique(streetOffice)))
        {
            return AjaxResult.error("新增街道办'" + streetOffice.getStreetName() + "'失败，街道办名称已存在");
        }
        return toAjax(streetOfficeService.insertStreetOffice(streetOffice));
    }

    /**
     * 修改街道办事处信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:streetOffice:edit')")
    @Log(title = "街道办事处信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody StreetOffice streetOffice)
    {
        if (!"0".equals(streetOfficeService.checkStreetCodeUnique(streetOffice)))
        {
            return AjaxResult.error("修改街道办'" + streetOffice.getStreetName() + "'失败，街道办编码已存在");
        }
        if (!"0".equals(streetOfficeService.checkStreetNameUnique(streetOffice)))
        {
            return AjaxResult.error("修改街道办'" + streetOffice.getStreetName() + "'失败，街道办名称已存在");
        }
        return toAjax(streetOfficeService.updateStreetOffice(streetOffice));
    }

    /**
     * 删除街道办事处信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:streetOffice:remove')")
    @Log(title = "街道办事处信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(streetOfficeService.deleteStreetOfficeByIds(ids));
    }

    /**
     * 获取所有正常的街道办列表（用于下拉选择）
     */
    @GetMapping("/selectList")
    public AjaxResult selectList()
    {
        List<StreetOffice> list = streetOfficeService.selectNormalStreetOfficeList();
        return AjaxResult.success(list);
    }
}
