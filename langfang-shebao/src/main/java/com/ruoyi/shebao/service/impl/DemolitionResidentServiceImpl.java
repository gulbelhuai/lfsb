package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.DemolitionResident;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.DemolitionResidentFormDto;
import com.ruoyi.shebao.dto.DemolitionResidentListReq;
import com.ruoyi.shebao.dto.DemolitionResidentListResp;
import com.ruoyi.shebao.mapper.DemolitionResidentMapper;
import com.ruoyi.shebao.mapper.SubsidyDistributionMapper;
import com.ruoyi.shebao.service.DemolitionResidentService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * 拆迁居民信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-20
 */
@Service
public class DemolitionResidentServiceImpl extends ServiceImpl<DemolitionResidentMapper, DemolitionResident> implements DemolitionResidentService
{
    @Autowired
    private DemolitionResidentMapper demolitionResidentMapper;

    @Autowired
    private SubsidyPersonService subsidyPersonService;

    @Autowired
    private SubsidyDistributionMapper subsidyDistributionMapper;

    @Override
    public Page<DemolitionResidentListResp> selectDemolitionResidentList(DemolitionResidentListReq req)
    {
        Page<DemolitionResidentListResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return demolitionResidentMapper.selectDemolitionResidentList(page, req);
    }

    @Override
    public DemolitionResidentFormDto selectDemolitionResidentFormById(Long id)
    {
        DemolitionResidentFormDto formDto = demolitionResidentMapper.selectDemolitionResidentFormById(id);
        if (formDto != null)
        {
            formDto.setPersonExists(true);
        }
        return formDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDemolitionResident(DemolitionResidentFormDto formDto)
    {
        validateTimeRelation(formDto);
        Long subsidyPersonId = handleSubsidyPersonInfo(formDto);

        DemolitionResident demolitionResident = new DemolitionResident();
        demolitionResident.setSubsidyPersonId(subsidyPersonId);
        demolitionResident.setDemolitionReason(formDto.getDemolitionReason());
        demolitionResident.setDemolitionTime(formDto.getDemolitionTime());
        demolitionResident.setRecognitionTime(formDto.getRecognitionTime());
        demolitionResident.setVillageStreet(formDto.getVillageStreet());
        demolitionResident.setRemark(formDto.getRemark());
        demolitionResident.setCreateBy(SecurityUtils.getUsername());
        demolitionResident.setCreateTime(LocalDateTime.now());
        int rows = demolitionResidentMapper.insert(demolitionResident);
        if (rows > 0)
        {
            markPersonPendingReview(subsidyPersonId);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDemolitionResident(DemolitionResidentFormDto formDto)
    {
        validateTimeRelation(formDto);
        Long subsidyPersonId = handleSubsidyPersonInfo(formDto);

        DemolitionResident demolitionResident = new DemolitionResident();
        demolitionResident.setId(formDto.getId());
        demolitionResident.setSubsidyPersonId(subsidyPersonId);
        demolitionResident.setDemolitionReason(formDto.getDemolitionReason());
        demolitionResident.setDemolitionTime(formDto.getDemolitionTime());
        demolitionResident.setRecognitionTime(formDto.getRecognitionTime());
        demolitionResident.setVillageStreet(formDto.getVillageStreet());
        demolitionResident.setRemark(formDto.getRemark());
        demolitionResident.setUpdateBy(SecurityUtils.getUsername());
        demolitionResident.setUpdateTime(LocalDateTime.now());
        int rows = demolitionResidentMapper.updateById(demolitionResident);
        if (rows > 0)
        {
            markPersonPendingReview(subsidyPersonId);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDemolitionResidentByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            int count = subsidyDistributionMapper.checkUndeletedDistributions("3", id);
            if (count > 0)
            {
                throw new ServiceException("该拆迁居民存在未删除的补贴发放记录，无法删除");
            }

            DemolitionResident demolitionResident = new DemolitionResident();
            demolitionResident.setId(id);
            demolitionResident.setDelFlag("2");
            demolitionResident.setUpdateBy(SecurityUtils.getUsername());
            demolitionResident.setUpdateTime(LocalDateTime.now());
            demolitionResidentMapper.updateById(demolitionResident);
        }
        return ids.length;
    }

    @Override
    public int deleteDemolitionResidentById(Long id)
    {
        return deleteDemolitionResidentByIds(new Long[] { id });
    }

    @Override
    public DemolitionResidentFormDto getFormDataByIdCardNo(String idCardNo)
    {
        DemolitionResidentFormDto formDto = new DemolitionResidentFormDto();
        if (StringUtils.isBlank(idCardNo))
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
            formDto.setIsAlive(subsidyPerson.getIsAlive());
            formDto.setDeathDate(subsidyPerson.getDeathDate());
            formDto.setIsVillageCoopMember(subsidyPerson.getIsVillageCoopMember());
            formDto.setStreetOfficeId(subsidyPerson.getStreetOfficeId());
            formDto.setVillageCommitteeId(subsidyPerson.getVillageCommitteeId());
            formDto.setUserCode(subsidyPerson.getUserCode());
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
    public String importDemolitionResident(List<DemolitionResidentFormDto> residentList, Boolean isUpdateSupport, String operName)
    {
        if (residentList == null || residentList.isEmpty())
        {
            throw new RuntimeException("导入拆迁居民数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (DemolitionResidentFormDto formDto : residentList)
        {
            try
            {
                formDto.setPersonExists(false);
                this.insertDemolitionResident(formDto);
                successNum++;
                successMsg.append("<br/>").append(successNum).append("、拆迁居民 ").append(formDto.getName()).append(" 导入成功");
            }
            catch (Exception e)
            {
                failureNum++;
                failureMsg.append("<br/>").append(failureNum).append("、拆迁居民 ").append(formDto.getName()).append(" 导入失败：").append(e.getMessage());
            }
        }

        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new RuntimeException(failureMsg.toString());
        }

        successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        return successMsg.toString();
    }

    private Long handleSubsidyPersonInfo(DemolitionResidentFormDto formDto)
    {
        if (StringUtils.isBlank(formDto.getIdCardNo()))
        {
            throw new RuntimeException("身份证号不能为空");
        }

        SubsidyPerson existingPerson = subsidyPersonService.selectSubsidyPersonByIdCardNo(formDto.getIdCardNo());
        if (existingPerson != null)
        {
            if (StringUtils.equals(existingPerson.getIsAlive(), "0"))
            {
                throw new ServiceException("该人员已注销（死亡），不能办理拆迁居民登记");
            }
            DemolitionResident existDemolitionResident = this.lambdaQuery()
                .eq(DemolitionResident::getSubsidyPersonId, existingPerson.getId())
                .ne(Objects.nonNull(formDto.getId()), DemolitionResident::getId, formDto.getId())
                .last("limit 1")
                .one();
            if (existDemolitionResident != null)
            {
                throw new ServiceException("该人员已被认定为拆迁居民，请核实后录入");
            }
            if (Boolean.TRUE.equals(formDto.getPersonExists()))
            {
                updateExistingSubsidyPerson(existingPerson, formDto);
            }
            return existingPerson.getId();
        }

        return createNewSubsidyPerson(formDto);
    }

    private void updateExistingSubsidyPerson(SubsidyPerson existingPerson, DemolitionResidentFormDto formDto)
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
        if (!StringUtils.equals(existingPerson.getIsAlive(), formDto.getIsAlive()))
        {
            existingPerson.setIsAlive(formDto.getIsAlive());
            needUpdate = true;
        }
        if (StringUtils.equals(formDto.getIsAlive(), "0") && !Objects.equals(existingPerson.getDeathDate(), formDto.getDeathDate()))
        {
            existingPerson.setDeathDate(formDto.getDeathDate());
            needUpdate = true;
        }
        if (!StringUtils.equals(existingPerson.getIsVillageCoopMember(), formDto.getIsVillageCoopMember()))
        {
            existingPerson.setIsVillageCoopMember(formDto.getIsVillageCoopMember());
            needUpdate = true;
        }
        if (needUpdate)
        {
            subsidyPersonService.updateSubsidyPerson(existingPerson);
        }
    }

    private Long createNewSubsidyPerson(DemolitionResidentFormDto formDto)
    {
        SubsidyPerson newPerson = new SubsidyPerson();
        newPerson.setName(formDto.getName());
        newPerson.setGender(formDto.getGender());
        newPerson.setIdCardNo(formDto.getIdCardNo());
        newPerson.setBirthday(formDto.getBirthday());
        newPerson.setHouseholdRegistration(formDto.getHouseholdRegistration());
        newPerson.setHomeAddress(formDto.getHomeAddress());
        newPerson.setPhone(formDto.getPhone());
        newPerson.setIsAlive(formDto.getIsAlive());
        newPerson.setDeathDate(formDto.getDeathDate());
        newPerson.setIsVillageCoopMember(formDto.getIsVillageCoopMember());
        newPerson.setStreetOfficeId(formDto.getStreetOfficeId());
        newPerson.setVillageCommitteeId(formDto.getVillageCommitteeId());
        newPerson.setUserCode(formDto.getUserCode());
        newPerson.setStatus("0");
        newPerson.setApprovalStatus("draft");
        subsidyPersonService.insertSubsidyPerson(newPerson);
        return newPerson.getId();
    }

    private void validateTimeRelation(DemolitionResidentFormDto formDto)
    {
        if (formDto.getDemolitionTime() != null
            && formDto.getRecognitionTime() != null
            && !formDto.getRecognitionTime().isAfter(formDto.getDemolitionTime()))
        {
            throw new ServiceException("认定为拆迁居民时间应晚于拆迁时间");
        }
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

    private LocalDate parseBirthdayFromIdCard(String idCardNo)
    {
        if (StringUtils.isBlank(idCardNo) || idCardNo.length() != 18)
        {
            return null;
        }
        try
        {
            return LocalDate.parse(idCardNo.substring(6, 14), DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
