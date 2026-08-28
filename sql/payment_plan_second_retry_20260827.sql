SET NAMES utf8mb4;

-- =====================================================================
-- 二次发放：支付计划明细增加源明细关联与重发计数/状态
-- 执行前请备份；本脚本仅结构变更，不删业务数据
-- Agent 不代执行，由人工在目标库执行
-- =====================================================================

SET @db := DATABASE();

-- ---------------------------------------------------------------------
-- 1) source_detail_id：二次计划新明细指向源失败明细；正常计划为 null
-- ---------------------------------------------------------------------
SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND COLUMN_NAME='source_detail_id');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_payment_plan_detail ADD COLUMN source_detail_id bigint DEFAULT NULL COMMENT ''二次发放源失败明细ID'' AFTER fail_reason',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ---------------------------------------------------------------------
-- 2) retry_count：源行被纳入二次计划的次数
-- ---------------------------------------------------------------------
SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND COLUMN_NAME='retry_count');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_payment_plan_detail ADD COLUMN retry_count int NOT NULL DEFAULT 0 COMMENT ''被纳入二次计划次数'' AFTER source_detail_id',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ---------------------------------------------------------------------
-- 3) retry_status：源行重发状态；retry_success=重发成功
-- ---------------------------------------------------------------------
SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND COLUMN_NAME='retry_status');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_payment_plan_detail ADD COLUMN retry_status varchar(32) DEFAULT NULL COMMENT ''retry_success=重发成功'' AFTER retry_count',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ---------------------------------------------------------------------
-- 4) 索引：按源明细反查
-- ---------------------------------------------------------------------
SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND INDEX_NAME='idx_ppd_source_detail');
SET @sql := IF(@idx=0,
  'ALTER TABLE shebao_payment_plan_detail ADD INDEX idx_ppd_source_detail (source_detail_id)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;
