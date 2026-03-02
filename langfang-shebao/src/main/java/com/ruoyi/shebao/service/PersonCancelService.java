package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.PersonCancel;
import com.ruoyi.shebao.dto.PersonCancelFormDto;
import com.ruoyi.shebao.dto.PersonCancelListReq;
import com.ruoyi.shebao.dto.PersonCancelListResp;

import java.util.List;

/**
 * 人员注销登记Service接口
 *
 * @author ruoyi
 * @date 2026-01-24
 */
public interface PersonCancelService extends IService<PersonCancel>
{
    Page<PersonCancelListResp> selectPersonCancelList(PersonCancelListReq req);

    List<PersonCancelListResp> selectPersonCancelListNoPage(PersonCancelListReq req);

    PersonCancelFormDto selectPersonCancelFormById(Long id);

    int insertPersonCancel(PersonCancelFormDto formDto);

    int updatePersonCancel(PersonCancelFormDto formDto);

    int deletePersonCancelByIds(Long[] ids);
}

