package com.ruoyi.shebao.aspect;

import com.ruoyi.shebao.service.CurrentMonthPaymentPlanLockService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 拦截标注了 {@link com.ruoyi.shebao.annotation.BlockIfCurrentMonthPaymentPlan} 的写操作。
 */
@Aspect
@Component
public class CurrentMonthPaymentPlanLockAspect
{
    @Autowired
    private CurrentMonthPaymentPlanLockService currentMonthPaymentPlanLockService;

    @Before("@within(com.ruoyi.shebao.annotation.BlockIfCurrentMonthPaymentPlan) || @annotation(com.ruoyi.shebao.annotation.BlockIfCurrentMonthPaymentPlan)")
    public void blockIfLocked()
    {
        currentMonthPaymentPlanLockService.assertWritable();
    }
}
