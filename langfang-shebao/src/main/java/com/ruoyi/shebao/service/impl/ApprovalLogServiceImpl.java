package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.ApprovalLog;
import com.ruoyi.shebao.mapper.ApprovalLogMapper;
import com.ruoyi.shebao.service.ApprovalLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ApprovalLogServiceImpl extends ServiceImpl<ApprovalLogMapper, ApprovalLog> implements ApprovalLogService
{
    @Override
    public void log(String businessType, Long businessId, String currentStatus, String operationType, String remark)
    {
        ApprovalLog log = new ApprovalLog();
        log.setBusinessType(businessType);
        log.setBusinessId(businessId);
        log.setCurrentStatus(currentStatus);
        log.setOperationType(operationType);
        log.setOperationRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        try {
            log.setOperatorId(SecurityUtils.getUserId());
            log.setOperatorName(SecurityUtils.getUsername());
        } catch (Exception ignored) {
            log.setOperatorName("system");
        }
        this.save(log);
    }
}
