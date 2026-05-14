SET NAMES utf8mb4;

-- 支付计划主表：批次号（首次保存生成，之后不修改）
ALTER TABLE `shebao_payment_plan`
  ADD COLUMN `batch_no` varchar(16) DEFAULT NULL COMMENT '批次号(yyyyMM+类型01/02+三位序号)' AFTER `business_period`;

ALTER TABLE `shebao_payment_plan`
  ADD UNIQUE KEY `uk_payment_plan_batch_no` (`batch_no`);
