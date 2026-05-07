package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.shebao.domain.PaymentPlanAudit;
import com.ruoyi.shebao.dto.PaymentPlanAuditResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentPlanAuditMapper extends BaseMapper<PaymentPlanAudit>
{
    List<PaymentPlanAuditResp> selectByPlanId(@Param("planId") Long planId);
}
