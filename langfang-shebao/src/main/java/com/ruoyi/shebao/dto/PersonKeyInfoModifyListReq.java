package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 人员关键信息修改申请列表查询请求
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonKeyInfoModifyListReq extends PageReq {

    /** 姓名 */
    private String name;
    /** 身份证号 */
    private String idCardNo;
    /** 审批状态 */
    private String approvalStatus;
}
