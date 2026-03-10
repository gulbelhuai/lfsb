package com.ruoyi.shebao.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.service.ApprovalLogService;
import com.ruoyi.shebao.service.LandLossResidentService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonRegistrationControllerTest {

    @Mock
    private LandLossResidentService landLossResidentService;

    @Mock
    private SubsidyPersonService subsidyPersonService;

    @Mock
    private ApprovalLogService approvalLogService;

    @Test
    @DisplayName("提交审核成功时应返回成功消息并记录审批日志")
    void submit_shouldReturnSuccessWhenStatusEditable() {
        PersonRegistrationController controller = new PersonRegistrationController();
        ReflectionTestUtils.setField(controller, "landLossResidentService", landLossResidentService);
        ReflectionTestUtils.setField(controller, "subsidyPersonService", subsidyPersonService);
        ReflectionTestUtils.setField(controller, "approvalLogService", approvalLogService);

        LandLossResident resident = new LandLossResident();
        resident.setId(1L);
        resident.setSubsidyPersonId(10L);

        SubsidyPerson person = new SubsidyPerson();
        person.setId(10L);
        person.setApprovalStatus("draft");

        when(landLossResidentService.getById(1L)).thenReturn(resident);
        when(subsidyPersonService.getById(10L)).thenReturn(person);
        when(subsidyPersonService.updateById(person)).thenReturn(true);

        AjaxResult result = controller.submit(1L, null);

        assertEquals(200, result.get("code"));
        assertEquals("提交审核成功", result.get("msg"));

        verify(approvalLogService).log(eq("person_register"), eq(1L), eq("pending_review"), eq("submit"), isNull());
    }

    @Test
    @DisplayName("当前状态不可提交时应返回错误")
    void submit_shouldReturnErrorWhenStatusIllegal() {
        PersonRegistrationController controller = new PersonRegistrationController();
        ReflectionTestUtils.setField(controller, "landLossResidentService", landLossResidentService);
        ReflectionTestUtils.setField(controller, "subsidyPersonService", subsidyPersonService);
        ReflectionTestUtils.setField(controller, "approvalLogService", approvalLogService);

        LandLossResident resident = new LandLossResident();
        resident.setId(2L);
        resident.setSubsidyPersonId(20L);

        SubsidyPerson person = new SubsidyPerson();
        person.setId(20L);
        person.setApprovalStatus("pending_review");

        when(landLossResidentService.getById(2L)).thenReturn(resident);
        when(subsidyPersonService.getById(20L)).thenReturn(person);

        AjaxResult result = controller.submit(2L, null);

        assertEquals(500, result.get("code"));
        assertEquals("当前状态不允许提交审核", result.get("msg"));
    }
}
