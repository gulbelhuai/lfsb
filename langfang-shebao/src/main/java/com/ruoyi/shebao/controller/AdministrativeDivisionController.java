package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.domain.AdministrativeDivision;
import com.ruoyi.shebao.dto.AdministrativeDivisionListReq;
import com.ruoyi.shebao.dto.AdministrativeDivisionListResp;
import com.ruoyi.shebao.service.AdministrativeDivisionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

/**
 * 行政区划Controller
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/shebao/administrativeDivision")
public class AdministrativeDivisionController extends BaseController
{
    private final AdministrativeDivisionService administrativeDivisionService;

    /**
     * 查询行政区划列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:administrativeDivision:list')")
    @GetMapping("/list")
    public AjaxResult list(AdministrativeDivisionListReq req)
    {
        Page<AdministrativeDivisionListResp> page = administrativeDivisionService.selectAdministrativeDivisionList(req);
        return AjaxResult.success(page);
    }

    /**
     * 导出行政区划列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:administrativeDivision:export')")
    @Log(title = "行政区划", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AdministrativeDivisionListReq req) throws IOException
    {
        Page<AdministrativeDivisionListResp> page = administrativeDivisionService.selectAdministrativeDivisionList(req);
        ExcelUtil<AdministrativeDivisionListResp> util = new ExcelUtil<>(AdministrativeDivisionListResp.class);
        util.exportExcel(response, page.getRecords(), "行政区划数据");
    }

    /**
     * 获取行政区划详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:administrativeDivision:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(administrativeDivisionService.selectAdministrativeDivisionById(id));
    }

    /**
     * 新增行政区划
     */
    @PreAuthorize("@ss.hasPermi('shebao:administrativeDivision:add')")
    @Log(title = "行政区划", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public AjaxResult add(@Validated @RequestBody AdministrativeDivision division)
    {
        // 使用带全路径维护的新增方法
        return toAjax(administrativeDivisionService.insertDivisionWithPath(division));
    }

    /**
     * 修改行政区划
     */
    @PreAuthorize("@ss.hasPermi('shebao:administrativeDivision:edit')")
    @Log(title = "行政区划", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody AdministrativeDivision division)
    {
        // 使用带全路径维护的修改方法
        return toAjax(administrativeDivisionService.updateDivisionWithPath(division));
    }

    /**
     * 删除行政区划
     */
    @PreAuthorize("@ss.hasPermi('shebao:administrativeDivision:remove')")
    @Log(title = "行政区划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(administrativeDivisionService.deleteAdministrativeDivisionByIds(ids));
    }

    /**
     * 修改行政区划状态
     */
    @PreAuthorize("@ss.hasPermi('shebao:administrativeDivision:edit')")
    @Log(title = "行政区划状态修改", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody AdministrativeDivision division)
    {
        return toAjax(administrativeDivisionService.updateById(division));
    }

    /**
     * 获取行政区划选择框列表（所有状态为正常的行政区划）
     */
    @GetMapping("/optionSelect")
    public AjaxResult optionSelect()
    {
        List<AdministrativeDivision> list = administrativeDivisionService.lambdaQuery()
                .eq(AdministrativeDivision::getStatus, "0")
                .orderByAsc(AdministrativeDivision::getSortOrder)
                .list();
        return success(list);
    }

    /**
     * 获取行政区划树形结构（用于级联选择器或树形组件）
     */
    @GetMapping("/tree")
    public AjaxResult getDivisionTree(
            @RequestParam(required = false, defaultValue = "5") Integer maxLevel,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyWithChildren)
    {
        List<AdministrativeDivision> tree = administrativeDivisionService.getDivisionTree(maxLevel, onlyWithChildren);
        return success(tree);
    }

    /**
     * 获取指定父级下的直接子级行政区划（用于级联选择器动态加载）
     */
    @GetMapping("/children")
    public AjaxResult getDirectChildren(
            @RequestParam(required = false) String parentCode,
            @RequestParam(required = false) Integer targetLevel)
    {
        List<AdministrativeDivision> children = administrativeDivisionService.getDirectChildren(parentCode, targetLevel);
        return success(children);
    }

    /**
     * 下载行政区划导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException
    {
        ExcelUtil<AdministrativeDivision> util = new ExcelUtil<>(AdministrativeDivision.class);
        util.importTemplateExcel(response, "行政区划数据");
    }

    /**
     * 批量导入行政区划
     */
    @Log(title = "行政区划", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('shebao:administrativeDivision:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<AdministrativeDivision> util = new ExcelUtil<>(AdministrativeDivision.class);
        List<AdministrativeDivision> divisionList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = administrativeDivisionService.importAdministrativeDivision(divisionList, updateSupport, operName);
        return success(message);
    }

    /**
     * 根据父级编码查询下级行政区划
     */
    @GetMapping("/selectByParentCode")
    public AjaxResult selectByParentCode(@RequestParam String parentCode)
    {
        List<AdministrativeDivision> list = administrativeDivisionService.selectByParentCode(parentCode);
        return success(list);
    }

    /**
     * 根据行政级别查询行政区划
     */
    @GetMapping("/selectByLevel")
    public AjaxResult selectByLevel(@RequestParam Integer divisionLevel)
    {
        List<AdministrativeDivision> list = administrativeDivisionService.selectByDivisionLevel(divisionLevel);
        return success(list);
    }

    /**
     * 获取省级行政区划列表
     */
    @GetMapping("/provinces")
    public AjaxResult getProvinces()
    {
        List<AdministrativeDivision> list = administrativeDivisionService.getProvinceList();
        return success(list);
    }

    /**
     * 获取市级行政区划列表
     */
    @GetMapping("/cities")
    public AjaxResult getCities(@RequestParam String provinceCode)
    {
        List<AdministrativeDivision> list = administrativeDivisionService.getCityList(provinceCode);
        return success(list);
    }

    /**
     * 获取县级行政区划列表
     */
    @GetMapping("/counties")
    public AjaxResult getCounties(@RequestParam String cityCode)
    {
        List<AdministrativeDivision> list = administrativeDivisionService.getCountyList(cityCode);
        return success(list);
    }

    /**
     * 获取乡镇级行政区划列表
     */
    @GetMapping("/townships")
    public AjaxResult getTownships(@RequestParam String countyCode)
    {
        List<AdministrativeDivision> list = administrativeDivisionService.getTownshipList(countyCode);
        return success(list);
    }

    /**
     * 校验行政区划编码是否唯一
     */
    @GetMapping("/checkDivisionCodeUnique")
    public AjaxResult checkDivisionCodeUnique(AdministrativeDivision division)
    {
        String result = administrativeDivisionService.checkDivisionCodeUnique(division);
        return success(result);
    }
}
