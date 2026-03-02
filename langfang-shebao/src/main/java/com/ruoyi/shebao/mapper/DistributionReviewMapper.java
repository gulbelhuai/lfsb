package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.shebao.domain.DistributionReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发放审核记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
@Mapper
public interface DistributionReviewMapper extends BaseMapper<DistributionReview>
{
    /**
     * 根据发放记录ID查询审核记录列表
     * 
     * @param distributionId 发放记录ID
     * @return 审核记录列表
     */
    List<DistributionReview> selectByDistributionId(@Param("distributionId") Long distributionId);
}

