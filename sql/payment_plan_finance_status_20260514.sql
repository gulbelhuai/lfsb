SET NAMES utf8mb4;

-- 支付计划：财务流程状态（空=未进入财务；后续页面可推进待复核/待审批/已通过）
ALTER TABLE `shebao_payment_plan`
  ADD COLUMN `finance_status` varchar(32) DEFAULT NULL COMMENT '财务状态(pending_finance/finance_pending_review/finance_pending_approve/finance_approved/finance_rejected)' AFTER `approval_status`;

-- 审核记录：区分补贴审核与财务审核
ALTER TABLE `shebao_payment_plan_audit`
  ADD COLUMN `approval_stage` varchar(20) DEFAULT NULL COMMENT 'subsidy=补贴审核, finance=财务审核' AFTER `operation_status`;

UPDATE `shebao_payment_plan_audit`
SET `approval_stage` = 'subsidy'
WHERE `approval_stage` IS NULL;
