package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.VillageOfficial;
import com.ruoyi.shebao.domain.VillageOfficialPosition;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.dto.VillageOfficialListReq;
import com.ruoyi.shebao.dto.VillageOfficialListResp;
import com.ruoyi.shebao.dto.VillageOfficialFormDto;
import com.ruoyi.shebao.constant.SubsidyApprovalStatus;
import com.ruoyi.shebao.mapper.PaymentPlanDetailMapper;
import com.ruoyi.shebao.mapper.VillageOfficialMapper;
import com.ruoyi.shebao.mapper.VillageOfficialPositionMapper;
import com.ruoyi.shebao.service.SubsidyCalculationService;
import com.ruoyi.shebao.service.support.SubsidyPersonRegistrationHelper;
import com.ruoyi.shebao.service.VillageOfficialService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import com.ruoyi.shebao.service.VillageCommitteeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 村干部信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-27
 */
@Service
public class VillageOfficialServiceImpl extends ServiceImpl<VillageOfficialMapper, VillageOfficial> implements VillageOfficialService
{
    @Autowired
    private VillageOfficialMapper villageOfficialMapper;

    @Autowired
    private VillageOfficialPositionMapper villageOfficialPositionMapper;

    @Autowired
    private SubsidyPersonService subsidyPersonService;

    @Autowired
    private PaymentPlanDetailMapper paymentPlanDetailMapper;

    @Autowired
    private VillageCommitteeService villageCommitteeService;

    @Autowired
    private SubsidyCalculationService subsidyCalculationService;

    @Autowired
    private SubsidyPersonRegistrationHelper subsidyPersonRegistrationHelper;


    /**
     * 查询村干部信息列表
     *
     * @param req 查询条件
     * @return 村干部信息列表
     */
    @Override
    public Page<VillageOfficialListResp> selectVillageOfficialList(VillageOfficialListReq req)
    {
        Page<VillageOfficialListResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return villageOfficialMapper.selectVillageOfficialList(page, req);
    }

    /**
     * 查询村干部信息详情（包含基础信息和任职信息）
     *
     * @param id 村干部信息主键
     * @return 村干部信息
     */
    @Override
    public VillageOfficialFormDto selectVillageOfficialFormById(Long id)
    {
        VillageOfficialFormDto formDto = villageOfficialMapper.selectVillageOfficialFormById(id);
        if (formDto != null)
        {
            formDto.setPersonExists(true);
            formDto.setNativePlace(formDto.getHouseholdRegistration());
            if (formDto.getVillageCommitteeId() != null)
            {
                VillageCommittee villageCommittee = villageCommitteeService.getById(formDto.getVillageCommitteeId());
                if (villageCommittee != null)
                {
                    formDto.setVillageCode(villageCommittee.getVillageCode());
                    formDto.setVillageName(villageCommittee.getVillageName());
                }
            }
            // 查询任职信息列表
            List<VillageOfficialFormDto.VillageOfficialPositionDto> positionList =
                villageOfficialPositionMapper.selectByVillageOfficialId(id);
            formDto.setPositionList(positionList);
        }
        return formDto;
    }

    /**
     * 新增村干部信息（智能处理基础信息和任职信息）
     *
     * @param formDto 村干部表单数据
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertVillageOfficial(VillageOfficialFormDto formDto)
    {
        calculateVillageOfficialBenefit(formDto);
        normalizeDivisionFields(formDto);
        SubsidyPerson existingByCard = subsidyPersonService.selectSubsidyPersonByIdCardNo(formDto.getIdCardNo());
        if (existingByCard != null)
        {
            VillageOfficial existOfficial = this.lambdaQuery()
                    .eq(VillageOfficial::getSubsidyPersonId, existingByCard.getId())
                    .eq(VillageOfficial::getDelFlag, "0")
                    .ne(Objects.nonNull(formDto.getId()), VillageOfficial::getId, formDto.getId())
                    .one();
            if (existOfficial != null)
            {
                throw new ServiceException("该人员已被认定为村干部，请核实后录入");
            }
        }
        Long subsidyPersonId = subsidyPersonRegistrationHelper.resolveSubsidyPersonForCreate(formDto, personId -> false, person -> {
            person.setIsAlive(StringUtils.isNotEmpty(formDto.getIsAlive()) ? formDto.getIsAlive() : "1");
            person.setDeathDate(formDto.getDeathDate());
            person.setIsVillageCoopMember(StringUtils.isNotEmpty(formDto.getIsVillageCoopMember()) ? formDto.getIsVillageCoopMember() : "1");
        });

        VillageOfficial villageOfficial = new VillageOfficial();
        villageOfficial.setSubsidyPersonId(subsidyPersonId);
        villageOfficial.setSubsidyAmount(formDto.getSubsidyAmount());
        villageOfficial.setHasViolation(formDto.getHasViolation());
        villageOfficial.setVillageStreet(formDto.getVillageStreet());
        villageOfficial.setRemark(formDto.getRemark());
        villageOfficial.setApprovalStatus(SubsidyApprovalStatus.PENDING_REVIEW);
        villageOfficial.setCreateTime(LocalDateTime.now());
        villageOfficial.setCreateBy(SecurityUtils.getUsername());

        int result = villageOfficialMapper.insert(villageOfficial);

        if (formDto.getPositionList() != null && !formDto.getPositionList().isEmpty())
        {
            handlePositionList(villageOfficial.getId(), formDto.getPositionList());
        }
        return result;
    }

    /**
     * 修改村干部信息（智能处理基础信息和任职信息）
     *
     * @param formDto 村干部表单数据
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateVillageOfficial(VillageOfficialFormDto formDto)
    {
        calculateVillageOfficialBenefit(formDto);
        normalizeDivisionFields(formDto);
        VillageOfficial existing = villageOfficialMapper.selectById(formDto.getId());
        if (existing == null || !"0".equals(existing.getDelFlag()))
        {
            throw new ServiceException("村干部记录不存在");
        }
        if (SubsidyApprovalStatus.isApproved(existing.getApprovalStatus()))
        {
            throw new ServiceException("已通过复核，不能修改");
        }
        subsidyPersonRegistrationHelper.resolveSubsidyPersonForUpdate(existing.getSubsidyPersonId());

        VillageOfficial villageOfficial = new VillageOfficial();
        villageOfficial.setId(formDto.getId());
        villageOfficial.setSubsidyAmount(formDto.getSubsidyAmount());
        villageOfficial.setHasViolation(formDto.getHasViolation());
        villageOfficial.setVillageStreet(formDto.getVillageStreet());
        villageOfficial.setRemark(formDto.getRemark());
        villageOfficial.setApprovalStatus(SubsidyApprovalStatus.PENDING_REVIEW);
        villageOfficial.setUpdateTime(LocalDateTime.now());
        villageOfficial.setUpdateBy(SecurityUtils.getUsername());

        int result = villageOfficialMapper.updateById(villageOfficial);

        villageOfficialPositionMapper.deleteByVillageOfficialId(formDto.getId());
        if (formDto.getPositionList() != null && !formDto.getPositionList().isEmpty())
        {
            handlePositionList(formDto.getId(), formDto.getPositionList());
        }
        return result;
    }

    /**
     * 批量删除村干部信息
     *
     * @param ids 需要删除的村干部信息主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteVillageOfficialByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            VillageOfficial existing = villageOfficialMapper.selectById(id);
            if (existing != null && existing.getSubsidyPersonId() != null
                    && paymentPlanDetailMapper.countUndeletedBySubsidyPersonId(existing.getSubsidyPersonId()) > 0)
            {
                throw new ServiceException("该村干部存在未删除的支付计划发放明细，无法删除");
            }

            // 逻辑删除村干部信息
            VillageOfficial villageOfficial = new VillageOfficial();
            villageOfficial.setId(id);
            villageOfficial.setDelFlag("2");
            villageOfficial.setUpdateTime(LocalDateTime.now());
            villageOfficial.setUpdateBy(SecurityUtils.getUsername());
            villageOfficialMapper.updateById(villageOfficial);

            // 删除任职信息
            villageOfficialPositionMapper.deleteByVillageOfficialId(id);
        }

        return ids.length;
    }

    @Override
    public VillageOfficialFormDto calculateVillageOfficialBenefit(VillageOfficialFormDto formDto)
    {
        if (formDto == null)
        {
            return null;
        }
        List<VillageOfficialFormDto.VillageOfficialPositionDto> normalized = normalizeAndComputePositionList(formDto.getPositionList());
        formDto.setPositionList(normalized);
        formDto.setSubsidyAmount(subsidyCalculationService.calculateVillageOfficialSubsidyAmount(formDto.getIdCardNo(), normalized));
        return formDto;
    }

    /**
     * 删除村干部信息信息
     *
     * @param id 村干部信息主键
     * @return 结果
     */
    @Override
    public int deleteVillageOfficialById(Long id)
    {
        return deleteVillageOfficialByIds(new Long[] { id });
    }

    /**
     * 根据身份证号查询基础信息并自动填充
     *
     * @param idCardNo 身份证号
     * @return 表单数据
     */
    @Override
    public VillageOfficialFormDto getFormDataByIdCardNo(String idCardNo)
    {
        VillageOfficialFormDto formDto = new VillageOfficialFormDto();

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
            formDto.setNativePlace(subsidyPerson.getHouseholdRegistration());
            formDto.setHomeAddress(subsidyPerson.getHomeAddress());
            formDto.setPhone(subsidyPerson.getPhone());
            formDto.setIsAlive(subsidyPerson.getIsAlive());
            formDto.setDeathDate(subsidyPerson.getDeathDate());
            formDto.setIsVillageCoopMember(subsidyPerson.getIsVillageCoopMember());
            formDto.setStreetOfficeId(subsidyPerson.getStreetOfficeId());
            formDto.setVillageCommitteeId(subsidyPerson.getVillageCommitteeId());
            formDto.setUserCode(subsidyPerson.getUserCode());
            if (subsidyPerson.getVillageCommitteeId() != null)
            {
                VillageCommittee villageCommittee = villageCommitteeService.getById(subsidyPerson.getVillageCommitteeId());
                if (villageCommittee != null)
                {
                    formDto.setVillageCode(villageCommittee.getVillageCode());
                    formDto.setVillageName(villageCommittee.getVillageName());
                }
            }
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

        // 设置默认值
        formDto.setHasViolation("0");
        formDto.setStatus("0");

        return formDto;
    }

    /**
     * 批量导入村干部信息
     *
     * @param villageOfficialList 村干部信息列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importVillageOfficial(List<VillageOfficialFormDto> villageOfficialList, Boolean isUpdateSupport, String operName)
    {
        if (CollectionUtils.isEmpty(villageOfficialList))
        {
            throw new RuntimeException("导入村干部数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();

        for (VillageOfficialFormDto formDto : villageOfficialList)
        {
            try
            {
                formDto.setPersonExists(false); // 导入时重新检查
                this.insertVillageOfficial(formDto);
                successNum++;
                successMsg.append("<br/>" + successNum + "、村干部 " + formDto.getName() + " 导入成功");
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、村干部 " + formDto.getName() + " 导入失败：";
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

    private void normalizeDivisionFields(VillageOfficialFormDto formDto)
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

    /**
     * 处理任职信息列表
     *
     * @param villageOfficialId 村干部信息ID
     * @param positionDtoList 任职信息DTO列表
     */
    private void handlePositionList(Long villageOfficialId, List<VillageOfficialFormDto.VillageOfficialPositionDto> positionDtoList)
    {
        if (positionDtoList == null || positionDtoList.isEmpty())
        {
            return;
        }

        List<VillageOfficialPosition> positionList = new ArrayList<>();
        for (VillageOfficialFormDto.VillageOfficialPositionDto dto : normalizeAndComputePositionList(positionDtoList))
        {
            VillageOfficialPosition position = new VillageOfficialPosition();
            position.setVillageOfficialId(villageOfficialId);
            position.setPosition(dto.getPosition());
            position.setStartDate(dto.getStartDate());
            position.setEndDate(dto.getEndDate());
            position.setServiceYears(dto.getServiceYears());
            position.setStatus(dto.getStatus() != null ? dto.getStatus() : "0");
            position.setRemark(dto.getRemark());
            position.setCreateTime(LocalDateTime.now());
            position.setCreateBy(SecurityUtils.getUsername());
            positionList.add(position);
        }

        villageOfficialPositionMapper.batchInsertPositions(positionList);
    }

    private List<VillageOfficialFormDto.VillageOfficialPositionDto> normalizeAndComputePositionList(
        List<VillageOfficialFormDto.VillageOfficialPositionDto> positionDtoList)
    {
        if (positionDtoList == null || positionDtoList.isEmpty())
        {
            return new ArrayList<>();
        }
        List<VillageOfficialFormDto.VillageOfficialPositionDto> normalized = new ArrayList<>();
        int idx = 0;
        for (VillageOfficialFormDto.VillageOfficialPositionDto dto : positionDtoList)
        {
            idx++;
            if (dto == null || StringUtils.isBlank(dto.getPosition()) || dto.getStartDate() == null || dto.getEndDate() == null)
            {
                throw new ServiceException("第" + idx + "行任职信息：任职职位、上任时间、卸任时间均不能为空");
            }
            VillageOfficialFormDto.VillageOfficialPositionDto item = new VillageOfficialFormDto.VillageOfficialPositionDto();
            item.setId(dto.getId());
            item.setVillageOfficialId(dto.getVillageOfficialId());
            item.setPosition(dto.getPosition());
            item.setStatus(dto.getStatus());
            item.setRemark(dto.getRemark());
            item.setStartDate(normalizeToFirstDay(dto.getStartDate()));
            item.setEndDate(normalizeToFirstDay(dto.getEndDate()));
            item.setServiceYears(subsidyCalculationService.computePositionServiceYears(item.getStartDate(), item.getEndDate()));
            normalized.add(item);
        }
        return normalized;
    }

    private static LocalDate normalizeToFirstDay(LocalDate date)
    {
        if (date == null)
        {
            return null;
        }
        return date.withDayOfMonth(1);
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
