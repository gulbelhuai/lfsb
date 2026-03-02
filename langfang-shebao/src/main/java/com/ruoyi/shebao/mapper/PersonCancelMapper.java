package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.PersonCancel;
import com.ruoyi.shebao.dto.PersonCancelFormDto;
import com.ruoyi.shebao.dto.PersonCancelListReq;
import com.ruoyi.shebao.dto.PersonCancelListResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 人员注销登记Mapper接口
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@Mapper
public interface PersonCancelMapper extends BaseMapper<PersonCancel>
{
    Page<PersonCancelListResp> selectPersonCancelList(Page<PersonCancelListResp> page, @Param("req") PersonCancelListReq req);

    PersonCancelFormDto selectPersonCancelFormById(@Param("id") Long id);

    List<PersonCancelListResp> selectPersonCancelListNoPage(@Param("req") PersonCancelListReq req);

    int countActiveByPersonId(@Param("subsidyPersonId") Long subsidyPersonId);

    LocalDate selectMaxDeathDateByPersonId(@Param("subsidyPersonId") Long subsidyPersonId);
}

