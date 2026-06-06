package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.PaymentPlan;
import com.ruoyi.shebao.domain.PaymentPlanAudit;
import com.ruoyi.shebao.domain.PaymentPlanDetail;
import com.ruoyi.shebao.domain.PaymentPlanSummary;
import com.ruoyi.shebao.dto.*;
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
    /** 银行标识 */
    public static final String BANK_LANGFANG = "langfang";
    public static final String BANK_BOC = "boc";
    private static final String GRANT_ORG_DICT = "shebao_grant_org";

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
        List<PaymentPlanDetailResp> details = paymentPlanDetailMapper.selectPreviewDetails(businessPeriod, req.getSubsidyType());
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
            plan.setSubsidyType(req.getSubsidyType());
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
            if (!Objects.equals(plan.getBusinessPeriod(), period)
                    || !Objects.equals(plan.getDeterminationType(), req.getDeterminationType())
                    || !Objects.equals(plan.getSubsidyType(), req.getSubsidyType()))
            {
                throw new ServiceException("仅支持在原业务期、补贴类型和核定方式下重算保存");
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
            insertAudit(planId, target, req.getRemark(), AUDIT_STAGE_SUBSIDY);
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
        return changeFinanceStatus(planId, FINANCE_PENDING_APPROVE, FINANCE_APPROVED, req, false);
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
    public List<String> selectAvailableBanks(Long planId)
    {
        List<PaymentPlanDetail> details = loadBankDetails(planId);
        Set<String> banks = new LinkedHashSet<>();
        for (PaymentPlanDetail d : details)
        {
            String bank = classifyBank(d.getGrantOrg());
            if (bank != null)
            {
                banks.add(bank);
            }
        }
        return new ArrayList<>(banks);
    }

    @Override
    public List<PaymentPlanDetail> selectDetailsForBank(Long planId, String bank)
    {
        List<PaymentPlanDetail> details = loadBankDetails(planId);
        List<PaymentPlanDetail> result = new ArrayList<>();
        for (PaymentPlanDetail d : details)
        {
            if (Objects.equals(bank, classifyBank(d.getGrantOrg())))
            {
                result.add(d);
            }
        }
        return result;
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

    /** 根据发放机构编码判断属于哪家代发银行（编码优先，字典标签兜底） */
    private String classifyBank(String grantOrgCode)
    {
        if (StringUtils.isEmpty(grantOrgCode))
        {
            return null;
        }
        // 字典编码：langfang_bank=廊坊银行, china_bank=中国银行
        if ("langfang_bank".equalsIgnoreCase(grantOrgCode))
        {
            return BANK_LANGFANG;
        }
        if ("china_bank".equalsIgnoreCase(grantOrgCode))
        {
            return BANK_BOC;
        }
        // 兜底：按字典标签文本判断
        String label = DictUtils.getDictLabel(GRANT_ORG_DICT, grantOrgCode);
        String text = (label == null ? "" : label) + "|" + grantOrgCode;
        if (text.contains("廊坊"))
        {
            return BANK_LANGFANG;
        }
        if (text.contains("中国银行") || text.toLowerCase(Locale.ROOT).contains("boc"))
        {
            return BANK_BOC;
        }
        return null;
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
        upd.setUpdateBy(SecurityUtils.getUsername());
        upd.setUpdateTime(new Date());
        int rows = paymentPlanMapper.updateById(upd);
        if (rows > 0)
        {
            insertAudit(planId, target, remark, AUDIT_STAGE_FINANCE);
        }
        return rows;
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
}
