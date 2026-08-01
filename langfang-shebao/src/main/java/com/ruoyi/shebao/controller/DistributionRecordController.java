package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.dto.DistributionRecordExportResp;
import com.ruoyi.shebao.dto.DistributionRecordListReq;
import com.ruoyi.shebao.dto.ResidentPaymentDetailResp;
import com.ruoyi.shebao.service.DistributionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 补贴发放记录（只读）：财务审核通过的支付计划明细
 */
@RestController
@RequestMapping("/shebao/distribution/record")
public class DistributionRecordController extends BaseController
{
    @Autowired
    private DistributionRecordService distributionRecordService;

    @PreAuthorize("@ss.hasPermi('shebao:distribution:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(DistributionRecordListReq req)
    {
        Page<ResidentPaymentDetailResp> page = distributionRecordService.selectList(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    @PreAuthorize("@ss.hasPermi('shebao:distribution:record:export')")
    @Log(title = "补贴发放记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DistributionRecordListReq req)
    {
        List<DistributionRecordExportResp> list = distributionRecordService.selectExportList(req);
        ExcelUtil<DistributionRecordExportResp> util = new ExcelUtil<>(DistributionRecordExportResp.class);
        util.exportExcel(response, list, "补贴发放记录");
    }
}
