package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.*;
import com.ruoyi.shebao.dto.AvailableSubsidyDto;
import com.ruoyi.shebao.dto.SubsidyDistributionListReq;
import com.ruoyi.shebao.dto.SubsidyDistributionListResp;
import com.ruoyi.shebao.dto.SubsidyDistributionFormDto;
import com.ruoyi.shebao.enums.DistributionStatusEnum;
import com.ruoyi.shebao.enums.SubsidyTypeEnum;
import com.ruoyi.shebao.mapper.*;
import com.ruoyi.shebao.service.DistributionReviewService;
import com.ruoyi.shebao.service.SubsidyDistributionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 补贴发放记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
@Service
public class SubsidyDistributionServiceImpl extends ServiceImpl<SubsidyDistributionMapper, SubsidyDistribution> implements SubsidyDistributionService
{
    @Autowired
    private SubsidyDistributionMapper subsidyDistributionMapper;

    @Autowired
    private SubsidyPersonMapper subsidyPersonMapper;

    @Autowired
    private LandLossResidentMapper landLossResidentMapper;

    @Autowired
    private ExpropriateeSubsidyMapper expropriateeSubsidyMapper;

    @Autowired
    private DemolitionResidentMapper demolitionResidentMapper;

    @Autowired
    private VillageOfficialMapper villageOfficialMapper;

    @Autowired
    private DistributionReviewService distributionReviewService;

    @Autowired
    private com.ruoyi.shebao.mapper.StreetOfficeMapper streetOfficeMapper;

    @Autowired
    private com.ruoyi.shebao.mapper.VillageCommitteeMapper villageCommitteeMapper;

    @Autowired
    private SubsidyPersonServiceImpl subsidyPersonService;

    /**
     * 查询补贴发放记录列表
     */
    @Override
    public Page<SubsidyDistributionListResp> selectSubsidyDistributionList(SubsidyDistributionListReq req)
    {
        Page<SubsidyDistributionListResp> page = new Page<>(req.getPageNum(), req.getPageSize());
        return subsidyDistributionMapper.selectSubsidyDistributionList(page, req);
    }

    /**
     * 查询补贴发放记录详情
     */
    @Override
    public SubsidyDistributionFormDto selectSubsidyDistributionFormById(Long id)
    {
        return subsidyDistributionMapper.selectSubsidyDistributionFormById(id);
    }

    /**
     * 自动搜索居民（下拉列表）
     * 
     * 说明：此接口用于补贴发放时的选人下拉,应排除注销人员(is_alive='0')
     */
    @Override
    public List<Map<String, Object>> searchResidents(String keyword)
    {
        // 查询被补贴人信息（默认排除注销人员）
        LambdaQueryWrapper<SubsidyPerson> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(SubsidyPerson::getName, keyword).or().like(SubsidyPerson::getIdCardNo, keyword));
        wrapper.eq(SubsidyPerson::getStatus, "0");
        wrapper.eq(SubsidyPerson::getIsAlive, "1"); // 排除注销人员
        wrapper.last("LIMIT 10");
        List<SubsidyPerson> persons = subsidyPersonMapper.selectList(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (SubsidyPerson person : persons) {
            Map<String, Object> item = new HashMap<>();
            item.put("subsidyPersonId", person.getId());
            item.put("name", person.getName());
            item.put("idCardNo", person.getIdCardNo());
            item.put("label", person.getName() + " " + person.getIdCardNo());
            result.add(item);
        }
        return result;
    }

    /**
     * 根据居民ID查询可发放的补贴
     * 
     * 说明：用于补贴发放时根据人员ID获取可发放补贴,应排除注销人员
     */
    @Override
    public AvailableSubsidyDto getAvailableSubsidiesByPersonId(Long subsidyPersonId)
    {
        // 使用selectAliveSubsidyPersonById确保只查询健在人员
        SubsidyPerson person = subsidyPersonService.selectAliveSubsidyPersonById(subsidyPersonId);
        if (person == null) {
            throw new ServiceException("未找到该居民信息或该居民已注销");
        }
        return buildAvailableSubsidyDto(person);
    }

    /**
     * 根据身份证号或姓名查询可发放的补贴
     * 
     * 说明：用于补贴发放时根据身份证号或姓名获取可发放补贴,应排除注销人员
     */
    @Override
    public AvailableSubsidyDto getAvailableSubsidies(String keyword)
    {
        // 查询被补贴人信息（排除注销人员）
        LambdaQueryWrapper<SubsidyPerson> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(SubsidyPerson::getIdCardNo, keyword).or().like(SubsidyPerson::getName, keyword));
        wrapper.eq(SubsidyPerson::getStatus, "0");
        wrapper.eq(SubsidyPerson::getIsAlive, "1"); // 排除注销人员
        SubsidyPerson person = subsidyPersonMapper.selectOne(wrapper);

        if (person == null) {
            throw new ServiceException("未找到该居民信息或该居民已注销");
        }

        return buildAvailableSubsidyDto(person);
    }

    /**
     * 构建可发放补贴DTO
     */
    private AvailableSubsidyDto buildAvailableSubsidyDto(SubsidyPerson person)
    {

        AvailableSubsidyDto dto = new AvailableSubsidyDto();
        dto.setSubsidyPersonId(person.getId());
        dto.setName(person.getName());
        dto.setIdCardNo(person.getIdCardNo());
        dto.setGender(person.getGender());
        dto.setBirthday(person.getBirthday());
        dto.setHouseholdRegistration(person.getHouseholdRegistration());
        dto.setHomeAddress(person.getHomeAddress());
        dto.setPhone(person.getPhone());
        dto.setIsAlive(person.getIsAlive());
        dto.setDeathDate(person.getDeathDate());
        dto.setIsVillageCoopMember(person.getIsVillageCoopMember());
        dto.setUserCode(person.getUserCode());
        
        // 查询街道办和村委会名称
        if (person.getStreetOfficeId() != null) {
            com.ruoyi.shebao.domain.StreetOffice streetOffice = streetOfficeMapper.selectById(person.getStreetOfficeId());
            if (streetOffice != null) {
                dto.setStreetOfficeName(streetOffice.getStreetName());
            }
        }
        if (person.getVillageCommitteeId() != null) {
            com.ruoyi.shebao.domain.VillageCommittee villageCommittee = villageCommitteeMapper.selectById(person.getVillageCommitteeId());
            if (villageCommittee != null) {
                dto.setVillageCommitteeName(villageCommittee.getVillageName());
            }
        }

        List<AvailableSubsidyDto.SubsidyTypeInfo> subsidyTypes = new ArrayList<>();

        // 查询失地居民补贴
        List<LandLossResident> landLossList = landLossResidentMapper.selectList(
            new LambdaQueryWrapper<LandLossResident>()
                .eq(LandLossResident::getSubsidyPersonId, person.getId())
        );
        if (!landLossList.isEmpty()) {
            AvailableSubsidyDto.SubsidyTypeInfo typeInfo = new AvailableSubsidyDto.SubsidyTypeInfo();
            typeInfo.setSubsidyType(SubsidyTypeEnum.LAND_LOSS.getCode());
            typeInfo.setSubsidyTypeName(SubsidyTypeEnum.LAND_LOSS.getName());
            List<AvailableSubsidyDto.SubsidyRecordInfo> records = new ArrayList<>();
            for (LandLossResident item : landLossList) {
                AvailableSubsidyDto.SubsidyRecordInfo record = new AvailableSubsidyDto.SubsidyRecordInfo();
                record.setSubsidyRecordId(item.getId());
                record.setSubsidyAmount(BigDecimal.ZERO); // 失地居民不再有补贴金额字段
                record.setDistributionStatus("0"); // 失地居民不再有发放状态字段
                record.setDistributionStatusName(getStatusName(record.getDistributionStatus()));
                record.setSelectable("0".equals(record.getDistributionStatus()));
                record.setSubsidyDetail(item);
                records.add(record);
            }
            typeInfo.setRecords(records);
            subsidyTypes.add(typeInfo);
        }

        // 查询被征地参保补贴
        List<ExpropriateeSubsidy> expropriateeList = expropriateeSubsidyMapper.selectList(
            new LambdaQueryWrapper<ExpropriateeSubsidy>()
                .eq(ExpropriateeSubsidy::getSubsidyPersonId, person.getId())
        );
        if (!expropriateeList.isEmpty()) {
            AvailableSubsidyDto.SubsidyTypeInfo typeInfo = new AvailableSubsidyDto.SubsidyTypeInfo();
            typeInfo.setSubsidyType(SubsidyTypeEnum.EXPROPRIATEE.getCode());
            typeInfo.setSubsidyTypeName(SubsidyTypeEnum.EXPROPRIATEE.getName());
            List<AvailableSubsidyDto.SubsidyRecordInfo> records = new ArrayList<>();
            for (ExpropriateeSubsidy item : expropriateeList) {
                AvailableSubsidyDto.SubsidyRecordInfo record = new AvailableSubsidyDto.SubsidyRecordInfo();
                record.setSubsidyRecordId(item.getId());
                // 补贴金额和发放状态已移到补贴发放管理表，这里设置默认值
                record.setSubsidyAmount(BigDecimal.ZERO);
                record.setDistributionStatus("0");
                record.setDistributionStatusName(getStatusName(record.getDistributionStatus()));
                record.setSelectable("0".equals(record.getDistributionStatus()));
                record.setSubsidyDetail(item);
                records.add(record);
            }
            typeInfo.setRecords(records);
            subsidyTypes.add(typeInfo);
        }

        // 查询拆迁居民补贴
        List<DemolitionResident> demolitionList = demolitionResidentMapper.selectList(
            new LambdaQueryWrapper<DemolitionResident>()
                .eq(DemolitionResident::getSubsidyPersonId, person.getId())
        );
        if (!demolitionList.isEmpty()) {
            AvailableSubsidyDto.SubsidyTypeInfo typeInfo = new AvailableSubsidyDto.SubsidyTypeInfo();
            typeInfo.setSubsidyType(SubsidyTypeEnum.DEMOLITION.getCode());
            typeInfo.setSubsidyTypeName(SubsidyTypeEnum.DEMOLITION.getName());
            List<AvailableSubsidyDto.SubsidyRecordInfo> records = new ArrayList<>();
            for (DemolitionResident item : demolitionList) {
                AvailableSubsidyDto.SubsidyRecordInfo record = new AvailableSubsidyDto.SubsidyRecordInfo();
                record.setSubsidyRecordId(item.getId());
                // 补贴金额和发放状态已移到补贴发放管理表，这里设置默认值
                record.setSubsidyAmount(BigDecimal.ZERO);
                record.setDistributionStatus("0");
                record.setDistributionStatusName(getStatusName(record.getDistributionStatus()));
                record.setSelectable("0".equals(record.getDistributionStatus()));
                record.setSubsidyDetail(item);
                records.add(record);
            }
            typeInfo.setRecords(records);
            subsidyTypes.add(typeInfo);
        }

        // 查询村干部补贴
        List<VillageOfficial> villageOfficialList = villageOfficialMapper.selectList(
            new LambdaQueryWrapper<VillageOfficial>()
                .eq(VillageOfficial::getSubsidyPersonId, person.getId())
        );
        if (!villageOfficialList.isEmpty()) {
            AvailableSubsidyDto.SubsidyTypeInfo typeInfo = new AvailableSubsidyDto.SubsidyTypeInfo();
            typeInfo.setSubsidyType(SubsidyTypeEnum.VILLAGE_OFFICIAL.getCode());
            typeInfo.setSubsidyTypeName(SubsidyTypeEnum.VILLAGE_OFFICIAL.getName());
            List<AvailableSubsidyDto.SubsidyRecordInfo> records = new ArrayList<>();
            for (VillageOfficial item : villageOfficialList) {
                AvailableSubsidyDto.SubsidyRecordInfo record = new AvailableSubsidyDto.SubsidyRecordInfo();
                record.setSubsidyRecordId(item.getId());
                // 补贴金额和发放状态已移到补贴发放管理表，这里设置默认值
                record.setSubsidyAmount(BigDecimal.ZERO);
                record.setDistributionStatus("0");
                record.setDistributionStatusName(getStatusName(record.getDistributionStatus()));
                record.setSelectable("0".equals(record.getDistributionStatus()));
                record.setSubsidyDetail(item);
                records.add(record);
            }
            typeInfo.setRecords(records);
            subsidyTypes.add(typeInfo);
        }

        dto.setSubsidyTypes(subsidyTypes);
        return dto;
    }

    /**
     * 新增补贴发放记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSubsidyDistribution(SubsidyDistributionFormDto formDto)
    {
        // 验证补贴记录是否可发放
        validateSubsidyRecord(formDto.getSubsidyType(), formDto.getSubsidyRecordId());

        // 创建发放记录
        SubsidyDistribution distribution = new SubsidyDistribution();
        BeanUtils.copyProperties(formDto, distribution);
        distribution.setDistributionStatus(DistributionStatusEnum.PENDING_REVIEW.getCode());
        distribution.setStatus("0");
        distribution.setCreateTime(LocalDateTime.now());
        distribution.setCreateBy(SecurityUtils.getUsername());
        
        int result = subsidyDistributionMapper.insert(distribution);

        // 更新补贴身份表的发放状态
        updateSubsidyRecordStatus(formDto.getSubsidyType(), formDto.getSubsidyRecordId(), DistributionStatusEnum.PENDING_REVIEW.getCode());

        // 记录审核记录
        distributionReviewService.addReviewRecord(distribution.getId(), "1", StringUtils.isNotBlank(distribution.getRemark()) ? distribution.getRemark() : "提交审核", SecurityUtils.getUsername());

        return result;
    }

    /**
     * 审核通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approveDistribution(Long id, String remark)
    {
        SubsidyDistribution distribution = getById(id);
        if (distribution == null) {
            throw new ServiceException("发放记录不存在");
        }
        if (!DistributionStatusEnum.PENDING_REVIEW.getCode().equals(distribution.getDistributionStatus())) {
            throw new ServiceException("只有待审核状态的记录才能审核");
        }

        // 更新发放记录状态
        distribution.setDistributionStatus(DistributionStatusEnum.PENDING_DISTRIBUTION.getCode());
        distribution.setReviewRemark(remark);
        distribution.setUpdateTime(LocalDateTime.now());
        distribution.setUpdateBy(SecurityUtils.getUsername());
        int result = updateById(distribution) ? 1 : 0;

        // 更新补贴身份表的发放状态
        updateSubsidyRecordStatus(distribution.getSubsidyType(), distribution.getSubsidyRecordId(), DistributionStatusEnum.PENDING_DISTRIBUTION.getCode());

        // 记录审核记录
        distributionReviewService.addReviewRecord(id, "2", remark != null ? remark : "审核通过", SecurityUtils.getUsername());

        return result;
    }

    /**
     * 拒绝发放
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int rejectDistribution(Long id, String remark)
    {
        SubsidyDistribution distribution = getById(id);
        if (distribution == null) {
            throw new ServiceException("发放记录不存在");
        }
        if (!DistributionStatusEnum.PENDING_REVIEW.getCode().equals(distribution.getDistributionStatus())
            && !DistributionStatusEnum.PENDING_DISTRIBUTION.getCode().equals(distribution.getDistributionStatus())) {
            throw new ServiceException("只有待审核或待发放状态的记录才能拒绝");
        }

        // 更新发放记录状态
        distribution.setDistributionStatus(DistributionStatusEnum.REJECTED.getCode());
        distribution.setReviewRemark(remark);
        distribution.setUpdateTime(LocalDateTime.now());
        distribution.setUpdateBy(SecurityUtils.getUsername());
        int result = updateById(distribution) ? 1 : 0;

        // 更新补贴身份表的发放状态
        updateSubsidyRecordStatus(distribution.getSubsidyType(), distribution.getSubsidyRecordId(), DistributionStatusEnum.REJECTED.getCode());

        // 记录审核记录
        distributionReviewService.addReviewRecord(id, "3", remark, SecurityUtils.getUsername());

        return result;
    }

    /**
     * 发放补贴
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int distributeSubsidy(Long id, String remark)
    {
        SubsidyDistribution distribution = getById(id);
        if (distribution == null) {
            throw new ServiceException("发放记录不存在");
        }
        if (!DistributionStatusEnum.PENDING_DISTRIBUTION.getCode().equals(distribution.getDistributionStatus())) {
            throw new ServiceException("只有待发放状态的记录才能发放");
        }

        // 更新发放记录状态
        distribution.setDistributionStatus(DistributionStatusEnum.DISTRIBUTED.getCode());
        distribution.setDistributionDate(LocalDate.now());
        distribution.setUpdateTime(LocalDateTime.now());
        distribution.setUpdateBy(SecurityUtils.getUsername());
        int result = updateById(distribution) ? 1 : 0;

        // 更新补贴身份表的发放状态
        updateSubsidyRecordStatus(distribution.getSubsidyType(), distribution.getSubsidyRecordId(), DistributionStatusEnum.DISTRIBUTED.getCode());

        // 记录审核记录
        distributionReviewService.addReviewRecord(id, "4", remark != null ? remark : "发放补贴", SecurityUtils.getUsername());

        return result;
    }

    /**
     * 重新提交
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resubmitDistribution(Long id)
    {
        SubsidyDistribution distribution = getById(id);
        if (distribution == null) {
            throw new ServiceException("发放记录不存在");
        }
        if (!DistributionStatusEnum.REJECTED.getCode().equals(distribution.getDistributionStatus())) {
            throw new ServiceException("只有已驳回状态的记录才能重新提交");
        }

        // 从补贴身份表重新读取补贴金额
        BigDecimal latestAmount = getLatestSubsidyAmount(distribution.getSubsidyType(), distribution.getSubsidyRecordId());
        
        // 更新发放记录状态
        distribution.setDistributionStatus(DistributionStatusEnum.PENDING_REVIEW.getCode());
        distribution.setDistributionAmount(latestAmount);
        distribution.setUpdateTime(LocalDateTime.now());
        distribution.setUpdateBy(SecurityUtils.getUsername());
        int result = updateById(distribution) ? 1 : 0;

        // 更新补贴身份表的发放状态
        updateSubsidyRecordStatus(distribution.getSubsidyType(), distribution.getSubsidyRecordId(), DistributionStatusEnum.PENDING_REVIEW.getCode());

        // 记录审核记录
        distributionReviewService.addReviewRecord(id, "5", "重新提交审核", SecurityUtils.getUsername());

        return result;
    }

    /**
     * 删除补贴发放记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSubsidyDistributionByIds(Long[] ids)
    {
        for (Long id : ids) {
            SubsidyDistribution distribution = getById(id);
            if (distribution != null) {
                // 只有待审核状态的记录才能删除
                if (!DistributionStatusEnum.PENDING_REVIEW.getCode().equals(distribution.getDistributionStatus())) {
                    throw new ServiceException("已审核的记录不可以删除");
                }
                // 恢复补贴身份表的发放状态为未发放
                updateSubsidyRecordStatus(distribution.getSubsidyType(), distribution.getSubsidyRecordId(), DistributionStatusEnum.NOT_DISTRIBUTED.getCode());
            }
        }
        return baseMapper.deleteBatchIds(List.of(ids));
    }

    /**
     * 验证补贴记录是否可发放
     */
    private void validateSubsidyRecord(String subsidyType, Long subsidyRecordId)
    {
        String status = null;
        SubsidyTypeEnum typeEnum = SubsidyTypeEnum.getByCode(subsidyType);
        
        if (typeEnum == null) {
            throw new ServiceException("补贴类型不存在");
        }

        switch (typeEnum) {
            case LAND_LOSS:
                LandLossResident landLoss = landLossResidentMapper.selectById(subsidyRecordId);
                if (landLoss == null) {
                    throw new ServiceException("补贴记录不存在");
                }
                status = "0"; // 失地居民不再有发放状态字段，默认为0
                break;
            case EXPROPRIATEE:
                ExpropriateeSubsidy expropriatee = expropriateeSubsidyMapper.selectById(subsidyRecordId);
                if (expropriatee == null) {
                    throw new ServiceException("补贴记录不存在");
                }
                // 补贴发放状态已移到补贴发放管理表，这里不再检查
                status = "0";
                break;
            case DEMOLITION:
                DemolitionResident demolition = demolitionResidentMapper.selectById(subsidyRecordId);
                if (demolition == null) {
                    throw new ServiceException("补贴记录不存在");
                }
                // 补贴发放状态已移到补贴发放管理表，这里不再检查
                status = "0";
                break;
            case VILLAGE_OFFICIAL:
                VillageOfficial villageOfficial = villageOfficialMapper.selectById(subsidyRecordId);
                if (villageOfficial == null) {
                    throw new ServiceException("补贴记录不存在");
                }
                // 补贴发放状态已移到补贴发放管理表，这里不再检查
                status = "0";
                break;
        }

        if (!"0".equals(status) && status != null) {
            throw new ServiceException("该补贴记录已在发放流程中，不能重复发放");
        }
    }

    /**
     * 更新补贴身份表的发放状态
     */
    private void updateSubsidyRecordStatus(String subsidyType, Long subsidyRecordId, String distributionStatus)
    {
        SubsidyTypeEnum typeEnum = SubsidyTypeEnum.getByCode(subsidyType);
        if (typeEnum == null) {
            return;
        }

        switch (typeEnum) {
            case LAND_LOSS:
                LandLossResident landLoss = landLossResidentMapper.selectById(subsidyRecordId);
                if (landLoss != null) {
                    // 失地居民不再有发放状态字段，跳过更新
                }
                break;
            case EXPROPRIATEE:
                // 补贴发放状态已移到补贴发放管理表，不再更新实体表
                break;
            case DEMOLITION:
                // 补贴发放状态已移到补贴发放管理表，不再更新实体表
                break;
            case VILLAGE_OFFICIAL:
                // 补贴发放状态已移到补贴发放管理表，不再更新实体表
                break;
        }
    }

    /**
     * 获取最新的补贴金额
     */
    private BigDecimal getLatestSubsidyAmount(String subsidyType, Long subsidyRecordId)
    {
        SubsidyTypeEnum typeEnum = SubsidyTypeEnum.getByCode(subsidyType);
        if (typeEnum == null) {
            return BigDecimal.ZERO;
        }

        switch (typeEnum) {
            case LAND_LOSS:
                // 失地居民不再有补贴金额字段，返回0
                return BigDecimal.ZERO;
            case EXPROPRIATEE:
                ExpropriateeSubsidy expropriatee = expropriateeSubsidyMapper.selectById(subsidyRecordId);
                return expropriatee != null && expropriatee.getSubsidyAmount() != null ? expropriatee.getSubsidyAmount() : BigDecimal.ZERO;
            case DEMOLITION:
                // 补贴金额已移到补贴发放管理表，返回默认值
                return BigDecimal.ZERO;
            case VILLAGE_OFFICIAL:
                // 补贴金额已移到补贴发放管理表，返回默认值
                return BigDecimal.ZERO;
            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(String status)
    {
        DistributionStatusEnum statusEnum = DistributionStatusEnum.getByCode(status);
        return statusEnum != null ? statusEnum.getName() : "未知";
    }

    /**
     * 查询居民最近的发放记录
     */
    @Override
    public List<SubsidyDistributionListResp> selectRecentDistributions(Long subsidyPersonId, Integer limit)
    {
        SubsidyDistributionListReq req = new SubsidyDistributionListReq();
        req.setSubsidyPersonId(subsidyPersonId);
        req.setPageNum(1);
        req.setPageSize(limit != null ? limit : 5);
        Page<SubsidyDistributionListResp> resultPage = new Page<>(req.getPageNum(), req.getPageSize());
        Page<SubsidyDistributionListResp> result = subsidyDistributionMapper.selectSubsidyDistributionList(resultPage, req);
        return result.getRecords();
    }
}
