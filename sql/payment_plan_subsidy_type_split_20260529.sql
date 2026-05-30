SET NAMES utf8mb4;

-- =====================================================================
-- 支付计划改造：按 业务期 + 补贴类型 + 核定方式 生成一个批次
-- 1) 主表增加 subsidy_type 列
-- 2) 将历史的“多补贴类型混合计划”按补贴类型拆分为多条计划
-- =====================================================================

-- 1. 增加补贴类型列（位于业务期之后）
ALTER TABLE `shebao_payment_plan`
  ADD COLUMN `subsidy_type` varchar(50) DEFAULT NULL COMMENT '补贴类型(land_loss/expropriatee/demolition/village_official/teacher)' AFTER `business_period`;

ALTER TABLE `shebao_payment_plan`
  ADD KEY `idx_plan_subsidy_type` (`subsidy_type`);

-- 2. 历史数据：按补贴类型拆分混合计划
DROP PROCEDURE IF EXISTS sp_split_payment_plan_by_subsidy;
DELIMITER $$
CREATE PROCEDURE sp_split_payment_plan_by_subsidy()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE v_plan_id BIGINT;
    DECLARE v_type VARCHAR(50);
    DECLARE v_prev_plan BIGINT DEFAULT NULL;
    DECLARE v_new_id BIGINT;
    DECLARE v_prefix CHAR(8);
    DECLARE v_seq INT;
    DECLARE v_new_batch VARCHAR(16);
    DECLARE v_period DATE;
    DECLARE v_dtype VARCHAR(20);

    -- 逐个 (计划, 补贴类型) 处理，明细表为准
    DECLARE cur CURSOR FOR
        SELECT pd.plan_id, pd.subsidy_type
        FROM shebao_payment_plan_detail pd
        JOIN shebao_payment_plan pp ON pp.id = pd.plan_id AND pp.del_flag = '0'
        WHERE pd.del_flag = '0'
        GROUP BY pd.plan_id, pd.subsidy_type
        ORDER BY pd.plan_id, pd.subsidy_type;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_plan_id, v_type;
        IF done THEN
            LEAVE read_loop;
        END IF;

        IF v_prev_plan IS NULL OR v_prev_plan <> v_plan_id THEN
            -- 该计划的第一个补贴类型：直接落在原计划上
            SET v_prev_plan = v_plan_id;

            UPDATE shebao_payment_plan
            SET subsidy_type = v_type,
                total_count = (SELECT COUNT(*) FROM shebao_payment_plan_detail d
                               WHERE d.plan_id = v_plan_id AND d.subsidy_type = v_type AND d.del_flag = '0'),
                total_amount = (SELECT IFNULL(SUM(d.distribution_amount), 0) FROM shebao_payment_plan_detail d
                                WHERE d.plan_id = v_plan_id AND d.subsidy_type = v_type AND d.del_flag = '0')
            WHERE id = v_plan_id;
        ELSE
            -- 同一计划的其它补贴类型：克隆出新计划，并把明细/汇总迁移过去
            SELECT business_period, determination_type INTO v_period, v_dtype
            FROM shebao_payment_plan WHERE id = v_plan_id;

            SET v_prefix = CONCAT(DATE_FORMAT(v_period, '%Y%m'), IF(v_dtype = 'normal', '01', '02'));
            SELECT IFNULL(MAX(CAST(RIGHT(batch_no, 3) AS UNSIGNED)), 0) + 1 INTO v_seq
            FROM shebao_payment_plan
            WHERE batch_no IS NOT NULL AND LENGTH(batch_no) = 11 AND LEFT(batch_no, 8) = v_prefix;
            SET v_new_batch = CONCAT(v_prefix, LPAD(v_seq, 3, '0'));

            INSERT INTO shebao_payment_plan
                (determination_type, business_period, subsidy_type, batch_no, total_count, total_amount,
                 operator_name, operator_time, approval_status, finance_status, del_flag,
                 create_by, create_time, update_by, update_time, remark)
            SELECT determination_type, business_period, v_type, v_new_batch,
                   (SELECT COUNT(*) FROM shebao_payment_plan_detail d
                    WHERE d.plan_id = v_plan_id AND d.subsidy_type = v_type AND d.del_flag = '0'),
                   (SELECT IFNULL(SUM(d.distribution_amount), 0) FROM shebao_payment_plan_detail d
                    WHERE d.plan_id = v_plan_id AND d.subsidy_type = v_type AND d.del_flag = '0'),
                   operator_name, operator_time, approval_status, finance_status, del_flag,
                   create_by, create_time, update_by, update_time, remark
            FROM shebao_payment_plan WHERE id = v_plan_id;

            SET v_new_id = LAST_INSERT_ID();

            UPDATE shebao_payment_plan_detail
            SET plan_id = v_new_id
            WHERE plan_id = v_plan_id AND subsidy_type = v_type AND del_flag = '0';

            UPDATE shebao_payment_plan_summary
            SET plan_id = v_new_id
            WHERE plan_id = v_plan_id AND subsidy_type = v_type AND del_flag = '0';

            -- 审核流水按计划复制一份到新计划（审核记录不含补贴类型，复制保留可追溯）
            INSERT INTO shebao_payment_plan_audit
                (plan_id, operation_status, approval_stage, operator_name, operation_time, remark,
                 del_flag, create_by, create_time, update_by, update_time)
            SELECT v_new_id, operation_status, approval_stage, operator_name, operation_time, remark,
                   del_flag, create_by, create_time, update_by, update_time
            FROM shebao_payment_plan_audit WHERE plan_id = v_plan_id;
        END IF;
    END LOOP;
    CLOSE cur;

    -- 无明细的历史计划：尝试用汇总表的单一类型回填，否则置 unknown
    UPDATE shebao_payment_plan p
    SET subsidy_type = IFNULL(
        (SELECT s.subsidy_type FROM shebao_payment_plan_summary s
         WHERE s.plan_id = p.id AND s.del_flag = '0'
         GROUP BY s.subsidy_type ORDER BY s.subsidy_type LIMIT 1),
        'unknown')
    WHERE p.del_flag = '0' AND (p.subsidy_type IS NULL OR p.subsidy_type = '');
END $$
DELIMITER ;

CALL sp_split_payment_plan_by_subsidy();
DROP PROCEDURE IF EXISTS sp_split_payment_plan_by_subsidy;
