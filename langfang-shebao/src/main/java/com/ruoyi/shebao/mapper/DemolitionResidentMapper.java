package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.DemolitionResident;
import com.ruoyi.shebao.dto.DemolitionResidentListReq;
import com.ruoyi.shebao.dto.DemolitionResidentListResp;
import com.ruoyi.shebao.dto.DemolitionResidentFormDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 拆迁居民信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Mapper
public interface DemolitionResidentMapper extends BaseMapper<DemolitionResident>
{
    /**
     * 查询拆迁居民信息列表
     */
    Page<DemolitionResidentListResp> selectDemolitionResidentList(Page<DemolitionResidentListResp> page, @Param("req") DemolitionResidentListReq req);

    /**
     * 查询拆迁居民详细信息（包含基础信息）
     */
    DemolitionResidentFormDto selectDemolitionResidentFormById(@Param("id") Long id);

    /**
     * 根据被补贴人ID查询拆迁居民信息列表
     */
    List<DemolitionResident> selectBySubsidyPersonId(@Param("subsidyPersonId") Long subsidyPersonId);

    /**
     * 批量插入拆迁居民信息
     */
    int batchInsertDemolitionResident(@Param("list") List<DemolitionResident> demolitionResidentList);
}
