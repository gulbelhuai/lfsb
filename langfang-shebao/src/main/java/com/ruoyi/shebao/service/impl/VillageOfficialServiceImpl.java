package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.VillageOfficial;
import com.ruoyi.shebao.domain.VillageOfficialPosition;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.dto.VillageOfficialListReq;
import com.ruoyi.shebao.dto.VillageOfficialListResp;
import com.ruoyi.shebao.dto.VillageOfficialFormDto;
import com.ruoyi.shebao.mapper.SubsidyDistributionMapper;
import com.ruoyi.shebao.mapper.VillageOfficialMapper;
import com.ruoyi.shebao.mapper.VillageOfficialPositionMapper;
import com.ruoyi.shebao.service.VillageOfficialService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import com.ruoyi.shebao.service.VillageCommitteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    private SubsidyDistributionMapper subsidyDistributionMapper;

    @Autowired
    private VillageCommitteeService villageCommitteeService;


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
        // 处理基础信息
        Long subsidyPersonId = handleSubsidyPersonInfo(formDto);

        // 创建村干部信息
        VillageOfficial villageOfficial = new VillageOfficial();
        villageOfficial.setSubsidyPersonId(subsidyPersonId);
        villageOfficial.setSubsidyAmount(formDto.getSubsidyAmount());
        villageOfficial.setHasViolation(formDto.getHasViolation());
        villageOfficial.setVillageStreet(formDto.getVillageStreet());
        villageOfficial.setRemark(formDto.getRemark());
        villageOfficial.setCreateTime(LocalDateTime.now());
        villageOfficial.setCreateBy(SecurityUtils.getUsername());

        int result = villageOfficialMapper.insert(villageOfficial);

        // 处理任职信息
        if (formDto.getPositionList() != null && !formDto.getPositionList().isEmpty())
        {
            handlePositionList(villageOfficial.getId(), formDto.getPositionList());
        }

        markPersonPendingReview(subsidyPersonId);
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
        // 处理基础信息
        Long subsidyPersonId = handleSubsidyPersonInfo(formDto);

        // 更新村干部信息
        VillageOfficial villageOfficial = new VillageOfficial();
        villageOfficial.setId(formDto.getId());
        villageOfficial.setSubsidyPersonId(subsidyPersonId);
        villageOfficial.setSubsidyAmount(formDto.getSubsidyAmount());
        villageOfficial.setHasViolation(formDto.getHasViolation());
        villageOfficial.setVillageStreet(formDto.getVillageStreet());
        villageOfficial.setRemark(formDto.getRemark());
        villageOfficial.setUpdateTime(LocalDateTime.now());
        villageOfficial.setUpdateBy(SecurityUtils.getUsername());

        int result = villageOfficialMapper.updateById(villageOfficial);

        // 处理任职信息（覆盖方式）
        // 先删除原有任职信息
        villageOfficialPositionMapper.deleteByVillageOfficialId(formDto.getId());

        // 再插入新的任职信息
        if (formDto.getPositionList() != null && !formDto.getPositionList().isEmpty())
        {
            handlePositionList(formDto.getId(), formDto.getPositionList());
        }

        markPersonPendingReview(subsidyPersonId);
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
            // 检查是否有未删除的发放记录
            int count = subsidyDistributionMapper.checkUndeletedDistributions("4", id);
            if (count > 0) {
                throw new ServiceException("该村干部存在未删除的补贴发放记录，无法删除");
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
        if (StringUtils.isNull(villageOfficialList) || villageOfficialList.size() == 0)
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

    /**
     * 智能处理基础信息（新增或更新）
     *
     * @param formDto 表单数据
     * @return 被补贴人ID
     */
    private Long handleSubsidyPersonInfo(VillageOfficialFormDto formDto)
    {
        normalizeDivisionFields(formDto);
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
                throw new ServiceException("该人员已注销（死亡），不能办理村干部登记");
            }
            VillageOfficial existOfficial = this.lambdaQuery()
                .eq(VillageOfficial::getSubsidyPersonId, existingPerson.getId())
                .ne(Objects.nonNull(formDto.getId()), VillageOfficial::getId, formDto.getId())
                .one();
            if (existOfficial != null)
            {
                throw new ServiceException("该人员已被认定为村干部，请核实后录入");
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
    private void updateExistingSubsidyPerson(SubsidyPerson existingPerson, VillageOfficialFormDto formDto)
    {
        boolean needUpdate = false;

        // 检查各字段是否需要更新
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
        if (!Objects.equals(existingPerson.getDeathDate(), formDto.getDeathDate()))
        {
            existingPerson.setDeathDate(formDto.getDeathDate());
            needUpdate = true;
        }
        if (!StringUtils.equals(existingPerson.getIsVillageCoopMember(), formDto.getIsVillageCoopMember()))
        {
            existingPerson.setIsVillageCoopMember(formDto.getIsVillageCoopMember());
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
        if (!StringUtils.equals(existingPerson.getUserCode(), formDto.getUserCode()))
        {
            existingPerson.setUserCode(formDto.getUserCode());
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
    private Long createNewSubsidyPerson(VillageOfficialFormDto formDto)
    {
        SubsidyPerson newPerson = new SubsidyPerson();
        newPerson.setName(formDto.getName());
        newPerson.setGender(formDto.getGender());
        newPerson.setIdCardNo(formDto.getIdCardNo());
        newPerson.setBirthday(formDto.getBirthday());
        newPerson.setHouseholdRegistration(formDto.getHouseholdRegistration());
        newPerson.setHomeAddress(formDto.getHomeAddress());
        newPerson.setPhone(formDto.getPhone());
        newPerson.setIsAlive(StringUtils.isNotEmpty(formDto.getIsAlive()) ? formDto.getIsAlive() : "1");
        newPerson.setDeathDate(formDto.getDeathDate());
        newPerson.setIsVillageCoopMember(StringUtils.isNotEmpty(formDto.getIsVillageCoopMember()) ? formDto.getIsVillageCoopMember() : "1");
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
        for (VillageOfficialFormDto.VillageOfficialPositionDto dto : positionDtoList)
        {
            VillageOfficialPosition position = new VillageOfficialPosition();
            position.setVillageOfficialId(villageOfficialId);
            position.setPosition(dto.getPosition());
            position.setStartDate(dto.getStartDate());
            position.setEndDate(dto.getEndDate());
            position.setServiceYears(computePositionServiceYears(dto.getStartDate(), dto.getEndDate()));
            position.setStatus(dto.getStatus() != null ? dto.getStatus() : "0");
            position.setRemark(dto.getRemark());
            position.setCreateTime(LocalDateTime.now());
            position.setCreateBy(SecurityUtils.getUsername());
            positionList.add(position);
        }

        villageOfficialPositionMapper.batchInsertPositions(positionList);
    }

    /**
     * 任职年限：完整周年数（与 ChronoUnit.YEARS.between 一致）+ 余下日历天数/365，结果保留两位小数。
     */
    private static BigDecimal computePositionServiceYears(LocalDate startDate, LocalDate endDate)
    {
        if (startDate == null)
        {
            return null;
        }
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        if (end.isBefore(startDate))
        {
            return null;
        }
        long fullYears = ChronoUnit.YEARS.between(startDate, end);
        LocalDate afterFullYears = startDate.plusYears(fullYears);
        long remainderDays = ChronoUnit.DAYS.between(afterFullYears, end);
        BigDecimal full = BigDecimal.valueOf(fullYears);
        BigDecimal fraction = BigDecimal.valueOf(remainderDays)
            .divide(BigDecimal.valueOf(365), 8, RoundingMode.HALF_UP);
        return full.add(fraction).setScale(2, RoundingMode.HALF_UP);
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
