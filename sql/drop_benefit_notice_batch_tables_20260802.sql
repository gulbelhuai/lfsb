-- ========================================
-- ISS-013：预到龄仅即时查询导出，删除批次落库表
-- 日期：2026-08-02
-- 【由用户执行】Agent 禁止连库执行本脚本
-- 执行前可用只读核对：表空、列存在即可
-- ========================================

SET NAMES utf8mb4;

-- 1) 删除未使用的通知批次表
DROP TABLE IF EXISTS shebao_benefit_notice_detail;
DROP TABLE IF EXISTS shebao_benefit_notice_batch;

-- 2) 核定/附件上遗留的批次关联列（实体未映射，无业务使用）
SET @sql := (
  SELECT IF(
    COUNT(*) > 0,
    'ALTER TABLE benefit_determination DROP COLUMN notice_detail_id, DROP COLUMN notice_batch_no',
    'SELECT ''skip: benefit_determination notice columns'' AS info'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'benefit_determination'
    AND COLUMN_NAME = 'notice_batch_no'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    COUNT(*) > 0,
    'ALTER TABLE shebao_benefit_attachment DROP COLUMN notice_batch_no',
    'SELECT ''skip: shebao_benefit_attachment.notice_batch_no'' AS info'
  )
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'shebao_benefit_attachment'
    AND COLUMN_NAME = 'notice_batch_no'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
