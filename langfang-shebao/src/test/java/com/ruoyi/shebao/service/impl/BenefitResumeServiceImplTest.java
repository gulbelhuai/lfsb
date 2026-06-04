package com.ruoyi.shebao.service.impl;

import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.domain.BenefitDeterminationItem;
import com.ruoyi.shebao.domain.BenefitSuspensionItem;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.BenefitResumeCreateReq;
import com.ruoyi.shebao.mapper.BenefitDeterminationItemMapper;
import com.ruoyi.shebao.mapper.BenefitDeterminationMapper;
import com.ruoyi.shebao.mapper.BenefitResumeItemMapper;
import com.ruoyi.shebao.mapper.BenefitResumeMapper;
import com.ruoyi.shebao.mapper.BenefitSuspensionItemMapper;
import com.ruoyi.shebao.mapper.StreetOfficeMapper;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import com.ruoyi.shebao.mapper.VillageCommitteeMapper;
import com.ruoyi.shebao.support.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BenefitResumeServiceImplTest
{
    @Mock
    private BenefitResumeMapper benefitResumeMapper;
    @Mock
    private BenefitResumeItemMapper benefitResumeItemMapper;
    @Mock
    private BenefitSuspensionItemMapper benefitSuspensionItemMapper;
    @Mock
    private BenefitDeterminationMapper benefitDeterminationMapper;
    @Mock
    private BenefitDeterminationItemMapper benefitDeterminationItemMapper;
    @Mock
    private SubsidyPersonMapper subsidyPersonMapper;
    @Mock
    private StreetOfficeMapper streetOfficeMapper;
    @Mock
    private VillageCommitteeMapper villageCommitteeMapper;

    @InjectMocks
    private BenefitResumeServiceImpl benefitResumeService;

    @BeforeEach
    void setUp()
    {
        TestSecurityContext.setUser("admin");
    }

    @AfterEach
    void tearDown()
    {
        TestSecurityContext.clear();
    }

    @Test
    @DisplayName("恢复待遇时应关闭对应暂停明细的 pause_active")
    void create_resume_closesPauseActiveOnSuspensionItems()
    {
        BenefitDetermination determination = new BenefitDetermination();
        determination.setId(10L);
        determination.setSubsidyPersonId(20L);
        determination.setApprovalStatus("approved");
        determination.setDelFlag("0");

        SubsidyPerson person = new SubsidyPerson();
        person.setId(20L);
        person.setIdCardNo("130000199001011234");
        person.setDelFlag("0");

        BenefitDeterminationItem item = new BenefitDeterminationItem();
        item.setId(30L);
        item.setDeterminationId(10L);
        item.setSubsidyType("land_loss");
        item.setBenefitStatus("1");
        item.setDelFlag("0");

        when(benefitDeterminationMapper.selectById(10L)).thenReturn(determination);
        when(subsidyPersonMapper.selectById(20L)).thenReturn(person);
        when(benefitDeterminationItemMapper.selectList(any())).thenReturn(List.of(item));

        BenefitResumeCreateReq req = new BenefitResumeCreateReq();
        req.setDeterminationId(10L);
        req.setSubsidyPersonId(20L);
        req.setIdCardNo("130000199001011234");
        BenefitResumeCreateReq.Item resumeItem = new BenefitResumeCreateReq.Item();
        resumeItem.setDeterminationItemId(30L);
        resumeItem.setNeedResume("1");
        resumeItem.setResumeMonth("2026-05");
        resumeItem.setResumeReason("测试恢复");
        req.setItems(List.of(resumeItem));

        benefitResumeService.create(req);

        ArgumentCaptor<BenefitSuspensionItem> closeCaptor = ArgumentCaptor.forClass(BenefitSuspensionItem.class);
        verify(benefitSuspensionItemMapper).update(closeCaptor.capture(), any());
        assertEquals("0", closeCaptor.getValue().getPauseActive());
    }
}
