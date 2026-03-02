package com.ruoyi.shebao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.shebao.domain.DistributionReview;
import com.ruoyi.shebao.mapper.DistributionReviewMapper;
import com.ruoyi.shebao.service.DistributionReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 发放审核记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
@Service
public class DistributionReviewServiceImpl extends ServiceImpl<DistributionReviewMapper, DistributionReview> implements DistributionReviewService
{
    @Autowired
    private DistributionReviewMapper distributionReviewMapper;

    /**
     * 根据发放记录ID查询审核记录列表
     */
    @Override
    public List<DistributionReview> getReviewRecordsByDistributionId(Long distributionId)
    {
        return distributionReviewMapper.selectByDistributionId(distributionId);
    }

    /**
     * 添加审核记录
     */
    @Override
    public int addReviewRecord(Long distributionId, String operationType, String operationRemark, String operatorName)
    {
        DistributionReview review = new DistributionReview();
        review.setDistributionId(distributionId);
        review.setOperationType(operationType);
        review.setOperationRemark(operationRemark);
        review.setOperatorName(operatorName);
        review.setOperationTime(LocalDateTime.now());
        review.setStatus("0");
        return distributionReviewMapper.insert(review);
    }
}

