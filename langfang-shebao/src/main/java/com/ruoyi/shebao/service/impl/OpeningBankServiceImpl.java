package com.ruoyi.shebao.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.shebao.domain.OpeningBank;
import com.ruoyi.shebao.dto.OpeningBankListReq;
import com.ruoyi.shebao.dto.OpeningBankListResp;
import com.ruoyi.shebao.mapper.OpeningBankMapper;
import com.ruoyi.shebao.service.OpeningBankService;
import com.ruoyi.shebao.util.OpeningBankUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class OpeningBankServiceImpl extends ServiceImpl<OpeningBankMapper, OpeningBank> implements OpeningBankService
{
    @Override
    public Page<OpeningBankListResp> selectOpeningBankList(OpeningBankListReq req)
    {
        long pageNum = req == null ? 1L : req.pageNumOrDefault();
        long pageSize = req == null ? 10L : req.pageSizeOrDefault();
        Page<OpeningBank> page = new Page<>(pageNum, pageSize);
        Page<OpeningBank> entityPage = this.page(page, new LambdaQueryWrapper<OpeningBank>()
                .eq(OpeningBank::getDelFlag, "0")
                .like(req != null && StringUtils.isNotBlank(req.getCode()), OpeningBank::getCode, req == null ? null : req.getCode())
                .like(req != null && StringUtils.isNotBlank(req.getShortName()), OpeningBank::getShortName, req == null ? null : req.getShortName())
                .eq(req != null && StringUtils.isNotBlank(req.getStatus()), OpeningBank::getStatus, req == null ? null : req.getStatus())
                .orderByAsc(OpeningBank::getCode));
        Page<OpeningBankListResp> respPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        if (CollectionUtils.isNotEmpty(entityPage.getRecords()))
        {
            respPage.setRecords(entityPage.getRecords().stream()
                    .map(e -> BeanUtil.copyProperties(e, OpeningBankListResp.class))
                    .toList());
        }
        return respPage;
    }

    @Override
    public OpeningBank selectOpeningBankById(Long id)
    {
        return getById(id);
    }

    @Override
    public List<OpeningBank> selectActiveList()
    {
        return list(new LambdaQueryWrapper<OpeningBank>()
                .eq(OpeningBank::getDelFlag, "0")
                .eq(OpeningBank::getStatus, "0")
                .orderByAsc(OpeningBank::getCode));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOpeningBank(OpeningBank bank)
    {
        bank.setDelFlag("0");
        if (StringUtils.isBlank(bank.getStatus()))
        {
            bank.setStatus("0");
        }
        bank.setCreateBy(SecurityUtils.getUsername());
        bank.setCreateTime(LocalDateTime.now());
        bank.setUpdateBy(SecurityUtils.getUsername());
        bank.setUpdateTime(LocalDateTime.now());
        int rows = baseMapper.insert(bank);
        OpeningBankUtils.clearCache();
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOpeningBank(OpeningBank bank)
    {
        OpeningBank existing = getById(bank.getId());
        if (existing == null || !"0".equals(existing.getDelFlag()))
        {
            throw new ServiceException("开户行不存在");
        }
        // 编码不可改，避免历史引用断裂
        bank.setCode(existing.getCode());
        bank.setUpdateBy(SecurityUtils.getUsername());
        bank.setUpdateTime(LocalDateTime.now());
        int rows = baseMapper.updateById(bank);
        OpeningBankUtils.clearCache();
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOpeningBankByIds(Long[] ids)
    {
        if (ids == null || ids.length == 0)
        {
            return 0;
        }
        int rows = 0;
        for (Long id : ids)
        {
            OpeningBank bank = getById(id);
            if (bank == null || !"0".equals(bank.getDelFlag()))
            {
                continue;
            }
            int refs = baseMapper.countBusinessReferences(bank.getCode());
            if (refs > 0)
            {
                throw new ServiceException("开户行「" + bank.getShortName() + "」已被业务引用，禁止删除，可改为停用");
            }
            OpeningBank upd = new OpeningBank();
            upd.setId(id);
            upd.setDelFlag("2");
            upd.setUpdateBy(SecurityUtils.getUsername());
            upd.setUpdateTime(LocalDateTime.now());
            rows += baseMapper.updateById(upd);
        }
        OpeningBankUtils.clearCache();
        return rows;
    }

    @Override
    public String checkCodeUnique(OpeningBank bank)
    {
        Long id = bank.getId() == null ? -1L : bank.getId();
        OpeningBank info = getOne(new LambdaQueryWrapper<OpeningBank>()
                .eq(OpeningBank::getCode, bank.getCode())
                .eq(OpeningBank::getDelFlag, "0")
                .last("limit 1"));
        if (info != null && !Objects.equals(info.getId(), id))
        {
            return "1";
        }
        return "0";
    }

    @Override
    public OpeningBank getByCode(String code)
    {
        if (StringUtils.isBlank(code))
        {
            return null;
        }
        return getOne(new LambdaQueryWrapper<OpeningBank>()
                .eq(OpeningBank::getCode, code)
                .eq(OpeningBank::getDelFlag, "0")
                .last("limit 1"));
    }
}
