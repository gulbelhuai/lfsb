package com.ruoyi.shebao.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.shebao.domain.PaymentPlan;
import com.ruoyi.shebao.dto.PaymentPlanDetailResp;
import com.ruoyi.shebao.dto.PaymentPlanGenerateReq;
import com.ruoyi.shebao.dto.PaymentPlanPreviewReq;
import com.ruoyi.shebao.dto.PaymentPlanPreviewResp;
import com.ruoyi.shebao.mapper.PaymentPlanDetailMapper;
import com.ruoyi.shebao.mapper.PaymentPlanMapper;
import com.ruoyi.shebao.mapper.PaymentPlanSummaryMapper;
import com.ruoyi.shebao.support.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentPlanServiceImplTest {

    @Mock
    private PaymentPlanMapper paymentPlanMapper;
    @Mock
    private PaymentPlanSummaryMapper paymentPlanSummaryMapper;
    @Mock
    private PaymentPlanDetailMapper paymentPlanDetailMapper;

    @InjectMocks
    private PaymentPlanServiceImpl paymentPlanService;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setUser("admin");
        doAnswer(invocation -> {
            PaymentPlan p = invocation.getArgument(0);
            p.setId(100L);
            return 1;
        }).when(paymentPlanMapper).insert(any(PaymentPlan.class));
    }

    @AfterEach
    void tearDown() {
        TestSecurityContext.clear();
    }

    @Test
    @DisplayName("二次发放预览应无明细")
    void preview_secondType_returnsEmptyDetails() {
        PaymentPlanPreviewReq req = new PaymentPlanPreviewReq();
        req.setDeterminationType("second");
        req.setBusinessPeriod("2026-04");

        PaymentPlanPreviewResp resp = paymentPlanService.preview(req);

        assertTrue(resp.getDetailList().isEmpty());
        assertEquals(0, resp.getTotalCount());
        verify(paymentPlanDetailMapper, never()).selectPreviewDetails(any(LocalDate.class));
    }

    @Test
    @DisplayName("正常发放预览应按补贴类型与机构汇总")
    void preview_normal_groupsSummary() {
        PaymentPlanPreviewReq req = new PaymentPlanPreviewReq();
        req.setDeterminationType("normal");
        req.setBusinessPeriod("2026-04");

        PaymentPlanDetailResp d1 = new PaymentPlanDetailResp();
        d1.setSubsidyType("land_loss");
        d1.setGrantOrg("A");
        d1.setDistributionAmount(new BigDecimal("100"));

        PaymentPlanDetailResp d2 = new PaymentPlanDetailResp();
        d2.setSubsidyType("land_loss");
        d2.setGrantOrg("A");
        d2.setDistributionAmount(new BigDecimal("50"));

        when(paymentPlanDetailMapper.selectPreviewDetails(LocalDate.of(2026, 4, 1)))
                .thenReturn(List.of(d1, d2));

        PaymentPlanPreviewResp resp = paymentPlanService.preview(req);

        assertEquals(2, resp.getTotalCount());
        assertEquals(new BigDecimal("150"), resp.getTotalAmount());
        assertEquals(1, resp.getSummaryList().size());
        assertEquals(2, resp.getSummaryList().get(0).getTotalCount());
        assertEquals(new BigDecimal("150"), resp.getSummaryList().get(0).getTotalAmount());
    }

    @Test
    @DisplayName("二次发放保存应拒绝")
    void generate_secondType_throws() {
        PaymentPlanGenerateReq req = new PaymentPlanGenerateReq();
        req.setDeterminationType("second");
        req.setBusinessPeriod("2026-04");

        assertThrows(ServiceException.class, () -> paymentPlanService.generate(req));
    }

    @Test
    @DisplayName("正常发放保存应写入主表与明细")
    void generate_normal_persists() {
        PaymentPlanDetailResp d = new PaymentPlanDetailResp();
        d.setSubsidyType("demolition");
        d.setGrantOrg("X");
        d.setDistributionAmount(new BigDecimal("200"));
        d.setDeterminationId(1L);
        d.setDeterminationItemId(2L);

        when(paymentPlanDetailMapper.selectPreviewDetails(LocalDate.of(2026, 4, 1)))
                .thenReturn(List.of(d));

        PaymentPlanGenerateReq req = new PaymentPlanGenerateReq();
        req.setDeterminationType("normal");
        req.setBusinessPeriod("2026-04");

        Long id = paymentPlanService.generate(req);

        assertEquals(100L, id);
        verify(paymentPlanMapper).insert(any(PaymentPlan.class));
        verify(paymentPlanSummaryMapper).batchInsert(anyList());
        verify(paymentPlanDetailMapper).batchInsert(anyList());
    }

    @Test
    @DisplayName("无可生成数据时应拒绝保存")
    void generate_empty_throws() {
        when(paymentPlanDetailMapper.selectPreviewDetails(LocalDate.of(2026, 4, 1)))
                .thenReturn(Collections.emptyList());

        PaymentPlanGenerateReq req = new PaymentPlanGenerateReq();
        req.setDeterminationType("normal");
        req.setBusinessPeriod("2026-04");

        assertThrows(ServiceException.class, () -> paymentPlanService.generate(req));
    }
}
