package com.ruoyi.shebao.service.historicalimport;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.constant.SubsidyApprovalStatus;
import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.domain.BenefitDeterminationItem;
import com.ruoyi.shebao.domain.BenefitSuspension;
import com.ruoyi.shebao.domain.BenefitSuspensionItem;
import com.ruoyi.shebao.domain.HistoricalImportBatch;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.domain.PersonCancel;
import com.ruoyi.shebao.domain.StreetOffice;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.dto.LandLossResidentFormDto;
import com.ruoyi.shebao.dto.historicalimport.HistoricalImportResult;
import com.ruoyi.shebao.dto.historicalimport.LandLossHistoricalImportDto;
import com.ruoyi.shebao.enums.HistoricalImportSubsidyType;
import com.ruoyi.shebao.mapper.BenefitDeterminationItemMapper;
import com.ruoyi.shebao.mapper.BenefitDeterminationMapper;
import com.ruoyi.shebao.mapper.BenefitSuspensionItemMapper;
import com.ruoyi.shebao.mapper.BenefitSuspensionMapper;
import com.ruoyi.shebao.mapper.HistoricalImportBatchMapper;
import com.ruoyi.shebao.mapper.LandLossResidentMapper;
import com.ruoyi.shebao.mapper.StreetOfficeMapper;
import com.ruoyi.shebao.mapper.VillageCommitteeMapper;
import com.ruoyi.shebao.service.FinanceBenefitRecoveryService;
import com.ruoyi.shebao.service.PersonCancelService;
import com.ruoyi.shebao.service.SubsidyPersonService;
import com.ruoyi.shebao.service.support.SubsidyPersonRegistrationHelper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class LandLossHistoricalImportHandler implements HistoricalImportHandler
{
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter YM_FMT = DateTimeFormatter.ofPattern("yyyy-MM");
    /** 与待遇暂停页面备注字段、库表 varchar(500) 一致 */
    private static final int PAUSE_REASON_REMARK_MAX_LENGTH = 500;

    private final HistoricalImportBatchMapper historicalImportBatchMapper;
    private final SubsidyPersonService subsidyPersonService;
    private final SubsidyPersonRegistrationHelper subsidyPersonRegistrationHelper;
    private final LandLossResidentMapper landLossResidentMapper;
    private final StreetOfficeMapper streetOfficeMapper;
    private final VillageCommitteeMapper villageCommitteeMapper;
    private final BenefitDeterminationMapper benefitDeterminationMapper;
    private final BenefitDeterminationItemMapper benefitDeterminationItemMapper;
    private final BenefitSuspensionMapper benefitSuspensionMapper;
    private final BenefitSuspensionItemMapper benefitSuspensionItemMapper;
    private final FinanceBenefitRecoveryService financeBenefitRecoveryService;
    private final PersonCancelService personCancelService;
    private final PlatformTransactionManager transactionManager;
    private final HistoricalImportTemplateExporter historicalImportTemplateExporter;

    @Override
    public String subsidyType()
    {
        return HistoricalImportSubsidyType.LAND_LOSS_RESIDENT.getCode();
    }

    @Override
    public void exportTemplate(HttpServletResponse response) throws Exception
    {
        historicalImportTemplateExporter.exportLandLossTemplate(response);
    }

    @Override
    public List<?> parseRows(MultipartFile file) throws Exception
    {
        ExcelUtil<LandLossHistoricalImportDto> util = new ExcelUtil<>(LandLossHistoricalImportDto.class);
        List<LandLossHistoricalImportDto> rows = util.importExcel(file.getInputStream());
        if (rows == null)
        {
            return List.of();
        }
        return rows.stream().filter(this::isNotBlankRow).toList();
    }

    @Override
    public HistoricalImportResult process(List<?> rows, String fileName, String sourceFilePath)
    {
        @SuppressWarnings("unchecked")
        List<LandLossHistoricalImportDto> importRows = (List<LandLossHistoricalImportDto>) rows;
        if (CollectionUtils.isEmpty(importRows))
        {
            throw new ServiceException("导入文件无有效数据行");
        }

        List<LandLossHistoricalImportDto> failedRows = new ArrayList<>();
        int successCount = 0;
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        for (LandLossHistoricalImportDto row : importRows)
        {
            try
            {
                tx.execute(status -> {
                    importSingleRow(row);
                    return null;
                });
                successCount++;
            }
            catch (Exception ex)
            {
                LandLossHistoricalImportDto failed = copyRow(row);
                failed.setFailureReason(ex.getMessage());
                failedRows.add(failed);
            }
        }

        HistoricalImportBatch batch = new HistoricalImportBatch();
        batch.setSubsidyType(subsidyType());
        batch.setFileName(fileName);
        batch.setSourceFilePath(sourceFilePath);
        batch.setTotalRows(importRows.size());
        batch.setSuccessRows(successCount);
        batch.setFailureRows(failedRows.size());
        batch.setImportStatus(resolveImportStatus(successCount, failedRows.size()));
        batch.setCreateBy(SecurityUtils.getUsername());
        batch.setCreateTime(LocalDateTime.now());
        if (!failedRows.isEmpty())
        {
            batch.setFailureFilePath(saveFailureFile(failedRows));
        }
        historicalImportBatchMapper.insert(batch);

        HistoricalImportResult result = new HistoricalImportResult();
        result.setBatchId(batch.getId());
        result.setTotalRows(importRows.size());
        result.setSuccessRows(successCount);
        result.setFailureRows(failedRows.size());
        result.setHasFailureFile(!failedRows.isEmpty());
        result.setMessage(String.format("导入完成：共 %d 行，成功 %d 行，失败 %d 行", importRows.size(), successCount, failedRows.size()));
        return result;
    }

    private void importSingleRow(LandLossHistoricalImportDto row)
    {
        ValidatedRow validated = validateRow(row);
        LandLossResidentFormDto formDto = toFormDto(row, validated);

        SubsidyPerson existing = subsidyPersonService.selectSubsidyPersonByIdCardNo(formDto.getIdCardNo());
        if (existing != null)
        {
            assertNoApprovedCancel(existing.getId());
            long duplicate = landLossResidentMapper.selectCount(new LambdaQueryWrapper<LandLossResident>()
                    .eq(LandLossResident::getSubsidyPersonId, existing.getId())
                    .eq(LandLossResident::getDelFlag, "0"));
            if (duplicate > 0)
            {
                throw new ServiceException("该人员已存在失地居民登记记录");
            }
        }

        Long subsidyPersonId = subsidyPersonRegistrationHelper.resolveSubsidyPersonForCreate(formDto, personId -> false, person -> {
            person.setPersonStatus(validated.personStatus);
            person.setSubsidyStatus(validated.subsidyStatus);
            person.setIsVillageCoopMember(validated.isVillageCoopMember);
        });

        LandLossResident resident = new LandLossResident();
        resident.setSubsidyPersonId(subsidyPersonId);
        resident.setLandRequisitionTime(parseOptionalDate(row.getLandRequisitionTime(), "征地时间"));
        resident.setCompensationCompleteTime(parseOptionalDate(row.getCompensationCompleteTime(), "完成补偿时间"));
        resident.setRecognitionTime(parseOptionalDate(row.getRecognitionTime(), "认定时间"));
        resident.setLandRequisitionBatch(trim(row.getLandRequisitionBatch()));
        resident.setVillageStreet(trim(row.getVillageStreet()));
        resident.setIsVillageCoopMember(validated.isVillageCoopMember);
        resident.setRemark(trim(row.getRemark()));
        resident.setApprovalStatus(SubsidyApprovalStatus.APPROVED);
        resident.setCreateBy(SecurityUtils.getUsername());
        resident.setCreateTime(LocalDateTime.now());
        landLossResidentMapper.insert(resident);

        if (validated.hasBenefitBlock)
        {
            Long determinationId = createBenefitDetermination(subsidyPersonId, row, validated);
            if (validated.hasPauseBlock)
            {
                createHistoricalSuspension(determinationId, subsidyPersonId, row, validated);
            }
        }

        if (validated.hasCancelBlock)
        {
            createApprovedPersonCancel(subsidyPersonId, validated);
        }
    }

    private ValidatedRow validateRow(LandLossHistoricalImportDto row)
    {
        ValidatedRow validated = new ValidatedRow();
        if (StringUtils.isBlank(row.getIdCardNo()))
        {
            throw new ServiceException("身份证号不能为空");
        }
        String idCard = row.getIdCardNo().trim().toUpperCase();
        row.setIdCardNo(idCard);
        if (!ID_CARD_PATTERN.matcher(idCard).matches())
        {
            throw new ServiceException("身份证号格式不正确");
        }

        validated.personStatus = HistoricalImportDictSupport.requireDictByLabelOrValue(
                "shebao_person_status", row.getPersonStatus(), "人员状态");
        validated.subsidyStatus = HistoricalImportDictSupport.requireDictByLabelOrValue(
                "shebao_subsidy_status", row.getSubsidyStatus(), "参保状态");
        validated.isVillageCoopMember = HistoricalImportDictSupport.requireYesNo(
                row.getIsVillageCoopMember(), "是否村合作经济组织成员");

        validated.hasCancelBlock = StringUtils.isNotBlank(row.getCancelTime())
                || StringUtils.isNotBlank(row.getCancelReason());
        if ("1".equals(validated.subsidyStatus) && !validated.hasCancelBlock)
        {
            throw new ServiceException("参保状态为终止时必须填写注销时间与注销原因");
        }
        if (validated.hasCancelBlock)
        {
            validated.cancelTime = parseRequiredDateNotAfterToday(row.getCancelTime(), "注销时间");
            validated.cancelReason = HistoricalImportDictSupport.requireDictByLabelOrValue(
                    "cancel_reason", row.getCancelReason(), "注销原因");
        }

        SubsidyPerson existing = subsidyPersonService.selectSubsidyPersonByIdCardNo(idCard);
        if (existing == null)
        {
            if (StringUtils.isBlank(row.getName()))
            {
                throw new ServiceException("新建人员时姓名不能为空");
            }
            validated.streetOffice = resolveStreetOffice(row.getStreetOfficeName());
            validated.villageCommittee = resolveVillageCommittee(row.getVillageCommitteeName(), validated.streetOffice.getId());
        }
        else
        {
            assertNoApprovedCancel(existing.getId());
        }

        validated.hasBenefitBlock = hasBenefitDeterminationData(row);
        if (validated.hasBenefitBlock)
        {
            validated.grantOrg = HistoricalImportDictSupport.requireDictByLabelOrValue(
                    "shebao_grant_org", row.getGrantOrg(), "发放机构");
            validated.accountName = HistoricalImportDictSupport.requireNotBlank(row.getAccountName(), "开户名");
            validated.relationToInsured = HistoricalImportDictSupport.requireNotBlank(row.getRelationToInsured(), "与参保人关系");
            validated.bankAccount = HistoricalImportDictSupport.requireNotBlank(row.getBankAccount(), "银行账号");
            validated.eligibleYearMonth = computeEligibleYearMonthFromIdCard(idCard);
            validated.subsidyStandard = parseRequiredAmount(row.getSubsidyStandard(), "补贴标准");
            validated.benefitStartMonth = parseYearMonth(row.getBenefitStartMonth(), "享受开始年月");
            if (row.getBenefitMonths() == null)
            {
                throw new ServiceException("补发月数不能为空");
            }
            if (row.getBenefitMonths() < 0)
            {
                throw new ServiceException("补发月数不能小于0");
            }
            validated.benefitMonths = row.getBenefitMonths();
            validated.benefitAmount = parseRequiredAmount(row.getBenefitAmount(), "补发金额");
        }

        validated.hasPauseBlock = StringUtils.isNotBlank(row.getPauseMonth())
                || StringUtils.isNotBlank(row.getPauseReason())
                || StringUtils.isNotBlank(row.getPauseReasonRemark())
                || StringUtils.isNotBlank(row.getRecoverStartMonth())
                || StringUtils.isNotBlank(row.getRecoverEndMonth())
                || StringUtils.isNotBlank(row.getRecoverAmount());
        if (validated.hasPauseBlock)
        {
            if (!validated.hasBenefitBlock)
            {
                throw new ServiceException("填写待遇暂停信息前须先填写完整的待遇核定信息");
            }
            validated.pauseMonth = parseYearMonth(row.getPauseMonth(), "暂停年月");
            validated.pauseReason = HistoricalImportDictSupport.requireDictByLabelOrValue(
                    "pause_reason", row.getPauseReason(), "暂停原因");
            validated.pauseReasonRemark = parseOptionalMaxLength(
                    row.getPauseReasonRemark(), "暂停原因备注", PAUSE_REASON_REMARK_MAX_LENGTH);
            validated.recoverStartMonth = parseYearMonth(row.getRecoverStartMonth(), "追回开始年月");
            validated.recoverEndMonth = parseYearMonth(row.getRecoverEndMonth(), "追回结束年月");
            if (validated.recoverStartMonth.isAfter(validated.recoverEndMonth))
            {
                throw new ServiceException("追回开始年月不能晚于追回结束年月");
            }
            validated.recoverAmount = parseRequiredAmount(row.getRecoverAmount(), "需要追回金额");
            validated.recoverMonths = (int) ChronoUnit.MONTHS.between(validated.recoverStartMonth, validated.recoverEndMonth) + 1;
            if (validated.recoverMonths <= 0)
            {
                throw new ServiceException("追回月数必须大于0");
            }
        }
        return validated;
    }

    private StreetOffice resolveStreetOffice(String name)
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

    private VillageCommittee resolveVillageCommittee(String name, Long streetOfficeId)
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

    private Long createBenefitDetermination(Long subsidyPersonId, LandLossHistoricalImportDto row, ValidatedRow validated)
    {
        long existed = benefitDeterminationMapper.selectCount(new LambdaQueryWrapper<BenefitDetermination>()
                .eq(BenefitDetermination::getDelFlag, "0")
                .and(w -> w.eq(BenefitDetermination::getSubsidyPersonId, subsidyPersonId)
                        .or()
                        .eq(BenefitDetermination::getIdCardNo, row.getIdCardNo())));
        if (existed > 0)
        {
            throw new ServiceException("该人员已存在待遇核定记录");
        }

        java.util.Date now = new java.util.Date();
        String operator = SecurityUtils.getUsername();

        BenefitDetermination determination = new BenefitDetermination();
        determination.setSubsidyPersonId(subsidyPersonId);
        determination.setIdCardNo(row.getIdCardNo());
        determination.setGrantOrg(validated.grantOrg);
        determination.setAccountName(validated.accountName);
        determination.setRelationToInsured(validated.relationToInsured);
        determination.setBankAccount(validated.bankAccount);
        determination.setEligibleYear(validated.eligibleYearMonth.getYear());
        determination.setEligibleMonth(validated.eligibleYearMonth.getMonthValue());
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

        BenefitDeterminationItem item = new BenefitDeterminationItem();
        item.setDeterminationId(determination.getId());
        item.setSubsidyType("land_loss");
        item.setVillageStreet(trim(row.getVillageStreet()));
        item.setEventDate(parseOptionalDate(row.getLandRequisitionTime(), "征地时间"));
        item.setSubsidyStandard(validated.subsidyStandard);
        item.setBenefitStartYear(validated.benefitStartMonth.getYear());
        item.setBenefitStartMonth(validated.benefitStartMonth.getMonthValue());
        item.setBenefitMonths(validated.benefitMonths);
        item.setBenefitAmount(validated.benefitAmount);
        item.setBenefitStatus("0");
        item.setDelFlag("0");
        item.setCreateBy(operator);
        item.setCreateTime(LocalDateTime.now());
        benefitDeterminationItemMapper.insert(item);
        return determination.getId();
    }

    private void createHistoricalSuspension(Long determinationId, Long subsidyPersonId,
                                            LandLossHistoricalImportDto row, ValidatedRow validated)
    {
        BenefitDeterminationItem detItem = benefitDeterminationItemMapper.selectOne(new LambdaQueryWrapper<BenefitDeterminationItem>()
                .eq(BenefitDeterminationItem::getDeterminationId, determinationId)
                .eq(BenefitDeterminationItem::getSubsidyType, "land_loss")
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
        suspension.setIdCardNo(row.getIdCardNo());
        suspension.setPauseMonth(yearMonthToDate(validated.pauseMonth));
        suspension.setPauseReason(validated.pauseReason);
        suspension.setRemark(validated.pauseReasonRemark);
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
        suspensionItem.setNeedRecover("1");
        suspensionItem.setPauseActive("1");
        suspensionItem.setRecoverStartMonth(yearMonthToDate(validated.recoverStartMonth));
        suspensionItem.setRecoverEndMonth(yearMonthToDate(validated.recoverEndMonth));
        suspensionItem.setRecoverMonths(validated.recoverMonths);
        suspensionItem.setRecoverAmount(validated.recoverAmount);
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
        updateItem.setPauseStartMonth(yearMonthToDate(validated.pauseMonth));
        updateItem.setUpdateBy(operator);
        updateItem.setUpdateTime(now);
        benefitDeterminationItemMapper.updateById(updateItem);
    }

    private LandLossResidentFormDto toFormDto(LandLossHistoricalImportDto row, ValidatedRow validated)
    {
        LandLossResidentFormDto dto = new LandLossResidentFormDto();
        dto.setName(trim(row.getName()));
        dto.setIdCardNo(row.getIdCardNo());
        dto.setGender(parseGenderFromIdCard(row.getIdCardNo()));
        dto.setBirthday(parseBirthdayFromIdCard(row.getIdCardNo()));
        dto.setHouseholdRegistration(trim(row.getHouseholdRegistration()));
        dto.setHomeAddress(trim(row.getHomeAddress()));
        dto.setPhone(trim(row.getPhone()));
        if (validated.streetOffice != null)
        {
            dto.setStreetOfficeId(validated.streetOffice.getId());
        }
        if (validated.villageCommittee != null)
        {
            dto.setVillageCommitteeId(validated.villageCommittee.getId());
        }
        dto.setLandRequisitionTime(parseOptionalDate(row.getLandRequisitionTime(), "征地时间"));
        dto.setCompensationCompleteTime(parseOptionalDate(row.getCompensationCompleteTime(), "完成补偿时间"));
        dto.setRecognitionTime(parseOptionalDate(row.getRecognitionTime(), "认定时间"));
        dto.setLandRequisitionBatch(trim(row.getLandRequisitionBatch()));
        dto.setVillageStreet(trim(row.getVillageStreet()));
        dto.setIsVillageCoopMember(validated.isVillageCoopMember);
        dto.setRemark(trim(row.getRemark()));
        return dto;
    }

    private boolean hasBenefitDeterminationData(LandLossHistoricalImportDto row)
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

    private boolean isNotBlankRow(LandLossHistoricalImportDto row)
    {
        return row != null && (StringUtils.isNotBlank(row.getIdCardNo()) || StringUtils.isNotBlank(row.getName()));
    }

    private String resolveImportStatus(int success, int failure)
    {
        if (failure == 0)
        {
            return "completed";
        }
        if (success == 0)
        {
            return "failed";
        }
        return "partial_failed";
    }

    private String saveFailureFile(List<LandLossHistoricalImportDto> failedRows)
    {
        try
        {
            return historicalImportTemplateExporter.exportLandLossFailureFile(failedRows);
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("生成失败记录文件失败：" + e.getMessage());
        }
    }

    private LandLossHistoricalImportDto copyRow(LandLossHistoricalImportDto row)
    {
        LandLossHistoricalImportDto copy = new LandLossHistoricalImportDto();
        copy.setName(row.getName());
        copy.setIdCardNo(row.getIdCardNo());
        copy.setHouseholdRegistration(row.getHouseholdRegistration());
        copy.setHomeAddress(row.getHomeAddress());
        copy.setPhone(row.getPhone());
        copy.setStreetOfficeName(row.getStreetOfficeName());
        copy.setVillageCommitteeName(row.getVillageCommitteeName());
        copy.setSubsidyStatus(row.getSubsidyStatus());
        copy.setPersonStatus(row.getPersonStatus());
        copy.setCancelTime(row.getCancelTime());
        copy.setCancelReason(row.getCancelReason());
        copy.setLandRequisitionTime(row.getLandRequisitionTime());
        copy.setCompensationCompleteTime(row.getCompensationCompleteTime());
        copy.setRecognitionTime(row.getRecognitionTime());
        copy.setLandRequisitionBatch(row.getLandRequisitionBatch());
        copy.setVillageStreet(row.getVillageStreet());
        copy.setIsVillageCoopMember(row.getIsVillageCoopMember());
        copy.setRemark(row.getRemark());
        copy.setGrantOrg(row.getGrantOrg());
        copy.setAccountName(row.getAccountName());
        copy.setRelationToInsured(row.getRelationToInsured());
        copy.setBankAccount(row.getBankAccount());
        copy.setSubsidyStandard(row.getSubsidyStandard());
        copy.setBenefitStartMonth(row.getBenefitStartMonth());
        copy.setBenefitMonths(row.getBenefitMonths());
        copy.setBenefitAmount(row.getBenefitAmount());
        copy.setPauseMonth(row.getPauseMonth());
        copy.setPauseReason(row.getPauseReason());
        copy.setPauseReasonRemark(row.getPauseReasonRemark());
        copy.setRecoverStartMonth(row.getRecoverStartMonth());
        copy.setRecoverEndMonth(row.getRecoverEndMonth());
        copy.setRecoverAmount(row.getRecoverAmount());
        return copy;
    }

    private void assertNoApprovedCancel(Long subsidyPersonId)
    {
        long count = personCancelService.count(new LambdaQueryWrapper<PersonCancel>()
                .eq(PersonCancel::getSubsidyPersonId, subsidyPersonId)
                .eq(PersonCancel::getDelFlag, "0")
                .eq(PersonCancel::getApprovalStatus, "approved"));
        if (count > 0)
        {
            throw new ServiceException("该人员已存在注销登记记录");
        }
    }

    private void createApprovedPersonCancel(Long subsidyPersonId, ValidatedRow validated)
    {
        String operator = SecurityUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();
        PersonCancel pc = new PersonCancel();
        pc.setSubsidyPersonId(subsidyPersonId);
        pc.setDeathDate(validated.cancelTime);
        pc.setCancelReason(validated.cancelReason);
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
        personCancelService.applyApprovedCancelToPerson(person, validated.cancelTime, validated.cancelReason);
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

    private LocalDate parseOptionalDate(String value, String label)
    {
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        String trimmed = value.trim();
        // Excel 日期单元格可能被读成序列号字符串
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

    /**
     * 兼容 Excel 导入：文本 yyyy-MM、日期单元格转换结果、未设格式的日期序列号等。
     */
    private String normalizeYearMonthText(String value)
    {
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

    private String parseOptionalMaxLength(String value, String label, int maxLength)
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

    private LocalDate parseBirthdayFromIdCard(String idCardNo)
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

    /** 到龄年月：年满60周岁所在年月，与待遇核定登记模块一致 */
    private YearMonth computeEligibleYearMonthFromIdCard(String idCardNo)
    {
        return YearMonth.from(parseBirthdayFromIdCard(idCardNo).plusYears(60));
    }

    private String parseGenderFromIdCard(String idCardNo)
    {
        int genderCode = Integer.parseInt(idCardNo.substring(16, 17));
        return genderCode % 2 == 0 ? "2" : "1";
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

    private String trim(String value)
    {
        return value == null ? null : value.trim();
    }

    private static class ValidatedRow
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
        private YearMonth recoverStartMonth;
        private YearMonth recoverEndMonth;
        private BigDecimal recoverAmount;
        private int recoverMonths;
    }
}
