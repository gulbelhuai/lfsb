package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.OpeningBank;
import com.ruoyi.shebao.dto.OpeningBankListReq;
import com.ruoyi.shebao.dto.OpeningBankListResp;

import java.util.List;

public interface OpeningBankService extends IService<OpeningBank>
{
    Page<OpeningBankListResp> selectOpeningBankList(OpeningBankListReq req);

    OpeningBank selectOpeningBankById(Long id);

    /** 下拉：仅正常状态，形状兼容字典 {value,label} */
    List<OpeningBank> selectActiveList();

    int insertOpeningBank(OpeningBank bank);

    int updateOpeningBank(OpeningBank bank);

    int deleteOpeningBankByIds(Long[] ids);

    String checkCodeUnique(OpeningBank bank);

    OpeningBank getByCode(String code);
}
