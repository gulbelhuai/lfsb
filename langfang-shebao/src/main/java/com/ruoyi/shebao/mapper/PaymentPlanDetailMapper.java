package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.PaymentPlanDetail;
import com.ruoyi.shebao.dto.PaymentPlanDetailResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PaymentPlanDetailMapper extends BaseMapper<PaymentPlanDetail>
{
    int batchInsert(@Param("list") List<PaymentPlanDetail> list);

    Page<PaymentPlanDetailResp> selectByPlanId(Page<PaymentPlanDetailResp> page, @Param("planId") Long planId);

    List<PaymentPlanDetailResp> selectPreviewDetails(@Param("businessPeriod") LocalDate businessPeriod);
}
