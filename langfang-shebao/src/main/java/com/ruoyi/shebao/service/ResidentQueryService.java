package com.ruoyi.shebao.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.shebao.dto.ResidentDetailInfoDto;
import com.ruoyi.shebao.dto.ResidentSearchResp;

import java.util.List;

/**
 * 居民查询Service接口
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public interface ResidentQueryService
{
    /**
     * 搜索居民
     * 
     * @param keyword 搜索关键词
     * @return 居民列表
     */
    List<ResidentSearchResp> searchResidents(String keyword);

    /**
     * 获取居民详细信息
     * 
     * @param keyword 搜索关键词
     * @param subsidyPersonId 居民ID（可选）
     * @return 居民详细信息
     */
    ResidentDetailInfoDto getResidentDetailInfo(String keyword, Long subsidyPersonId);

    /**
     * 获取居民发放记录
     * 
     * @param subsidyPersonId 居民ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 发放记录分页结果
     */
    AjaxResult getResidentDistributionList(Long subsidyPersonId, Integer pageNum, Integer pageSize);
}
