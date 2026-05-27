package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.BenefitSuspension;
import com.ruoyi.shebao.dto.BenefitSuspensionListReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BenefitSuspensionMapper extends BaseMapper<BenefitSuspension>
{
    Page<BenefitSuspension> selectActiveSuspensionPage(Page<BenefitSuspension> page, @Param("req") BenefitSuspensionListReq req);
}
