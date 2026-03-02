package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.DistributionBatch;
import com.ruoyi.shebao.dto.DistributionBatchListReq;
import com.ruoyi.shebao.mapper.DistributionBatchMapper;
import com.ruoyi.shebao.service.IDistributionBatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 发放批次Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@Service
public class DistributionBatchServiceImpl extends ServiceImpl<DistributionBatchMapper, DistributionBatch> implements IDistributionBatchService
{
    /**
     * 查询发放批次列表
     *
     * @param req 查询条件
     * @return 发放批次列表
     */
    @Override
    public Page<DistributionBatch> selectDistributionBatchList(DistributionBatchListReq req)
    {
        // 使用默认分页参数
        Page<DistributionBatch> page = new Page<>(1, 10);

        var query = this.lambdaQuery()
                .like(StringUtils.isNotBlank(req.getBatchNo()), DistributionBatch::getBatchNo, req.getBatchNo())
                .eq(StringUtils.isNotBlank(req.getSubsidyType()), DistributionBatch::getSubsidyType, req.getSubsidyType())
                .eq(req.getBatchType() != null, DistributionBatch::getBatchType, req.getBatchType())
                .eq(StringUtils.isNotBlank(req.getStatus()), DistributionBatch::getStatus, req.getStatus());

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
        return this.lambdaUpdate()
                .eq(DistributionBatch::getId, id)
                .set(DistributionBatch::getStatus, "pending_review")
                .set(DistributionBatch::getSubmitTime, new Date())
                .update() ? 1 : 0;
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
        return this.lambdaUpdate()
                .eq(DistributionBatch::getId, id)
                .set(DistributionBatch::getStatus, "pending_approve")
                .set(DistributionBatch::getReviewTime, new Date())
                .set(DistributionBatch::getRemark, remark)
                .update() ? 1 : 0;
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
        return this.lambdaUpdate()
                .eq(DistributionBatch::getId, id)
                .set(DistributionBatch::getStatus, "rejected")
                .set(DistributionBatch::getReviewTime, new Date())
                .set(DistributionBatch::getRemark, reason)
                .update() ? 1 : 0;
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
        return this.lambdaUpdate()
                .eq(DistributionBatch::getId, id)
                .set(DistributionBatch::getStatus, "pending_finance")
                .set(DistributionBatch::getApproveTime, new Date())
                .set(DistributionBatch::getRemark, remark)
                .update() ? 1 : 0;
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
        return this.lambdaUpdate()
                .eq(DistributionBatch::getId, id)
                .set(DistributionBatch::getStatus, "rejected")
                .set(DistributionBatch::getApproveTime, new Date())
                .set(DistributionBatch::getRemark, reason)
                .update() ? 1 : 0;
    }
}
