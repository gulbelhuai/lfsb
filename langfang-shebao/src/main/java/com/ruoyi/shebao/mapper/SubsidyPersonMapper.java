package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.SubsidyPerson;
import com.ruoyi.shebao.dto.SubsidyPersonListReq;
import com.ruoyi.shebao.dto.SubsidyPersonListResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 被补贴人基础信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Mapper
public interface SubsidyPersonMapper extends BaseMapper<SubsidyPerson>
{

    /**
     * 检查被补贴人是否存在关联数据
     *
     * @param subsidyPersonId 被补贴人ID
     * @return 关联数据数量
     */
    int checkSubsidyPersonRelatedData(@Param("subsidyPersonId") Long subsidyPersonId);

}
