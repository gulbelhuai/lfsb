package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.LandLossResidentListReq;
import com.ruoyi.shebao.dto.LandLossResidentListResp;
import com.ruoyi.shebao.dto.LandLossResidentFormDto;
import com.ruoyi.shebao.mapper.LandLossResidentMapper;
import com.ruoyi.shebao.mapper.SubsidyDistributionMapper;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import com.ruoyi.shebao.service.LandLossResidentService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import org.apache.commons.collections4.CollectionUtils;
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
 * 失地居民信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-27
 */
@Service
public class LandLossResidentServiceImpl extends ServiceImpl<LandLossResidentMapper, LandLossResident> implements LandLossResidentService
{
    @Autowired
    private LandLossResidentMapper landLossResidentMapper;

    @Autowired
    private SubsidyPersonMapper subsidyPersonMapper;

    @Autowired
    private SubsidyPersonService subsidyPersonService;

    @Autowired
    private SubsidyDistributionMapper subsidyDistributionMapper;

    /**
     * 查询失地居民信息列表
     *
     * @param req 查询条件
     * @return 失地居民信息列表
     */
    @Override
    public Page<LandLossResidentListResp> selectLandLossResidentList(LandLossResidentListReq req)
    {
        // 使用默认分页参数
        Page<LandLossResidentListResp> page = new Page<>(1, 10);
        return landLossResidentMapper.selectLandLossResidentList(page, req);
    }

    /**
     * 查询失地居民信息详情（包含基础信息）
     *
     * @param id 失地居民信息主键
     * @return 失地居民信息
     */
    @Override
    public LandLossResidentFormDto selectLandLossResidentFormById(Long id)
    {
        LandLossResidentFormDto formDto = landLossResidentMapper.selectLandLossResidentFormById(id);
        if (formDto != null)
        {
            formDto.setPersonExists(true);
        }
        return formDto;
    }

    /**
     * 新增失地居民信息（智能处理基础信息）
     *
     * @param formDto 失地居民表单数据
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertLandLossResident(LandLossResidentFormDto formDto)
    {
        // 处理基础信息
        Long subsidyPersonId = handleSubsidyPersonInfo(formDto);

        // 创建失地居民信息
        LandLossResident landLossResident = new LandLossResident();
        landLossResident.setSubsidyPersonId(subsidyPersonId);
        landLossResident.setLandRequisitionTime(formDto.getLandRequisitionTime());
        landLossResident.setCompensationCompleteTime(formDto.getCompensationCompleteTime());
        landLossResident.setRecognitionTime(formDto.getRecognitionTime());
        landLossResident.setRemark(formDto.getRemark());
        landLossResident.setCreateTime(LocalDateTime.now());
        landLossResident.setCreateBy(SecurityUtils.getUsername());

        return landLossResidentMapper.insert(landLossResident);
    }

    /**
     * 修改失地居民信息（智能处理基础信息）
     *
     * @param formDto 失地居民表单数据
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateLandLossResident(LandLossResidentFormDto formDto)
    {
        // 处理基础信息
        Long subsidyPersonId = handleSubsidyPersonInfo(formDto);

        // 更新失地居民信息
        LandLossResident landLossResident = new LandLossResident();
        landLossResident.setId(formDto.getId());
        landLossResident.setSubsidyPersonId(subsidyPersonId);
        landLossResident.setLandRequisitionTime(formDto.getLandRequisitionTime());
        landLossResident.setCompensationCompleteTime(formDto.getCompensationCompleteTime());
        landLossResident.setRecognitionTime(formDto.getRecognitionTime());
        landLossResident.setRemark(formDto.getRemark());
        landLossResident.setUpdateTime(LocalDateTime.now());
        landLossResident.setUpdateBy(SecurityUtils.getUsername());

        return landLossResidentMapper.updateById(landLossResident);
    }

    /**
     * 批量删除失地居民信息
     *
     * @param ids 需要删除的失地居民信息主键
     * @return 结果
     */
    @Override
    public int deleteLandLossResidentByIds(Long[] ids)
    {
        // 逻辑删除
        for (Long id : ids)
        {
            // 检查是否有未删除的发放记录
            int count = subsidyDistributionMapper.checkUndeletedDistributions("1", id);
            if (count > 0) {
                throw new ServiceException("该失地居民存在未删除的补贴发放记录，无法删除");
            }

            LandLossResident landLossResident = new LandLossResident();
            landLossResident.setId(id);
            landLossResident.setDelFlag("2");
            landLossResident.setUpdateTime(LocalDateTime.now());
            landLossResident.setUpdateBy(SecurityUtils.getUsername());
            landLossResidentMapper.updateById(landLossResident);
        }

        return ids.length;
    }

    /**
     * 删除失地居民信息信息
     *
     * @param id 失地居民信息主键
     * @return 结果
     */
    @Override
    public int deleteLandLossResidentById(Long id)
    {
        return deleteLandLossResidentByIds(new Long[] { id });
    }

    /**
     * 根据身份证号查询基础信息并自动填充
     *
     * @param idCardNo 身份证号
     * @return 表单数据
     */
    @Override
    public LandLossResidentFormDto getFormDataByIdCardNo(String idCardNo)
    {
        LandLossResidentFormDto formDto = new LandLossResidentFormDto();

        if (StringUtils.isEmpty(idCardNo))
        {
            formDto.setPersonExists(false);
            return formDto;
        }

        // 查询基础信息（选人/回填时排除注销人员）
        SubsidyPerson subsidyPerson = subsidyPersonService.selectAliveSubsidyPersonByIdCardNo(idCardNo);

        if (subsidyPerson != null)
        {
            // 基础信息存在，自动填充
            formDto.setPersonExists(true);
            formDto.setSubsidyPersonId(subsidyPerson.getId());
            formDto.setName(subsidyPerson.getName());
            formDto.setGender(subsidyPerson.getGender());
            formDto.setIdCardNo(subsidyPerson.getIdCardNo());
            formDto.setBirthday(subsidyPerson.getBirthday());
            formDto.setHouseholdRegistration(subsidyPerson.getHouseholdRegistration());
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
            // 基础信息不存在，只设置身份证号和解析的生日
            formDto.setPersonExists(false);
            formDto.setIdCardNo(idCardNo);

            // 自动解析生日
            LocalDate birthday = parseBirthdayFromIdCard(idCardNo);
            if (birthday != null)
            {
                formDto.setBirthday(birthday);
            }
        }

        return formDto;
    }

    /**
     * 批量导入失地居民信息
     *
     * @param landLossResidentList 失地居民信息列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importLandLossResident(List<LandLossResidentFormDto> landLossResidentList, Boolean isUpdateSupport, String operName)
    {
        if (CollectionUtils.isEmpty(landLossResidentList))
        {
            throw new RuntimeException("导入失地居民数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (LandLossResidentFormDto formDto : landLossResidentList)
        {
            try
            {
                formDto.setPersonExists(false); // 导入时重新检查
                this.insertLandLossResident(formDto);
                successNum++;
                successMsg.append("<br/>" + successNum + "、失地居民 " + formDto.getName() + " 导入成功");
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、失地居民 " + formDto.getName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
            }
        }

        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new RuntimeException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }

        return successMsg.toString();
    }

    /**
     * 智能处理基础信息（新增或更新）
     *
     * @param formDto 表单数据
     * @return 被补贴人ID
     */
    private Long handleSubsidyPersonInfo(LandLossResidentFormDto formDto)
    {
        if (StringUtils.isEmpty(formDto.getIdCardNo()))
        {
            throw new RuntimeException("身份证号不能为空");
        }

        // 查询基础信息是否存在
        SubsidyPerson existingPerson = subsidyPersonService.selectSubsidyPersonByIdCardNo(formDto.getIdCardNo());

        if (existingPerson != null)
        {
            if (StringUtils.equals(existingPerson.getIsAlive(), "0"))
            {
                throw new ServiceException("该人员已注销（死亡），不能办理失地居民登记");
            }
            // 基础信息已存在，检查是否需要更新
            if (formDto.getPersonExists() != null && formDto.getPersonExists())
            {
                // 用户知道基础信息存在，且可能修改了基础信息，需要更新
                updateExistingSubsidyPerson(existingPerson, formDto);
            }
            return existingPerson.getId();
        }
        else
        {
            // 基础信息不存在，自动创建
            return createNewSubsidyPerson(formDto);
        }
    }

    /**
     * 更新已存在的基础信息
     *
     * @param existingPerson 已存在的基础信息
     * @param formDto 表单数据
     */
    private void updateExistingSubsidyPerson(SubsidyPerson existingPerson, LandLossResidentFormDto formDto)
    {
        boolean needUpdate = false;

        // 检查各字段是否需要更新
        //  姓名
        if (!StringUtils.equals(existingPerson.getName(), formDto.getName()))
        {
            existingPerson.setName(formDto.getName());
            needUpdate = true;
        }
        //  性别
        if (!StringUtils.equals(existingPerson.getGender(), formDto.getGender()))
        {
            existingPerson.setGender(formDto.getGender());
            needUpdate = true;
        }
        // 生日
        if (!Objects.equals(existingPerson.getBirthday(), formDto.getBirthday()))
        {
            existingPerson.setBirthday(formDto.getBirthday());
            needUpdate = true;
        }
        // 所属街道办ID
        if (!Objects.equals(existingPerson.getStreetOfficeId(), formDto.getStreetOfficeId()))
        {
            existingPerson.setStreetOfficeId(formDto.getStreetOfficeId());
            needUpdate = true;
        }
        // 所属村委会ID
        if (!Objects.equals(existingPerson.getVillageCommitteeId(), formDto.getVillageCommitteeId()))
        {
            existingPerson.setVillageCommitteeId(formDto.getVillageCommitteeId());
            needUpdate = true;
        }
        //  户籍所在地
        if (!StringUtils.equals(existingPerson.getHouseholdRegistration(), formDto.getHouseholdRegistration()))
        {
            existingPerson.setHouseholdRegistration(formDto.getHouseholdRegistration());
            needUpdate = true;
        }
        //  家庭住址
        if (!StringUtils.equals(existingPerson.getHomeAddress(), formDto.getHomeAddress()))
        {
            existingPerson.setHomeAddress(formDto.getHomeAddress());
            needUpdate = true;
        }
        //  联系电话
        if (!StringUtils.equals(existingPerson.getPhone(), formDto.getPhone()))
        {
            existingPerson.setPhone(formDto.getPhone());
            needUpdate = true;
        }
        //  是否健在
        if (!StringUtils.equals(existingPerson.getIsAlive(), formDto.getIsAlive()))
        {
            existingPerson.setIsAlive(formDto.getIsAlive());
            needUpdate = true;
        }
        //  不健在，且死亡时间不相等
        if (StringUtils.equals(formDto.getIsAlive(), "0") && !Objects.equals(existingPerson.getDeathDate(), formDto.getDeathDate()))
        {
            existingPerson.setDeathDate(formDto.getDeathDate());
            needUpdate = true;
        }
        //  是否村合作经济组织成员
        if (!StringUtils.equals(existingPerson.getIsVillageCoopMember(), formDto.getIsVillageCoopMember()))
        {
            existingPerson.setIsVillageCoopMember(formDto.getIsVillageCoopMember());
            needUpdate = true;
        }

        // 如果有变化，执行更新
        if (needUpdate)
        {
            subsidyPersonService.updateSubsidyPerson(existingPerson);
        }
    }

    /**
     * 创建新的基础信息
     *
     * @param formDto 表单数据
     * @return 新创建的被补贴人ID
     */
    private Long createNewSubsidyPerson(LandLossResidentFormDto formDto)
    {
        SubsidyPerson newPerson = new SubsidyPerson();
        newPerson.setName(formDto.getName());
        newPerson.setGender(formDto.getGender());
        newPerson.setIdCardNo(formDto.getIdCardNo());
        newPerson.setBirthday(formDto.getBirthday());
        newPerson.setHouseholdRegistration(formDto.getHouseholdRegistration());
        newPerson.setPhone(formDto.getPhone());
        newPerson.setIsAlive(formDto.getIsAlive());
        newPerson.setDeathDate(formDto.getDeathDate());
        newPerson.setIsVillageCoopMember(formDto.getIsVillageCoopMember());
        newPerson.setStreetOfficeId(formDto.getStreetOfficeId());
        newPerson.setVillageCommitteeId(formDto.getVillageCommitteeId());
        newPerson.setUserCode(formDto.getUserCode());
        newPerson.setStatus("0");

        subsidyPersonService.insertSubsidyPerson(newPerson);
        return newPerson.getId();
    }

    /**
     * 从身份证号解析生日
     *
     * @param idCardNo 身份证号
     * @return 生日
     */
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