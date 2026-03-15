package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.domain.BenefitNoticeBatch;
import com.ruoyi.shebao.domain.BenefitNoticeDetail;
import com.ruoyi.shebao.domain.DemolitionResident;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.domain.VillageOfficial;
import com.ruoyi.shebao.dto.BenefitNoticeBatchListReq;
import com.ruoyi.shebao.dto.BenefitNoticeBatchResp;
import com.ruoyi.shebao.dto.BenefitNoticeDetailListReq;
import com.ruoyi.shebao.dto.BenefitNoticeDetailResp;
import com.ruoyi.shebao.dto.BenefitNoticeExportRow;
import com.ruoyi.shebao.dto.BenefitNoticeGenerateReq;
import com.ruoyi.shebao.mapper.BenefitNoticeBatchMapper;
import com.ruoyi.shebao.mapper.BenefitNoticeDetailMapper;
import com.ruoyi.shebao.mapper.DemolitionResidentMapper;
import com.ruoyi.shebao.mapper.LandLossResidentMapper;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import com.ruoyi.shebao.mapper.VillageOfficialMapper;
import com.ruoyi.shebao.service.IBenefitDeterminationService;
import com.ruoyi.shebao.service.IBenefitNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BenefitNoticeServiceImpl implements IBenefitNoticeService
{
    private final BenefitNoticeBatchMapper benefitNoticeBatchMapper;
    private final BenefitNoticeDetailMapper benefitNoticeDetailMapper;
    private final SubsidyPersonMapper subsidyPersonMapper;
    private final LandLossResidentMapper landLossResidentMapper;
    private final DemolitionResidentMapper demolitionResidentMapper;
    private final VillageOfficialMapper villageOfficialMapper;
    private final IBenefitDeterminationService benefitDeterminationService;

    @Override
    public Page<BenefitNoticeBatchResp> selectBatchPage(BenefitNoticeBatchListReq req)
    {
        Page<BenefitNoticeBatchResp> page = new Page<>(req.getPageNum(), req.getPageSize());
        return benefitNoticeBatchMapper.selectBatchPage(page, req);
    }

    @Override
    public BenefitNoticeBatchResp selectBatchByBatchNo(String batchNo)
    {
        return benefitNoticeBatchMapper.selectBatchByBatchNo(batchNo);
    }

    @Override
    public List<BenefitNoticeDetailResp> selectDetailList(BenefitNoticeDetailListReq req)
    {
        return benefitNoticeBatchMapper.selectDetailList(req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BenefitNoticeBatch generateNoticeBatch(BenefitNoticeGenerateReq req)
    {
        String noticeMonth = StringUtils.isBlank(req.getNoticeMonth()) ? YearMonth.now().toString() : req.getNoticeMonth();
        BenefitNoticeBatch existed = benefitNoticeBatchMapper.selectOne(new LambdaQueryWrapper<BenefitNoticeBatch>()
                .eq(BenefitNoticeBatch::getNoticeMonth, noticeMonth)
                .eq(BenefitNoticeBatch::getDelFlag, "0")
                .last("limit 1"));
        if (existed != null)
        {
            refreshBatchStatistics(existed.getBatchNo());
            return existed;
        }

        Integer ageThreshold = req.getAgeThreshold() == null ? 60 : req.getAgeThreshold();
        YearMonth noticeYm = YearMonth.parse(noticeMonth);
        YearMonth retirementYm = noticeYm.plusMonths(3);

        List<SubsidyPerson> persons = subsidyPersonMapper.selectList(new LambdaQueryWrapper<SubsidyPerson>()
                .eq(SubsidyPerson::getDelFlag, "0")
                .eq(SubsidyPerson::getIsAlive, "1")
                .eq(SubsidyPerson::getApprovalStatus, "approved"));

        List<SubsidyPerson> eligiblePersons = new ArrayList<>();
        for (SubsidyPerson person : persons)
        {
            if (person.getBirthday() == null)
            {
                continue;
            }
            YearMonth personRetireYm = YearMonth.from(person.getBirthday().plusYears(ageThreshold));
            if (retirementYm.equals(personRetireYm))
            {
                eligiblePersons.add(person);
            }
        }

        BenefitNoticeBatch batch = new BenefitNoticeBatch();
        batch.setBatchNo(buildBatchNo(noticeYm));
        batch.setNoticeMonth(noticeMonth);
        batch.setRetirementMonth(retirementYm.toString());
        batch.setAgeThreshold(ageThreshold);
        batch.setBatchStatus("generated");
        batch.setTotalCount(0);
        batch.setPendingReviewCount(0);
        batch.setApprovedCount(0);
        batch.setRejectedCount(0);
        batch.setCreateBy(SecurityUtils.getUsername());
        batch.setUpdateBy(SecurityUtils.getUsername());
        benefitNoticeBatchMapper.insert(batch);

        for (SubsidyPerson person : eligiblePersons)
        {
            String subsidyType = resolveEligibleSubsidyType(person.getId());
            if (StringUtils.isBlank(subsidyType))
            {
                continue;
            }
            BenefitNoticeDetail detail = new BenefitNoticeDetail();
            detail.setNoticeBatchId(batch.getId());
            detail.setBatchNo(batch.getBatchNo());
            detail.setSubsidyPersonId(person.getId());
            detail.setUserCode(person.getUserCode());
            detail.setName(person.getName());
            detail.setIdCardNo(person.getIdCardNo());
            detail.setSubsidyType(subsidyType);
            detail.setBirthday(person.getBirthday());
            detail.setRetirementDate(person.getBirthday().plusYears(ageThreshold));
            detail.setDeterminationStatus("draft");
            detail.setMaterialStatus("pending_upload");
            detail.setReviewStatus("draft");
            detail.setCreateBy(SecurityUtils.getUsername());
            detail.setUpdateBy(SecurityUtils.getUsername());
            benefitNoticeDetailMapper.insert(detail);

            BenefitDetermination determination = new BenefitDetermination();
            determination.setSubsidyPersonId(person.getId());
            determination.setNoticeBatchNo(batch.getBatchNo());
            determination.setNoticeDetailId(detail.getId());
            determination.setSubsidyType(detail.getSubsidyType());
            determination.setIdCardNo(person.getIdCardNo());
            determination.setMaterialStatus("pending_upload");
            benefitDeterminationService.insertBenefitDetermination(determination);

            detail.setDeterminationId(determination.getId());
            benefitNoticeDetailMapper.updateById(detail);
        }

        refreshBatchStatistics(batch.getBatchNo());
        return batch;
    }

    @Override
    public List<BenefitNoticeExportRow> selectExportRows(String noticeMonth, String batchNo)
    {
        return benefitNoticeBatchMapper.selectExportRows(noticeMonth, batchNo);
    }

    @Override
    public void refreshBatchStatistics(String batchNo)
    {
        if (StringUtils.isBlank(batchNo))
        {
            return;
        }
        int total = countDetails(batchNo, null);
        int pendingReview = countDetails(batchNo, "pending_review");
        int approved = countDetails(batchNo, "approved");
        int rejected = countDetails(batchNo, "rejected");
        String status = "generated";
        if (approved + rejected > 0)
        {
            status = approved + rejected >= total ? "completed" : "processing";
        }
        benefitNoticeBatchMapper.update(null, new LambdaUpdateWrapper<BenefitNoticeBatch>()
                .eq(BenefitNoticeBatch::getBatchNo, batchNo)
                .set(BenefitNoticeBatch::getTotalCount, total)
                .set(BenefitNoticeBatch::getPendingReviewCount, pendingReview)
                .set(BenefitNoticeBatch::getApprovedCount, approved)
                .set(BenefitNoticeBatch::getRejectedCount, rejected)
                .set(BenefitNoticeBatch::getBatchStatus, status)
                .set(BenefitNoticeBatch::getUpdateBy, SecurityUtils.getUsername())
                .set(BenefitNoticeBatch::getUpdateTime, java.time.LocalDateTime.now()));
    }

    private int countDetails(String batchNo, String status)
    {
        LambdaQueryWrapper<BenefitNoticeDetail> wrapper = new LambdaQueryWrapper<BenefitNoticeDetail>()
                .eq(BenefitNoticeDetail::getBatchNo, batchNo)
                .eq(BenefitNoticeDetail::getDelFlag, "0");
        if (StringUtils.isNotBlank(status))
        {
            wrapper.eq(BenefitNoticeDetail::getDeterminationStatus, status);
        }
        return Math.toIntExact(benefitNoticeDetailMapper.selectCount(wrapper));
    }

    private String buildBatchNo(YearMonth noticeYm)
    {
        return "NT" + noticeYm.toString().replace("-", "") + System.currentTimeMillis() % 100000;
    }

    private String resolveEligibleSubsidyType(Long subsidyPersonId)
    {
        List<LandLossResident> landLossResidents = landLossResidentMapper.selectBySubsidyPersonId(subsidyPersonId);
        if (!landLossResidents.isEmpty())
        {
            return "land_loss_resident";
        }
        List<DemolitionResident> demolitionResidents = demolitionResidentMapper.selectBySubsidyPersonId(subsidyPersonId);
        if (!demolitionResidents.isEmpty())
        {
            return "demolition_resident";
        }
        List<VillageOfficial> villageOfficials = villageOfficialMapper.selectBySubsidyPersonId(subsidyPersonId);
        if (!villageOfficials.isEmpty())
        {
            return "village_official";
        }
        return null;
    }
}
