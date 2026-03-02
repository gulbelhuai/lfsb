package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.dto.LandLossResidentListReq;
import com.ruoyi.shebao.dto.LandLossResidentListResp;
import com.ruoyi.shebao.dto.LandLossResidentFormDto;
import com.ruoyi.shebao.service.LandLossResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 失地居民信息Controller
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@RestController
@RequestMapping("/shebao/landLossResident")
public class LandLossResidentController extends BaseController
{
    @Autowired
    private LandLossResidentService landLossResidentService;

    /**
     * 查询失地居民信息列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:landLossResident:list')")
    @GetMapping("/list")
    public AjaxResult list(LandLossResidentListReq req)
    {
        Page<LandLossResidentListResp> page = landLossResidentService.selectLandLossResidentList(req);
        return AjaxResult.success(page);
    }

    /**
     * 导出失地居民信息列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:landLossResident:export')")
    @Log(title = "失地居民信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, LandLossResidentListReq req)
    {
        Page<LandLossResidentListResp> page = landLossResidentService.selectLandLossResidentList(req);
        ExcelUtil<LandLossResidentListResp> util = new ExcelUtil<LandLossResidentListResp>(LandLossResidentListResp.class);
        util.exportExcel(response, page.getRecords(), "失地居民信息数据");
    }

    /**
     * 获取失地居民信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:landLossResident:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(landLossResidentService.selectLandLossResidentFormById(id));
    }

    /**
     * 根据身份证号获取表单数据（智能填充基础信息）
     */
    @GetMapping("/getFormDataByIdCardNo")
    public AjaxResult getFormDataByIdCardNo(@RequestParam String idCardNo)
    {
        return AjaxResult.success(landLossResidentService.getFormDataByIdCardNo(idCardNo));
    }

    /**
     * 新增失地居民信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:landLossResident:add')")
    @Log(title = "失地居民信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody LandLossResidentFormDto formDto)
    {
        return toAjax(landLossResidentService.insertLandLossResident(formDto));
    }

    /**
     * 修改失地居民信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:landLossResident:edit')")
    @Log(title = "失地居民信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody LandLossResidentFormDto formDto)
    {
        return toAjax(landLossResidentService.updateLandLossResident(formDto));
    }

    /**
     * 删除失地居民信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:landLossResident:remove')")
    @Log(title = "失地居民信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(landLossResidentService.deleteLandLossResidentByIds(ids));
    }

    /**
     * 下载模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<LandLossResidentFormDto> util = new ExcelUtil<LandLossResidentFormDto>(LandLossResidentFormDto.class);
        util.importTemplateExcel(response, "失地居民信息数据");
    }

    /**
     * 导入数据
     */
    @PreAuthorize("@ss.hasPermi('shebao:landLossResident:import')")
    @Log(title = "失地居民信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<LandLossResidentFormDto> util = new ExcelUtil<LandLossResidentFormDto>(LandLossResidentFormDto.class);
        List<LandLossResidentFormDto> landLossResidentList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = landLossResidentService.importLandLossResident(landLossResidentList, updateSupport, operName);
        return AjaxResult.success(message);
    }
}
