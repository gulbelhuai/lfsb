SET NAMES utf8mb4;

-- 财务批次管理：审批单下载权限（挂在「批次管理」菜单下）
-- 权限码：shebao:finance:batch:approvalForm

SET @batch_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'shebao:finance:batch:list' LIMIT 1);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '审批单下载', @batch_menu_id, 8, '#', '', 1, 0, 'F', '0', '0', 'shebao:finance:batch:approvalForm', '#', 'admin', NOW(), '财务审批通过后下载审批单'
WHERE @batch_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'shebao:finance:batch:approvalForm');

SET @approval_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'shebao:finance:batch:approvalForm' LIMIT 1);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @approval_menu_id
FROM sys_role_menu rm
WHERE rm.menu_id = @batch_menu_id
  AND @approval_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = @approval_menu_id
  );

SELECT menu_id, menu_name, parent_id, perms, order_num
FROM sys_menu
WHERE perms IN ('shebao:finance:batch:list', 'shebao:finance:batch:export', 'shebao:finance:batch:approvalForm')
   OR parent_id = @batch_menu_id
ORDER BY parent_id, order_num, menu_id;
