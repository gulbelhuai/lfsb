package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.dto.LandLossResidentListReq;
import com.ruoyi.shebao.dto.LandLossResidentListResp;
import com.ruoyi.shebao.dto.LandLossResidentFormDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 失地居民信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Mapper
public interface LandLossResidentMapper extends BaseMapper<LandLossResident>
{
    /**
     * 查询失地居民信息列表
     * 
     * @param page 分页对象
     * @param req 查询条件
     * @return 失地居民信息集合
     */
    Page<LandLossResidentListResp> selectLandLossResidentList(Page<LandLossResidentListResp> page, @Param("req") LandLossResidentListReq req);

    /**
     * 查询失地居民详细信息（包含基础信息）
     * 
     * @param id 失地居民信息主键
     * @return 失地居民详细信息
     */
    LandLossResidentFormDto selectLandLossResidentFormById(@Param("id") Long id);

    /**
     * 根据被补贴人ID查询失地居民信息列表
     * 
     * @param subsidyPersonId 被补贴人ID
     * @return 失地居民信息列表
     */
    List<LandLossResident> selectBySubsidyPersonId(@Param("subsidyPersonId") Long subsidyPersonId);

    /**
     * 批量插入失地居民信息
     * 
     * @param landLossResidentList 失地居民信息列表
     * @return 插入数量
     */
    int batchInsertLandLossResident(@Param("list") List<LandLossResident> landLossResidentList);
}
