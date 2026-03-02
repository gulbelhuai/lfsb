package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.StreetOffice;
import com.ruoyi.shebao.dto.StreetOfficeListReq;
import com.ruoyi.shebao.dto.StreetOfficeListResp;

import java.util.List;

/**
 * 街道办事处信息Service接口
 * 
 * @author ruoyi
 * @date 2025-10-18
 */
public interface StreetOfficeService extends IService<StreetOffice>
{
    /**
     * 查询街道办事处信息列表
     * 
     * @param req 查询条件
     * @return 街道办事处信息列表
     */
    Page<StreetOfficeListResp> selectStreetOfficeList(StreetOfficeListReq req);

    /**
     * 查询街道办事处信息详情
     * 
     * @param id 街道办事处信息主键
     * @return 街道办事处信息
     */
    StreetOffice selectStreetOfficeById(Long id);

    /**
     * 新增街道办事处信息
     * 
     * @param streetOffice 街道办事处信息
     * @return 结果
     */
    int insertStreetOffice(StreetOffice streetOffice);

    /**
     * 修改街道办事处信息
     * 
     * @param streetOffice 街道办事处信息
     * @return 结果
     */
    int updateStreetOffice(StreetOffice streetOffice);

    /**
     * 批量删除街道办事处信息
     * 
     * @param ids 需要删除的街道办事处信息主键集合
     * @return 结果
     */
    int deleteStreetOfficeByIds(Long[] ids);

    /**
     * 删除街道办事处信息信息
     * 
     * @param id 街道办事处信息主键
     * @return 结果
     */
    int deleteStreetOfficeById(Long id);

    /**
     * 校验街道办编码是否唯一
     * 
     * @param streetOffice 街道办事处信息
     * @return 结果
     */
    String checkStreetCodeUnique(StreetOffice streetOffice);

    /**
     * 校验街道办名称是否唯一
     * 
     * @param streetOffice 街道办事处信息
     * @return 结果
     */
    String checkStreetNameUnique(StreetOffice streetOffice);

    /**
     * 获取所有正常的街道办列表（用于下拉选择）
     * 
     * @return 街道办列表
     */
    List<StreetOffice> selectNormalStreetOfficeList();
}
