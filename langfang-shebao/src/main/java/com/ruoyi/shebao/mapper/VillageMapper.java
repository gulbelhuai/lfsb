package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.Village;
import com.ruoyi.shebao.dto.VillageListReq;
import com.ruoyi.shebao.dto.VillageListResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 村级单位Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Mapper
public interface VillageMapper extends BaseMapper<Village>
{
    /**
     * 查询村级单位列表
     * 
     * @param page 分页对象
     * @param req 查询条件
     * @return 村级单位列表
     */
    Page<VillageListResp> selectVillageList(Page<VillageListResp> page, @Param("req") VillageListReq req);

    /**
     * 根据村编码查询村级单位
     * 
     * @param villageCode 村编码
     * @return 村级单位
     */
    Village selectVillageByCode(@Param("villageCode") String villageCode);

    /**
     * 检查村级单位是否存在关联数据
     * 
     * @param villageId 村级单位ID
     * @return 关联数据数量
     */
    int checkVillageRelatedData(@Param("villageId") Long villageId);

    /**
     * 批量插入村级单位
     * 
     * @param villageList 村级单位列表
     * @return 插入数量
     */
    int batchInsertVillage(@Param("list") List<Village> villageList);
}
