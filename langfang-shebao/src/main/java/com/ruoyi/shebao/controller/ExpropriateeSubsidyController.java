package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyListReq;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyListResp;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyFormDto;
import com.ruoyi.shebao.service.ExpropriateeSubsidyService;
import com.ruoyi.shebao.service.SubsidyCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 被征地参保补贴Controller
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@RestController
@RequestMapping("/shebao/expropriateeSubsidy")
public class ExpropriateeSubsidyController extends BaseController
{
    @Autowired
    private ExpropriateeSubsidyService expropriateeSubsidyService;

    @Autowired
    private SubsidyCalculationService subsidyCalculationService;

    /**
     * 查询被征地参保补贴列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:expropriateeSubsidy:list')")
    @GetMapping("/list")
    public AjaxResult list(ExpropriateeSubsidyListReq req)
    {
        Page<ExpropriateeSubsidyListResp> page = expropriateeSubsidyService.selectExpropriateeSubsidyList(req);
        return AjaxResult.success(page);
    }

    /**
     * 导出被征地参保补贴列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:expropriateeSubsidy:export')")
    @Log(title = "被征地参保补贴", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ExpropriateeSubsidyListReq req)
    {
        Page<ExpropriateeSubsidyListResp> page = expropriateeSubsidyService.selectExpropriateeSubsidyList(req);
        ExcelUtil<ExpropriateeSubsidyListResp> util = new ExcelUtil<ExpropriateeSubsidyListResp>(ExpropriateeSubsidyListResp.class);
        util.exportExcel(response, page.getRecords(), "被征地参保补贴数据");
    }

    /**
     * 获取被征地参保补贴详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:expropriateeSubsidy:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(expropriateeSubsidyService.selectExpropriateeSubsidyFormById(id));
    }

    /**
     * 根据身份证号获取表单数据（智能填充基础信息）
     */
    @GetMapping("/getFormDataByIdCardNo")
    public AjaxResult getFormDataByIdCardNo(@RequestParam String idCardNo)
    {
        return AjaxResult.success(expropriateeSubsidyService.getFormDataByIdCardNo(idCardNo));
    }

    /**
     * 计算补贴数据（前端实时计算用）
     */
    @PostMapping("/calculateSubsidy")
    public AjaxResult calculateSubsidy(@RequestBody Map<String, Object> params)
    {
        try
        {
            LocalDate birthday = null;
            LocalDate baseDate = null;
            
            if (params.get("birthday") != null)
            {
                birthday = LocalDate.parse(params.get("birthday").toString());
            }
            if (params.get("baseDate") != null)
            {
                baseDate = LocalDate.parse(params.get("baseDate").toString());
            }
            
            Map<String, Object> result = new HashMap<>();
            
            if (birthday != null && baseDate != null)
            {
                // 计算年龄
                Integer ageAtBaseDate = subsidyCalculationService.calculateAgeAtBaseDate(birthday, baseDate);
                result.put("ageAtBaseDate", ageAtBaseDate);
                
                // 计算补贴年限
                BigDecimal subsidyYears = subsidyCalculationService.calculateSubsidyYears(ageAtBaseDate);
                result.put("subsidyYears", subsidyYears);
                
                // 计算补贴金额
                BigDecimal subsidyAmount = subsidyCalculationService.calculateSubsidyAmount(ageAtBaseDate, subsidyYears);
                result.put("subsidyAmount", subsidyAmount);
            }
            else
            {
                result.put("ageAtBaseDate", null);
                result.put("subsidyYears", BigDecimal.ZERO);
                result.put("subsidyAmount", BigDecimal.ZERO);
            }
            
            return AjaxResult.success(result);
        }
        catch (Exception e)
        {
            return AjaxResult.error("计算失败：" + e.getMessage());
        }
    }

    /**
     * 新增被征地参保补贴
     */
    @PreAuthorize("@ss.hasPermi('shebao:expropriateeSubsidy:add')")
    @Log(title = "被征地参保补贴", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ExpropriateeSubsidyFormDto formDto)
    {
        return toAjax(expropriateeSubsidyService.insertExpropriateeSubsidy(formDto));
    }

    /**
     * 修改被征地参保补贴
     */
    @PreAuthorize("@ss.hasPermi('shebao:expropriateeSubsidy:edit')")
    @Log(title = "被征地参保补贴", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ExpropriateeSubsidyFormDto formDto)
    {
        return toAjax(expropriateeSubsidyService.updateExpropriateeSubsidy(formDto));
    }

    /**
     * 删除被征地参保补贴
     */
    @PreAuthorize("@ss.hasPermi('shebao:expropriateeSubsidy:remove')")
    @Log(title = "被征地参保补贴", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(expropriateeSubsidyService.deleteExpropriateeSubsidyByIds(ids));
    }

    /**
     * 下载模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<ExpropriateeSubsidyFormDto> util = new ExcelUtil<ExpropriateeSubsidyFormDto>(ExpropriateeSubsidyFormDto.class);
        util.importTemplateExcel(response, "被征地参保补贴数据");
    }

    /**
     * 导入数据
     */
    @PreAuthorize("@ss.hasPermi('shebao:expropriateeSubsidy:import')")
    @Log(title = "被征地参保补贴", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<ExpropriateeSubsidyFormDto> util = new ExcelUtil<ExpropriateeSubsidyFormDto>(ExpropriateeSubsidyFormDto.class);
        List<ExpropriateeSubsidyFormDto> expropriateeSubsidyList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = expropriateeSubsidyService.importExpropriateeSubsidy(expropriateeSubsidyList, updateSupport, operName);
        return AjaxResult.success(message);
    }
}
