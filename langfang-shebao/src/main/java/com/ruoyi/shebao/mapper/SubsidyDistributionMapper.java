package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.SubsidyDistribution;
import com.ruoyi.shebao.dto.SubsidyDistributionListReq;
import com.ruoyi.shebao.dto.SubsidyDistributionListResp;
import com.ruoyi.shebao.dto.SubsidyDistributionFormDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 补贴发放记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-10-13
 */
@Mapper
public interface SubsidyDistributionMapper extends BaseMapper<SubsidyDistribution>
{
    /**
     * 查询补贴发放记录列表
     * 
     * @param page 分页对象
     * @param req 查询条件
     * @return 补贴发放记录集合
     */
    Page<SubsidyDistributionListResp> selectSubsidyDistributionList(Page<SubsidyDistributionListResp> page, @Param("req") SubsidyDistributionListReq req);

    /**
     * 查询补贴发放记录详细信息
     * 
     * @param id 补贴发放记录主键
     * @return 补贴发放记录详细信息
     */
    SubsidyDistributionFormDto selectSubsidyDistributionFormById(@Param("id") Long id);

    /**
     * 检查补贴身份是否有未删除的发放记录
     * 
     * @param subsidyType 补贴类型
     * @param subsidyRecordId 补贴身份记录ID
     * @return 未删除的发放记录数量
     */
    int checkUndeletedDistributions(@Param("subsidyType") String subsidyType, @Param("subsidyRecordId") Long subsidyRecordId);
}

