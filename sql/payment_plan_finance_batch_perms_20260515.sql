SET NAMES utf8mb4;

-- 财务批次管理：各审批环节独立按钮权限（挂在「批次管理」菜单下）
SET @batch_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'shebao:finance:batch:list' LIMIT 1);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '财务通过', @batch_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'shebao:finance:batch:financePass', '#', 'admin', NOW(), ''
WHERE @batch_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'shebao:finance:batch:financePass');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '财务驳回', @batch_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'shebao:finance:batch:financeReject', '#', 'admin', NOW(), ''
WHERE @batch_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'shebao:finance:batch:financeReject');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '复核通过', @batch_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'shebao:finance:batch:reviewPass', '#', 'admin', NOW(), ''
WHERE @batch_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'shebao:finance:batch:reviewPass');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '复核驳回', @batch_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'shebao:finance:batch:reviewReject', '#', 'admin', NOW(), ''
WHERE @batch_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'shebao:finance:batch:reviewReject');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '审批通过', @batch_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'shebao:finance:batch:approvePass', '#', 'admin', NOW(), ''
WHERE @batch_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'shebao:finance:batch:approvePass');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '审批驳回', @batch_menu_id, 6, '#', '', 1, 0, 'F', '0', '0', 'shebao:finance:batch:approveReject', '#', 'admin', NOW(), ''
WHERE @batch_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'shebao:finance:batch:approveReject');
