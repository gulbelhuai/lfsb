package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.DistributionReview;

import java.util.List;

/**
 * 发放审核记录Service接口
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
public interface DistributionReviewService extends IService<DistributionReview>
{
    /**
     * 根据发放记录ID查询审核记录列表
     * 
     * @param distributionId 发放记录ID
     * @return 审核记录列表
     */
    List<DistributionReview> getReviewRecordsByDistributionId(Long distributionId);

    /**
     * 添加审核记录
     * 
     * @param distributionId 发放记录ID
     * @param operationType 操作类型
     * @param operationRemark 操作说明
     * @param operatorName 操作人
     * @return 结果
     */
    int addReviewRecord(Long distributionId, String operationType, String operationRemark, String operatorName);
}

