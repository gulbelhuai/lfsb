package com.ruoyi.shebao.service.impl;

import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.BenefitDeterminationPrintDto;
import com.ruoyi.shebao.mapper.DemolitionResidentMapper;
import com.ruoyi.shebao.mapper.ExpropriateeSubsidyMapper;
import com.ruoyi.shebao.mapper.LandLossResidentMapper;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BenefitDeterminationServiceImplTest {

    @Mock
    private SubsidyPersonMapper subsidyPersonMapper;

    @Mock
    private LandLossResidentMapper landLossResidentMapper;

    @Mock
    private DemolitionResidentMapper demolitionResidentMapper;

    @Mock
    private ExpropriateeSubsidyMapper expropriateeSubsidyMapper;

    private BenefitDeterminationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = spy(new BenefitDeterminationServiceImpl(
                subsidyPersonMapper,
                landLossResidentMapper,
                demolitionResidentMapper,
                expropriateeSubsidyMapper
        ));
    }

    @Test
    @DisplayName("新增核定时应按身份证反查人员并填充默认年月")
    void insertBenefitDetermination_shouldResolvePersonAndDerivedMonths() {
        SubsidyPerson person = buildPerson(100L, "张三", "110101199001011234", LocalDate.of(1965, 1, 15));
        BenefitDetermination determination = new BenefitDetermination();
        determination.setIdCardNo(person.getIdCardNo());
        determination.setSubsidyType("land_loss_resident");

        when(subsidyPersonMapper.selectOne(any())).thenReturn(person);
        when(subsidyPersonMapper.selectById(100L)).thenReturn(person);
        when(landLossResidentMapper.selectBySubsidyPersonId(100L)).thenReturn(List.of(buildLandLossResident(LocalDate.of(2024, 3, 1))));

        ArgumentCaptor<BenefitDetermination> captor = ArgumentCaptor.forClass(BenefitDetermination.class);
        doAnswer(invocation -> true).when(service).save(captor.capture());

        int result = service.insertBenefitDetermination(determination);

        assertEquals(1, result);
        BenefitDetermination saved = captor.getValue();
        assertEquals(100L, saved.getSubsidyPersonId());
        assertEquals(2025, saved.getEligibleYear());
        assertEquals(1, saved.getEligibleMonth());
        assertEquals(2025, saved.getBenefitStartYear());
        assertEquals(2, saved.getBenefitStartMonth());
        assertEquals("draft", saved.getApprovalStatus());
        assertEquals("0", saved.getDelFlag());
        assertTrue(saved.getCreateTime() != null);
    }

    @Test
    @DisplayName("事件月份晚于到龄次月时应取事件月份作为默认享受开始年月")
    void insertBenefitDetermination_shouldUseLaterEventMonthAsStartMonth() {
        SubsidyPerson person = buildPerson(101L, "李四", "110101196501011234", LocalDate.of(1965, 1, 1));
        BenefitDetermination determination = new BenefitDetermination();
        determination.setSubsidyPersonId(101L);
        determination.setSubsidyType("1");

        when(subsidyPersonMapper.selectById(101L)).thenReturn(person);
        when(landLossResidentMapper.selectBySubsidyPersonId(101L)).thenReturn(List.of(buildLandLossResident(LocalDate.of(2025, 5, 1))));

        ArgumentCaptor<BenefitDetermination> captor = ArgumentCaptor.forClass(BenefitDetermination.class);
        doAnswer(invocation -> true).when(service).save(captor.capture());

        int result = service.insertBenefitDetermination(determination);

        assertEquals(1, result);
        BenefitDetermination saved = captor.getValue();
        assertEquals(2025, saved.getBenefitStartYear());
        assertEquals(5, saved.getBenefitStartMonth());
    }

    @Test
    @DisplayName("打印 DTO 应拼装人员基础信息")
    void buildPrintDto_shouldMergePersonFields() {
        BenefitDetermination determination = new BenefitDetermination();
        determination.setId(1L);
        determination.setSubsidyPersonId(200L);
        determination.setSubsidyType("1");

        SubsidyPerson person = buildPerson(200L, "王五", "110101197001011234", LocalDate.of(1970, 1, 1));
        person.setUserCode("001-002-0001");

        doReturn(determination).when(service).getById(1L);
        when(subsidyPersonMapper.selectById(200L)).thenReturn(person);

        BenefitDeterminationPrintDto dto = service.buildPrintDto(1L);

        assertEquals("王五", dto.getName());
        assertEquals("110101197001011234", dto.getIdCardNo());
        assertEquals("001-002-0001", dto.getUserCode());
        assertEquals("1", dto.getSubsidyType());
    }

    @Test
    @DisplayName("人员生日为空时新增核定应失败")
    void insertBenefitDetermination_shouldFailWhenBirthdayMissing() {
        SubsidyPerson person = buildPerson(300L, "赵六", "110101198001011234", null);
        BenefitDetermination determination = new BenefitDetermination();
        determination.setSubsidyPersonId(300L);
        determination.setSubsidyType("1");

        when(subsidyPersonMapper.selectById(300L)).thenReturn(person);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.insertBenefitDetermination(determination));

        assertEquals("人员生日为空，无法计算到龄年月", ex.getMessage());
    }

    private SubsidyPerson buildPerson(Long id, String name, String idCardNo, LocalDate birthday) {
        SubsidyPerson person = new SubsidyPerson();
        person.setId(id);
        person.setName(name);
        person.setIdCardNo(idCardNo);
        person.setBirthday(birthday);
        person.setStatus("0");
        person.setIsAlive("1");
        return person;
    }

    private LandLossResident buildLandLossResident(LocalDate eventDate) {
        LandLossResident resident = new LandLossResident();
        resident.setLandRequisitionTime(eventDate);
        return resident;
    }
}
