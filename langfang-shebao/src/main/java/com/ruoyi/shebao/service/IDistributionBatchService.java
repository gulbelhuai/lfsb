package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.DistributionBatch;
import com.ruoyi.shebao.dto.DistributionBatchListReq;

/**
 * 发放批次Service接口
 *
 * @author ruoyi
 * @date 2025-01-19
 */
public interface IDistributionBatchService extends IService<DistributionBatch>
{
    /**
     * 查询发放批次列表
     *
     * @param req 查询条件
     * @return 发放批次列表
     */
    Page<DistributionBatch> selectDistributionBatchList(DistributionBatchListReq req);

    /**
     * 查询发放批次详情
     *
     * @param id 发放批次ID
     * @return 发放批次详情
     */
    DistributionBatch selectDistributionBatchById(Long id);

    /**
     * 新增发放批次
     *
     * @param distributionBatch 发放批次
     * @return 结果
     */
    int insertDistributionBatch(DistributionBatch distributionBatch);

    /**
     * 修改发放批次
     *
     * @param distributionBatch 发放批次
     * @return 结果
     */
    int updateDistributionBatch(DistributionBatch distributionBatch);

    /**
     * 批量删除发放批次
     *
     * @param ids 发放批次IDs
     * @return 结果
     */
    int deleteDistributionBatchByIds(Long[] ids);

    /**
     * 提交审核
     *
     * @param id 批次ID
     * @return 结果
     */
    int submitForReview(Long id);

    /**
     * 复核通过
     *
     * @param id 批次ID
     * @param remark 复核意见
     * @return 结果
     */
    int approveReview(Long id, String remark);

    /**
     * 复核驳回
     *
     * @param id 批次ID
     * @param reason 驳回原因
     * @return 结果
     */
    int rejectReview(Long id, String reason);

    /**
     * 审批通过
     *
     * @param id 批次ID
     * @param remark 审批意见
     * @return 结果
     */
    int approve(Long id, String remark);

    /**
     * 审批驳回
     *
     * @param id 批次ID
     * @param reason 驳回原因
     * @return 结果
     */
    int reject(Long id, String reason);
}
