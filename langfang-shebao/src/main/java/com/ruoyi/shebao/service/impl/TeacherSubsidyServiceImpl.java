package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.domain.TeacherSubsidy;
import com.ruoyi.shebao.dto.TeacherSubsidyFormDto;
import com.ruoyi.shebao.dto.TeacherSubsidyListReq;
import com.ruoyi.shebao.dto.TeacherSubsidyListResp;
import com.ruoyi.shebao.mapper.SubsidyDistributionMapper;
import com.ruoyi.shebao.mapper.TeacherSubsidyMapper;
import com.ruoyi.shebao.service.SubsidyPersonService;
import com.ruoyi.shebao.service.TeacherSubsidyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 教龄补助Service实现
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@Service
public class TeacherSubsidyServiceImpl extends ServiceImpl<TeacherSubsidyMapper, TeacherSubsidy> implements TeacherSubsidyService
{
    @Autowired
    private TeacherSubsidyMapper teacherSubsidyMapper;

    @Autowired
    private SubsidyPersonService subsidyPersonService;

    @Autowired
    private SubsidyDistributionMapper subsidyDistributionMapper;

    @Override
    public Page<TeacherSubsidyListResp> selectTeacherSubsidyList(TeacherSubsidyListReq req)
    {
        long pageNum = req.pageNumOrDefault();
        long pageSize = req.pageSizeOrDefault();
        Page<TeacherSubsidyListResp> page = new Page<>(pageNum, pageSize);
        return teacherSubsidyMapper.selectTeacherSubsidyList(page, req);
    }

    @Override
    public TeacherSubsidyFormDto selectTeacherSubsidyFormById(Long id)
    {
        TeacherSubsidyFormDto formDto = teacherSubsidyMapper.selectTeacherSubsidyFormById(id);
        if (formDto != null)
        {
            formDto.setPersonExists(true);
        }
        return formDto;
    }

    @Override
    public TeacherSubsidyFormDto getFormDataByIdCardNo(String idCardNo)
    {
        TeacherSubsidyFormDto formDto = new TeacherSubsidyFormDto();
        if (StringUtils.isEmpty(idCardNo))
        {
            formDto.setPersonExists(false);
            return formDto;
        }

        // 选人/回填时排除注销人员（is_alive=0）
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
            formDto.setStatus(subsidyPerson.getStatus());
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
    @Transactional(rollbackFor = Exception.class)
    public int insertTeacherSubsidy(TeacherSubsidyFormDto formDto)
    {
        Long subsidyPersonId = handleSubsidyPersonInfo(formDto);

        TeacherSubsidy teacherSubsidy = new TeacherSubsidy();
        teacherSubsidy.setSubsidyPersonId(subsidyPersonId);
        teacherSubsidy.setSchoolName(formDto.getSchoolName());
        teacherSubsidy.setTeachingYears(formDto.getTeachingYears());
        teacherSubsidy.setRemark(formDto.getRemark());
        teacherSubsidy.setCreateTime(LocalDateTime.now());
        teacherSubsidy.setCreateBy(SecurityUtils.getUsername());

        return teacherSubsidyMapper.insert(teacherSubsidy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTeacherSubsidy(TeacherSubsidyFormDto formDto)
    {
        if (formDto.getId() == null)
        {
            throw new ServiceException("缺少ID，无法修改");
        }
        Long subsidyPersonId = handleSubsidyPersonInfo(formDto);

        TeacherSubsidy teacherSubsidy = new TeacherSubsidy();
        teacherSubsidy.setId(formDto.getId());
        teacherSubsidy.setSubsidyPersonId(subsidyPersonId);
        teacherSubsidy.setSchoolName(formDto.getSchoolName());
        teacherSubsidy.setTeachingYears(formDto.getTeachingYears());
        teacherSubsidy.setRemark(formDto.getRemark());
        teacherSubsidy.setUpdateTime(LocalDateTime.now());
        teacherSubsidy.setUpdateBy(SecurityUtils.getUsername());

        return teacherSubsidyMapper.updateById(teacherSubsidy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTeacherSubsidyByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            // 约定：教龄补助=补贴类型5（教师）
            int count = subsidyDistributionMapper.checkUndeletedDistributions("5", id);
            if (count > 0)
            {
                throw new ServiceException("该教龄补助存在未删除的补贴发放记录，无法删除");
            }

            TeacherSubsidy ts = new TeacherSubsidy();
            ts.setId(id);
            ts.setDelFlag("2");
            ts.setUpdateTime(LocalDateTime.now());
            ts.setUpdateBy(SecurityUtils.getUsername());
            teacherSubsidyMapper.updateById(ts);
        }
        return ids.length;
    }

    /**
     * 智能处理基础信息（新增或更新）
     */
    private Long handleSubsidyPersonInfo(TeacherSubsidyFormDto formDto)
    {
        if (StringUtils.isEmpty(formDto.getIdCardNo()))
        {
            throw new ServiceException("身份证号不能为空");
        }

        SubsidyPerson existingPerson = subsidyPersonService.selectSubsidyPersonByIdCardNo(formDto.getIdCardNo());
        if (existingPerson != null)
        {
            if ("0".equals(existingPerson.getIsAlive()))
            {
                throw new ServiceException("该人员已注销（死亡），不能办理教龄补助登记");
            }
            if (formDto.getPersonExists() != null && formDto.getPersonExists())
            {
                updateExistingSubsidyPerson(existingPerson, formDto);
            }
            return existingPerson.getId();
        }

        return createNewSubsidyPerson(formDto);
    }

    private void updateExistingSubsidyPerson(SubsidyPerson existingPerson, TeacherSubsidyFormDto formDto)
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
        if (!StringUtils.equals(existingPerson.getUserCode(), formDto.getUserCode()) && StringUtils.isNotBlank(formDto.getUserCode()))
        {
            existingPerson.setUserCode(formDto.getUserCode());
            needUpdate = true;
        }
        if (!StringUtils.equals(existingPerson.getStatus(), formDto.getStatus()) && StringUtils.isNotBlank(formDto.getStatus()))
        {
            existingPerson.setStatus(formDto.getStatus());
            needUpdate = true;
        }

        if (needUpdate)
        {
            subsidyPersonService.updateSubsidyPerson(existingPerson);
        }
    }

    private Long createNewSubsidyPerson(TeacherSubsidyFormDto formDto)
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
        newPerson.setStatus(StringUtils.defaultIfBlank(formDto.getStatus(), "0"));
        newPerson.setApprovalStatus("draft");

        subsidyPersonService.insertSubsidyPerson(newPerson);
        return newPerson.getId();
    }

    private LocalDate parseBirthdayFromIdCard(String idCardNo)
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
}

