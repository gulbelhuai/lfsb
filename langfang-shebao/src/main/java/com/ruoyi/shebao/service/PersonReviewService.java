package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.dto.PersonReviewListReq;
import com.ruoyi.shebao.dto.PersonReviewListResp;

/**
 * 人员登记复核（按补贴子记录）
 */
public interface PersonReviewService
{
    Page<PersonReviewListResp> selectPersonReviewList(PersonReviewListReq req);

    void approve(String subsidyType, Long recordId, String remark);

    void reject(String subsidyType, Long recordId, String reason);
}
