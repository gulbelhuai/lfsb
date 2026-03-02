package com.ruoyi.shebao.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * 发放明细Controller
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@RestController
@RequestMapping("/shebao/audit/detail")
public class AuditDetailController extends BaseController
{
    /**
     * 查询发放明细列表
     */
    @PreAuthorize("@ss.hasPermi('shebao:audit:detail:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String name,
                             @RequestParam(required = false) String idCardNo,
                             @RequestParam(required = false) String subsidyType,
                             @RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize)
    {
        // TODO: 实现发放明细查询逻辑
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(new ArrayList<>());
        rspData.setTotal(0);
        return rspData;
    }

    /**
     * 导出发放明细
     */
    @PreAuthorize("@ss.hasPermi('shebao:audit:detail:export')")
    @PostMapping("/export")
    public AjaxResult export(@RequestParam(required = false) String name,
                            @RequestParam(required = false) String idCardNo,
                            @RequestParam(required = false) String subsidyType,
                            @RequestParam(required = false) String startDate,
                            @RequestParam(required = false) String endDate)
    {
        // TODO: 实现导出逻辑
        return AjaxResult.success();
    }
}
