package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.VillageOfficial;
import com.ruoyi.shebao.dto.VillageOfficialListReq;
import com.ruoyi.shebao.dto.VillageOfficialListResp;
import com.ruoyi.shebao.dto.VillageOfficialFormDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 村干部信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Mapper
public interface VillageOfficialMapper extends BaseMapper<VillageOfficial>
{
    /**
     * 查询村干部信息列表
     * 
     * @param page 分页对象
     * @param req 查询条件
     * @return 村干部信息集合
     */
    Page<VillageOfficialListResp> selectVillageOfficialList(Page<VillageOfficialListResp> page, @Param("req") VillageOfficialListReq req);

    /**
     * 查询村干部详细信息（包含基础信息和任职信息）
     * 
     * @param id 村干部信息主键
     * @return 村干部详细信息
     */
    VillageOfficialFormDto selectVillageOfficialFormById(@Param("id") Long id);

    /**
     * 根据被补贴人ID查询村干部信息列表
     * 
     * @param subsidyPersonId 被补贴人ID
     * @return 村干部信息列表
     */
    List<VillageOfficial> selectBySubsidyPersonId(@Param("subsidyPersonId") Long subsidyPersonId);

    /**
     * 批量插入村干部信息
     * 
     * @param villageOfficialList 村干部信息列表
     * @return 插入数量
     */
    int batchInsertVillageOfficial(@Param("list") List<VillageOfficial> villageOfficialList);
}
