package com.ruoyi.shebao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.shebao.dto.PaymentPlanGenerateReq;
import com.ruoyi.shebao.dto.PaymentPlanPreviewReq;
import com.ruoyi.shebao.dto.PaymentPlanPreviewResp;
import com.ruoyi.shebao.service.PaymentPlanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentPlanControllerTest {

    @Mock
    private PaymentPlanService paymentPlanService;

    @Test
    @DisplayName("预览接口应返回成功结果")
    void preview_shouldReturnSuccess() {
        PaymentPlanController controller = new PaymentPlanController();
        ReflectionTestUtils.setField(controller, "paymentPlanService", paymentPlanService);
        when(paymentPlanService.preview(any())).thenReturn(new PaymentPlanPreviewResp());

        AjaxResult result = controller.preview(new PaymentPlanPreviewReq());

        assertEquals(200, result.get("code"));
    }

    @Test
    @DisplayName("生成接口应调用服务并返回成功")
    void generate_shouldReturnSuccess() {
        PaymentPlanController controller = new PaymentPlanController();
        ReflectionTestUtils.setField(controller, "paymentPlanService", paymentPlanService);
        when(paymentPlanService.generate(any())).thenReturn(5L);

        AjaxResult result = controller.generate(new PaymentPlanGenerateReq());

        assertEquals(200, result.get("code"));
        assertEquals("操作成功", result.get("msg"));
    }

    @Test
    @DisplayName("汇总接口应返回成功")
    void summary_shouldReturnSuccess() {
        PaymentPlanController controller = new PaymentPlanController();
        ReflectionTestUtils.setField(controller, "paymentPlanService", paymentPlanService);
        when(paymentPlanService.selectSummaryByPlanId(1L)).thenReturn(Collections.emptyList());

        AjaxResult result = controller.getSummary(1L);

        assertEquals(200, result.get("code"));
    }

    @Test
    @DisplayName("明细接口应返回表格数据")
    void detail_shouldReturnTableData() {
        PaymentPlanController controller = new PaymentPlanController();
        ReflectionTestUtils.setField(controller, "paymentPlanService", paymentPlanService);
        when(paymentPlanService.selectDetailByPlanId(1L, 1, 10, null, null)).thenReturn(new Page<>());

        TableDataInfo result = controller.getDetail(1L, 1, 10, null, null);

        assertEquals(200, result.getCode());
    }
}
