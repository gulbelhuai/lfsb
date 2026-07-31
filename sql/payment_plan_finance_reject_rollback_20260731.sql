-- 历史数据修复：财务已驳回但审批状态仍为 approved 的支付计划，回退为可再提交
SET NAMES utf8mb4;

UPDATE shebao_payment_plan
SET approval_status = 'finance_rejected',
    update_time = NOW()
WHERE del_flag = '0'
  AND finance_status = 'finance_rejected'
  AND approval_status = 'approved';
