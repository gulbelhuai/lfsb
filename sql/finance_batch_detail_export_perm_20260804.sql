SET NAMES utf8mb4;

-- 财务批次管理：明细导出权限（挂在「批次管理」菜单下）
-- 权限码：shebao:finance:batch:export

SET @batch_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'shebao:finance:batch:list' LIMIT 1);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '明细导出', @batch_menu_id, 7, '#', '', 1, 0, 'F', '0', '0', 'shebao:finance:batch:export', '#', 'admin', NOW(), '批次明细全量导出'
WHERE @batch_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'shebao:finance:batch:export');

SET @export_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'shebao:finance:batch:export' LIMIT 1);

-- 拥有批次管理父菜单的角色同步授权导出
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @export_menu_id
FROM sys_role_menu rm
WHERE rm.menu_id = @batch_menu_id
  AND @export_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = @export_menu_id
  );

SELECT menu_id, menu_name, parent_id, perms, order_num
FROM sys_menu
WHERE perms IN ('shebao:finance:batch:list', 'shebao:finance:batch:export')
   OR parent_id = @batch_menu_id
ORDER BY parent_id, order_num, menu_id;
