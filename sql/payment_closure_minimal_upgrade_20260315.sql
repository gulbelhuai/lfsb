-- 支付闭环最小可测试数据库升级脚本
-- 适用目标:
-- 1. 当前数据库结构落后于最新支付闭环代码
-- 2. 需要先完成最小必要字段补齐, 以便启动后端并开始联调/测试
-- 3. 兼容“旧库部分字段已存在、部分字段缺失”的场景
--
-- 执行建议:
-- 1. 先备份当前库
-- 2. 确认当前库名为 lfpm
-- 3. 使用 MySQL 8.0 执行

USE `lfpm`;

-- =========================================================
-- 0. 补齐 benefit_determination
-- =========================================================

ALTER TABLE `benefit_determination`
    ADD COLUMN IF NOT EXISTS `approval_batch_no` varchar(50) DEFAULT NULL COMMENT '审批批次号' AFTER `approval_status`,
    ADD COLUMN IF NOT EXISTS `notice_batch_no` varchar(50) DEFAULT NULL COMMENT '预到龄通知批次号' AFTER `approval_batch_no`,
    ADD COLUMN IF NOT EXISTS `notice_detail_id` bigint DEFAULT NULL COMMENT '预到龄通知明细ID' AFTER `notice_batch_no`,
    ADD COLUMN IF NOT EXISTS `id_card_no` varchar(18) DEFAULT NULL COMMENT '身份证号快照' AFTER `notice_detail_id`,
    ADD COLUMN IF NOT EXISTS `submit_by` varchar(64) DEFAULT NULL COMMENT '提交人' AFTER `id_card_no`,
    ADD COLUMN IF NOT EXISTS `submit_time` datetime DEFAULT NULL COMMENT '提交时间' AFTER `submit_by`,
    ADD COLUMN IF NOT EXISTS `review_by` varchar(64) DEFAULT NULL COMMENT '复核人' AFTER `submit_time`,
    ADD COLUMN IF NOT EXISTS `review_time` datetime DEFAULT NULL COMMENT '复核时间' AFTER `review_by`,
    ADD COLUMN IF NOT EXISTS `review_remark` varchar(500) DEFAULT NULL COMMENT '复核意见' AFTER `review_time`,
    ADD COLUMN IF NOT EXISTS `material_zip_path` varchar(255) DEFAULT NULL COMMENT '材料ZIP路径' AFTER `review_remark`,
    ADD COLUMN IF NOT EXISTS `material_extract_dir` varchar(255) DEFAULT NULL COMMENT '材料解压目录' AFTER `material_zip_path`,
    ADD COLUMN IF NOT EXISTS `material_image_paths` varchar(2000) DEFAULT NULL COMMENT '材料图片路径集合' AFTER `material_extract_dir`,
    ADD COLUMN IF NOT EXISTS `material_status` varchar(20) DEFAULT 'pending_upload' COMMENT '材料状态(pending_upload/uploaded/verified)' AFTER `material_image_paths`,
    ADD COLUMN IF NOT EXISTS `payment_plan_generated` char(1) DEFAULT '0' COMMENT '是否已进入支付计划(0否1是)' AFTER `material_status`;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = 'benefit_determination'
              AND index_name = 'idx_batch'
        ),
        'SELECT ''idx_batch exists''',
        'CREATE INDEX idx_batch ON benefit_determination (approval_batch_no)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = 'benefit_determination'
              AND index_name = 'idx_notice_batch_no'
        ),
        'SELECT ''idx_notice_batch_no exists''',
        'CREATE INDEX idx_notice_batch_no ON benefit_determination (notice_batch_no)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = 'benefit_determination'
              AND index_name = 'idx_notice_detail_id'
        ),
        'SELECT ''idx_notice_detail_id exists''',
        'CREATE INDEX idx_notice_detail_id ON benefit_determination (notice_detail_id)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = 'benefit_determination'
              AND index_name = 'idx_payment_plan_generated'
        ),
        'SELECT ''idx_payment_plan_generated exists''',
        'CREATE INDEX idx_payment_plan_generated ON benefit_determination (payment_plan_generated)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 身份证快照补齐
UPDATE `benefit_determination` bd
LEFT JOIN `shebao_subsidy_person` sp ON sp.id = bd.subsidy_person_id
SET bd.id_card_no = COALESCE(bd.id_card_no, sp.id_card_no)
WHERE bd.id_card_no IS NULL OR bd.id_card_no = '';

-- 已生成支付计划的数据回填标记
UPDATE `benefit_determination` bd
SET bd.payment_plan_generated = '1'
WHERE EXISTS (
    SELECT 1
    FROM `shebao_subsidy_distribution` sd
    WHERE sd.subsidy_record_id = bd.id
      AND sd.del_flag = '0'
);

-- 如果当前库里的 subsidy_type 仍是旧字符串, 统一转成代码值
UPDATE `benefit_determination`
SET `subsidy_type` = CASE `subsidy_type`
    WHEN 'land_loss_resident' THEN '1'
    WHEN 'expropriatee' THEN '2'
    WHEN 'demolition_resident' THEN '3'
    WHEN 'village_official' THEN '4'
    WHEN 'teacher' THEN '5'
    ELSE `subsidy_type`
END
WHERE `subsidy_type` IN ('land_loss_resident', 'expropriatee', 'demolition_resident', 'village_official', 'teacher');

-- =========================================================
-- 1. 补齐 shebao_subsidy_distribution
-- =========================================================

ALTER TABLE `shebao_subsidy_distribution`
    ADD COLUMN IF NOT EXISTS `batch_no` varchar(50) DEFAULT NULL COMMENT '批次号' AFTER `id`,
    ADD COLUMN IF NOT EXISTS `batch_type` varchar(20) DEFAULT 'first' COMMENT '批次类型(first/second/third)' AFTER `batch_no`,
    ADD COLUMN IF NOT EXISTS `approval_status` varchar(20) DEFAULT 'draft' COMMENT '审批状态(draft/pending_review/pending_approve/pending_finance/submitted_bank/completed/partial_failed/failed/manual_closed/rejected)' AFTER `batch_type`,
    ADD COLUMN IF NOT EXISTS `rejection_reason` varchar(500) DEFAULT NULL COMMENT '驳回原因' AFTER `approval_status`,
    ADD COLUMN IF NOT EXISTS `review_time` datetime DEFAULT NULL COMMENT '复核时间' AFTER `rejection_reason`,
    ADD COLUMN IF NOT EXISTS `review_user_id` bigint DEFAULT NULL COMMENT '复核人ID' AFTER `review_time`,
    ADD COLUMN IF NOT EXISTS `review_user_name` varchar(64) DEFAULT NULL COMMENT '复核人姓名' AFTER `review_user_id`,
    ADD COLUMN IF NOT EXISTS `approve_time` datetime DEFAULT NULL COMMENT '审批时间' AFTER `review_user_name`,
    ADD COLUMN IF NOT EXISTS `approve_user_id` bigint DEFAULT NULL COMMENT '审批人ID' AFTER `approve_time`,
    ADD COLUMN IF NOT EXISTS `approve_user_name` varchar(64) DEFAULT NULL COMMENT '审批人姓名' AFTER `approve_user_id`;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = 'shebao_subsidy_distribution'
              AND index_name = 'idx_batch_no'
        ),
        'SELECT ''idx_batch_no exists''',
        'CREATE INDEX idx_batch_no ON shebao_subsidy_distribution (batch_no)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = 'shebao_subsidy_distribution'
              AND index_name = 'idx_approval_status'
        ),
        'SELECT ''idx_approval_status exists''',
        'CREATE INDEX idx_approval_status ON shebao_subsidy_distribution (approval_status)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = 'shebao_subsidy_distribution'
              AND index_name = 'idx_batch_type'
        ),
        'SELECT ''idx_batch_type exists''',
        'CREATE INDEX idx_batch_type ON shebao_subsidy_distribution (batch_type)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 老数据兜底
UPDATE `shebao_subsidy_distribution`
SET `batch_type` = COALESCE(NULLIF(`batch_type`, ''), 'first')
WHERE `batch_type` IS NULL OR `batch_type` = '';

UPDATE `shebao_subsidy_distribution`
SET `approval_status` = CASE
    WHEN `approval_status` IS NOT NULL AND `approval_status` <> '' THEN `approval_status`
    WHEN `distribution_status` = '1' THEN 'pending_review'
    WHEN `distribution_status` = '2' THEN 'pending_finance'
    WHEN `distribution_status` = '3' THEN 'rejected'
    WHEN `distribution_status` = '4' THEN 'completed'
    ELSE 'draft'
END
WHERE `approval_status` IS NULL OR `approval_status` = '';

UPDATE `shebao_subsidy_distribution`
SET `subsidy_type` = CASE `subsidy_type`
    WHEN 'land_loss_resident' THEN '1'
    WHEN 'expropriatee' THEN '2'
    WHEN 'demolition_resident' THEN '3'
    WHEN 'village_official' THEN '4'
    WHEN 'teacher' THEN '5'
    ELSE `subsidy_type`
END
WHERE `subsidy_type` IN ('land_loss_resident', 'expropriatee', 'demolition_resident', 'village_official', 'teacher');

-- =========================================================
-- 2. 补齐/创建 distribution_batch
-- =========================================================

CREATE TABLE IF NOT EXISTS `distribution_batch` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `batch_no` varchar(50) NOT NULL COMMENT '批次号',
    `batch_type` varchar(20) NOT NULL DEFAULT 'first' COMMENT '批次类型(first/second/third)',
    `subsidy_type` varchar(50) DEFAULT NULL COMMENT '补贴类型',
    `distribution_year` int DEFAULT NULL COMMENT '发放年份',
    `distribution_month` int DEFAULT NULL COMMENT '发放月份',
    `total_count` int DEFAULT 0 COMMENT '总人数',
    `total_amount` decimal(12,2) DEFAULT 0.00 COMMENT '总金额',
    `success_count` int DEFAULT 0 COMMENT '成功人数',
    `success_amount` decimal(12,2) DEFAULT 0.00 COMMENT '成功金额',
    `fail_count` int DEFAULT 0 COMMENT '失败人数',
    `fail_amount` decimal(12,2) DEFAULT 0.00 COMMENT '失败金额',
    `approval_status` varchar(20) DEFAULT 'draft' COMMENT '审批状态(draft/pending_review/pending_approve/pending_finance/submitted_bank/completed/partial_failed)',
    `finance_upload_time` datetime DEFAULT NULL COMMENT '财务上传时间',
    `bank_submit_time` datetime DEFAULT NULL COMMENT '银行提交时间',
    `bank_result_time` datetime DEFAULT NULL COMMENT '银行回盘时间',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0正常 1删除)',
    `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_distribution_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发放批次表';

ALTER TABLE `distribution_batch`
    ADD COLUMN IF NOT EXISTS `distribution_year` int DEFAULT NULL COMMENT '发放年份' AFTER `subsidy_type`,
    ADD COLUMN IF NOT EXISTS `distribution_month` int DEFAULT NULL COMMENT '发放月份' AFTER `distribution_year`,
    ADD COLUMN IF NOT EXISTS `success_count` int DEFAULT 0 COMMENT '成功人数' AFTER `total_amount`,
    ADD COLUMN IF NOT EXISTS `success_amount` decimal(12,2) DEFAULT 0.00 COMMENT '成功金额' AFTER `success_count`,
    ADD COLUMN IF NOT EXISTS `fail_count` int DEFAULT 0 COMMENT '失败人数' AFTER `success_amount`,
    ADD COLUMN IF NOT EXISTS `fail_amount` decimal(12,2) DEFAULT 0.00 COMMENT '失败金额' AFTER `fail_count`,
    ADD COLUMN IF NOT EXISTS `finance_upload_time` datetime DEFAULT NULL COMMENT '财务上传时间' AFTER `approval_status`,
    ADD COLUMN IF NOT EXISTS `bank_submit_time` datetime DEFAULT NULL COMMENT '银行提交时间' AFTER `finance_upload_time`,
    ADD COLUMN IF NOT EXISTS `bank_result_time` datetime DEFAULT NULL COMMENT '银行回盘时间' AFTER `bank_submit_time`,
    ADD COLUMN IF NOT EXISTS `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0正常 1删除)' AFTER `bank_result_time`;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = 'distribution_batch'
              AND index_name = 'idx_distribution_batch_no'
        ),
        'SELECT ''idx_distribution_batch_no exists''',
        'CREATE INDEX idx_distribution_batch_no ON distribution_batch (batch_no)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = 'distribution_batch'
              AND index_name = 'idx_distribution_batch_status'
        ),
        'SELECT ''idx_distribution_batch_status exists''',
        'CREATE INDEX idx_distribution_batch_status ON distribution_batch (approval_status)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.statistics
            WHERE table_schema = DATABASE()
              AND table_name = 'distribution_batch'
              AND index_name = 'idx_distribution_batch_type'
        ),
        'SELECT ''idx_distribution_batch_type exists''',
        'CREATE INDEX idx_distribution_batch_type ON distribution_batch (batch_type)'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 兼容旧表把 status 挪到 approval_status 的场景
SET @has_status_col = (
    SELECT COUNT(1)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'distribution_batch'
      AND column_name = 'status'
);

SET @sql = IF(
    @has_status_col > 0,
    'UPDATE distribution_batch SET approval_status = COALESCE(NULLIF(approval_status, ''''), status, ''draft'')',
    'SELECT ''distribution_batch.status not exists'''
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `distribution_batch`
SET `batch_type` = COALESCE(NULLIF(`batch_type`, ''), 'first'),
    `approval_status` = COALESCE(NULLIF(`approval_status`, ''), 'draft'),
    `del_flag` = COALESCE(NULLIF(`del_flag`, ''), '0');

UPDATE `distribution_batch`
SET `subsidy_type` = CASE `subsidy_type`
    WHEN 'land_loss_resident' THEN '1'
    WHEN 'expropriatee' THEN '2'
    WHEN 'demolition_resident' THEN '3'
    WHEN 'village_official' THEN '4'
    WHEN 'teacher' THEN '5'
    ELSE `subsidy_type`
END
WHERE `subsidy_type` IN ('land_loss_resident', 'expropriatee', 'demolition_resident', 'village_official', 'teacher');

-- =========================================================
-- 3. 执行完成后的核对 SQL
-- =========================================================

-- 查看三张关键表字段
-- SELECT COLUMN_NAME, COLUMN_TYPE
-- FROM information_schema.columns
-- WHERE table_schema = DATABASE()
--   AND table_name IN ('benefit_determination', 'shebao_subsidy_distribution', 'distribution_batch')
-- ORDER BY table_name, ordinal_position;

-- 查看补贴类型是否已统一为代码
-- SELECT subsidy_type, COUNT(*) FROM benefit_determination GROUP BY subsidy_type;
-- SELECT subsidy_type, COUNT(*) FROM shebao_subsidy_distribution GROUP BY subsidy_type;
-- SELECT subsidy_type, COUNT(*) FROM distribution_batch GROUP BY subsidy_type;

-- 查看支付计划生成标记回填情况
-- SELECT payment_plan_generated, COUNT(*) FROM benefit_determination GROUP BY payment_plan_generated;
