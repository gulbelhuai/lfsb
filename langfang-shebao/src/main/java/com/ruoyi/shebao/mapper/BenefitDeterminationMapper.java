package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.BenefitDetermination;
import com.ruoyi.shebao.dto.BenefitDeterminationDetailResp;
import com.ruoyi.shebao.dto.BenefitDeterminationListReq;
import com.ruoyi.shebao.dto.BenefitDeterminationListResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 待遇核定Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-19
 */
@Mapper
public interface BenefitDeterminationMapper extends BaseMapper<BenefitDetermination>
{
    Page<BenefitDeterminationListResp> selectBenefitDeterminationPage(Page<BenefitDeterminationListResp> page, @Param("req") BenefitDeterminationListReq req);

    BenefitDeterminationDetailResp selectBenefitDeterminationDetail(@Param("id") Long id);

    java.util.List<BenefitDetermination> selectEligibleForPaymentPlan(@Param("subsidyType") String subsidyType,
                                                                      @Param("benefitStartYear") Integer benefitStartYear,
                                                                      @Param("benefitStartMonth") Integer benefitStartMonth,
                                                                      @Param("streetOfficeId") Long streetOfficeId);
}
