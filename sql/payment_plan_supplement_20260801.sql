SET NAMES utf8mb4;

-- =====================================================================
-- 支付计划纳入补发：补发发放状态 + 支付明细拆分金额/起止/来源
-- 执行前请备份；本脚本仅结构变更与存量默认值，不删业务数据
-- =====================================================================

SET @db := DATABASE();

-- ---------------------------------------------------------------------
-- 1) 核定明细：补发发放状态与关联支付计划
-- supplement_pay_status: 0未补发 1已纳入支付 2已发放
-- ---------------------------------------------------------------------
SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='benefit_determination_item' AND COLUMN_NAME='supplement_pay_status');
SET @sql := IF(@c=0,
  'ALTER TABLE benefit_determination_item ADD COLUMN supplement_pay_status char(1) NOT NULL DEFAULT ''0'' COMMENT ''补发发放状态(0未补发 1已纳入支付 2已发放)'' AFTER benefit_amount',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='benefit_determination_item' AND COLUMN_NAME='supplement_plan_id');
SET @sql := IF(@c=0,
  'ALTER TABLE benefit_determination_item ADD COLUMN supplement_plan_id bigint DEFAULT NULL COMMENT ''纳入的支付计划ID'' AFTER supplement_pay_status',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='benefit_determination_item' AND COLUMN_NAME='supplement_detail_id');
SET @sql := IF(@c=0,
  'ALTER TABLE benefit_determination_item ADD COLUMN supplement_detail_id bigint DEFAULT NULL COMMENT ''纳入的支付计划明细ID'' AFTER supplement_plan_id',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

UPDATE benefit_determination_item
SET supplement_pay_status = '0'
WHERE supplement_pay_status IS NULL;

-- ---------------------------------------------------------------------
-- 2) 恢复明细：补发发放状态与关联支付计划
-- ---------------------------------------------------------------------
SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_benefit_resume_item' AND COLUMN_NAME='supplement_pay_status');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_benefit_resume_item ADD COLUMN supplement_pay_status char(1) NOT NULL DEFAULT ''0'' COMMENT ''补发发放状态(0未补发 1已纳入支付 2已发放)'' AFTER supplement_amount',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_benefit_resume_item' AND COLUMN_NAME='supplement_plan_id');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_benefit_resume_item ADD COLUMN supplement_plan_id bigint DEFAULT NULL COMMENT ''纳入的支付计划ID'' AFTER supplement_pay_status',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_benefit_resume_item' AND COLUMN_NAME='supplement_detail_id');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_benefit_resume_item ADD COLUMN supplement_detail_id bigint DEFAULT NULL COMMENT ''纳入的支付计划明细ID'' AFTER supplement_plan_id',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

UPDATE shebao_benefit_resume_item
SET supplement_pay_status = '0'
WHERE supplement_pay_status IS NULL;

-- ---------------------------------------------------------------------
-- 3) 支付计划明细：当月/补发拆分与来源快照
-- ---------------------------------------------------------------------
SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND COLUMN_NAME='monthly_amount');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_payment_plan_detail ADD COLUMN monthly_amount decimal(12,2) DEFAULT NULL COMMENT ''当月金额'' AFTER payment_month',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND COLUMN_NAME='supplement_amount');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_payment_plan_detail ADD COLUMN supplement_amount decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT ''补发金额'' AFTER monthly_amount',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND COLUMN_NAME='supplement_start_month');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_payment_plan_detail ADD COLUMN supplement_start_month date DEFAULT NULL COMMENT ''补发所属期起始'' AFTER supplement_amount',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND COLUMN_NAME='supplement_end_month');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_payment_plan_detail ADD COLUMN supplement_end_month date DEFAULT NULL COMMENT ''补发所属期终止'' AFTER supplement_start_month',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND COLUMN_NAME='supplement_source');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_payment_plan_detail ADD COLUMN supplement_source varchar(20) DEFAULT NULL COMMENT ''补发来源(determination/resume)'' AFTER supplement_end_month',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND COLUMN_NAME='supplement_source_id');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_payment_plan_detail ADD COLUMN supplement_source_id bigint DEFAULT NULL COMMENT ''补发来源明细ID'' AFTER supplement_source',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 存量明细：当月金额回填为原发放金额，补发为0
UPDATE shebao_payment_plan_detail
SET monthly_amount = IFNULL(monthly_amount, distribution_amount),
    supplement_amount = IFNULL(supplement_amount, 0)
WHERE del_flag = '0';

-- 索引（便于按计划释放补发）
SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA=@db AND TABLE_NAME='benefit_determination_item' AND INDEX_NAME='idx_bdi_supplement_plan');
SET @sql := IF(@idx=0,
  'ALTER TABLE benefit_determination_item ADD INDEX idx_bdi_supplement_plan (supplement_plan_id)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_benefit_resume_item' AND INDEX_NAME='idx_bri_supplement_plan');
SET @sql := IF(@idx=0,
  'ALTER TABLE shebao_benefit_resume_item ADD INDEX idx_bri_supplement_plan (supplement_plan_id)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;
