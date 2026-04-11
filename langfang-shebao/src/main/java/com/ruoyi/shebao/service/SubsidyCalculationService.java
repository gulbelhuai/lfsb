package com.ruoyi.shebao.service;

import com.ruoyi.shebao.dto.VillageOfficialFormDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
     * @param employeePensionMonths 职工养老月数
     * @param difficultySubsidyMonths 困难补贴月数
     * @return 补贴年限
     */
    BigDecimal calculateSubsidyYears(Integer ageAtBaseDate, Integer employeePensionMonths, Integer difficultySubsidyMonths);

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

    /**
     * 计算村干部补贴标准
     * @param positionList 任职信息
     * @return 补贴标准
     */
    BigDecimal calculateVillageOfficialSubsidyAmount(String idCardNo, List<VillageOfficialFormDto.VillageOfficialPositionDto> positionList);


    /**
     * 计算职位服务年限
     * @param startDate 上任时间
     * @param endDate 卸任时间
     * @return 任职年限
     */
    BigDecimal computePositionServiceYears(LocalDate startDate, LocalDate endDate);
}
