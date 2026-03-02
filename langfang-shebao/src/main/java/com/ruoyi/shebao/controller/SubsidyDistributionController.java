package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.domain.SubsidyDistribution;
import com.ruoyi.shebao.dto.AvailableSubsidyDto;
import com.ruoyi.shebao.dto.SubsidyDistributionListReq;
import com.ruoyi.shebao.dto.SubsidyDistributionListResp;
import com.ruoyi.shebao.dto.SubsidyDistributionFormDto;
import com.ruoyi.shebao.service.SubsidyDistributionService;
import com.ruoyi.shebao.service.DistributionReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 补贴发放记录Controller
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
@RestController
@RequestMapping("/shebao/subsidyDistribution")
public class SubsidyDistributionController extends BaseController
{
    @Autowired
    private SubsidyDistributionService subsidyDistributionService;

    @Autowired
    private DistributionReviewService distributionReviewService;

    /**
     * 查询补贴发放记录列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyDistribution:list')")
    @GetMapping("/list")
    public AjaxResult list(SubsidyDistributionListReq req)
    {
        Page<SubsidyDistributionListResp> page = subsidyDistributionService.selectSubsidyDistributionList(req);
        return AjaxResult.success(page);
    }

    /**
     * 导出补贴发放记录列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyDistribution:export')")
    @Log(title = "补贴发放记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SubsidyDistributionListReq req)
    {
        Page<SubsidyDistributionListResp> page = subsidyDistributionService.selectSubsidyDistributionList(req);
        ExcelUtil<SubsidyDistributionListResp> util = new ExcelUtil<SubsidyDistributionListResp>(SubsidyDistributionListResp.class);
        util.exportExcel(response, page.getRecords(), "补贴发放记录数据");
    }

    /**
     * 获取补贴发放记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyDistribution:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        SubsidyDistributionFormDto distribution = subsidyDistributionService.selectSubsidyDistributionFormById(id);
        // 获取审核记录
        Map<String, Object> result = new HashMap<>();
        result.put("distribution", distribution);
        result.put("reviewRecords", distributionReviewService.getReviewRecordsByDistributionId(id));
        return AjaxResult.success(result);
    }

    /**
     * 自动搜索居民（下拉列表）
     */
    @GetMapping("/searchResidents")
    public AjaxResult searchResidents(@RequestParam String keyword)
    {
        return AjaxResult.success(subsidyDistributionService.searchResidents(keyword));
    }

    /**
     * 根据居民ID查询可发放的补贴
     */
    @GetMapping("/getAvailableSubsidiesByPersonId")
    public AjaxResult getAvailableSubsidiesByPersonId(@RequestParam Long subsidyPersonId)
    {
        AvailableSubsidyDto result = subsidyDistributionService.getAvailableSubsidiesByPersonId(subsidyPersonId);
        return AjaxResult.success(result);
    }

    /**
     * 根据身份证号或姓名查询可发放的补贴
     */
    @GetMapping("/getAvailableSubsidies")
    public AjaxResult getAvailableSubsidies(@RequestParam String keyword)
    {
        AvailableSubsidyDto result = subsidyDistributionService.getAvailableSubsidies(keyword);
        return AjaxResult.success(result);
    }

    /**
     * 新增补贴发放记录
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyDistribution:add')")
    @Log(title = "补贴发放记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SubsidyDistributionFormDto formDto)
    {
        return toAjax(subsidyDistributionService.insertSubsidyDistribution(formDto));
    }

    /**
     * 审核通过
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyDistribution:approve')")
    @Log(title = "补贴发放审核通过", businessType = BusinessType.UPDATE)
    @PutMapping("/approve")
    public AjaxResult approve(@RequestBody Map<String, Object> params)
    {
        Long id = Long.parseLong(params.get("id").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : null;
        return toAjax(subsidyDistributionService.approveDistribution(id, remark));
    }

    /**
     * 拒绝发放
     * 根据记录状态动态检查权限：
     * - 待审核状态（1）的拒绝需要 approve 权限
     * - 待发放状态（2）的拒绝需要 distribute 权限
     */
    @Log(title = "拒绝补贴发放", businessType = BusinessType.UPDATE)
    @PutMapping("/reject")
    public AjaxResult reject(@RequestBody Map<String, Object> params)
    {
        Long id = Long.parseLong(params.get("id").toString());
        String remark = params.get("remark").toString();
        
        // 先查询记录状态
        SubsidyDistribution distribution = subsidyDistributionService.getById(id);
        if (distribution == null) {
            throw new ServiceException("发放记录不存在");
        }
        
        // 根据状态检查权限
        String distributionStatus = distribution.getDistributionStatus();
        if ("1".equals(distributionStatus)) {
            // 待审核状态，需要 approve 权限
            if (!SecurityUtils.hasPermi("shebao:subsidyDistribution:approve")) {
                throw new ServiceException("没有权限执行此操作");
            }
        } else if ("2".equals(distributionStatus)) {
            // 待发放状态，需要 distribute 权限
            if (!SecurityUtils.hasPermi("shebao:subsidyDistribution:distribute")) {
                throw new ServiceException("没有权限执行此操作");
            }
        } else {
            throw new ServiceException("只有待审核或待发放状态的记录才能拒绝");
        }
        
        return toAjax(subsidyDistributionService.rejectDistribution(id, remark));
    }

    /**
     * 发放补贴
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyDistribution:distribute')")
    @Log(title = "补贴发放", businessType = BusinessType.UPDATE)
    @PutMapping("/distribute")
    public AjaxResult distribute(@RequestBody Map<String, Object> params)
    {
        Long id = Long.parseLong(params.get("id").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : null;
        return toAjax(subsidyDistributionService.distributeSubsidy(id, remark));
    }

    /**
     * 重新提交
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyDistribution:resubmit')")
    @Log(title = "补贴发放重新提交", businessType = BusinessType.UPDATE)
    @PutMapping("/resubmit/{id}")
    public AjaxResult resubmit(@PathVariable Long id)
    {
        return toAjax(subsidyDistributionService.resubmitDistribution(id));
    }

    /**
     * 删除补贴发放记录
     */
    @PreAuthorize("@ss.hasPermi('shebao:subsidyDistribution:remove')")
    @Log(title = "补贴发放记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(subsidyDistributionService.deleteSubsidyDistributionByIds(ids));
    }

    /**
     * 查询居民最近的发放记录
     */
    @GetMapping("/recent/{subsidyPersonId}")
    public AjaxResult getRecentDistributions(@PathVariable Long subsidyPersonId, @RequestParam(required = false, defaultValue = "5") Integer limit)
    {
        return AjaxResult.success(subsidyDistributionService.selectRecentDistributions(subsidyPersonId, limit));
    }
}

