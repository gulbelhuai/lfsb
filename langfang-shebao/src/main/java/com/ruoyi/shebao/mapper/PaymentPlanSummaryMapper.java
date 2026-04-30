package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.shebao.domain.PaymentPlanSummary;
import com.ruoyi.shebao.dto.PaymentPlanSummaryResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentPlanSummaryMapper extends BaseMapper<PaymentPlanSummary>
{
    int batchInsert(@Param("list") List<PaymentPlanSummary> list);

    List<PaymentPlanSummaryResp> selectByPlanId(@Param("planId") Long planId);
}
