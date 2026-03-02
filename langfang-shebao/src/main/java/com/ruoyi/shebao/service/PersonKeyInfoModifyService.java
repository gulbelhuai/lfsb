package com.ruoyi.shebao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.shebao.domain.PersonKeyInfoModify;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyFormDto;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyListReq;
import com.ruoyi.shebao.dto.PersonKeyInfoModifyListResp;

/**
 * 人员关键信息修改申请Service接口
 * 经办-复核-审批 三级
 *
 * @author ruoyi
 */
public interface PersonKeyInfoModifyService extends IService<PersonKeyInfoModify> {

    Page<PersonKeyInfoModifyListResp> selectModifyList(PersonKeyInfoModifyListReq req);

    PersonKeyInfoModifyFormDto selectFormById(Long id);

    /** 保存或新增（草稿） */
    long saveOrUpdateDraft(PersonKeyInfoModifyFormDto form);

    /** 提交：草稿 -> 待复核 */
    int submit(Long id);

    /** 复核：待复核 -> 待审批 或 草稿（驳回） */
    int review(Long id, boolean approved, String remark);

    /** 审批：待审批 -> 已通过 或 待复核（驳回）；通过时回写 subsidy_person */
    int approve(Long id, boolean approved, String remark);
}
