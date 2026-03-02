package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.DemolitionResident;
import com.ruoyi.shebao.dto.DemolitionResidentListReq;
import com.ruoyi.shebao.dto.DemolitionResidentListResp;
import com.ruoyi.shebao.dto.DemolitionResidentFormDto;

import java.util.List;

/**
 * 拆迁居民信息Service接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
public interface DemolitionResidentService extends IService<DemolitionResident>
{
    /**
     * 查询拆迁居民信息列表
     */
    Page<DemolitionResidentListResp> selectDemolitionResidentList(DemolitionResidentListReq req);

    /**
     * 查询拆迁居民信息详情（包含基础信息）
     */
    DemolitionResidentFormDto selectDemolitionResidentFormById(Long id);

    /**
     * 新增拆迁居民信息（智能处理基础信息）
     */
    int insertDemolitionResident(DemolitionResidentFormDto formDto);

    /**
     * 修改拆迁居民信息（智能处理基础信息）
     */
    int updateDemolitionResident(DemolitionResidentFormDto formDto);

    /**
     * 批量删除拆迁居民信息
     */
    int deleteDemolitionResidentByIds(Long[] ids);

    /**
     * 删除拆迁居民信息信息
     */
    int deleteDemolitionResidentById(Long id);

    /**
     * 根据身份证号查询基础信息并自动填充
     */
    DemolitionResidentFormDto getFormDataByIdCardNo(String idCardNo);

    /**
     * 批量导入拆迁居民信息
     */
    String importDemolitionResident(List<DemolitionResidentFormDto> demolitionResidentList, Boolean isUpdateSupport, String operName);
}
