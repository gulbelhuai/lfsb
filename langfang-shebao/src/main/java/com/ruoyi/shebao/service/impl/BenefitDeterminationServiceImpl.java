package com.ruoyi.shebao.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.shebao.domain.BenefitAttachment;
import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.domain.BenefitNoticeDetail;
import com.ruoyi.shebao.domain.DemolitionResident;
import com.ruoyi.shebao.domain.ExpropriateeSubsidy;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.BenefitDeterminationDetailResp;
import com.ruoyi.shebao.dto.BenefitDeterminationImportDto;
import com.ruoyi.shebao.dto.BenefitDeterminationListReq;
import com.ruoyi.shebao.dto.BenefitDeterminationListResp;
import com.ruoyi.shebao.dto.BenefitDeterminationPrintDto;
import com.ruoyi.shebao.mapper.BenefitAttachmentMapper;
import com.ruoyi.shebao.mapper.BenefitNoticeBatchMapper;
import com.ruoyi.shebao.mapper.BenefitDeterminationMapper;
import com.ruoyi.shebao.mapper.BenefitNoticeDetailMapper;
import com.ruoyi.shebao.mapper.DemolitionResidentMapper;
import com.ruoyi.shebao.mapper.ExpropriateeSubsidyMapper;
import com.ruoyi.shebao.mapper.LandLossResidentMapper;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import com.ruoyi.shebao.enums.SubsidyTypeEnum;
import com.ruoyi.shebao.service.IBenefitDeterminationService;
import com.ruoyi.shebao.util.ZipPreviewUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

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
    private final BenefitNoticeBatchMapper benefitNoticeBatchMapper;
    private final BenefitNoticeDetailMapper benefitNoticeDetailMapper;
    private final BenefitAttachmentMapper benefitAttachmentMapper;

    /**
     * 查询待遇核定列表
     *
     * @param req 查询条件
     * @return 待遇核定列表
     */
    @Override
    public Page<BenefitDeterminationListResp> selectBenefitDeterminationList(BenefitDeterminationListReq req)
    {
        Page<BenefitDeterminationListResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return this.baseMapper.selectBenefitDeterminationPage(page, req);
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

    @Override
    public BenefitDeterminationDetailResp selectBenefitDeterminationDetail(Long id)
    {
        BenefitDeterminationDetailResp detail = this.baseMapper.selectBenefitDeterminationDetail(id);
        Assert.notNull(detail, "待遇核定记录不存在");
        BenefitDetermination entity = this.getById(id);
        if (entity != null && StringUtils.isNotBlank(entity.getMaterialImagePaths()))
        {
            detail.setMaterialImagePaths(Arrays.stream(entity.getMaterialImagePaths().split(","))
                    .filter(StringUtils::isNotBlank)
                    .toList());
            detail.setMaterialZipPath(entity.getMaterialZipPath());
        }
        return detail;
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
        benefitDetermination.setPaymentPlanGenerated("0");
        if (StringUtils.isBlank(benefitDetermination.getMaterialStatus()))
        {
            benefitDetermination.setMaterialStatus("pending_upload");
        }
        benefitDetermination.setCreateBy(SecurityUtils.getUsername());
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
        benefitDetermination.setUpdateBy(SecurityUtils.getUsername());
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
        BenefitDetermination determination = this.getById(id);
        Assert.notNull(determination, "待遇核定记录不存在");
        if (StringUtils.isBlank(determination.getIdCardNo()))
        {
            throw new ServiceException("身份证号不能为空");
        }
        if (StringUtils.isBlank(determination.getBankName()) || StringUtils.isBlank(determination.getBankAccount()))
        {
            throw new ServiceException("银行名称和银行卡号不能为空");
        }
        if (!"uploaded".equals(determination.getMaterialStatus()) && !"verified".equals(determination.getMaterialStatus()))
        {
            throw new ServiceException("请先上传并解压证明材料ZIP");
        }
        int rows = this.lambdaUpdate()
                .eq(BenefitDetermination::getId, id)
                .set(BenefitDetermination::getApprovalStatus, "pending_review")
                .set(BenefitDetermination::getSubmitBy, SecurityUtils.getUsername())
                .set(BenefitDetermination::getSubmitTime, new Date())
                .set(BenefitDetermination::getUpdateBy, SecurityUtils.getUsername())
                .set(BenefitDetermination::getUpdateTime, new Date())
                .update() ? 1 : 0;
        syncNoticeDetailStatus(determination.getNoticeDetailId(), "pending_review", determination.getMaterialStatus(), "pending_review");
        refreshBatchStatistics(determination.getNoticeBatchNo());
        return rows;
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
        BenefitDetermination determination = this.getById(id);
        Assert.notNull(determination, "待遇核定记录不存在");
        int rows = this.lambdaUpdate()
                .eq(BenefitDetermination::getId, id)
                .set(BenefitDetermination::getApprovalStatus, "approved")
                .set(BenefitDetermination::getReviewBy, SecurityUtils.getUsername())
                .set(BenefitDetermination::getReviewTime, new Date())
                .set(BenefitDetermination::getReviewRemark, remark)
                .set(BenefitDetermination::getUpdateBy, SecurityUtils.getUsername())
                .set(BenefitDetermination::getUpdateTime, new Date())
                .update() ? 1 : 0;
        syncNoticeDetailStatus(determination.getNoticeDetailId(), "approved", determination.getMaterialStatus(), "approved");
        refreshBatchStatistics(determination.getNoticeBatchNo());
        return rows;
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
        BenefitDetermination determination = this.getById(id);
        Assert.notNull(determination, "待遇核定记录不存在");
        int rows = this.lambdaUpdate()
                .eq(BenefitDetermination::getId, id)
                .set(BenefitDetermination::getApprovalStatus, "rejected")
                .set(BenefitDetermination::getReviewBy, SecurityUtils.getUsername())
                .set(BenefitDetermination::getReviewTime, new Date())
                .set(BenefitDetermination::getReviewRemark, reason)
                .set(BenefitDetermination::getRemark, reason)
                .set(BenefitDetermination::getUpdateBy, SecurityUtils.getUsername())
                .set(BenefitDetermination::getUpdateTime, new Date())
                .update() ? 1 : 0;
        syncNoticeDetailStatus(determination.getNoticeDetailId(), "rejected", determination.getMaterialStatus(), "rejected");
        refreshBatchStatistics(determination.getNoticeBatchNo());
        return rows;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchImport(String noticeBatchNo, List<BenefitDeterminationImportDto> rows, MultipartFile[] attachmentFiles) throws Exception
    {
        if (CollectionUtils.isEmpty(rows))
        {
            return 0;
        }
        Map<Long, MultipartFile> attachmentMap = buildAttachmentMap(attachmentFiles);
        int success = 0;
        for (BenefitDeterminationImportDto row : rows)
        {
            String batchNo = StringUtils.isNotBlank(row.getNoticeBatchNo()) ? row.getNoticeBatchNo() : noticeBatchNo;
            if (StringUtils.isBlank(batchNo))
            {
                throw new ServiceException("导入数据缺少通知批次号");
            }
            if (row.getSubsidyPersonId() == null)
            {
                throw new ServiceException("导入数据缺少用户ID");
            }
            BenefitDetermination determination = this.lambdaQuery()
                    .eq(BenefitDetermination::getNoticeBatchNo, batchNo)
                    .eq(BenefitDetermination::getSubsidyPersonId, row.getSubsidyPersonId())
                    .eq(BenefitDetermination::getDelFlag, "0")
                    .last("limit 1")
                    .one();
            if (determination == null)
            {
                throw new ServiceException("未找到批次[" + batchNo + "]下用户ID[" + row.getSubsidyPersonId() + "]的核定草稿");
            }
            determination.setIdCardNo(row.getIdCardNo());
            determination.setBankName(row.getBankName());
            determination.setBankAccount(row.getBankAccount());
            determination.setBankAccountName(row.getBankAccountName());
            if (StringUtils.isNotBlank(row.getSubsidyStandard()))
            {
                determination.setSubsidyStandard(new BigDecimal(row.getSubsidyStandard()));
            }
            if (StringUtils.isNotBlank(row.getStartMonth()))
            {
                String[] parts = row.getStartMonth().split("-");
                if (parts.length != 2)
                {
                    throw new ServiceException("享受开始年月格式错误，应为yyyy-MM");
                }
                determination.setBenefitStartYear(Integer.parseInt(parts[0]));
                determination.setBenefitStartMonth(Integer.parseInt(parts[1]));
            }
            this.updateBenefitDetermination(determination);
            MultipartFile attachment = attachmentMap.get(row.getSubsidyPersonId());
            if (attachment != null)
            {
                uploadAttachment(determination.getId(), attachment);
            }
            submitForReview(determination.getId());
            success++;
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BenefitAttachment uploadAttachment(Long determinationId, MultipartFile file) throws Exception
    {
        BenefitDetermination determination = this.getById(determinationId);
        Assert.notNull(determination, "待遇核定记录不存在");
        String batchNo = determination.getNoticeBatchNo();
        Long userId = determination.getSubsidyPersonId();
        if (userId == null)
        {
            throw new ServiceException("待遇核定记录缺少用户ID");
        }
        String expectedFileName = userId + ".zip";
        if (file.getOriginalFilename() == null || !expectedFileName.equalsIgnoreCase(file.getOriginalFilename()))
        {
            throw new ServiceException("材料ZIP文件名必须为“" + expectedFileName + "”");
        }
        String uploadDir = RuoYiConfig.getProfile() + "/benefit/determination/" + batchNo + "/zip";
        String zipPath = FileUploadUtils.upload(uploadDir, file);
        Path profileDir = Path.of(RuoYiConfig.getProfile());
        Path extractDir = profileDir.resolve("benefit/determination/" + batchNo + "/" + userId + "/images");
        ZipPreviewUtils.deleteDirectory(extractDir);
        List<String> imagePaths = ZipPreviewUtils.extractImages(profileDir.resolve(stripProfilePrefix(zipPath)), extractDir, profileDir);
        if (imagePaths.isEmpty())
        {
            throw new ServiceException("ZIP 中未发现可预览图片");
        }

        BenefitAttachment attachment = benefitAttachmentMapper.selectOne(new LambdaQueryWrapper<BenefitAttachment>()
                .eq(BenefitAttachment::getBusinessType, "benefit_determination")
                .eq(BenefitAttachment::getBusinessId, determinationId)
                .eq(BenefitAttachment::getDelFlag, "0")
                .last("limit 1"));
        if (attachment == null)
        {
            attachment = new BenefitAttachment();
            attachment.setBusinessType("benefit_determination");
            attachment.setBusinessId(determinationId);
            attachment.setNoticeBatchNo(batchNo);
            attachment.setSubsidyPersonId(userId);
            attachment.setCreateBy(SecurityUtils.getUsername());
        }
        attachment.setOriginalFileName(file.getOriginalFilename());
        attachment.setZipFilePath(zipPath);
        attachment.setExtractDir("/profile/" + profileDir.relativize(extractDir).toString().replace("\\", "/"));
        attachment.setPreviewImagePaths(String.join(",", imagePaths));
        attachment.setUpdateBy(SecurityUtils.getUsername());
        if (attachment.getId() == null)
        {
            benefitAttachmentMapper.insert(attachment);
        }
        else
        {
            benefitAttachmentMapper.updateById(attachment);
        }

        this.lambdaUpdate()
                .eq(BenefitDetermination::getId, determinationId)
                .set(BenefitDetermination::getMaterialZipPath, zipPath)
                .set(BenefitDetermination::getMaterialExtractDir, attachment.getExtractDir())
                .set(BenefitDetermination::getMaterialImagePaths, attachment.getPreviewImagePaths())
                .set(BenefitDetermination::getMaterialStatus, "uploaded")
                .set(BenefitDetermination::getUpdateBy, SecurityUtils.getUsername())
                .set(BenefitDetermination::getUpdateTime, new Date())
                .update();
        syncNoticeDetailStatus(determination.getNoticeDetailId(), null, "uploaded", null);
        return attachment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchApprove(List<Long> ids, String noticeBatchNo, String remark)
    {
        List<BenefitDetermination> list = this.lambdaQuery()
                .eq(StringUtils.isNotBlank(noticeBatchNo), BenefitDetermination::getNoticeBatchNo, noticeBatchNo)
                .in(!CollectionUtils.isEmpty(ids), BenefitDetermination::getId, ids)
                .eq(BenefitDetermination::getApprovalStatus, "pending_review")
                .eq(BenefitDetermination::getDelFlag, "0")
                .list();
        int success = 0;
        for (BenefitDetermination determination : list)
        {
            success += approveReview(determination.getId(), remark);
        }
        return success;
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
            case "teacher_subsidy" -> SubsidyTypeEnum.TEACHER;
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

    private void syncNoticeDetailStatus(Long noticeDetailId, String determinationStatus, String materialStatus, String reviewStatus)
    {
        if (noticeDetailId == null)
        {
            return;
        }
        LambdaUpdateWrapper<BenefitNoticeDetail> updateWrapper = new LambdaUpdateWrapper<BenefitNoticeDetail>()
                .eq(BenefitNoticeDetail::getId, noticeDetailId);
        if (StringUtils.isNotBlank(determinationStatus))
        {
            updateWrapper.set(BenefitNoticeDetail::getDeterminationStatus, determinationStatus);
        }
        if (StringUtils.isNotBlank(materialStatus))
        {
            updateWrapper.set(BenefitNoticeDetail::getMaterialStatus, materialStatus);
        }
        if (StringUtils.isNotBlank(reviewStatus))
        {
            updateWrapper.set(BenefitNoticeDetail::getReviewStatus, reviewStatus);
        }
        updateWrapper.set(BenefitNoticeDetail::getUpdateBy, SecurityUtils.getUsername())
                .set(BenefitNoticeDetail::getUpdateTime, java.time.LocalDateTime.now());
        benefitNoticeDetailMapper.update(null, updateWrapper);
    }

    private void refreshBatchStatistics(String batchNo)
    {
        if (StringUtils.isBlank(batchNo))
        {
            return;
        }
        int total = countDetails(batchNo, null);
        int pendingReview = countDetails(batchNo, "pending_review");
        int approved = countDetails(batchNo, "approved");
        int rejected = countDetails(batchNo, "rejected");
        String status = "generated";
        if (approved + rejected > 0)
        {
            status = approved + rejected >= total ? "completed" : "processing";
        }
        benefitNoticeBatchMapper.update(null, new LambdaUpdateWrapper<com.ruoyi.shebao.domain.BenefitNoticeBatch>()
                .eq(com.ruoyi.shebao.domain.BenefitNoticeBatch::getBatchNo, batchNo)
                .set(com.ruoyi.shebao.domain.BenefitNoticeBatch::getTotalCount, total)
                .set(com.ruoyi.shebao.domain.BenefitNoticeBatch::getPendingReviewCount, pendingReview)
                .set(com.ruoyi.shebao.domain.BenefitNoticeBatch::getApprovedCount, approved)
                .set(com.ruoyi.shebao.domain.BenefitNoticeBatch::getRejectedCount, rejected)
                .set(com.ruoyi.shebao.domain.BenefitNoticeBatch::getBatchStatus, status)
                .set(com.ruoyi.shebao.domain.BenefitNoticeBatch::getUpdateBy, SecurityUtils.getUsername())
                .set(com.ruoyi.shebao.domain.BenefitNoticeBatch::getUpdateTime, java.time.LocalDateTime.now()));
    }

    private int countDetails(String batchNo, String determinationStatus)
    {
        LambdaQueryWrapper<BenefitNoticeDetail> wrapper = new LambdaQueryWrapper<BenefitNoticeDetail>()
                .eq(BenefitNoticeDetail::getBatchNo, batchNo)
                .eq(BenefitNoticeDetail::getDelFlag, "0");
        if (StringUtils.isNotBlank(determinationStatus))
        {
            wrapper.eq(BenefitNoticeDetail::getDeterminationStatus, determinationStatus);
        }
        return Math.toIntExact(benefitNoticeDetailMapper.selectCount(wrapper));
    }

    private Map<Long, MultipartFile> buildAttachmentMap(MultipartFile[] attachmentFiles)
    {
        Map<Long, MultipartFile> map = new HashMap<>();
        if (attachmentFiles == null)
        {
            return map;
        }
        for (MultipartFile attachmentFile : attachmentFiles)
        {
            if (attachmentFile == null || StringUtils.isBlank(attachmentFile.getOriginalFilename()))
            {
                continue;
            }
            String name = attachmentFile.getOriginalFilename();
            int index = Objects.requireNonNull(name).lastIndexOf('.');
            if (index <= 0)
            {
                continue;
            }
            try
            {
                Long userId = Long.parseLong(name.substring(0, index));
                map.put(userId, attachmentFile);
            }
            catch (NumberFormatException ignored)
            {
            }
        }
        return map;
    }

    private String stripProfilePrefix(String resourcePath)
    {
        return resourcePath.replaceFirst("^/profile/", "");
    }
}
