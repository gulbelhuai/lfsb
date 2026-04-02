-- 人员注销登记：新增独立复核权限 shebao:person:cancel:review
-- 说明：向 sys_menu / sys_role_menu 补充“通过/不通过”按钮权限
-- 执行前建议确认 sys_menu/sys_role_menu/sys_role 表存在

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET autocommit = 1;

-- ==================== 新增按钮菜单（sys_menu）====================
SET @person_cancel_menu_id = (
  SELECT menu_id
  FROM sys_menu
  WHERE perms = 'shebao:person:cancel:list'
  LIMIT 1
);

-- 若权限已存在则跳过
INSERT INTO sys_menu
  (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT
  '人员注销登记复核',
  @person_cancel_menu_id,
  6,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'shebao:person:cancel:review',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE perms = 'shebao:person:cancel:review'
);

-- ==================== 角色菜单关联（sys_role_menu）====================
-- 复核人：role_id=102
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT
  102,
  m.menu_id
FROM sys_menu m
WHERE m.perms = 'shebao:person:cancel:review'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 102 AND rm.menu_id = m.menu_id
  );

-- 超级管理员：role_id=1（保底）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT
  1,
  m.menu_id
FROM sys_menu m
WHERE m.perms = 'shebao:person:cancel:review'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id
  );

