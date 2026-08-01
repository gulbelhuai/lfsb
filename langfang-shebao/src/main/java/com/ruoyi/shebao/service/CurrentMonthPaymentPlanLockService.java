package com.ruoyi.shebao.service;

/**
 * 当前自然月业务期支付计划锁：存在未删除计划则禁止待遇相关写操作。
 */
public interface CurrentMonthPaymentPlanLockService
{
    /**
     * 若当前服务器时间所在月对应业务期已有支付计划，则抛出业务异常。
     */
    void assertWritable();

    /**
     * @return 当前月业务期（月初）是否存在未删除支付计划
     */
    boolean hasActivePlanForCurrentMonth();
}
