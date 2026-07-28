package com.ruoyi.shebao.service.historicalimport;

/**
 * 历史导入模板公共列（失地/拆迁共用：基本信息、待遇核定、待遇暂停）
 */
public interface HistoricalImportCommonColumns
{
    String getName();

    String getIdCardNo();

    String getHouseholdRegistration();

    String getHomeAddress();

    String getPhone();

    String getStreetOfficeName();

    String getVillageCommitteeName();

    String getSubsidyStatus();

    String getPersonStatus();

    String getCancelTime();

    String getCancelReason();

    String getIsVillageCoopMember();

    String getRemark();

    String getGrantOrg();

    String getAccountName();

    String getRelationToInsured();

    String getBankAccount();

    String getSubsidyStandard();

    String getBenefitStartMonth();

    Integer getBenefitMonths();

    String getBenefitAmount();

    String getPauseMonth();

    String getPauseReason();

    String getPauseReasonRemark();

    String getRecoverStartMonth();

    String getRecoverEndMonth();

    String getRecoverAmount();
}
