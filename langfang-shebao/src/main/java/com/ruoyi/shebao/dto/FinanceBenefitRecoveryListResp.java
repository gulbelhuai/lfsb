package com.ruoyi.shebao.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class FinanceBenefitRecoveryListResp
{
    private Long id;
    private Long subsidyPersonId;
    private String personName;
    private String idCardNo;
    private String subsidyType;
    private Long suspensionId;

    @JsonFormat(pattern = "yyyy-MM")
    private Date recoverStartMonth;

    @JsonFormat(pattern = "yyyy-MM")
    private Date recoverEndMonth;

    private BigDecimal recoverAmount;

    /** 0未追回 1已追回 */
    private String recoveryStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recoveryTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
