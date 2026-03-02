package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.domain.AdministrativeDivision;
import com.ruoyi.shebao.domain.Village;
import com.ruoyi.shebao.dto.VillageListReq;
import com.ruoyi.shebao.dto.VillageListResp;
import com.ruoyi.shebao.service.AdministrativeDivisionService;
import com.ruoyi.shebao.service.VillageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 村级单位Controller
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@RestController
@RequestMapping("/shebao/village")
public class VillageController extends BaseController
{
    @Autowired
    private VillageService villageService;

    @Autowired
    private AdministrativeDivisionService administrativeDivisionService;

    /**
     * 查询村级单位列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:village:list')")
    @GetMapping("/list")
    public AjaxResult list(VillageListReq req)
    {
        Page<VillageListResp> page = villageService.selectVillageList(req);
        return AjaxResult.success(page);
    }

    /**
     * 导出村级单位列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:village:export')")
    @Log(title = "村级单位", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, VillageListReq req)
    {
        Page<VillageListResp> page = villageService.selectVillageList(req);
        ExcelUtil<VillageListResp> util = new ExcelUtil<VillageListResp>(VillageListResp.class);
        util.exportExcel(response, page.getRecords(), "村级单位数据");
    }

    /**
     * 获取村级单位详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:village:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(villageService.selectVillageById(id));
    }

    /**
     * 新增村级单位
     */
    @PreAuthorize("@ss.hasPermi('shebao:village:add')")
    @Log(title = "村级单位", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Village village)
    {
        if (!"0".equals(villageService.checkVillageCodeUnique(village)))
        {
            return AjaxResult.error("新增村级单位'" + village.getVillageName() + "'失败，村编码已存在");
        }
        return toAjax(villageService.insertVillage(village));
    }

    /**
     * 修改村级单位
     */
    @PreAuthorize("@ss.hasPermi('shebao:village:edit')")
    @Log(title = "村级单位", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody Village village)
    {
        if (!"0".equals(villageService.checkVillageCodeUnique(village)))
        {
            return AjaxResult.error("修改村级单位'" + village.getVillageName() + "'失败，村编码已存在");
        }
        return toAjax(villageService.updateVillage(village));
    }

    /**
     * 删除村级单位
     */
    @PreAuthorize("@ss.hasPermi('shebao:village:remove')")
    @Log(title = "村级单位", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(villageService.deleteVillageByIds(ids));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('shebao:village:edit')")
    @Log(title = "村级单位", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody Village village)
    {
        return toAjax(villageService.updateVillage(village));
    }

    /**
     * 获取村级单位选择框列表
     */
    @GetMapping("/optionSelect")
    public AjaxResult optionSelect()
    {
        VillageListReq req = new VillageListReq();
        req.setStatus("0");
        Page<VillageListResp> page = villageService.selectVillageList(req);
        return AjaxResult.success(page.getRecords());
    }

    /**
     * 下载模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<Village> util = new ExcelUtil<Village>(Village.class);
        util.importTemplateExcel(response, "村级单位数据");
    }

    /**
     * 导入数据
     */
    @PreAuthorize("@ss.hasPermi('shebao:village:import')")
    @Log(title = "村级单位", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<Village> util = new ExcelUtil<Village>(Village.class);
        List<Village> villageList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = villageService.importVillage(villageList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    /**
     * 获取省级行政区划列表
     */
    @GetMapping("/provinces")
    public AjaxResult getProvinceList()
    {
        List<AdministrativeDivision> provinces = administrativeDivisionService.getProvinceList();
        return AjaxResult.success(provinces);
    }

    /**
     * 获取市级行政区划列表
     */
    @GetMapping("/cities/{provinceCode}")
    public AjaxResult getCityList(@PathVariable("provinceCode") String provinceCode)
    {
        List<AdministrativeDivision> cities = administrativeDivisionService.getCityList(provinceCode);
        return AjaxResult.success(cities);
    }

    /**
     * 获取县级行政区划列表
     */
    @GetMapping("/counties/{cityCode}")
    public AjaxResult getCountyList(@PathVariable("cityCode") String cityCode)
    {
        List<AdministrativeDivision> counties = administrativeDivisionService.getCountyList(cityCode);
        return AjaxResult.success(counties);
    }

    /**
     * 获取乡镇级行政区划列表
     */
    @GetMapping("/townships/{countyCode}")
    public AjaxResult getTownshipList(@PathVariable("countyCode") String countyCode)
    {
        if ("null".equals(countyCode) || "undefined".equals(countyCode) || countyCode.trim().isEmpty())
        {
            return AjaxResult.success(new ArrayList<>());
        }
        List<AdministrativeDivision> townships = administrativeDivisionService.getTownshipList(countyCode);
        return AjaxResult.success(townships);
    }

    /**
     * 获取所有乡镇级行政区划列表（用于查询筛选）
     */
    @GetMapping("/townships")
    public AjaxResult getAllTownshipList()
    {
        List<AdministrativeDivision> townships = administrativeDivisionService.selectByDivisionLevel(4);
        return AjaxResult.success(townships);
    }

    /**
     * 根据父级编码获取下级行政区划
     */
    @GetMapping("/children/{parentCode}")
    public AjaxResult getChildrenByParentCode(@PathVariable("parentCode") String parentCode)
    {
        List<AdministrativeDivision> children = administrativeDivisionService.selectByParentCode(parentCode);
        return AjaxResult.success(children);
    }

    /**
     * 根据编码获取行政区划信息
     */
    @GetMapping("/division/{divisionCode}")
    public AjaxResult getDivisionByCode(@PathVariable("divisionCode") String divisionCode)
    {
        AdministrativeDivision division = administrativeDivisionService.selectByDivisionCode(divisionCode);
        return AjaxResult.success(division);
    }
}
