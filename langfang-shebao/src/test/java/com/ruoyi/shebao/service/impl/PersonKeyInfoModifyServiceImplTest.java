package com.ruoyi.shebao.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.shebao.domain.PersonKeyInfoModify;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyFormDto;
import com.ruoyi.shebao.mapper.PersonKeyInfoModifyMapper;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import com.ruoyi.shebao.support.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonKeyInfoModifyServiceImplTest {

    @Mock
    private PersonKeyInfoModifyMapper personKeyInfoModifyMapper;

    @Mock
    private SubsidyPersonMapper subsidyPersonMapper;

    private PersonKeyInfoModifyServiceImpl service;

    @BeforeEach
    void setUp() {
        service = spy(new PersonKeyInfoModifyServiceImpl(personKeyInfoModifyMapper, subsidyPersonMapper));
        TestSecurityContext.setUser("tester");
    }

    @AfterEach
    void tearDown() {
        TestSecurityContext.clear();
    }

    @Test
    @DisplayName("仅草稿或已驳回状态可保存")
    void saveOrUpdateDraft_shouldRejectNonEditableStatus() {
        PersonKeyInfoModifyFormDto form = new PersonKeyInfoModifyFormDto();
        form.setId(10L);

        PersonKeyInfoModify existing = new PersonKeyInfoModify();
        existing.setId(10L);
        existing.setApprovalStatus("pending_review");

        doReturn(existing).when(service).getById(10L);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.saveOrUpdateDraft(form));

        assertEquals("仅草稿或已驳回状态可保存", ex.getMessage());
    }

    @Test
    @DisplayName("驳回记录重新提交时应进入待复核并清空驳回原因")
    void submit_shouldMoveRejectedRecordToPendingReview() {
        PersonKeyInfoModify entity = new PersonKeyInfoModify();
        entity.setId(20L);
        entity.setApprovalStatus("rejected");
        entity.setRejectReason("旧驳回原因");

        doReturn(entity).when(service).getById(20L);
        doReturn(true).when(service).updateById(any(PersonKeyInfoModify.class));

        int result = service.submit(20L);

        assertEquals(1, result);
        assertEquals("pending_review", entity.getApprovalStatus());
        assertEquals("tester", entity.getSubmitBy());
        assertEquals("tester", entity.getUpdateBy());
        assertEquals(null, entity.getRejectReason());
        assertTrue(entity.getSubmitTime() != null);
    }

    @Test
    @DisplayName("复核通过时应流转到待审批")
    void review_shouldMovePendingReviewToPendingApprove() {
        PersonKeyInfoModify entity = new PersonKeyInfoModify();
        entity.setId(30L);
        entity.setApprovalStatus("pending_review");

        doReturn(entity).when(service).getById(30L);
        doReturn(true).when(service).updateById(any(PersonKeyInfoModify.class));

        int result = service.review(30L, true, "复核通过");

        assertEquals(1, result);
        assertEquals("pending_approve", entity.getApprovalStatus());
        assertEquals("tester", entity.getReviewBy());
        assertTrue(entity.getReviewTime() != null);
    }

    @Test
    @DisplayName("审批通过后应回写被补贴人关键信息")
    void approve_shouldUpdateSubsidyPersonWhenApproved() {
        PersonKeyInfoModify entity = new PersonKeyInfoModify();
        entity.setId(40L);
        entity.setSubsidyPersonId(88L);
        entity.setName("新姓名");
        entity.setIdCardNo("110101199901019999");
        entity.setHouseholdRegistration("新户籍");
        entity.setStreetOfficeId(11L);
        entity.setVillageCommitteeId(22L);
        entity.setApprovalStatus("pending_approve");

        SubsidyPerson person = new SubsidyPerson();
        person.setId(88L);
        person.setName("旧姓名");
        person.setIdCardNo("旧身份证");

        doReturn(entity).when(service).getById(40L);
        doReturn(true).when(service).updateById(any(PersonKeyInfoModify.class));
        when(subsidyPersonMapper.selectById(88L)).thenReturn(person);

        ArgumentCaptor<SubsidyPerson> personCaptor = ArgumentCaptor.forClass(SubsidyPerson.class);

        int result = service.approve(40L, true, "审批通过");

        assertEquals(1, result);
        assertEquals("approved", entity.getApprovalStatus());
        assertEquals("tester", entity.getApproveBy());
        verify(subsidyPersonMapper).updateById(personCaptor.capture());

        SubsidyPerson updated = personCaptor.getValue();
        assertEquals("新姓名", updated.getName());
        assertEquals("110101199901019999", updated.getIdCardNo());
        assertEquals("新户籍", updated.getHouseholdRegistration());
        assertEquals(11L, updated.getStreetOfficeId());
        assertEquals(22L, updated.getVillageCommitteeId());
        assertEquals("tester", updated.getUpdateBy());
        assertTrue(updated.getUpdateTime() != null);
    }

    @Test
    @DisplayName("基本信息变更复核通过应直接已通过并回写非关键字段")
    void review_basicApproved_shouldApproveAndPatchPerson() {
        PersonKeyInfoModify entity = new PersonKeyInfoModify();
        entity.setId(50L);
        entity.setSubsidyPersonId(99L);
        entity.setModifyType("basic");
        entity.setApprovalStatus("pending_review");
        entity.setHouseholdRegistration("新户籍");
        entity.setStreetOfficeId(5L);
        entity.setVillageCommitteeId(6L);
        entity.setHomeAddress("住址");
        entity.setPhone("13800000000");

        SubsidyPerson person = new SubsidyPerson();
        person.setId(99L);

        doReturn(entity).when(service).getById(50L);
        doReturn(true).when(service).updateById(any(PersonKeyInfoModify.class));
        when(subsidyPersonMapper.selectById(99L)).thenReturn(person);

        ArgumentCaptor<SubsidyPerson> personCaptor = ArgumentCaptor.forClass(SubsidyPerson.class);

        int result = service.review(50L, true, "ok");

        assertEquals(1, result);
        assertEquals("approved", entity.getApprovalStatus());
        verify(subsidyPersonMapper).updateById(personCaptor.capture());
        SubsidyPerson updated = personCaptor.getValue();
        assertEquals("新户籍", updated.getHouseholdRegistration());
        assertEquals(5L, updated.getStreetOfficeId());
        assertEquals(6L, updated.getVillageCommitteeId());
        assertEquals("住址", updated.getHomeAddress());
        assertEquals("13800000000", updated.getPhone());
    }

    @Test
    @DisplayName("关键信息审批驳回应为已驳回且不更新人员")
    void approve_reject_shouldBeRejected() {
        PersonKeyInfoModify entity = new PersonKeyInfoModify();
        entity.setId(60L);
        entity.setModifyType("key");
        entity.setApprovalStatus("pending_approve");
        entity.setSubsidyPersonId(1L);

        doReturn(entity).when(service).getById(60L);
        doReturn(true).when(service).updateById(any(PersonKeyInfoModify.class));

        int result = service.approve(60L, false, "打回");

        assertEquals(1, result);
        assertEquals("rejected", entity.getApprovalStatus());
        assertEquals("打回", entity.getRejectReason());
        verify(subsidyPersonMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("同一人员已有未办结申请时不允许再保存新草稿")
    void saveOrUpdateDraft_new_shouldRejectWhenUnfinishedExists() {
        PersonKeyInfoModifyFormDto form = new PersonKeyInfoModifyFormDto();
        form.setModifyType("key");
        form.setSubsidyPersonId(77L);
        form.setName("n");
        form.setIdCardNo("110101199001011234");

        doReturn(1L).when(service).count(any());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.saveOrUpdateDraft(form));

        assertEquals("存在未完成的业务", ex.getMessage());
    }

    @Test
    @DisplayName("关键信息提交时若身份证已被他人占用应拦截")
    void submit_keyShouldRejectDuplicateIdCard() {
        PersonKeyInfoModify entity = new PersonKeyInfoModify();
        entity.setId(70L);
        entity.setModifyType("key");
        entity.setSubsidyPersonId(200L);
        entity.setIdCardNo("110101199001011111");
        entity.setApprovalStatus("draft");

        SubsidyPerson duplicated = new SubsidyPerson();
        duplicated.setId(201L);
        duplicated.setIdCardNo("110101199001011111");

        doReturn(entity).when(service).getById(70L);
        when(subsidyPersonMapper.selectOne(any())).thenReturn(duplicated);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.submit(70L));

        assertEquals("相同的身份证号码已存在", ex.getMessage());
    }
}
