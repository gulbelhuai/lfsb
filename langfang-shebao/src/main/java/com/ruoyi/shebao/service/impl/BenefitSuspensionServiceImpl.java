package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.domain.BenefitDeterminationItem;
import com.ruoyi.shebao.domain.BenefitSuspension;
import com.ruoyi.shebao.domain.BenefitSuspensionItem;
import com.ruoyi.shebao.domain.StreetOffice;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.dto.BenefitSuspensionCandidateResp;
import com.ruoyi.shebao.dto.BenefitSuspensionCreateReq;
import com.ruoyi.shebao.dto.BenefitSuspensionDetailResp;
import com.ruoyi.shebao.dto.BenefitSuspensionListReq;
import com.ruoyi.shebao.dto.BenefitSuspensionListResp;
import com.ruoyi.shebao.mapper.BenefitDeterminationItemMapper;
import com.ruoyi.shebao.mapper.BenefitDeterminationMapper;
import com.ruoyi.shebao.mapper.BenefitSuspensionItemMapper;
import com.ruoyi.shebao.mapper.BenefitSuspensionMapper;
import com.ruoyi.shebao.mapper.StreetOfficeMapper;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import com.ruoyi.shebao.mapper.VillageCommitteeMapper;
import com.ruoyi.shebao.service.BenefitSuspensionService;
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
public class BenefitSuspensionServiceImpl implements BenefitSuspensionService
{
    private final BenefitSuspensionMapper benefitSuspensionMapper;
    private final BenefitSuspensionItemMapper benefitSuspensionItemMapper;
    private final BenefitDeterminationMapper benefitDeterminationMapper;
    private final BenefitDeterminationItemMapper benefitDeterminationItemMapper;
    private final SubsidyPersonMapper subsidyPersonMapper;
    private final StreetOfficeMapper streetOfficeMapper;
    private final VillageCommitteeMapper villageCommitteeMapper;

    @Override
    public Page<BenefitSuspensionListResp> list(BenefitSuspensionListReq req)
    {
        long pageNum = req == null ? 1L : req.pageNumOrDefault();
        long pageSize = req == null ? 10L : req.pageSizeOrDefault();
        String pauseReason = req == null ? null : req.getPauseReason();
        Page<BenefitSuspension> page = new Page<>(pageNum, pageSize);

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

        LambdaQueryWrapper<BenefitSuspension> wrapper = new LambdaQueryWrapper<BenefitSuspension>()
                .eq(BenefitSuspension::getDelFlag, "0")
                .eq(StringUtils.isNotBlank(pauseReason), BenefitSuspension::getPauseReason, pauseReason)
                .orderByDesc(BenefitSuspension::getCreateTime);
        if (personIds != null)
        {
            wrapper.in(BenefitSuspension::getSubsidyPersonId, personIds);
        }
        Page<BenefitSuspension> resultPage = benefitSuspensionMapper.selectPage(page, wrapper);

        if (resultPage.getRecords().isEmpty())
        {
            Page<BenefitSuspensionListResp> empty = new Page<>(pageNum, pageSize);
            empty.setTotal(resultPage.getTotal());
            return empty;
        }

        List<BenefitSuspension> records = resultPage.getRecords();
        Set<Long> queryPersonIds = records.stream().map(BenefitSuspension::getSubsidyPersonId).collect(Collectors.toSet());
        Set<Long> suspensionIds = records.stream().map(BenefitSuspension::getId).collect(Collectors.toSet());
        Set<Long> determinationIds = records.stream().map(BenefitSuspension::getDeterminationId).collect(Collectors.toSet());

        Map<Long, SubsidyPerson> personMap = subsidyPersonMapper.selectBatchIds(queryPersonIds)
                .stream()
                .collect(Collectors.toMap(SubsidyPerson::getId, p -> p));

        Map<Long, List<BenefitSuspensionItem>> pauseItemMap = benefitSuspensionItemMapper.selectList(
                        new LambdaQueryWrapper<BenefitSuspensionItem>()
                                .in(BenefitSuspensionItem::getSuspensionId, suspensionIds)
                                .eq(BenefitSuspensionItem::getDelFlag, "0"))
                .stream()
                .collect(Collectors.groupingBy(BenefitSuspensionItem::getSuspensionId));

        Map<Long, List<BenefitDeterminationItem>> determinationItemMap = benefitDeterminationItemMapper.selectList(
                        new LambdaQueryWrapper<BenefitDeterminationItem>()
                                .in(BenefitDeterminationItem::getDeterminationId, determinationIds)
                                .eq(BenefitDeterminationItem::getDelFlag, "0"))
                .stream()
                .collect(Collectors.groupingBy(BenefitDeterminationItem::getDeterminationId));

        List<BenefitSuspensionListResp> rows = records.stream().map(record -> {
            BenefitSuspensionListResp resp = new BenefitSuspensionListResp();
            resp.setId(record.getId());
            SubsidyPerson person = personMap.get(record.getSubsidyPersonId());
            resp.setName(person == null ? null : person.getName());
            resp.setIdCardNo(record.getIdCardNo());
            resp.setPauseMonth(record.getPauseMonth());
            resp.setPauseReason(record.getPauseReason());

            List<BenefitDeterminationItem> allItems = determinationItemMap.getOrDefault(record.getDeterminationId(), List.of());
            String allTypes = allItems.stream()
                    .map(BenefitDeterminationItem::getSubsidyType)
                    .distinct()
                    .map(this::subsidyTypeLabel)
                    .collect(Collectors.joining("、"));
            resp.setSubsidyTypes(allTypes);

            List<BenefitSuspensionItem> pausedItems = pauseItemMap.getOrDefault(record.getId(), List.of());
            String pausedTypes = pausedItems.stream()
                    .map(BenefitSuspensionItem::getSubsidyType)
                    .distinct()
                    .map(this::subsidyTypeLabel)
                    .collect(Collectors.joining("、"));
            resp.setPausedSubsidyTypes(pausedTypes);
            return resp;
        }).toList();

        Page<BenefitSuspensionListResp> responsePage = new Page<>(pageNum, pageSize);
        responsePage.setTotal(resultPage.getTotal());
        responsePage.setRecords(rows);
        return responsePage;
    }

    @Override
    public BenefitSuspensionCandidateResp findCandidateByIdCardNo(String idCardNo)
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

        List<BenefitDeterminationItem> items = benefitDeterminationItemMapper.selectList(new LambdaQueryWrapper<BenefitDeterminationItem>()
                .eq(BenefitDeterminationItem::getDeterminationId, determination.getId())
                .eq(BenefitDeterminationItem::getDelFlag, "0")
                .orderByAsc(BenefitDeterminationItem::getId));
        if (items.isEmpty())
        {
            throw new ServiceException("未查询到待遇人员");
        }

        BenefitSuspensionCandidateResp resp = new BenefitSuspensionCandidateResp();
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
        resp.setTreatmentItems(items.stream().map(this::toCandidateItem).toList());
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(BenefitSuspensionCreateReq req)
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

        YearMonth pauseYm = parsePauseMonth(req.getPauseMonth());
        List<BenefitDeterminationItem> selectedItems = benefitDeterminationItemMapper.selectList(
                new LambdaQueryWrapper<BenefitDeterminationItem>()
                        .in(BenefitDeterminationItem::getId, req.getDeterminationItemIds())
                        .eq(BenefitDeterminationItem::getDeterminationId, req.getDeterminationId())
                        .eq(BenefitDeterminationItem::getDelFlag, "0"));
        if (selectedItems.size() != req.getDeterminationItemIds().size())
        {
            throw new ServiceException("所选补贴类型不存在或不属于当前待遇核定记录");
        }
        List<BenefitDeterminationItem> pausedItems = selectedItems.stream()
                .filter(item -> "1".equals(StringUtils.defaultIfBlank(item.getBenefitStatus(), "0")))
                .toList();
        if (!pausedItems.isEmpty())
        {
            throw new ServiceException("选中的补贴类型存在已暂停记录，请先恢复后再操作");
        }

        BenefitSuspension suspension = new BenefitSuspension();
        suspension.setDeterminationId(req.getDeterminationId());
        suspension.setSubsidyPersonId(req.getSubsidyPersonId());
        suspension.setIdCardNo(req.getIdCardNo());
        suspension.setPauseMonth(yearMonthToDate(pauseYm));
        suspension.setPauseReason(req.getPauseReason());
        suspension.setRemark(req.getRemark());
        suspension.setStatus("0");
        suspension.setCreateBy(SecurityUtils.getUsername());
        suspension.setCreateTime(LocalDateTime.now());
        suspension.setUpdateBy(SecurityUtils.getUsername());
        suspension.setUpdateTime(LocalDateTime.now());
        benefitSuspensionMapper.insert(suspension);

        YearMonth currentYm = YearMonth.now();
        for (BenefitDeterminationItem selectedItem : selectedItems)
        {
            BenefitSuspensionItem suspensionItem = buildSuspensionItem(suspension.getId(), selectedItem, pauseYm, currentYm);
            benefitSuspensionItemMapper.insert(suspensionItem);
        }

        for (BenefitDeterminationItem selectedItem : selectedItems)
        {
            BenefitDeterminationItem update = new BenefitDeterminationItem();
            update.setId(selectedItem.getId());
            update.setBenefitStatus("1");
            update.setPauseStartMonth(yearMonthToDate(pauseYm));
            update.setUpdateBy(SecurityUtils.getUsername());
            update.setUpdateTime(LocalDateTime.now());
            benefitDeterminationItemMapper.updateById(update);
        }
        return suspension.getId();
    }

    @Override
    public BenefitSuspensionDetailResp getDetail(Long id)
    {
        BenefitSuspension suspension = benefitSuspensionMapper.selectById(id);
        if (suspension == null || !"0".equals(suspension.getDelFlag()))
        {
            throw new ServiceException("暂停记录不存在");
        }
        SubsidyPerson person = subsidyPersonMapper.selectById(suspension.getSubsidyPersonId());
        BenefitDetermination determination = benefitDeterminationMapper.selectById(suspension.getDeterminationId());
        if (person == null || determination == null)
        {
            throw new ServiceException("暂停记录关联的人员或待遇核定不存在");
        }

        List<BenefitDeterminationItem> treatmentItems = benefitDeterminationItemMapper.selectList(
                new LambdaQueryWrapper<BenefitDeterminationItem>()
                        .eq(BenefitDeterminationItem::getDeterminationId, suspension.getDeterminationId())
                        .eq(BenefitDeterminationItem::getDelFlag, "0")
                        .orderByAsc(BenefitDeterminationItem::getId));
        List<BenefitSuspensionItem> recoverItems = benefitSuspensionItemMapper.selectList(
                new LambdaQueryWrapper<BenefitSuspensionItem>()
                        .eq(BenefitSuspensionItem::getSuspensionId, id)
                        .eq(BenefitSuspensionItem::getDelFlag, "0")
                        .orderByAsc(BenefitSuspensionItem::getId));

        BenefitSuspensionDetailResp detail = new BenefitSuspensionDetailResp();
        detail.setId(suspension.getId());
        detail.setDeterminationId(suspension.getDeterminationId());
        detail.setSubsidyPersonId(suspension.getSubsidyPersonId());
        detail.setName(person.getName());
        detail.setIdCardNo(suspension.getIdCardNo());
        detail.setPauseMonth(suspension.getPauseMonth());
        detail.setPauseReason(suspension.getPauseReason());
        detail.setRemark(suspension.getRemark());
        detail.setTreatmentItems(treatmentItems.stream().map(this::toDetailTreatmentItem).toList());
        detail.setRecoverItems(recoverItems.stream().map(this::toDetailRecoverItem).toList());
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

    private BenefitSuspensionCandidateResp.TreatmentItem toCandidateItem(BenefitDeterminationItem item)
    {
        BenefitSuspensionCandidateResp.TreatmentItem result = new BenefitSuspensionCandidateResp.TreatmentItem();
        result.setDeterminationItemId(item.getId());
        result.setSubsidyType(item.getSubsidyType());
        result.setBenefitStartMonth(yearMonthToDate(item.getBenefitStartYear(), item.getBenefitStartMonth()));
        result.setBenefitStatus(StringUtils.defaultIfBlank(item.getBenefitStatus(), "0"));
        result.setSubsidyStandard(item.getSubsidyStandard());
        return result;
    }

    private BenefitSuspensionDetailResp.TreatmentItem toDetailTreatmentItem(BenefitDeterminationItem item)
    {
        BenefitSuspensionDetailResp.TreatmentItem result = new BenefitSuspensionDetailResp.TreatmentItem();
        result.setDeterminationItemId(item.getId());
        result.setSubsidyType(item.getSubsidyType());
        result.setBenefitStartMonth(yearMonthToDate(item.getBenefitStartYear(), item.getBenefitStartMonth()));
        result.setBenefitStatus(StringUtils.defaultIfBlank(item.getBenefitStatus(), "0"));
        result.setSubsidyStandard(item.getSubsidyStandard());
        return result;
    }

    private BenefitSuspensionDetailResp.RecoverItem toDetailRecoverItem(BenefitSuspensionItem item)
    {
        BenefitSuspensionDetailResp.RecoverItem result = new BenefitSuspensionDetailResp.RecoverItem();
        result.setDeterminationItemId(item.getDeterminationItemId());
        result.setSubsidyType(item.getSubsidyType());
        result.setNeedRecover(item.getNeedRecover());
        result.setRecoverStartMonth(item.getRecoverStartMonth());
        result.setRecoverEndMonth(item.getRecoverEndMonth());
        result.setRecoverMonths(item.getRecoverMonths());
        result.setRecoverAmount(item.getRecoverAmount());
        return result;
    }

    private BenefitSuspensionItem buildSuspensionItem(Long suspensionId,
                                                      BenefitDeterminationItem item,
                                                      YearMonth pauseYm,
                                                      YearMonth currentYm)
    {
        boolean needRecover = pauseYm.isBefore(currentYm);
        YearMonth recoverEndYm = needRecover ? currentYm.minusMonths(1) : null;
        int recoverMonths = 0;
        if (needRecover && recoverEndYm != null && !pauseYm.isAfter(recoverEndYm))
        {
            recoverMonths = (int) ChronoUnit.MONTHS.between(pauseYm, recoverEndYm) + 1;
        }
        BigDecimal standard = item.getSubsidyStandard() == null ? BigDecimal.ZERO : item.getSubsidyStandard();
        BigDecimal recoverAmount = standard.multiply(BigDecimal.valueOf(recoverMonths)).setScale(2, RoundingMode.HALF_UP);

        BenefitSuspensionItem suspensionItem = new BenefitSuspensionItem();
        suspensionItem.setSuspensionId(suspensionId);
        suspensionItem.setDeterminationItemId(item.getId());
        suspensionItem.setSubsidyType(item.getSubsidyType());
        suspensionItem.setBenefitStartMonth(yearMonthToDate(item.getBenefitStartYear(), item.getBenefitStartMonth()));
        suspensionItem.setSubsidyStandard(standard);
        suspensionItem.setNeedRecover(needRecover ? "1" : "0");
        suspensionItem.setRecoverStartMonth(yearMonthToDate(pauseYm));
        suspensionItem.setRecoverEndMonth(yearMonthToDate(recoverEndYm));
        suspensionItem.setRecoverMonths(recoverMonths);
        suspensionItem.setRecoverAmount(recoverAmount);
        suspensionItem.setStatus("0");
        suspensionItem.setCreateBy(SecurityUtils.getUsername());
        suspensionItem.setCreateTime(LocalDateTime.now());
        suspensionItem.setUpdateBy(SecurityUtils.getUsername());
        suspensionItem.setUpdateTime(LocalDateTime.now());
        return suspensionItem;
    }

    private YearMonth parsePauseMonth(String pauseMonth)
    {
        if (StringUtils.isBlank(pauseMonth) || !pauseMonth.matches("\\d{4}-\\d{2}"))
        {
            throw new ServiceException("暂停年月格式错误，应为yyyy-MM");
        }
        String[] parts = pauseMonth.split("-");
        return YearMonth.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    private Date yearMonthToDate(Integer year, Integer month)
    {
        if (year == null || month == null)
        {
            return null;
        }
        return java.sql.Date.valueOf(YearMonth.of(year, month).atDay(1));
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
