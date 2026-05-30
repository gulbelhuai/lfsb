SET NAMES utf8mb4;

-- =====================================================================
-- 银行发放功能：数据来源为批次管理中财务状态=已通过(finance_approved)的支付计划
-- 1) 支付计划增加发放状态 distribution_status
-- 2) 支付计划明细增加发放结果 distribution_result 与失败原因 fail_reason
-- 3) 银行发放相关菜单权限
-- =====================================================================

-- 1. 支付计划：发放状态(pending=待发放, submitted=已提交银行, completed=已完成)
ALTER TABLE `shebao_payment_plan`
  ADD COLUMN `distribution_status` varchar(20) DEFAULT NULL COMMENT '发放状态(pending待发放/submitted已提交银行/completed已完成)' AFTER `finance_status`;

-- 2. 支付计划明细：发放结果(success成功, failed失败) 与失败原因
ALTER TABLE `shebao_payment_plan_detail`
  ADD COLUMN `distribution_result` varchar(20) DEFAULT NULL COMMENT '发放结果(success成功/failed失败)' AFTER `relation_to_insured`;

ALTER TABLE `shebao_payment_plan_detail`
  ADD COLUMN `fail_reason` varchar(255) DEFAULT NULL COMMENT '发放失败原因' AFTER `distribution_result`;

-- 3. 银行发放菜单权限（挂在“银行发放”菜单下，请将 @parentId 替换为实际菜单ID；
--    若已用界面维护可忽略本段）
-- SET @parentId = (SELECT menu_id FROM sys_menu WHERE perms = 'shebao:finance:bank:list' LIMIT 1);
-- INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
-- ('银行导出', @parentId, 1, '', '', 1, 0, 'F', '0', '0', 'shebao:finance:bank:export', '#', 'admin', sysdate(), '银行发放-导出代发文件'),
-- ('提交银行', @parentId, 2, '', '', 1, 0, 'F', '0', '0', 'shebao:finance:bank:submit', '#', 'admin', sysdate(), '银行发放-提交银行'),
-- ('导入失败数据', @parentId, 3, '', '', 1, 0, 'F', '0', '0', 'shebao:finance:bank:importFail', '#', 'admin', sysdate(), '银行发放-导入失败数据'),
-- ('已完成', @parentId, 4, '', '', 1, 0, 'F', '0', '0', 'shebao:finance:bank:complete', '#', 'admin', sysdate(), '银行发放-标记已完成');
