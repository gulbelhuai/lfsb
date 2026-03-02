package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 人员注销登记列表响应
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@Data
public class PersonCancelListResp
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID */
    private Long subsidyPersonId;

    /** 用户编号 */
    @Excel(name = "用户编号", sort = 1, cellType = Excel.ColumnType.TEXT)
    private String userCode;

    /** 姓名 */
    @Excel(name = "姓名", sort = 2)
    private String name;

    /** 身份证号 */
    @Excel(name = "身份证号", sort = 3, cellType = Excel.ColumnType.TEXT)
    private String idCardNo;

    /** 死亡时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "死亡时间", sort = 4, dateFormat = "yyyy-MM-dd")
    private LocalDate deathDate;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", sort = 5, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 备注 */
    @Excel(name = "备注", sort = 6)
    private String remark;
}

