package com.ruoyi.shebao.service.historicalimport;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.shebao.constant.SubsidyApprovalStatus;
import com.ruoyi.shebao.domain.DemolitionResident;
import com.ruoyi.shebao.domain.HistoricalImportBatch;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.DemolitionResidentFormDto;
import com.ruoyi.shebao.dto.historicalimport.DemolitionHistoricalImportDto;
import com.ruoyi.shebao.dto.historicalimport.HistoricalImportResult;
import com.ruoyi.shebao.enums.HistoricalImportSubsidyType;
import com.ruoyi.shebao.mapper.DemolitionResidentMapper;
import com.ruoyi.shebao.mapper.HistoricalImportBatchMapper;
import com.ruoyi.shebao.service.SubsidyPersonService;
import com.ruoyi.shebao.service.support.SubsidyPersonRegistrationHelper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DemolitionHistoricalImportHandler implements HistoricalImportHandler
{
    private final HistoricalImportBatchMapper historicalImportBatchMapper;
    private final SubsidyPersonService subsidyPersonService;
    private final SubsidyPersonRegistrationHelper subsidyPersonRegistrationHelper;
    private final DemolitionResidentMapper demolitionResidentMapper;
    private final PlatformTransactionManager transactionManager;
    private final HistoricalImportTemplateExporter historicalImportTemplateExporter;
    private final HistoricalImportCommonSupport common;

    @Override
    public String subsidyType()
    {
        return HistoricalImportSubsidyType.DEMOLITION_RESIDENT.getCode();
    }

    @Override
    public void exportTemplate(HttpServletResponse response) throws Exception
    {
        historicalImportTemplateExporter.exportDemolitionTemplate(response);
    }

    @Override
    public List<?> parseRows(MultipartFile file) throws Exception
    {
        ExcelUtil<DemolitionHistoricalImportDto> util = new ExcelUtil<>(DemolitionHistoricalImportDto.class);
        List<DemolitionHistoricalImportDto> rows = util.importExcel(file.getInputStream());
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
        List<DemolitionHistoricalImportDto> importRows = (List<DemolitionHistoricalImportDto>) rows;
        if (CollectionUtils.isEmpty(importRows))
        {
            throw new ServiceException("导入文件无有效数据行");
        }

        List<DemolitionHistoricalImportDto> failedRows = new ArrayList<>();
        int successCount = 0;
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        for (DemolitionHistoricalImportDto row : importRows)
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
                DemolitionHistoricalImportDto failed = copyRow(row);
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

    private void importSingleRow(DemolitionHistoricalImportDto row)
    {
        HistoricalImportCommonSupport.ValidatedContext ctx = validateRow(row);
        DemolitionResidentFormDto formDto = toFormDto(row, ctx);

        SubsidyPerson existing = subsidyPersonService.selectSubsidyPersonByIdCardNo(formDto.getIdCardNo());
        if (existing != null)
        {
            long duplicate = demolitionResidentMapper.selectCount(new LambdaQueryWrapper<DemolitionResident>()
                    .eq(DemolitionResident::getSubsidyPersonId, existing.getId())
                    .eq(DemolitionResident::getDelFlag, "0"));
            if (duplicate > 0)
            {
                throw new ServiceException("该人员已存在拆迁居民登记记录");
            }
        }

        Long subsidyPersonId = subsidyPersonRegistrationHelper.resolveSubsidyPersonForHistoricalImport(formDto, person -> {
            person.setPersonStatus(ctx.getPersonStatus());
            person.setSubsidyStatus(ctx.getSubsidyStatus());
            person.setIsVillageCoopMember(ctx.getIsVillageCoopMember());
        });

        DemolitionResident resident = new DemolitionResident();
        resident.setSubsidyPersonId(subsidyPersonId);
        resident.setDemolitionReason(ctx.getDemolitionReason());
        resident.setDemolitionTime(ctx.getDemolitionTime());
        resident.setRecognitionTime(ctx.getRecognitionTime());
        resident.setVillageStreet(common.trim(row.getVillageStreet()));
        resident.setRemark(common.trim(row.getRemark()));
        resident.setApprovalStatus(SubsidyApprovalStatus.APPROVED);
        resident.setCreateBy(SecurityUtils.getUsername());
        resident.setCreateTime(LocalDateTime.now());
        demolitionResidentMapper.insert(resident);

        if (ctx.isHasBenefitBlock())
        {
            Long determinationId = common.upsertBenefitDetermination(
                    subsidyPersonId, row.getIdCardNo(), ctx, "demolition",
                    row.getVillageStreet(), ctx.getDemolitionTime(),
                    "该人员已存在拆迁待遇核定明细");
            if (ctx.isHasPauseBlock())
            {
                common.createHistoricalSuspension(determinationId, subsidyPersonId, row.getIdCardNo(), "demolition", ctx);
            }
        }

        common.applyPersonCancelIfNeeded(subsidyPersonId, ctx);
    }

    private HistoricalImportCommonSupport.ValidatedContext validateRow(DemolitionHistoricalImportDto row)
    {
        String idCard = common.validateAndNormalizeIdCard(row.getIdCardNo());
        row.setIdCardNo(idCard);

        HistoricalImportCommonSupport.ValidatedContext ctx = new HistoricalImportCommonSupport.ValidatedContext();
        common.validateCommonPerson(row, ctx, idCard);
        ctx.setDemolitionReason(common.parseOptionalMaxLength(
                row.getDemolitionReason(), "拆迁事由", HistoricalImportCommonSupport.DEMOLITION_REASON_MAX_LENGTH));
        ctx.setDemolitionTime(common.parseOptionalDate(row.getDemolitionTime(), "拆迁时间"));
        ctx.setRecognitionTime(common.parseOptionalDate(row.getRecognitionTime(), "认定时间"));
        common.validateBenefitAndPause(row, ctx, idCard);
        return ctx;
    }

    private DemolitionResidentFormDto toFormDto(DemolitionHistoricalImportDto row,
                                                  HistoricalImportCommonSupport.ValidatedContext ctx)
    {
        DemolitionResidentFormDto dto = new DemolitionResidentFormDto();
        dto.setName(common.trim(row.getName()));
        dto.setIdCardNo(row.getIdCardNo());
        dto.setGender(common.parseGenderFromIdCard(row.getIdCardNo()));
        dto.setBirthday(common.parseBirthdayFromIdCard(row.getIdCardNo()));
        dto.setHouseholdRegistration(common.trim(row.getHouseholdRegistration()));
        dto.setHomeAddress(common.trim(row.getHomeAddress()));
        dto.setPhone(common.trim(row.getPhone()));
        if (ctx.getStreetOffice() != null)
        {
            dto.setStreetOfficeId(ctx.getStreetOffice().getId());
        }
        if (ctx.getVillageCommittee() != null)
        {
            dto.setVillageCommitteeId(ctx.getVillageCommittee().getId());
        }
        dto.setDemolitionReason(ctx.getDemolitionReason());
        dto.setDemolitionTime(ctx.getDemolitionTime());
        dto.setRecognitionTime(ctx.getRecognitionTime());
        dto.setVillageStreet(common.trim(row.getVillageStreet()));
        dto.setIsVillageCoopMember(ctx.getIsVillageCoopMember());
        dto.setRemark(common.trim(row.getRemark()));
        return dto;
    }

    private boolean isNotBlankRow(DemolitionHistoricalImportDto row)
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

    private String saveFailureFile(List<DemolitionHistoricalImportDto> failedRows)
    {
        try
        {
            return historicalImportTemplateExporter.exportDemolitionFailureFile(failedRows);
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

    private DemolitionHistoricalImportDto copyRow(DemolitionHistoricalImportDto row)
    {
        DemolitionHistoricalImportDto copy = new DemolitionHistoricalImportDto();
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
        copy.setDemolitionReason(row.getDemolitionReason());
        copy.setDemolitionTime(row.getDemolitionTime());
        copy.setRecognitionTime(row.getRecognitionTime());
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
}
