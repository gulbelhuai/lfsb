package com.ruoyi.shebao.service.impl;

import com.ruoyi.system.service.ISysConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * 补贴计算服务单元测试
 * 对照需求：被征地参保补贴计算规则（补贴年限、金额、年龄计算）
 */
@ExtendWith(MockitoExtension.class)
class SubsidyCalculationServiceImplTest {

    @Mock
    private ISysConfigService configService;

    @InjectMocks
    private SubsidyCalculationServiceImpl subsidyCalculationService;

    private static final String AVERAGE_SALARY_KEY = "shebao.average.annual.salary";
    private static final String DEFAULT_SALARY = "56724";

    @BeforeEach
    void setUp() {
        lenient().when(configService.selectConfigByKey(eq(AVERAGE_SALARY_KEY))).thenReturn(DEFAULT_SALARY);
    }

    @Nested
    @DisplayName("补贴年限计算 calculateSubsidyYears")
    class CalculateSubsidyYearsTests {

        @Test
        @DisplayName("16-45岁应返回3年")
        void age16To45_returns3() {
            assertEquals(new BigDecimal("3"), subsidyCalculationService.calculateSubsidyYears(16));
            assertEquals(new BigDecimal("3"), subsidyCalculationService.calculateSubsidyYears(45));
        }

        @Test
        @DisplayName("46-50岁应返回5年")
        void age46To50_returns5() {
            assertEquals(new BigDecimal("5"), subsidyCalculationService.calculateSubsidyYears(46));
            assertEquals(new BigDecimal("5"), subsidyCalculationService.calculateSubsidyYears(50));
        }

        @Test
        @DisplayName("51-59岁应为实际年龄减45")
        void age51To59_returnsAgeMinus45() {
            assertEquals(new BigDecimal("6"), subsidyCalculationService.calculateSubsidyYears(51));
            assertEquals(new BigDecimal("14"), subsidyCalculationService.calculateSubsidyYears(59));
        }

        @Test
        @DisplayName("60岁及以上应返回15年")
        void age60Plus_returns15() {
            assertEquals(new BigDecimal("15"), subsidyCalculationService.calculateSubsidyYears(60));
            assertEquals(new BigDecimal("15"), subsidyCalculationService.calculateSubsidyYears(70));
        }

        @Test
        @DisplayName("16岁以下应返回0")
        void ageBelow16_returns0() {
            assertEquals(BigDecimal.ZERO, subsidyCalculationService.calculateSubsidyYears(15));
            assertEquals(BigDecimal.ZERO, subsidyCalculationService.calculateSubsidyYears(0));
        }

        @Test
        @DisplayName("null 年龄应返回0")
        void nullAge_returns0() {
            assertEquals(BigDecimal.ZERO, subsidyCalculationService.calculateSubsidyYears(null));
        }
    }

    @Nested
    @DisplayName("年龄计算 calculateAgeAtBaseDate")
    class CalculateAgeAtBaseDateTests {

        @Test
        @DisplayName("基准日在生日之后应正确计算年龄")
        void afterBirthday_calculatesCorrectly() {
            LocalDate birthday = LocalDate.of(1970, 5, 1);
            LocalDate baseDate = LocalDate.of(2025, 5, 1);
            assertEquals(55, subsidyCalculationService.calculateAgeAtBaseDate(birthday, baseDate));
        }

        @Test
        @DisplayName("基准日等于生日应返回0岁")
        void baseDateEqualsBirthday_returns0() {
            LocalDate date = LocalDate.of(1990, 1, 1);
            assertEquals(0, subsidyCalculationService.calculateAgeAtBaseDate(date, date));
        }

        @Test
        @DisplayName("基准日在生日之前应返回0")
        void baseDateBeforeBirthday_returns0() {
            LocalDate birthday = LocalDate.of(1990, 1, 1);
            LocalDate baseDate = LocalDate.of(1989, 12, 31);
            assertEquals(0, subsidyCalculationService.calculateAgeAtBaseDate(birthday, baseDate));
        }

        @Test
        @DisplayName("null 输入应返回 null")
        void nullInput_returnsNull() {
            assertNull(subsidyCalculationService.calculateAgeAtBaseDate(null, LocalDate.now()));
            assertNull(subsidyCalculationService.calculateAgeAtBaseDate(LocalDate.now(), null));
        }
    }

    @Nested
    @DisplayName("补贴金额计算 calculateSubsidyAmount")
    class CalculateSubsidyAmountTests {

        @Test
        @DisplayName("正常参数应返回正数金额且保留2位小数")
        void validInput_returnsPositiveAmount() {
            BigDecimal amount = subsidyCalculationService.calculateSubsidyAmount(55, new BigDecimal("10"));
            assertNotNull(amount);
            assertTrue(amount.compareTo(BigDecimal.ZERO) > 0);
            assertEquals(2, amount.scale());
        }

        @Test
        @DisplayName("年龄或年限为 null/0 应返回0")
        void nullOrZeroInput_returnsZero() {
            assertEquals(BigDecimal.ZERO, subsidyCalculationService.calculateSubsidyAmount(null, new BigDecimal("10")));
            assertEquals(BigDecimal.ZERO, subsidyCalculationService.calculateSubsidyAmount(55, null));
            assertEquals(BigDecimal.ZERO, subsidyCalculationService.calculateSubsidyAmount(55, BigDecimal.ZERO));
        }

        @Test
        @DisplayName("使用默认年平均工资56724时金额应大于0")
        void withDefaultSalary_amountNonZero() {
            when(configService.selectConfigByKey(eq(AVERAGE_SALARY_KEY))).thenReturn(null);
            BigDecimal amount = subsidyCalculationService.calculateSubsidyAmount(60, new BigDecimal("15"));
            assertTrue(amount.compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Nested
    @DisplayName("年平均工资 getAverageAnnualSalary")
    class GetAverageAnnualSalaryTests {

        @Test
        @DisplayName("有配置时返回配置值")
        void whenConfigSet_returnsConfigValue() {
            when(configService.selectConfigByKey(eq(AVERAGE_SALARY_KEY))).thenReturn("60000");
            assertEquals(new BigDecimal("60000"), subsidyCalculationService.getAverageAnnualSalary());
        }

        @Test
        @DisplayName("无配置时返回默认56724")
        void whenConfigEmpty_returnsDefault() {
            when(configService.selectConfigByKey(eq(AVERAGE_SALARY_KEY))).thenReturn("");
            assertEquals(new BigDecimal("56724"), subsidyCalculationService.getAverageAnnualSalary());
        }
    }
}
