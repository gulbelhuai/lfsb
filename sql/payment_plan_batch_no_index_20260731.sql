SET NAMES utf8mb4;

-- 批次号唯一索引改为普通索引：撤销(逻辑删除)后可复用序号，唯一性由 nextBatchNo 代码控制
SET @db := DATABASE();

SET @uk := (SELECT COUNT(*) FROM information_schema.STATISTICS
            WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'shebao_payment_plan' AND INDEX_NAME = 'uk_payment_plan_batch_no');
SET @sql := IF(@uk > 0,
    'ALTER TABLE shebao_payment_plan DROP INDEX uk_payment_plan_batch_no',
    'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'shebao_payment_plan' AND INDEX_NAME = 'idx_payment_plan_batch_no');
SET @sql := IF(@idx = 0,
    'ALTER TABLE shebao_payment_plan ADD INDEX idx_payment_plan_batch_no (batch_no)',
    'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;
