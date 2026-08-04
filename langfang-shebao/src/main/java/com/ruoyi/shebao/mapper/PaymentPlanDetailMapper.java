package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.PaymentPlanDetail;
import com.ruoyi.shebao.dto.DistributionRecordListReq;
import com.ruoyi.shebao.dto.PaymentPlanDetailResp;
import com.ruoyi.shebao.dto.PaymentPlanFailureListReq;
import com.ruoyi.shebao.dto.PaymentPlanFailureListResp;
import com.ruoyi.shebao.dto.ResidentPaymentDetailResp;
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

    Page<PaymentPlanDetailResp> selectByPlanId(Page<PaymentPlanDetailResp> page,
                                              @Param("planId") Long planId,
                                              @Param("personName") String personName,
                                              @Param("idCardNo") String idCardNo);

    /** 导出：按计划查询全部明细 */
    List<PaymentPlanDetailResp> selectListByPlanId(@Param("planId") Long planId);

    /**
     * 居民查询：按人查支付明细
     * @param mode pre=未财务通过；paid=已财务通过
     */
    Page<ResidentPaymentDetailResp> selectResidentPaymentDetails(Page<ResidentPaymentDetailResp> page,
                                                                @Param("subsidyPersonId") Long subsidyPersonId,
                                                                @Param("mode") String mode);

    /**
     * 补贴发放记录（全局）：财务审核通过的支付计划明细
     */
    Page<ResidentPaymentDetailResp> selectDistributionRecordList(Page<ResidentPaymentDetailResp> page,
                                                                 @Param("req") DistributionRecordListReq req);

    /** 登记删除校验：该被补贴人是否仍有未删支付计划明细 */
    int countUndeletedBySubsidyPersonId(@Param("subsidyPersonId") Long subsidyPersonId);

    List<PaymentPlanDetailResp> selectPreviewDetails(@Param("businessPeriod") LocalDate businessPeriod,
                                                     @Param("subsidyType") String subsidyType,
                                                     @Param("excludePlanId") Long excludePlanId);

    /** 按身份证号将明细标记为发放失败并记录原因 */
    int markFailedByIdCard(@Param("planId") Long planId,
                           @Param("idCardNo") String idCardNo,
                           @Param("reason") String reason);

    /** 将未失败的明细标记为发放成功 */
    int markRemainingSuccess(@Param("planId") Long planId);

    /** 汇总批次全部明细金额（应发） */
    BigDecimal sumAllAmountByPlanId(@Param("planId") Long planId);

    /** 汇总批次发放成功明细金额 */
    BigDecimal sumSuccessAmountByPlanId(@Param("planId") Long planId);

    /** 汇总批次发放失败明细金额 */
    BigDecimal sumFailedAmountByPlanId(@Param("planId") Long planId);

    /** 银行发放失败明细列表 */
    Page<PaymentPlanFailureListResp> selectFailureList(Page<PaymentPlanFailureListResp> page,
                                                     @Param("req") PaymentPlanFailureListReq req);

    /** 统计某补贴类型下未复核的登记记录数 */
    int countPendingRegistrationReview(@Param("subsidyType") String subsidyType);

    /** 统计某补贴类型下未审核通过的核定记录数 */
    int countPendingDeterminationApproval(@Param("subsidyType") String subsidyType);
}
