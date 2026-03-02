package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.TeacherSubsidyFormDto;
import com.ruoyi.shebao.dto.TeacherSubsidyListReq;
import com.ruoyi.shebao.dto.TeacherSubsidyListResp;
import com.ruoyi.shebao.service.TeacherSubsidyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 教龄补助登记Controller（人员信息管理 → 教龄补助登记）
 *
 * 说明：
 * - 权限标识遵循概要设计文档：shebao:person:teacher:*
 * - 基础信息与业务信息分表：基础信息在 shebao_subsidy_person；教龄补助信息在 shebao_teacher_subsidy
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@RestController
@RequestMapping("/shebao/teacherSubsidy")
public class TeacherSubsidyController extends BaseController
{
    @Autowired
    private TeacherSubsidyService teacherSubsidyService;

    /**
     * 查询教龄补助登记列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:teacher:list')")
    @GetMapping("/list")
    public AjaxResult list(TeacherSubsidyListReq req)
    {
        Page<TeacherSubsidyListResp> page = teacherSubsidyService.selectTeacherSubsidyList(req);
        return AjaxResult.success(page);
    }

    /**
     * 获取教龄补助登记详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:teacher:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(teacherSubsidyService.selectTeacherSubsidyFormById(id));
    }

    /**
     * 根据身份证号获取表单数据（智能填充基础信息）
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:teacher:query')")
    @GetMapping("/getFormDataByIdCardNo")
    public AjaxResult getFormDataByIdCardNo(@RequestParam String idCardNo)
    {
        return AjaxResult.success(teacherSubsidyService.getFormDataByIdCardNo(idCardNo));
    }

    /**
     * 新增教龄补助登记
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:teacher:add')")
    @Log(title = "教龄补助登记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody TeacherSubsidyFormDto formDto)
    {
        return toAjax(teacherSubsidyService.insertTeacherSubsidy(formDto));
    }

    /**
     * 修改教龄补助登记
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:teacher:edit')")
    @Log(title = "教龄补助登记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody TeacherSubsidyFormDto formDto)
    {
        return toAjax(teacherSubsidyService.updateTeacherSubsidy(formDto));
    }

    /**
     * 删除教龄补助登记
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:teacher:remove')")
    @Log(title = "教龄补助登记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(teacherSubsidyService.deleteTeacherSubsidyByIds(ids));
    }

    /**
     * 提交审核（预留：符合概要设计“人员登记2级审核”流程）
     *
     * 当前版本仅返回成功，后续可接入 approval_log 状态机。
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:teacher:submit')")
    @Log(title = "教龄补助登记", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{id}")
    public AjaxResult submit(@PathVariable("id") Long id, @RequestBody(required = false) String remark)
    {
        return AjaxResult.success("提交成功");
    }
}

