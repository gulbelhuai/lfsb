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
import com.ruoyi.shebao.constant.SubsidyApprovalStatus;
import com.ruoyi.shebao.mapper.LandLossResidentMapper;
import com.ruoyi.shebao.mapper.PaymentPlanDetailMapper;
import com.ruoyi.shebao.service.LandLossResidentService;
import com.ruoyi.shebao.service.support.SubsidyPersonRegistrationHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private SubsidyPersonServiceImpl subsidyPersonService;

    @Autowired
    private PaymentPlanDetailMapper paymentPlanDetailMapper;

    @Autowired
    private SubsidyPersonRegistrationHelper subsidyPersonRegistrationHelper;

    /**
     * 通过代理调用本类事务方法，避免 this 调用导致 @Transactional 失效。
     */
    @Lazy
    @Autowired
    private LandLossResidentService landLossResidentService;

    /**
     * 查询失地居民信息列表
     *
     * @param req 查询条件
     * @return 失地居民信息列表
     */
    @Override
    public Page<LandLossResidentListResp> selectLandLossResidentList(LandLossResidentListReq req)
    {
        long pageNum = req == null ? 1L : req.pageNumOrDefault();
        long pageSize = req == null ? 10L : req.pageSizeOrDefault();
        Page<LandLossResidentListResp> page = new Page<>(pageNum, pageSize);
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
            formDto.setPersonExists(formDto.getSubsidyPersonId() != null);
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
        SubsidyPerson existingByCard = subsidyPersonService.selectSubsidyPersonByIdCardNo(formDto.getIdCardNo());
        if (existingByCard != null)
        {
            long duplicateCount = this.lambdaQuery()
                    .eq(LandLossResident::getSubsidyPersonId, existingByCard.getId())
                    .eq(LandLossResident::getDelFlag, "0")
                    .ne(Objects.nonNull(formDto.getId()), LandLossResident::getId, formDto.getId())
                    .count();
            if (duplicateCount > 0)
            {
                throw new ServiceException("该人员已被认定为失地居民，请核实后录入");
            }
        }
        Long subsidyPersonId = subsidyPersonRegistrationHelper.resolveSubsidyPersonForCreate(formDto, personId -> false);

        LandLossResident landLossResident = new LandLossResident();
        landLossResident.setSubsidyPersonId(subsidyPersonId);
        landLossResident.setLandRequisitionTime(formDto.getLandRequisitionTime());
        landLossResident.setCompensationCompleteTime(formDto.getCompensationCompleteTime());
        landLossResident.setRecognitionTime(formDto.getRecognitionTime());
        landLossResident.setLandRequisitionBatch(formDto.getLandRequisitionBatch());
        landLossResident.setVillageStreet(formDto.getVillageStreet());
        landLossResident.setIsVillageCoopMember(formDto.getIsVillageCoopMember());
        landLossResident.setRemark(formDto.getRemark());
        landLossResident.setApprovalStatus(SubsidyApprovalStatus.PENDING_REVIEW);
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
        LandLossResident existing = landLossResidentMapper.selectById(formDto.getId());
        if (existing == null || !"0".equals(existing.getDelFlag()))
        {
            throw new ServiceException("失地居民记录不存在");
        }
        if (SubsidyApprovalStatus.isApproved(existing.getApprovalStatus()))
        {
            throw new ServiceException("已通过复核，不能修改");
        }
        subsidyPersonRegistrationHelper.resolveSubsidyPersonForUpdate(existing.getSubsidyPersonId());

        LandLossResident landLossResident = new LandLossResident();
        landLossResident.setId(formDto.getId());
        landLossResident.setLandRequisitionTime(formDto.getLandRequisitionTime());
        landLossResident.setCompensationCompleteTime(formDto.getCompensationCompleteTime());
        landLossResident.setRecognitionTime(formDto.getRecognitionTime());
        landLossResident.setLandRequisitionBatch(formDto.getLandRequisitionBatch());
        landLossResident.setVillageStreet(formDto.getVillageStreet());
        landLossResident.setIsVillageCoopMember(formDto.getIsVillageCoopMember());
        landLossResident.setRemark(formDto.getRemark());
        landLossResident.setApprovalStatus(SubsidyApprovalStatus.PENDING_REVIEW);
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
            LandLossResident existing = landLossResidentMapper.selectById(id);
            if (existing != null && existing.getSubsidyPersonId() != null
                    && paymentPlanDetailMapper.countUndeletedBySubsidyPersonId(existing.getSubsidyPersonId()) > 0)
            {
                throw new ServiceException("该失地居民存在未删除的支付计划发放明细，无法删除");
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
            formDto.setHomeAddress(subsidyPerson.getHomeAddress());
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
                landLossResidentService.insertLandLossResident(formDto);
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