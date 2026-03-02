package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.PersonKeyInfoModify;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyListReq;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyListResp;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyFormDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 人员关键信息修改申请Mapper
 *
 * @author ruoyi
 */
@Mapper
public interface PersonKeyInfoModifyMapper extends BaseMapper<PersonKeyInfoModify> {

    Page<PersonKeyInfoModifyListResp> selectModifyList(Page<PersonKeyInfoModifyListResp> page, @Param("req") PersonKeyInfoModifyListReq req);

    PersonKeyInfoModifyFormDto selectFormById(@Param("id") Long id);
}
