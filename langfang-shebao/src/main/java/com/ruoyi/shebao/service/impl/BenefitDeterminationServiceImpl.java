package com.ruoyi.shebao.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.shebao.domain.BenefitAttachment;
import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.domain.DemolitionResident;
import com.ruoyi.shebao.domain.ExpropriateeSubsidy;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.domain.BenefitDeterminationItem;
import com.ruoyi.shebao.domain.StreetOffice;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.domain.VillageOfficial;
import com.ruoyi.shebao.dto.BenefitDeterminationDetailResp;
import com.ruoyi.shebao.dto.BenefitDeterminationImportDto;
import com.ruoyi.shebao.dto.BenefitDeterminationListReq;
import com.ruoyi.shebao.dto.BenefitDeterminationListResp;
import com.ruoyi.shebao.dto.BenefitDeterminationPrintDto;
import com.ruoyi.shebao.dto.BenefitDeterminationPrepareResp;
import com.ruoyi.shebao.dto.BenefitDeterminationSaveDraftReq;
import com.ruoyi.shebao.mapper.BenefitAttachmentMapper;
import com.ruoyi.shebao.mapper.BenefitDeterminationMapper;
import com.ruoyi.shebao.mapper.BenefitDeterminationItemMapper;
import com.ruoyi.shebao.mapper.DemolitionResidentMapper;
import com.ruoyi.shebao.mapper.ExpropriateeSubsidyMapper;
import com.ruoyi.shebao.mapper.LandLossResidentMapper;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import com.ruoyi.shebao.mapper.StreetOfficeMapper;
import com.ruoyi.shebao.mapper.VillageCommitteeMapper;
import com.ruoyi.shebao.mapper.VillageOfficialMapper;
import com.ruoyi.shebao.service.IBenefitDeterminationService;
import com.ruoyi.shebao.util.ZipPreviewUtils;
import com.ruoyi.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    private final BenefitAttachmentMapper benefitAttachmentMapper;
    private final BenefitDeterminationItemMapper benefitDeterminationItemMapper;
    private final StreetOfficeMapper streetOfficeMapper;
    private final VillageCommitteeMapper villageCommitteeMapper;
    private final VillageOfficialMapper villageOfficialMapper;
    private final ISysConfigService sysConfigService;

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
        SubsidyPerson person = subsidyPersonMapper.selectById(detail.getSubsidyPersonId());
        if (person != null)
        {
            detail.setGender(person.getGender());
            detail.setBirthday(person.getBirthday());
            detail.setHouseholdRegistration(person.getHouseholdRegistration());
            detail.setHomeAddress(person.getHomeAddress());
            detail.setPhone(person.getPhone());
            detail.setSubsidyStatus(person.getSubsidyStatus());
            detail.setPersonStatus(person.getPersonStatus());
            if (person.getStreetOfficeId() != null)
            {
                StreetOffice streetOffice = streetOfficeMapper.selectById(person.getStreetOfficeId());
                detail.setStreetOfficeName(streetOffice == null ? null : streetOffice.getStreetName());
            }
            if (person.getVillageCommitteeId() != null)
            {
                VillageCommittee villageCommittee = villageCommitteeMapper.selectById(person.getVillageCommitteeId());
                detail.setVillageCommitteeName(villageCommittee == null ? null : villageCommittee.getVillageName());
            }
            List<ExpropriateeSubsidy> exList = expropriateeSubsidyMapper.selectBySubsidyPersonId(person.getId());
            if (!CollectionUtils.isEmpty(exList))
            {
                ExpropriateeSubsidy ex = exList.get(0);
                detail.setJoinUrbanRuralInsurance(ex.getJoinUrbanRuralInsurance());
                detail.setJoinEmployeePension(ex.getJoinEmployeePension());
                detail.setHasEmployeePension(ex.getHasEmployeePension());
                detail.setEmployeePensionMonths(ex.getEmployeePensionMonths());
                detail.setFlexibleEmploymentMonths(ex.getFlexibleEmploymentMonths());
                detail.setDifficultySubsidyMonths(ex.getDifficultySubsidyMonths());
            }
        }
        BenefitDetermination entity = this.getById(id);
        if (entity != null && StringUtils.isNotBlank(entity.getMaterialImagePaths()))
        {
            detail.setMaterialImagePaths(Arrays.stream(entity.getMaterialImagePaths().split(","))
                    .filter(StringUtils::isNotBlank)
                    .toList());
            detail.setMaterialZipPath(entity.getMaterialZipPath());
        }

        List<BenefitDeterminationItem> items = benefitDeterminationItemMapper.selectList(
                new LambdaQueryWrapper<BenefitDeterminationItem>()
                        .eq(BenefitDeterminationItem::getDeterminationId, id)
                        .eq(BenefitDeterminationItem::getDelFlag, "0")
                        .orderByAsc(BenefitDeterminationItem::getId)
        );
        List<BenefitDeterminationDetailResp.BenefitDeterminationItemResp> itemResps = items.stream().map(it -> {
            BenefitDeterminationDetailResp.BenefitDeterminationItemResp r = new BenefitDeterminationDetailResp.BenefitDeterminationItemResp();
            r.setId(it.getId());
            r.setSubsidyType(it.getSubsidyType());
            r.setVillageStreet(it.getVillageStreet());
            r.setEventDate(it.getEventDate());
            r.setSubsidyStandard(it.getSubsidyStandard());
            r.setBenefitStartYear(it.getBenefitStartYear());
            r.setBenefitStartMonth(it.getBenefitStartMonth());
            r.setBenefitMonths(it.getBenefitMonths());
            r.setBenefitAmount(it.getBenefitAmount());
            return r;
        }).toList();
        detail.setItems(itemResps);
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
        // 旧接口保留：仅用于兼容历史调用（新需求请使用 /draft）
        if (benefitDetermination.getSubsidyPersonId() == null)
        {
            throw new ServiceException("缺少被补贴人ID");
        }
        fillEligibleMonth(benefitDetermination);
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
        fillEligibleMonth(benefitDetermination);
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
        if (StringUtils.isBlank(determination.getBankAccount()))
        {
            throw new ServiceException("银行账号不能为空");
        }
        if (StringUtils.isBlank(determination.getGrantOrg()))
        {
            throw new ServiceException("发放机构不能为空");
        }
        if (StringUtils.isBlank(determination.getAccountName()))
        {
            throw new ServiceException("开户名不能为空");
        }
        if (StringUtils.isBlank(determination.getRelationToInsured()))
        {
            throw new ServiceException("与参保人关系不能为空");
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
        return rows;
    }

    @Override
    public List<BenefitDeterminationPrintDto> buildPrintDto(Long id)
    {
        BenefitDetermination determination = this.getById(id);
        Assert.notNull(determination, "待遇核定记录不存在");

        SubsidyPerson person = subsidyPersonMapper.selectById(determination.getSubsidyPersonId());
        Assert.notNull(person, "被补贴人不存在");

        List<BenefitDeterminationItem> items = benefitDeterminationItemMapper.selectList(
                new LambdaQueryWrapper<BenefitDeterminationItem>()
                        .eq(BenefitDeterminationItem::getDeterminationId, id)
                        .eq(BenefitDeterminationItem::getDelFlag, "0")
                        .orderByAsc(BenefitDeterminationItem::getId)
        );
        if (items.isEmpty())
        {
            BenefitDeterminationPrintDto dto = BeanUtil.copyProperties(determination, BenefitDeterminationPrintDto.class);
            dto.setName(person.getName());
            dto.setIdCardNo(person.getIdCardNo());
            dto.setUserCode(person.getUserCode());
            dto.setBankName(DictUtils.getDictLabel("shebao_grant_org", determination.getGrantOrg()));
            return List.of(dto);
        }
        return items.stream().map(it -> {
            BenefitDeterminationPrintDto dto = new BenefitDeterminationPrintDto();
            dto.setUserCode(person.getUserCode());
            dto.setName(person.getName());
            dto.setIdCardNo(person.getIdCardNo());
            dto.setSubsidyType(it.getSubsidyType());
            dto.setEligibleMonth(yearMonthToDate(determination.getEligibleYear(), determination.getEligibleMonth()));
            dto.setBenefitStartMonth(yearMonthToDate(it.getBenefitStartYear(), it.getBenefitStartMonth()));
            dto.setBankName(DictUtils.getDictLabel("shebao_grant_org", determination.getGrantOrg()));
            dto.setBankAccount(determination.getBankAccount());
            dto.setSubsidyStandard(it.getSubsidyStandard());
            dto.setBenefitMonths(it.getBenefitMonths());
            dto.setBenefitAmount(it.getBenefitAmount());
            dto.setCreateTime(determination.getCreateTime());
            return dto;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchImport(List<BenefitDeterminationImportDto> rows, MultipartFile[] attachmentFiles) throws Exception
    {
        throw new ServiceException("待遇核定已调整为逐个录入，批量导入功能已取消");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BenefitAttachment uploadAttachment(Long determinationId, MultipartFile file) throws Exception
    {
        BenefitDetermination determination = this.getById(determinationId);
        Assert.notNull(determination, "待遇核定记录不存在");
        Long userId = determination.getSubsidyPersonId();
        if (userId == null)
        {
            throw new ServiceException("待遇核定记录缺少用户ID");
        }
        String originalFileName = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFileName) || !StringUtils.endsWithIgnoreCase(originalFileName, ".zip"))
        {
            throw new ServiceException("材料文件格式必须为zip");
        }
        String uploadDir = RuoYiConfig.getProfile() + "/benefit/determination/" + determinationId + "/zip";
        String zipPath = FileUploadUtils.upload(uploadDir, file);
        Path profileDir = Path.of(RuoYiConfig.getProfile());
        Path extractDir = profileDir.resolve("benefit/determination/" + determinationId + "/" + userId + "/images");
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
            attachment.setSubsidyPersonId(userId);
            attachment.setCreateBy(SecurityUtils.getUsername());
        }
        attachment.setOriginalFileName(originalFileName);
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
        return attachment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchApprove(List<Long> ids, String remark)
    {
        List<BenefitDetermination> list = this.lambdaQuery()
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
     * 自动生成到龄年月（按生日+60周岁当月）
     */
    private void fillEligibleMonth(BenefitDetermination benefitDetermination)
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
    }

    private void validateSubsidyReviewStatus(SubsidyPerson person, List<LandLossResident> landList,
                                             List<DemolitionResident> demoList, List<VillageOfficial> voList)
    {
        if (person == null)
        {
            return;
        }
        if ("approved".equalsIgnoreCase(StringUtils.trimToEmpty(person.getApprovalStatus())))
        {
            return;
        }
        List<String> pendingTypes = new ArrayList<>();
        if (!CollectionUtils.isEmpty(landList))
        {
            pendingTypes.add("失地");
        }
        if (!CollectionUtils.isEmpty(demoList))
        {
            pendingTypes.add("拆迁");
        }
        if (!CollectionUtils.isEmpty(voList))
        {
            pendingTypes.add("村干部");
        }
        if (!pendingTypes.isEmpty())
        {
            String personName = person.getName() == null ? "" : person.getName();
            throw new ServiceException("人员" + personName + "的"
                    + String.join("、", pendingTypes) + "补贴尚未复核，请复核通过后再进行核定");
        }
    }

    @Override
    public BenefitDeterminationPrepareResp prepareByIdCardNo(String idCardNo)
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
            throw new ServiceException("未找到该身份证号的人员信息");
        }
        List<LandLossResident> landList = landLossResidentMapper.selectBySubsidyPersonId(person.getId());
        List<DemolitionResident> demoList = demolitionResidentMapper.selectBySubsidyPersonId(person.getId());
        List<VillageOfficial> voList = villageOfficialMapper.selectBySubsidyPersonId(person.getId());
        validateSubsidyReviewStatus(person, landList, demoList, voList);

        boolean existsDetermination = this.lambdaQuery()
                .eq(BenefitDetermination::getDelFlag, "0")
                .and(w -> w.eq(BenefitDetermination::getSubsidyPersonId, person.getId())
                        .or()
                        .eq(BenefitDetermination::getIdCardNo, idCardNo))
                .count() > 0;
        boolean approvedDetermination = this.lambdaQuery()
                .eq(BenefitDetermination::getSubsidyPersonId, person.getId())
                .eq(BenefitDetermination::getApprovalStatus, "approved")
                .eq(BenefitDetermination::getDelFlag, "0")
                .count() > 0;
        boolean personBenefitActive = StringUtils.isNotBlank(person.getPersonStatus())
                && !"0".equals(person.getPersonStatus().trim());

        BenefitDeterminationPrepareResp resp = new BenefitDeterminationPrepareResp();
        boolean already = existsDetermination || approvedDetermination || personBenefitActive;
        resp.setAlreadyDetermined(already);
        resp.setAlreadyDeterminedMsg(already
                ? (existsDetermination ? "已有待遇核定记录" : "人员待遇已核定")
                : null);

        BenefitDeterminationPrepareResp.PersonInfo personInfo = new BenefitDeterminationPrepareResp.PersonInfo();
        personInfo.setSubsidyPersonId(person.getId());
        personInfo.setName(person.getName());
        personInfo.setGender(person.getGender());
        personInfo.setBirthMonth(person.getBirthday() == null ? null : formatYm(java.time.YearMonth.from(person.getBirthday())));
        personInfo.setIdCardNo(person.getIdCardNo());
        if (person.getStreetOfficeId() != null)
        {
            StreetOffice so = streetOfficeMapper.selectById(person.getStreetOfficeId());
            personInfo.setStreetOfficeName(so != null ? so.getStreetName() : null);
        }
        if (person.getVillageCommitteeId() != null)
        {
            VillageCommittee vc = villageCommitteeMapper.selectById(person.getVillageCommitteeId());
            personInfo.setVillageCommitteeName(vc != null ? vc.getVillageName() : null);
        }
        personInfo.setHouseholdRegistration(person.getHouseholdRegistration());
        personInfo.setHomeAddress(person.getHomeAddress());
        personInfo.setPhone(person.getPhone());
        resp.setPerson(personInfo);

        BenefitDeterminationPrepareResp.SocialInsuranceInfo social = new BenefitDeterminationPrepareResp.SocialInsuranceInfo();
        social.setSubsidyStatus(person.getSubsidyStatus());
        social.setPersonStatus(person.getPersonStatus());
        List<ExpropriateeSubsidy> exList = expropriateeSubsidyMapper.selectBySubsidyPersonId(person.getId());
        if (!CollectionUtils.isEmpty(exList))
        {
            ExpropriateeSubsidy ex = exList.get(0);
            social.setJoinUrbanRuralInsurance(ex.getJoinUrbanRuralInsurance());
            social.setJoinEmployeePension(ex.getJoinEmployeePension());
            social.setHasEmployeePension(ex.getHasEmployeePension());
            social.setEmployeePensionMonths(ex.getEmployeePensionMonths());
            social.setFlexibleEmploymentMonths(ex.getFlexibleEmploymentMonths());
            social.setDifficultySubsidyMonths(ex.getDifficultySubsidyMonths());
        }
        resp.setSocialInsurance(social);

        YearMonth eligibleYm = computeEligibleYm(person);
        YearMonth eligibleNext = eligibleYm == null ? null : eligibleYm.plusMonths(1);

        List<BenefitDeterminationPrepareResp.SubsidyInfo> subsidies = new java.util.ArrayList<>();

        if (!CollectionUtils.isEmpty(landList))
        {
            LocalDate minEvent = landList.stream().map(LandLossResident::getLandRequisitionTime).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(null);
            String villageStreet = landList.stream().map(LandLossResident::getVillageStreet).filter(StringUtils::isNotBlank).findFirst().orElse(null);
            BenefitDeterminationPrepareResp.SubsidyInfo s = new BenefitDeterminationPrepareResp.SubsidyInfo();
            s.setSubsidyType("land_loss");
            s.setVillageStreet(villageStreet);
            s.setEventDate(minEvent);
            s.setSubsidyStandard(resolveStandard(person.getId(), "land_loss"));
            s.setDefaultStartMonth(formatYm(maxYm(eligibleNext, plusOneMonth(minEvent))));
            subsidies.add(s);
        }

        if (!CollectionUtils.isEmpty(demoList))
        {
            LocalDate minEvent = demoList.stream().map(DemolitionResident::getDemolitionTime).filter(Objects::nonNull).min(LocalDate::compareTo).orElse(null);
            String villageStreet = demoList.stream().map(DemolitionResident::getVillageStreet).filter(StringUtils::isNotBlank).findFirst().orElse(null);
            BenefitDeterminationPrepareResp.SubsidyInfo s = new BenefitDeterminationPrepareResp.SubsidyInfo();
            s.setSubsidyType("demolition");
            s.setVillageStreet(villageStreet);
            s.setEventDate(minEvent);
            s.setSubsidyStandard(resolveStandard(person.getId(), "demolition"));
            s.setDefaultStartMonth(formatYm(maxYm(eligibleNext, plusOneMonth(minEvent))));
            subsidies.add(s);
        }

        if (!CollectionUtils.isEmpty(voList))
        {
            BigDecimal maxStd = voList.stream().map(VillageOfficial::getSubsidyAmount).filter(Objects::nonNull).max(BigDecimal::compareTo).orElse(null);
            String villageStreet = voList.stream().map(VillageOfficial::getVillageStreet).filter(StringUtils::isNotBlank).findFirst().orElse(null);
            BenefitDeterminationPrepareResp.SubsidyInfo s = new BenefitDeterminationPrepareResp.SubsidyInfo();
            s.setSubsidyType("village_official");
            s.setVillageStreet(villageStreet);
            s.setSubsidyStandard(maxStd);
            s.setDefaultStartMonth(formatYm(eligibleNext));
            subsidies.add(s);
        }

        resp.setSubsidies(subsidies);
        return resp;
    }

    @Override
    @Transactional
    public Long saveDraft(BenefitDeterminationSaveDraftReq req)
    {
        Assert.notNull(req, "请求不能为空");
        SubsidyPerson person = subsidyPersonMapper.selectById(req.getSubsidyPersonId());
        Assert.notNull(person, "被补贴人不存在");

        for (BenefitDeterminationSaveDraftReq.Item it : req.getItems())
        {
            if (it == null || StringUtils.isBlank(it.getSubsidyType()))
            {
                throw new ServiceException("补贴类型不能为空");
            }
            if (!"land_loss".equals(it.getSubsidyType()) && !"demolition".equals(it.getSubsidyType()) && !"village_official".equals(it.getSubsidyType()))
            {
                throw new ServiceException("不支持的补贴类型：" + it.getSubsidyType());
            }
        }

        BenefitDetermination determination;
        if (req.getId() != null)
        {
            determination = this.getById(req.getId());
            Assert.notNull(determination, "待遇核定记录不存在");
            if (!Objects.equals(determination.getSubsidyPersonId(), req.getSubsidyPersonId()))
            {
                throw new ServiceException("人员不匹配，无法修改");
            }
            if (!"draft".equals(determination.getApprovalStatus()) && !"rejected".equals(determination.getApprovalStatus()))
            {
                throw new ServiceException("仅草稿/驳回状态允许修改");
            }
        }
        else
        {
            long existed = this.lambdaQuery()
                    .eq(BenefitDetermination::getDelFlag, "0")
                    .and(w -> w.eq(BenefitDetermination::getSubsidyPersonId, req.getSubsidyPersonId())
                            .or()
                            .eq(BenefitDetermination::getIdCardNo, req.getIdCardNo()))
                    .count();
            if (existed > 0)
            {
                throw new ServiceException("已有待遇核定记录");
            }
            determination = new BenefitDetermination();
            determination.setApprovalStatus("draft");
            determination.setDelFlag("0");
            determination.setPaymentPlanGenerated("0");
            determination.setMaterialStatus("pending_upload");
            determination.setCreateBy(SecurityUtils.getUsername());
            determination.setCreateTime(new Date());
        }

        determination.setSubsidyPersonId(req.getSubsidyPersonId());
        determination.setIdCardNo(req.getIdCardNo());
        determination.setGrantOrg(req.getGrantOrg());
        determination.setAccountName(req.getAccountName());
        determination.setRelationToInsured(StringUtils.isNotBlank(req.getRelationToInsured()) ? req.getRelationToInsured() : "本人");
        determination.setBankAccount(req.getBankAccount());
        determination.setGrantRemark(req.getGrantRemark());
        fillEligibleMonth(determination);
        determination.setUpdateBy(SecurityUtils.getUsername());
        determination.setUpdateTime(new Date());

        if (determination.getId() == null)
        {
            this.save(determination);
        }
        else
        {
            this.updateById(determination);
        }

        // 先软删旧明细，再插入新明细
        benefitDeterminationItemMapper.update(null, new LambdaUpdateWrapper<BenefitDeterminationItem>()
                .eq(BenefitDeterminationItem::getDeterminationId, determination.getId())
                .eq(BenefitDeterminationItem::getDelFlag, "0")
                .set(BenefitDeterminationItem::getDelFlag, "2")
                .set(BenefitDeterminationItem::getUpdateBy, SecurityUtils.getUsername())
                .set(BenefitDeterminationItem::getUpdateTime, java.time.LocalDateTime.now()));

        List<BenefitDeterminationItem> items = req.getItems().stream().map(it -> buildItem(determination.getId(), person, it)).toList();
        for (BenefitDeterminationItem item : items)
        {
            benefitDeterminationItemMapper.insert(item);
        }

        return determination.getId();
    }

    private String stripProfilePrefix(String resourcePath)
    {
        return resourcePath.replaceFirst("^/profile/", "");
    }

    private BenefitDeterminationItem buildItem(Long determinationId, SubsidyPerson person, BenefitDeterminationSaveDraftReq.Item reqItem)
    {
        YearMonth startYm = parseYearMonth(reqItem.getStartMonth(), "享受开始年月");
        int months = computeBackpayMonths(startYm, YearMonth.now());
        BigDecimal standard = resolveStandard(person.getId(), reqItem.getSubsidyType());
        BigDecimal amount = standard.multiply(BigDecimal.valueOf(months)).setScale(2, java.math.RoundingMode.HALF_UP);

        BenefitDeterminationItem item = new BenefitDeterminationItem();
        item.setDeterminationId(determinationId);
        item.setSubsidyType(reqItem.getSubsidyType());
        item.setVillageStreet(reqItem.getVillageStreet());
        item.setEventDate(StringUtils.isBlank(reqItem.getEventDate()) ? null : LocalDate.parse(reqItem.getEventDate()));
        item.setSubsidyStandard(standard);
        item.setBenefitStartYear(startYm.getYear());
        item.setBenefitStartMonth(startYm.getMonthValue());
        item.setBenefitMonths(months);
        item.setBenefitAmount(amount);
        item.setDelFlag("0");
        item.setCreateBy(SecurityUtils.getUsername());
        item.setCreateTime(java.time.LocalDateTime.now());
        item.setUpdateBy(SecurityUtils.getUsername());
        item.setUpdateTime(java.time.LocalDateTime.now());
        return item;
    }

    private BigDecimal resolveStandard(Long subsidyPersonId, String subsidyType)
    {
        if ("land_loss".equals(subsidyType))
        {
            return readConfigDecimal(com.ruoyi.shebao.service.impl.SubsidyCalculationServiceImpl.LAND_LOSS_RESIDENT_SUBSIDY_KEY);
        }
        if ("demolition".equals(subsidyType))
        {
            return readConfigDecimal(com.ruoyi.shebao.service.impl.SubsidyCalculationServiceImpl.CEMETERY_RESIDENT_SUBSIDY_KEY);
        }
        if ("village_official".equals(subsidyType))
        {
            List<VillageOfficial> list = villageOfficialMapper.selectBySubsidyPersonId(subsidyPersonId);
            return (list == null ? java.util.stream.Stream.<BigDecimal>empty() : list.stream().map(VillageOfficial::getSubsidyAmount))
                    .filter(Objects::nonNull)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal readConfigDecimal(String key)
    {
        String val = sysConfigService.selectConfigByKey(key);
        if (StringUtils.isBlank(val))
        {
            return BigDecimal.ZERO;
        }
        try
        {
            return new BigDecimal(val.trim());
        }
        catch (Exception e)
        {
            return BigDecimal.ZERO;
        }
    }

    private static YearMonth parseYearMonth(String text, String fieldName)
    {
        if (text == null || !text.matches("\\d{4}-\\d{2}"))
        {
            throw new ServiceException(fieldName + "格式错误，应为yyyy-MM");
        }
        String[] parts = text.split("-");
        return YearMonth.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    private static int computeBackpayMonths(YearMonth startYm, YearMonth currentYm)
    {
        if (startYm == null || currentYm == null)
        {
            return 0;
        }
        long months = java.time.temporal.ChronoUnit.MONTHS.between(startYm, currentYm);
        return (int) Math.max(months, 0);
    }

    private static YearMonth computeEligibleYm(SubsidyPerson person)
    {
        if (person == null || person.getBirthday() == null)
        {
            return null;
        }
        return YearMonth.from(person.getBirthday().plusYears(60));
    }

    private static YearMonth plusOneMonth(LocalDate date)
    {
        return date == null ? null : YearMonth.from(date).plusMonths(1);
    }

    private static YearMonth maxYm(YearMonth a, YearMonth b)
    {
        if (a == null) return b;
        if (b == null) return a;
        return a.isAfter(b) ? a : b;
    }

    private static Date yearMonthToDate(Integer year, Integer month)
    {
        if (year == null || month == null)
        {
            return null;
        }
        return java.sql.Date.valueOf(LocalDate.of(year, month, 1));
    }

    private static String formatYm(YearMonth ym)
    {
        return ym == null ? null : String.format("%04d-%02d", ym.getYear(), ym.getMonthValue());
    }
}
