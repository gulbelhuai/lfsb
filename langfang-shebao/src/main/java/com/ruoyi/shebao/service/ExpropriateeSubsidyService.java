package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.ExpropriateeSubsidy;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyListReq;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyListResp;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyFormDto;

import java.util.List;

/**
 * 被征地参保补贴Service接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
public interface ExpropriateeSubsidyService extends IService<ExpropriateeSubsidy>
{
    /**
     * 查询被征地参保补贴列表
     * 
     * @param req 查询条件
     * @return 被征地参保补贴列表
     */
    Page<ExpropriateeSubsidyListResp> selectExpropriateeSubsidyList(ExpropriateeSubsidyListReq req);

    /**
     * 查询被征地参保补贴详情（包含基础信息）
     * 
     * @param id 被征地参保补贴主键
     * @return 被征地参保补贴信息
     */
    ExpropriateeSubsidyFormDto selectExpropriateeSubsidyFormById(Long id);

    /**
     * 新增被征地参保补贴（智能处理基础信息）
     * 
     * @param formDto 被征地参保补贴表单数据
     * @return 结果
     */
    int insertExpropriateeSubsidy(ExpropriateeSubsidyFormDto formDto);

    /**
     * 修改被征地参保补贴（智能处理基础信息）
     * 
     * @param formDto 被征地参保补贴表单数据
     * @return 结果
     */
    int updateExpropriateeSubsidy(ExpropriateeSubsidyFormDto formDto);

    /**
     * 批量删除被征地参保补贴
     * 
     * @param ids 需要删除的被征地参保补贴主键集合
     * @return 结果
     */
    int deleteExpropriateeSubsidyByIds(Long[] ids);

    /**
     * 删除被征地参保补贴信息
     * 
     * @param id 被征地参保补贴主键
     * @return 结果
     */
    int deleteExpropriateeSubsidyById(Long id);

    /**
     * 根据身份证号查询基础信息并自动填充
     * 
     * @param idCardNo 身份证号
     * @return 表单数据
     */
    ExpropriateeSubsidyFormDto getFormDataByIdCardNo(String idCardNo);

    /**
     * 批量导入被征地参保补贴
     * 
     * @param expropriateeSubsidyList 被征地参保补贴列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    String importExpropriateeSubsidy(List<ExpropriateeSubsidyFormDto> expropriateeSubsidyList, Boolean isUpdateSupport, String operName);
}
