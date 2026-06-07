-- 历史数据导入菜单（挂到社保管理下，请按实际 parent_id 调整）
-- 若已有「数据管理」目录，可将 parent_id 改为该目录 ID

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '历史数据导入', m.menu_id, 99, 'historicalImport', 'shebao/historicalImport/index', 1, 0, 'C', '0', '0', 'shebao:historicalImport:list', 'upload', 'admin', NOW(), '历史数据导入列表'
FROM sys_menu m WHERE m.menu_name = '社保管理' AND m.parent_id = 0 LIMIT 1;

SET @menu_id = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
('历史数据导入查询', @menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'shebao:historicalImport:list', '#', 'admin', NOW(), ''),
('历史数据导入操作', @menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'shebao:historicalImport:import', '#', 'admin', NOW(), '');
