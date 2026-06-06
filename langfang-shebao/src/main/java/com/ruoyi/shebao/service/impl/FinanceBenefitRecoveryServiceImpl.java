package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.BenefitSuspension;
import com.ruoyi.shebao.domain.BenefitSuspensionItem;
import com.ruoyi.shebao.domain.FinanceBenefitRecovery;
import com.ruoyi.shebao.dto.FinanceBenefitRecoveryListReq;
import com.ruoyi.shebao.dto.FinanceBenefitRecoveryListResp;
import com.ruoyi.shebao.mapper.FinanceBenefitRecoveryMapper;
import com.ruoyi.shebao.service.FinanceBenefitRecoveryService;
import com.ruoyi.shebao.service.IFinanceAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FinanceBenefitRecoveryServiceImpl implements FinanceBenefitRecoveryService
{
    public static final String STATUS_PENDING = "0";
    public static final String STATUS_RECOVERED = "1";

    private final FinanceBenefitRecoveryMapper financeBenefitRecoveryMapper;
    private final IFinanceAccountService financeAccountService;

    @Override
    public Page<FinanceBenefitRecoveryListResp> list(FinanceBenefitRecoveryListReq req)
    {
        long pageNum = req == null ? 1L : req.pageNumOrDefault();
        long pageSize = req == null ? 10L : req.pageSizeOrDefault();
        Page<FinanceBenefitRecoveryListResp> page = new Page<>(pageNum, pageSize);
        return financeBenefitRecoveryMapper.selectRecoveryPage(page, req);
    }

    @Override
    public void syncFromSuspensionItem(BenefitSuspension suspension, BenefitSuspensionItem item)
    {
        if (suspension == null || item == null || !"1".equals(item.getNeedRecover()))
        {
            return;
        }
        Long exists = financeBenefitRecoveryMapper.selectCount(new LambdaQueryWrapper<FinanceBenefitRecovery>()
                .eq(FinanceBenefitRecovery::getSuspensionItemId, item.getId())
                .eq(FinanceBenefitRecovery::getDelFlag, "0"));
        if (exists != null && exists > 0)
        {
            return;
        }

        FinanceBenefitRecovery record = new FinanceBenefitRecovery();
        record.setSubsidyPersonId(suspension.getSubsidyPersonId());
        record.setIdCardNo(suspension.getIdCardNo());
        record.setSubsidyType(item.getSubsidyType());
        record.setSuspensionId(suspension.getId());
        record.setSuspensionItemId(item.getId());
        record.setRecoverStartMonth(item.getRecoverStartMonth());
        record.setRecoverEndMonth(item.getRecoverEndMonth());
        record.setRecoverAmount(item.getRecoverAmount() == null ? BigDecimal.ZERO : item.getRecoverAmount());
        record.setRecoveryStatus(STATUS_PENDING);
        record.setCreateBy(SecurityUtils.getUsername());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateBy(SecurityUtils.getUsername());
        record.setUpdateTime(LocalDateTime.now());
        financeBenefitRecoveryMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmRecovered(Long id)
    {
        FinanceBenefitRecovery record = financeBenefitRecoveryMapper.selectById(id);
        if (record == null || !"0".equals(record.getDelFlag()))
        {
            throw new ServiceException("追回记录不存在");
        }
        if (STATUS_RECOVERED.equals(record.getRecoveryStatus()))
        {
            throw new ServiceException("该记录已追回，请勿重复操作");
        }

        LocalDateTime now = LocalDateTime.now();
        String username = SecurityUtils.getUsername();
        Long transactionId = null;
        BigDecimal amount = record.getRecoverAmount() == null ? BigDecimal.ZERO : record.getRecoverAmount();
        if (amount.compareTo(BigDecimal.ZERO) > 0)
        {
            transactionId = financeAccountService.creditForBenefitRecovery(
                    record.getSubsidyType(),
                    record.getId(),
                    amount,
                    "待遇追回");
        }

        FinanceBenefitRecovery update = new FinanceBenefitRecovery();
        update.setId(id);
        update.setRecoveryStatus(STATUS_RECOVERED);
        update.setRecoveryTime(now);
        update.setAccountTransactionId(transactionId);
        update.setUpdateBy(username);
        update.setUpdateTime(now);
        int rows = financeBenefitRecoveryMapper.updateById(update);
        if (rows == 0)
        {
            throw new ServiceException("更新追回状态失败");
        }
    }
}
