SET NAMES utf8mb4;

-- =====================================================================
-- 支付计划汇总：由「补贴类型+发放机构」改为「补贴类型+村委会」
-- 1) 增加 village_name
-- 2) 按明细重建全部历史汇总
-- 3) 清空汇总表 grant_org（列表发放机构改从明细聚合）
-- =====================================================================

SET @db := DATABASE();
SET @col_exists := (
  SELECT COUNT(1)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @db
    AND TABLE_NAME = 'shebao_payment_plan_summary'
    AND COLUMN_NAME = 'village_name'
);

SET @sql := IF(
  @col_exists = 0,
  'ALTER TABLE `shebao_payment_plan_summary` ADD COLUMN `village_name` varchar(100) DEFAULT NULL COMMENT ''村委会'' AFTER `subsidy_type`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 重建：先删后插（按计划明细聚合）
DELETE FROM `shebao_payment_plan_summary`;

INSERT INTO `shebao_payment_plan_summary` (
  `plan_id`,
  `business_period`,
  `subsidy_type`,
  `village_name`,
  `grant_org`,
  `total_count`,
  `total_amount`,
  `del_flag`,
  `create_by`,
  `create_time`,
  `update_by`,
  `update_time`,
  `remark`
)
SELECT
  d.plan_id,
  d.business_period,
  d.subsidy_type,
  IFNULL(NULLIF(TRIM(d.village_name), ''), '') AS village_name,
  NULL AS grant_org,
  COUNT(1) AS total_count,
  IFNULL(SUM(d.distribution_amount), 0) AS total_amount,
  '0' AS del_flag,
  'admin' AS create_by,
  NOW() AS create_time,
  'admin' AS update_by,
  NOW() AS update_time,
  'migrate village summary 20260805' AS remark
FROM `shebao_payment_plan_detail` d
WHERE d.del_flag = '0'
GROUP BY d.plan_id, d.business_period, d.subsidy_type, IFNULL(NULLIF(TRIM(d.village_name), ''), '');
