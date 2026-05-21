-- 被征地参保补贴：已申领金额、补贴余额（手动录入）
ALTER TABLE `shebao_expropriatee_subsidy`
    ADD COLUMN `claimed_amount` decimal(10, 2) DEFAULT NULL COMMENT '已申领金额' AFTER `subsidy_amount`,
    ADD COLUMN `subsidy_balance` decimal(10, 2) DEFAULT NULL COMMENT '补贴余额' AFTER `claimed_amount`;
