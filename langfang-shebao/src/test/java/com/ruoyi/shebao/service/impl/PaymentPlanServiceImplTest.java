package com.ruoyi.shebao.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.shebao.domain.PaymentPlan;
import com.ruoyi.shebao.domain.PaymentPlanAudit;
import com.ruoyi.shebao.dto.PaymentPlanDetailResp;
import com.ruoyi.shebao.dto.PaymentPlanFinanceStatusChangeReq;
import com.ruoyi.shebao.dto.PaymentPlanGenerateReq;
import com.ruoyi.shebao.dto.PaymentPlanPreviewReq;
import com.ruoyi.shebao.dto.PaymentPlanPreviewResp;
import com.ruoyi.shebao.mapper.PaymentPlanAuditMapper;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class PaymentPlanServiceImplTest {

    @Mock
    private PaymentPlanMapper paymentPlanMapper;
    @Mock
    private PaymentPlanSummaryMapper paymentPlanSummaryMapper;
    @Mock
    private PaymentPlanDetailMapper paymentPlanDetailMapper;
    @Mock
    private PaymentPlanAuditMapper paymentPlanAuditMapper;

    @InjectMocks
    private PaymentPlanServiceImpl paymentPlanService;

    @BeforeEach
    void setUp() {
        TestSecurityContext.setUser("admin");
        lenient().doAnswer(invocation -> {
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
        req.setSubsidyType("land_loss");

        PaymentPlanPreviewResp resp = paymentPlanService.preview(req);

        assertTrue(resp.getDetailList().isEmpty());
        assertEquals(0, resp.getTotalCount());
        verify(paymentPlanDetailMapper, never()).selectPreviewDetails(any(LocalDate.class), any(String.class));
    }

    @Test
    @DisplayName("正常发放预览应按补贴类型与机构汇总")
    void preview_normal_groupsSummary() {
        PaymentPlanPreviewReq req = new PaymentPlanPreviewReq();
        req.setDeterminationType("normal");
        req.setBusinessPeriod("2026-04");
        req.setSubsidyType("land_loss");

        PaymentPlanDetailResp d1 = new PaymentPlanDetailResp();
        d1.setSubsidyType("land_loss");
        d1.setGrantOrg("A");
        d1.setDistributionAmount(new BigDecimal("100"));

        PaymentPlanDetailResp d2 = new PaymentPlanDetailResp();
        d2.setSubsidyType("land_loss");
        d2.setGrantOrg("A");
        d2.setDistributionAmount(new BigDecimal("50"));

        when(paymentPlanDetailMapper.selectPreviewDetails(LocalDate.of(2026, 4, 1), "land_loss"))
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
        req.setSubsidyType("land_loss");

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

        when(paymentPlanDetailMapper.selectPreviewDetails(LocalDate.of(2026, 4, 1), "demolition"))
                .thenReturn(List.of(d));

        when(paymentPlanMapper.selectMaxBatchSeqSuffix("20260401")).thenReturn(0);

        PaymentPlanGenerateReq req = new PaymentPlanGenerateReq();
        req.setDeterminationType("normal");
        req.setBusinessPeriod("2026-04");
        req.setSubsidyType("demolition");

        Long id = paymentPlanService.generate(req);

        assertEquals(100L, id);
        ArgumentCaptor<PaymentPlan> planCaptor = ArgumentCaptor.forClass(PaymentPlan.class);
        verify(paymentPlanMapper).insert(planCaptor.capture());
        assertEquals("20260401001", planCaptor.getValue().getBatchNo());
        verify(paymentPlanMapper).selectMaxBatchSeqSuffix("20260401");
        verify(paymentPlanSummaryMapper).batchInsert(anyList());
        verify(paymentPlanDetailMapper).batchInsert(anyList());
        verify(paymentPlanAuditMapper).insert(any(PaymentPlanAudit.class));
    }

    @Test
    @DisplayName("审批通过且无财务状态时可上传财务")
    void submitFinanceUpload_setsPendingFinance() {
        PaymentPlan plan = new PaymentPlan();
        plan.setId(7L);
        plan.setDelFlag("0");
        plan.setApprovalStatus("approved");
        plan.setFinanceStatus(null);
        when(paymentPlanMapper.selectById(7L)).thenReturn(plan);
        when(paymentPlanMapper.updateById(any(PaymentPlan.class))).thenReturn(1);

        int rows = paymentPlanService.submitFinanceUpload(7L);

        assertEquals(1, rows);
        ArgumentCaptor<PaymentPlanAudit> auditCap = ArgumentCaptor.forClass(PaymentPlanAudit.class);
        verify(paymentPlanAuditMapper).insert(auditCap.capture());
        assertEquals("pending_finance", auditCap.getValue().getOperationStatus());
        assertEquals("finance", auditCap.getValue().getApprovalStage());
    }

    @Test
    @DisplayName("待财务可财务通过进入待复核")
    void financePass_fromPendingFinance() {
        PaymentPlan plan = new PaymentPlan();
        plan.setId(8L);
        plan.setDelFlag("0");
        plan.setFinanceStatus("pending_finance");
        when(paymentPlanMapper.selectById(8L)).thenReturn(plan);
        when(paymentPlanMapper.updateById(any(PaymentPlan.class))).thenReturn(1);

        int rows = paymentPlanService.financePass(8L, new PaymentPlanFinanceStatusChangeReq());

        assertEquals(1, rows);
        ArgumentCaptor<PaymentPlanAudit> cap = ArgumentCaptor.forClass(PaymentPlanAudit.class);
        verify(paymentPlanAuditMapper).insert(cap.capture());
        assertEquals("finance_pending_review", cap.getValue().getOperationStatus());
        assertEquals("finance", cap.getValue().getApprovalStage());
    }

    @Test
    @DisplayName("无可生成数据时应拒绝保存")
    void generate_empty_throws() {
        when(paymentPlanDetailMapper.selectPreviewDetails(LocalDate.of(2026, 4, 1), "land_loss"))
                .thenReturn(Collections.emptyList());

        PaymentPlanGenerateReq req = new PaymentPlanGenerateReq();
        req.setDeterminationType("normal");
        req.setBusinessPeriod("2026-04");
        req.setSubsidyType("land_loss");

        assertThrows(ServiceException.class, () -> paymentPlanService.generate(req));
    }

    @Test
    @DisplayName("财务已通过的批次可提交银行")
    void submitToBank_fromApproved() {
        PaymentPlan plan = new PaymentPlan();
        plan.setId(9L);
        plan.setDelFlag("0");
        plan.setFinanceStatus("finance_approved");
        plan.setDistributionStatus(null);
        when(paymentPlanMapper.selectById(9L)).thenReturn(plan);
        when(paymentPlanMapper.updateById(any(PaymentPlan.class))).thenReturn(1);

        int rows = paymentPlanService.submitToBank(9L);

        assertEquals(1, rows);
        ArgumentCaptor<PaymentPlan> cap = ArgumentCaptor.forClass(PaymentPlan.class);
        verify(paymentPlanMapper).updateById(cap.capture());
        assertEquals("submitted", cap.getValue().getDistributionStatus());
    }

    @Test
    @DisplayName("未提交银行的批次不可标记完成")
    void complete_requiresSubmitted() {
        PaymentPlan plan = new PaymentPlan();
        plan.setId(10L);
        plan.setDelFlag("0");
        plan.setFinanceStatus("finance_approved");
        plan.setDistributionStatus("pending");
        when(paymentPlanMapper.selectById(10L)).thenReturn(plan);

        assertThrows(ServiceException.class, () -> paymentPlanService.completeDistribution(10L));
    }

    @Test
    @DisplayName("已提交银行的批次标记完成时未失败明细记为成功")
    void complete_marksRemainingSuccess() {
        PaymentPlan plan = new PaymentPlan();
        plan.setId(11L);
        plan.setDelFlag("0");
        plan.setDistributionStatus("submitted");
        when(paymentPlanMapper.selectById(11L)).thenReturn(plan);
        when(paymentPlanMapper.updateById(any(PaymentPlan.class))).thenReturn(1);

        int rows = paymentPlanService.completeDistribution(11L);

        assertEquals(1, rows);
        verify(paymentPlanDetailMapper).markRemainingSuccess(11L);
        ArgumentCaptor<PaymentPlan> cap = ArgumentCaptor.forClass(PaymentPlan.class);
        verify(paymentPlanMapper).updateById(cap.capture());
        assertEquals("completed", cap.getValue().getDistributionStatus());
    }
}
