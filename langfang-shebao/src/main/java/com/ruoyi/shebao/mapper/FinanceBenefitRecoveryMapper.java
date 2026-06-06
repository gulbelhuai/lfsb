package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.FinanceBenefitRecovery;
import com.ruoyi.shebao.dto.FinanceBenefitRecoveryListReq;
import com.ruoyi.shebao.dto.FinanceBenefitRecoveryListResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FinanceBenefitRecoveryMapper extends BaseMapper<FinanceBenefitRecovery>
{
    Page<FinanceBenefitRecoveryListResp> selectRecoveryPage(Page<FinanceBenefitRecoveryListResp> page,
                                                            @Param("req") FinanceBenefitRecoveryListReq req);
}
