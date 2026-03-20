package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.PersonCancel;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.PersonCancelFormDto;
import com.ruoyi.shebao.dto.PersonCancelListReq;
import com.ruoyi.shebao.dto.PersonCancelListResp;
import com.ruoyi.shebao.mapper.PersonCancelMapper;
import com.ruoyi.shebao.service.PersonCancelService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 人员注销登记Service实现
 *
 * 说明：
 * - 新增/修改注销登记时，会同步把基础信息（shebao_subsidy_person）标记为死亡：is_alive=0，death_date=填入时间
 * - 删除注销登记记录仅做逻辑删除，不自动回滚人员死亡状态（避免误删导致状态回退）
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@Service
public class PersonCancelServiceImpl extends ServiceImpl<PersonCancelMapper, PersonCancel> implements PersonCancelService
{
    @Autowired
    private PersonCancelMapper personCancelMapper;

    @Autowired
    private SubsidyPersonService subsidyPersonService;

    @Override
    public Page<PersonCancelListResp> selectPersonCancelList(PersonCancelListReq req)
    {
        long pageNum = req.pageNumOrDefault();
        long pageSize = req.pageSizeOrDefault();
        Page<PersonCancelListResp> page = new Page<>(pageNum, pageSize);
        return personCancelMapper.selectPersonCancelList(page, req);
    }

    @Override
    public List<PersonCancelListResp> selectPersonCancelListNoPage(PersonCancelListReq req)
    {
        return personCancelMapper.selectPersonCancelListNoPage(req);
    }

    @Override
    public PersonCancelFormDto selectPersonCancelFormById(Long id)
    {
        return personCancelMapper.selectPersonCancelFormById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPersonCancel(PersonCancelFormDto formDto)
    {
        if (StringUtils.isBlank(formDto.getIdCardNo()))
        {
            throw new ServiceException("身份证号不能为空");
        }
        if (formDto.getDeathDate() == null)
        {
            throw new ServiceException("死亡时间不能为空");
        }

        SubsidyPerson person = subsidyPersonService.selectSubsidyPersonByIdCardNo(formDto.getIdCardNo());
        if (person == null)
        {
            throw new ServiceException("未找到该身份证号对应的人员");
        }

        // 同步更新人员基础信息为死亡
        person.setIsAlive("0");
        person.setDeathDate(formDto.getDeathDate());
        subsidyPersonService.updateSubsidyPerson(person);

        PersonCancel pc = new PersonCancel();
        pc.setSubsidyPersonId(person.getId());
        pc.setDeathDate(formDto.getDeathDate());
        pc.setRemark(formDto.getRemark());
        pc.setDelFlag("0"); // 设置删除标志为正常
        pc.setCreateBy(SecurityUtils.getUsername());
        pc.setCreateTime(LocalDateTime.now());
        return personCancelMapper.insert(pc);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePersonCancel(PersonCancelFormDto formDto)
    {
        if (formDto.getId() == null)
        {
            throw new ServiceException("缺少ID，无法修改");
        }
        if (formDto.getDeathDate() == null)
        {
            throw new ServiceException("死亡时间不能为空");
        }

        PersonCancelFormDto existing = personCancelMapper.selectPersonCancelFormById(formDto.getId());
        if (existing == null)
        {
            throw new ServiceException("记录不存在或已删除");
        }

        // 同步更新人员基础信息为死亡（以记录对应人员为准）
        SubsidyPerson person = subsidyPersonService.selectSubsidyPersonById(existing.getSubsidyPersonId());
        if (person != null)
        {
            person.setIsAlive("0");
            person.setDeathDate(formDto.getDeathDate());
            subsidyPersonService.updateSubsidyPerson(person);
        }

        PersonCancel pc = new PersonCancel();
        pc.setId(formDto.getId());
        pc.setSubsidyPersonId(existing.getSubsidyPersonId());
        pc.setDeathDate(formDto.getDeathDate());
        pc.setRemark(formDto.getRemark());
        pc.setUpdateBy(SecurityUtils.getUsername());
        pc.setUpdateTime(LocalDateTime.now());
        return personCancelMapper.updateById(pc);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePersonCancelByIds(Long[] ids)
    {
        Set<Long> affectedPersonIds = new HashSet<>();

        for (Long id : ids)
        {
            PersonCancel existing = this.getById(id);
            if (existing != null && existing.getSubsidyPersonId() != null)
            {
                affectedPersonIds.add(existing.getSubsidyPersonId());
            }

            PersonCancel pc = new PersonCancel();
            pc.setId(id);
            pc.setDelFlag("2");
            pc.setUpdateBy(SecurityUtils.getUsername());
            pc.setUpdateTime(LocalDateTime.now());
            personCancelMapper.updateById(pc);
        }

        // 删除后：回滚人员死亡状态
        for (Long personId : affectedPersonIds)
        {
            int remaining = personCancelMapper.countActiveByPersonId(personId);
            SubsidyPerson person = subsidyPersonService.selectSubsidyPersonById(personId);
            if (person == null)
            {
                continue;
            }

            if (remaining <= 0)
            {
                // 没有任何注销记录 -> 视为回滚到健在
                person.setIsAlive("1");
                person.setDeathDate(null);
            }
            else
            {
                // 仍有注销记录 -> 以最新（最大）死亡时间为准
                LocalDate maxDeathDate = personCancelMapper.selectMaxDeathDateByPersonId(personId);
                person.setIsAlive("0");
                person.setDeathDate(maxDeathDate);
            }
            subsidyPersonService.updateSubsidyPerson(person);
        }
        return ids.length;
    }
}

