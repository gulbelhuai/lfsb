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
import com.ruoyi.shebao.dto.PersonReviewListReq;
import com.ruoyi.shebao.dto.PersonReviewListResp;
import com.ruoyi.shebao.mapper.DemolitionResidentMapper;
import com.ruoyi.shebao.mapper.ExpropriateeSubsidyMapper;
import com.ruoyi.shebao.mapper.LandLossResidentMapper;
import com.ruoyi.shebao.mapper.PersonReviewMapper;
import com.ruoyi.shebao.domain.TeacherSubsidy;
import com.ruoyi.shebao.mapper.TeacherSubsidyMapper;
import com.ruoyi.shebao.mapper.VillageOfficialMapper;
import com.ruoyi.shebao.service.ApprovalLogService;
import com.ruoyi.shebao.service.PersonReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
