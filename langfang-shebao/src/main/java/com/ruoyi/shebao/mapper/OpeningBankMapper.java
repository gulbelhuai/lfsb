package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.shebao.domain.OpeningBank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OpeningBankMapper extends BaseMapper<OpeningBank>
{
    /** 统计业务引用次数（核定 + 支付计划明细 + 汇总） */
    int countBusinessReferences(@Param("code") String code);
}
