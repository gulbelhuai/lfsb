package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.ExpropriateeSubsidy;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyListReq;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyListResp;
import com.ruoyi.shebao.dto.ExpropriateeSubsidyFormDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 被征地参保补贴Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Mapper
public interface ExpropriateeSubsidyMapper extends BaseMapper<ExpropriateeSubsidy>
{
    /**
     * 查询被征地参保补贴列表
     * 
     * @param page 分页对象
     * @param req 查询条件
     * @return 被征地参保补贴集合
     */
    Page<ExpropriateeSubsidyListResp> selectExpropriateeSubsidyList(Page<ExpropriateeSubsidyListResp> page, @Param("req") ExpropriateeSubsidyListReq req);

    /**
     * 查询被征地参保补贴详细信息（包含基础信息）
     * 
     * @param id 被征地参保补贴主键
     * @return 被征地参保补贴详细信息
     */
    ExpropriateeSubsidyFormDto selectExpropriateeSubsidyFormById(@Param("id") Long id);

    /**
     * 根据被补贴人ID查询被征地参保补贴列表
     * 
     * @param subsidyPersonId 被补贴人ID
     * @return 被征地参保补贴列表
     */
    List<ExpropriateeSubsidy> selectBySubsidyPersonId(@Param("subsidyPersonId") Long subsidyPersonId);

    /**
     * 批量插入被征地参保补贴
     * 
     * @param expropriateeSubsidyList 被征地参保补贴列表
     * @return 插入数量
     */
    int batchInsertExpropriateeSubsidy(@Param("list") List<ExpropriateeSubsidy> expropriateeSubsidyList);
}
