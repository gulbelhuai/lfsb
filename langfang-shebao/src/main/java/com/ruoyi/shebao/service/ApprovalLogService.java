package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.ApprovalLog;

public interface ApprovalLogService extends IService<ApprovalLog>
{
    void log(String businessType, Long businessId, String currentStatus, String operationType, String remark);
}
