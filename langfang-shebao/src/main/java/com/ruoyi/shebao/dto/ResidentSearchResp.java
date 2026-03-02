package com.ruoyi.shebao.dto;

import lombok.Data;

/**
 * 居民搜索结果DTO
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
@Data
public class ResidentSearchResp
{
    /** 居民ID */
    private Long subsidyPersonId;

    /** 姓名 */
    private String name;

    /** 身份证号 */
    private String idCardNo;
}