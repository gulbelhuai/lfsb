package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.TeacherSubsidy;
import com.ruoyi.shebao.dto.TeacherSubsidyFormDto;
import com.ruoyi.shebao.dto.TeacherSubsidyListReq;
import com.ruoyi.shebao.dto.TeacherSubsidyListResp;

/**
 * 教龄补助Service接口
 *
 * @author ruoyi
 * @date 2026-01-24
 */
public interface TeacherSubsidyService extends IService<TeacherSubsidy>
{
    /**
     * 查询教龄补助列表
     */
    Page<TeacherSubsidyListResp> selectTeacherSubsidyList(TeacherSubsidyListReq req);

    /**
     * 查询教龄补助表单详情（包含基础信息）
     */
    TeacherSubsidyFormDto selectTeacherSubsidyFormById(Long id);

    /**
     * 根据身份证号查询基础信息并自动填充
     */
    TeacherSubsidyFormDto getFormDataByIdCardNo(String idCardNo);

    /**
     * 新增教龄补助登记（智能处理基础信息）
     */
    int insertTeacherSubsidy(TeacherSubsidyFormDto formDto);

    /**
     * 修改教龄补助登记（智能处理基础信息）
     */
    int updateTeacherSubsidy(TeacherSubsidyFormDto formDto);

    /**
     * 批量删除教龄补助登记（逻辑删除）
     */
    int deleteTeacherSubsidyByIds(Long[] ids);
}

