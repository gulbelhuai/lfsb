package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.shebao.domain.VillageOfficialPosition;
import com.ruoyi.shebao.dto.VillageOfficialFormDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 村干部任职信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Mapper
public interface VillageOfficialPositionMapper extends BaseMapper<VillageOfficialPosition>
{
    /**
     * 根据村干部ID查询任职信息列表
     * 
     * @param villageOfficialId 村干部信息ID
     * @return 任职信息列表
     */
    List<VillageOfficialFormDto.VillageOfficialPositionDto> selectByVillageOfficialId(@Param("villageOfficialId") Long villageOfficialId);

    /**
     * 根据村干部ID删除任职信息
     * 
     * @param villageOfficialId 村干部信息ID
     * @return 删除数量
     */
    int deleteByVillageOfficialId(@Param("villageOfficialId") Long villageOfficialId);

    /**
     * 批量插入任职信息
     * 
     * @param positionList 任职信息列表
     * @return 插入数量
     */
    int batchInsertPositions(@Param("list") List<VillageOfficialPosition> positionList);
}
