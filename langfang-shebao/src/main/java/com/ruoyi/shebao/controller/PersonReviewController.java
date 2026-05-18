package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.dto.PersonReviewListReq;
import com.ruoyi.shebao.dto.PersonReviewListResp;
import com.ruoyi.shebao.dto.ResidentDetailInfoDto;
import com.ruoyi.shebao.service.PersonReviewService;
import com.ruoyi.shebao.service.ResidentQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 人员登记复核Controller（按补贴子记录维度）
 */
@RestController
@RequestMapping("/shebao/person/review")
public class PersonReviewController extends BaseController
{
    @Autowired
    private PersonReviewService personReviewService;

    @Autowired
    private ResidentQueryService residentQueryService;

    /**
     * 查询待复核列表（每条为一条补贴登记记录）
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:review:list')")
    @GetMapping("/list")
    public TableDataInfo list(PersonReviewListReq req)
    {
        Page<PersonReviewListResp> page = personReviewService.selectPersonReviewList(req);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 复核通过（recordId 为补贴子表主键）
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:review:approve')")
    @Log(title = "人员登记复核", businessType = BusinessType.UPDATE)
    @PostMapping("/approve/{subsidyType}/{recordId}")
    public AjaxResult approve(@PathVariable String subsidyType,
                              @PathVariable Long recordId,
                              @RequestParam(required = false) String remark)
    {
        personReviewService.approve(subsidyType, recordId, remark);
        return AjaxResult.success("复核通过");
    }

    /**
     * 复核驳回
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:review:reject')")
    @Log(title = "人员登记复核", businessType = BusinessType.UPDATE)
    @PostMapping("/reject/{subsidyType}/{recordId}")
    public AjaxResult reject(@PathVariable String subsidyType,
                             @PathVariable Long recordId,
                             @RequestParam String reason)
    {
        if (StringUtils.isBlank(reason))
        {
            return AjaxResult.error("请填写不通过原因");
        }
        personReviewService.reject(subsidyType, recordId, reason);
        return AjaxResult.success("复核驳回成功");
    }

    /**
     * 获取人员详细信息（subsidyPersonId）
     */
    @PreAuthorize("@ss.hasPermi('shebao:person:review:query')")
    @GetMapping("/{subsidyPersonId}")
    public AjaxResult getInfo(@PathVariable Long subsidyPersonId)
    {
        ResidentDetailInfoDto detailInfo = residentQueryService.getResidentDetailInfo(null, subsidyPersonId);
        if (detailInfo == null)
        {
            return AjaxResult.error("记录不存在");
        }
        return AjaxResult.success(detailInfo);
    }
}
