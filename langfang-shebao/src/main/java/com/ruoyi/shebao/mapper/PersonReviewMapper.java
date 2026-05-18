package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.dto.PersonReviewListReq;
import com.ruoyi.shebao.dto.PersonReviewListResp;
import org.apache.ibatis.annotations.Param;

/**
 * 人员登记复核（按补贴子表）
 */
public interface PersonReviewMapper
{
    Page<PersonReviewListResp> selectPersonReviewList(Page<PersonReviewListResp> page, @Param("req") PersonReviewListReq req);
}
