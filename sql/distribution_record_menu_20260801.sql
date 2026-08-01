-- 补贴发放记录：切到支付计划明细只读查询；下线旧 perms；发放明细表共用同一页面
-- 执行时机：与代码发版同步（先发代码再执行，或停机窗口同批执行）
-- 权限码：shebao:distribution:record:list / query / export

SET NAMES utf8mb4;

-- 1) 「补贴发放记录」菜单（原 2013）改为新页面与权限
UPDATE sys_menu
SET menu_name  = '补贴发放记录',
    path       = 'distributionRecord',
    component  = 'shebao/distribution/record/index',
    perms      = 'shebao:distribution:record:list',
    icon       = 'money',
    update_by  = 'admin',
    update_time = NOW(),
    remark     = '财务通过的支付计划明细查询'
WHERE menu_id = 2013;

-- 删除旧按钮权限，重建 list/query/export
DELETE FROM sys_role_menu WHERE menu_id IN (2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021);
DELETE FROM sys_menu WHERE menu_id IN (2014, 2015, 2016, 2017, 2018, 2019, 2020, 2021)
   OR (parent_id = 2013 AND menu_type = 'F');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES
('补贴发放记录查询', 2013, 1, '#', '', 1, 0, 'F', '0', '0', 'shebao:distribution:record:query', '#', 'admin', NOW(), ''),
('补贴发放记录导出', 2013, 2, '#', '', 1, 0, 'F', '0', '0', 'shebao:distribution:record:export', '#', 'admin', NOW(), '');

SET @dist_query_id = (SELECT menu_id FROM sys_menu WHERE parent_id = 2013 AND perms = 'shebao:distribution:record:query' LIMIT 1);
SET @dist_export_id = (SELECT menu_id FROM sys_menu WHERE parent_id = 2013 AND perms = 'shebao:distribution:record:export' LIMIT 1);

-- 原绑定了 2013 的角色：补上 list(父) 已有 + query/export
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT rm.role_id, @dist_query_id
FROM sys_role_menu rm
WHERE rm.menu_id = 2013
  AND @dist_query_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = @dist_query_id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT rm.role_id, @dist_export_id
FROM sys_role_menu rm
WHERE rm.menu_id = 2013
  AND @dist_export_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = @dist_export_id
  );

-- 2) 「发放明细表」(2107) 指向同一页面；按钮权限改为新码
UPDATE sys_menu
SET component   = 'shebao/distribution/record/index',
    perms       = 'shebao:distribution:record:list',
    update_by   = 'admin',
    update_time = NOW(),
    remark      = '与补贴发放记录共用页面'
WHERE menu_id = 2107;

-- 原发放明细按钮 3241/3242
UPDATE sys_menu SET perms = 'shebao:distribution:record:list', update_time = NOW()
WHERE menu_id = 3241 OR (parent_id = 2107 AND perms = 'shebao:audit:detail:list');

UPDATE sys_menu SET perms = 'shebao:distribution:record:export', update_time = NOW()
WHERE menu_id = 3242 OR (parent_id = 2107 AND perms = 'shebao:audit:detail:export');

-- 拥有发放明细父菜单的角色，补绑定 2013 的 query/export（若尚无）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @dist_query_id
FROM sys_role_menu rm
WHERE rm.menu_id = 2107
  AND @dist_query_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = @dist_query_id
  );

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @dist_export_id
FROM sys_role_menu rm
WHERE rm.menu_id = 2107
  AND @dist_export_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = @dist_export_id
  );

-- 校验
SELECT menu_id, menu_name, parent_id, path, component, perms, visible, status
FROM sys_menu
WHERE menu_id IN (2013, 2107)
   OR parent_id IN (2013, 2107)
ORDER BY parent_id, order_num, menu_id;
