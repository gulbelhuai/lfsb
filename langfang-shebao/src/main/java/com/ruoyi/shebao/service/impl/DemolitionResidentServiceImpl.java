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
import com.ruoyi.shebao.constant.SubsidyApprovalStatus;
import com.ruoyi.shebao.mapper.DemolitionResidentMapper;
import com.ruoyi.shebao.mapper.PaymentPlanDetailMapper;
import com.ruoyi.shebao.service.DemolitionResidentService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import com.ruoyi.shebao.service.support.SubsidyPersonRegistrationHelper;
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
    private PaymentPlanDetailMapper paymentPlanDetailMapper;

    @Autowired
    private SubsidyPersonRegistrationHelper subsidyPersonRegistrationHelper;

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
            formDto.setPersonExists(formDto.getSubsidyPersonId() != null);
        }
        return formDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDemolitionResident(DemolitionResidentFormDto formDto)
    {
        validateTimeRelation(formDto);
        SubsidyPerson existingByCard = subsidyPersonService.selectSubsidyPersonByIdCardNo(formDto.getIdCardNo());
        if (existingByCard != null)
        {
            DemolitionResident existRecord = this.lambdaQuery()
                    .eq(DemolitionResident::getSubsidyPersonId, existingByCard.getId())
                    .eq(DemolitionResident::getDelFlag, "0")
                    .ne(Objects.nonNull(formDto.getId()), DemolitionResident::getId, formDto.getId())
                    .last("limit 1")
                    .one();
            if (existRecord != null)
            {
                throw new ServiceException("该人员已被认定为拆迁居民，请核实后录入");
            }
        }
        Long subsidyPersonId = subsidyPersonRegistrationHelper.resolveSubsidyPersonForCreate(formDto, personId -> false, person -> {
            person.setIsAlive(StringUtils.isNotEmpty(formDto.getIsAlive()) ? formDto.getIsAlive() : "1");
            person.setDeathDate(formDto.getDeathDate());
            person.setIsVillageCoopMember(StringUtils.isNotEmpty(formDto.getIsVillageCoopMember()) ? formDto.getIsVillageCoopMember() : "1");
        });

        DemolitionResident demolitionResident = new DemolitionResident();
        demolitionResident.setSubsidyPersonId(subsidyPersonId);
        demolitionResident.setDemolitionReason(formDto.getDemolitionReason());
        demolitionResident.setDemolitionTime(formDto.getDemolitionTime());
        demolitionResident.setRecognitionTime(formDto.getRecognitionTime());
        demolitionResident.setVillageStreet(formDto.getVillageStreet());
        demolitionResident.setRemark(formDto.getRemark());
        demolitionResident.setApprovalStatus(SubsidyApprovalStatus.PENDING_REVIEW);
        demolitionResident.setCreateBy(SecurityUtils.getUsername());
        demolitionResident.setCreateTime(LocalDateTime.now());
        return demolitionResidentMapper.insert(demolitionResident);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDemolitionResident(DemolitionResidentFormDto formDto)
    {
        validateTimeRelation(formDto);
        DemolitionResident existing = demolitionResidentMapper.selectById(formDto.getId());
        if (existing == null || !"0".equals(existing.getDelFlag()))
        {
            throw new ServiceException("拆迁居民记录不存在");
        }
        if (SubsidyApprovalStatus.isApproved(existing.getApprovalStatus()))
        {
            throw new ServiceException("已通过复核，不能修改");
        }
        subsidyPersonRegistrationHelper.resolveSubsidyPersonForUpdate(existing.getSubsidyPersonId());

        DemolitionResident demolitionResident = new DemolitionResident();
        demolitionResident.setId(formDto.getId());
        demolitionResident.setDemolitionReason(formDto.getDemolitionReason());
        demolitionResident.setDemolitionTime(formDto.getDemolitionTime());
        demolitionResident.setRecognitionTime(formDto.getRecognitionTime());
        demolitionResident.setVillageStreet(formDto.getVillageStreet());
        demolitionResident.setRemark(formDto.getRemark());
        demolitionResident.setApprovalStatus(SubsidyApprovalStatus.PENDING_REVIEW);
        demolitionResident.setUpdateBy(SecurityUtils.getUsername());
        demolitionResident.setUpdateTime(LocalDateTime.now());
        return demolitionResidentMapper.updateById(demolitionResident);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDemolitionResidentByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            DemolitionResident existing = demolitionResidentMapper.selectById(id);
            if (existing != null && existing.getSubsidyPersonId() != null
                    && paymentPlanDetailMapper.countUndeletedBySubsidyPersonId(existing.getSubsidyPersonId()) > 0)
            {
                throw new ServiceException("该拆迁居民存在未删除的支付计划发放明细，无法删除");
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

    private void validateTimeRelation(DemolitionResidentFormDto formDto)
    {
        if (formDto.getDemolitionTime() != null
            && formDto.getRecognitionTime() != null
            && !formDto.getRecognitionTime().isAfter(formDto.getDemolitionTime()))
        {
            throw new ServiceException("认定为拆迁居民时间应晚于拆迁时间");
        }
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
