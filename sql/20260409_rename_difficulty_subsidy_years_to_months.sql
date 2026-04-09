-- 被征地参保补贴：困难补贴年限 -> 困难补贴月数
ALTER TABLE shebao_expropriatee_subsidy
    CHANGE COLUMN difficulty_subsidy_years difficulty_subsidy_months int DEFAULT 0 NULL COMMENT '已领取困难人员社会保险补贴月数';
