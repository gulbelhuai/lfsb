package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教龄补助信息对象 shebao_teacher_subsidy（原“教师补贴”）
 *
 * @author ruoyi
 * @date 2026-01-24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_teacher_subsidy")
public class TeacherSubsidy extends BaseDomain
{
    /** 主键ID */
    private Long id;

    /** 被补贴人ID（关联shebao_subsidy_person.id） */
    @Excel(name = "被补贴人ID")
    private Long subsidyPersonId;

    /** 学校名称 */
    @Excel(name = "学校名称")
    private String schoolName;

    /** 任教年限 */
    @Excel(name = "任教年限")
    private Integer teachingYears;
}

