package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 人员注销登记列表查询请求
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonCancelListReq extends PageReq
{
    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 用户编号 */
    private String userCode;

    /** 死亡时间-开始 */
    private LocalDate deathDateStart;

    /** 死亡时间-结束 */
    private LocalDate deathDateEnd;
}

