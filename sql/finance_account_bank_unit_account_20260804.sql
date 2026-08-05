SET NAMES utf8mb4;

-- =====================================================================
-- 财务账户：本行单位账号（银行代发导出按补贴类型取值）
-- 失地/拆迁/村干部 → 31307060000120111000648
-- 被征地           → 31307060000120111001434
-- 教师暂不处理
-- =====================================================================

SET @db := DATABASE();
SET @col_exists := (
  SELECT COUNT(1)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @db
    AND TABLE_NAME = 'finance_account'
    AND COLUMN_NAME = 'bank_unit_account'
);

SET @sql := IF(
  @col_exists = 0,
  'ALTER TABLE `finance_account` ADD COLUMN `bank_unit_account` varchar(64) DEFAULT NULL COMMENT ''本行单位账号(银行代发导出)'' AFTER `bank_name`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `finance_account`
SET `bank_unit_account` = '31307060000120111000648',
    `update_by` = 'admin',
    `update_time` = NOW()
WHERE `del_flag` = '0'
  AND `subsidy_type` IN ('land_loss', 'demolition', 'village_official');

UPDATE `finance_account`
SET `bank_unit_account` = '31307060000120111001434',
    `update_by` = 'admin',
    `update_time` = NOW()
WHERE `del_flag` = '0'
  AND `subsidy_type` = 'expropriatee';
