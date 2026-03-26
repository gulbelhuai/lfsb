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
import com.ruoyi.shebao.mapper.ExpropriateeSubsidyMapper;
import com.ruoyi.shebao.mapper.SubsidyDistributionMapper;
import com.ruoyi.shebao.service.ExpropriateeSubsidyService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import com.ruoyi.shebao.service.VillageCommitteeService;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private SubsidyDistributionMapper subsidyDistributionMapper;

    @Resource
    private VillageCommitteeService villageCommitteeService;

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
            formDto.setPersonExists(true);
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
        Long subsidyPersonId = handleSubsidyPersonInfo(formDto);

        ExpropriateeSubsidy entity = new ExpropriateeSubsidy();
        entity.setSubsidyPersonId(subsidyPersonId);
        entity.setLandRequisitionBatch(formDto.getLandRequisitionBatch());
        entity.setVillageStreet(formDto.getVillageStreet());
        entity.setBaseDate(formDto.getBaseDate());
        entity.setEmployeePensionMonths(defaultInt(formDto.getEmployeePensionMonths()));
        entity.setFlexibleEmploymentMonths(defaultInt(formDto.getFlexibleEmploymentMonths()));
        entity.setDifficultySubsidyYears(defaultDecimal(formDto.getDifficultySubsidyYears()));
        entity.setAgeAtBaseDate(formDto.getAgeAtBaseDate());
        entity.setSubsidyYears(defaultDecimal(formDto.getSubsidyYears()));
        entity.setSubsidyAmount(defaultDecimal(formDto.getSubsidyAmount()));
        entity.setJoinUrbanRuralInsurance(defaultFlag(formDto.getJoinUrbanRuralInsurance()));
        entity.setJoinEmployeePension(defaultFlag(formDto.getJoinEmployeePension()));
        entity.setHasEmployeePension(defaultFlag(formDto.getHasEmployeePension()));
        entity.setStatus(StringUtils.isNotEmpty(formDto.getStatus()) ? formDto.getStatus() : "0");
        entity.setRemark(formDto.getRemark());
        entity.setCreateTime(LocalDateTime.now());
        entity.setCreateBy(SecurityUtils.getUsername());

        int rows = expropriateeSubsidyMapper.insert(entity);
        if (rows > 0)
        {
            markPersonPendingReview(subsidyPersonId);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateExpropriateeSubsidy(ExpropriateeSubsidyFormDto formDto)
    {
        validateBaseDate(formDto.getBaseDate());
        validateSubsidyMode(formDto);
        Long subsidyPersonId = handleSubsidyPersonInfo(formDto);

        ExpropriateeSubsidy entity = new ExpropriateeSubsidy();
        entity.setId(formDto.getId());
        entity.setSubsidyPersonId(subsidyPersonId);
        entity.setLandRequisitionBatch(formDto.getLandRequisitionBatch());
        entity.setVillageStreet(formDto.getVillageStreet());
        entity.setBaseDate(formDto.getBaseDate());
        entity.setEmployeePensionMonths(defaultInt(formDto.getEmployeePensionMonths()));
        entity.setFlexibleEmploymentMonths(defaultInt(formDto.getFlexibleEmploymentMonths()));
        entity.setDifficultySubsidyYears(defaultDecimal(formDto.getDifficultySubsidyYears()));
        entity.setAgeAtBaseDate(formDto.getAgeAtBaseDate());
        entity.setSubsidyYears(defaultDecimal(formDto.getSubsidyYears()));
        entity.setSubsidyAmount(defaultDecimal(formDto.getSubsidyAmount()));
        entity.setJoinUrbanRuralInsurance(defaultFlag(formDto.getJoinUrbanRuralInsurance()));
        entity.setJoinEmployeePension(defaultFlag(formDto.getJoinEmployeePension()));
        entity.setHasEmployeePension(defaultFlag(formDto.getHasEmployeePension()));
        entity.setStatus(StringUtils.isNotEmpty(formDto.getStatus()) ? formDto.getStatus() : "0");
        entity.setRemark(formDto.getRemark());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdateBy(SecurityUtils.getUsername());

        int rows = expropriateeSubsidyMapper.updateById(entity);
        if (rows > 0)
        {
            markPersonPendingReview(subsidyPersonId);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteExpropriateeSubsidyByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            int count = subsidyDistributionMapper.checkUndeletedDistributions("2", id);
            if (count > 0)
            {
                throw new ServiceException("该被征地参保补贴存在未删除的补贴发放记录，无法删除");
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

    private Long handleSubsidyPersonInfo(ExpropriateeSubsidyFormDto formDto)
    {
        normalizeDivisionFields(formDto);

        if (StringUtils.isEmpty(formDto.getIdCardNo()))
        {
            throw new ServiceException("身份证号不能为空");
        }

        SubsidyPerson existingPerson = subsidyPersonService.selectSubsidyPersonByIdCardNo(formDto.getIdCardNo());

        if (existingPerson != null)
        {
            if (StringUtils.equals(existingPerson.getSubsidyStatus(), "1"))
            {
                throw new ServiceException("该人员已注销，不能办理被征地参保补贴登记");
            }
            if (StringUtils.equals(existingPerson.getIsAlive(), "0"))
            {
                throw new ServiceException("该人员已死亡，不能办理被征地参保补贴登记");
            }
            if (Boolean.TRUE.equals(formDto.getPersonExists()))
            {
                updateExistingSubsidyPerson(existingPerson, formDto);
            }
            return existingPerson.getId();
        }

        return createNewSubsidyPerson(formDto);
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

    private void updateExistingSubsidyPerson(SubsidyPerson existingPerson, ExpropriateeSubsidyFormDto formDto)
    {
        boolean needUpdate = false;

        if (!StringUtils.equals(existingPerson.getName(), formDto.getName()))
        {
            existingPerson.setName(formDto.getName());
            needUpdate = true;
        }
        if (!StringUtils.equals(existingPerson.getGender(), formDto.getGender()))
        {
            existingPerson.setGender(formDto.getGender());
            needUpdate = true;
        }
        if (!Objects.equals(existingPerson.getBirthday(), formDto.getBirthday()))
        {
            existingPerson.setBirthday(formDto.getBirthday());
            needUpdate = true;
        }
        if (!Objects.equals(existingPerson.getStreetOfficeId(), formDto.getStreetOfficeId()))
        {
            existingPerson.setStreetOfficeId(formDto.getStreetOfficeId());
            needUpdate = true;
        }
        if (!Objects.equals(existingPerson.getVillageCommitteeId(), formDto.getVillageCommitteeId()))
        {
            existingPerson.setVillageCommitteeId(formDto.getVillageCommitteeId());
            needUpdate = true;
        }
        if (!StringUtils.equals(existingPerson.getHouseholdRegistration(), formDto.getHouseholdRegistration()))
        {
            existingPerson.setHouseholdRegistration(formDto.getHouseholdRegistration());
            needUpdate = true;
        }
        if (!StringUtils.equals(existingPerson.getHomeAddress(), formDto.getHomeAddress()))
        {
            existingPerson.setHomeAddress(formDto.getHomeAddress());
            needUpdate = true;
        }
        if (!StringUtils.equals(existingPerson.getPhone(), formDto.getPhone()))
        {
            existingPerson.setPhone(formDto.getPhone());
            needUpdate = true;
        }

        if (needUpdate)
        {
            subsidyPersonService.updateSubsidyPerson(existingPerson);
        }
    }

    private Long createNewSubsidyPerson(ExpropriateeSubsidyFormDto formDto)
    {
        SubsidyPerson newPerson = new SubsidyPerson();
        newPerson.setName(formDto.getName());
        newPerson.setGender(formDto.getGender());
        newPerson.setIdCardNo(formDto.getIdCardNo());
        newPerson.setBirthday(formDto.getBirthday());
        newPerson.setHouseholdRegistration(formDto.getHouseholdRegistration());
        newPerson.setHomeAddress(formDto.getHomeAddress());
        newPerson.setPhone(formDto.getPhone());
        newPerson.setStreetOfficeId(formDto.getStreetOfficeId());
        newPerson.setVillageCommitteeId(formDto.getVillageCommitteeId());
        newPerson.setUserCode(formDto.getUserCode());
        newPerson.setStatus("0");
        newPerson.setApprovalStatus("draft");
        newPerson.setPersonStatus("0");
        newPerson.setSubsidyStatus("0");

        subsidyPersonService.insertSubsidyPerson(newPerson);
        return newPerson.getId();
    }

    private void markPersonPendingReview(Long subsidyPersonId)
    {
        SubsidyPerson person = subsidyPersonService.getById(subsidyPersonId);
        if (person == null)
        {
            throw new ServiceException("被补贴人信息不存在");
        }
        person.setApprovalStatus("pending_review");
        person.setUpdateBy(SecurityUtils.getUsername());
        person.setUpdateTime(LocalDateTime.now());
        subsidyPersonService.updateById(person);
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
