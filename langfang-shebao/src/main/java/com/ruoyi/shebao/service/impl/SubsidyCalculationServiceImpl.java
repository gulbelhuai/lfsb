package com.ruoyi.shebao.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.dto.VillageOfficialFormDto;
import com.ruoyi.shebao.service.SubsidyCalculationService;
import com.ruoyi.system.service.ISysConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

/**
 * 补贴计算服务实现类
 * 
 * @author ruoyi
 * @date 2025-09-28
 */
@Slf4j
@Service
public class SubsidyCalculationServiceImpl implements SubsidyCalculationService
{

    @Resource
    private ISysConfigService configService;

    /** 年平均工资系统参数key */
    private static final String AVERAGE_ANNUAL_SALARY_KEY = "shebao.average.annual.salary";

    public static final String POSITION_SECRETARY_AND_DIRECTOR_SUBSIDY_KEY = "shebao.official.secretary.and.director.subsidy";
    public static final String POSITION_SECRETARY_OR_DIRECTOR_SUBSIDY_KEY = "shebao.official.secretary.or.director.subsidy";
    public static final String POSITION_TWO_COMMITTEES_SUBSIDY_KEY = "shebao.official.two.committees.subsidy";

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

    /**
     * 村干部补贴标准：仅统计职位 1/2/3；重叠月份按职位优先级 1 &gt; 2 &gt; 3 只计一次；
     * 同职位且时间上相邻的合并后再用 {@link #computePositionServiceYears} 算年限；
     * 最终为 各职位有效年限 × 对应系统配置补贴单价 之和。
     */
    @Override
    public BigDecimal calculateVillageOfficialSubsidyAmount(String idCardNo, List<VillageOfficialFormDto.VillageOfficialPositionDto> positionList)
    {
        if (positionList == null || positionList.isEmpty())
        {
            return BigDecimal.ZERO.setScale(2);
        }
        BigDecimal secretaryAndDirectorSubsidy = readSubsidyRateConfig(POSITION_SECRETARY_AND_DIRECTOR_SUBSIDY_KEY);
        BigDecimal secretaryOrDirectorSubsidy = readSubsidyRateConfig(POSITION_SECRETARY_OR_DIRECTOR_SUBSIDY_KEY);
        BigDecimal twoCommitteesSubsidy = readSubsidyRateConfig(POSITION_TWO_COMMITTEES_SUBSIDY_KEY);

        /*
         * 核心思路（按月离散、半开区间扫描）：
         * 1) 每条任职记录转为 [上任月, 卸任月] 闭区间，再写成半开区间 [startYm, endYm.plusMonths(1))，便于拼接边界。
         * 2) 收集所有边界点并排序，相邻边界之间是一段「原子月序列」，其中任意月份被同一组原始记录覆盖。
         * 3) 对每一段，在所有完全覆盖该段的原始记录中取职位编号最小者（1 优先于 2、2 优先于 3），重叠部分只归属一个职位。
         * 4) 将相邻且职位相同的原子段合并，再对合并后的闭区间调用 computePositionServiceYears（与业务口径一致）。
         */
        List<RawYmInterval> rawIntervals = buildSubsidyRawIntervals(positionList);
        if (rawIntervals.isEmpty())
        {
            log.info("村干部补贴标准计算[{}]：无职位1/2/3的有效任职区间，补贴金额=0", idCardNo);
            return BigDecimal.ZERO.setScale(2);
        }

        TreeSet<YearMonth> boundaries = new TreeSet<>();
        for (RawYmInterval raw : rawIntervals)
        {
            boundaries.add(raw.startYm());
            boundaries.add(raw.endYm().plusMonths(1));
        }
        List<YearMonth> boundaryList = new ArrayList<>(boundaries);

        List<HalfOpenSegment> atomic = new ArrayList<>();
        for (int i = 0; i < boundaryList.size() - 1; i++)
        {
            YearMonth b0 = boundaryList.get(i);
            YearMonth b1 = boundaryList.get(i + 1);
            long monthSpan = ChronoUnit.MONTHS.between(b0, b1);
            if (monthSpan <= 0)
            {
                continue;
            }
            String winner = resolveWinnerPosition(rawIntervals, b0, b1);
            if (winner == null)
            {
                continue;
            }
            atomic.add(new HalfOpenSegment(b0, b1, winner));
        }

        List<MergedYmSegment> merged = mergeAdjacentSamePosition(atomic);
        BigDecimal yearsPos1 = BigDecimal.ZERO;
        BigDecimal yearsPos2 = BigDecimal.ZERO;
        BigDecimal yearsPos3 = BigDecimal.ZERO;
        StringBuilder timelineLog = new StringBuilder();
        for (MergedYmSegment seg : merged)
        {
            LocalDate segStart = seg.startYm().atDay(1);
            LocalDate segEnd = seg.endYm().atDay(1);
            BigDecimal segYears = computePositionServiceYears(segStart, segEnd);
            if (segYears == null)
            {
                continue;
            }
            if (!timelineLog.isEmpty())
            {
                timelineLog.append("；");
            }
            timelineLog.append(seg.startYm())
                .append("至")
                .append(seg.endYm())
                .append(" \"")
                .append(seg.position())
                .append("\" ")
                .append(segYears.stripTrailingZeros().toPlainString())
                .append("年");

            if (VillageOfficialFormDto.VillageOfficialPositionDto.POSITION_SECRETARY_AND_DIRECTOR.equals(seg.position()))
            {
                yearsPos1 = yearsPos1.add(segYears);
            }
            else if (VillageOfficialFormDto.VillageOfficialPositionDto.POSITION_SECRETARY_OR_DIRECTOR.equals(seg.position()))
            {
                yearsPos2 = yearsPos2.add(segYears);
            }
            else if (VillageOfficialFormDto.VillageOfficialPositionDto.POSITION_TWO_COMMITTEES.equals(seg.position()))
            {
                yearsPos3 = yearsPos3.add(segYears);
            }
        }

        BigDecimal amount = yearsPos1.multiply(secretaryAndDirectorSubsidy)
            .add(yearsPos2.multiply(secretaryOrDirectorSubsidy))
            .add(yearsPos3.multiply(twoCommitteesSubsidy))
            .setScale(2, RoundingMode.HALF_UP);

        log.info("村干部补贴标准计算[{}]：去重叠合并后的任职时间段（按时间先后）=> {}", idCardNo, timelineLog);
        log.info("村干部补贴标准计算[{}]：职位\"1\"累计年限={}，职位\"2\"累计年限={}，职位\"3\"累计年限={}；单价(元/年) 1={} 2={} 3={}；合计补贴={}",
            idCardNo,
            yearsPos1.stripTrailingZeros().toPlainString(),
            yearsPos2.stripTrailingZeros().toPlainString(),
            yearsPos3.stripTrailingZeros().toPlainString(),
            secretaryAndDirectorSubsidy.stripTrailingZeros().toPlainString(),
            secretaryOrDirectorSubsidy.stripTrailingZeros().toPlainString(),
            twoCommitteesSubsidy.stripTrailingZeros().toPlainString(),
            amount.stripTrailingZeros().toPlainString());

        return amount;
    }

    /** 仅职位 1/2/3 参与补贴；起止规范到当月 1 日对应的年月。 */
    private static List<RawYmInterval> buildSubsidyRawIntervals(List<VillageOfficialFormDto.VillageOfficialPositionDto> positionList)
    {
        List<RawYmInterval> list = new ArrayList<>();
        for (VillageOfficialFormDto.VillageOfficialPositionDto dto : positionList)
        {
            if (dto == null || !isSubsidyPosition(dto.getPosition()) || dto.getStartDate() == null)
            {
                continue;
            }
            LocalDate start = dto.getStartDate().withDayOfMonth(1);
            LocalDate end = dto.getEndDate() != null ? dto.getEndDate().withDayOfMonth(1) : LocalDate.now().withDayOfMonth(1);
            if (end.isBefore(start))
            {
                continue;
            }
            YearMonth startYm = YearMonth.from(start);
            YearMonth endYm = YearMonth.from(end);
            list.add(new RawYmInterval(startYm, endYm, dto.getPosition()));
        }
        return list;
    }

    private static boolean isSubsidyPosition(String position)
    {
        return VillageOfficialFormDto.VillageOfficialPositionDto.POSITION_SECRETARY_AND_DIRECTOR.equals(position)
            || VillageOfficialFormDto.VillageOfficialPositionDto.POSITION_SECRETARY_OR_DIRECTOR.equals(position)
            || VillageOfficialFormDto.VillageOfficialPositionDto.POSITION_TWO_COMMITTEES.equals(position);
    }

    /**
     * 在半开区间 [b0, b1) 上，找出所有完全覆盖该段的原始任职（startYm ≤ b0 且 endYm+1 ≥ b1），
     * 取 position 字典序最小（即 "1" &lt; "2" &lt; "3"）作为本段归属职位。
     */
    private static String resolveWinnerPosition(List<RawYmInterval> rawIntervals, YearMonth b0, YearMonth b1)
    {
        String winner = null;
        int bestRank = Integer.MAX_VALUE;
        for (RawYmInterval raw : rawIntervals)
        {
            YearMonth endExclusive = raw.endYm().plusMonths(1);
            if (!raw.startYm().isAfter(b0) && !endExclusive.isBefore(b1))
            {
                int rank = positionRank(raw.position());
                if (rank < bestRank)
                {
                    bestRank = rank;
                    winner = raw.position();
                }
            }
        }
        return winner;
    }

    private static int positionRank(String position)
    {
        try
        {
            return Integer.parseInt(position.trim());
        }
        catch (Exception e)
        {
            return 99;
        }
    }

    /** 半开区间上相邻且职位相同则合并，便于按连续段计算年限。 */
    private static List<MergedYmSegment> mergeAdjacentSamePosition(List<HalfOpenSegment> atomic)
    {
        List<MergedYmSegment> merged = new ArrayList<>();
        for (HalfOpenSegment seg : atomic)
        {
            if (merged.isEmpty())
            {
                merged.add(MergedYmSegment.fromHalfOpen(seg.start(), seg.endExclusive(), seg.position()));
                continue;
            }
            MergedYmSegment last = merged.get(merged.size() - 1);
            if (last.endYm().plusMonths(1).equals(seg.start()) && Objects.equals(last.position(), seg.position()))
            {
                merged.set(merged.size() - 1, new MergedYmSegment(last.startYm(), seg.endExclusive().minusMonths(1), last.position()));
            }
            else
            {
                merged.add(MergedYmSegment.fromHalfOpen(seg.start(), seg.endExclusive(), seg.position()));
            }
        }
        return merged;
    }

    private BigDecimal readSubsidyRateConfig(String key)
    {
        String raw = configService.selectConfigByKey(key);
        if (StringUtils.isEmpty(raw))
        {
            return BigDecimal.ZERO;
        }
        try
        {
            return new BigDecimal(raw.trim());
        }
        catch (NumberFormatException e)
        {
            log.warn("村干部补贴配置 key={} 值非法: {}，按0处理", key, raw);
            return BigDecimal.ZERO;
        }
    }

    private record RawYmInterval(YearMonth startYm, YearMonth endYm, String position) {}

    private record HalfOpenSegment(YearMonth start, YearMonth endExclusive, String position) {}

    private record MergedYmSegment(YearMonth startYm, YearMonth endYm, String position)
    {
        static MergedYmSegment fromHalfOpen(YearMonth start, YearMonth endExclusive, String position)
        {
            return new MergedYmSegment(start, endExclusive.minusMonths(1), position);
        }
    }

    /**
     * 任职年限：先算含首尾月的月数，再换算成年限。
     * 月数 = ChronoUnit.MONTHS.between(上任月, 卸任月) + 1
     * 年限 = floor(月数/12) + (余数>=6 ? 1 : (余数>=0 ? 0.5 : 0))
     */
    @Override
    public BigDecimal computePositionServiceYears(LocalDate startDate, LocalDate endDate)
    {
        if (startDate == null)
        {
            return null;
        }
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        if (end.isBefore(startDate))
        {
            return null;
        }
        YearMonth startYm = YearMonth.from(startDate);
        YearMonth endYm = YearMonth.from(end);
        long months = ChronoUnit.MONTHS.between(startYm, endYm) + 1;
        if (months <= 0)
        {
            return null;
        }

        long years = months / 12;
        long remainder = months % 12;
        BigDecimal bonus = BigDecimal.ZERO;
        if (remainder >= 6) {
            bonus = BigDecimal.ONE;
        } else if (remainder > 0) {
            bonus = new BigDecimal("0.5");
        }
        return BigDecimal.valueOf(years).add(bonus);
    }
}
