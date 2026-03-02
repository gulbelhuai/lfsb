package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.SubsidyPersonCancelReq;
import com.ruoyi.shebao.dto.SubsidyPersonListReq;
import com.ruoyi.shebao.dto.SubsidyPersonListResp;
import com.ruoyi.shebao.service.SubsidyPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 被补贴人基础信息Controller
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@RestController
@RequestMapping("/shebao/subsidyPerson")
public class SubsidyPersonController extends BaseController
{
    @Autowired
    private SubsidyPersonService subsidyPersonService;

    /**
     * 查询被补贴人基础信息列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyPerson:list')")
    @GetMapping("/list")
    public AjaxResult list(SubsidyPersonListReq req)
    {
        Page<SubsidyPersonListResp> page = subsidyPersonService.selectSubsidyPersonList(req);
        return AjaxResult.success(page);
    }

    /**
     * 导出被补贴人基础信息列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyPerson:export')")
    @Log(title = "被补贴人基础信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SubsidyPersonListReq req)
    {
        Page<SubsidyPersonListResp> page = subsidyPersonService.selectSubsidyPersonList(req);
        ExcelUtil<SubsidyPersonListResp> util = new ExcelUtil<SubsidyPersonListResp>(SubsidyPersonListResp.class);
        util.exportExcel(response, page.getRecords(), "被补贴人基础信息数据");
    }

    /**
     * 获取被补贴人基础信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyPerson:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(subsidyPersonService.selectSubsidyPersonById(id));
    }

    /**
     * 新增被补贴人基础信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyPerson:add')")
    @Log(title = "被补贴人基础信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SubsidyPerson subsidyPerson)
    {
        if (!"0".equals(subsidyPersonService.checkIdCardNoUnique(subsidyPerson)))
        {
            return AjaxResult.error("新增被补贴人'" + subsidyPerson.getName() + "'失败，身份证号已存在");
        }
        return toAjax(subsidyPersonService.insertSubsidyPerson(subsidyPerson));
    }

    /**
     * 修改被补贴人基础信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyPerson:edit')")
    @Log(title = "被补贴人基础信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SubsidyPerson subsidyPerson)
    {
        if (!"0".equals(subsidyPersonService.checkIdCardNoUnique(subsidyPerson)))
        {
            return AjaxResult.error("修改被补贴人'" + subsidyPerson.getName() + "'失败，身份证号已存在");
        }
        return toAjax(subsidyPersonService.updateSubsidyPerson(subsidyPerson));
    }

    /**
     * 删除被补贴人基础信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyPerson:remove')")
    @Log(title = "被补贴人基础信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(subsidyPersonService.deleteSubsidyPersonByIds(ids));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyPerson:edit')")
    @Log(title = "被补贴人基础信息", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SubsidyPerson subsidyPerson)
    {
        return toAjax(subsidyPersonService.updateSubsidyPerson(subsidyPerson));
    }

    /**
     * 校验身份证号
     */
    @GetMapping("/checkIdCardNoUnique")
    public AjaxResult checkIdCardNoUnique(SubsidyPerson subsidyPerson)
    {
        return AjaxResult.success(subsidyPersonService.checkIdCardNoUnique(subsidyPerson));
    }

    /**
     * 下载模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<SubsidyPerson> util = new ExcelUtil<SubsidyPerson>(SubsidyPerson.class);
        util.importTemplateExcel(response, "被补贴人基础信息数据");
    }

    /**
     * 导入数据
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyPerson:import')")
    @Log(title = "被补贴人基础信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SubsidyPerson> util = new ExcelUtil<SubsidyPerson>(SubsidyPerson.class);
        List<SubsidyPerson> subsidyPersonList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = subsidyPersonService.importSubsidyPerson(subsidyPersonList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    /**
     * 根据身份证号查询被补贴人基础信息
     * 
     * @param idCardNo 身份证号
     * @param includeCancel 是否包含注销人员(默认false,只查健在人员)
     */
    @GetMapping("/getByIdCardNo")
    public AjaxResult getByIdCardNo(
            @RequestParam String idCardNo,
            @RequestParam(required = false, defaultValue = "false") Boolean includeCancel)
    {
        SubsidyPerson subsidyPerson;
        if (includeCancel) {
            // 查询所有人(包括注销人员),用于综合查询/追溯
            subsidyPerson = subsidyPersonService.selectSubsidyPersonByIdCardNo(idCardNo);
        } else {
            // 默认只查健在人员,用于业务办理场景
            subsidyPerson = subsidyPersonService.selectAliveSubsidyPersonByIdCardNo(idCardNo);
        }
        return AjaxResult.success(subsidyPerson);
    }

    /**
     * 人员注销登记（标记死亡）
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyPerson:cancel')")
    @Log(title = "人员注销登记", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel")
    public AjaxResult cancel(@RequestBody SubsidyPersonCancelReq req)
    {
        return toAjax(subsidyPersonService.cancelByIdCardNo(req));
    }
}
