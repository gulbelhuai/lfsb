package com.ruoyi.shebao.service.support;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.SubsidyPersonBasicForm;
import com.ruoyi.shebao.service.SubsidyPersonService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 补贴登记时主表人员基础信息的创建/关联逻辑（不修改主表审批状态）
 */
@Component
@RequiredArgsConstructor
public class SubsidyPersonRegistrationHelper
{
    private final SubsidyPersonService subsidyPersonService;

    /**
     * 新增补贴记录：身份证号已存在则仅关联主表，不更新基础信息；不存在则创建主表。
     */
    public Long resolveSubsidyPersonForCreate(SubsidyPersonBasicForm form, Predicate<Long> duplicateSubsidyChecker)
    {
        return resolveSubsidyPersonForCreate(form, duplicateSubsidyChecker, null);
    }

    public Long resolveSubsidyPersonForCreate(SubsidyPersonBasicForm form, Predicate<Long> duplicateSubsidyChecker,
                                              Consumer<SubsidyPerson> newPersonCustomizer)
    {
        if (StringUtils.isEmpty(form.getIdCardNo()))
        {
            throw new ServiceException("身份证号不能为空");
        }

        SubsidyPerson existingPerson = subsidyPersonService.selectSubsidyPersonByIdCardNo(form.getIdCardNo());
        if (existingPerson != null)
        {
            assertPersonEligible(existingPerson);
            if (duplicateSubsidyChecker != null && duplicateSubsidyChecker.test(existingPerson.getId()))
            {
                throw new ServiceException("该人员已存在同类型补贴登记，请核实后录入");
            }
            return existingPerson.getId();
        }

        return createNewSubsidyPerson(form, newPersonCustomizer);
    }

    /**
     * 修改补贴记录：仅校验主表存在且可办理，不更新基础信息。
     */
    public Long resolveSubsidyPersonForUpdate(Long subsidyPersonId)
    {
        if (subsidyPersonId == null)
        {
            throw new ServiceException("被补贴人信息不存在");
        }
        SubsidyPerson person = subsidyPersonService.getById(subsidyPersonId);
        if (person == null)
        {
            throw new ServiceException("被补贴人信息不存在");
        }
        assertPersonEligible(person);
        return person.getId();
    }

    private Long createNewSubsidyPerson(SubsidyPersonBasicForm formDto, Consumer<SubsidyPerson> newPersonCustomizer)
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
        newPerson.setPersonStatus("0");
        newPerson.setSubsidyStatus("0");
        newPerson.setIsAlive("1");
        if (newPersonCustomizer != null)
        {
            newPersonCustomizer.accept(newPerson);
        }
        newPerson.setCreateTime(LocalDateTime.now());
        newPerson.setCreateBy(SecurityUtils.getUsername());
        subsidyPersonService.insertSubsidyPerson(newPerson);
        return newPerson.getId();
    }

    private void assertPersonEligible(SubsidyPerson person)
    {
        if (StringUtils.equals(person.getSubsidyStatus(), "1"))
        {
            throw new ServiceException("该人员已注销，不能办理登记");
        }
        if (StringUtils.equals(person.getIsAlive(), "0"))
        {
            throw new ServiceException("该人员已死亡，不能办理登记");
        }
    }
}
