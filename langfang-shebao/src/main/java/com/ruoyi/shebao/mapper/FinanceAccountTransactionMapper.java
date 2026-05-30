package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.shebao.domain.FinanceAccountTransaction;
import com.ruoyi.shebao.dto.FinanceAccountTransactionListReq;
import com.ruoyi.shebao.dto.FinanceAccountTransactionListResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FinanceAccountTransactionMapper extends BaseMapper<FinanceAccountTransaction>
{
    Page<FinanceAccountTransactionListResp> selectTransactionList(Page<FinanceAccountTransactionListResp> page,
                                                                  @Param("req") FinanceAccountTransactionListReq req);
}
