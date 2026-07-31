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

    /**
     * 同业务期年月+类型前缀下，未删除记录中已用最大三位序号（无则 0）
     *
     * @param prefix8 前 8 位：yyyyMM + 01/02
     */
    int selectMaxBatchSeqSuffix(@Param("prefix8") String prefix8);
}
