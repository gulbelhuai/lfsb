package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
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
    public BigDecimal sumTransactionAmount(FinanceAccountTransactionListReq req)
    {
        BigDecimal sum = financeAccountTransactionMapper.sumTransactionAmount(req);
        return sum == null ? BigDecimal.ZERO : sum;
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
        txn.setAccountName(account.getAccountName());
        txn.setBatchNo(null);
        txn.setBusinessId(null);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void settleSubsidyDistribution(String subsidyType, Long businessId, String batchNo,
                                          String businessPeriodYm, BigDecimal totalAmount, BigDecimal failedAmount)
    {
        BigDecimal total = totalAmount == null ? BigDecimal.ZERO : totalAmount;
        BigDecimal failed = failedAmount == null ? BigDecimal.ZERO : failedAmount;
        if (total.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        if (failed.compareTo(BigDecimal.ZERO) < 0 || failed.compareTo(total) > 0)
        {
            throw new ServiceException("失败金额不合法，无法完成发放结算");
        }
        if (StringUtils.isEmpty(subsidyType))
        {
            throw new ServiceException("支付计划缺少补贴类型，无法扣款");
        }
        FinanceAccount account = baseMapper.selectBySubsidyType(subsidyType);
        if (account == null)
        {
            throw new ServiceException("未找到对应补贴类型的财务账户");
        }
        BigDecimal before = account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();
        LocalDateTime now = LocalDateTime.now();
        String username = SecurityUtils.getUsername();
        String period = StringUtils.isEmpty(businessPeriodYm) ? "" : businessPeriodYm.trim();
        String typeLabel = subsidyTypeShortLabel(subsidyType);

        int deductRows = baseMapper.deductBalance(account.getId(), total, username, now);
        if (deductRows == 0)
        {
            throw new ServiceException("账户余额不足，无法完成发放扣款");
        }
        BigDecimal afterPay = before.subtract(total);
        insertDistributionTxn(account, businessId, batchNo, total.negate(), afterPay, now, username,
                typeLabel + period + "发放");

        if (failed.compareTo(BigDecimal.ZERO) > 0)
        {
            int creditRows = baseMapper.addBalance(account.getId(), failed, username, now);
            if (creditRows == 0)
            {
                throw new ServiceException("财务账户不可用，无法完成失败退回入账");
            }
            BigDecimal afterRefund = afterPay.add(failed);
            insertDistributionTxn(account, businessId, batchNo, failed, afterRefund, now, username,
                    typeLabel + period + "发放失败退回");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long creditForBenefitRecovery(String subsidyType, Long recoveryId, BigDecimal amount, String remark)
    {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("追回金额须大于0");
        }
        if (StringUtils.isEmpty(subsidyType))
        {
            throw new ServiceException("追回记录缺少补贴类型，无法入账");
        }
        FinanceAccount account = baseMapper.selectBySubsidyType(subsidyType);
        if (account == null)
        {
            throw new ServiceException("未找到对应补贴类型的财务账户");
        }
        BigDecimal before = account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();
        LocalDateTime now = LocalDateTime.now();
        String username = SecurityUtils.getUsername();

        int rows = baseMapper.addBalance(account.getId(), amount, username, now);
        if (rows == 0)
        {
            throw new ServiceException("财务账户不可用，无法完成待遇追收入账");
        }

        BigDecimal after = before.add(amount);
        FinanceAccountTransaction txn = new FinanceAccountTransaction();
        txn.setAccountId(account.getId());
        txn.setAccountName(account.getAccountName());
        txn.setBatchNo(null);
        txn.setBusinessId(recoveryId);
        txn.setTransactionType(TX_BENEFIT_RECOVERY);
        txn.setAmount(amount);
        txn.setBalance(after);
        txn.setTransactionTime(now);
        txn.setRemark(remark == null || remark.isBlank() ? "待遇追回" : remark.trim());
        txn.setDelFlag("0");
        txn.setCreateBy(username);
        txn.setCreateTime(now);
        txn.setUpdateBy(username);
        txn.setUpdateTime(now);
        financeAccountTransactionMapper.insert(txn);
        return txn.getId();
    }

    private void insertDistributionTxn(FinanceAccount account, Long businessId, String batchNo,
                                       BigDecimal amount, BigDecimal balanceAfter, LocalDateTime now,
                                       String username, String remark)
    {
        FinanceAccountTransaction txn = new FinanceAccountTransaction();
        txn.setAccountId(account.getId());
        txn.setAccountName(account.getAccountName());
        txn.setBatchNo(batchNo);
        txn.setBusinessId(businessId);
        txn.setTransactionType(TX_SUBSIDY_DISTRIBUTION);
        txn.setAmount(amount);
        txn.setBalance(balanceAfter);
        txn.setTransactionTime(now);
        txn.setRemark(remark);
        txn.setDelFlag("0");
        txn.setCreateBy(username);
        txn.setCreateTime(now);
        txn.setUpdateBy(username);
        txn.setUpdateTime(now);
        financeAccountTransactionMapper.insert(txn);
    }

    /** 备注用补贴类型简称（不含「账户」） */
    static String subsidyTypeShortLabel(String subsidyType)
    {
        if (subsidyType == null || subsidyType.isBlank())
        {
            return "";
        }
        return switch (subsidyType)
        {
            case "land_loss", "land_loss_resident" -> "失地补贴";
            case "expropriatee", "expropriatee_subsidy" -> "被征地补贴";
            case "demolition", "demolition_resident" -> "拆迁补贴";
            case "village_official" -> "村干部补贴";
            case "teacher", "teacher_subsidy" -> "教师补贴";
            default -> subsidyType;
        };
    }
}
