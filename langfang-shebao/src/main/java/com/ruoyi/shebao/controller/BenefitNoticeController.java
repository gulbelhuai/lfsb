package com.ruoyi.shebao.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.dto.BenefitNoticeBatchListReq;
import com.ruoyi.shebao.dto.BenefitNoticeExportRow;
import com.ruoyi.shebao.service.IBenefitNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import com.ruoyi.common.utils.StringUtils;

@RestController
@RequestMapping("/shebao/benefit/notice")
public class BenefitNoticeController extends BaseController
{
    @Autowired
    private IBenefitNoticeService benefitNoticeService;

    @PreAuthorize("@ss.hasPermi('shebao:benefit:notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(BenefitNoticeBatchListReq req)
    {
        validateQuery(req);
        if (req.getPageNum() == null)
        {
            req.setPageNum(1);
        }
        if (req.getPageSize() == null)
        {
            req.setPageSize(10);
        }
        var page = benefitNoticeService.selectPage(req);
        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(200);
        rsp.setRows(page.getRecords());
        rsp.setTotal(page.getTotal());
        return rsp;
    }

    @PreAuthorize("@ss.hasPermi('shebao:benefit:notice:list')")
    @PostMapping("/export")
    public void export(BenefitNoticeBatchListReq req,
                       HttpServletResponse response)
    {
        validateQuery(req);
        ExcelUtil<BenefitNoticeExportRow> util = new ExcelUtil<>(BenefitNoticeExportRow.class);
        util.exportExcel(response, benefitNoticeService.selectExportRows(req), "预到龄通知清单");
    }

    private void validateQuery(BenefitNoticeBatchListReq req)
    {
        boolean hasNoticeMonth = StringUtils.isNotBlank(req.getNoticeMonth());
        boolean hasIdCardNo = StringUtils.isNotBlank(req.getIdCardNo());
        if (hasNoticeMonth == hasIdCardNo)
        {
            throw new ServiceException("“预到龄年月”和“身份证号”必须二选一填写");
        }
    }
}
