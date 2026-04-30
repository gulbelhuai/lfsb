package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.PaymentPlan;
import com.ruoyi.shebao.dto.PaymentPlanListReq;
import com.ruoyi.shebao.dto.PaymentPlanListResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentPlanMapper extends BaseMapper<PaymentPlan>
{
    Page<PaymentPlanListResp> selectPaymentPlanList(Page<PaymentPlanListResp> page, @Param("req") PaymentPlanListReq req);
}
