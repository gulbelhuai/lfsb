package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.dto.DemolitionResidentListReq;
import com.ruoyi.shebao.dto.DemolitionResidentListResp;
import com.ruoyi.shebao.dto.DemolitionResidentFormDto;
import com.ruoyi.shebao.service.DemolitionResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 拆迁居民信息Controller
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@RestController
@RequestMapping("/shebao/demolitionResident")
public class DemolitionResidentController extends BaseController
{
    @Autowired
    private DemolitionResidentService demolitionResidentService;

    /**
     * 查询拆迁居民信息列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:demolitionResident:list')")
    @GetMapping("/list")
    public AjaxResult list(DemolitionResidentListReq req)
    {
        Page<DemolitionResidentListResp> page = demolitionResidentService.selectDemolitionResidentList(req);
        return AjaxResult.success(page);
    }

    /**
     * 获取拆迁居民信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:demolitionResident:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(demolitionResidentService.selectDemolitionResidentFormById(id));
    }

    /**
     * 导出拆迁居民信息列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:demolitionResident:export')")
    @Log(title = "拆迁居民信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DemolitionResidentListReq req)
    {
        Page<DemolitionResidentListResp> page = demolitionResidentService.selectDemolitionResidentList(req);
        ExcelUtil<DemolitionResidentListResp> util = new ExcelUtil<DemolitionResidentListResp>(DemolitionResidentListResp.class);
        util.exportExcel(response, page.getRecords(), "拆迁居民信息数据");
    }

    /**
     * 根据身份证号获取表单数据（智能填充基础信息）
     */
    @GetMapping("/getFormDataByIdCardNo")
    public AjaxResult getFormDataByIdCardNo(@RequestParam String idCardNo)
    {
        return AjaxResult.success(demolitionResidentService.getFormDataByIdCardNo(idCardNo));
    }

    /**
     * 新增拆迁居民信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:demolitionResident:add')")
    @Log(title = "拆迁居民信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody DemolitionResidentFormDto formDto)
    {
        return toAjax(demolitionResidentService.insertDemolitionResident(formDto));
    }

    /**
     * 修改拆迁居民信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:demolitionResident:edit')")
    @Log(title = "拆迁居民信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody DemolitionResidentFormDto formDto)
    {
        return toAjax(demolitionResidentService.updateDemolitionResident(formDto));
    }

    /**
     * 删除拆迁居民信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:demolitionResident:remove')")
    @Log(title = "拆迁居民信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(demolitionResidentService.deleteDemolitionResidentByIds(ids));
    }

    /**
     * 下载模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<DemolitionResidentFormDto> util = new ExcelUtil<DemolitionResidentFormDto>(DemolitionResidentFormDto.class);
        util.importTemplateExcel(response, "拆迁居民信息数据");
    }

    /**
     * 导入数据
     */
    @PreAuthorize("@ss.hasPermi('shebao:demolitionResident:import')")
    @Log(title = "拆迁居民信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<DemolitionResidentFormDto> util = new ExcelUtil<DemolitionResidentFormDto>(DemolitionResidentFormDto.class);
        List<DemolitionResidentFormDto> demolitionResidentList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = demolitionResidentService.importDemolitionResident(demolitionResidentList, updateSupport, operName);
        return AjaxResult.success(message);
    }
}
