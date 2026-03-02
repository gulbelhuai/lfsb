package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.VillageOfficial;
import com.ruoyi.shebao.dto.VillageOfficialListReq;
import com.ruoyi.shebao.dto.VillageOfficialListResp;
import com.ruoyi.shebao.dto.VillageOfficialFormDto;

import java.util.List;

/**
 * 村干部信息Service接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
public interface VillageOfficialService extends IService<VillageOfficial>
{
    /**
     * 查询村干部信息列表
     * 
     * @param req 查询条件
     * @return 村干部信息列表
     */
    Page<VillageOfficialListResp> selectVillageOfficialList(VillageOfficialListReq req);

    /**
     * 查询村干部信息详情（包含基础信息和任职信息）
     * 
     * @param id 村干部信息主键
     * @return 村干部信息
     */
    VillageOfficialFormDto selectVillageOfficialFormById(Long id);

    /**
     * 新增村干部信息（智能处理基础信息和任职信息）
     * 
     * @param formDto 村干部表单数据
     * @return 结果
     */
    int insertVillageOfficial(VillageOfficialFormDto formDto);

    /**
     * 修改村干部信息（智能处理基础信息和任职信息）
     * 
     * @param formDto 村干部表单数据
     * @return 结果
     */
    int updateVillageOfficial(VillageOfficialFormDto formDto);

    /**
     * 批量删除村干部信息
     * 
     * @param ids 需要删除的村干部信息主键集合
     * @return 结果
     */
    int deleteVillageOfficialByIds(Long[] ids);

    /**
     * 删除村干部信息信息
     * 
     * @param id 村干部信息主键
     * @return 结果
     */
    int deleteVillageOfficialById(Long id);

    /**
     * 根据身份证号查询基础信息并自动填充
     * 
     * @param idCardNo 身份证号
     * @return 表单数据
     */
    VillageOfficialFormDto getFormDataByIdCardNo(String idCardNo);

    /**
     * 批量导入村干部信息
     * 
     * @param villageOfficialList 村干部信息列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importVillageOfficial(List<VillageOfficialFormDto> villageOfficialList, Boolean isUpdateSupport, String operName);
}
