package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.domain.BenefitDeterminationItem;
import com.ruoyi.shebao.domain.BenefitResume;
import com.ruoyi.shebao.domain.BenefitResumeItem;
import com.ruoyi.shebao.domain.StreetOffice;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.dto.BenefitResumeCandidateResp;
import com.ruoyi.shebao.dto.BenefitResumeCreateReq;
import com.ruoyi.shebao.dto.BenefitResumeDetailResp;
import com.ruoyi.shebao.dto.BenefitResumeListReq;
import com.ruoyi.shebao.dto.BenefitResumeListResp;
import com.ruoyi.shebao.mapper.BenefitDeterminationItemMapper;
import com.ruoyi.shebao.mapper.BenefitDeterminationMapper;
import com.ruoyi.shebao.mapper.BenefitResumeItemMapper;
import com.ruoyi.shebao.mapper.BenefitResumeMapper;
import com.ruoyi.shebao.mapper.StreetOfficeMapper;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import com.ruoyi.shebao.mapper.VillageCommitteeMapper;
import com.ruoyi.shebao.service.BenefitResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BenefitResumeServiceImpl implements BenefitResumeService
{
    private final BenefitResumeMapper benefitResumeMapper;
    private final BenefitResumeItemMapper benefitResumeItemMapper;
    private final BenefitDeterminationMapper benefitDeterminationMapper;
    private final BenefitDeterminationItemMapper benefitDeterminationItemMapper;
    private final SubsidyPersonMapper subsidyPersonMapper;
    private final StreetOfficeMapper streetOfficeMapper;
    private final VillageCommitteeMapper villageCommitteeMapper;

    @Override
    public Page<BenefitResumeListResp> list(BenefitResumeListReq req)
    {
        long pageNum = req == null ? 1L : req.pageNumOrDefault();
        long pageSize = req == null ? 10L : req.pageSizeOrDefault();
        Page<BenefitResume> page = new Page<>(pageNum, pageSize);

        Set<Long> personIds = null;
        if (req != null && (StringUtils.isNotBlank(req.getName()) || StringUtils.isNotBlank(req.getIdCardNo())))
        {
            List<SubsidyPerson> persons = subsidyPersonMapper.selectList(new LambdaQueryWrapper<SubsidyPerson>()
                    .eq(SubsidyPerson::getDelFlag, "0")
                    .like(StringUtils.isNotBlank(req.getName()), SubsidyPerson::getName, req.getName())
                    .like(StringUtils.isNotBlank(req.getIdCardNo()), SubsidyPerson::getIdCardNo, req.getIdCardNo()));
            personIds = persons.stream().map(SubsidyPerson::getId).collect(Collectors.toSet());
            if (personIds.isEmpty())
            {
                return new Page<>(pageNum, pageSize);
            }
        }

        LambdaQueryWrapper<BenefitResume> wrapper = new LambdaQueryWrapper<BenefitResume>()
                .eq(BenefitResume::getDelFlag, "0")
                .orderByDesc(BenefitResume::getCreateTime);
        if (personIds != null)
        {
            wrapper.in(BenefitResume::getSubsidyPersonId, personIds);
        }
        Page<BenefitResume> resultPage = benefitResumeMapper.selectPage(page, wrapper);
        if (resultPage.getRecords().isEmpty())
        {
            Page<BenefitResumeListResp> empty = new Page<>(pageNum, pageSize);
            empty.setTotal(resultPage.getTotal());
            return empty;
        }

        List<BenefitResume> records = resultPage.getRecords();
        Set<Long> resumeIds = records.stream().map(BenefitResume::getId).collect(Collectors.toSet());
        Set<Long> personIdSet = records.stream().map(BenefitResume::getSubsidyPersonId).collect(Collectors.toSet());
        Set<Long> determinationIds = records.stream().map(BenefitResume::getDeterminationId).collect(Collectors.toSet());

        Map<Long, SubsidyPerson> personMap = subsidyPersonMapper.selectBatchIds(personIdSet).stream()
                .collect(Collectors.toMap(SubsidyPerson::getId, p -> p));
        Map<Long, List<BenefitResumeItem>> resumeItemMap = benefitResumeItemMapper.selectList(
                        new LambdaQueryWrapper<BenefitResumeItem>()
                                .in(BenefitResumeItem::getResumeId, resumeIds)
                                .eq(BenefitResumeItem::getDelFlag, "0"))
                .stream()
                .collect(Collectors.groupingBy(BenefitResumeItem::getResumeId));
        Map<Long, List<BenefitDeterminationItem>> determinationItemMap = benefitDeterminationItemMapper.selectList(
                        new LambdaQueryWrapper<BenefitDeterminationItem>()
                                .in(BenefitDeterminationItem::getDeterminationId, determinationIds)
                                .eq(BenefitDeterminationItem::getDelFlag, "0"))
                .stream()
                .collect(Collectors.groupingBy(BenefitDeterminationItem::getDeterminationId));

        List<BenefitResumeListResp> rows = records.stream().map(record -> {
            BenefitResumeListResp resp = new BenefitResumeListResp();
            resp.setId(record.getId());
            SubsidyPerson person = personMap.get(record.getSubsidyPersonId());
            resp.setName(person == null ? null : person.getName());
            resp.setIdCardNo(record.getIdCardNo());
            resp.setResumeMonth(record.getResumeMonth());
            resp.setResumeReason(record.getResumeReason());
            resp.setSubsidyTypes(determinationItemMap.getOrDefault(record.getDeterminationId(), List.of()).stream()
                    .map(BenefitDeterminationItem::getSubsidyType)
                    .distinct()
                    .map(this::subsidyTypeLabel)
                    .collect(Collectors.joining("、")));
            resp.setResumedSubsidyTypes(resumeItemMap.getOrDefault(record.getId(), List.of()).stream()
                    .filter(item -> "1".equals(item.getNeedResume()))
                    .map(BenefitResumeItem::getSubsidyType)
                    .distinct()
                    .map(this::subsidyTypeLabel)
                    .collect(Collectors.joining("、")));
            return resp;
        }).toList();

        Page<BenefitResumeListResp> responsePage = new Page<>(pageNum, pageSize);
        responsePage.setTotal(resultPage.getTotal());
        responsePage.setRecords(rows);
        return responsePage;
    }

    @Override
    public BenefitResumeCandidateResp findCandidateByIdCardNo(String idCardNo)
    {
        if (StringUtils.isBlank(idCardNo))
        {
            throw new ServiceException("身份证号不能为空");
        }
        SubsidyPerson person = subsidyPersonMapper.selectOne(new LambdaQueryWrapper<SubsidyPerson>()
                .eq(SubsidyPerson::getIdCardNo, idCardNo)
                .eq(SubsidyPerson::getDelFlag, "0")
                .last("limit 1"));
        if (person == null)
        {
            throw new ServiceException("未查询到待遇人员");
        }
        BenefitDetermination determination = benefitDeterminationMapper.selectOne(new LambdaQueryWrapper<BenefitDetermination>()
                .eq(BenefitDetermination::getSubsidyPersonId, person.getId())
                .eq(BenefitDetermination::getApprovalStatus, "approved")
                .eq(BenefitDetermination::getDelFlag, "0")
                .orderByDesc(BenefitDetermination::getId)
                .last("limit 1"));
        if (determination == null)
        {
            throw new ServiceException("未查询到待遇人员");
        }

        List<BenefitDeterminationItem> pausedItems = benefitDeterminationItemMapper.selectList(
                new LambdaQueryWrapper<BenefitDeterminationItem>()
                        .eq(BenefitDeterminationItem::getDeterminationId, determination.getId())
                        .eq(BenefitDeterminationItem::getDelFlag, "0")
                        .eq(BenefitDeterminationItem::getBenefitStatus, "1")
                        .orderByAsc(BenefitDeterminationItem::getId));
        if (pausedItems.isEmpty())
        {
            throw new ServiceException("该人员没有暂停的补贴");
        }

        BenefitResumeCandidateResp resp = new BenefitResumeCandidateResp();
        resp.setDeterminationId(determination.getId());
        resp.setSubsidyPersonId(person.getId());
        resp.setName(person.getName());
        resp.setIdCardNo(person.getIdCardNo());
        if (person.getStreetOfficeId() != null)
        {
            StreetOffice so = streetOfficeMapper.selectById(person.getStreetOfficeId());
            resp.setStreetOfficeName(so == null ? null : so.getStreetName());
        }
        if (person.getVillageCommitteeId() != null)
        {
            VillageCommittee vc = villageCommitteeMapper.selectById(person.getVillageCommitteeId());
            resp.setVillageCommitteeName(vc == null ? null : vc.getVillageName());
        }
        resp.setResumeItems(pausedItems.stream().map(this::toCandidateItem).toList());
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(BenefitResumeCreateReq req)
    {
        BenefitDetermination determination = benefitDeterminationMapper.selectById(req.getDeterminationId());
        if (determination == null || !"0".equals(determination.getDelFlag()) || !"approved".equals(determination.getApprovalStatus()))
        {
            throw new ServiceException("未查询到待遇人员");
        }
        if (!req.getSubsidyPersonId().equals(determination.getSubsidyPersonId()))
        {
            throw new ServiceException("人员信息不匹配");
        }
        SubsidyPerson person = subsidyPersonMapper.selectById(req.getSubsidyPersonId());
        if (person == null || !"0".equals(person.getDelFlag()) || !StringUtils.equals(person.getIdCardNo(), req.getIdCardNo()))
        {
            throw new ServiceException("人员信息不匹配");
        }

        List<Long> itemIds = req.getItems().stream().map(BenefitResumeCreateReq.Item::getDeterminationItemId).toList();
        List<BenefitDeterminationItem> determinationItems = benefitDeterminationItemMapper.selectList(
                new LambdaQueryWrapper<BenefitDeterminationItem>()
                        .in(BenefitDeterminationItem::getId, itemIds)
                        .eq(BenefitDeterminationItem::getDeterminationId, req.getDeterminationId())
                        .eq(BenefitDeterminationItem::getDelFlag, "0"));
        if (determinationItems.size() != itemIds.size())
        {
            throw new ServiceException("恢复补贴记录不存在或不属于当前待遇核定");
        }
        boolean hasNotPaused = determinationItems.stream().anyMatch(item -> !"1".equals(StringUtils.defaultIfBlank(item.getBenefitStatus(), "0")));
        if (hasNotPaused)
        {
            throw new ServiceException("存在非暂停状态补贴，无法恢复");
        }

        Map<Long, BenefitResumeCreateReq.Item> reqItemMap = req.getItems().stream()
                .collect(Collectors.toMap(BenefitResumeCreateReq.Item::getDeterminationItemId, item -> item, (a, b) -> b));
        boolean hasNeedResume = req.getItems().stream().anyMatch(item -> "1".equals(item.getNeedResume()));
        if (!hasNeedResume)
        {
            throw new ServiceException("请至少选择一条需要恢复的补贴记录");
        }

        Date mainResumeMonth = yearMonthToDate(parseYearMonth(req.getItems().get(0).getResumeMonth()));
        String mergedResumeReason = req.getItems().stream()
                .filter(item -> "1".equals(item.getNeedResume()))
                .map(BenefitResumeCreateReq.Item::getResumeReason)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.joining("；"));

        BenefitResume resume = new BenefitResume();
        resume.setDeterminationId(req.getDeterminationId());
        resume.setSubsidyPersonId(req.getSubsidyPersonId());
        resume.setIdCardNo(req.getIdCardNo());
        resume.setResumeMonth(mainResumeMonth);
        resume.setResumeReason(StringUtils.defaultIfBlank(mergedResumeReason, "待遇恢复"));
        resume.setStatus("0");
        resume.setCreateBy(SecurityUtils.getUsername());
        resume.setCreateTime(LocalDateTime.now());
        resume.setUpdateBy(SecurityUtils.getUsername());
        resume.setUpdateTime(LocalDateTime.now());
        benefitResumeMapper.insert(resume);

        YearMonth currentYm = YearMonth.now();
        for (BenefitDeterminationItem item : determinationItems)
        {
            BenefitResumeCreateReq.Item reqItem = reqItemMap.get(item.getId());
            YearMonth resumeYm = parseYearMonth(reqItem.getResumeMonth());
            BenefitResumeItem resumeItem = buildResumeItem(resume.getId(), item, reqItem, resumeYm, currentYm);
            benefitResumeItemMapper.insert(resumeItem);

            if ("1".equals(reqItem.getNeedResume()))
            {
                BenefitDeterminationItem update = new BenefitDeterminationItem();
                update.setId(item.getId());
                update.setBenefitStatus("0");
                update.setPauseStartMonth(null);
                update.setUpdateBy(SecurityUtils.getUsername());
                update.setUpdateTime(LocalDateTime.now());
                benefitDeterminationItemMapper.updateById(update);
            }
        }
        return resume.getId();
    }

    @Override
    public BenefitResumeDetailResp getDetail(Long id)
    {
        BenefitResume resume = benefitResumeMapper.selectById(id);
        if (resume == null || !"0".equals(resume.getDelFlag()))
        {
            throw new ServiceException("恢复记录不存在");
        }
        SubsidyPerson person = subsidyPersonMapper.selectById(resume.getSubsidyPersonId());
        if (person == null || !"0".equals(person.getDelFlag()))
        {
            throw new ServiceException("恢复记录关联人员不存在");
        }

        List<BenefitResumeItem> items = benefitResumeItemMapper.selectList(
                new LambdaQueryWrapper<BenefitResumeItem>()
                        .eq(BenefitResumeItem::getResumeId, id)
                        .eq(BenefitResumeItem::getDelFlag, "0")
                        .orderByAsc(BenefitResumeItem::getId));

        BenefitResumeDetailResp detail = new BenefitResumeDetailResp();
        detail.setId(resume.getId());
        detail.setDeterminationId(resume.getDeterminationId());
        detail.setSubsidyPersonId(resume.getSubsidyPersonId());
        detail.setName(person.getName());
        detail.setIdCardNo(resume.getIdCardNo());
        detail.setResumeMonth(resume.getResumeMonth());
        detail.setResumeReason(resume.getResumeReason());
        detail.setRemark(resume.getRemark());
        detail.setResumeItems(items.stream().map(this::toDetailItem).toList());
        if (person.getStreetOfficeId() != null)
        {
            StreetOffice so = streetOfficeMapper.selectById(person.getStreetOfficeId());
            detail.setStreetOfficeName(so == null ? null : so.getStreetName());
        }
        if (person.getVillageCommitteeId() != null)
        {
            VillageCommittee vc = villageCommitteeMapper.selectById(person.getVillageCommitteeId());
            detail.setVillageCommitteeName(vc == null ? null : vc.getVillageName());
        }
        return detail;
    }

    private BenefitResumeCandidateResp.ResumeItem toCandidateItem(BenefitDeterminationItem item)
    {
        BenefitResumeCandidateResp.ResumeItem result = new BenefitResumeCandidateResp.ResumeItem();
        result.setDeterminationItemId(item.getId());
        result.setSubsidyType(item.getSubsidyType());
        result.setPauseStartMonth(item.getPauseStartMonth());
        result.setNeedResume("1");
        YearMonth currentYm = YearMonth.now();
        YearMonth resumeYm = currentYm;
        result.setResumeMonth(yearMonthToDate(resumeYm));
        result.setSupplementStartMonth(yearMonthToDate(resumeYm));
        YearMonth endYm = currentYm.minusMonths(1);
        if (resumeYm.isAfter(endYm))
        {
            result.setSupplementEndMonth(null);
            result.setSupplementMonths(0);
            result.setSupplementAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }
        else
        {
            int months = (int) ChronoUnit.MONTHS.between(resumeYm, endYm) + 1;
            BigDecimal standard = item.getSubsidyStandard() == null ? BigDecimal.ZERO : item.getSubsidyStandard();
            result.setSupplementEndMonth(yearMonthToDate(endYm));
            result.setSupplementMonths(months);
            result.setSupplementAmount(standard.multiply(BigDecimal.valueOf(months)).setScale(2, RoundingMode.HALF_UP));
        }
        result.setSubsidyStandard(item.getSubsidyStandard() == null ? BigDecimal.ZERO : item.getSubsidyStandard());
        return result;
    }

    private BenefitResumeDetailResp.ResumeItem toDetailItem(BenefitResumeItem item)
    {
        BenefitResumeDetailResp.ResumeItem result = new BenefitResumeDetailResp.ResumeItem();
        result.setDeterminationItemId(item.getDeterminationItemId());
        result.setSubsidyType(item.getSubsidyType());
        result.setPauseStartMonth(item.getPauseStartMonth());
        result.setNeedResume(item.getNeedResume());
        result.setResumeReason(item.getResumeReason());
        result.setResumeMonth(item.getResumeMonth());
        result.setSupplementStartMonth(item.getSupplementStartMonth());
        result.setSupplementEndMonth(item.getSupplementEndMonth());
        result.setSupplementMonths(item.getSupplementMonths());
        result.setSupplementAmount(item.getSupplementAmount());
        result.setSubsidyStandard(item.getSubsidyStandard());
        result.setRemark(item.getRemark());
        return result;
    }

    private BenefitResumeItem buildResumeItem(Long resumeId,
                                              BenefitDeterminationItem determinationItem,
                                              BenefitResumeCreateReq.Item reqItem,
                                              YearMonth resumeYm,
                                              YearMonth currentYm)
    {
        YearMonth endYm = currentYm.minusMonths(1);
        int supplementMonths = 0;
        Date supplementEndMonth = null;
        if (!resumeYm.isAfter(endYm))
        {
            supplementMonths = (int) ChronoUnit.MONTHS.between(resumeYm, endYm) + 1;
            supplementEndMonth = yearMonthToDate(endYm);
        }
        BigDecimal standard = determinationItem.getSubsidyStandard() == null ? BigDecimal.ZERO : determinationItem.getSubsidyStandard();
        BigDecimal supplementAmount = standard.multiply(BigDecimal.valueOf(supplementMonths)).setScale(2, RoundingMode.HALF_UP);

        BenefitResumeItem resumeItem = new BenefitResumeItem();
        resumeItem.setResumeId(resumeId);
        resumeItem.setDeterminationItemId(determinationItem.getId());
        resumeItem.setSubsidyType(determinationItem.getSubsidyType());
        resumeItem.setPauseStartMonth(determinationItem.getPauseStartMonth());
        resumeItem.setNeedResume(reqItem.getNeedResume());
        resumeItem.setResumeReason(reqItem.getResumeReason());
        resumeItem.setResumeMonth(yearMonthToDate(resumeYm));
        resumeItem.setSupplementStartMonth(yearMonthToDate(resumeYm));
        resumeItem.setSupplementEndMonth(supplementEndMonth);
        resumeItem.setSupplementMonths(supplementMonths);
        resumeItem.setSupplementAmount(supplementAmount);
        resumeItem.setSubsidyStandard(standard);
        resumeItem.setRemark(reqItem.getRemark());
        resumeItem.setStatus("0");
        resumeItem.setCreateBy(SecurityUtils.getUsername());
        resumeItem.setCreateTime(LocalDateTime.now());
        resumeItem.setUpdateBy(SecurityUtils.getUsername());
        resumeItem.setUpdateTime(LocalDateTime.now());
        return resumeItem;
    }

    private YearMonth parseYearMonth(String month)
    {
        if (StringUtils.isBlank(month) || !month.matches("\\d{4}-\\d{2}"))
        {
            throw new ServiceException("年月格式错误，应为yyyy-MM");
        }
        String[] parts = month.split("-");
        return YearMonth.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    private Date yearMonthToDate(YearMonth ym)
    {
        if (ym == null)
        {
            return null;
        }
        return java.sql.Date.valueOf(ym.atDay(1));
    }

    private String subsidyTypeLabel(String subsidyType)
    {
        if (StringUtils.isBlank(subsidyType))
        {
            return subsidyType;
        }
        return switch (subsidyType)
        {
            case "land_loss", "land_loss_resident", "1" -> "失地居民";
            case "expropriatee", "expropriatee_subsidy", "2" -> "被征地居民";
            case "demolition", "demolition_resident", "3" -> "拆迁居民";
            case "village_official", "4" -> "村干部";
            case "teacher", "teacher_subsidy", "5" -> "教师";
            default -> subsidyType;
        };
    }
}
