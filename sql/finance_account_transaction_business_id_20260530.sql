SET NAMES utf8mb4;

SET @db := DATABASE();
SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'finance_account_transaction' AND COLUMN_NAME = 'business_id');
SET @sql := IF(@c = 0,
    'ALTER TABLE finance_account_transaction ADD COLUMN business_id bigint DEFAULT NULL COMMENT ''关联业务ID(补贴发放/待遇追回等)'' AFTER batch_no',
    'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;
