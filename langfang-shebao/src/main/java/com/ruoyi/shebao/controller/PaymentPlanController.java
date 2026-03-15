package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.shebao.dto.BenefitPaymentPlanGenerateReq;
import com.ruoyi.shebao.dto.PaymentPlanListReq;
import com.ruoyi.shebao.dto.SubsidyDistributionFormDto;
import com.ruoyi.shebao.dto.SubsidyDistributionListResp;
import com.ruoyi.shebao.service.SubsidyDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付计划生成Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/payment/plan")
public class PaymentPlanController extends BaseController
{
    @Autowired
    private SubsidyDistributionService subsidyDistributionService;

    /**
     * 查询支付计划列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:list')")
    @GetMapping("/list")
    public TableDataInfo list(PaymentPlanListReq req)
    {
        // 设置分页参数默认值
        if (req.getPageNum() == null) {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null) {
            req.setPageSize(10);
        }

        // 转换为 SubsidyDistributionListReq
        com.ruoyi.shebao.dto.SubsidyDistributionListReq distributionReq = new com.ruoyi.shebao.dto.SubsidyDistributionListReq();
        distributionReq.setPageNum(req.getPageNum());
        distributionReq.setPageSize(req.getPageSize());
        distributionReq.setName(req.getName());
        distributionReq.setIdCardNo(req.getIdCardNo());
        distributionReq.setSubsidyType(req.getSubsidyType());
        distributionReq.setDistributionStatus(req.getDistributionStatus());
        distributionReq.setDistributionDateStart(req.getDistributionDateStart());
        distributionReq.setDistributionDateEnd(req.getDistributionDateEnd());

        Page<SubsidyDistributionListResp> page = subsidyDistributionService.selectSubsidyDistributionList(distributionReq);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    /**
     * 获取支付计划详细信息
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(subsidyDistributionService.selectSubsidyDistributionFormById(id));
    }

    /**
     * 生成支付计划
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:generate')")
    @Log(title = "支付计划", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody Map<String, Object> params)
    {
        if (params.get("subsidyRecordId") != null)
        {
            SubsidyDistributionFormDto formDto = new SubsidyDistributionFormDto();
            formDto.setSubsidyPersonId(params.get("subsidyPersonId") == null ? null : Long.valueOf(params.get("subsidyPersonId").toString()));
            formDto.setSubsidyType(params.get("subsidyType") == null ? null : params.get("subsidyType").toString());
            formDto.setSubsidyRecordId(Long.valueOf(params.get("subsidyRecordId").toString()));
            formDto.setDistributionAmount(params.get("distributionAmount") == null ? null : new java.math.BigDecimal(params.get("distributionAmount").toString()));
            formDto.setRemark(params.get("remark") == null ? null : params.get("remark").toString());
            return toAjax(subsidyDistributionService.insertSubsidyDistribution(formDto));
        }
        String subsidyType = params.get("subsidyType") == null ? null : params.get("subsidyType").toString();
        String paymentMonth = params.get("paymentMonth") == null ? null : params.get("paymentMonth").toString();
        Long streetOfficeId = params.get("streetOfficeId") == null || "".equals(params.get("streetOfficeId").toString())
                ? null : Long.valueOf(params.get("streetOfficeId").toString());
        return toAjax(subsidyDistributionService.generatePaymentPlans(subsidyType, paymentMonth, streetOfficeId));
    }

    /**
     * 预览统计
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:list')")
    @PostMapping("/statistics")
    public AjaxResult statistics(@RequestBody Map<String, Object> params)
    {
        String subsidyType = params.get("subsidyType") == null ? null : params.get("subsidyType").toString();
        String paymentMonth = params.get("paymentMonth") == null ? null : params.get("paymentMonth").toString();
        Long streetOfficeId = params.get("streetOfficeId") == null || "".equals(params.get("streetOfficeId").toString())
                ? null : Long.valueOf(params.get("streetOfficeId").toString());
        return AjaxResult.success(subsidyDistributionService.previewPaymentStatistics(subsidyType, paymentMonth, streetOfficeId));
    }

    /**
     * 删除支付计划
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:remove')")
    @Log(title = "支付计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(subsidyDistributionService.deleteSubsidyDistributionByIds(ids));
    }

    /**
     * 审核通过
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:approve')")
    @Log(title = "支付计划审核", businessType = BusinessType.UPDATE)
    @PostMapping("/approve/{id}")
    public AjaxResult approve(@PathVariable Long id, @RequestParam(required = false) String remark)
    {
        return toAjax(subsidyDistributionService.approveDistribution(id, remark));
    }

    /**
     * 审核驳回
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:reject')")
    @Log(title = "支付计划审核", businessType = BusinessType.UPDATE)
    @PostMapping("/reject/{id}")
    public AjaxResult reject(@PathVariable Long id, @RequestParam String remark)
    {
        return toAjax(subsidyDistributionService.rejectDistribution(id, remark));
    }

    /**
     * 批量生成支付计划
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:batchGenerate')")
    @Log(title = "批量生成支付计划", businessType = BusinessType.INSERT)
    @PostMapping("/batchGenerate")
    public AjaxResult batchGenerate(@RequestParam String subsidyType, @RequestParam String month)
    {
        return toAjax(subsidyDistributionService.generatePaymentPlans(subsidyType, month, null));
    }

    /**
     * 按通知批次生成支付计划
     */
    @PreAuthorize("@ss.hasPermi('shebao:payment:plan:generate')")
    @Log(title = "按通知批次生成支付计划", businessType = BusinessType.INSERT)
    @PostMapping("/generateByNoticeBatch")
    public AjaxResult generateByNoticeBatch(@RequestBody BenefitPaymentPlanGenerateReq req)
    {
        return toAjax(subsidyDistributionService.generateFromNoticeBatch(req.getNoticeBatchNo()));
    }
}
