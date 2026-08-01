package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.shebao.domain.PaymentPlan;
import com.ruoyi.shebao.mapper.PaymentPlanMapper;
import com.ruoyi.shebao.service.CurrentMonthPaymentPlanLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * 按服务器当前月业务期查询是否存在支付计划。
 */
@Service
public class CurrentMonthPaymentPlanLockServiceImpl implements CurrentMonthPaymentPlanLockService
{
    private static final DateTimeFormatter PERIOD_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private PaymentPlanMapper paymentPlanMapper;

    @Override
    public void assertWritable()
    {
        LocalDate businessPeriod = currentMonthBusinessPeriod();
        if (!hasActivePlan(businessPeriod))
        {
            return;
        }
        throw new ServiceException("当前业务期 " + businessPeriod.format(PERIOD_FMT) + " 已生成支付计划，请次月办理");
    }

    @Override
    public boolean hasActivePlanForCurrentMonth()
    {
        return hasActivePlan(currentMonthBusinessPeriod());
    }

    private boolean hasActivePlan(LocalDate businessPeriod)
    {
        Long count = paymentPlanMapper.selectCount(new LambdaQueryWrapper<PaymentPlan>()
                .eq(PaymentPlan::getBusinessPeriod, businessPeriod)
                .eq(PaymentPlan::getDelFlag, "0"));
        return count != null && count > 0;
    }

    private static LocalDate currentMonthBusinessPeriod()
    {
        return YearMonth.now().atDay(1);
    }
}
