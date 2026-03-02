package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 人员注销登记（死亡）请求
 *
 * 按身份证号查询人员后，登记死亡时间与备注，并将人员标记为死亡状态。
 */
@Data
public class SubsidyPersonCancelReq {

    /** 身份证号 */
    private String idCardNo;

    /** 死亡时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deathDate;

    /** 备注 */
    private String remark;
}

