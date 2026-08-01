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
import com.ruoyi.shebao.constant.SubsidyApprovalStatus;
import com.ruoyi.shebao.mapper.PaymentPlanDetailMapper;
import com.ruoyi.shebao.mapper.TeacherSubsidyMapper;
import com.ruoyi.shebao.service.SubsidyPersonService;
import com.ruoyi.shebao.service.TeacherSubsidyService;
import com.ruoyi.shebao.service.support.SubsidyPersonRegistrationHelper;
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
    private PaymentPlanDetailMapper paymentPlanDetailMapper;

    @Autowired
    private SubsidyPersonRegistrationHelper subsidyPersonRegistrationHelper;

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
            formDto.setPersonExists(formDto.getSubsidyPersonId() != null);
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
        Long subsidyPersonId = subsidyPersonRegistrationHelper.resolveSubsidyPersonForCreate(formDto, personId -> false, person -> {
            person.setIsAlive(StringUtils.isNotEmpty(formDto.getIsAlive()) ? formDto.getIsAlive() : "1");
            person.setDeathDate(formDto.getDeathDate());
            person.setIsVillageCoopMember(StringUtils.isNotEmpty(formDto.getIsVillageCoopMember()) ? formDto.getIsVillageCoopMember() : "1");
            person.setStatus(StringUtils.defaultIfBlank(formDto.getStatus(), "0"));
        });

        TeacherSubsidy teacherSubsidy = new TeacherSubsidy();
        teacherSubsidy.setSubsidyPersonId(subsidyPersonId);
        teacherSubsidy.setSchoolName(formDto.getSchoolName());
        teacherSubsidy.setTeachingYears(formDto.getTeachingYears());
        teacherSubsidy.setRemark(formDto.getRemark());
        teacherSubsidy.setApprovalStatus(SubsidyApprovalStatus.PENDING_REVIEW);
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
        TeacherSubsidy existing = teacherSubsidyMapper.selectById(formDto.getId());
        if (existing == null || !"0".equals(existing.getDelFlag()))
        {
            throw new ServiceException("教龄补助记录不存在");
        }
        subsidyPersonRegistrationHelper.resolveSubsidyPersonForUpdate(existing.getSubsidyPersonId());

        TeacherSubsidy teacherSubsidy = new TeacherSubsidy();
        teacherSubsidy.setId(formDto.getId());
        teacherSubsidy.setSchoolName(formDto.getSchoolName());
        teacherSubsidy.setTeachingYears(formDto.getTeachingYears());
        teacherSubsidy.setRemark(formDto.getRemark());
        teacherSubsidy.setApprovalStatus(SubsidyApprovalStatus.PENDING_REVIEW);
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
            TeacherSubsidy existing = teacherSubsidyMapper.selectById(id);
            if (existing != null && existing.getSubsidyPersonId() != null
                    && paymentPlanDetailMapper.countUndeletedBySubsidyPersonId(existing.getSubsidyPersonId()) > 0)
            {
                throw new ServiceException("该教龄补助存在未删除的支付计划发放明细，无法删除");
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

