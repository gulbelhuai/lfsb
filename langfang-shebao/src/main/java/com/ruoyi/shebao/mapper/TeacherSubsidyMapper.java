package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.TeacherSubsidy;
import com.ruoyi.shebao.dto.TeacherSubsidyFormDto;
import com.ruoyi.shebao.dto.TeacherSubsidyListReq;
import com.ruoyi.shebao.dto.TeacherSubsidyListResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 教龄补助Mapper接口
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@Mapper
public interface TeacherSubsidyMapper extends BaseMapper<TeacherSubsidy>
{
    /**
     * 查询教龄补助列表（包含基础信息字段）
     *
     * @param page 分页对象
     * @param req 查询条件
     * @return 列表分页
     */
    Page<TeacherSubsidyListResp> selectTeacherSubsidyList(Page<TeacherSubsidyListResp> page, @Param("req") TeacherSubsidyListReq req);

    /**
     * 查询教龄补助表单（包含基础信息字段）
     *
     * @param id 主键
     * @return 表单DTO
     */
    TeacherSubsidyFormDto selectTeacherSubsidyFormById(@Param("id") Long id);
}

