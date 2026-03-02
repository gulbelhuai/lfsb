package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.LandLossResident;
import com.ruoyi.shebao.dto.LandLossResidentListReq;
import com.ruoyi.shebao.dto.LandLossResidentListResp;
import com.ruoyi.shebao.dto.LandLossResidentFormDto;

import java.util.List;

/**
 * 失地居民信息Service接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
public interface LandLossResidentService extends IService<LandLossResident>
{
    /**
     * 查询失地居民信息列表
     * 
     * @param req 查询条件
     * @return 失地居民信息列表
     */
    Page<LandLossResidentListResp> selectLandLossResidentList(LandLossResidentListReq req);

    /**
     * 查询失地居民信息详情（包含基础信息）
     * 
     * @param id 失地居民信息主键
     * @return 失地居民信息
     */
    LandLossResidentFormDto selectLandLossResidentFormById(Long id);

    /**
     * 新增失地居民信息（智能处理基础信息）
     * 
     * @param formDto 失地居民表单数据
     * @return 结果
     */
    int insertLandLossResident(LandLossResidentFormDto formDto);

    /**
     * 修改失地居民信息（智能处理基础信息）
     * 
     * @param formDto 失地居民表单数据
     * @return 结果
     */
    int updateLandLossResident(LandLossResidentFormDto formDto);

    /**
     * 批量删除失地居民信息
     * 
     * @param ids 需要删除的失地居民信息主键集合
     * @return 结果
     */
    int deleteLandLossResidentByIds(Long[] ids);

    /**
     * 删除失地居民信息信息
     * 
     * @param id 失地居民信息主键
     * @return 结果
     */
    int deleteLandLossResidentById(Long id);

    /**
     * 根据身份证号查询基础信息并自动填充
     * 
     * @param idCardNo 身份证号
     * @return 表单数据
     */
    LandLossResidentFormDto getFormDataByIdCardNo(String idCardNo);

    /**
     * 批量导入失地居民信息
     * 
     * @param landLossResidentList 失地居民信息列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importLandLossResident(List<LandLossResidentFormDto> landLossResidentList, Boolean isUpdateSupport, String operName);
}
