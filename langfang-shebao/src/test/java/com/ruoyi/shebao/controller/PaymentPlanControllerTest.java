package com.ruoyi.shebao.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.shebao.dto.SubsidyDistributionFormDto;
import com.ruoyi.shebao.service.SubsidyDistributionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentPlanControllerTest {

    @Mock
    private SubsidyDistributionService subsidyDistributionService;

    @Test
    @DisplayName("生成支付计划接口应返回成功结果")
    void generate_shouldReturnSuccess() {
        PaymentPlanController controller = new PaymentPlanController();
        ReflectionTestUtils.setField(controller, "subsidyDistributionService", subsidyDistributionService);
        when(subsidyDistributionService.insertSubsidyDistribution(any())).thenReturn(1);

        SubsidyDistributionFormDto formDto = new SubsidyDistributionFormDto();
        formDto.setSubsidyPersonId(1L);
        formDto.setSubsidyType("2");
        formDto.setSubsidyRecordId(10L);
        formDto.setDistributionAmount(new BigDecimal("1000.00"));

        AjaxResult result = controller.generate(formDto);

        assertEquals(200, result.get("code"));
        assertEquals("操作成功", result.get("msg"));
    }

    @Test
    @DisplayName("审核通过接口应调用服务并返回成功")
    void approve_shouldReturnSuccess() {
        PaymentPlanController controller = new PaymentPlanController();
        ReflectionTestUtils.setField(controller, "subsidyDistributionService", subsidyDistributionService);
        when(subsidyDistributionService.approveDistribution(5L, "审核通过")).thenReturn(1);

        AjaxResult result = controller.approve(5L, "审核通过");

        assertEquals(200, result.get("code"));
        assertEquals("操作成功", result.get("msg"));
    }

    @Test
    @DisplayName("驳回接口应调用服务并返回成功")
    void reject_shouldReturnSuccess() {
        PaymentPlanController controller = new PaymentPlanController();
        ReflectionTestUtils.setField(controller, "subsidyDistributionService", subsidyDistributionService);
        when(subsidyDistributionService.rejectDistribution(eq(6L), eq("资料不全"))).thenReturn(1);

        AjaxResult result = controller.reject(6L, "资料不全");

        assertEquals(200, result.get("code"));
        assertEquals("操作成功", result.get("msg"));
    }
}
