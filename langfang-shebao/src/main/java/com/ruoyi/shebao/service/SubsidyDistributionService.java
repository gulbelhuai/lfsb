package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.SubsidyDistribution;
import com.ruoyi.shebao.dto.AvailableSubsidyDto;
import com.ruoyi.shebao.dto.SubsidyDistributionListReq;
import com.ruoyi.shebao.dto.SubsidyDistributionListResp;
import com.ruoyi.shebao.dto.SubsidyDistributionFormDto;

import java.util.List;
import java.util.Map;

/**
 * 补贴发放记录Service接口
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
public interface SubsidyDistributionService extends IService<SubsidyDistribution>
{
    /**
     * 查询补贴发放记录列表
     * 
     * @param req 查询条件
     * @return 补贴发放记录列表
     */
    Page<SubsidyDistributionListResp> selectSubsidyDistributionList(SubsidyDistributionListReq req);

    /**
     * 查询补贴发放记录详情
     * 
     * @param id 补贴发放记录主键
     * @return 补贴发放记录信息
     */
    SubsidyDistributionFormDto selectSubsidyDistributionFormById(Long id);

    /**
     * 自动搜索居民（下拉列表）
     * 
     * @param keyword 关键词（身份证号或姓名）
     * @return 居民列表
     */
    List<Map<String, Object>> searchResidents(String keyword);

    /**
     * 根据居民ID查询可发放的补贴
     * 
     * @param subsidyPersonId 被补贴人ID
     * @return 可发放的补贴信息
     */
    AvailableSubsidyDto getAvailableSubsidiesByPersonId(Long subsidyPersonId);

    /**
     * 根据身份证号或姓名查询可发放的补贴
     * 
     * @param keyword 关键词（身份证号或姓名）
     * @return 可发放的补贴信息
     */
    AvailableSubsidyDto getAvailableSubsidies(String keyword);

    /**
     * 新增补贴发放记录
     * 
     * @param formDto 补贴发放记录表单数据
     * @return 结果
     */
    int insertSubsidyDistribution(SubsidyDistributionFormDto formDto);

    /**
     * 审核通过
     * 
     * @param id 发放记录ID
     * @param remark 审核说明
     * @return 结果
     */
    int approveDistribution(Long id, String remark);

    /**
     * 拒绝发放
     * 
     * @param id 发放记录ID
     * @param remark 拒绝原因
     * @return 结果
     */
    int rejectDistribution(Long id, String remark);

    /**
     * 发放补贴
     * 
     * @param id 发放记录ID
     * @param remark 发放说明
     * @return 结果
     */
    int distributeSubsidy(Long id, String remark);

    /**
     * 重新提交
     * 
     * @param id 发放记录ID
     * @return 结果
     */
    int resubmitDistribution(Long id);

    /**
     * 删除补贴发放记录
     * 
     * @param ids 需要删除的补贴发放记录主键集合
     * @return 结果
     */
    int deleteSubsidyDistributionByIds(Long[] ids);

    /**
     * 查询居民最近的发放记录
     * 
     * @param subsidyPersonId 被补贴人ID
     * @param limit 查询数量
     * @return 发放记录列表
     */
    List<SubsidyDistributionListResp> selectRecentDistributions(Long subsidyPersonId, Integer limit);

    int generateFromNoticeBatch(String noticeBatchNo);

    Map<String, Object> previewPaymentStatistics(String subsidyType, String paymentMonth, Long streetOfficeId);

    int generatePaymentPlans(String subsidyType, String paymentMonth, Long streetOfficeId);

    List<SubsidyDistributionListResp> selectByBatchNo(String batchNo);
}

