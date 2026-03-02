package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.dto.VillageOfficialListReq;
import com.ruoyi.shebao.dto.VillageOfficialListResp;
import com.ruoyi.shebao.dto.VillageOfficialFormDto;
import com.ruoyi.shebao.service.VillageOfficialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 村干部信息Controller
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@RestController
@RequestMapping("/shebao/villageOfficial")
public class VillageOfficialController extends BaseController
{
    @Autowired
    private VillageOfficialService villageOfficialService;

    /**
     * 查询村干部信息列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageOfficial:list')")
    @GetMapping("/list")
    public AjaxResult list(VillageOfficialListReq req)
    {
        Page<VillageOfficialListResp> page = villageOfficialService.selectVillageOfficialList(req);
        return AjaxResult.success(page);
    }

    /**
     * 导出村干部信息列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageOfficial:export')")
    @Log(title = "村干部信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, VillageOfficialListReq req)
    {
        Page<VillageOfficialListResp> page = villageOfficialService.selectVillageOfficialList(req);
        ExcelUtil<VillageOfficialListResp> util = new ExcelUtil<VillageOfficialListResp>(VillageOfficialListResp.class);
        util.exportExcel(response, page.getRecords(), "村干部信息数据");
    }

    /**
     * 获取村干部信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageOfficial:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(villageOfficialService.selectVillageOfficialFormById(id));
    }

    /**
     * 根据身份证号获取表单数据（智能填充基础信息）
     */
    @GetMapping("/getFormDataByIdCardNo")
    public AjaxResult getFormDataByIdCardNo(@RequestParam String idCardNo)
    {
        return AjaxResult.success(villageOfficialService.getFormDataByIdCardNo(idCardNo));
    }

    /**
     * 新增村干部信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageOfficial:add')")
    @Log(title = "村干部信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody VillageOfficialFormDto formDto)
    {
        return toAjax(villageOfficialService.insertVillageOfficial(formDto));
    }

    /**
     * 修改村干部信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageOfficial:edit')")
    @Log(title = "村干部信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody VillageOfficialFormDto formDto)
    {
        return toAjax(villageOfficialService.updateVillageOfficial(formDto));
    }

    /**
     * 删除村干部信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageOfficial:remove')")
    @Log(title = "村干部信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(villageOfficialService.deleteVillageOfficialByIds(ids));
    }

    /**
     * 下载模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<VillageOfficialFormDto> util = new ExcelUtil<VillageOfficialFormDto>(VillageOfficialFormDto.class);
        util.importTemplateExcel(response, "村干部信息数据");
    }

    /**
     * 导入数据
     */
    @PreAuthorize("@ss.hasPermi('shebao:villageOfficial:import')")
    @Log(title = "村干部信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<VillageOfficialFormDto> util = new ExcelUtil<VillageOfficialFormDto>(VillageOfficialFormDto.class);
        List<VillageOfficialFormDto> villageOfficialList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = villageOfficialService.importVillageOfficial(villageOfficialList, updateSupport, operName);
        return AjaxResult.success(message);
    }
}
