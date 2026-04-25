package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.dto.BenefitResumeCandidateResp;
import com.ruoyi.shebao.dto.BenefitResumeCreateReq;
import com.ruoyi.shebao.dto.BenefitResumeDetailResp;
import com.ruoyi.shebao.dto.BenefitResumeListReq;
import com.ruoyi.shebao.dto.BenefitResumeListResp;

public interface BenefitResumeService
{
    Page<BenefitResumeListResp> list(BenefitResumeListReq req);

    BenefitResumeCandidateResp findCandidateByIdCardNo(String idCardNo);

    Long create(BenefitResumeCreateReq req);

    BenefitResumeDetailResp getDetail(Long id);
}
