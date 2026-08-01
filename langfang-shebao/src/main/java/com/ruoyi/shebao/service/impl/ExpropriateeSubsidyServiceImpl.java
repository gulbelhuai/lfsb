package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.ExpropriateeSubsidy;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyFormDto;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyListReq;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyListResp;
import com.ruoyi.shebao.constant.SubsidyApprovalStatus;
import com.ruoyi.shebao.mapper.ExpropriateeSubsidyMapper;
import com.ruoyi.shebao.mapper.PaymentPlanDetailMapper;
import com.ruoyi.shebao.service.ExpropriateeSubsidyService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import com.ruoyi.shebao.service.VillageCommitteeService;
import com.ruoyi.shebao.service.support.SubsidyPersonRegistrationHelper;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * 被征地参保补贴Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-20
 */
@Service
public class ExpropriateeSubsidyServiceImpl extends ServiceImpl<ExpropriateeSubsidyMapper, ExpropriateeSubsidy> implements ExpropriateeSubsidyService
{
    @Resource
    private ExpropriateeSubsidyMapper expropriateeSubsidyMapper;

    @Resource
    private SubsidyPersonService subsidyPersonService;

    @Resource
    private PaymentPlanDetailMapper paymentPlanDetailMapper;

    @Resource
    private VillageCommitteeService villageCommitteeService;

    @Resource
    private SubsidyPersonRegistrationHelper subsidyPersonRegistrationHelper;

    @Override
    public Page<ExpropriateeSubsidyListResp> selectExpropriateeSubsidyList(ExpropriateeSubsidyListReq req)
    {
        long pageNum = req == null ? 1L : req.pageNumOrDefault();
        long pageSize = req == null ? 10L : req.pageSizeOrDefault();
        Page<ExpropriateeSubsidyListResp> page = new Page<>(pageNum, pageSize);
        return expropriateeSubsidyMapper.selectExpropriateeSubsidyList(page, req);
    }

    @Override
    public ExpropriateeSubsidyFormDto selectExpropriateeSubsidyFormById(Long id)
    {
        ExpropriateeSubsidyFormDto formDto = expropriateeSubsidyMapper.selectExpropriateeSubsidyFormById(id);
        if (formDto != null)
        {
            formDto.setPersonExists(formDto.getSubsidyPersonId() != null);
            enrichDivisionDisplayFields(formDto);
        }
        return formDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertExpropriateeSubsidy(ExpropriateeSubsidyFormDto formDto)
    {
        validateBaseDate(formDto.getBaseDate());
        validateSubsidyMode(formDto);
        normalizeDivisionFields(formDto);
        Long subsidyPersonId = subsidyPersonRegistrationHelper.resolveSubsidyPersonForCreate(formDto, personId -> false);

        ExpropriateeSubsidy entity = new ExpropriateeSubsidy();
        entity.setSubsidyPersonId(subsidyPersonId);
        entity.setLandRequisitionBatch(formDto.getLandRequisitionBatch());
        entity.setVillageStreet(formDto.getVillageStreet());
        entity.setBaseDate(formDto.getBaseDate());
        entity.setEmployeePensionMonths(defaultInt(formDto.getEmployeePensionMonths()));
        entity.setFlexibleEmploymentMonths(defaultInt(formDto.getFlexibleEmploymentMonths()));
        entity.setDifficultySubsidyMonths(defaultInt(formDto.getDifficultySubsidyMonths()));
        entity.setAgeAtBaseDate(formDto.getAgeAtBaseDate());
        entity.setSubsidyYears(defaultDecimal(formDto.getSubsidyYears()));
        entity.setSubsidyAmount(defaultDecimal(formDto.getSubsidyAmount()));
        entity.setClaimedAmount(formDto.getClaimedAmount());
        entity.setSubsidyBalance(formDto.getSubsidyBalance());
        entity.setJoinUrbanRuralInsurance(defaultFlag(formDto.getJoinUrbanRuralInsurance()));
        entity.setJoinEmployeePension(defaultFlag(formDto.getJoinEmployeePension()));
        entity.setHasEmployeePension(defaultFlag(formDto.getHasEmployeePension()));
        entity.setStatus(StringUtils.isNotEmpty(formDto.getStatus()) ? formDto.getStatus() : "0");
        entity.setRemark(formDto.getRemark());
        entity.setApprovalStatus(SubsidyApprovalStatus.PENDING_REVIEW);
        entity.setCreateTime(LocalDateTime.now());
        entity.setCreateBy(SecurityUtils.getUsername());
        return expropriateeSubsidyMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateExpropriateeSubsidy(ExpropriateeSubsidyFormDto formDto)
    {
        validateBaseDate(formDto.getBaseDate());
        validateSubsidyMode(formDto);
        normalizeDivisionFields(formDto);
        ExpropriateeSubsidy existing = expropriateeSubsidyMapper.selectById(formDto.getId());
        if (existing == null || !"0".equals(existing.getDelFlag()))
        {
            throw new ServiceException("被征地参保补贴记录不存在");
        }
        if (SubsidyApprovalStatus.isApproved(existing.getApprovalStatus()))
        {
            throw new ServiceException("已通过复核，不能修改");
        }
        subsidyPersonRegistrationHelper.resolveSubsidyPersonForUpdate(existing.getSubsidyPersonId());

        ExpropriateeSubsidy entity = new ExpropriateeSubsidy();
        entity.setId(formDto.getId());
        entity.setLandRequisitionBatch(formDto.getLandRequisitionBatch());
        entity.setVillageStreet(formDto.getVillageStreet());
        entity.setBaseDate(formDto.getBaseDate());
        entity.setEmployeePensionMonths(defaultInt(formDto.getEmployeePensionMonths()));
        entity.setFlexibleEmploymentMonths(defaultInt(formDto.getFlexibleEmploymentMonths()));
        entity.setDifficultySubsidyMonths(defaultInt(formDto.getDifficultySubsidyMonths()));
        entity.setAgeAtBaseDate(formDto.getAgeAtBaseDate());
        entity.setSubsidyYears(defaultDecimal(formDto.getSubsidyYears()));
        entity.setSubsidyAmount(defaultDecimal(formDto.getSubsidyAmount()));
        entity.setClaimedAmount(formDto.getClaimedAmount());
        entity.setSubsidyBalance(formDto.getSubsidyBalance());
        entity.setJoinUrbanRuralInsurance(defaultFlag(formDto.getJoinUrbanRuralInsurance()));
        entity.setJoinEmployeePension(defaultFlag(formDto.getJoinEmployeePension()));
        entity.setHasEmployeePension(defaultFlag(formDto.getHasEmployeePension()));
        entity.setStatus(StringUtils.isNotEmpty(formDto.getStatus()) ? formDto.getStatus() : "0");
        entity.setRemark(formDto.getRemark());
        entity.setApprovalStatus(SubsidyApprovalStatus.PENDING_REVIEW);
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdateBy(SecurityUtils.getUsername());
        return expropriateeSubsidyMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteExpropriateeSubsidyByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            ExpropriateeSubsidy existing = expropriateeSubsidyMapper.selectById(id);
            if (existing != null && existing.getSubsidyPersonId() != null
                    && paymentPlanDetailMapper.countUndeletedBySubsidyPersonId(existing.getSubsidyPersonId()) > 0)
            {
                throw new ServiceException("该被征地参保补贴存在未删除的支付计划发放明细，无法删除");
            }

            ExpropriateeSubsidy entity = new ExpropriateeSubsidy();
            entity.setId(id);
            entity.setDelFlag("2");
            entity.setUpdateTime(LocalDateTime.now());
            entity.setUpdateBy(SecurityUtils.getUsername());
            expropriateeSubsidyMapper.updateById(entity);
        }
        return ids.length;
    }

    @Override
    public int deleteExpropriateeSubsidyById(Long id)
    {
        return deleteExpropriateeSubsidyByIds(new Long[] { id });
    }

    @Override
    public ExpropriateeSubsidyFormDto getFormDataByIdCardNo(String idCardNo)
    {
        ExpropriateeSubsidyFormDto formDto = new ExpropriateeSubsidyFormDto();

        if (StringUtils.isEmpty(idCardNo))
        {
            formDto.setPersonExists(false);
            return formDto;
        }

        SubsidyPerson subsidyPerson = subsidyPersonService.selectAliveSubsidyPersonByIdCardNo(idCardNo);

        if (subsidyPerson != null)
        {
            formDto.setPersonExists(true);
            formDto.setSubsidyPersonId(subsidyPerson.getId());
            formDto.setName(subsidyPerson.getName());
            formDto.setGender(subsidyPerson.getGender());
            formDto.setIdCardNo(subsidyPerson.getIdCardNo());
            formDto.setBirthday(subsidyPerson.getBirthday());
            formDto.setHouseholdRegistration(subsidyPerson.getHouseholdRegistration());
            formDto.setHomeAddress(subsidyPerson.getHomeAddress());
            formDto.setPhone(subsidyPerson.getPhone());
            formDto.setStreetOfficeId(subsidyPerson.getStreetOfficeId());
            formDto.setVillageCommitteeId(subsidyPerson.getVillageCommitteeId());
            formDto.setUserCode(subsidyPerson.getUserCode());
            formDto.setHasEmployeePension("0");
            enrichDivisionDisplayFields(formDto);
        }
        else
        {
            formDto.setPersonExists(false);
            formDto.setIdCardNo(idCardNo);
            LocalDate birthday = parseBirthdayFromIdCard(idCardNo);
            if (birthday != null)
            {
                formDto.setBirthday(birthday);
            }
        }

        return formDto;
    }

    @Override
    public String importExpropriateeSubsidy(List<ExpropriateeSubsidyFormDto> subsidyList, Boolean isUpdateSupport, String operName)
    {
        if (CollectionUtils.isEmpty(subsidyList))
        {
            throw new ServiceException("导入被征地参保补贴数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (ExpropriateeSubsidyFormDto formDto : subsidyList)
        {
            try
            {
                formDto.setPersonExists(false);
                this.insertExpropriateeSubsidy(formDto);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、被征地参保补贴 ").append(formDto.getName()).append(" 导入成功");
            }
            catch (Exception e)
            {
                failureNum++;
                failureMsg.append("<br/>").append(failureNum).append("、被征地参保补贴 ").append(formDto.getName()).append(" 导入失败：").append(e.getMessage());
            }
        }

        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        return successMsg.toString();
    }

    private void normalizeDivisionFields(ExpropriateeSubsidyFormDto formDto)
    {
        if (StringUtils.isBlank(formDto.getHouseholdRegistration()) && StringUtils.isNotBlank(formDto.getNativePlace()))
        {
            formDto.setHouseholdRegistration(formDto.getNativePlace());
        }
        if (StringUtils.isNotBlank(formDto.getVillageCode()) && formDto.getVillageCommitteeId() == null)
        {
            VillageCommittee villageCommittee = villageCommitteeService.lambdaQuery()
                    .eq(VillageCommittee::getVillageCode, formDto.getVillageCode())
                    .last("limit 1")
                    .one();
            if (villageCommittee != null)
            {
                formDto.setVillageCommitteeId(villageCommittee.getId());
                formDto.setStreetOfficeId(villageCommittee.getStreetOfficeId());
                if (StringUtils.isBlank(formDto.getVillageName()))
                {
                    formDto.setVillageName(villageCommittee.getVillageName());
                }
            }
        }
    }

    private void enrichDivisionDisplayFields(ExpropriateeSubsidyFormDto formDto)
    {
        if (formDto.getHouseholdRegistration() != null)
        {
            formDto.setNativePlace(formDto.getHouseholdRegistration());
        }
        if (formDto.getVillageCommitteeId() != null)
        {
            VillageCommittee villageCommittee = villageCommitteeService.getById(formDto.getVillageCommitteeId());
            if (villageCommittee != null)
            {
                formDto.setVillageCode(villageCommittee.getVillageCode());
                formDto.setVillageName(villageCommittee.getVillageName());
            }
        }
    }

    private static LocalDate parseBirthdayFromIdCard(String idCardNo)
    {
        if (StringUtils.isEmpty(idCardNo) || idCardNo.length() != 18)
        {
            return null;
        }
        try
        {
            String birthdayStr = idCardNo.substring(6, 14);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return LocalDate.parse(birthdayStr, formatter);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static int defaultInt(Integer v)
    {
        return v != null ? v : 0;
    }

    private static BigDecimal defaultDecimal(BigDecimal v)
    {
        return v != null ? v : BigDecimal.ZERO;
    }

    private static String defaultFlag(String v)
    {
        return StringUtils.isNotEmpty(v) ? v : "0";
    }

    private static void validateBaseDate(LocalDate baseDate)
    {
        if (baseDate != null && baseDate.isAfter(LocalDate.now()))
        {
            throw new ServiceException("基准日不能晚于当前日期");
        }
    }

    private static void validateSubsidyMode(ExpropriateeSubsidyFormDto formDto)
    {
        boolean urbanRural = "1".equals(formDto.getJoinUrbanRuralInsurance());
        boolean employee = "1".equals(formDto.getJoinEmployeePension());
        if (urbanRural == employee)
        {
            throw new ServiceException("补贴方式必须且只能选择一项");
        }
    }
}
