package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.PersonKeyInfoModify;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyFormDto;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyListReq;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyListResp;
import com.ruoyi.shebao.mapper.PersonKeyInfoModifyMapper;
import com.ruoyi.shebao.mapper.SubsidyPersonMapper;
import com.ruoyi.shebao.service.PersonKeyInfoModifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 人员信息变更申请：basic 为 2 级（复核通过即回写被补贴人非关键字段），key 为 3 级（复核→审批后回写，含姓名/身份证）
 *
 * @author ruoyi
 */
@Service
@RequiredArgsConstructor
public class PersonKeyInfoModifyServiceImpl extends ServiceImpl<PersonKeyInfoModifyMapper, PersonKeyInfoModify> implements PersonKeyInfoModifyService {

    private static final String MODIFY_BASIC = "basic";
    private static final String MODIFY_KEY = "key";

    private final PersonKeyInfoModifyMapper personKeyInfoModifyMapper;
    private final SubsidyPersonMapper subsidyPersonMapper;

    @Override
    public Page<PersonKeyInfoModifyListResp> selectModifyList(PersonKeyInfoModifyListReq req) {
        Page<PersonKeyInfoModifyListResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return personKeyInfoModifyMapper.selectModifyList(page, req);
    }

    @Override
    public PersonKeyInfoModifyFormDto selectFormById(Long id) {
        return personKeyInfoModifyMapper.selectFormById(id);
    }

    /**
     * 未办结：除「已通过」外均算未完成（含草稿/待复核/待审批/已驳回等；状态为空的历史数据也视为未完成）。
     * 注意：仅用 {@code ne("approved")} 在 SQL 中会漏掉 {@code approval_status IS NULL} 的行。
     */
    @Override
    public boolean hasUnfinishedModifyForPerson(Long subsidyPersonId, Long excludeModifyId) {
        if (subsidyPersonId == null) {
            return false;
        }
        LambdaQueryWrapper<PersonKeyInfoModify> w = new LambdaQueryWrapper<>();
        w.eq(PersonKeyInfoModify::getSubsidyPersonId, subsidyPersonId)
            .eq(PersonKeyInfoModify::getDelFlag, "0")
            .and(q -> q.isNull(PersonKeyInfoModify::getApprovalStatus)
                .or()
                .ne(PersonKeyInfoModify::getApprovalStatus, "approved"));
        if (excludeModifyId != null) {
            w.ne(PersonKeyInfoModify::getId, excludeModifyId);
        }
        return count(w) > 0;
    }

    private void assertNoUnfinishedConflict(Long subsidyPersonId, Long excludeModifyId) {
        if (hasUnfinishedModifyForPerson(subsidyPersonId, excludeModifyId)) {
            throw new ServiceException("存在未完成的业务");
        }
    }

    private void fillOldSnapshotFromPerson(PersonKeyInfoModify entity, SubsidyPerson person) {
        if (person == null) {
            return;
        }
        entity.setOldName(person.getName());
        entity.setOldIdCardNo(person.getIdCardNo());
        entity.setOldHouseholdRegistration(person.getHouseholdRegistration());
        entity.setOldStreetOfficeId(person.getStreetOfficeId());
        entity.setOldVillageCommitteeId(person.getVillageCommitteeId());
        entity.setOldHomeAddress(person.getHomeAddress());
        entity.setOldPhone(person.getPhone());
    }

    private void keepOldSnapshotFromExisting(PersonKeyInfoModify entity, PersonKeyInfoModify existing) {
        entity.setOldName(existing.getOldName());
        entity.setOldIdCardNo(existing.getOldIdCardNo());
        entity.setOldHouseholdRegistration(existing.getOldHouseholdRegistration());
        entity.setOldStreetOfficeId(existing.getOldStreetOfficeId());
        entity.setOldVillageCommitteeId(existing.getOldVillageCommitteeId());
        entity.setOldHomeAddress(existing.getOldHomeAddress());
        entity.setOldPhone(existing.getOldPhone());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long saveOrUpdateDraft(PersonKeyInfoModifyFormDto form) {
        PersonKeyInfoModify entity = new PersonKeyInfoModify();
        entity.setSubsidyPersonId(form.getSubsidyPersonId());
        entity.setName(form.getName());
        entity.setIdCardNo(form.getIdCardNo());
        entity.setHouseholdRegistration(form.getHouseholdRegistration());
        entity.setStreetOfficeId(form.getStreetOfficeId());
        entity.setVillageCommitteeId(form.getVillageCommitteeId());
        entity.setHomeAddress(form.getHomeAddress());
        entity.setPhone(form.getPhone());
        entity.setApprovalStatus("draft");
        entity.setDelFlag("0");
        entity.setRemark(form.getRemark());
        String user = SecurityUtils.getUsername();
        entity.setCreateBy(user);
        entity.setUpdateBy(user);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        if (form.getId() != null && form.getId() > 0) {
            PersonKeyInfoModify existing = getById(form.getId());
            if (existing == null) {
                throw new ServiceException("修改申请不存在");
            }
            if (!"draft".equals(existing.getApprovalStatus()) && !"rejected".equals(existing.getApprovalStatus())) {
                throw new ServiceException("仅草稿或已驳回状态可保存");
            }
            assertNoUnfinishedConflict(form.getSubsidyPersonId(), form.getId());
            String modifyType = resolveModifyTypeForSave(existing, form.getModifyType());
            if (MODIFY_BASIC.equals(modifyType)) {
                validateBasicIdentityUnchanged(form);
            }
            entity.setModifyType(modifyType);
            entity.setId(form.getId());
            keepOldSnapshotFromExisting(entity, existing);
            entity.setCreateBy(existing.getCreateBy());
            entity.setCreateTime(existing.getCreateTime());
            updateById(entity);
            return entity.getId();
        }
        String modifyType = normalizeNewModifyType(form.getModifyType());
        if (MODIFY_BASIC.equals(modifyType)) {
            validateBasicIdentityUnchanged(form);
        }
        assertNoUnfinishedConflict(form.getSubsidyPersonId(), null);
        entity.setModifyType(modifyType);
        SubsidyPerson currentPerson = subsidyPersonMapper.selectById(form.getSubsidyPersonId());
        fillOldSnapshotFromPerson(entity, currentPerson);
        save(entity);
        return entity.getId();
    }

    /** 已存在记录沿用库中变更类型，避免 basic/key 混切 */
    private static String resolveModifyTypeForSave(PersonKeyInfoModify existing, String formType) {
        if (existing.getModifyType() != null && !existing.getModifyType().isBlank()) {
            return existing.getModifyType();
        }
        return normalizeNewModifyType(formType);
    }

    private static String normalizeNewModifyType(String formType) {
        if (formType == null || formType.isBlank()) {
            return MODIFY_KEY;
        }
        if (MODIFY_BASIC.equals(formType) || MODIFY_KEY.equals(formType)) {
            return formType;
        }
        throw new ServiceException("变更类型无效，应为 basic 或 key");
    }

    private void validateBasicIdentityUnchanged(PersonKeyInfoModifyFormDto form) {
        if (form.getSubsidyPersonId() == null) {
            throw new ServiceException("请先查询并关联被补贴人");
        }
        SubsidyPerson p = subsidyPersonMapper.selectById(form.getSubsidyPersonId());
        if (p == null) {
            throw new ServiceException("被补贴人不存在");
        }
        if (!strEq(p.getName(), form.getName()) || !strEq(p.getIdCardNo(), form.getIdCardNo())) {
            throw new ServiceException("基本信息变更不可修改姓名与身份证号");
        }
    }

    private static boolean strEq(String a, String b) {
        return Objects.equals(trim(a), trim(b));
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String effectiveModifyType(PersonKeyInfoModify entity) {
        String t = entity.getModifyType();
        if (t == null || t.isBlank()) {
            return MODIFY_KEY;
        }
        return t;
    }

    /**
     * 关键信息变更场景下，校验目标身份证号不能与其他人员重复。
     */
    private void validateKeyIdCardUnique(PersonKeyInfoModify entity) {
        if (!MODIFY_KEY.equals(effectiveModifyType(entity))) {
            return;
        }
        String idCardNo = trim(entity.getIdCardNo());
        if (idCardNo.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<SubsidyPerson> w = new LambdaQueryWrapper<>();
        w.eq(SubsidyPerson::getIdCardNo, idCardNo)
            .ne(entity.getSubsidyPersonId() != null, SubsidyPerson::getId, entity.getSubsidyPersonId())
            .last("limit 1");
        SubsidyPerson existed = subsidyPersonMapper.selectOne(w);
        if (existed != null) {
            throw new ServiceException("相同的身份证号码已存在");
        }
    }

    private void applyNonKeyPersonFields(PersonKeyInfoModify entity, SubsidyPerson person) {
        person.setHouseholdRegistration(entity.getHouseholdRegistration());
        person.setStreetOfficeId(entity.getStreetOfficeId());
        person.setVillageCommitteeId(entity.getVillageCommitteeId());
        person.setHomeAddress(entity.getHomeAddress());
        person.setPhone(entity.getPhone());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submit(Long id) {
        PersonKeyInfoModify entity = getById(id);
        if (entity == null) {
            throw new ServiceException("修改申请不存在");
        }
        if (!"draft".equals(entity.getApprovalStatus()) && !"rejected".equals(entity.getApprovalStatus())) {
            throw new ServiceException("仅草稿或已驳回状态可提交");
        }
        validateKeyIdCardUnique(entity);
        entity.setApprovalStatus("pending_review");
        entity.setSubmitBy(SecurityUtils.getUsername());
        entity.setSubmitTime(LocalDateTime.now());
        entity.setUpdateBy(SecurityUtils.getUsername());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setRejectReason(null);
        return updateById(entity) ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int review(Long id, boolean approved, String remark) {
        PersonKeyInfoModify entity = getById(id);
        if (entity == null) {
            throw new ServiceException("修改申请不存在");
        }
        if (!"pending_review".equals(entity.getApprovalStatus())) {
            throw new ServiceException("当前状态不允许复核");
        }
        entity.setReviewBy(SecurityUtils.getUsername());
        entity.setReviewTime(LocalDateTime.now());
        entity.setReviewRemark(remark);
        entity.setUpdateBy(SecurityUtils.getUsername());
        entity.setUpdateTime(LocalDateTime.now());
        if (approved) {
            if (MODIFY_BASIC.equals(effectiveModifyType(entity))) {
                entity.setApprovalStatus("approved");
                entity.setRejectReason(null);
                writeBackAfterBasicReviewPass(entity);
            } else {
                entity.setApprovalStatus("pending_approve");
                entity.setRejectReason(null);
            }
        } else {
            entity.setApprovalStatus("rejected");
            entity.setRejectReason(remark);
        }
        return updateById(entity) ? 1 : 0;
    }

    /** 基本信息变更：复核通过即回写 subsider_person（不含姓名、身份证号） */
    private void writeBackAfterBasicReviewPass(PersonKeyInfoModify entity) {
        SubsidyPerson person = subsidyPersonMapper.selectById(entity.getSubsidyPersonId());
        if (person == null) {
            return;
        }
        applyNonKeyPersonFields(entity, person);
        person.setUpdateBy(SecurityUtils.getUsername());
        person.setUpdateTime(LocalDateTime.now());
        subsidyPersonMapper.updateById(person);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approve(Long id, boolean approved, String remark) {
        PersonKeyInfoModify entity = getById(id);
        if (entity == null) {
            throw new ServiceException("修改申请不存在");
        }
        if (!"pending_approve".equals(entity.getApprovalStatus())) {
            throw new ServiceException("当前状态不允许审批");
        }
        if (MODIFY_BASIC.equals(effectiveModifyType(entity))) {
            throw new ServiceException("基本信息变更无需所长审批");
        }
        entity.setApproveBy(SecurityUtils.getUsername());
        entity.setApproveTime(LocalDateTime.now());
        entity.setApproveRemark(remark);
        entity.setUpdateBy(SecurityUtils.getUsername());
        entity.setUpdateTime(LocalDateTime.now());
        if (approved) {
            validateKeyIdCardUnique(entity);
            entity.setApprovalStatus("approved");
            entity.setRejectReason(null);
            SubsidyPerson person = subsidyPersonMapper.selectById(entity.getSubsidyPersonId());
            if (person != null) {
                person.setName(entity.getName());
                person.setIdCardNo(entity.getIdCardNo());
                applyNonKeyPersonFields(entity, person);
                person.setUpdateBy(SecurityUtils.getUsername());
                person.setUpdateTime(LocalDateTime.now());
                subsidyPersonMapper.updateById(person);
            }
        } else {
            entity.setApprovalStatus("rejected");
            entity.setRejectReason(remark);
        }
        return updateById(entity) ? 1 : 0;
    }
}
