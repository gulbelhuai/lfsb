package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教龄补助查询请求
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TeacherSubsidyListReq extends PageReq
{
    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 用户编号 */
    private String userCode;

    /** 学校名称 */
    private String schoolName;

    /** 所属街道办ID */
    private Long streetOfficeId;

    /** 所属村委会ID */
    private Long villageCommitteeId;
}

