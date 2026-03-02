package com.ruoyi.shebao.service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 补贴计算服务接口
 * 
 * @author ruoyi
 * @date 2025-09-28
 */
public interface SubsidyCalculationService
{
    /**
     * 计算被征地参保补贴年限
     * 根据截至基准日的年龄计算补贴年限
     * 
     * @param ageAtBaseDate 截至基准日的年龄
     * @return 补贴年限
     */
    BigDecimal calculateSubsidyYears(Integer ageAtBaseDate);

    /**
     * 计算被征地参保补贴金额
     * 
     * @param ageAtBaseDate 截至基准日的年龄
     * @param subsidyYears 补贴年限
     * @return 补贴金额
     */
    BigDecimal calculateSubsidyAmount(Integer ageAtBaseDate, BigDecimal subsidyYears);

    /**
     * 根据生日和基准日计算年龄
     * 
     * @param birthday 生日
     * @param baseDate 基准日
     * @return 年龄
     */
    Integer calculateAgeAtBaseDate(LocalDate birthday, LocalDate baseDate);

    /**
     * 获取年平均工资
     * 
     * @return 年平均工资
     */
    BigDecimal getAverageAnnualSalary();
}
