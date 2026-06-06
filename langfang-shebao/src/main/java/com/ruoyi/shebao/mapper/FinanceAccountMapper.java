package com.ruoyi.shebao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.shebao.domain.FinanceAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务账户Mapper接口
 */
@Mapper
public interface FinanceAccountMapper extends BaseMapper<FinanceAccount>
{
    FinanceAccount selectBySubsidyType(@Param("subsidyType") String subsidyType);

    /**
     * 原子扣减账户余额（须 balance >= amount），返回影响行数
     */
    int deductBalance(@Param("id") Long id,
                    @Param("amount") BigDecimal amount,
                    @Param("updateBy") String updateBy,
                    @Param("updateTime") LocalDateTime updateTime);

    /**
     * 原子增加账户余额，返回影响行数
     */
    int addBalance(@Param("id") Long id,
                   @Param("amount") BigDecimal amount,
                   @Param("updateBy") String updateBy,
                   @Param("updateTime") LocalDateTime updateTime);
}
