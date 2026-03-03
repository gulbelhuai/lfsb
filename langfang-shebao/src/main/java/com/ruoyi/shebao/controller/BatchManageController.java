package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.domain.DistributionBatch;
import com.ruoyi.shebao.dto.DistributionBatchListReq;
import com.ruoyi.shebao.service.IDistributionBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 批次管理Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/payment/batch")
public class BatchManageController extends BaseController
{
    @Autowired
    private IDistributionBatchService distributionBatchService;

    /**
     * 查询批次列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:batch:list')")
    @GetMapping("/list")
    public TableDataInfo list(DistributionBatchListReq req)
    {
        // 设置分页参数默认值
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }
        Page<DistributionBatch> page = distributionBatchService.selectDistributionBatchList(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    /**
     * 获取批次详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:batch:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(distributionBatchService.selectDistributionBatchById(id));
    }

    /**
     * 新增批次
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:batch:add')")
    @Log(title = "批次管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody DistributionBatch distributionBatch)
    {
        return toAjax(distributionBatchService.insertDistributionBatch(distributionBatch));
    }

    /**
     * 修改批次
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:batch:edit')")
    @Log(title = "批次管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody DistributionBatch distributionBatch)
    {
        return toAjax(distributionBatchService.updateDistributionBatch(distributionBatch));
    }

    /**
     * 删除批次
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:batch:remove')")
    @Log(title = "批次管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(distributionBatchService.deleteDistributionBatchByIds(ids));
    }

    /**
     * 提交审核
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:batch:submit')")
    @Log(title = "批次管理", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{id}")
    public AjaxResult submit(@PathVariable Long id)
    {
        return toAjax(distributionBatchService.submitForReview(id));
    }

    /**
     * 创建批次
     */
    @PostMapping("/create")
    public AjaxResult create(@RequestBody Map<String, Object> params)
    {
        DistributionBatch batch = new DistributionBatch();
        batch.setBatchType(1);
        batch.setStatus("draft");
        return toAjax(distributionBatchService.save(batch));
    }

    /**
     * 上传财务
     */
    @PostMapping("/upload/{id}")
    public AjaxResult upload(@PathVariable Long id)
    {
        return toAjax(distributionBatchService.lambdaUpdate()
            .eq(DistributionBatch::getId, id)
            .set(DistributionBatch::getStatus, "uploaded")
            .update());
    }
}
