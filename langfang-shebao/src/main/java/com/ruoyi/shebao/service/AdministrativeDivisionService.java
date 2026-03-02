package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.AdministrativeDivision;
import com.ruoyi.shebao.dto.AdministrativeDivisionListReq;
import com.ruoyi.shebao.dto.AdministrativeDivisionListResp;

import java.util.List;

/**
 * 行政区划Service接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
public interface AdministrativeDivisionService extends IService<AdministrativeDivision>
{
    /**
     * 查询行政区划列表
     * 
     * @param req 查询条件
     * @return 行政区划列表
     */
    Page<AdministrativeDivisionListResp> selectAdministrativeDivisionList(AdministrativeDivisionListReq req);

    /**
     * 查询行政区划详情
     * 
     * @param id 行政区划主键
     * @return 行政区划
     */
    AdministrativeDivision selectAdministrativeDivisionById(Long id);

    /**
     * 新增行政区划
     * 
     * @param division 行政区划
     * @return 结果
     */
    int insertAdministrativeDivision(AdministrativeDivision division);

    /**
     * 修改行政区划
     * 
     * @param division 行政区划
     * @return 结果
     */
    int updateAdministrativeDivision(AdministrativeDivision division);

    /**
     * 批量删除行政区划
     * 
     * @param ids 需要删除的行政区划主键集合
     * @return 结果
     */
    int deleteAdministrativeDivisionByIds(Long[] ids);

    /**
     * 删除行政区划信息
     * 
     * @param id 行政区划主键
     * @return 结果
     */
    int deleteAdministrativeDivisionById(Long id);

    /**
     * 根据父级编码查询下级行政区划
     * 
     * @param parentCode 父级编码
     * @return 行政区划列表
     */
    List<AdministrativeDivision> selectByParentCode(String parentCode);

    /**
     * 根据行政级别查询行政区划
     * 
     * @param divisionLevel 行政级别
     * @return 行政区划列表
     */
    List<AdministrativeDivision> selectByDivisionLevel(Integer divisionLevel);

    /**
     * 根据编码查询行政区划
     * 
     * @param divisionCode 行政区划编码
     * @return 行政区划
     */
    AdministrativeDivision selectByDivisionCode(String divisionCode);

    /**
     * 构建全路径信息
     * 
     * @param divisionCode 行政区划编码
     * @return 包含全路径的行政区划信息
     */
    AdministrativeDivision buildFullPath(String divisionCode);

    /**
     * 新增行政区划并自动维护全路径
     * 
     * @param division 行政区划
     * @return 结果
     */
    int insertDivisionWithPath(AdministrativeDivision division);

    /**
     * 修改行政区划并自动维护全路径
     * 
     * @param division 行政区划
     * @return 结果
     */
    int updateDivisionWithPath(AdministrativeDivision division);

    /**
     * 获取省级行政区划列表
     * 
     * @return 省级列表
     */
    List<AdministrativeDivision> getProvinceList();

    /**
     * 获取市级行政区划列表
     * 
     * @param provinceCode 省份编码
     * @return 市级列表
     */
    List<AdministrativeDivision> getCityList(String provinceCode);

    /**
     * 获取县级行政区划列表
     * 
     * @param cityCode 城市编码
     * @return 县级列表
     */
    List<AdministrativeDivision> getCountyList(String cityCode);

    /**
     * 获取乡镇级行政区划列表
     * 
     * @param countyCode 县区编码
     * @return 乡镇级列表
     */
    List<AdministrativeDivision> getTownshipList(String countyCode);

    /**
     * 校验行政区划编码是否唯一
     * 
     * @param division 行政区划信息
     * @return 结果
     */
    String checkDivisionCodeUnique(AdministrativeDivision division);

    /**
     * 批量导入行政区划
     * 
     * @param divisionList 行政区划列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importAdministrativeDivision(List<AdministrativeDivision> divisionList, Boolean isUpdateSupport, String operName);

    /**
     * 获取行政区划树形结构
     * 
     * @param maxLevel 最大显示级别（1-5）
     * @param onlyWithChildren 是否只显示有子级的节点
     * @return 树形结构列表
     */
    List<AdministrativeDivision> getDivisionTree(Integer maxLevel, Boolean onlyWithChildren);

    /**
     * 获取指定父级下的直接子级行政区划
     * 
     * @param parentCode 父级编码
     * @param targetLevel 目标级别
     * @return 子级行政区划列表
     */
    List<AdministrativeDivision> getDirectChildren(String parentCode, Integer targetLevel);

}
