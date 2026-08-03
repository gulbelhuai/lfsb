package com.ruoyi.shebao.util;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.shebao.domain.OpeningBank;
import com.ruoyi.shebao.service.OpeningBankService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 开户行编码解析（替代原字典 shebao_grant_org）
 */
public final class OpeningBankUtils
{
    private static final Map<String, OpeningBank> CACHE = new ConcurrentHashMap<>();

    private OpeningBankUtils()
    {
    }

    public static void clearCache()
    {
        CACHE.clear();
    }

    public static OpeningBank getByCode(String code)
    {
        if (StringUtils.isBlank(code))
        {
            return null;
        }
        OpeningBank cached = CACHE.get(code);
        if (cached != null)
        {
            return cached;
        }
        OpeningBank bank = SpringUtils.getBean(OpeningBankService.class).getByCode(code);
        if (bank != null)
        {
            CACHE.put(code, bank);
        }
        return bank;
    }

    public static String getShortName(String code)
    {
        OpeningBank bank = getByCode(code);
        return bank == null ? null : bank.getShortName();
    }

    public static String getFullName(String code)
    {
        OpeningBank bank = getByCode(code);
        return bank == null ? null : bank.getFullName();
    }

    public static String getBankNo(String code)
    {
        OpeningBank bank = getByCode(code);
        return bank == null ? null : bank.getBankNo();
    }

    /** 简称优先，找不到则回退编码 */
    public static String getShortNameOrCode(String code)
    {
        String name = getShortName(code);
        return StringUtils.isNotBlank(name) ? name : code;
    }

    /**
     * 历史导入：按编码或简称解析为 code；仅正常状态可用
     */
    public static String requireCodeByLabelOrValue(String raw, String fieldName)
    {
        if (StringUtils.isBlank(raw))
        {
            throw new ServiceException(fieldName + "不能为空");
        }
        String trimmed = raw.trim();
        OpeningBank byCode = getByCode(trimmed);
        if (byCode != null)
        {
            if (!"0".equals(byCode.getStatus()))
            {
                throw new ServiceException(fieldName + "已停用：" + trimmed);
            }
            return byCode.getCode();
        }
        List<OpeningBank> all = SpringUtils.getBean(OpeningBankService.class).list();
        for (OpeningBank bank : all)
        {
            if (!"0".equals(bank.getDelFlag()))
            {
                continue;
            }
            if (trimmed.equals(bank.getShortName()) || trimmed.equals(bank.getFullName()))
            {
                if (!"0".equals(bank.getStatus()))
                {
                    throw new ServiceException(fieldName + "已停用：" + trimmed);
                }
                CACHE.put(bank.getCode(), bank);
                return bank.getCode();
            }
        }
        throw new ServiceException(fieldName + "不在开户行范围内：" + trimmed);
    }

    public static List<String> activeShortNames()
    {
        List<OpeningBank> list = SpringUtils.getBean(OpeningBankService.class).selectActiveList();
        if (list == null || list.isEmpty())
        {
            return Collections.emptyList();
        }
        return list.stream().map(OpeningBank::getShortName).collect(Collectors.toList());
    }
}
