package com.ruoyi.shebao.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.domain.BenefitNoticeBatch;
import com.ruoyi.shebao.dto.BenefitNoticeBatchListReq;
import com.ruoyi.shebao.dto.BenefitNoticeBatchResp;
import com.ruoyi.shebao.dto.BenefitNoticeDetailListReq;
import com.ruoyi.shebao.dto.BenefitNoticeExportRow;
import com.ruoyi.shebao.dto.BenefitNoticeGenerateReq;
import com.ruoyi.shebao.service.IBenefitNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/shebao/benefit/notice")
public class BenefitNoticeController extends BaseController
{
    @Autowired
    private IBenefitNoticeService benefitNoticeService;

    @PreAuthorize("@ss.hasPermi('shebao:benefit:notice:list')")
    @GetMapping("/batch/list")
    public TableDataInfo batchList(BenefitNoticeBatchListReq req)
    {
        if (req.getPageNum() == null)
        {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null)
        {
            req.setPageSize(10);
        }
        var page = benefitNoticeService.selectBatchPage(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    @PreAuthorize("@ss.hasPermi('shebao:benefit:notice:list')")
    @GetMapping("/detail/list")
    public TableDataInfo detailList(BenefitNoticeDetailListReq req)
    {
        if (req.getPageNum() == null)
        {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null)
        {
            req.setPageSize(500);
        }
        var list = benefitNoticeService.selectDetailList(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(list);
        rsp.setTotal(list.size());
        return rsp;
    }

    @PreAuthorize("@ss.hasPermi('shebao:benefit:notice:list')")
    @GetMapping("/batch/{batchNo}")
    public AjaxResult getBatch(@PathVariable String batchNo)
    {
        BenefitNoticeBatchResp resp = benefitNoticeService.selectBatchByBatchNo(batchNo);
        return AjaxResult.success(resp);
    }

    @PreAuthorize("@ss.hasPermi('shebao:benefit:notice:generate')")
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody BenefitNoticeGenerateReq params)
    {
        BenefitNoticeBatch batch = benefitNoticeService.generateNoticeBatch(params);
        return AjaxResult.success("预到龄通知批次生成成功", batch);
    }

    @PreAuthorize("@ss.hasPermi('shebao:benefit:notice:list')")
    @PostMapping("/export")
    public void export(@RequestParam(required = false) String noticeMonth,
                       @RequestParam(required = false) String batchNo,
                       HttpServletResponse response)
    {
        ExcelUtil<BenefitNoticeExportRow> util = new ExcelUtil<>(BenefitNoticeExportRow.class);
        util.exportExcel(response, benefitNoticeService.selectExportRows(noticeMonth, batchNo), "预到龄通知清单");
    }
}
