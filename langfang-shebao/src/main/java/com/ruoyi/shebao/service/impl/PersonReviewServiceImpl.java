package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.constant.SubsidyApprovalStatus;
import com.ruoyi.shebao.domain.DemolitionResident;
import com.ruoyi.shebao.domain.ExpropriateeSubsidy;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.domain.VillageOfficial;
import com.ruoyi.shebao.dto.DemolitionResidentDto;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyDto;
import com.ruoyi.shebao.dto.LandLossResidentDto;
import com.ruoyi.shebao.dto.PersonReviewListReq;
import com.ruoyi.shebao.dto.PersonReviewListResp;
import com.ruoyi.shebao.dto.ResidentDetailInfoDto;
import com.ruoyi.shebao.dto.SubsidyInfoDto;
import com.ruoyi.shebao.dto.VillageOfficialDto;
import com.ruoyi.shebao.mapper.DemolitionResidentMapper;
import com.ruoyi.shebao.mapper.ExpropriateeSubsidyMapper;
import com.ruoyi.shebao.mapper.LandLossResidentMapper;
import com.ruoyi.shebao.mapper.PersonReviewMapper;
import com.ruoyi.shebao.domain.TeacherSubsidy;
import com.ruoyi.shebao.mapper.TeacherSubsidyMapper;
import com.ruoyi.shebao.mapper.VillageOfficialMapper;
import com.ruoyi.shebao.service.ApprovalLogService;
import com.ruoyi.shebao.service.PersonReviewService;
import com.ruoyi.shebao.service.ResidentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonReviewServiceImpl implements PersonReviewService
{
    private final PersonReviewMapper personReviewMapper;
    private final LandLossResidentMapper landLossResidentMapper;
    private final ExpropriateeSubsidyMapper expropriateeSubsidyMapper;
    private final DemolitionResidentMapper demolitionResidentMapper;
    private final VillageOfficialMapper villageOfficialMapper;
    private final TeacherSubsidyMapper teacherSubsidyMapper;
    private final ApprovalLogService approvalLogService;
    private final ResidentQueryService residentQueryService;

    @Override
    public Page<PersonReviewListResp> selectPersonReviewList(PersonReviewListReq req)
    {
        if (StringUtils.isBlank(req.getApprovalStatus()))
        {
            req.setApprovalStatus(SubsidyApprovalStatus.PENDING_REVIEW);
        }
        Page<PersonReviewListResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return personReviewMapper.selectPersonReviewList(page, req);
    }

    @Override
    public ResidentDetailInfoDto getReviewDetail(String subsidyType, Long recordId)
    {
        Long subsidyPersonId = resolveSubsidyPersonId(subsidyType, recordId);
        ResidentDetailInfoDto detail = new ResidentDetailInfoDto();
        detail.setResidentInfo(residentQueryService.getResidentBasicInfo(subsidyPersonId));
        detail.setSubsidyInfo(buildSingleSubsidyInfo(subsidyType, recordId));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(String subsidyType, Long recordId, String remark)
    {
        updateReviewStatus(subsidyType, recordId, SubsidyApprovalStatus.APPROVED);
        approvalLogService.log("person_register", recordId, SubsidyApprovalStatus.APPROVED, "approve", remark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(String subsidyType, Long recordId, String reason)
    {
        updateReviewStatus(subsidyType, recordId, SubsidyApprovalStatus.REJECTED);
        approvalLogService.log("person_register", recordId, SubsidyApprovalStatus.REJECTED, "reject", reason);
    }

    private void updateReviewStatus(String subsidyType, Long recordId, String targetStatus)
    {
        if (StringUtils.isBlank(subsidyType) || recordId == null)
        {
            throw new ServiceException("补贴类型或记录ID不能为空");
        }
        String currentStatus = getCurrentStatus(subsidyType, recordId);
        if (!SubsidyApprovalStatus.PENDING_REVIEW.equals(currentStatus))
        {
            throw new ServiceException("当前状态不允许复核");
        }
        LocalDateTime now = LocalDateTime.now();
        String username = SecurityUtils.getUsername();
        switch (subsidyType)
        {
            case "land_loss_resident" -> {
                LandLossResident entity = new LandLossResident();
                entity.setId(recordId);
                entity.setApprovalStatus(targetStatus);
                entity.setUpdateTime(now);
                entity.setUpdateBy(username);
                landLossResidentMapper.updateById(entity);
            }
            case "expropriatee" -> {
                ExpropriateeSubsidy entity = new ExpropriateeSubsidy();
                entity.setId(recordId);
                entity.setApprovalStatus(targetStatus);
                entity.setUpdateTime(now);
                entity.setUpdateBy(username);
                expropriateeSubsidyMapper.updateById(entity);
            }
            case "demolition_resident" -> {
                DemolitionResident entity = new DemolitionResident();
                entity.setId(recordId);
                entity.setApprovalStatus(targetStatus);
                entity.setUpdateTime(now);
                entity.setUpdateBy(username);
                demolitionResidentMapper.updateById(entity);
            }
            case "village_official" -> {
                VillageOfficial entity = new VillageOfficial();
                entity.setId(recordId);
                entity.setApprovalStatus(targetStatus);
                entity.setUpdateTime(now);
                entity.setUpdateBy(username);
                villageOfficialMapper.updateById(entity);
            }
            case "teacher" -> {
                TeacherSubsidy entity = new TeacherSubsidy();
                entity.setId(recordId);
                entity.setApprovalStatus(targetStatus);
                entity.setUpdateTime(now);
                entity.setUpdateBy(username);
                teacherSubsidyMapper.updateById(entity);
            }
            default -> throw new ServiceException("不支持的补贴类型: " + subsidyType);
        }
    }

    private Long resolveSubsidyPersonId(String subsidyType, Long recordId)
    {
        return switch (subsidyType)
        {
            case "land_loss_resident" -> {
                LandLossResident r = landLossResidentMapper.selectById(recordId);
                if (r == null || !"0".equals(r.getDelFlag()))
                {
                    throw new ServiceException("失地居民登记记录不存在");
                }
                yield r.getSubsidyPersonId();
            }
            case "expropriatee" -> {
                ExpropriateeSubsidy r = expropriateeSubsidyMapper.selectById(recordId);
                if (r == null || !"0".equals(r.getDelFlag()))
                {
                    throw new ServiceException("被征地参保补贴记录不存在");
                }
                yield r.getSubsidyPersonId();
            }
            case "demolition_resident" -> {
                DemolitionResident r = demolitionResidentMapper.selectById(recordId);
                if (r == null || !"0".equals(r.getDelFlag()))
                {
                    throw new ServiceException("拆迁居民登记记录不存在");
                }
                yield r.getSubsidyPersonId();
            }
            case "village_official" -> {
                VillageOfficial r = villageOfficialMapper.selectById(recordId);
                if (r == null || !"0".equals(r.getDelFlag()))
                {
                    throw new ServiceException("村干部登记记录不存在");
                }
                yield r.getSubsidyPersonId();
            }
            case "teacher" -> {
                TeacherSubsidy r = teacherSubsidyMapper.selectById(recordId);
                if (r == null || !"0".equals(r.getDelFlag()))
                {
                    throw new ServiceException("教龄补助登记记录不存在");
                }
                yield r.getSubsidyPersonId();
            }
            default -> throw new ServiceException("不支持的补贴类型: " + subsidyType);
        };
    }

    private SubsidyInfoDto buildSingleSubsidyInfo(String subsidyType, Long recordId)
    {
        SubsidyInfoDto info = new SubsidyInfoDto();
        info.setLandLossResidents(Collections.emptyList());
        info.setExpropriateeSubsidies(Collections.emptyList());
        info.setDemolitionResidents(Collections.emptyList());
        info.setVillageOfficials(Collections.emptyList());
        switch (subsidyType)
        {
            case "land_loss_resident" -> {
                LandLossResident resident = landLossResidentMapper.selectById(recordId);
                if (resident == null || !"0".equals(resident.getDelFlag()))
                {
                    throw new ServiceException("失地居民登记记录不存在");
                }
                LandLossResidentDto dto = new LandLossResidentDto();
                dto.setLandRequisitionBatch(resident.getLandRequisitionBatch());
                dto.setVillageStreet(resident.getVillageStreet());
                dto.setRecognitionTime(resident.getRecognitionTime());
                dto.setLandRequisitionTime(resident.getLandRequisitionTime());
                dto.setCompensationCompleteTime(resident.getCompensationCompleteTime());
                dto.setRemark(resident.getRemark());
                dto.setCreateTime(resident.getCreateTime());
                info.setLandLossResidents(List.of(dto));
            }
            case "expropriatee" -> {
                ExpropriateeSubsidy subsidy = expropriateeSubsidyMapper.selectById(recordId);
                if (subsidy == null || !"0".equals(subsidy.getDelFlag()))
                {
                    throw new ServiceException("被征地参保补贴记录不存在");
                }
                ExpropriateeSubsidyDto dto = new ExpropriateeSubsidyDto();
                dto.setLandRequisitionBatch(subsidy.getLandRequisitionBatch());
                dto.setVillageStreet(subsidy.getVillageStreet());
                dto.setBaseDate(subsidy.getBaseDate());
                dto.setEmployeePensionMonths(subsidy.getEmployeePensionMonths());
                dto.setFlexibleEmploymentMonths(subsidy.getFlexibleEmploymentMonths());
                dto.setDifficultySubsidyMonths(subsidy.getDifficultySubsidyMonths());
                dto.setAgeAtBaseDate(subsidy.getAgeAtBaseDate());
                dto.setSubsidyYears(subsidy.getSubsidyYears() != null ? subsidy.getSubsidyYears().intValue() : null);
                dto.setSubsidyAmount(subsidy.getSubsidyAmount());
                dto.setJoinUrbanRuralInsurance(subsidy.getJoinUrbanRuralInsurance());
                dto.setJoinEmployeePension(subsidy.getJoinEmployeePension());
                dto.setHasEmployeePension(subsidy.getHasEmployeePension());
                dto.setRemark(subsidy.getRemark());
                dto.setCreateTime(subsidy.getCreateTime());
                info.setExpropriateeSubsidies(List.of(dto));
            }
            case "demolition_resident" -> {
                DemolitionResident resident = demolitionResidentMapper.selectById(recordId);
                if (resident == null || !"0".equals(resident.getDelFlag()))
                {
                    throw new ServiceException("拆迁居民登记记录不存在");
                }
                DemolitionResidentDto dto = new DemolitionResidentDto();
                dto.setVillageStreet(resident.getVillageStreet());
                dto.setRecognitionTime(resident.getRecognitionTime());
                dto.setDemolitionTime(resident.getDemolitionTime());
                dto.setDemolitionReason(resident.getDemolitionReason());
                dto.setRemark(resident.getRemark());
                dto.setCreateTime(resident.getCreateTime());
                info.setDemolitionResidents(List.of(dto));
            }
            case "village_official" -> {
                VillageOfficial official = villageOfficialMapper.selectById(recordId);
                if (official == null || !"0".equals(official.getDelFlag()))
                {
                    throw new ServiceException("村干部登记记录不存在");
                }
                VillageOfficialDto dto = new VillageOfficialDto();
                dto.setTotalServiceYears(official.getTotalServiceYears());
                dto.setSubsidyAmount(official.getSubsidyAmount());
                dto.setHasViolation(official.getHasViolation());
                dto.setVillageStreet(official.getVillageStreet());
                dto.setRemark(official.getRemark());
                dto.setCreateTime(official.getCreateTime());
                info.setVillageOfficials(List.of(dto));
            }
            case "teacher" -> throw new ServiceException("教龄补助请使用教龄补助模块查看详情");
            default -> throw new ServiceException("不支持的补贴类型: " + subsidyType);
        }
        return info;
    }

    private String getCurrentStatus(String subsidyType, Long recordId)
    {
        return switch (subsidyType)
        {
            case "land_loss_resident" -> {
                LandLossResident r = landLossResidentMapper.selectById(recordId);
                if (r == null || !"0".equals(r.getDelFlag()))
                {
                    throw new ServiceException("记录不存在");
                }
                yield r.getApprovalStatus();
            }
            case "expropriatee" -> {
                ExpropriateeSubsidy r = expropriateeSubsidyMapper.selectById(recordId);
                if (r == null || !"0".equals(r.getDelFlag()))
                {
                    throw new ServiceException("记录不存在");
                }
                yield r.getApprovalStatus();
            }
            case "demolition_resident" -> {
                DemolitionResident r = demolitionResidentMapper.selectById(recordId);
                if (r == null || !"0".equals(r.getDelFlag()))
                {
                    throw new ServiceException("记录不存在");
                }
                yield r.getApprovalStatus();
            }
            case "village_official" -> {
                VillageOfficial r = villageOfficialMapper.selectById(recordId);
                if (r == null || !"0".equals(r.getDelFlag()))
                {
                    throw new ServiceException("记录不存在");
                }
                yield r.getApprovalStatus();
            }
            case "teacher" -> {
                TeacherSubsidy r = teacherSubsidyMapper.selectById(recordId);
                if (r == null || !"0".equals(r.getDelFlag()))
                {
                    throw new ServiceException("记录不存在");
                }
                yield r.getApprovalStatus();
            }
            default -> throw new ServiceException("不支持的补贴类型: " + subsidyType);
        };
    }
}
