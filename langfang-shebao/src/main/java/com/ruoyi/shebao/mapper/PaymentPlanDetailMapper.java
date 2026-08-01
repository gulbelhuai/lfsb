package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.PaymentPlanDetail;
import com.ruoyi.shebao.dto.PaymentPlanDetailResp;
import com.ruoyi.shebao.dto.PaymentPlanFailureListReq;
import com.ruoyi.shebao.dto.PaymentPlanFailureListResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PaymentPlanDetailMapper extends BaseMapper<PaymentPlanDetail>
{
    int batchInsert(@Param("list") List<PaymentPlanDetail> list);
    int deleteByPlanId(@Param("planId") Long planId);

    Page<PaymentPlanDetailResp> selectByPlanId(Page<PaymentPlanDetailResp> page, @Param("planId") Long planId);

    /** 导出：按计划查询全部明细 */
    List<PaymentPlanDetailResp> selectListByPlanId(@Param("planId") Long planId);

    List<PaymentPlanDetailResp> selectPreviewDetails(@Param("businessPeriod") LocalDate businessPeriod,
                                                     @Param("subsidyType") String subsidyType,
                                                     @Param("excludePlanId") Long excludePlanId);

    /** 按身份证号将明细标记为发放失败并记录原因 */
    int markFailedByIdCard(@Param("planId") Long planId,
                           @Param("idCardNo") String idCardNo,
                           @Param("reason") String reason);

    /** 将未失败的明细标记为发放成功 */
    int markRemainingSuccess(@Param("planId") Long planId);

    /** 汇总批次发放成功明细金额 */
    BigDecimal sumSuccessAmountByPlanId(@Param("planId") Long planId);

    /** 银行发放失败明细列表 */
    Page<PaymentPlanFailureListResp> selectFailureList(Page<PaymentPlanFailureListResp> page,
                                                     @Param("req") PaymentPlanFailureListReq req);

    /** 统计某补贴类型下未复核的登记记录数 */
    int countPendingRegistrationReview(@Param("subsidyType") String subsidyType);

    /** 统计某补贴类型下未审核通过的核定记录数 */
    int countPendingDeterminationApproval(@Param("subsidyType") String subsidyType);
}
