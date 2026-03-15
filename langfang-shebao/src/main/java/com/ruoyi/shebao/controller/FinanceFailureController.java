package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.SubsidyDistributionListReq;
import com.ruoyi.shebao.dto.SubsidyDistributionListResp;
import com.ruoyi.shebao.service.SubsidyDistributionService;
import com.ruoyi.shebao.service.IDistributionBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 失败处理Controller
 *
 * @author ruoyi
 * @date 2025-01-20
 */
@RestController
@RequestMapping("/shebao/finance/failure")
public class FinanceFailureController extends BaseController
{
    @Autowired
    private SubsidyDistributionService subsidyDistributionService;

    @Autowired
    private IDistributionBatchService distributionBatchService;

    /**
     * 查询失败记录列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:failure:list')")
    @GetMapping("/list")
    public TableDataInfo list(SubsidyDistributionListReq req)
    {
        // 设置分页参数默认值
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        // 默认查询失败状态
        if (req.getApprovalStatus() == null || req.getApprovalStatus().isEmpty()) {
            req.setApprovalStatus("failed");
        }
        Page<SubsidyDistributionListResp> page = subsidyDistributionService.selectSubsidyDistributionList(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    /**
     * 获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:failure:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(subsidyDistributionService.selectSubsidyDistributionFormById(id));
    }

    /**
     * 重新发放
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:failure:retry')")
    @Log(title = "失败重发", businessType = BusinessType.UPDATE)
    @PostMapping("/retry/{id}")
    public AjaxResult retry(@PathVariable Long id)
    {
        return handle(Map.of("distributionId", id, "handleType", "retry", "remark", "重新发放"));
    }

    /**
     * 批量重新发放
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:failure:batchRetry')")
    @Log(title = "批量失败重发", businessType = BusinessType.UPDATE)
    @PostMapping("/batchRetry")
    public AjaxResult batchRetry(@RequestBody Long[] ids)
    {
        int count = 0;
        for (Long id : ids)
        {
            handle(Map.of("distributionId", id, "handleType", "retry", "remark", "批量重新发放"));
            count++;
        }
        return AjaxResult.success("已提交批量重新发放，共" + count + "条");
    }

    /**
     * 标记为人工处理
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:failure:manual')")
    @Log(title = "标记人工处理", businessType = BusinessType.UPDATE)
    @PostMapping("/manual/{id}")
    public AjaxResult markAsManual(@PathVariable Long id, @RequestParam String remark)
    {
        return handle(Map.of("distributionId", id, "handleType", "manual", "remark", remark));
    }

    /**
     * 失败处理统一入口
     */
    @PreAuthorize("@ss.hasPermi('shebao:finance:failure:retry')")
    @Log(title = "失败处理", businessType = BusinessType.UPDATE)
    @PostMapping("/handle")
    public AjaxResult handle(@RequestBody Map<String, Object> params)
    {
        Long distributionId = Long.valueOf(params.get("distributionId").toString());
        String handleType = params.get("handleType").toString();
        String remark = params.get("remark") == null ? "" : params.get("remark").toString();
        var distribution = subsidyDistributionService.getById(distributionId);
        if (distribution == null)
        {
            return AjaxResult.error("失败记录不存在");
        }
        if ("correct".equals(handleType))
        {
            distribution.setReviewRemark("已更正：" + remark + "；新账号：" + params.getOrDefault("bankAccountNo", ""));
            distribution.setUpdateBy(getUsername());
            distribution.setUpdateTime(LocalDateTime.now());
            subsidyDistributionService.updateById(distribution);
            return AjaxResult.success("信息更正成功");
        }
        if ("manual".equals(handleType))
        {
            distribution.setApprovalStatus("manual_closed");
            distribution.setReviewRemark(remark);
            distribution.setUpdateBy(getUsername());
            distribution.setUpdateTime(LocalDateTime.now());
            subsidyDistributionService.updateById(distribution);
            return AjaxResult.success("已标记为人工处理");
        }
        if ("retry".equals(handleType))
        {
            String nextBatchType = "first".equals(distribution.getBatchType()) || distribution.getBatchType() == null ? "second" : "third";
            com.ruoyi.shebao.domain.DistributionBatch batch = new com.ruoyi.shebao.domain.DistributionBatch();
            String batchNo = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
                    + "-" + String.format("%03d", System.currentTimeMillis() % 1000);
            batch.setBatchNo(batchNo);
            batch.setBatchType(nextBatchType);
            batch.setSubsidyType(distribution.getSubsidyType());
            batch.setDistributionYear(java.time.LocalDate.now().getYear());
            batch.setDistributionMonth(java.time.LocalDate.now().getMonthValue());
            batch.setTotalCount(1);
            batch.setTotalAmount(distribution.getDistributionAmount());
            batch.setSuccessCount(0);
            batch.setSuccessAmount(java.math.BigDecimal.ZERO);
            batch.setFailCount(0);
            batch.setFailAmount(java.math.BigDecimal.ZERO);
            batch.setStatus("pending_review");
            batch.setDelFlag("0");
            batch.setCreateBy(getUsername());
            batch.setCreateTime(new java.util.Date());
            distributionBatchService.save(batch);

            com.ruoyi.shebao.domain.SubsidyDistribution retryDistribution = new com.ruoyi.shebao.domain.SubsidyDistribution();
            retryDistribution.setBatchNo(batchNo);
            retryDistribution.setBatchType(nextBatchType);
            retryDistribution.setApprovalStatus("pending_review");
            retryDistribution.setSubsidyPersonId(distribution.getSubsidyPersonId());
            retryDistribution.setSubsidyType(distribution.getSubsidyType());
            retryDistribution.setSubsidyRecordId(distribution.getSubsidyRecordId());
            retryDistribution.setDistributionAmount(distribution.getDistributionAmount());
            retryDistribution.setDistributionDate(distribution.getDistributionDate());
            retryDistribution.setDistributionStatus("1");
            retryDistribution.setStatus("0");
            retryDistribution.setRemark("来源失败重发记录：" + distributionId);
            retryDistribution.setCreateBy(getUsername());
            retryDistribution.setCreateTime(LocalDateTime.now());
            subsidyDistributionService.save(retryDistribution);
            return AjaxResult.success("已创建重发批次");
        }
        return AjaxResult.error("不支持的失败处理类型");
    }
}
