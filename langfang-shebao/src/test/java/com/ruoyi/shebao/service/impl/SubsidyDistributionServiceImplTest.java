package com.ruoyi.shebao.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.shebao.domain.ExpropriateeSubsidy;
import com.ruoyi.shebao.domain.SubsidyDistribution;
import com.ruoyi.shebao.enums.DistributionStatusEnum;
import com.ruoyi.shebao.mapper.DemolitionResidentMapper;
import com.ruoyi.shebao.mapper.ExpropriateeSubsidyMapper;
import com.ruoyi.shebao.mapper.LandLossResidentMapper;
import com.ruoyi.shebao.mapper.StreetOfficeMapper;
import com.ruoyi.shebao.mapper.SubsidyDistributionMapper;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import com.ruoyi.shebao.mapper.TeacherSubsidyMapper;
import com.ruoyi.shebao.mapper.VillageCommitteeMapper;
import com.ruoyi.shebao.mapper.VillageOfficialMapper;
import com.ruoyi.shebao.service.DistributionReviewService;
import com.ruoyi.shebao.support.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubsidyDistributionServiceImplTest {

    @Mock
    private SubsidyDistributionMapper subsidyDistributionMapper;

    @Mock
    private SubsidyPersonMapper subsidyPersonMapper;

    @Mock
    private LandLossResidentMapper landLossResidentMapper;

    @Mock
    private ExpropriateeSubsidyMapper expropriateeSubsidyMapper;

    @Mock
    private DemolitionResidentMapper demolitionResidentMapper;

    @Mock
    private VillageOfficialMapper villageOfficialMapper;

    @Mock
    private TeacherSubsidyMapper teacherSubsidyMapper;

    @Mock
    private DistributionReviewService distributionReviewService;

    @Mock
    private StreetOfficeMapper streetOfficeMapper;

    @Mock
    private VillageCommitteeMapper villageCommitteeMapper;

    @Mock
    private SubsidyPersonServiceImpl subsidyPersonService;

    private SubsidyDistributionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = spy(new SubsidyDistributionServiceImpl());
        ReflectionTestUtils.setField(service, "subsidyDistributionMapper", subsidyDistributionMapper);
        ReflectionTestUtils.setField(service, "subsidyPersonMapper", subsidyPersonMapper);
        ReflectionTestUtils.setField(service, "landLossResidentMapper", landLossResidentMapper);
        ReflectionTestUtils.setField(service, "expropriateeSubsidyMapper", expropriateeSubsidyMapper);
        ReflectionTestUtils.setField(service, "demolitionResidentMapper", demolitionResidentMapper);
        ReflectionTestUtils.setField(service, "villageOfficialMapper", villageOfficialMapper);
        ReflectionTestUtils.setField(service, "teacherSubsidyMapper", teacherSubsidyMapper);
        ReflectionTestUtils.setField(service, "distributionReviewService", distributionReviewService);
        ReflectionTestUtils.setField(service, "streetOfficeMapper", streetOfficeMapper);
        ReflectionTestUtils.setField(service, "villageCommitteeMapper", villageCommitteeMapper);
        ReflectionTestUtils.setField(service, "subsidyPersonService", subsidyPersonService);
        TestSecurityContext.setUser("tester");
    }

    @AfterEach
    void tearDown() {
        TestSecurityContext.clear();
    }

    @Test
    @DisplayName("已驳回记录重新提交时应刷新金额并回到待审核")
    void resubmitDistribution_shouldRefreshAmountAndMoveToPendingReview() {
        SubsidyDistribution distribution = new SubsidyDistribution();
        distribution.setId(1L);
        distribution.setSubsidyType("2");
        distribution.setSubsidyRecordId(99L);
        distribution.setDistributionStatus(DistributionStatusEnum.REJECTED.getCode());

        ExpropriateeSubsidy subsidy = new ExpropriateeSubsidy();
        subsidy.setId(99L);
        subsidy.setSubsidyAmount(new BigDecimal("1234.56"));

        doReturn(distribution).when(service).getById(1L);
        doReturn(true).when(service).updateById(any(SubsidyDistribution.class));
        when(expropriateeSubsidyMapper.selectById(99L)).thenReturn(subsidy);

        int result = service.resubmitDistribution(1L);

        assertEquals(1, result);
        assertEquals(DistributionStatusEnum.PENDING_REVIEW.getCode(), distribution.getDistributionStatus());
        assertEquals(new BigDecimal("1234.56"), distribution.getDistributionAmount());
        verify(distributionReviewService).addReviewRecord(1L, "5", "重新提交审核", "tester");
    }

    @Test
    @DisplayName("待审核记录审核通过后应变为待发放并记录审核意见")
    void approveDistribution_shouldMovePendingReviewToPendingDistribution() {
        SubsidyDistribution distribution = new SubsidyDistribution();
        distribution.setId(2L);
        distribution.setSubsidyType("2");
        distribution.setSubsidyRecordId(100L);
        distribution.setDistributionStatus(DistributionStatusEnum.PENDING_REVIEW.getCode());

        doReturn(distribution).when(service).getById(2L);
        doReturn(true).when(service).updateById(any(SubsidyDistribution.class));

        int result = service.approveDistribution(2L, "审核通过");

        assertEquals(1, result);
        assertEquals(DistributionStatusEnum.PENDING_DISTRIBUTION.getCode(), distribution.getDistributionStatus());
        assertEquals("审核通过", distribution.getReviewRemark());
        verify(distributionReviewService).addReviewRecord(2L, "2", "审核通过", "tester");
    }

    @Test
    @DisplayName("待发放记录驳回时应变为已拒绝")
    void rejectDistribution_shouldRejectPendingDistributionRecord() {
        SubsidyDistribution distribution = new SubsidyDistribution();
        distribution.setId(3L);
        distribution.setSubsidyType("2");
        distribution.setSubsidyRecordId(101L);
        distribution.setDistributionStatus(DistributionStatusEnum.PENDING_DISTRIBUTION.getCode());

        doReturn(distribution).when(service).getById(3L);
        doReturn(true).when(service).updateById(any(SubsidyDistribution.class));

        int result = service.rejectDistribution(3L, "资料不全");

        assertEquals(1, result);
        assertEquals(DistributionStatusEnum.REJECTED.getCode(), distribution.getDistributionStatus());
        assertEquals("资料不全", distribution.getReviewRemark());
        verify(distributionReviewService).addReviewRecord(3L, "3", "资料不全", "tester");
    }

    @Test
    @DisplayName("非待审核状态不允许审核通过")
    void approveDistribution_shouldRejectIllegalStatus() {
        SubsidyDistribution distribution = new SubsidyDistribution();
        distribution.setId(4L);
        distribution.setDistributionStatus(DistributionStatusEnum.DISTRIBUTED.getCode());

        doReturn(distribution).when(service).getById(4L);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.approveDistribution(4L, "非法操作"));

        assertEquals("只有待审核状态的记录才能审核", ex.getMessage());
    }
}
