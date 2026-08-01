SET NAMES utf8mb4;

-- =====================================================================
-- 支付计划明细：人员ID + 发放日期（财务通过写入）
-- 含存量回填；执行前请备份
-- =====================================================================

SET @db := DATABASE();

-- ---------------------------------------------------------------------
-- 1) 结构：subsidy_person_id
-- ---------------------------------------------------------------------
SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND COLUMN_NAME='subsidy_person_id');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_payment_plan_detail ADD COLUMN subsidy_person_id bigint DEFAULT NULL COMMENT ''被补贴人ID(快照关联)'' AFTER determination_item_id',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
             WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND INDEX_NAME='idx_ppd_subsidy_person');
SET @sql := IF(@idx=0,
  'ALTER TABLE shebao_payment_plan_detail ADD INDEX idx_ppd_subsidy_person (subsidy_person_id)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ---------------------------------------------------------------------
-- 2) 结构：distribution_date（财务通过时间，仅日期）
-- ---------------------------------------------------------------------
SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA=@db AND TABLE_NAME='shebao_payment_plan_detail' AND COLUMN_NAME='distribution_date');
SET @sql := IF(@c=0,
  'ALTER TABLE shebao_payment_plan_detail ADD COLUMN distribution_date date DEFAULT NULL COMMENT ''发放日期(财务通过日)'' AFTER distribution_amount',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ---------------------------------------------------------------------
-- 3) 存量回填 subsidy_person_id：优先核定主表，其次身份证
-- ---------------------------------------------------------------------
UPDATE shebao_payment_plan_detail d
INNER JOIN benefit_determination bd
        ON bd.id = d.determination_id
       AND IFNULL(bd.del_flag, '0') = '0'
SET d.subsidy_person_id = bd.subsidy_person_id
WHERE d.del_flag = '0'
  AND d.subsidy_person_id IS NULL
  AND bd.subsidy_person_id IS NOT NULL;

UPDATE shebao_payment_plan_detail d
INNER JOIN shebao_subsidy_person sp
        ON sp.id_card_no = d.id_card_no
       AND IFNULL(sp.del_flag, '0') = '0'
SET d.subsidy_person_id = sp.id
WHERE d.del_flag = '0'
  AND d.subsidy_person_id IS NULL
  AND d.id_card_no IS NOT NULL
  AND d.id_card_no <> '';

-- ---------------------------------------------------------------------
-- 4) 存量回填 distribution_date：已财务通过计划，取财务通过审核流水时间
-- ---------------------------------------------------------------------
UPDATE shebao_payment_plan_detail d
INNER JOIN shebao_payment_plan p
        ON p.id = d.plan_id
       AND p.del_flag = '0'
INNER JOIN (
    SELECT plan_id, MAX(operation_time) AS finance_pass_time
    FROM shebao_payment_plan_audit
    WHERE operation_status = 'finance_approved'
      AND IFNULL(del_flag, '0') = '0'
    GROUP BY plan_id
) a ON a.plan_id = d.plan_id
SET d.distribution_date = DATE(a.finance_pass_time)
WHERE d.del_flag = '0'
  AND d.distribution_date IS NULL
  AND p.finance_status = 'finance_approved'
  AND a.finance_pass_time IS NOT NULL;

-- 无审核流水时：用计划更新时间兜底（仅已财务通过且仍空）
UPDATE shebao_payment_plan_detail d
INNER JOIN shebao_payment_plan p
        ON p.id = d.plan_id
       AND p.del_flag = '0'
SET d.distribution_date = DATE(IFNULL(p.update_time, p.create_time))
WHERE d.del_flag = '0'
  AND d.distribution_date IS NULL
  AND p.finance_status = 'finance_approved'
  AND IFNULL(p.update_time, p.create_time) IS NOT NULL;

-- ---------------------------------------------------------------------
-- 5) 兼容回填：当月金额仍空时用发放金额（与补发脚本一致，幂等）
-- ---------------------------------------------------------------------
UPDATE shebao_payment_plan_detail
SET monthly_amount = IFNULL(monthly_amount, distribution_amount),
    supplement_amount = IFNULL(supplement_amount, 0)
WHERE del_flag = '0'
  AND (monthly_amount IS NULL OR supplement_amount IS NULL);
