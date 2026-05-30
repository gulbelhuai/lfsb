package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.FinanceAccount;
import com.ruoyi.shebao.domain.FinanceAccountTransaction;
import com.ruoyi.shebao.dto.FinanceAccountFiscalAllocationReq;
import com.ruoyi.shebao.dto.FinanceAccountTransactionListReq;
import com.ruoyi.shebao.dto.FinanceAccountTransactionListResp;
import com.ruoyi.shebao.mapper.FinanceAccountMapper;
import com.ruoyi.shebao.mapper.FinanceAccountTransactionMapper;
import com.ruoyi.shebao.service.IFinanceAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinanceAccountServiceImpl extends ServiceImpl<FinanceAccountMapper, FinanceAccount> implements IFinanceAccountService
{
    public static final String TX_FISCAL_ALLOCATION = "fiscal_allocation";
    public static final String TX_SUBSIDY_DISTRIBUTION = "subsidy_distribution";
    public static final String TX_BENEFIT_RECOVERY = "benefit_recovery";

    @Autowired
    private FinanceAccountTransactionMapper financeAccountTransactionMapper;

    @Override
    public List<FinanceAccount> selectOverviewAccounts()
    {
        return lambdaQuery()
                .eq(FinanceAccount::getDelFlag, "0")
                .eq(FinanceAccount::getStatus, "1")
                .notIn(FinanceAccount::getAccountType, "teacher", "teacher_subsidy")
                .orderByAsc(FinanceAccount::getAccountType)
                .list();
    }

    @Override
    public Page<FinanceAccountTransactionListResp> selectTransactionList(FinanceAccountTransactionListReq req)
    {
        Page<FinanceAccountTransactionListResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return financeAccountTransactionMapper.selectTransactionList(page, req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int fiscalAllocation(Long accountId, FinanceAccountFiscalAllocationReq req)
    {
        if (req == null || req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("拨款金额须大于0");
        }
        FinanceAccount account = getById(accountId);
        if (account == null || !"0".equals(account.getDelFlag()))
        {
            throw new ServiceException("财务账户不存在");
        }
        if (!"1".equals(account.getStatus()))
        {
            throw new ServiceException("该账户已停用，无法拨款");
        }
        BigDecimal current = account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();
        BigDecimal newBalance = current.add(req.getAmount());
        LocalDateTime now = LocalDateTime.now();
        String username = SecurityUtils.getUsername();

        FinanceAccountTransaction txn = new FinanceAccountTransaction();
        txn.setAccountId(accountId);
        txn.setAccountName(buildAccountDisplayName(account.getAccountType()));
        txn.setBatchNo(null);
        txn.setTransactionType(TX_FISCAL_ALLOCATION);
        txn.setAmount(req.getAmount());
        txn.setBalance(newBalance);
        txn.setTransactionTime(now);
        txn.setRemark(req.getRemark() == null || req.getRemark().isBlank() ? "财政拨款" : req.getRemark().trim());
        txn.setDelFlag("0");
        txn.setCreateBy(username);
        txn.setCreateTime(now);
        txn.setUpdateBy(username);
        txn.setUpdateTime(now);
        financeAccountTransactionMapper.insert(txn);

        FinanceAccount upd = new FinanceAccount();
        upd.setId(accountId);
        upd.setBalance(newBalance);
        upd.setUpdateBy(username);
        upd.setUpdateTime(now);
        return baseMapper.updateById(upd);
    }

    private String buildAccountDisplayName(String subsidyType)
    {
        if (subsidyType == null || subsidyType.isBlank())
        {
            return "账户";
        }
        String label = switch (subsidyType)
        {
            case "land_loss", "land_loss_resident" -> "失地补贴";
            case "expropriatee", "expropriatee_subsidy" -> "被征地补贴";
            case "demolition", "demolition_resident" -> "拆迁补贴";
            case "village_official" -> "村干部补贴";
            default -> subsidyType;
        };
        return label + "账户";
    }
}
