package com.ruoyi.shebao.service.impl;

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

/**
 * 人员关键信息修改申请Service实现
 * 经办-复核-审批 三级
 *
 * @author ruoyi
 */
@Service
@RequiredArgsConstructor
public class PersonKeyInfoModifyServiceImpl extends ServiceImpl<PersonKeyInfoModifyMapper, PersonKeyInfoModify> implements PersonKeyInfoModifyService {

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
            entity.setId(form.getId());
            entity.setCreateBy(existing.getCreateBy());
            entity.setCreateTime(existing.getCreateTime());
            updateById(entity);
            return entity.getId();
        }
        save(entity);
        return entity.getId();
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
        entity.setUpdateBy(SecurityUtils.getUsername());
        entity.setUpdateTime(LocalDateTime.now());
        if (approved) {
            entity.setApprovalStatus("pending_approve");
            entity.setRejectReason(null);
        } else {
            entity.setApprovalStatus("rejected");
            entity.setRejectReason(remark);
        }
        return updateById(entity) ? 1 : 0;
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
        entity.setApproveBy(SecurityUtils.getUsername());
        entity.setApproveTime(LocalDateTime.now());
        entity.setUpdateBy(SecurityUtils.getUsername());
        entity.setUpdateTime(LocalDateTime.now());
        if (approved) {
            entity.setApprovalStatus("approved");
            entity.setRejectReason(null);
            // 回写被补贴人关键信息
            SubsidyPerson person = subsidyPersonMapper.selectById(entity.getSubsidyPersonId());
            if (person != null) {
                person.setName(entity.getName());
                person.setIdCardNo(entity.getIdCardNo());
                person.setHouseholdRegistration(entity.getHouseholdRegistration());
                person.setStreetOfficeId(entity.getStreetOfficeId());
                person.setVillageCommitteeId(entity.getVillageCommitteeId());
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
