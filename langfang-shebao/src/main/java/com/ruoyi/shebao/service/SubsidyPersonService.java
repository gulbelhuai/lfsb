package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.SubsidyPersonCancelReq;
import com.ruoyi.shebao.dto.SubsidyPersonListReq;
import com.ruoyi.shebao.dto.SubsidyPersonListResp;

import java.util.List;

/**
 * 被补贴人基础信息Service接口
 *
 * @author ruoyi
 * @date 2025-09-27
 */
public interface SubsidyPersonService extends IService<SubsidyPerson>
{
    /**
     * 查询被补贴人基础信息列表
     *
     * @param req 查询条件
     * @return 被补贴人基础信息列表
     */
    Page<SubsidyPersonListResp> selectSubsidyPersonList(SubsidyPersonListReq req);

    /**
     * 查询被补贴人基础信息详情
     *
     * @param id 被补贴人基础信息主键
     * @return 被补贴人基础信息
     */
    SubsidyPerson selectSubsidyPersonById(Long id);

    /**
     * 新增被补贴人基础信息
     *
     * @param subsidyPerson 被补贴人基础信息
     * @return 结果
     */
    int insertSubsidyPerson(SubsidyPerson subsidyPerson);

    /**
     * 修改被补贴人基础信息
     *
     * @param subsidyPerson 被补贴人基础信息
     * @return 结果
     */
    int updateSubsidyPerson(SubsidyPerson subsidyPerson);

    /**
     * 批量删除被补贴人基础信息
     *
     * @param ids 需要删除的被补贴人基础信息主键集合
     * @return 结果
     */
    int deleteSubsidyPersonByIds(Long[] ids);

    /**
     * 删除被补贴人基础信息信息
     *
     * @param id 被补贴人基础信息主键
     * @return 结果
     */
    int deleteSubsidyPersonById(Long id);

    /**
     * 校验身份证号是否唯一
     *
     * @param subsidyPerson 被补贴人基础信息
     * @return 结果
     */
    String checkIdCardNoUnique(SubsidyPerson subsidyPerson);

    /**
     * 批量导入被补贴人基础信息
     *
     * @param subsidyPersonList 被补贴人基础信息列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importSubsidyPerson(List<SubsidyPerson> subsidyPersonList, Boolean isUpdateSupport, String operName);

    /**
     * 生成用户编号
     *
     * @param streetOfficeId 街道办ID
     * @param villageCommitteeId 村委会ID
     * @return 用户编号
     */
    String generateUserCode(Long streetOfficeId, Long villageCommitteeId);

    /**
     * 校验用户编号是否唯一
     *
     * @param userCode 用户编号
     * @param id 当前记录ID（修改时使用）
     * @return 结果
     */
    String checkUserCodeUnique(String userCode, Long id);

    /**
     * 根据身份证号查询被补贴人基础信息
     *
     * @param idCardNo 身份证号
     * @return 被补贴人基础信息
     */
    SubsidyPerson selectSubsidyPersonByIdCardNo(String idCardNo);

    /**
     * 根据身份证号查询“有效人员”（排除注销人员：is_alive=0）
     *
     * 用于各业务办理场景的“选人/回填”。
     *
     * @param idCardNo 身份证号
     * @return 健在人员；若人员不存在或已注销则返回null
     */
    SubsidyPerson selectAliveSubsidyPersonByIdCardNo(String idCardNo);

    /**
     * 根据ID查询"有效人员"（排除注销人员：is_alive=0）
     *
     * 用于各业务办理场景的"选人/回填"。
     *
     * @param id 人员ID
     * @return 健在人员；若人员不存在或已注销则返回null
     */
    SubsidyPerson selectAliveSubsidyPersonById(Long id);

    /**
     * 人员注销登记（标记死亡）
     *
     * @param req 注销登记请求
     * @return 结果
     */
    int cancelByIdCardNo(SubsidyPersonCancelReq req);
}
