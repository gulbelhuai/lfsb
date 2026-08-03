package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.domain.BenefitDeterminationItem;
import com.ruoyi.shebao.domain.BenefitResumeItem;
import com.ruoyi.shebao.domain.PaymentPlan;
import com.ruoyi.shebao.domain.PaymentPlanAudit;
import com.ruoyi.shebao.domain.PaymentPlanDetail;
import com.ruoyi.shebao.domain.PaymentPlanSummary;
import com.ruoyi.shebao.dto.*;
import com.ruoyi.shebao.mapper.BenefitDeterminationItemMapper;
import com.ruoyi.shebao.mapper.BenefitDeterminationMapper;
import com.ruoyi.shebao.mapper.BenefitResumeItemMapper;
import com.ruoyi.shebao.mapper.PaymentPlanAuditMapper;
import com.ruoyi.shebao.mapper.PaymentPlanDetailMapper;
import com.ruoyi.shebao.mapper.PaymentPlanMapper;
import com.ruoyi.shebao.mapper.PaymentPlanSummaryMapper;
import com.ruoyi.shebao.service.IFinanceAccountService;
import com.ruoyi.shebao.service.PaymentPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
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
    /** 财务驳回后退回业务侧，可再提交/重算（与 finance_status 同值） */
    private static final String STATUS_FINANCE_REJECTED = "finance_rejected";
    private static final Set<String> SUBMIT_ALLOWED = Set.of(
            STATUS_DRAFT, STATUS_REVIEW_REJECTED, STATUS_APPROVE_REJECTED, STATUS_FINANCE_REJECTED);
    /** 允许保存/重算换绑（含待复核） */
    private static final Set<String> SAVE_ALLOWED = Set.of(
            STATUS_DRAFT, STATUS_PENDING_REVIEW, STATUS_REVIEW_REJECTED, STATUS_APPROVE_REJECTED, STATUS_FINANCE_REJECTED);

    private static final String SUPPLEMENT_UNPAID = "0";
    private static final String SUPPLEMENT_INCLUDED = "1";
    private static final String SUPPLEMENT_PAID = "2";
    private static final String SUPPLEMENT_SRC_DETERMINATION = "determination";
    private static final String SUPPLEMENT_SRC_RESUME = "resume";

    private static final String AUDIT_STAGE_SUBSIDY = "subsidy";
    private static final String AUDIT_STAGE_FINANCE = "finance";
    /** 上传财务后进入待财务 */
    private static final String FINANCE_PENDING = "pending_finance";
    private static final String FINANCE_PENDING_REVIEW = "finance_pending_review";
    private static final String FINANCE_PENDING_APPROVE = "finance_pending_approve";
    private static final String FINANCE_APPROVED = "finance_approved";
    private static final String FINANCE_REJECTED = "finance_rejected";

    /** 发放状态 */
    private static final String DIST_PENDING = "pending";
    private static final String DIST_SUBMITTED = "submitted";
    private static final String DIST_COMPLETED = "completed";
    /** 发放结果 */
    private static final String DIST_RESULT_FAILED = "failed";

    @Autowired
    private PaymentPlanMapper paymentPlanMapper;
    @Autowired
    private PaymentPlanSummaryMapper paymentPlanSummaryMapper;
    @Autowired
    private PaymentPlanDetailMapper paymentPlanDetailMapper;
    @Autowired
    private PaymentPlanAuditMapper paymentPlanAuditMapper;
    @Autowired
    private IFinanceAccountService financeAccountService;
    @Autowired
    private BenefitDeterminationMapper benefitDeterminationMapper;
    @Autowired
    private BenefitDeterminationItemMapper benefitDeterminationItemMapper;
    @Autowired
    private BenefitResumeItemMapper benefitResumeItemMapper;

    @Override
    public Page<PaymentPlanListResp> selectPaymentPlanList(PaymentPlanListReq req)
    {
        Page<PaymentPlanListResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return paymentPlanMapper.selectPaymentPlanList(page, req);
    }

    @Override
    public Page<PaymentPlanFailureListResp> selectFailureList(PaymentPlanFailureListReq req)
    {
        Page<PaymentPlanFailureListResp> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());
        return paymentPlanDetailMapper.selectFailureList(page, req);
    }

    @Override
    public PaymentPlanPreviewResp preview(PaymentPlanPreviewReq req)
    {
        validateReq(req.getDeterminationType(), req.getBusinessPeriod(), req.getSubsidyType());
        PaymentPlanPreviewResp resp = buildBasePreview(req.getDeterminationType(), req.getBusinessPeriod());
        resp.setSubsidyType(req.getSubsidyType());
        if (!TYPE_NORMAL.equals(req.getDeterminationType()))
        {
            return resp;
        }
        LocalDate businessPeriod = parseBusinessPeriod(req.getBusinessPeriod());
        List<PaymentPlanDetailResp> details = paymentPlanDetailMapper.selectPreviewDetails(
                businessPeriod, req.getSubsidyType(), req.getExcludePlanId());
        attachSupplementCandidates(details);
        resp.setDetailList(details);
        fillSummaryAndTotal(resp, details);
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generate(PaymentPlanGenerateReq req)
    {
        validateNoPendingRecords(req.getSubsidyType());
        req.setTargetStatus(STATUS_PENDING_REVIEW);
        return saveOrSubmit(req);
    }

    private void validateNoPendingRecords(String subsidyType)
    {
        if (StringUtils.isEmpty(subsidyType))
        {
            return;
        }
        List<String> problems = new ArrayList<>();
        int pendingReg = paymentPlanDetailMapper.countPendingRegistrationReview(subsidyType);
        if (pendingReg > 0)
        {
            problems.add("未复核的登记记录 " + pendingReg + " 条");
        }
        int pendingDet = paymentPlanDetailMapper.countPendingDeterminationApproval(subsidyType);
        if (pendingDet > 0)
        {
            problems.add("未审核的核定记录 " + pendingDet + " 条");
        }
        if (!problems.isEmpty())
        {
            throw new ServiceException("存在" + String.join("、", problems) + "，请先完成复核/审核后再生成支付计划");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrSubmit(PaymentPlanGenerateReq req)
    {
        LocalDate period = parseBusinessPeriod(req.getBusinessPeriod());
        Date now = new Date();
        String operatorName = resolveOperatorName();
        String username = SecurityUtils.getUsername();
        String targetStatus = normalizeTargetStatus(req.getTargetStatus());

        if (!TYPE_NORMAL.equals(req.getDeterminationType()))
        {
            throw new ServiceException("二次发放暂未实现");
        }

        PaymentPlan plan = req.getPlanId() == null ? null : paymentPlanMapper.selectById(req.getPlanId());
        String previousStatus = null;
        if (plan == null)
        {
            assertNoDuplicateActivePlan(req.getDeterminationType(), period, req.getSubsidyType(), null);
            plan = new PaymentPlan();
            plan.setDeterminationType(req.getDeterminationType());
            plan.setBusinessPeriod(period);
            plan.setSubsidyType(req.getSubsidyType());
            plan.setDelFlag("0");
            plan.setCreateBy(username);
            plan.setCreateTime(now);
            previousStatus = null;
        }
        else
        {
            previousStatus = plan.getApprovalStatus();
            if (!SAVE_ALLOWED.contains(previousStatus))
            {
                throw new ServiceException("当前状态不允许保存或重算");
            }
            if (FINANCE_APPROVED.equals(plan.getFinanceStatus()))
            {
                throw new ServiceException("财务已通过，不允许重算");
            }
            if (!Objects.equals(plan.getBusinessPeriod(), period)
                    || !Objects.equals(plan.getDeterminationType(), req.getDeterminationType())
                    || !Objects.equals(plan.getSubsidyType(), req.getSubsidyType()))
            {
                throw new ServiceException("仅支持在原业务期、补贴类型和核定方式下重算保存");
            }
            assertNoDuplicateActivePlan(req.getDeterminationType(), period, req.getSubsidyType(), plan.getId());
            // 重算换绑：先释放本计划已纳入的补发
            releaseIncludedSupplements(plan.getId());
        }

        PaymentPlanPreviewReq previewReq = new PaymentPlanPreviewReq();
        BeanUtils.copyProperties(req, previewReq);
        previewReq.setExcludePlanId(plan.getId());
        PaymentPlanPreviewResp preview = preview(previewReq);
        if (preview.getDetailList().isEmpty())
        {
            throw new ServiceException("没有可保存的支付计划数据");
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
            if (STATUS_FINANCE_REJECTED.equals(previousStatus) || StringUtils.isNotEmpty(plan.getFinanceStatus()))
            {
                clearFinanceStatus(plan.getId());
            }
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
            row.setSubsidyPersonId(item.getSubsidyPersonId());
            row.setSubsidyType(item.getSubsidyType());
            row.setStreetName(item.getStreetName());
            row.setVillageName(item.getVillageName());
            row.setPersonName(item.getPersonName());
            row.setIdCardNo(item.getIdCardNo());
            row.setBusinessPeriod(period);
            row.setPaymentMonth(String.format("%04d-%02d", period.getYear(), period.getMonthValue()));
            row.setMonthlyAmount(nz(item.getMonthlyAmount()));
            row.setSupplementAmount(nz(item.getSupplementAmount()));
            row.setSupplementStartMonth(parseYearMonthDay(item.getSupplementStartMonth()));
            row.setSupplementEndMonth(parseYearMonthDay(item.getSupplementEndMonth()));
            row.setSupplementSource(item.getSupplementSource());
            row.setSupplementSourceId(item.getSupplementSourceId());
            row.setDistributionAmount(nz(item.getDistributionAmount()));
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
        claimSupplementsAfterInsert(persistedPlanId);

        if (!Objects.equals(previousStatus, targetStatus))
        {
            insertAudit(plan.getId(), targetStatus, req.getRemark(), AUDIT_STAGE_SUBSIDY);
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
            if (STATUS_FINANCE_REJECTED.equals(current))
            {
                clearFinanceStatus(planId);
            }
            insertAudit(planId, target, req.getRemark(), AUDIT_STAGE_SUBSIDY);
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int revoke(Long planId)
    {
        PaymentPlan plan = paymentPlanMapper.selectById(planId);
        if (plan == null || !"0".equals(plan.getDelFlag()))
        {
            throw new ServiceException("支付计划不存在");
        }
        String status = plan.getApprovalStatus();
        boolean canRevoke = STATUS_DRAFT.equals(status)
                || STATUS_REVIEW_REJECTED.equals(status)
                || STATUS_APPROVE_REJECTED.equals(status)
                || STATUS_FINANCE_REJECTED.equals(status)
                || (STATUS_PENDING_REVIEW.equals(status) && StringUtils.isEmpty(plan.getFinanceStatus()));
        if (!canRevoke)
        {
            throw new ServiceException("当前状态不可撤销，仅草稿/驳回/待复核(未进财务)可撤销");
        }
        releaseIncludedSupplements(planId);
        paymentPlanDetailMapper.deleteByPlanId(planId);
        paymentPlanSummaryMapper.deleteByPlanId(planId);
        paymentPlanAuditMapper.deleteByPlanId(planId);
        return paymentPlanMapper.deleteById(planId);
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

    @Override
    public List<PaymentPlanDetailExportResp> selectDetailExportByPlanId(Long planId)
    {
        PaymentPlan plan = paymentPlanMapper.selectById(planId);
        if (plan == null || !"0".equals(plan.getDelFlag()))
        {
            throw new ServiceException("支付计划不存在");
        }
        List<PaymentPlanDetailResp> details = paymentPlanDetailMapper.selectListByPlanId(planId);
        return details.stream().map(item -> {
            PaymentPlanDetailExportResp row = new PaymentPlanDetailExportResp();
            BeanUtils.copyProperties(item, row);
            return row;
        }).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitFinanceUpload(Long planId)
    {
        PaymentPlan plan = paymentPlanMapper.selectById(planId);
        if (plan == null || !"0".equals(plan.getDelFlag()))
        {
            throw new ServiceException("支付计划不存在");
        }
        if (!STATUS_APPROVED.equals(plan.getApprovalStatus()))
        {
            throw new ServiceException("仅审批通过的支付计划可上传财务");
        }
        if (StringUtils.isNotEmpty(plan.getFinanceStatus()))
        {
            throw new ServiceException("已存在财务状态，无需重复上传");
        }
        PaymentPlan upd = new PaymentPlan();
        upd.setId(planId);
        upd.setFinanceStatus(FINANCE_PENDING);
        upd.setUpdateBy(SecurityUtils.getUsername());
        upd.setUpdateTime(new Date());
        int rows = paymentPlanMapper.updateById(upd);
        if (rows > 0)
        {
            insertAudit(planId, FINANCE_PENDING, null, AUDIT_STAGE_FINANCE);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int financePass(Long planId, PaymentPlanFinanceStatusChangeReq req)
    {
        return changeFinanceStatus(planId, FINANCE_PENDING, FINANCE_PENDING_REVIEW, req, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int financeReject(Long planId, PaymentPlanFinanceStatusChangeReq req)
    {
        return changeFinanceStatus(planId, FINANCE_PENDING, FINANCE_REJECTED, req, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int financeReviewPass(Long planId, PaymentPlanFinanceStatusChangeReq req)
    {
        return changeFinanceStatus(planId, FINANCE_PENDING_REVIEW, FINANCE_PENDING_APPROVE, req, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int financeReviewReject(Long planId, PaymentPlanFinanceStatusChangeReq req)
    {
        return changeFinanceStatus(planId, FINANCE_PENDING_REVIEW, FINANCE_REJECTED, req, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int financeApprovePass(Long planId, PaymentPlanFinanceStatusChangeReq req)
    {
        int rows = changeFinanceStatus(planId, FINANCE_PENDING_APPROVE, FINANCE_APPROVED, req, false);
        if (rows > 0)
        {
            markSupplementsPaid(planId);
            stampDistributionDate(planId);
        }
        return rows;
    }

    /** 财务通过：写入明细发放日期（当日） */
    private void stampDistributionDate(Long planId)
    {
        LocalDate today = LocalDate.now();
        paymentPlanDetailMapper.update(null, new LambdaUpdateWrapper<PaymentPlanDetail>()
                .eq(PaymentPlanDetail::getPlanId, planId)
                .eq(PaymentPlanDetail::getDelFlag, "0")
                .isNull(PaymentPlanDetail::getDistributionDate)
                .set(PaymentPlanDetail::getDistributionDate, today)
                .set(PaymentPlanDetail::getUpdateBy, SecurityUtils.getUsername())
                .set(PaymentPlanDetail::getUpdateTime, new Date()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int financeApproveReject(Long planId, PaymentPlanFinanceStatusChangeReq req)
    {
        return changeFinanceStatus(planId, FINANCE_PENDING_APPROVE, FINANCE_REJECTED, req, true);
    }

    @Override
    public PaymentPlan getBankPlan(Long planId)
    {
        PaymentPlan plan = paymentPlanMapper.selectById(planId);
        if (plan == null || !"0".equals(plan.getDelFlag()))
        {
            throw new ServiceException("支付计划不存在");
        }
        return plan;
    }

    @Override
    public List<PaymentPlanDetail> selectBankExportDetails(Long planId)
    {
        return loadBankDetails(planId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitToBank(Long planId)
    {
        PaymentPlan plan = getBankPlan(planId);
        if (!FINANCE_APPROVED.equals(plan.getFinanceStatus()))
        {
            throw new ServiceException("仅财务已通过的批次可提交银行");
        }
        if (DIST_SUBMITTED.equals(plan.getDistributionStatus()) || DIST_COMPLETED.equals(plan.getDistributionStatus()))
        {
            throw new ServiceException("该批次已提交银行，无需重复提交");
        }
        PaymentPlan upd = new PaymentPlan();
        upd.setId(planId);
        upd.setDistributionStatus(DIST_SUBMITTED);
        upd.setUpdateBy(SecurityUtils.getUsername());
        upd.setUpdateTime(new Date());
        return paymentPlanMapper.updateById(upd);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importBankFailures(Long planId, List<PaymentPlanBankFailureRow> rows)
    {
        PaymentPlan plan = getBankPlan(planId);
        if (!DIST_SUBMITTED.equals(plan.getDistributionStatus()))
        {
            throw new ServiceException("仅已提交银行的批次可导入失败数据");
        }
        if (rows == null || rows.isEmpty())
        {
            throw new ServiceException("导入数据为空");
        }
        int matched = 0;
        for (PaymentPlanBankFailureRow row : rows)
        {
            if (row == null || StringUtils.isEmpty(row.getIdCardNo()))
            {
                continue;
            }
            matched += paymentPlanDetailMapper.markFailedByIdCard(planId, row.getIdCardNo().trim(),
                    StringUtils.isEmpty(row.getReason()) ? "银行发放失败" : row.getReason().trim());
        }
        return matched;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int completeDistribution(Long planId)
    {
        PaymentPlan plan = getBankPlan(planId);
        if (!DIST_SUBMITTED.equals(plan.getDistributionStatus()))
        {
            throw new ServiceException("仅已提交银行的批次可标记已完成");
        }
        paymentPlanDetailMapper.markRemainingSuccess(planId);
        BigDecimal successAmount = paymentPlanDetailMapper.sumSuccessAmountByPlanId(planId);
        if (successAmount == null)
        {
            successAmount = BigDecimal.ZERO;
        }
        if (successAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            financeAccountService.deductForSubsidyDistribution(plan.getSubsidyType(), planId, plan.getBatchNo(), successAmount);
        }
        PaymentPlan upd = new PaymentPlan();
        upd.setId(planId);
        upd.setDistributionStatus(DIST_COMPLETED);
        upd.setUpdateBy(SecurityUtils.getUsername());
        upd.setUpdateTime(new Date());
        return paymentPlanMapper.updateById(upd);
    }

    private List<PaymentPlanDetail> loadBankDetails(Long planId)
    {
        return paymentPlanDetailMapper.selectList(new LambdaQueryWrapper<PaymentPlanDetail>()
                .eq(PaymentPlanDetail::getPlanId, planId)
                .eq(PaymentPlanDetail::getDelFlag, "0")
                .orderByAsc(PaymentPlanDetail::getId));
    }

    private int changeFinanceStatus(Long planId, String expectedCurrent, String target,
                                    PaymentPlanFinanceStatusChangeReq req, boolean remarkRequired)
    {
        PaymentPlan plan = paymentPlanMapper.selectById(planId);
        if (plan == null || !"0".equals(plan.getDelFlag()))
        {
            throw new ServiceException("支付计划不存在");
        }
        if (!expectedCurrent.equals(plan.getFinanceStatus()))
        {
            throw new ServiceException("当前财务状态不支持该操作");
        }
        String remark = req == null ? null : req.getRemark();
        if (remarkRequired && (remark == null || remark.isBlank()))
        {
            throw new ServiceException("驳回时备注必填");
        }
        PaymentPlan upd = new PaymentPlan();
        upd.setId(planId);
        upd.setFinanceStatus(target);
        if (remarkRequired)
        {
            // 财务驳回：业务审批退回可再提交态
            upd.setApprovalStatus(STATUS_FINANCE_REJECTED);
        }
        upd.setUpdateBy(SecurityUtils.getUsername());
        upd.setUpdateTime(new Date());
        int rows = paymentPlanMapper.updateById(upd);
        if (rows > 0)
        {
            insertAudit(planId, target, remark, AUDIT_STAGE_FINANCE);
        }
        return rows;
    }

    /** 清空财务状态，便于重新走上传财务 */
    private void clearFinanceStatus(Long planId)
    {
        paymentPlanMapper.update(null, new LambdaUpdateWrapper<PaymentPlan>()
                .eq(PaymentPlan::getId, planId)
                .set(PaymentPlan::getFinanceStatus, null)
                .set(PaymentPlan::getUpdateBy, SecurityUtils.getUsername())
                .set(PaymentPlan::getUpdateTime, new Date()));
    }

    private void validateReq(String determinationType, String businessPeriod, String subsidyType)
    {
        if (determinationType == null || determinationType.isBlank())
        {
            throw new ServiceException("请选择核定方式");
        }
        if (businessPeriod == null || businessPeriod.isBlank())
        {
            throw new ServiceException("请选择业务期");
        }
        if (subsidyType == null || subsidyType.isBlank())
        {
            throw new ServiceException("请选择补贴类型");
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
                STATUS_APPROVE_REJECTED,
                STATUS_FINANCE_REJECTED).contains(status))
        {
            return status;
        }
        throw new ServiceException("不支持的状态变更");
    }

    private void validateTransition(String current, String target)
    {
        if (STATUS_PENDING_REVIEW.equals(target) && SUBMIT_ALLOWED.contains(current))
        {
            return; // 提交（含财务驳回后重新提交）
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

    private void insertAudit(Long planId, String operationStatus, String remark, String approvalStage)
    {
        PaymentPlanAudit audit = new PaymentPlanAudit();
        audit.setPlanId(planId);
        audit.setOperationStatus(operationStatus);
        audit.setApprovalStage(approvalStage);
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

    private void assertNoDuplicateActivePlan(String determinationType, LocalDate businessPeriod,
                                             String subsidyType, Long excludePlanId)
    {
        int count = paymentPlanMapper.countActivePlan(determinationType, businessPeriod, subsidyType, excludePlanId);
        if (count > 0)
        {
            throw new ServiceException("该业务期下该补贴类型已存在支付计划，不可重复生成");
        }
    }

    /**
     * 预览/落库共用：按人+补贴类型选取一笔未发补发（核定优先 → 起始最早 → create_time/id）。
     */
    private void attachSupplementCandidates(List<PaymentPlanDetailResp> details)
    {
        if (details == null || details.isEmpty())
        {
            return;
        }
        for (PaymentPlanDetailResp detail : details)
        {
            BigDecimal monthly = nz(detail.getMonthlyAmount());
            detail.setMonthlyAmount(monthly);
            detail.setSupplementAmount(BigDecimal.ZERO);
            detail.setSupplementStartMonth(null);
            detail.setSupplementEndMonth(null);
            detail.setSupplementSource(null);
            detail.setSupplementSourceId(null);
            detail.setDistributionAmount(monthly);
        }

        Set<String> idCards = details.stream()
                .map(PaymentPlanDetailResp::getIdCardNo)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (idCards.isEmpty())
        {
            return;
        }
        String subsidyType = details.get(0).getSubsidyType();
        if (StringUtils.isEmpty(subsidyType))
        {
            return;
        }

        Set<Long> planDeterminationIds = details.stream()
                .map(PaymentPlanDetailResp::getDeterminationId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> personIdToCard = new HashMap<>();
        if (!planDeterminationIds.isEmpty())
        {
            List<BenefitDetermination> planDets = benefitDeterminationMapper.selectBatchIds(planDeterminationIds);
            Map<Long, String> detIdToDetailCard = details.stream()
                    .filter(d -> d.getDeterminationId() != null && StringUtils.isNotEmpty(d.getIdCardNo()))
                    .collect(Collectors.toMap(PaymentPlanDetailResp::getDeterminationId,
                            PaymentPlanDetailResp::getIdCardNo, (a, b) -> a));
            for (BenefitDetermination det : planDets)
            {
                if (det.getSubsidyPersonId() == null)
                {
                    continue;
                }
                String card = firstNonBlank(det.getIdCardNo(), detIdToDetailCard.get(det.getId()));
                if (StringUtils.isNotEmpty(card))
                {
                    personIdToCard.put(det.getSubsidyPersonId(), card);
                }
            }
        }

        LambdaQueryWrapper<BenefitDetermination> detQw = new LambdaQueryWrapper<BenefitDetermination>()
                .eq(BenefitDetermination::getApprovalStatus, STATUS_APPROVED)
                .eq(BenefitDetermination::getDelFlag, "0");
        if (personIdToCard.isEmpty())
        {
            detQw.in(BenefitDetermination::getIdCardNo, idCards);
        }
        else
        {
            Set<Long> personIds = personIdToCard.keySet();
            detQw.and(w -> w.in(BenefitDetermination::getIdCardNo, idCards)
                    .or()
                    .in(BenefitDetermination::getSubsidyPersonId, personIds));
        }
        List<BenefitDetermination> determinations = benefitDeterminationMapper.selectList(detQw);
        if (determinations.isEmpty())
        {
            return;
        }

        Map<Long, String> determinationIdToCard = new HashMap<>();
        for (BenefitDetermination det : determinations)
        {
            String card = firstNonBlank(det.getIdCardNo(),
                    det.getSubsidyPersonId() == null ? null : personIdToCard.get(det.getSubsidyPersonId()));
            if (StringUtils.isNotEmpty(card) && idCards.contains(card))
            {
                determinationIdToCard.put(det.getId(), card);
            }
        }
        if (determinationIdToCard.isEmpty())
        {
            return;
        }

        List<BenefitDeterminationItem> relatedItems = benefitDeterminationItemMapper.selectList(
                new LambdaQueryWrapper<BenefitDeterminationItem>()
                        .in(BenefitDeterminationItem::getDeterminationId, determinationIdToCard.keySet())
                        .eq(BenefitDeterminationItem::getSubsidyType, subsidyType)
                        .eq(BenefitDeterminationItem::getDelFlag, "0"));
        if (relatedItems.isEmpty())
        {
            return;
        }

        Map<String, List<SupplementCandidate>> candidatesByCard = new HashMap<>();
        for (BenefitDeterminationItem item : relatedItems)
        {
            String card = determinationIdToCard.get(item.getDeterminationId());
            if (StringUtils.isEmpty(card))
            {
                continue;
            }
            if (isUnpaidSupplementStatus(item.getSupplementPayStatus())
                    && item.getBenefitAmount() != null
                    && item.getBenefitAmount().compareTo(BigDecimal.ZERO) > 0)
            {
                YearMonth start = determinationSupplementStart(item);
                YearMonth end = determinationSupplementEnd(item, start);
                candidatesByCard.computeIfAbsent(card, k -> new ArrayList<>()).add(new SupplementCandidate(
                        SUPPLEMENT_SRC_DETERMINATION,
                        item.getId(),
                        item.getBenefitAmount(),
                        start,
                        end,
                        item.getCreateTime()));
            }
        }

        Set<Long> relatedItemIds = relatedItems.stream().map(BenefitDeterminationItem::getId).collect(Collectors.toSet());
        Map<Long, String> itemIdToCard = relatedItems.stream()
                .filter(i -> determinationIdToCard.containsKey(i.getDeterminationId()))
                .collect(Collectors.toMap(BenefitDeterminationItem::getId,
                        i -> determinationIdToCard.get(i.getDeterminationId()), (a, b) -> a));
        if (!relatedItemIds.isEmpty())
        {
            List<BenefitResumeItem> resumeItems = benefitResumeItemMapper.selectList(
                    new LambdaQueryWrapper<BenefitResumeItem>()
                            .in(BenefitResumeItem::getDeterminationItemId, relatedItemIds)
                            .eq(BenefitResumeItem::getSubsidyType, subsidyType)
                            .eq(BenefitResumeItem::getDelFlag, "0")
                            .gt(BenefitResumeItem::getSupplementAmount, BigDecimal.ZERO));
            for (BenefitResumeItem item : resumeItems)
            {
                if (!isUnpaidSupplementStatus(item.getSupplementPayStatus()))
                {
                    continue;
                }
                String card = itemIdToCard.get(item.getDeterminationItemId());
                if (StringUtils.isEmpty(card))
                {
                    continue;
                }
                candidatesByCard.computeIfAbsent(card, k -> new ArrayList<>()).add(new SupplementCandidate(
                        SUPPLEMENT_SRC_RESUME,
                        item.getId(),
                        item.getSupplementAmount(),
                        toYearMonth(item.getSupplementStartMonth()),
                        toYearMonth(item.getSupplementEndMonth()),
                        item.getCreateTime()));
            }
        }

        for (List<SupplementCandidate> list : candidatesByCard.values())
        {
            list.sort(PaymentPlanServiceImpl::compareSupplementCandidates);
        }

        Set<String> usedKeys = new HashSet<>();
        for (PaymentPlanDetailResp detail : details)
        {
            if (StringUtils.isEmpty(detail.getIdCardNo()))
            {
                continue;
            }
            List<SupplementCandidate> list = candidatesByCard.get(detail.getIdCardNo());
            if (list == null || list.isEmpty())
            {
                continue;
            }
            SupplementCandidate chosen = null;
            for (SupplementCandidate c : list)
            {
                String key = c.source + ":" + c.sourceId;
                if (!usedKeys.contains(key))
                {
                    chosen = c;
                    usedKeys.add(key);
                    break;
                }
            }
            if (chosen == null)
            {
                continue;
            }
            BigDecimal monthly = nz(detail.getMonthlyAmount());
            BigDecimal supplement = nz(chosen.amount);
            detail.setSupplementAmount(supplement);
            detail.setSupplementStartMonth(formatYearMonth(chosen.startMonth));
            detail.setSupplementEndMonth(formatYearMonth(chosen.endMonth));
            detail.setSupplementSource(chosen.source);
            detail.setSupplementSourceId(chosen.sourceId);
            detail.setDistributionAmount(monthly.add(supplement));
        }
    }

    private void claimSupplementsAfterInsert(Long planId)
    {
        List<PaymentPlanDetail> details = paymentPlanDetailMapper.selectList(
                new LambdaQueryWrapper<PaymentPlanDetail>()
                        .eq(PaymentPlanDetail::getPlanId, planId)
                        .eq(PaymentPlanDetail::getDelFlag, "0"));
        boolean anyDropped = false;
        Date now = new Date();
        String username = SecurityUtils.getUsername();
        for (PaymentPlanDetail detail : details)
        {
            if (detail.getSupplementSourceId() == null || StringUtils.isEmpty(detail.getSupplementSource()))
            {
                continue;
            }
            boolean claimed = false;
            if (SUPPLEMENT_SRC_DETERMINATION.equals(detail.getSupplementSource()))
            {
                claimed = benefitDeterminationItemMapper.update(null, new LambdaUpdateWrapper<BenefitDeterminationItem>()
                        .eq(BenefitDeterminationItem::getId, detail.getSupplementSourceId())
                        .and(w -> w.eq(BenefitDeterminationItem::getSupplementPayStatus, SUPPLEMENT_UNPAID)
                                .or()
                                .isNull(BenefitDeterminationItem::getSupplementPayStatus))
                        .set(BenefitDeterminationItem::getSupplementPayStatus, SUPPLEMENT_INCLUDED)
                        .set(BenefitDeterminationItem::getSupplementPlanId, planId)
                        .set(BenefitDeterminationItem::getSupplementDetailId, detail.getId())
                        .set(BenefitDeterminationItem::getUpdateBy, username)
                        .set(BenefitDeterminationItem::getUpdateTime, LocalDateTime.now())) > 0;
            }
            else if (SUPPLEMENT_SRC_RESUME.equals(detail.getSupplementSource()))
            {
                claimed = benefitResumeItemMapper.update(null, new LambdaUpdateWrapper<BenefitResumeItem>()
                        .eq(BenefitResumeItem::getId, detail.getSupplementSourceId())
                        .and(w -> w.eq(BenefitResumeItem::getSupplementPayStatus, SUPPLEMENT_UNPAID)
                                .or()
                                .isNull(BenefitResumeItem::getSupplementPayStatus))
                        .set(BenefitResumeItem::getSupplementPayStatus, SUPPLEMENT_INCLUDED)
                        .set(BenefitResumeItem::getSupplementPlanId, planId)
                        .set(BenefitResumeItem::getSupplementDetailId, detail.getId())
                        .set(BenefitResumeItem::getUpdateBy, username)
                        .set(BenefitResumeItem::getUpdateTime, LocalDateTime.now())) > 0;
            }
            if (!claimed)
            {
                anyDropped = true;
                paymentPlanDetailMapper.update(null, new LambdaUpdateWrapper<PaymentPlanDetail>()
                        .eq(PaymentPlanDetail::getId, detail.getId())
                        .set(PaymentPlanDetail::getSupplementAmount, BigDecimal.ZERO)
                        .set(PaymentPlanDetail::getSupplementStartMonth, null)
                        .set(PaymentPlanDetail::getSupplementEndMonth, null)
                        .set(PaymentPlanDetail::getSupplementSource, null)
                        .set(PaymentPlanDetail::getSupplementSourceId, null)
                        .set(PaymentPlanDetail::getDistributionAmount, nz(detail.getMonthlyAmount()))
                        .set(PaymentPlanDetail::getUpdateBy, username)
                        .set(PaymentPlanDetail::getUpdateTime, now));
            }
        }
        if (anyDropped)
        {
            refreshPlanAggregates(planId, username, now);
        }
    }

    private void releaseIncludedSupplements(Long planId)
    {
        if (planId == null)
        {
            return;
        }
        String username = SecurityUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();
        benefitDeterminationItemMapper.update(null, new LambdaUpdateWrapper<BenefitDeterminationItem>()
                .eq(BenefitDeterminationItem::getSupplementPlanId, planId)
                .eq(BenefitDeterminationItem::getSupplementPayStatus, SUPPLEMENT_INCLUDED)
                .set(BenefitDeterminationItem::getSupplementPayStatus, SUPPLEMENT_UNPAID)
                .set(BenefitDeterminationItem::getSupplementPlanId, null)
                .set(BenefitDeterminationItem::getSupplementDetailId, null)
                .set(BenefitDeterminationItem::getUpdateBy, username)
                .set(BenefitDeterminationItem::getUpdateTime, now));
        benefitResumeItemMapper.update(null, new LambdaUpdateWrapper<BenefitResumeItem>()
                .eq(BenefitResumeItem::getSupplementPlanId, planId)
                .eq(BenefitResumeItem::getSupplementPayStatus, SUPPLEMENT_INCLUDED)
                .set(BenefitResumeItem::getSupplementPayStatus, SUPPLEMENT_UNPAID)
                .set(BenefitResumeItem::getSupplementPlanId, null)
                .set(BenefitResumeItem::getSupplementDetailId, null)
                .set(BenefitResumeItem::getUpdateBy, username)
                .set(BenefitResumeItem::getUpdateTime, now));
    }

    private void markSupplementsPaid(Long planId)
    {
        if (planId == null)
        {
            return;
        }
        String username = SecurityUtils.getUsername();
        LocalDateTime now = LocalDateTime.now();
        benefitDeterminationItemMapper.update(null, new LambdaUpdateWrapper<BenefitDeterminationItem>()
                .eq(BenefitDeterminationItem::getSupplementPlanId, planId)
                .eq(BenefitDeterminationItem::getSupplementPayStatus, SUPPLEMENT_INCLUDED)
                .set(BenefitDeterminationItem::getSupplementPayStatus, SUPPLEMENT_PAID)
                .set(BenefitDeterminationItem::getUpdateBy, username)
                .set(BenefitDeterminationItem::getUpdateTime, now));
        benefitResumeItemMapper.update(null, new LambdaUpdateWrapper<BenefitResumeItem>()
                .eq(BenefitResumeItem::getSupplementPlanId, planId)
                .eq(BenefitResumeItem::getSupplementPayStatus, SUPPLEMENT_INCLUDED)
                .set(BenefitResumeItem::getSupplementPayStatus, SUPPLEMENT_PAID)
                .set(BenefitResumeItem::getUpdateBy, username)
                .set(BenefitResumeItem::getUpdateTime, now));
    }

    private void refreshPlanAggregates(Long planId, String username, Date now)
    {
        List<PaymentPlanDetail> details = paymentPlanDetailMapper.selectList(
                new LambdaQueryWrapper<PaymentPlanDetail>()
                        .eq(PaymentPlanDetail::getPlanId, planId)
                        .eq(PaymentPlanDetail::getDelFlag, "0"));
        BigDecimal totalAmount = details.stream()
                .map(d -> nz(d.getDistributionAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        PaymentPlan planUpd = new PaymentPlan();
        planUpd.setId(planId);
        planUpd.setTotalCount(details.size());
        planUpd.setTotalAmount(totalAmount);
        planUpd.setUpdateBy(username);
        planUpd.setUpdateTime(now);
        paymentPlanMapper.updateById(planUpd);

        paymentPlanSummaryMapper.deleteByPlanId(planId);
        Map<String, List<PaymentPlanDetail>> grouped = details.stream()
                .collect(Collectors.groupingBy(d -> defaultValue(d.getSubsidyType()) + "||" + defaultValue(d.getGrantOrg())));
        List<PaymentPlanSummary> summaryRows = new ArrayList<>();
        for (List<PaymentPlanDetail> group : grouped.values())
        {
            PaymentPlanDetail first = group.get(0);
            PaymentPlanSummary row = new PaymentPlanSummary();
            row.setPlanId(planId);
            row.setBusinessPeriod(first.getBusinessPeriod());
            row.setSubsidyType(first.getSubsidyType());
            row.setGrantOrg(first.getGrantOrg());
            row.setTotalCount(group.size());
            row.setTotalAmount(group.stream()
                    .map(d -> nz(d.getDistributionAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            row.setDelFlag("0");
            row.setCreateBy(username);
            row.setCreateTime(now);
            row.setUpdateBy(username);
            row.setUpdateTime(now);
            summaryRows.add(row);
        }
        if (!summaryRows.isEmpty())
        {
            paymentPlanSummaryMapper.batchInsert(summaryRows);
        }
    }

    private static boolean isUnpaidSupplementStatus(String status)
    {
        return status == null || status.isBlank() || SUPPLEMENT_UNPAID.equals(status);
    }

    private static YearMonth determinationSupplementStart(BenefitDeterminationItem item)
    {
        if (item.getBenefitStartYear() == null || item.getBenefitStartMonth() == null)
        {
            return null;
        }
        return YearMonth.of(item.getBenefitStartYear(), item.getBenefitStartMonth());
    }

    private static YearMonth determinationSupplementEnd(BenefitDeterminationItem item, YearMonth start)
    {
        if (start == null)
        {
            return null;
        }
        int months = item.getBenefitMonths() == null ? 0 : item.getBenefitMonths();
        if (months <= 0)
        {
            return start;
        }
        return start.plusMonths(months - 1L);
    }

    private static YearMonth toYearMonth(Date date)
    {
        if (date == null)
        {
            return null;
        }
        if (date instanceof java.sql.Date)
        {
            return YearMonth.from(((java.sql.Date) date).toLocalDate());
        }
        return YearMonth.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    private static String formatYearMonth(YearMonth ym)
    {
        return ym == null ? null : ym.toString();
    }

    private static LocalDate parseYearMonthDay(String yearMonth)
    {
        if (StringUtils.isEmpty(yearMonth))
        {
            return null;
        }
        return YearMonth.parse(yearMonth).atDay(1);
    }

    private static BigDecimal nz(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static String firstNonBlank(String a, String b)
    {
        if (StringUtils.isNotEmpty(a))
        {
            return a;
        }
        return StringUtils.isNotEmpty(b) ? b : null;
    }

    private static final class SupplementCandidate
    {
        private final String source;
        private final Long sourceId;
        private final BigDecimal amount;
        private final YearMonth startMonth;
        private final YearMonth endMonth;
        private final LocalDateTime createTime;

        private SupplementCandidate(String source, Long sourceId, BigDecimal amount,
                                    YearMonth startMonth, YearMonth endMonth,
                                    LocalDateTime createTime)
        {
            this.source = source;
            this.sourceId = sourceId;
            this.amount = amount;
            this.startMonth = startMonth;
            this.endMonth = endMonth;
            this.createTime = createTime;
        }
    }

    private static int compareSupplementCandidates(SupplementCandidate a, SupplementCandidate b)
    {
        int bySource = Integer.compare(
                SUPPLEMENT_SRC_DETERMINATION.equals(a.source) ? 0 : 1,
                SUPPLEMENT_SRC_DETERMINATION.equals(b.source) ? 0 : 1);
        if (bySource != 0)
        {
            return bySource;
        }
        YearMonth aStart = a.startMonth == null ? YearMonth.of(9999, 12) : a.startMonth;
        YearMonth bStart = b.startMonth == null ? YearMonth.of(9999, 12) : b.startMonth;
        int byStart = aStart.compareTo(bStart);
        if (byStart != 0)
        {
            return byStart;
        }
        LocalDateTime aTime = a.createTime == null ? LocalDateTime.MAX : a.createTime;
        LocalDateTime bTime = b.createTime == null ? LocalDateTime.MAX : b.createTime;
        int byTime = aTime.compareTo(bTime);
        if (byTime != 0)
        {
            return byTime;
        }
        long aId = a.sourceId == null ? Long.MAX_VALUE : a.sourceId;
        long bId = b.sourceId == null ? Long.MAX_VALUE : b.sourceId;
        return Long.compare(aId, bId);
    }
}
