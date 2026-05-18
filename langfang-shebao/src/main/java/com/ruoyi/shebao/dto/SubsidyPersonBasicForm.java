package com.ruoyi.shebao.dto;

import java.time.LocalDate;

/**
 * 补贴登记表单中的被补贴人基础信息字段（四类补贴共用）
 */
public interface SubsidyPersonBasicForm
{
    Long getId();

    Long getSubsidyPersonId();

    Boolean getPersonExists();

    String getName();

    String getGender();

    String getIdCardNo();

    LocalDate getBirthday();

    String getHouseholdRegistration();

    String getHomeAddress();

    String getPhone();

    Long getStreetOfficeId();

    Long getVillageCommitteeId();

    String getUserCode();
}
