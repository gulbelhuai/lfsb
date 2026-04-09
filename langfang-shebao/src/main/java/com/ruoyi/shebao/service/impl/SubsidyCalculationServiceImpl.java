package com.ruoyi.shebao.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.service.SubsidyCalculationService;
import com.ruoyi.system.service.ISysConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

/**
 * 补贴计算服务实现类
 * 
 * @author ruoyi
 * @date 2025-09-28
 */
@Service
public class SubsidyCalculationServiceImpl implements SubsidyCalculationService
{
    @Resource
    private ISysConfigService configService;

    /** 年平均工资系统参数key */
    private static final String AVERAGE_ANNUAL_SALARY_KEY = "shebao.average.annual.salary";

    /**
     * 计算被征地参保补贴年限
     * 根据截至基准日的年龄计算补贴年限
     * 
     * 计算规则：
     * - 年满16至45周岁的，补贴年限为3年
     * - 年满46至50周岁的，补贴年限为5年
     * - 年满51周岁及以上未达到60周岁的，补贴年限为实际年龄减去45
     * - 年满60周岁，享受15年的参保补贴
     * 
     * @param ageAtBaseDate 截至基准日的年龄
     * @return 补贴年限
     */
    @Override
    public BigDecimal calculateSubsidyYears(Integer ageAtBaseDate, Integer employeePensionMonths, Integer difficultySubsidyMonths)
    {
        if (ageAtBaseDate == null)
        {
            return BigDecimal.ZERO;
        }

        BigDecimal baseSubsidyYears;
        if (ageAtBaseDate >= 16 && ageAtBaseDate <= 45)
        {
            baseSubsidyYears = new BigDecimal("3");
        }
        else if (ageAtBaseDate >= 46 && ageAtBaseDate <= 50)
        {
            baseSubsidyYears = new BigDecimal("5");
        }
        else if (ageAtBaseDate >= 51 && ageAtBaseDate < 60)
        {
            baseSubsidyYears = new BigDecimal(ageAtBaseDate - 45);
        }
        else if (ageAtBaseDate >= 60)
        {
            baseSubsidyYears = new BigDecimal("15");
        }
        else
        {
            // 16岁以下不符合条件
            return BigDecimal.ZERO;
        }

        // 新规则：补贴年限=向上取整((现有补贴年限*12-职工养老月数-困难补贴月数)/12)
        int employeeMonths = employeePensionMonths == null ? 0 : Math.max(employeePensionMonths, 0);
        int difficultyMonths = difficultySubsidyMonths == null ? 0 : Math.max(difficultySubsidyMonths, 0);
        BigDecimal totalMonths = baseSubsidyYears.multiply(new BigDecimal("12"));
        BigDecimal deductedMonths = totalMonths
                .subtract(new BigDecimal(employeeMonths))
                .subtract(new BigDecimal(difficultyMonths));
        BigDecimal yearsAfterDeduction = deductedMonths
                .divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP)
                .setScale(0, RoundingMode.CEILING);
        return yearsAfterDeduction.max(BigDecimal.ZERO);
    }

    /**
     * 计算被征地参保补贴金额
     * 
     * 计算公式：
     * 参保补贴标准 = 2018年度河北省全口径城镇单位就业人员年平均工资（56724元）×50%×14%×12%×补贴年限
     * 
     * @param ageAtBaseDate 截至基准日的年龄
     * @param subsidyYears 补贴年限
     * @return 补贴金额
     */
    @Override
    public BigDecimal calculateSubsidyAmount(Integer ageAtBaseDate, BigDecimal subsidyYears)
    {
        if (ageAtBaseDate == null || subsidyYears == null || subsidyYears.compareTo(BigDecimal.ZERO) <= 0)
        {
            return BigDecimal.ZERO;
        }

        // 获取年平均工资
        BigDecimal averageAnnualSalary = getAverageAnnualSalary();
        
        // 计算补贴金额 = 年平均工资 × 50% × 14% × 12% × 补贴年限
        BigDecimal subsidyAmount = averageAnnualSalary
                .multiply(new BigDecimal("0.50"))  // 50%
                .multiply(new BigDecimal("0.14"))  // 14%
                .multiply(new BigDecimal("0.12"))  // 12%
                .multiply(subsidyYears);           // 补贴年限

        // “补贴金额”四舍五入取整，当补贴年限为“1、2、4”时，保留两位小数。
        int years = subsidyYears.intValue();
        if (years == 1 || years == 2 || years == 4) {
            return subsidyAmount.setScale(2, RoundingMode.HALF_UP);
        }
        return subsidyAmount.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 根据生日和基准日计算年龄
     * 
     * @param birthday 生日
     * @param baseDate 基准日
     * @return 年龄
     */
    @Override
    public Integer calculateAgeAtBaseDate(LocalDate birthday, LocalDate baseDate)
    {
        if (birthday == null || baseDate == null)
        {
            return null;
        }

        if (baseDate.isBefore(birthday))
        {
            return 0;
        }

        Period period = Period.between(birthday, baseDate);
        return period.getYears();
    }

    /**
     * 获取年平均工资
     * 
     * @return 年平均工资
     */
    @Override
    public BigDecimal getAverageAnnualSalary()
    {
        String salaryStr = configService.selectConfigByKey(AVERAGE_ANNUAL_SALARY_KEY);
        
        if (StringUtils.isEmpty(salaryStr))
        {
            // 如果没有配置，返回默认值 56724
            return new BigDecimal("56724");
        }

        try
        {
            return new BigDecimal(salaryStr);
        }
        catch (NumberFormatException e)
        {
            // 配置值格式错误，返回默认值
            return new BigDecimal("56724");
        }
    }
}
