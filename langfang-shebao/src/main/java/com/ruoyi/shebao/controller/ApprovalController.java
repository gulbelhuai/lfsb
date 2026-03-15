package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.shebao.domain.ApprovalLog;
import com.ruoyi.shebao.service.ApprovalLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shebao/approval")
public class ApprovalController extends BaseController
{
    @Autowired
    private ApprovalLogService approvalLogService;

    @GetMapping("/history/{businessType}/{businessId}")
    public AjaxResult history(@PathVariable String businessType, @PathVariable Long businessId)
    {
        List<ApprovalLog> history = approvalLogService.list(
                new LambdaQueryWrapper<ApprovalLog>()
                        .eq(ApprovalLog::getBusinessType, businessType)
                        .eq(ApprovalLog::getBusinessId, businessId)
                        .orderByDesc(ApprovalLog::getCreateTime));
        return AjaxResult.success(history);
    }
}
