package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.dto.BenefitSuspensionCandidateResp;
import com.ruoyi.shebao.dto.BenefitSuspensionCreateReq;
import com.ruoyi.shebao.dto.BenefitSuspensionDetailResp;
import com.ruoyi.shebao.dto.BenefitSuspensionListReq;
import com.ruoyi.shebao.dto.BenefitSuspensionListResp;

public interface BenefitSuspensionService
{
    Page<BenefitSuspensionListResp> list(BenefitSuspensionListReq req);

    BenefitSuspensionCandidateResp findCandidateByIdCardNo(String idCardNo);

    Long create(BenefitSuspensionCreateReq req);

    BenefitSuspensionDetailResp getDetail(Long id);
}
