package com.ruoyi.shebao.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 补贴类型枚举单元测试
 * 对照需求：五种补贴类型（失地、被征地、拆迁、村干部、教龄补助）
 */
class SubsidyTypeEnumTest {

    @Test
    @DisplayName("按编码获取枚举")
    void getByCode() {
        assertEquals(SubsidyTypeEnum.LAND_LOSS, SubsidyTypeEnum.getByCode("1"));
        assertEquals(SubsidyTypeEnum.EXPROPRIATEE, SubsidyTypeEnum.getByCode("2"));
        assertEquals(SubsidyTypeEnum.DEMOLITION, SubsidyTypeEnum.getByCode("3"));
        assertEquals(SubsidyTypeEnum.VILLAGE_OFFICIAL, SubsidyTypeEnum.getByCode("4"));
        assertEquals(SubsidyTypeEnum.TEACHER, SubsidyTypeEnum.getByCode("5")); // 需求：类型5为教龄补助
    }

    @Test
    @DisplayName("按表名获取枚举")
    void getByTableName() {
        assertEquals(SubsidyTypeEnum.LAND_LOSS, SubsidyTypeEnum.getByTableName("shebao_land_loss_resident"));
        assertEquals(SubsidyTypeEnum.VILLAGE_OFFICIAL, SubsidyTypeEnum.getByTableName("shebao_village_official"));
    }

    @Test
    @DisplayName("需求规定应有5种补贴类型含教龄补助")
    void shouldHaveFiveTypesIncludingTeacher() {
        // 用户需求说明书：5.教师补贴-教龄补助
        SubsidyTypeEnum teacher = SubsidyTypeEnum.getByCode("5");
        assertNotNull(teacher, "需求规定补贴类型5为教龄补助，应在枚举中定义");
        assertEquals("教龄补助", teacher.getName());
        assertEquals(SubsidyTypeEnum.TEACHER, teacher);
    }
}
