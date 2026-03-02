package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 人员注销登记对象 shebao_person_cancel
 *
 * 说明：记录注销（死亡）登记历史，支持独立列表与增删改查。
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_person_cancel")
public class PersonCancel extends BaseDomain
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID（关联shebao_subsidy_person.id） */
    private Long subsidyPersonId;

    /** 死亡时间 */
    private LocalDate deathDate;
}

