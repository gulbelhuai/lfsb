package com.ruoyi.shebao.dto;

import com.ruoyi.common.core.page.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 拆迁居民信息查询请求
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DemolitionResidentListReq extends PageReq
{
    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;

    /** 所属街道办ID */
    private Long streetOfficeId;

    /** 所属村委会ID */
    private Long villageCommitteeId;

    /** 用户编号 */
    private String userCode;

    /** 拆迁时间-开始 */
    private LocalDate demolitionTimeStart;

    /** 拆迁时间-结束 */
    private LocalDate demolitionTimeEnd;

    /** 认定为拆迁居民时间-开始 */
    private LocalDate recognitionTimeStart;

    /** 认定为拆迁居民时间-结束 */
    private LocalDate recognitionTimeEnd;

    /** 状态（0正常 1停用） */
    private String status;
}
