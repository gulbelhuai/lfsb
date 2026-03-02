package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.dto.VillageCommitteeListReq;
import com.ruoyi.shebao.dto.VillageCommitteeListResp;

import java.util.List;

/**
 * 村委会信息Service接口
 *
 * @author ruoyi
 * @date 2025-10-18
 */
public interface VillageCommitteeService extends IService<VillageCommittee>
{
    /**
     * 查询村委会信息列表
     *
     * @param req 查询条件
     * @return 村委会信息列表
     */
    Page<VillageCommitteeListResp> selectVillageCommitteeList(VillageCommitteeListReq req);

    /**
     * 查询村委会信息详情
     *
     * @param id 村委会信息主键
     * @return 村委会信息
     */
    VillageCommittee selectVillageCommitteeById(Long id);

    /**
     * 新增村委会信息
     *
     * @param villageCommittee 村委会信息
     * @return 结果
     */
    int insertVillageCommittee(VillageCommittee villageCommittee);

    /**
     * 修改村委会信息
     *
     * @param villageCommittee 村委会信息
     * @return 结果
     */
    int updateVillageCommittee(VillageCommittee villageCommittee);

    /**
     * 批量删除村委会信息
     *
     * @param ids 需要删除的村委会信息主键集合
     * @return 结果
     */
    int deleteVillageCommitteeByIds(Long[] ids);

    /**
     * 删除村委会信息信息
     *
     * @param id 村委会信息主键
     * @return 结果
     */
    int deleteVillageCommitteeById(Long id);

    /**
     * 校验村委会编码是否唯一
     *
     * @param villageCommittee 村委会信息
     * @return 结果
     */
    String checkVillageCodeUnique(VillageCommittee villageCommittee);

    /**
     * 校验村委会名称是否唯一
     *
     * @param villageCommittee 村委会信息
     * @return 结果
     */
    String checkVillageNameUnique(VillageCommittee villageCommittee);

    /**
     * 获取所有正常的村委会列表（用于下拉选择）
     *
     * @return 村委会列表
     */
    List<VillageCommittee> selectNormalVillageCommitteeList();

    /**
     * 根据街道办ID获取村委会列表（用于下拉选择）
     *
     * @param streetOfficeId 街道办ID
     * @return 村委会列表
     */
    List<VillageCommittee> selectVillageCommitteeListByStreetOfficeId(Long streetOfficeId);
}
