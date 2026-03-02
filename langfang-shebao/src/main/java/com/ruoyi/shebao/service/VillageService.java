package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.Village;
import com.ruoyi.shebao.dto.VillageListReq;
import com.ruoyi.shebao.dto.VillageListResp;

import java.util.List;

/**
 * 村级单位Service接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
public interface VillageService extends IService<Village>
{
    /**
     * 查询村级单位列表
     * 
     * @param req 查询条件
     * @return 村级单位列表
     */
    Page<VillageListResp> selectVillageList(VillageListReq req);

    /**
     * 查询村级单位详情
     * 
     * @param id 村级单位主键
     * @return 村级单位
     */
    Village selectVillageById(Long id);

    /**
     * 新增村级单位
     * 
     * @param village 村级单位
     * @return 结果
     */
    int insertVillage(Village village);

    /**
     * 修改村级单位
     * 
     * @param village 村级单位
     * @return 结果
     */
    int updateVillage(Village village);

    /**
     * 批量删除村级单位
     * 
     * @param ids 需要删除的村级单位主键集合
     * @return 结果
     */
    int deleteVillageByIds(Long[] ids);

    /**
     * 删除村级单位信息
     * 
     * @param id 村级单位主键
     * @return 结果
     */
    int deleteVillageById(Long id);

    /**
     * 根据村编码查询村级单位
     * 
     * @param villageCode 村编码
     * @return 村级单位
     */
    Village selectVillageByCode(String villageCode);

    /**
     * 校验村编码是否唯一
     * 
     * @param village 村级单位信息
     * @return 结果
     */
    String checkVillageCodeUnique(Village village);

    /**
     * 检查村级单位是否存在关联数据
     * 
     * @param villageId 村级单位ID
     * @return 结果
     */
    boolean checkVillageRelatedData(Long villageId);

    /**
     * 批量导入村级单位
     * 
     * @param villageList 村级单位列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importVillage(List<Village> villageList, Boolean isUpdateSupport, String operName);
}
