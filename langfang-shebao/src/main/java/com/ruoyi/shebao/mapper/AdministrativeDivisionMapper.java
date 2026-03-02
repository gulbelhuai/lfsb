package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.AdministrativeDivision;
import com.ruoyi.shebao.dto.AdministrativeDivisionListReq;
import com.ruoyi.shebao.dto.AdministrativeDivisionListResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 行政区划Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Mapper
public interface AdministrativeDivisionMapper extends BaseMapper<AdministrativeDivision>
{
    /**
     * 查询行政区划列表
     * 
     * @param page 分页对象
     * @param req 查询条件
     * @return 行政区划列表
     */
    Page<AdministrativeDivisionListResp> selectAdministrativeDivisionList(Page<AdministrativeDivisionListResp> page, @Param("req") AdministrativeDivisionListReq req);

    /**
     * 根据父级编码查询下级行政区划
     * 
     * @param parentCode 父级编码
     * @return 行政区划列表
     */
    List<AdministrativeDivision> selectByParentCode(@Param("parentCode") String parentCode);

    /**
     * 根据行政级别查询行政区划
     * 
     * @param divisionLevel 行政级别
     * @return 行政区划列表
     */
    List<AdministrativeDivision> selectByDivisionLevel(@Param("divisionLevel") Integer divisionLevel);

    /**
     * 根据编码查询行政区划
     * 
     * @param divisionCode 行政区划编码
     * @return 行政区划
     */
    AdministrativeDivision selectByDivisionCode(@Param("divisionCode") String divisionCode);

    /**
     * 根据编码构建全路径信息
     * 
     * @param divisionCode 行政区划编码
     * @return 全路径信息
     */
    AdministrativeDivision buildFullPath(@Param("divisionCode") String divisionCode);

    /**
     * 检查行政区划是否有下级
     * 
     * @param divisionCode 行政区划编码
     * @return 下级数量
     */
    int checkHasChildren(@Param("divisionCode") String divisionCode);

    /**
     * 检查行政区划是否有村级单位关联
     * 
     * @param divisionCode 行政区划编码
     * @return 关联数量
     */
    int checkHasVillageRelated(@Param("divisionCode") String divisionCode);

    /**
     * 批量插入行政区划
     * 
     * @param divisionList 行政区划列表
     * @return 插入数量
     */
    int batchInsert(@Param("list") List<AdministrativeDivision> divisionList);

    /**
     * 获取指定节点的所有子级节点（递归）
     * 
     * @param divisionCode 行政区划编码
     * @return 所有子级节点列表
     */
    List<AdministrativeDivision> selectAllChildren(@Param("divisionCode") String divisionCode);

    /**
     * 批量更新全路径信息
     * 
     * @param divisionList 需要更新的行政区划列表
     * @return 更新数量
     */
    int batchUpdateFullPath(@Param("list") List<AdministrativeDivision> divisionList);

    /**
     * 批量更新所有子级的全路径信息（使用左匹配）
     * 
     * @param oldFullCode 原全路径编码
     * @param newFullCode 新全路径编码
     * @param oldFullName 原全路径名称
     * @param newFullName 新全路径名称
     * @param updateBy 更新人
     * @param updateTime 更新时间
     * @return 更新数量
     */
    int batchUpdateChildrenFullPath(@Param("oldFullCode") String oldFullCode,
                                   @Param("newFullCode") String newFullCode,
                                   @Param("oldFullName") String oldFullName,
                                   @Param("newFullName") String newFullName,
                                   @Param("updateBy") String updateBy,
                                   @Param("updateTime") LocalDateTime updateTime);
}
