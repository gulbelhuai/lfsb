package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.FinanceAccount;
import com.ruoyi.shebao.dto.FinanceAccountFiscalAllocationReq;
import com.ruoyi.shebao.dto.FinanceAccountTransactionListReq;
import com.ruoyi.shebao.dto.FinanceAccountTransactionListResp;

import java.math.BigDecimal;
import java.util.List;

/**
 * 财务账户Service接口
 */
public interface IFinanceAccountService extends IService<FinanceAccount>
{
    /** 账户概览：按补贴类型列出正常账户 */
    List<FinanceAccount> selectOverviewAccounts();

    /** 账户明细列表 */
    Page<FinanceAccountTransactionListResp> selectTransactionList(FinanceAccountTransactionListReq req);

    /** 账户明细金额汇总（当前筛选条件全量，不分页） */
    BigDecimal sumTransactionAmount(FinanceAccountTransactionListReq req);

    /** 财政拨款：增加账户余额并记明细 */
    int fiscalAllocation(Long accountId, FinanceAccountFiscalAllocationReq req);

    /**
     * 银行发放完成结算：先按应发合计扣款记「发放」，再按失败合计入账记「退回」（失败为0则不写退回）
     *
     * @param subsidyType       补贴类型
     * @param businessId        支付计划ID
     * @param batchNo           批次号
     * @param businessPeriodYm  业务期 yyyy-MM
     * @param totalAmount       本批次全部明细应发合计（正数）
     * @param failedAmount      本批次失败明细合计（正数，可为0）
     */
    void settleSubsidyDistribution(String subsidyType, Long businessId, String batchNo,
                                   String businessPeriodYm, BigDecimal totalAmount, BigDecimal failedAmount);

    /**
     * 待遇追回入账：按补贴类型增加账户余额并记收入明细
     *
     * @return 财务账户明细ID
     */
    Long creditForBenefitRecovery(String subsidyType, Long recoveryId, BigDecimal amount, String remark);
}
