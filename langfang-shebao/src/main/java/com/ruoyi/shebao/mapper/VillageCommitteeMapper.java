package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.dto.VillageCommitteeListReq;
import com.ruoyi.shebao.dto.VillageCommitteeListResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 村委会信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-10-18
 */
@Mapper
public interface VillageCommitteeMapper extends BaseMapper<VillageCommittee>
{
    /**
     * 查询村委会信息列表
     * 
     * @param req 查询条件
     * @return 村委会信息列表
     */
    List<VillageCommitteeListResp> selectVillageCommitteeList(VillageCommitteeListReq req);

    /**
     * 校验村委会编码是否唯一（在同一街道办下）
     * 
     * @param streetOfficeId 街道办ID
     * @param villageCode 村委会编码
     * @param id 主键ID（更新时排除自己）
     * @return 结果
     */
    int checkVillageCodeUnique(@Param("streetOfficeId") Long streetOfficeId, @Param("villageCode") String villageCode, @Param("id") Long id);

    /**
     * 校验村委会名称是否唯一（在同一街道办下）
     * 
     * @param streetOfficeId 街道办ID
     * @param villageName 村委会名称
     * @param id 主键ID（更新时排除自己）
     * @return 结果
     */
    int checkVillageNameUnique(@Param("streetOfficeId") Long streetOfficeId, @Param("villageName") String villageName, @Param("id") Long id);

    /**
     * 根据街道办ID查询村委会列表
     * 
     * @param streetOfficeId 街道办ID
     * @return 村委会列表
     */
    List<VillageCommittee> selectVillageCommitteeListByStreetOfficeId(@Param("streetOfficeId") Long streetOfficeId);
}
