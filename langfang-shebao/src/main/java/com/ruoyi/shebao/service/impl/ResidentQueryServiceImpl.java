package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.shebao.domain.*;
import com.ruoyi.shebao.dto.*;
import com.ruoyi.shebao.mapper.*;
import com.ruoyi.shebao.service.ResidentQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 居民查询Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Service
public class ResidentQueryServiceImpl implements ResidentQueryService
{
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
    private SubsidyDistributionMapper subsidyDistributionMapper;

    @Autowired
    private StreetOfficeMapper streetOfficeMapper;

    @Autowired
    private VillageCommitteeMapper villageCommitteeMapper;

    /**
     * 搜索居民
     * 
     * 说明：用于综合查询的搜索,默认排除注销人员
     */
    @Override
    public List<ResidentSearchResp> searchResidents(String keyword)
    {
        LambdaQueryWrapper<SubsidyPerson> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(SubsidyPerson::getName, keyword).or().like(SubsidyPerson::getIdCardNo, keyword));
        wrapper.eq(SubsidyPerson::getDelFlag, "0");
        wrapper.eq(SubsidyPerson::getIsAlive, "1"); // 默认排除注销人员
        wrapper.orderByAsc(SubsidyPerson::getName);
        wrapper.last("LIMIT 10");

        List<SubsidyPerson> persons = subsidyPersonMapper.selectList(wrapper);
        
        List<ResidentSearchResp> result = new ArrayList<>();
        for (SubsidyPerson person : persons) {
            ResidentSearchResp resp = new ResidentSearchResp();
            resp.setSubsidyPersonId(person.getId());
            resp.setName(person.getName());
            resp.setIdCardNo(person.getIdCardNo());
            result.add(resp);
        }
        
        return result;
    }

    /**
     * 获取居民详细信息
     * 
     * 说明：综合查询接口,支持查询所有人员(包括注销人员),用于历史追溯和综合查询
     */
    @Override
    public ResidentDetailInfoDto getResidentDetailInfo(String keyword, Long subsidyPersonId)
    {
        // 参数验证：至少提供一个查询参数
        if (subsidyPersonId == null && (keyword == null || keyword.trim().isEmpty())) {
            throw new RuntimeException("请提供居民ID或关键词进行查询");
        }
        
        SubsidyPerson person;
        
        if (subsidyPersonId != null) {
            person = subsidyPersonMapper.selectById(subsidyPersonId);
        } else {
            LambdaQueryWrapper<SubsidyPerson> wrapper = new LambdaQueryWrapper<>();
            wrapper.and(w -> w.eq(SubsidyPerson::getName, keyword).or().eq(SubsidyPerson::getIdCardNo, keyword));
            wrapper.eq(SubsidyPerson::getDelFlag, "0");
            // 注意：此处不过滤is_alive,支持查询注销人员用于历史追溯
            person = subsidyPersonMapper.selectOne(wrapper);
        }
        
        if (person == null) {
            throw new RuntimeException("未找到该居民信息");
        }

        ResidentDetailInfoDto detailInfo = new ResidentDetailInfoDto();
        
        // 设置居民基本信息
        ResidentBasicInfo basicInfo = new ResidentBasicInfo();
        basicInfo.setId(person.getId());
        basicInfo.setName(person.getName());
        basicInfo.setIdCardNo(person.getIdCardNo());
        basicInfo.setGender(person.getGender());
        basicInfo.setBirthday(person.getBirthday());
        basicInfo.setHouseholdRegistration(person.getHouseholdRegistration());
        basicInfo.setHomeAddress(person.getHomeAddress());
        basicInfo.setPhone(person.getPhone());
        basicInfo.setIsAlive(person.getIsAlive());
        basicInfo.setDeathDate(person.getDeathDate());
        basicInfo.setIsVillageCoopMember(person.getIsVillageCoopMember());
        basicInfo.setUserCode(person.getUserCode());
        basicInfo.setStreetOfficeId(person.getStreetOfficeId());
        basicInfo.setVillageCommitteeId(person.getVillageCommitteeId());
        
        // 查询街道办和村委会名称
        if (person.getStreetOfficeId() != null) {
            StreetOffice streetOffice = streetOfficeMapper.selectById(person.getStreetOfficeId());
            if (streetOffice != null) {
                basicInfo.setStreetOfficeName(streetOffice.getStreetName());
            }
        }
        if (person.getVillageCommitteeId() != null) {
            VillageCommittee villageCommittee = villageCommitteeMapper.selectById(person.getVillageCommitteeId());
            if (villageCommittee != null) {
                basicInfo.setVillageCommitteeName(villageCommittee.getVillageName());
            }
        }
        
        detailInfo.setResidentInfo(basicInfo);
        
        // 设置补贴信息
        SubsidyInfoDto subsidyInfo = new SubsidyInfoDto();
        
        // 查询失地居民补贴
        LambdaQueryWrapper<LandLossResident> landLossWrapper = new LambdaQueryWrapper<>();
        landLossWrapper.eq(LandLossResident::getSubsidyPersonId, person.getId());
        landLossWrapper.eq(LandLossResident::getDelFlag, "0");
        List<LandLossResident> landLossResidents = landLossResidentMapper.selectList(landLossWrapper);
        List<LandLossResidentDto> landLossDtos = new ArrayList<>();
        for (LandLossResident resident : landLossResidents) {
            LandLossResidentDto dto = new LandLossResidentDto();
            dto.setLandRequisitionBatch(resident.getLandRequisitionBatch());
            dto.setVillageStreet(resident.getVillageStreet());
            dto.setRecognitionTime(resident.getRecognitionTime());
            dto.setLandRequisitionTime(resident.getLandRequisitionTime());
            dto.setCompensationCompleteTime(resident.getCompensationCompleteTime());
            dto.setRemark(resident.getRemark());
            dto.setCreateTime(resident.getCreateTime());
            landLossDtos.add(dto);
        }
        subsidyInfo.setLandLossResidents(landLossDtos);
        
        // 查询被征地居民补贴
        LambdaQueryWrapper<ExpropriateeSubsidy> expropriateeWrapper = new LambdaQueryWrapper<>();
        expropriateeWrapper.eq(ExpropriateeSubsidy::getSubsidyPersonId, person.getId());
        expropriateeWrapper.eq(ExpropriateeSubsidy::getDelFlag, "0");
        List<ExpropriateeSubsidy> expropriateeSubsidies = expropriateeSubsidyMapper.selectList(expropriateeWrapper);
        List<ExpropriateeSubsidyDto> expropriateeDtos = new ArrayList<>();
        for (ExpropriateeSubsidy subsidy : expropriateeSubsidies) {
            ExpropriateeSubsidyDto dto = new ExpropriateeSubsidyDto();
            dto.setLandRequisitionBatch(subsidy.getLandRequisitionBatch());
            dto.setVillageStreet(subsidy.getVillageStreet());
            dto.setBaseDate(subsidy.getBaseDate());
            dto.setEmployeePensionMonths(subsidy.getEmployeePensionMonths());
            dto.setFlexibleEmploymentMonths(subsidy.getFlexibleEmploymentMonths());
            dto.setDifficultySubsidyMonths(subsidy.getDifficultySubsidyMonths() != null ? subsidy.getDifficultySubsidyMonths().intValue() : null);
            dto.setAgeAtBaseDate(subsidy.getAgeAtBaseDate());
            dto.setSubsidyYears(subsidy.getSubsidyYears() != null ? subsidy.getSubsidyYears().intValue() : null);
            dto.setSubsidyAmount(subsidy.getSubsidyAmount());
            dto.setJoinUrbanRuralInsurance(subsidy.getJoinUrbanRuralInsurance());
            dto.setJoinEmployeePension(subsidy.getJoinEmployeePension());
            dto.setHasEmployeePension(subsidy.getHasEmployeePension());
            dto.setRemark(subsidy.getRemark());
            dto.setCreateTime(subsidy.getCreateTime());
            expropriateeDtos.add(dto);
        }
        subsidyInfo.setExpropriateeSubsidies(expropriateeDtos);
        
        // 查询拆迁居民补贴
        LambdaQueryWrapper<DemolitionResident> demolitionWrapper = new LambdaQueryWrapper<>();
        demolitionWrapper.eq(DemolitionResident::getSubsidyPersonId, person.getId());
        demolitionWrapper.eq(DemolitionResident::getDelFlag, "0");
        List<DemolitionResident> demolitionResidents = demolitionResidentMapper.selectList(demolitionWrapper);
        List<DemolitionResidentDto> demolitionDtos = new ArrayList<>();
        for (DemolitionResident resident : demolitionResidents) {
            DemolitionResidentDto dto = new DemolitionResidentDto();
            dto.setVillageStreet(resident.getVillageStreet());
            dto.setRecognitionTime(resident.getRecognitionTime());
            dto.setDemolitionTime(resident.getDemolitionTime());
            dto.setDemolitionReason(resident.getDemolitionReason());
            dto.setRemark(resident.getRemark());
            dto.setCreateTime(resident.getCreateTime());
            demolitionDtos.add(dto);
        }
        subsidyInfo.setDemolitionResidents(demolitionDtos);
        
        // 查询村干部补贴
        LambdaQueryWrapper<VillageOfficial> villageOfficialWrapper = new LambdaQueryWrapper<>();
        villageOfficialWrapper.eq(VillageOfficial::getSubsidyPersonId, person.getId());
        villageOfficialWrapper.eq(VillageOfficial::getDelFlag, "0");
        List<VillageOfficial> villageOfficials = villageOfficialMapper.selectList(villageOfficialWrapper);
        List<VillageOfficialDto> villageOfficialDtos = new ArrayList<>();
        for (VillageOfficial official : villageOfficials) {
            VillageOfficialDto dto = new VillageOfficialDto();
            dto.setTotalServiceYears(official.getTotalServiceYears());
            dto.setSubsidyAmount(official.getSubsidyAmount());
            dto.setHasViolation(official.getHasViolation());
            dto.setVillageStreet(official.getVillageStreet());
            dto.setRemark(official.getRemark());
            dto.setCreateTime(official.getCreateTime());
            villageOfficialDtos.add(dto);
        }
        subsidyInfo.setVillageOfficials(villageOfficialDtos);
        
        detailInfo.setSubsidyInfo(subsidyInfo);
        
        return detailInfo;
    }

    /**
     * 获取居民发放记录
     */
    @Override
    public AjaxResult getResidentDistributionList(Long subsidyPersonId, Integer pageNum, Integer pageSize)
    {
        SubsidyDistributionListReq req = new SubsidyDistributionListReq();
        req.setSubsidyPersonId(subsidyPersonId);
        req.setPageNum(pageNum);
        req.setPageSize(pageSize);
        
        Page<SubsidyDistributionListResp> page = new Page<>(pageNum, pageSize);
        Page<SubsidyDistributionListResp> result = subsidyDistributionMapper.selectSubsidyDistributionList(page, req);
        
        return AjaxResult.success(result);
    }
}
