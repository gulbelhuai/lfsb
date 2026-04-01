package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyFormDto;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyListReq;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyListResp;
import com.ruoyi.shebao.service.PersonKeyInfoModifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 人员信息变更：basic（两级：复核通过即回写）、key（三级：复核→审批后回写含姓名/身份证）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/shebao/person/modify")
public class PersonModifyController extends BaseController {

    @Autowired
    private PersonKeyInfoModifyService personKeyInfoModifyService;

    /**
     * 查询人员信息变更申请列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:modify:list')")
    @GetMapping("/list")
    public AjaxResult list(PersonKeyInfoModifyListReq req) {
        if (req.getPageNum() == null) req.setPageNum(1);
        if (req.getPageSize() == null) req.setPageSize(10);
        Page<PersonKeyInfoModifyListResp> page = personKeyInfoModifyService.selectModifyList(req);
        return AjaxResult.success(page);
    }

    /**
     * 是否存在未办结的人员信息变更（用于按身份证拉取人员前校验）
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:modify:add')")
    @GetMapping("/checkUnfinished")
    public AjaxResult checkUnfinished(@RequestParam Long subsidyPersonId,
                                      @RequestParam(required = false) Long excludeModifyId) {
        boolean blocked = personKeyInfoModifyService.hasUnfinishedModifyForPerson(subsidyPersonId, excludeModifyId);
        return AjaxResult.success(blocked);
    }

    /**
     * 获取人员信息变更申请详情
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:modify:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return AjaxResult.success(personKeyInfoModifyService.selectFormById(id));
    }

    /**
     * 新增/保存草稿（经办人）
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:modify:add')")
    @Log(title = "人员信息变更", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PersonKeyInfoModifyFormDto form) {
        long id = personKeyInfoModifyService.saveOrUpdateDraft(form);
        return AjaxResult.success(id);
    }

    /**
     * 修改草稿（经办人）
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:modify:edit')")
    @Log(title = "人员信息变更", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PersonKeyInfoModifyFormDto form) {
        personKeyInfoModifyService.saveOrUpdateDraft(form);
        return AjaxResult.success();
    }

    /**
     * 提交：草稿 -> 待复核（经办人）
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:modify:submit')")
    @Log(title = "人员信息变更提交", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{id}")
    public AjaxResult submit(@PathVariable Long id) {
        return toAjax(personKeyInfoModifyService.submit(id));
    }

    /**
     * 复核：basic 通过即已通过；key 通过 -> 待审批；不通过 -> 已驳回
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:modify:review')")
    @Log(title = "人员信息变更复核", businessType = BusinessType.UPDATE)
    @PostMapping("/review/{id}")
    public AjaxResult review(@PathVariable Long id, @RequestParam Boolean approved, @RequestParam(required = false) String remark) {
        return toAjax(personKeyInfoModifyService.review(id, approved, remark));
    }

    /**
     * 审批（仅关键信息）：待审批 -> 已通过；驳回 -> 已驳回
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:modify:approve')")
    @Log(title = "人员信息变更审批", businessType = BusinessType.UPDATE)
    @PostMapping("/approve/{id}")
    public AjaxResult approve(@PathVariable Long id, @RequestParam Boolean approved, @RequestParam(required = false) String remark) {
        return toAjax(personKeyInfoModifyService.approve(id, approved, remark));
    }

    /**
     * 驳回（兼容旧前端）
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:modify:reject')")
    @Log(title = "人员信息变更驳回", businessType = BusinessType.UPDATE)
    @PostMapping("/reject/{id}")
    public AjaxResult reject(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return toAjax(personKeyInfoModifyService.review(id, false, reason != null ? reason : "驳回"));
    }
}
