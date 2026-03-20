package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.DistributionBatch;
import com.ruoyi.shebao.domain.SubsidyDistribution;
import com.ruoyi.shebao.dto.DistributionBatchListReq;
import com.ruoyi.shebao.dto.SubsidyDistributionListReq;
import com.ruoyi.shebao.dto.SubsidyDistributionListResp;
import com.ruoyi.shebao.mapper.DistributionBatchMapper;
import com.ruoyi.shebao.mapper.SubsidyDistributionMapper;
import com.ruoyi.shebao.service.ApprovalLogService;
import com.ruoyi.shebao.service.IDistributionBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 发放批次Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@Service
public class DistributionBatchServiceImpl extends ServiceImpl<DistributionBatchMapper, DistributionBatch> implements IDistributionBatchService
{
    @Autowired
    private SubsidyDistributionMapper subsidyDistributionMapper;

    @Autowired
    private ApprovalLogService approvalLogService;

    /**
     * 查询发放批次列表
     *
     * @param req 查询条件
     * @return 发放批次列表
     */
    @Override
    public Page<DistributionBatch> selectDistributionBatchList(DistributionBatchListReq req)
    {
        Page<DistributionBatch> page = new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault());

        var query = this.lambdaQuery()
                .like(StringUtils.isNotBlank(req.getBatchNo()), DistributionBatch::getBatchNo, req.getBatchNo())
                .eq(StringUtils.isNotBlank(req.getSubsidyType()), DistributionBatch::getSubsidyType, req.getSubsidyType())
                .eq(req.getBatchType() != null, DistributionBatch::getBatchType, req.getBatchType())
                .eq(StringUtils.isNotBlank(req.getStatus()), DistributionBatch::getStatus, req.getStatus())
                .eq(DistributionBatch::getDelFlag, "0");

        // 处理日期范围查询（LocalDate 转 LocalDateTime）
        if (req.getCreateTimeStart() != null) {
            query.ge(DistributionBatch::getCreateTime, java.sql.Timestamp.valueOf(req.getCreateTimeStart().atStartOfDay()));
        }
        if (req.getCreateTimeEnd() != null) {
            query.le(DistributionBatch::getCreateTime, java.sql.Timestamp.valueOf(req.getCreateTimeEnd().atTime(23, 59, 59)));
        }

        return this.page(page, query.orderByDesc(DistributionBatch::getCreateTime).getWrapper());
    }

    /**
     * 查询发放批次详情
     *
     * @param id 发放批次ID
     * @return 发放批次详情
     */
    @Override
    public DistributionBatch selectDistributionBatchById(Long id)
    {
        return this.getById(id);
    }

    /**
     * 新增发放批次
     *
     * @param distributionBatch 发放批次
     * @return 结果
     */
    @Override
    @Transactional
    public int insertDistributionBatch(DistributionBatch distributionBatch)
    {
        distributionBatch.setStatus("draft");
        distributionBatch.setCreateTime(new Date());
        return this.save(distributionBatch) ? 1 : 0;
    }

    /**
     * 修改发放批次
     *
     * @param distributionBatch 发放批次
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDistributionBatch(DistributionBatch distributionBatch)
    {
        distributionBatch.setUpdateTime(new Date());
        return this.updateById(distributionBatch) ? 1 : 0;
    }

    /**
     * 批量删除发放批次
     *
     * @param ids 发放批次IDs
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteDistributionBatchByIds(Long[] ids)
    {
        return this.removeBatchByIds(java.util.Arrays.asList(ids)) ? ids.length : 0;
    }

    /**
     * 提交审核
     *
     * @param id 批次ID
     * @return 结果
     */
    @Override
    @Transactional
    public int submitForReview(Long id)
    {
        int updated = this.lambdaUpdate()
                .eq(DistributionBatch::getId, id)
                .set(DistributionBatch::getStatus, "pending_review")
                .set(DistributionBatch::getUpdateBy, SecurityUtils.getUsername())
                .set(DistributionBatch::getUpdateTime, new Date())
                .update() ? 1 : 0;
        if (updated > 0)
        {
            logBatchAction(id, "pending_review", "submit", "提交复核");
        }
        return updated;
    }

    /**
     * 复核通过
     *
     * @param id 批次ID
     * @param remark 复核意见
     * @return 结果
     */
    @Override
    @Transactional
    public int approveReview(Long id, String remark)
    {
        DistributionBatch batch = requireBatch(id);
        updateRelatedPlans(batch.getBatchNo(), "pending_approve", null, null);
        int updated = this.lambdaUpdate()
                .eq(DistributionBatch::getId, id)
                .set(DistributionBatch::getStatus, "pending_approve")
                .set(DistributionBatch::getRemark, remark)
                .set(DistributionBatch::getUpdateBy, SecurityUtils.getUsername())
                .set(DistributionBatch::getUpdateTime, new Date())
                .update() ? 1 : 0;
        if (updated > 0)
        {
            logBatchAction(id, "pending_approve", "review", remark);
        }
        return updated;
    }

    /**
     * 复核驳回
     *
     * @param id 批次ID
     * @param reason 驳回原因
     * @return 结果
     */
    @Override
    @Transactional
    public int rejectReview(Long id, String reason)
    {
        DistributionBatch batch = requireBatch(id);
        updateRelatedPlans(batch.getBatchNo(), "rejected", reason, null);
        int updated = this.lambdaUpdate()
                .eq(DistributionBatch::getId, id)
                .set(DistributionBatch::getStatus, "rejected")
                .set(DistributionBatch::getRemark, reason)
                .set(DistributionBatch::getUpdateBy, SecurityUtils.getUsername())
                .set(DistributionBatch::getUpdateTime, new Date())
                .update() ? 1 : 0;
        if (updated > 0)
        {
            logBatchAction(id, "rejected", "reject", reason);
        }
        return updated;
    }

    /**
     * 审批通过
     *
     * @param id 批次ID
     * @param remark 审批意见
     * @return 结果
     */
    @Override
    @Transactional
    public int approve(Long id, String remark)
    {
        DistributionBatch batch = requireBatch(id);
        updateRelatedPlans(batch.getBatchNo(), "pending_finance", null, null);
        int updated = this.lambdaUpdate()
                .eq(DistributionBatch::getId, id)
                .set(DistributionBatch::getStatus, "pending_finance")
                .set(DistributionBatch::getRemark, remark)
                .set(DistributionBatch::getUpdateBy, SecurityUtils.getUsername())
                .set(DistributionBatch::getUpdateTime, new Date())
                .update() ? 1 : 0;
        if (updated > 0)
        {
            logBatchAction(id, "pending_finance", "approve", remark);
        }
        return updated;
    }

    /**
     * 审批驳回
     *
     * @param id 批次ID
     * @param reason 驳回原因
     * @return 结果
     */
    @Override
    @Transactional
    public int reject(Long id, String reason)
    {
        DistributionBatch batch = requireBatch(id);
        updateRelatedPlans(batch.getBatchNo(), "rejected", reason, null);
        int updated = this.lambdaUpdate()
                .eq(DistributionBatch::getId, id)
                .set(DistributionBatch::getStatus, "rejected")
                .set(DistributionBatch::getRemark, reason)
                .set(DistributionBatch::getUpdateBy, SecurityUtils.getUsername())
                .set(DistributionBatch::getUpdateTime, new Date())
                .update() ? 1 : 0;
        if (updated > 0)
        {
            logBatchAction(id, "rejected", "reject", reason);
        }
        return updated;
    }

    @Override
    @Transactional
    public Long createBatchFromPlanIds(List<Long> planIds)
    {
        if (planIds == null || planIds.isEmpty())
        {
            throw new ServiceException("请选择要创建批次的支付计划");
        }
        List<SubsidyDistribution> plans = subsidyDistributionMapper.selectBatchIds(planIds);
        if (plans.size() != planIds.size())
        {
            throw new ServiceException("部分支付计划不存在或已被删除");
        }
        List<SubsidyDistribution> eligiblePlans = plans.stream()
                .filter(item -> "0".equals(item.getDelFlag()))
                .filter(item -> StringUtils.isBlank(item.getBatchNo()))
                .filter(item -> StringUtils.isBlank(item.getApprovalStatus()) || "draft".equals(item.getApprovalStatus()))
                .collect(Collectors.toList());
        if (eligiblePlans.size() != plans.size())
        {
            throw new ServiceException("仅支持对未入批次的草稿支付计划创建批次");
        }

        DistributionBatch batch = new DistributionBatch();
        batch.setBatchNo(generateBatchNo());
        batch.setBatchType(resolveNextBatchType(eligiblePlans.get(0).getBatchType()));
        batch.setSubsidyType(resolveBatchSubsidyType(eligiblePlans));
        LocalDate batchDate = resolveBatchDate(eligiblePlans);
        batch.setDistributionYear(batchDate.getYear());
        batch.setDistributionMonth(batchDate.getMonthValue());
        batch.setTotalCount(eligiblePlans.size());
        batch.setTotalAmount(eligiblePlans.stream()
                .map(SubsidyDistribution::getDistributionAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        batch.setSuccessCount(0);
        batch.setSuccessAmount(BigDecimal.ZERO);
        batch.setFailCount(0);
        batch.setFailAmount(BigDecimal.ZERO);
        batch.setStatus("pending_review");
        batch.setDelFlag("0");
        batch.setCreateBy(SecurityUtils.getUsername());
        batch.setCreateTime(new Date());
        this.save(batch);

        LocalDateTime now = LocalDateTime.now();
        for (SubsidyDistribution plan : eligiblePlans)
        {
            SubsidyDistribution update = new SubsidyDistribution();
            update.setId(plan.getId());
            update.setBatchNo(batch.getBatchNo());
            update.setBatchType(batch.getBatchType());
            update.setApprovalStatus("pending_review");
            update.setUpdateBy(SecurityUtils.getUsername());
            update.setUpdateTime(now);
            subsidyDistributionMapper.updateById(update);
        }
        logBatchAction(batch.getId(), "pending_review", "submit", "创建批次并提交复核");
        return batch.getId();
    }

    @Override
    public Map<String, Object> getBatchDetailByBatchNo(String batchNo)
    {
        DistributionBatch batch = getBatchByBatchNo(batchNo);
        SubsidyDistributionListReq req = new SubsidyDistributionListReq();
        req.setBatchNo(batchNo);
        req.setPageNum(1);
        req.setPageSize(1000);
        List<SubsidyDistributionListResp> details = subsidyDistributionMapper
                .selectSubsidyDistributionList(new Page<>(req.pageNumOrDefault(), req.pageSizeOrDefault()), req)
                .getRecords();
        Map<String, Object> result = new HashMap<>();
        result.put("batch", batch);
        result.put("details", details);
        return result;
    }

    @Override
    public byte[] generateBankFile(String batchNo)
    {
        DistributionBatch batch = getBatchByBatchNo(batchNo);
        List<SubsidyDistribution> details = subsidyDistributionMapper.selectList(
                new LambdaQueryWrapper<SubsidyDistribution>()
                        .eq(SubsidyDistribution::getBatchNo, batchNo)
                        .eq(SubsidyDistribution::getDelFlag, "0")
                        .orderByAsc(SubsidyDistribution::getId));
        StringBuilder builder = new StringBuilder();
        builder.append("批次号\t记录ID\t补贴类型\t发放金额\t审批状态").append(System.lineSeparator());
        for (SubsidyDistribution detail : details)
        {
            builder.append(batch.getBatchNo()).append('\t')
                    .append(detail.getId()).append('\t')
                    .append(detail.getSubsidyType()).append('\t')
                    .append(detail.getDistributionAmount() == null ? "" : detail.getDistributionAmount()).append('\t')
                    .append(detail.getApprovalStatus() == null ? "" : detail.getApprovalStatus())
                    .append(System.lineSeparator());
        }
        return builder.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    @Transactional
    public int submitToBank(String batchNo)
    {
        DistributionBatch batch = getBatchByBatchNo(batchNo);
        updateRelatedPlans(batchNo, "submitted_bank", null, null);
        int updated = this.lambdaUpdate()
                .eq(DistributionBatch::getId, batch.getId())
                .set(DistributionBatch::getStatus, "submitted_bank")
                .set(DistributionBatch::getBankSubmitTime, new Date())
                .set(DistributionBatch::getUpdateBy, SecurityUtils.getUsername())
                .set(DistributionBatch::getUpdateTime, new Date())
                .update() ? 1 : 0;
        if (updated > 0)
        {
            logBatchAction(batch.getId(), "submitted_bank", "submit", "提交银行");
        }
        return updated;
    }

    @Override
    @Transactional
    public Map<String, Object> importBankResult(String batchNo, List<Map<String, String>> rows)
    {
        if (rows == null || rows.isEmpty())
        {
            throw new ServiceException("导入结果为空");
        }
        DistributionBatch batch = getBatchByBatchNo(batchNo);
        List<SubsidyDistribution> details = subsidyDistributionMapper.selectList(
                new LambdaQueryWrapper<SubsidyDistribution>()
                        .eq(SubsidyDistribution::getBatchNo, batchNo)
                        .eq(SubsidyDistribution::getDelFlag, "0"));
        if (details.isEmpty())
        {
            throw new ServiceException("当前批次没有可导入结果的支付记录");
        }

        Map<Long, SubsidyDistribution> byId = details.stream().collect(Collectors.toMap(SubsidyDistribution::getId, item -> item, (a, b) -> a));
        int matched = 0;
        for (Map<String, String> row : rows)
        {
            SubsidyDistribution detail = matchDistributionRow(row, byId);
            if (detail == null)
            {
                continue;
            }
            String result = normalizeResult(row.get("result"));
            String reason = row.getOrDefault("reason", "");
            SubsidyDistribution update = new SubsidyDistribution();
            update.setId(detail.getId());
            update.setApprovalStatus(result);
            update.setReviewRemark("failed".equals(result) ? reason : "银行发放成功");
            update.setDistributionStatus("success".equals(result) ? "4" : detail.getDistributionStatus());
            update.setUpdateBy(SecurityUtils.getUsername());
            update.setUpdateTime(LocalDateTime.now());
            subsidyDistributionMapper.updateById(update);
            matched++;
        }
        if (matched == 0)
        {
            throw new ServiceException("导入文件未匹配到当前批次的支付记录");
        }

        List<SubsidyDistribution> refreshed = subsidyDistributionMapper.selectList(
                new LambdaQueryWrapper<SubsidyDistribution>()
                        .eq(SubsidyDistribution::getBatchNo, batchNo)
                        .eq(SubsidyDistribution::getDelFlag, "0"));
        int successCount = 0;
        int failCount = 0;
        BigDecimal successAmount = BigDecimal.ZERO;
        BigDecimal failAmount = BigDecimal.ZERO;
        for (SubsidyDistribution detail : refreshed)
        {
            if ("success".equals(detail.getApprovalStatus()) || "completed".equals(detail.getApprovalStatus()))
            {
                successCount++;
                successAmount = successAmount.add(detail.getDistributionAmount() == null ? BigDecimal.ZERO : detail.getDistributionAmount());
            }
            else if ("failed".equals(detail.getApprovalStatus()))
            {
                failCount++;
                failAmount = failAmount.add(detail.getDistributionAmount() == null ? BigDecimal.ZERO : detail.getDistributionAmount());
            }
        }
        this.lambdaUpdate()
                .eq(DistributionBatch::getId, batch.getId())
                .set(DistributionBatch::getSuccessCount, successCount)
                .set(DistributionBatch::getSuccessAmount, successAmount)
                .set(DistributionBatch::getFailCount, failCount)
                .set(DistributionBatch::getFailAmount, failAmount)
                .set(DistributionBatch::getBankResultTime, new Date())
                .set(DistributionBatch::getStatus, failCount > 0 ? "partial_failed" : "completed")
                .set(DistributionBatch::getUpdateBy, SecurityUtils.getUsername())
                .set(DistributionBatch::getUpdateTime, new Date())
                .update();
        logBatchAction(batch.getId(), failCount > 0 ? "partial_failed" : "completed", "approve", "导入银行结果");

        Map<String, Object> result = new HashMap<>();
        result.put("matched", matched);
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        return result;
    }

    private DistributionBatch requireBatch(Long id)
    {
        DistributionBatch batch = getById(id);
        if (batch == null)
        {
            throw new ServiceException("发放批次不存在");
        }
        return batch;
    }

    private DistributionBatch getBatchByBatchNo(String batchNo)
    {
        if (StringUtils.isBlank(batchNo))
        {
            throw new ServiceException("批次号不能为空");
        }
        DistributionBatch batch = this.lambdaQuery()
                .eq(DistributionBatch::getBatchNo, batchNo)
                .eq(DistributionBatch::getDelFlag, "0")
                .one();
        if (batch == null)
        {
            throw new ServiceException("发放批次不存在");
        }
        return batch;
    }

    private void updateRelatedPlans(String batchNo, String approvalStatus, String rejectionReason, String reviewRemark)
    {
        List<SubsidyDistribution> details = subsidyDistributionMapper.selectList(
                new LambdaQueryWrapper<SubsidyDistribution>()
                        .eq(SubsidyDistribution::getBatchNo, batchNo)
                        .eq(SubsidyDistribution::getDelFlag, "0"));
        LocalDateTime now = LocalDateTime.now();
        for (SubsidyDistribution detail : details)
        {
            SubsidyDistribution update = new SubsidyDistribution();
            update.setId(detail.getId());
            update.setApprovalStatus(approvalStatus);
            update.setRejectionReason(rejectionReason);
            update.setReviewRemark(reviewRemark != null ? reviewRemark : detail.getReviewRemark());
            update.setUpdateBy(SecurityUtils.getUsername());
            update.setUpdateTime(now);
            subsidyDistributionMapper.updateById(update);
        }
    }

    private String generateBatchNo()
    {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-"
                + String.format("%03d", System.currentTimeMillis() % 1000);
    }

    private String resolveBatchSubsidyType(List<SubsidyDistribution> plans)
    {
        return plans.stream().map(SubsidyDistribution::getSubsidyType).distinct().count() == 1
                ? plans.get(0).getSubsidyType()
                : "mixed";
    }

    private LocalDate resolveBatchDate(List<SubsidyDistribution> plans)
    {
        return plans.stream()
                .map(SubsidyDistribution::getDistributionDate)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(LocalDate.now());
    }

    private String resolveNextBatchType(String currentBatchType)
    {
        if ("second".equals(currentBatchType))
        {
            return "third";
        }
        if ("third".equals(currentBatchType))
        {
            return "third";
        }
        return "first";
    }

    private SubsidyDistribution matchDistributionRow(Map<String, String> row, Map<Long, SubsidyDistribution> byId)
    {
        String idValue = row.get("id");
        if (StringUtils.isNotBlank(idValue))
        {
            try
            {
                return byId.get(Long.valueOf(idValue));
            }
            catch (NumberFormatException ignored)
            {
            }
        }
        return null;
    }

    private String normalizeResult(String rawResult)
    {
        String value = rawResult == null ? "" : rawResult.trim().toLowerCase();
        if ("成功".equals(rawResult) || "success".equals(value) || "succeed".equals(value) || "1".equals(value) || "yes".equals(value))
        {
            return "success";
        }
        return "failed";
    }

    private void logBatchAction(Long batchId, String currentStatus, String operationType, String remark)
    {
        approvalLogService.log("payment_batch", batchId, currentStatus, operationType, remark);
    }
}
