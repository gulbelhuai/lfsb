package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.FinanceAccount;
import com.ruoyi.shebao.dto.FinanceAccountFiscalAllocationReq;
import com.ruoyi.shebao.dto.FinanceAccountTransactionListReq;
import com.ruoyi.shebao.dto.FinanceAccountTransactionListResp;

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

    /** 财政拨款：增加账户余额并记明细 */
    int fiscalAllocation(Long accountId, FinanceAccountFiscalAllocationReq req);
}
