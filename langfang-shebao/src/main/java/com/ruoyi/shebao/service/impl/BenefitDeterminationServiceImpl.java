package com.ruoyi.shebao.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.domain.DemolitionResident;
import com.ruoyi.shebao.domain.ExpropriateeSubsidy;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.BenefitDeterminationListReq;
import com.ruoyi.shebao.dto.BenefitDeterminationListResp;
import com.ruoyi.shebao.dto.BenefitDeterminationPrintDto;
import com.ruoyi.shebao.mapper.BenefitDeterminationMapper;
import com.ruoyi.shebao.mapper.DemolitionResidentMapper;
import com.ruoyi.shebao.mapper.ExpropriateeSubsidyMapper;
import com.ruoyi.shebao.mapper.LandLossResidentMapper;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import com.ruoyi.shebao.enums.SubsidyTypeEnum;
import com.ruoyi.shebao.service.IBenefitDeterminationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.stream.Collectors;

/**
 * 待遇核定Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@Service
@RequiredArgsConstructor
public class BenefitDeterminationServiceImpl extends ServiceImpl<BenefitDeterminationMapper, BenefitDetermination> implements IBenefitDeterminationService
{
    private final SubsidyPersonMapper subsidyPersonMapper;
    private final LandLossResidentMapper landLossResidentMapper;
    private final DemolitionResidentMapper demolitionResidentMapper;
    private final ExpropriateeSubsidyMapper expropriateeSubsidyMapper;

    /**
     * 查询待遇核定列表
     *
     * @param req 查询条件
     * @return 待遇核定列表
     */
    @Override
    public Page<BenefitDeterminationListResp> selectBenefitDeterminationList(BenefitDeterminationListReq req)
    {
        // 使用默认分页参数
        Page<BenefitDetermination> page = new Page<>(req.getPageNum(), req.getPageSize());
        Page<BenefitDetermination> entityPage = this.page(page, this.lambdaQuery()
                .eq(StringUtils.isNotBlank(req.getSubsidyType()), BenefitDetermination::getSubsidyType, req.getSubsidyType())
                .eq(StringUtils.isNotBlank(req.getApprovalStatus()), BenefitDetermination::getApprovalStatus, req.getApprovalStatus())
                .ge(req.getEligibleMonthStart() != null, BenefitDetermination::getEligibleMonth, req.getEligibleMonthStart())
                .le(req.getEligibleMonthEnd() != null, BenefitDetermination::getEligibleMonth, req.getEligibleMonthEnd())
                .eq(BenefitDetermination::getDelFlag, "0")
                .orderByDesc(BenefitDetermination::getCreateTime)
                .getWrapper()
        );

        Page<BenefitDeterminationListResp> respPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());

        if (!CollectionUtils.isEmpty(entityPage.getRecords())) {
            // 获取人员信息
            List<Long> personIds = entityPage.getRecords().stream()
                    .map(BenefitDetermination::getSubsidyPersonId)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Long, SubsidyPerson> personMap = subsidyPersonMapper.selectBatchIds(personIds)
                    .stream()
                    .collect(Collectors.toMap(SubsidyPerson::getId, p -> p));

            respPage.setRecords(entityPage.getRecords().stream()
                    .map(entity -> {
                        BenefitDeterminationListResp resp = BeanUtil.copyProperties(entity, BenefitDeterminationListResp.class);
                        SubsidyPerson person = personMap.get(entity.getSubsidyPersonId());
                        if (person != null) {
                            resp.setName(person.getName());
                            resp.setIdCardNo(person.getIdCardNo());
                            resp.setUserCode(person.getUserCode());
                        }
                        return resp;
                    })
                    .collect(Collectors.toList())
            );
        }

        return respPage;
    }

    /**
     * 查询待遇核定详情
     *
     * @param id 待遇核定ID
     * @return 待遇核定详情
     */
    @Override
    public BenefitDetermination selectBenefitDeterminationById(Long id)
    {
        return this.getById(id);
    }

    /**
     * 新增待遇核定
     *
     * @param benefitDetermination 待遇核定
     * @return 结果
     */
    @Override
    @Transactional
    public int insertBenefitDetermination(BenefitDetermination benefitDetermination)
    {
        if (benefitDetermination.getSubsidyPersonId() == null && StringUtils.isNotBlank(benefitDetermination.getIdCardNo()))
        {
            SubsidyPerson person = subsidyPersonMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SubsidyPerson>()
                    .eq(SubsidyPerson::getIdCardNo, benefitDetermination.getIdCardNo())
                    .eq(SubsidyPerson::getDelFlag, "0")
                    .last("LIMIT 1")
            );
            if (person != null) {
                benefitDetermination.setSubsidyPersonId(person.getId());
            }
        }
        fillDerivedMonths(benefitDetermination);
        benefitDetermination.setApprovalStatus("draft");
        benefitDetermination.setDelFlag("0");
        benefitDetermination.setCreateTime(new Date());
        return this.save(benefitDetermination) ? 1 : 0;
    }

    /**
     * 修改待遇核定
     *
     * @param benefitDetermination 待遇核定
     * @return 结果
     */
    @Override
    @Transactional
    public int updateBenefitDetermination(BenefitDetermination benefitDetermination)
    {
        fillDerivedMonths(benefitDetermination);
        benefitDetermination.setUpdateTime(new Date());
        return this.updateById(benefitDetermination) ? 1 : 0;
    }

    /**
     * 批量删除待遇核定
     *
     * @param ids 待遇核定IDs
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteBenefitDeterminationByIds(Long[] ids)
    {
        return this.lambdaUpdate()
                .in(BenefitDetermination::getId, Arrays.asList(ids))
                .set(BenefitDetermination::getDelFlag, "2")
                .update() ? ids.length : 0;
    }

    /**
     * 提交审核
     *
     * @param id 待遇核定ID
     * @return 结果
     */
    @Override
    @Transactional
    public int submitForReview(Long id)
    {
        return this.lambdaUpdate()
                .eq(BenefitDetermination::getId, id)
                .set(BenefitDetermination::getApprovalStatus, "pending_review")
                .update() ? 1 : 0;
    }

    /**
     * 复核通过
     *
     * @param id 待遇核定ID
     * @param remark 复核意见
     * @return 结果
     */
    @Override
    @Transactional
    public int approveReview(Long id, String remark)
    {
        return this.lambdaUpdate()
                .eq(BenefitDetermination::getId, id)
                .set(BenefitDetermination::getApprovalStatus, "approved")
                .update() ? 1 : 0;
    }

    /**
     * 复核驳回
     *
     * @param id 待遇核定ID
     * @param reason 驳回原因
     * @return 结果
     */
    @Override
    @Transactional
    public int rejectReview(Long id, String reason)
    {
        return this.lambdaUpdate()
                .eq(BenefitDetermination::getId, id)
                .set(BenefitDetermination::getApprovalStatus, "rejected")
                .set(BenefitDetermination::getRemark, reason)
                .update() ? 1 : 0;
    }

    @Override
    public BenefitDeterminationPrintDto buildPrintDto(Long id)
    {
        BenefitDetermination determination = this.getById(id);
        Assert.notNull(determination, "待遇核定记录不存在");

        SubsidyPerson person = subsidyPersonMapper.selectById(determination.getSubsidyPersonId());
        Assert.notNull(person, "被补贴人不存在");

        BenefitDeterminationPrintDto dto = BeanUtil.copyProperties(determination, BenefitDeterminationPrintDto.class);
        dto.setName(person.getName());
        dto.setIdCardNo(person.getIdCardNo());
        dto.setUserCode(person.getUserCode());
        return dto;
    }

    /**
     * 自动生成到龄年月，并按规则填充默认享受开始年月（允许手工覆盖）
     */
    private void fillDerivedMonths(BenefitDetermination benefitDetermination)
    {
        if (benefitDetermination == null || benefitDetermination.getSubsidyPersonId() == null)
        {
            return;
        }

        SubsidyPerson person = subsidyPersonMapper.selectById(benefitDetermination.getSubsidyPersonId());
        Assert.notNull(person, "被补贴人不存在");
        LocalDate birthday = person.getBirthday();
        Assert.notNull(birthday, "人员生日为空，无法计算到龄年月");

        // 到龄年月：年满60周岁所在年月（取当月）
        YearMonth eligibleYm = YearMonth.from(birthday.plusYears(60));
        benefitDetermination.setEligibleYear(eligibleYm.getYear());
        benefitDetermination.setEligibleMonth(eligibleYm.getMonthValue());

        // 享受开始年月：默认=到龄次月；若到龄次月早于征地/拆迁时间，则取征地/拆迁时间；否则取到龄次月
        if (benefitDetermination.getBenefitStartMonth() == null)
        {
            YearMonth startYm = eligibleYm.plusMonths(1);
            YearMonth eventYm = findEventMonth(resolveSubsidyType(benefitDetermination.getSubsidyType()), benefitDetermination.getSubsidyPersonId());
            if (eventYm != null && eventYm.isAfter(startYm))
            {
                startYm = eventYm;
            }
            benefitDetermination.setBenefitStartYear(startYm.getYear());
            benefitDetermination.setBenefitStartMonth(startYm.getMonthValue());
        }
    }

    private SubsidyTypeEnum resolveSubsidyType(String subsidyType)
    {
        if (StringUtils.isBlank(subsidyType))
        {
            return null;
        }
        SubsidyTypeEnum byCode = SubsidyTypeEnum.getByCode(subsidyType);
        if (byCode != null)
        {
            return byCode;
        }
        // 兼容前端/历史字符串
        return switch (subsidyType)
        {
            case "land_loss_resident" -> SubsidyTypeEnum.LAND_LOSS;
            case "expropriatee" -> SubsidyTypeEnum.EXPROPRIATEE;
            case "demolition_resident" -> SubsidyTypeEnum.DEMOLITION;
            case "village_official" -> SubsidyTypeEnum.VILLAGE_OFFICIAL;
            default -> null;
        };
    }

    private YearMonth findEventMonth(SubsidyTypeEnum type, Long subsidyPersonId)
    {
        if (type == null || subsidyPersonId == null)
        {
            return null;
        }

        return switch (type)
        {
            case LAND_LOSS ->
            {
                List<LandLossResident> list = landLossResidentMapper.selectBySubsidyPersonId(subsidyPersonId);
                LocalDate min = list == null ? null : list.stream()
                        .map(LandLossResident::getLandRequisitionTime)
                        .filter(d -> d != null)
                        .min(LocalDate::compareTo)
                        .orElse(null);
                yield min == null ? null : YearMonth.from(min);
            }
            case DEMOLITION ->
            {
                List<DemolitionResident> list = demolitionResidentMapper.selectBySubsidyPersonId(subsidyPersonId);
                LocalDate min = list == null ? null : list.stream()
                        .map(DemolitionResident::getDemolitionTime)
                        .filter(d -> d != null)
                        .min(LocalDate::compareTo)
                        .orElse(null);
                yield min == null ? null : YearMonth.from(min);
            }
            case EXPROPRIATEE ->
            {
                List<ExpropriateeSubsidy> list = expropriateeSubsidyMapper.selectBySubsidyPersonId(subsidyPersonId);
                LocalDate min = list == null ? null : list.stream()
                        .map(ExpropriateeSubsidy::getBaseDate)
                        .filter(d -> d != null)
                        .min(LocalDate::compareTo)
                        .orElse(null);
                yield min == null ? null : YearMonth.from(min);
            }
            default -> null;
        };
    }
}
