SET NAMES utf8mb4;

-- =====================================================================
-- 账户明细：补贴发放拆成「发放 + 失败退回」；修复批次 20260701001 坏数据
-- 执行时机：与代码发版同步（先发代码或停机窗口同批执行均可；本脚本幂等可重复执行）
-- 口径：
--   发放金额 = 批次全部明细 distribution_amount 之和（负数）
--   退回金额 = 批次失败明细之和（正数；为 0 则不写退回）
--   账户名称 = finance_account.account_name
--   备注 = {补贴类型简称}{业务期yyyy-MM}发放 / …发放失败退回
-- 说明：仅改流水展示与拆分，账户当前余额不变（净额与原「只扣成功」一致）
-- =====================================================================

DROP TEMPORARY TABLE IF EXISTS tmp_subsidy_dist_migrate;
CREATE TEMPORARY TABLE tmp_subsidy_dist_migrate AS
SELECT
    t.id AS old_txn_id,
    t.account_id,
    a.account_name AS formal_account_name,
    t.batch_no,
    t.business_id AS plan_id,
    t.transaction_time,
    t.create_by,
    t.create_time,
    t.update_by,
    t.update_time,
    DATE_FORMAT(p.business_period, '%Y-%m') AS period_ym,
    CASE p.subsidy_type
        WHEN 'land_loss' THEN '失地补贴'
        WHEN 'land_loss_resident' THEN '失地补贴'
        WHEN 'expropriatee' THEN '被征地补贴'
        WHEN 'expropriatee_subsidy' THEN '被征地补贴'
        WHEN 'demolition' THEN '拆迁补贴'
        WHEN 'demolition_resident' THEN '拆迁补贴'
        WHEN 'village_official' THEN '村干部补贴'
        WHEN 'teacher' THEN '教师补贴'
        WHEN 'teacher_subsidy' THEN '教师补贴'
        ELSE IFNULL(p.subsidy_type, '')
    END AS type_label,
    IFNULL((
        SELECT SUM(d.distribution_amount)
        FROM shebao_payment_plan_detail d
        WHERE d.plan_id = t.business_id AND d.del_flag = '0'
    ), 0) AS total_amt,
    IFNULL((
        SELECT SUM(d.distribution_amount)
        FROM shebao_payment_plan_detail d
        WHERE d.plan_id = t.business_id AND d.del_flag = '0' AND d.distribution_result = 'failed'
    ), 0) AS failed_amt,
    /* 发放前余额 = 原流水余额 - 原流水金额（原金额为负，即 balance - (-success) = balance + success） */
    (t.balance - t.amount) AS balance_before
FROM finance_account_transaction t
JOIN shebao_payment_plan p ON p.id = t.business_id AND p.del_flag = '0'
JOIN finance_account a ON a.id = t.account_id
WHERE t.del_flag = '0'
  AND t.transaction_type = 'subsidy_distribution'
  AND t.amount < 0
  AND t.business_id IS NOT NULL
  /* 尚未迁移：同批次同计划尚无「发放失败退回」正数流水，且备注仍是旧文案或账户名为「账户」或金额仍按成功扣 */
  AND NOT EXISTS (
      SELECT 1
      FROM finance_account_transaction x
      WHERE x.del_flag = '0'
        AND x.transaction_type = 'subsidy_distribution'
        AND x.business_id = t.business_id
        AND x.batch_no <=> t.batch_no
        AND x.amount > 0
        AND x.remark LIKE '%发放失败退回'
  )
  AND (
      t.remark = '银行发放完成扣款'
      OR t.account_name = '账户'
      OR t.remark NOT LIKE '%发放'
  );

-- 1) 更新原「发放」流水：应发合计负数 + 正式账户名 + 新备注 + 发放后余额
UPDATE finance_account_transaction t
JOIN tmp_subsidy_dist_migrate m ON m.old_txn_id = t.id
SET t.account_name = m.formal_account_name,
    t.amount = -m.total_amt,
    t.balance = m.balance_before - m.total_amt,
    t.remark = CONCAT(m.type_label, m.period_ym, '发放'),
    t.update_by = 'admin',
    t.update_time = NOW()
WHERE m.total_amt > 0;

-- 2) 失败金额 > 0 时补「退回」流水（正数）；同一秒，id 自增排在发放之后
INSERT INTO finance_account_transaction (
    account_id, account_name, batch_no, business_id, transaction_type,
    amount, balance, transaction_time, remark, del_flag,
    create_by, create_time, update_by, update_time
)
SELECT
    m.account_id,
    m.formal_account_name,
    m.batch_no,
    m.plan_id,
    'subsidy_distribution',
    m.failed_amt,
    (m.balance_before - m.total_amt + m.failed_amt),
    m.transaction_time,
    CONCAT(m.type_label, m.period_ym, '发放失败退回'),
    '0',
    IFNULL(NULLIF(m.create_by, ''), 'admin'),
    IFNULL(m.create_time, NOW()),
    'admin',
    NOW()
FROM tmp_subsidy_dist_migrate m
WHERE m.failed_amt > 0
  AND m.total_amt > 0
  AND NOT EXISTS (
      SELECT 1
      FROM finance_account_transaction x
      WHERE x.del_flag = '0'
        AND x.transaction_type = 'subsidy_distribution'
        AND x.business_id = m.plan_id
        AND x.batch_no <=> m.batch_no
        AND x.amount > 0
        AND x.remark LIKE '%发放失败退回'
  );

-- 校验：批次 20260701001 / plan_id=14
SELECT id, account_id, account_name, batch_no, business_id, transaction_type,
       amount, balance, transaction_time, remark
FROM finance_account_transaction
WHERE del_flag = '0'
  AND batch_no = '20260701001'
ORDER BY id;

DROP TEMPORARY TABLE IF EXISTS tmp_subsidy_dist_migrate;
