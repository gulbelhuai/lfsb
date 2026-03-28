package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 村干部任职信息对象 shebao_village_official_position
 * 
 * @author ruoyi
 * @date 2025-09-27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_village_official_position")
public class VillageOfficialPosition extends BaseDomain
{
    /** 主键ID */
    private Long id;

    /** 村干部信息ID（关联shebao_village_official.id） */
    @Excel(name = "村干部信息ID")
    private Long villageOfficialId;

    /** 任职职位（1书记或主任 2书记兼主任 3村"两委"其他成员） */
    @Excel(name = "任职职位", readConverterExp = "1=书记或主任,2=书记兼主任,3=村\"两委\"其他成员")
    private String position;

    /** 上任时间 */
    @Excel(name = "上任时间", dateFormat = "yyyy-MM-dd")
    private LocalDate startDate;

    /** 卸任时间 */
    @Excel(name = "卸任时间", dateFormat = "yyyy-MM-dd")
    private LocalDate endDate;

    /** 任职年限：整年数 + 余下天数/365，保留两位小数 */
    @Excel(name = "任职年限")
    private BigDecimal serviceYears;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
}
