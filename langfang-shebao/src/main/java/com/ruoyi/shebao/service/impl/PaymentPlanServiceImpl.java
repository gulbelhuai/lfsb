package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.shebao.domain.PaymentPlan;
import com.ruoyi.shebao.domain.PaymentPlanAudit;
import com.ruoyi.shebao.domain.PaymentPlanDetail;
import com.ruoyi.shebao.domain.PaymentPlanSummary;
import com.ruoyi.shebao.dto.*;
import com.ruoyi.shebao.mapper.PaymentPlanAuditMapper;
import com.ruoyi.shebao.mapper.PaymentPlanDetailMapper;
import com.ruoyi.shebao.mapper.PaymentPlanMapper;
import com.ruoyi.shebao.mapper.PaymentPlanSummaryMapper;
import com.ruoyi.shebao.service.PaymentPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentPlanServiceImpl implements PaymentPlanService
{
    private static final String TYPE_NORMAL = "normal";
    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_PENDING_REVIEW = "pending_review";
    private static final String STATUS_PENDING_APPROVE = "pending_approve";
    private static final String STATUS_APPROVED = "approved";
    private static final String STATUS_REVIEW_REJECTED = "review_rejected";
    private static final String STATUS_APPROVE_REJECTED = "approve_rejected";
    private static final Set<String> SUBMIT_ALLOWED = Set.of(STATUS_DRAFT, STATUS_REVIEW_REJECTED, STATUS_APPROVE_REJECTED);

    @Autowired
    private PaymentPlanMapper paymentPlanMapper;
    @Autowired
    private PaymentPlanSummaryMapper paymentPlanSummaryMapper;
    @Autowired
    private PaymentPlanDetailMapper paymentPlanDetailMapper;
    @Autowired
    private PaymentPlanAuditMapper paymentPlanAuditMapper;

    @Override
    public Page<PaymentPlanListResp> selectPaymentPlanList(PaymentPlanListReq req)
    {
        Page<PaymentPlanListResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return paymentPlanMapper.selectPaymentPlanList(page, req);
    }

    @Override
    public PaymentPlanPreviewResp preview(PaymentPlanPreviewReq req)
    {
        validateReq(req.getDeterminationType(), req.getBusinessPeriod());
        PaymentPlanPreviewResp resp = buildBasePreview(req.getDeterminationType(), req.getBusinessPeriod());
        if (!TYPE_NORMAL.equals(req.getDeterminationType()))
        {
            return resp;
        }
        LocalDate businessPeriod = parseBusinessPeriod(req.getBusinessPeriod());
        List<PaymentPlanDetailResp> details = paymentPlanDetailMapper.selectPreviewDetails(businessPeriod);
        resp.setDetailList(details);
        fillSummaryAndTotal(resp, details);
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generate(PaymentPlanGenerateReq req)
    {
        req.setTargetStatus(STATUS_PENDING_REVIEW);
        return saveOrSubmit(req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrSubmit(PaymentPlanGenerateReq req)
    {
        PaymentPlanPreviewReq previewReq = new PaymentPlanPreviewReq();
        BeanUtils.copyProperties(req, previewReq);
        PaymentPlanPreviewResp preview = preview(previewReq);
        if (!TYPE_NORMAL.equals(req.getDeterminationType()))
        {
            throw new ServiceException("二次发放暂未实现");
        }
        if (preview.getDetailList().isEmpty())
        {
            throw new ServiceException("没有可保存的支付计划数据");
        }
        LocalDate period = parseBusinessPeriod(req.getBusinessPeriod());
        Date now = new Date();
        String operatorName = resolveOperatorName();
        String username = SecurityUtils.getUsername();
        String targetStatus = normalizeTargetStatus(req.getTargetStatus());

        PaymentPlan plan = req.getPlanId() == null ? null : paymentPlanMapper.selectById(req.getPlanId());
        String previousStatus = null;
        if (plan == null)
        {
            plan = new PaymentPlan();
            plan.setDeterminationType(req.getDeterminationType());
            plan.setBusinessPeriod(period);
            plan.setDelFlag("0");
            plan.setCreateBy(username);
            plan.setCreateTime(now);
            previousStatus = null;
        }
        else
        {
            previousStatus = plan.getApprovalStatus();
            if (!SUBMIT_ALLOWED.contains(previousStatus))
            {
                throw new ServiceException("当前状态不允许保存或提交");
            }
            if (!Objects.equals(plan.getBusinessPeriod(), period) || !Objects.equals(plan.getDeterminationType(), req.getDeterminationType()))
            {
                throw new ServiceException("仅支持在原业务期和核定方式下重算保存");
            }
        }

        plan.setTotalCount(preview.getTotalCount());
        plan.setTotalAmount(preview.getTotalAmount());
        plan.setOperatorName(operatorName);
        plan.setOperatorTime(now);
        plan.setApprovalStatus(targetStatus);
        plan.setUpdateBy(username);
        plan.setUpdateTime(now);
        if (plan.getId() == null)
        {
            plan.setBatchNo(nextBatchNo(period, req.getDeterminationType()));
            paymentPlanMapper.insert(plan);
        }
        else
        {
            paymentPlanMapper.updateById(plan);
            paymentPlanSummaryMapper.deleteByPlanId(plan.getId());
            paymentPlanDetailMapper.deleteByPlanId(plan.getId());
        }
        final Long persistedPlanId = plan.getId();

        List<PaymentPlanSummary> summaryRows = preview.getSummaryList().stream().map(item -> {
            PaymentPlanSummary row = new PaymentPlanSummary();
            row.setPlanId(persistedPlanId);
            row.setBusinessPeriod(period);
            row.setSubsidyType(item.getSubsidyType());
            row.setGrantOrg(item.getGrantOrg());
            row.setTotalCount(item.getTotalCount());
            row.setTotalAmount(item.getTotalAmount());
            row.setDelFlag("0");
            row.setCreateBy(username);
            row.setCreateTime(now);
            row.setUpdateBy(username);
            row.setUpdateTime(now);
            return row;
        }).toList();
        if (!summaryRows.isEmpty())
        {
            paymentPlanSummaryMapper.batchInsert(summaryRows);
        }

        List<PaymentPlanDetail> detailRows = preview.getDetailList().stream().map(item -> {
            PaymentPlanDetail row = new PaymentPlanDetail();
            row.setPlanId(persistedPlanId);
            row.setDeterminationId(item.getDeterminationId());
            row.setDeterminationItemId(item.getDeterminationItemId());
            row.setSubsidyType(item.getSubsidyType());
            row.setStreetName(item.getStreetName());
            row.setVillageName(item.getVillageName());
            row.setPersonName(item.getPersonName());
            row.setIdCardNo(item.getIdCardNo());
            row.setBusinessPeriod(period);
            row.setPaymentMonth(item.getPaymentMonth());
            row.setDistributionAmount(item.getDistributionAmount());
            row.setGrantOrg(item.getGrantOrg());
            row.setAccountName(item.getAccountName());
            row.setBankAccount(item.getBankAccount());
            row.setRelationToInsured(item.getRelationToInsured());
            row.setDelFlag("0");
            row.setCreateBy(username);
            row.setCreateTime(now);
            row.setUpdateBy(username);
            row.setUpdateTime(now);
            return row;
        }).toList();
        if (!detailRows.isEmpty())
        {
            paymentPlanDetailMapper.batchInsert(detailRows);
        }

        if (!Objects.equals(previousStatus, targetStatus) && STATUS_PENDING_REVIEW.equals(targetStatus))
        {
            insertAudit(plan.getId(), targetStatus, req.getRemark());
        }
        return plan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(Long planId, PaymentPlanStatusChangeReq req)
    {
        PaymentPlan plan = paymentPlanMapper.selectById(planId);
        if (plan == null || !"0".equals(plan.getDelFlag()))
        {
            throw new ServiceException("支付计划不存在");
        }
        String target = normalizeChangeStatus(req.getTargetStatus());
        String current = plan.getApprovalStatus();
        validateTransition(current, target);
        validateRemark(req.getRemark(), target);
        plan.setApprovalStatus(target);
        plan.setUpdateBy(SecurityUtils.getUsername());
        plan.setUpdateTime(new Date());
        int updated = paymentPlanMapper.updateById(plan);
        if (updated > 0)
        {
            insertAudit(planId, target, req.getRemark());
        }
        return updated;
    }

    @Override
    public List<PaymentPlanSummaryResp> selectSummaryByPlanId(Long planId)
    {
        return paymentPlanSummaryMapper.selectByPlanId(planId);
    }

    @Override
    public List<PaymentPlanAuditResp> selectAuditByPlanId(Long planId)
    {
        return paymentPlanAuditMapper.selectByPlanId(planId);
    }

    @Override
    public Page<PaymentPlanDetailResp> selectDetailByPlanId(Long planId, Integer pageNum, Integer pageSize)
    {
        Page<PaymentPlanDetailResp> page = new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
        return paymentPlanDetailMapper.selectByPlanId(page, planId);
    }

    private void validateReq(String determinationType, String businessPeriod)
    {
        if (determinationType == null || determinationType.isBlank())
        {
            throw new ServiceException("请选择核定方式");
        }
        if (businessPeriod == null || businessPeriod.isBlank())
        {
            throw new ServiceException("请选择业务期");
        }
    }

    private PaymentPlanPreviewResp buildBasePreview(String determinationType, String businessPeriod)
    {
        PaymentPlanPreviewResp resp = new PaymentPlanPreviewResp();
        resp.setDeterminationType(determinationType);
        resp.setBusinessPeriod(businessPeriod);
        resp.setOperatorName(resolveOperatorName());
        resp.setOperatorTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return resp;
    }

    private LocalDate parseBusinessPeriod(String businessPeriod)
    {
        return LocalDate.parse(businessPeriod + "-01");
    }

    private void fillSummaryAndTotal(PaymentPlanPreviewResp resp, List<PaymentPlanDetailResp> details)
    {
        Map<String, List<PaymentPlanDetailResp>> grouped = details.stream()
                .collect(Collectors.groupingBy(item -> defaultValue(item.getSubsidyType()) + "||" + defaultValue(item.getGrantOrg())));
        List<PaymentPlanSummaryResp> summaryList = new ArrayList<>();
        for (Map.Entry<String, List<PaymentPlanDetailResp>> entry : grouped.entrySet())
        {
            List<PaymentPlanDetailResp> groupRows = entry.getValue();
            PaymentPlanSummaryResp summary = new PaymentPlanSummaryResp();
            summary.setBusinessPeriod(resp.getBusinessPeriod());
            summary.setSubsidyType(groupRows.get(0).getSubsidyType());
            summary.setGrantOrg(groupRows.get(0).getGrantOrg());
            summary.setTotalCount(groupRows.size());
            summary.setTotalAmount(groupRows.stream()
                    .map(PaymentPlanDetailResp::getDistributionAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            summaryList.add(summary);
        }
        summaryList.sort(Comparator
                .comparing((PaymentPlanSummaryResp o) -> defaultValue(o.getSubsidyType()))
                .thenComparing(o -> defaultValue(o.getGrantOrg())));
        resp.setSummaryList(summaryList);
        resp.setTotalCount(details.size());
        resp.setTotalAmount(summaryList.stream()
                .map(PaymentPlanSummaryResp::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private String defaultValue(String value)
    {
        return value == null ? "" : value;
    }

    private String resolveOperatorName()
    {
        try
        {
            var loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null && loginUser.getUser() != null)
            {
                String nickName = loginUser.getUser().getNickName();
                if (nickName != null && !nickName.isBlank())
                {
                    return nickName;
                }
            }
        }
        catch (Exception ignored)
        {
            // 回退到用户名
        }
        return SecurityUtils.getUsername();
    }

    private String normalizeTargetStatus(String status)
    {
        if (status == null || status.isBlank() || STATUS_DRAFT.equals(status))
        {
            return STATUS_DRAFT;
        }
        if (STATUS_PENDING_REVIEW.equals(status))
        {
            return STATUS_PENDING_REVIEW;
        }
        throw new ServiceException("targetStatus仅支持draft或pending_review");
    }

    private String normalizeChangeStatus(String status)
    {
        if (Set.of(
                STATUS_DRAFT,
                STATUS_PENDING_REVIEW,
                STATUS_PENDING_APPROVE,
                STATUS_APPROVED,
                STATUS_REVIEW_REJECTED,
                STATUS_APPROVE_REJECTED).contains(status))
        {
            return status;
        }
        throw new ServiceException("不支持的状态变更");
    }

    private void validateTransition(String current, String target)
    {
        if (STATUS_PENDING_REVIEW.equals(target) && SUBMIT_ALLOWED.contains(current))
        {
            return; // 提交
        }
        if (STATUS_DRAFT.equals(target) && STATUS_PENDING_REVIEW.equals(current))
        {
            return; // 撤回
        }
        if (STATUS_PENDING_APPROVE.equals(target) && STATUS_PENDING_REVIEW.equals(current))
        {
            return; // 复核通过
        }
        if (STATUS_REVIEW_REJECTED.equals(target) && STATUS_PENDING_REVIEW.equals(current))
        {
            return; // 复核驳回
        }
        if (STATUS_PENDING_REVIEW.equals(target) && (STATUS_PENDING_APPROVE.equals(current) || STATUS_REVIEW_REJECTED.equals(current)))
        {
            return; // 撤销复核
        }
        if (STATUS_APPROVED.equals(target) && STATUS_PENDING_APPROVE.equals(current))
        {
            return; // 审批通过
        }
        if (STATUS_APPROVE_REJECTED.equals(target) && STATUS_PENDING_APPROVE.equals(current))
        {
            return; // 审批驳回
        }
        throw new ServiceException("当前状态不支持该操作");
    }

    private void validateRemark(String remark, String target)
    {
        if ((STATUS_REVIEW_REJECTED.equals(target) || STATUS_APPROVE_REJECTED.equals(target))
                && (remark == null || remark.isBlank()))
        {
            throw new ServiceException("驳回时备注必填");
        }
    }

    private void insertAudit(Long planId, String status, String remark)
    {
        PaymentPlanAudit audit = new PaymentPlanAudit();
        audit.setPlanId(planId);
        audit.setOperationStatus(status);
        audit.setOperatorName(resolveOperatorName());
        audit.setOperationTime(new Date());
        audit.setRemark(remark);
        audit.setDelFlag("0");
        audit.setCreateBy(SecurityUtils.getUsername());
        audit.setCreateTime(new Date());
        audit.setUpdateBy(SecurityUtils.getUsername());
        audit.setUpdateTime(new Date());
        paymentPlanAuditMapper.insert(audit);
    }

    /**
     * 批次号：业务期年月 6 位 + 类型 2 位（01 正常 / 02 二次）+ 三位自增序号。
     */
    private String nextBatchNo(LocalDate businessPeriod, String determinationType)
    {
        String yyyymm = String.format("%04d%02d", businessPeriod.getYear(), businessPeriod.getMonthValue());
        String typeCode = TYPE_NORMAL.equals(determinationType) ? "01" : "02";
        String prefix8 = yyyymm + typeCode;
        int maxSeq = paymentPlanMapper.selectMaxBatchSeqSuffix(prefix8);
        int next = maxSeq + 1;
        if (next > 999)
        {
            throw new ServiceException("该业务期与类型下批次序号已超过999");
        }
        return prefix8 + String.format("%03d", next);
    }
}
