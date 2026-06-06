package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.BenefitSuspension;
import com.ruoyi.shebao.domain.BenefitSuspensionItem;
import com.ruoyi.shebao.dto.FinanceBenefitRecoveryListReq;
import com.ruoyi.shebao.dto.FinanceBenefitRecoveryListResp;

public interface FinanceBenefitRecoveryService
{
    Page<FinanceBenefitRecoveryListResp> list(FinanceBenefitRecoveryListReq req);

    /** 待遇暂停创建时，同步需追回明细到财务追回记录 */
    void syncFromSuspensionItem(BenefitSuspension suspension, BenefitSuspensionItem item);

    /** 确认已追回：更新状态、记入账户余额与明细 */
    void confirmRecovered(Long id);
}
