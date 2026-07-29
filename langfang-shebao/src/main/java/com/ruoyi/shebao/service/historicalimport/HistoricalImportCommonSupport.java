package com.ruoyi.shebao.service.historicalimport;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.constant.SubsidyApprovalStatus;
import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.domain.BenefitDeterminationItem;
import com.ruoyi.shebao.domain.BenefitSuspension;
import com.ruoyi.shebao.domain.BenefitSuspensionItem;
import com.ruoyi.shebao.domain.PersonCancel;
import com.ruoyi.shebao.domain.StreetOffice;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.mapper.BenefitDeterminationItemMapper;
import com.ruoyi.shebao.mapper.BenefitDeterminationMapper;
import com.ruoyi.shebao.mapper.BenefitSuspensionItemMapper;
import com.ruoyi.shebao.mapper.BenefitSuspensionMapper;
import com.ruoyi.shebao.mapper.StreetOfficeMapper;
import com.ruoyi.shebao.mapper.VillageCommitteeMapper;
import com.ruoyi.shebao.service.FinanceBenefitRecoveryService;
import com.ruoyi.shebao.service.PersonCancelService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

/**
 * 历史数据导入公共校验与待遇/暂停/注销写入
 */
@Component
@RequiredArgsConstructor
public class HistoricalImportCommonSupport
{
    static final Pattern ID_CARD_PATTERN = Pattern.compile(
            "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter YM_FMT = DateTimeFormatter.ofPattern("yyyy-MM");
    static final int PAUSE_REASON_REMARK_MAX_LENGTH = 500;
    static final int DEMOLITION_REASON_MAX_LENGTH = 200;

    private final SubsidyPersonService subsidyPersonService;
    private final StreetOfficeMapper streetOfficeMapper;
    private final VillageCommitteeMapper villageCommitteeMapper;
    private final BenefitDeterminationMapper benefitDeterminationMapper;
    private final BenefitDeterminationItemMapper benefitDeterminationItemMapper;
    private final BenefitSuspensionMapper benefitSuspensionMapper;
    private final BenefitSuspensionItemMapper benefitSuspensionItemMapper;
    private final FinanceBenefitRecoveryService financeBenefitRecoveryService;
    private final PersonCancelService personCancelService;

    public String validateAndNormalizeIdCard(String rawIdCard)
    {
        if (StringUtils.isBlank(rawIdCard))
        {
            throw new ServiceException("身份证号不能为空");
        }
        String idCard = rawIdCard.trim().toUpperCase();
        if (!ID_CARD_PATTERN.matcher(idCard).matches())
        {
            throw new ServiceException("身份证号格式不正确");
        }
        return idCard;
    }

    public void validateCommonPerson(HistoricalImportCommonColumns row, ValidatedContext ctx, String idCard)
    {
        ctx.personStatus = HistoricalImportDictSupport.requireDictByLabelOrValue(
                "shebao_person_status", row.getPersonStatus(), "人员状态");
        ctx.subsidyStatus = HistoricalImportDictSupport.requireDictByLabelOrValue(
                "shebao_subsidy_status", row.getSubsidyStatus(), "参保状态");
        ctx.isVillageCoopMember = HistoricalImportDictSupport.requireYesNo(
                row.getIsVillageCoopMember(), "是否村合作经济组织成员");

        SubsidyPerson existing = subsidyPersonService.selectSubsidyPersonByIdCardNo(idCard);
        boolean alreadyCancelled = existing != null && hasApprovedCancel(existing.getId());

        boolean excelHasCancel = StringUtils.isNotBlank(row.getCancelTime())
                || StringUtils.isNotBlank(row.getCancelReason());
        if (alreadyCancelled)
        {
            ctx.hasCancelBlock = false;
        }
        else
        {
            ctx.hasCancelBlock = excelHasCancel;
            if ("1".equals(ctx.subsidyStatus) && !ctx.hasCancelBlock)
            {
                throw new ServiceException("参保状态为终止时必须填写注销时间与注销原因");
            }
            if (ctx.hasCancelBlock)
            {
                ctx.cancelTime = parseRequiredDateNotAfterToday(row.getCancelTime(), "注销时间");
                ctx.cancelReason = HistoricalImportDictSupport.requireDictByLabelOrValue(
                        "cancel_reason", row.getCancelReason(), "注销原因");
            }
        }

        if (existing == null)
        {
            if (StringUtils.isBlank(row.getName()))
            {
                throw new ServiceException("新建人员时姓名不能为空");
            }
            ctx.streetOffice = resolveStreetOffice(row.getStreetOfficeName());
            ctx.villageCommittee = resolveVillageCommittee(row.getVillageCommitteeName(), ctx.streetOffice.getId());
        }
    }

    public void validateBenefitAndPause(HistoricalImportCommonColumns row, ValidatedContext ctx, String idCard)
    {
        ctx.hasBenefitBlock = hasBenefitDeterminationData(row);
        if (ctx.hasBenefitBlock)
        {
            ctx.grantOrg = HistoricalImportDictSupport.requireDictByLabelOrValue(
                    "shebao_grant_org", row.getGrantOrg(), "发放机构");
            ctx.accountName = HistoricalImportDictSupport.requireNotBlank(row.getAccountName(), "开户名");
            ctx.relationToInsured = HistoricalImportDictSupport.requireNotBlank(row.getRelationToInsured(), "与参保人关系");
            ctx.bankAccount = HistoricalImportDictSupport.requireNotBlank(row.getBankAccount(), "银行账号");
            ctx.eligibleYearMonth = computeEligibleYearMonthFromIdCard(idCard);
            ctx.subsidyStandard = parseRequiredAmount(row.getSubsidyStandard(), "补贴标准");
            ctx.benefitStartMonth = parseYearMonth(row.getBenefitStartMonth(), "享受开始年月");
            if (row.getBenefitMonths() == null)
            {
                throw new ServiceException("补发月数不能为空");
            }
            if (row.getBenefitMonths() < 0)
            {
                throw new ServiceException("补发月数不能小于0");
            }
            ctx.benefitMonths = row.getBenefitMonths();
            ctx.benefitAmount = parseRequiredAmount(row.getBenefitAmount(), "补发金额");
        }

        ctx.hasPauseBlock = StringUtils.isNotBlank(row.getPauseMonth())
                || StringUtils.isNotBlank(row.getPauseReason())
                || StringUtils.isNotBlank(row.getPauseReasonRemark())
                || StringUtils.isNotBlank(row.getRecoverStartMonth())
                || StringUtils.isNotBlank(row.getRecoverEndMonth())
                || StringUtils.isNotBlank(row.getRecoverAmount());
        if (ctx.hasPauseBlock)
        {
            if (!ctx.hasBenefitBlock)
            {
                throw new ServiceException("填写待遇暂停信息前须先填写完整的待遇核定信息");
            }
            ctx.pauseMonth = parseYearMonth(row.getPauseMonth(), "暂停年月");
            ctx.pauseReason = HistoricalImportDictSupport.requireDictByLabelOrValue(
                    "pause_reason", row.getPauseReason(), "暂停原因");
            ctx.pauseReasonRemark = parseOptionalMaxLength(
                    row.getPauseReasonRemark(), "暂停原因备注", PAUSE_REASON_REMARK_MAX_LENGTH);

            boolean hasRecoverStart = StringUtils.isNotBlank(row.getRecoverStartMonth());
            boolean hasRecoverEnd = StringUtils.isNotBlank(row.getRecoverEndMonth());
            boolean hasRecoverAmount = StringUtils.isNotBlank(row.getRecoverAmount());
            boolean anyRecover = hasRecoverStart || hasRecoverEnd || hasRecoverAmount;
            boolean allRecover = hasRecoverStart && hasRecoverEnd && hasRecoverAmount;
            if (anyRecover && !allRecover)
            {
                throw new ServiceException("追回开始年月、追回结束年月、需要追回金额须同时填写或同时留空");
            }
            if (allRecover)
            {
                ctx.needRecover = true;
                ctx.recoverStartMonth = parseYearMonth(row.getRecoverStartMonth(), "追回开始年月");
                ctx.recoverEndMonth = parseYearMonth(row.getRecoverEndMonth(), "追回结束年月");
                if (ctx.recoverStartMonth.isAfter(ctx.recoverEndMonth))
                {
                    throw new ServiceException("追回开始年月不能晚于追回结束年月");
                }
                ctx.recoverAmount = parseRequiredAmount(row.getRecoverAmount(), "需要追回金额");
                ctx.recoverMonths = (int) ChronoUnit.MONTHS.between(ctx.recoverStartMonth, ctx.recoverEndMonth) + 1;
                if (ctx.recoverMonths <= 0)
                {
                    throw new ServiceException("追回月数必须大于0");
                }
            }
            else
            {
                ctx.needRecover = false;
            }
        }
    }

    public boolean hasApprovedCancel(Long subsidyPersonId)
    {
        return personCancelService.count(new LambdaQueryWrapper<PersonCancel>()
                .eq(PersonCancel::getSubsidyPersonId, subsidyPersonId)
                .eq(PersonCancel::getDelFlag, "0")
                .eq(PersonCancel::getApprovalStatus, "approved")) > 0;
    }

    public Long upsertBenefitDetermination(Long subsidyPersonId, String idCardNo, ValidatedContext ctx,
                                           String subsidyType, String villageStreet, LocalDate eventDate,
                                           String duplicateItemMessage)
    {
        BenefitDetermination determination = benefitDeterminationMapper.selectOne(new LambdaQueryWrapper<BenefitDetermination>()
                .eq(BenefitDetermination::getDelFlag, "0")
                .and(w -> w.eq(BenefitDetermination::getSubsidyPersonId, subsidyPersonId)
                        .or()
                        .eq(BenefitDetermination::getIdCardNo, idCardNo))
                .last("limit 1"));

        java.util.Date now = new java.util.Date();
        String operator = SecurityUtils.getUsername();

        if (determination == null)
        {
            determination = new BenefitDetermination();
            determination.setSubsidyPersonId(subsidyPersonId);
            determination.setIdCardNo(idCardNo);
            determination.setGrantOrg(ctx.grantOrg);
            determination.setAccountName(ctx.accountName);
            determination.setRelationToInsured(ctx.relationToInsured);
            determination.setBankAccount(ctx.bankAccount);
            determination.setEligibleYear(ctx.eligibleYearMonth.getYear());
            determination.setEligibleMonth(ctx.eligibleYearMonth.getMonthValue());
            determination.setApprovalStatus(SubsidyApprovalStatus.APPROVED);
            determination.setMaterialStatus("verified");
            determination.setPaymentPlanGenerated("0");
            determination.setDelFlag("0");
            determination.setSubmitBy(operator);
            determination.setSubmitTime(now);
            determination.setReviewBy(operator);
            determination.setReviewTime(now);
            determination.setCreateBy(operator);
            determination.setCreateTime(now);
            benefitDeterminationMapper.insert(determination);
        }
        else
        {
            long duplicateItem = benefitDeterminationItemMapper.selectCount(new LambdaQueryWrapper<BenefitDeterminationItem>()
                    .eq(BenefitDeterminationItem::getDeterminationId, determination.getId())
                    .eq(BenefitDeterminationItem::getSubsidyType, subsidyType)
                    .eq(BenefitDeterminationItem::getDelFlag, "0"));
            if (duplicateItem > 0)
            {
                throw new ServiceException(duplicateItemMessage);
            }
        }

        BenefitDeterminationItem item = new BenefitDeterminationItem();
        item.setDeterminationId(determination.getId());
        item.setSubsidyType(subsidyType);
        item.setVillageStreet(trim(villageStreet));
        item.setEventDate(eventDate);
        item.setSubsidyStandard(ctx.subsidyStandard);
        item.setBenefitStartYear(ctx.benefitStartMonth.getYear());
        item.setBenefitStartMonth(ctx.benefitStartMonth.getMonthValue());
        item.setBenefitMonths(ctx.benefitMonths);
        item.setBenefitAmount(ctx.benefitAmount);
        item.setBenefitStatus("0");
        item.setDelFlag("0");
        item.setCreateBy(operator);
        item.setCreateTime(LocalDateTime.now());
        benefitDeterminationItemMapper.insert(item);
        return determination.getId();
    }

    public void createHistoricalSuspension(Long determinationId, Long subsidyPersonId, String idCardNo,
                                           String subsidyType, ValidatedContext ctx)
    {
        BenefitDeterminationItem detItem = benefitDeterminationItemMapper.selectOne(new LambdaQueryWrapper<BenefitDeterminationItem>()
                .eq(BenefitDeterminationItem::getDeterminationId, determinationId)
                .eq(BenefitDeterminationItem::getSubsidyType, subsidyType)
                .eq(BenefitDeterminationItem::getDelFlag, "0")
                .last("limit 1"));
        if (detItem == null)
        {
            throw new ServiceException("待遇核定明细不存在");
        }
        if ("1".equals(StringUtils.defaultIfBlank(detItem.getBenefitStatus(), "0")))
        {
            throw new ServiceException("该补贴已处于暂停状态");
        }

        String operator = SecurityUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();

        BenefitSuspension suspension = new BenefitSuspension();
        suspension.setDeterminationId(determinationId);
        suspension.setSubsidyPersonId(subsidyPersonId);
        suspension.setIdCardNo(idCardNo);
        suspension.setPauseMonth(yearMonthToDate(ctx.pauseMonth));
        suspension.setPauseReason(ctx.pauseReason);
        suspension.setRemark(ctx.pauseReasonRemark);
        suspension.setStatus("0");
        suspension.setCreateBy(operator);
        suspension.setCreateTime(now);
        suspension.setUpdateBy(operator);
        suspension.setUpdateTime(now);
        benefitSuspensionMapper.insert(suspension);

        BenefitSuspensionItem suspensionItem = new BenefitSuspensionItem();
        suspensionItem.setSuspensionId(suspension.getId());
        suspensionItem.setDeterminationItemId(detItem.getId());
        suspensionItem.setSubsidyType(detItem.getSubsidyType());
        suspensionItem.setBenefitStartMonth(yearMonthToDate(detItem.getBenefitStartYear(), detItem.getBenefitStartMonth()));
        suspensionItem.setSubsidyStandard(detItem.getSubsidyStandard());
        suspensionItem.setNeedRecover(ctx.needRecover ? "1" : "0");
        suspensionItem.setPauseActive("1");
        if (ctx.needRecover)
        {
            suspensionItem.setRecoverStartMonth(yearMonthToDate(ctx.recoverStartMonth));
            suspensionItem.setRecoverEndMonth(yearMonthToDate(ctx.recoverEndMonth));
            suspensionItem.setRecoverMonths(ctx.recoverMonths);
            suspensionItem.setRecoverAmount(ctx.recoverAmount);
        }
        else
        {
            suspensionItem.setRecoverMonths(0);
        }
        suspensionItem.setStatus("0");
        suspensionItem.setCreateBy(operator);
        suspensionItem.setCreateTime(now);
        suspensionItem.setUpdateBy(operator);
        suspensionItem.setUpdateTime(now);
        benefitSuspensionItemMapper.insert(suspensionItem);
        financeBenefitRecoveryService.syncFromSuspensionItem(suspension, suspensionItem);

        BenefitDeterminationItem updateItem = new BenefitDeterminationItem();
        updateItem.setId(detItem.getId());
        updateItem.setBenefitStatus("1");
        updateItem.setPauseStartMonth(yearMonthToDate(ctx.pauseMonth));
        updateItem.setUpdateBy(operator);
        updateItem.setUpdateTime(now);
        benefitDeterminationItemMapper.updateById(updateItem);
    }

    public void applyPersonCancelIfNeeded(Long subsidyPersonId, ValidatedContext ctx)
    {
        if (!ctx.hasCancelBlock || hasApprovedCancel(subsidyPersonId))
        {
            return;
        }
        String operator = SecurityUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();
        PersonCancel pc = new PersonCancel();
        pc.setSubsidyPersonId(subsidyPersonId);
        pc.setDeathDate(ctx.cancelTime);
        pc.setCancelReason(ctx.cancelReason);
        pc.setApprovalStatus("approved");
        pc.setReviewBy(operator);
        pc.setReviewTime(now);
        pc.setDelFlag("0");
        pc.setCreateBy(operator);
        pc.setCreateTime(now);
        pc.setUpdateBy(operator);
        pc.setUpdateTime(now);
        personCancelService.save(pc);

        SubsidyPerson person = subsidyPersonService.selectSubsidyPersonById(subsidyPersonId);
        personCancelService.applyApprovedCancelToPerson(person, ctx.cancelTime, ctx.cancelReason);
    }

    public String parseGenderFromIdCard(String idCardNo)
    {
        int genderCode = Integer.parseInt(idCardNo.substring(16, 17));
        return genderCode % 2 == 0 ? "2" : "1";
    }

    public LocalDate parseBirthdayFromIdCard(String idCardNo)
    {
        try
        {
            return LocalDate.parse(idCardNo.substring(6, 14), DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        catch (Exception ex)
        {
            throw new ServiceException("无法从身份证号解析出生日期");
        }
    }

    public LocalDate parseOptionalDate(String value, String label)
    {
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.matches("^\\d+(\\.\\d+)?$"))
        {
            double serial = Double.parseDouble(trimmed);
            if (serial >= 20000 && serial < 100000)
            {
                return DateUtil.getJavaDate(serial).toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
            }
        }
        try
        {
            return LocalDate.parse(trimmed, DATE_FMT);
        }
        catch (DateTimeParseException ex)
        {
            throw new ServiceException(label + "格式应为 yyyy-MM-dd");
        }
    }

    public String parseOptionalMaxLength(String value, String label, int maxLength)
    {
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLength)
        {
            throw new ServiceException(label + "不能超过" + maxLength + "个字符");
        }
        return trimmed;
    }

    public String trim(String value)
    {
        return value == null ? null : value.trim();
    }

    boolean hasBenefitDeterminationData(HistoricalImportCommonColumns row)
    {
        return StringUtils.isNotBlank(row.getGrantOrg())
                || StringUtils.isNotBlank(row.getAccountName())
                || StringUtils.isNotBlank(row.getRelationToInsured())
                || StringUtils.isNotBlank(row.getBankAccount())
                || StringUtils.isNotBlank(row.getSubsidyStandard())
                || StringUtils.isNotBlank(row.getBenefitStartMonth())
                || row.getBenefitMonths() != null
                || StringUtils.isNotBlank(row.getBenefitAmount());
    }

    StreetOffice resolveStreetOffice(String name)
    {
        if (StringUtils.isBlank(name))
        {
            throw new ServiceException("所属街道办不能为空");
        }
        StreetOffice streetOffice = streetOfficeMapper.selectOne(new LambdaQueryWrapper<StreetOffice>()
                .eq(StreetOffice::getStreetName, name.trim())
                .eq(StreetOffice::getDelFlag, "0")
                .last("limit 1"));
        if (streetOffice == null)
        {
            throw new ServiceException("所属街道办不存在：" + name.trim());
        }
        return streetOffice;
    }

    VillageCommittee resolveVillageCommittee(String name, Long streetOfficeId)
    {
        if (StringUtils.isBlank(name))
        {
            throw new ServiceException("所属村委会不能为空");
        }
        VillageCommittee villageCommittee = villageCommitteeMapper.selectOne(new LambdaQueryWrapper<VillageCommittee>()
                .eq(VillageCommittee::getVillageName, name.trim())
                .eq(VillageCommittee::getStreetOfficeId, streetOfficeId)
                .eq(VillageCommittee::getDelFlag, "0")
                .last("limit 1"));
        if (villageCommittee == null)
        {
            throw new ServiceException("所属村委会不存在或与街道办不匹配：" + name.trim());
        }
        return villageCommittee;
    }

    private LocalDate parseRequiredDateNotAfterToday(String value, String label)
    {
        if (StringUtils.isBlank(value))
        {
            throw new ServiceException(label + "不能为空");
        }
        LocalDate date = parseOptionalDate(value, label);
        if (date == null)
        {
            throw new ServiceException(label + "不能为空");
        }
        if (date.isAfter(LocalDate.now()))
        {
            throw new ServiceException(label + "不能晚于今天");
        }
        return date;
    }

    private YearMonth parseYearMonth(String value, String label)
    {
        if (StringUtils.isBlank(value))
        {
            throw new ServiceException(label + "不能为空");
        }
        String normalized = normalizeYearMonthText(value.trim());
        try
        {
            return YearMonth.parse(normalized, YM_FMT);
        }
        catch (DateTimeParseException ex)
        {
            try
            {
                return YearMonth.from(LocalDate.parse(normalized, DATE_FMT));
            }
            catch (DateTimeParseException ignored)
            {
                throw new ServiceException(label + "格式应为 yyyy-MM");
            }
        }
    }

    private String normalizeYearMonthText(String value)
    {
        if (value.matches("^\\d{6}$"))
        {
            return value.substring(0, 4) + "-" + value.substring(4, 6);
        }
        if (value.matches("^\\d+(\\.\\d+)?$"))
        {
            double serial = Double.parseDouble(value);
            if (serial >= 20000 && serial < 100000)
            {
                LocalDate excelDate = DateUtil.getJavaDate(serial).toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                return excelDate.format(YM_FMT);
            }
        }
        if (value.length() >= 7 && value.charAt(4) == '/' && value.indexOf(' ') < 0)
        {
            return value.substring(0, 4) + "-" + value.substring(5, 7);
        }
        return value;
    }

    private BigDecimal parseRequiredAmount(String value, String label)
    {
        if (StringUtils.isBlank(value))
        {
            throw new ServiceException(label + "不能为空");
        }
        try
        {
            return new BigDecimal(value.trim());
        }
        catch (NumberFormatException ex)
        {
            throw new ServiceException(label + "格式不正确");
        }
    }

    private YearMonth computeEligibleYearMonthFromIdCard(String idCardNo)
    {
        return YearMonth.from(parseBirthdayFromIdCard(idCardNo).plusYears(60));
    }

    private Date yearMonthToDate(YearMonth ym)
    {
        return Date.valueOf(ym.atDay(1));
    }

    private Date yearMonthToDate(Integer year, Integer month)
    {
        if (year == null || month == null)
        {
            return null;
        }
        return Date.valueOf(YearMonth.of(year, month).atDay(1));
    }

    @Data
    public static class ValidatedContext
    {
        private String personStatus;
        private String subsidyStatus;
        private String isVillageCoopMember;
        private StreetOffice streetOffice;
        private VillageCommittee villageCommittee;
        private boolean hasBenefitBlock;
        private boolean hasPauseBlock;
        private boolean hasCancelBlock;
        private LocalDate cancelTime;
        private String cancelReason;
        private String grantOrg;
        private String accountName;
        private String relationToInsured;
        private String bankAccount;
        private YearMonth eligibleYearMonth;
        private BigDecimal subsidyStandard;
        private YearMonth benefitStartMonth;
        private Integer benefitMonths;
        private BigDecimal benefitAmount;
        private YearMonth pauseMonth;
        private String pauseReason;
        private String pauseReasonRemark;
        private boolean needRecover;
        private YearMonth recoverStartMonth;
        private YearMonth recoverEndMonth;
        private BigDecimal recoverAmount;
        private int recoverMonths;
        private String demolitionReason;
        private LocalDate demolitionTime;
        private LocalDate recognitionTime;
    }
}
