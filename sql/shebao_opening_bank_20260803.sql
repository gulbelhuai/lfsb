SET NAMES utf8mb4;

-- =====================================================================
-- 开户行业务表（发放机构引用 code；替代字典 shebao_grant_org）
-- =====================================================================

CREATE TABLE IF NOT EXISTS `shebao_opening_bank` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(64) NOT NULL COMMENT '开户行编码(业务关联键，如 china_bank)',
  `short_name` varchar(100) NOT NULL COMMENT '简称(页面发放机构展示)',
  `full_name` varchar(200) NOT NULL COMMENT '全称(银行代发导出对方地址等)',
  `bank_no` varchar(32) NOT NULL COMMENT '行号(银行代发导出对方开户行行号)',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0存在 2删除)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_opening_bank_code` (`code`),
  KEY `idx_opening_bank_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='开户行(发放机构)';

INSERT INTO `shebao_opening_bank` (
  `code`, `short_name`, `full_name`, `bank_no`, `status`, `del_flag`, `create_by`, `create_time`, `remark`
) VALUES
  ('china_bank', '中国银行', '中国银行廊坊市开发区支行', '104146000190', '0', '0', 'admin', NOW(), '历史字典迁移'),
  ('langfang_bank', '廊坊银行', '廊坊银行开发区支行', '313146000078', '0', '0', 'admin', NOW(), '历史字典迁移')
ON DUPLICATE KEY UPDATE
  `short_name` = VALUES(`short_name`),
  `full_name` = VALUES(`full_name`),
  `bank_no` = VALUES(`bank_no`),
  `status` = '0',
  `del_flag` = '0',
  `update_by` = 'admin',
  `update_time` = NOW();

-- 停用原发放机构字典（保留数据备查，业务不再使用）
UPDATE sys_dict_type
SET status = '1',
    remark = CONCAT(IFNULL(remark, ''), ' [已迁移至 shebao_opening_bank]'),
    update_by = 'admin',
    update_time = NOW()
WHERE dict_type = 'shebao_grant_org';

UPDATE sys_dict_data
SET status = '1',
    update_by = 'admin',
    update_time = NOW()
WHERE dict_type = 'shebao_grant_org';

-- 若曾执行方案B扩展表，可手工删除（本脚本不强制 DROP）
-- DROP TABLE IF EXISTS dict_shebao_grant_org_profile;

-- =====================================================================
-- 菜单：基础数据 → 开户行
-- =====================================================================
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '开户行', m.menu_id, 5, 'openingBank', 'shebao/openingBank/index', 1, 0, 'C', '0', '0', 'shebao:openingBank:list', 'money', 'admin', NOW(), '开户行(发放机构)维护'
FROM sys_menu m
WHERE m.menu_name = '基础数据' AND m.parent_id = 0
  AND NOT EXISTS (
      SELECT 1 FROM sys_menu x WHERE x.parent_id = m.menu_id AND x.path = 'openingBank'
  )
LIMIT 1;

SET @opening_bank_menu_id = (
  SELECT menu_id FROM sys_menu
  WHERE path = 'openingBank' AND component = 'shebao/openingBank/index'
  ORDER BY menu_id DESC LIMIT 1
);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '开户行查询', @opening_bank_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'shebao:openingBank:query', '#', 'admin', NOW(), ''
FROM DUAL WHERE @opening_bank_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @opening_bank_menu_id AND perms = 'shebao:openingBank:query');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '开户行新增', @opening_bank_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'shebao:openingBank:add', '#', 'admin', NOW(), ''
FROM DUAL WHERE @opening_bank_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @opening_bank_menu_id AND perms = 'shebao:openingBank:add');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '开户行修改', @opening_bank_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'shebao:openingBank:edit', '#', 'admin', NOW(), ''
FROM DUAL WHERE @opening_bank_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @opening_bank_menu_id AND perms = 'shebao:openingBank:edit');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '开户行删除', @opening_bank_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'shebao:openingBank:remove', '#', 'admin', NOW(), ''
FROM DUAL WHERE @opening_bank_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @opening_bank_menu_id AND perms = 'shebao:openingBank:remove');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '开户行导出', @opening_bank_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'shebao:openingBank:export', '#', 'admin', NOW(), ''
FROM DUAL WHERE @opening_bank_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @opening_bank_menu_id AND perms = 'shebao:openingBank:export');

-- 拥有「街道办」菜单的角色同步授权开户行
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, m.menu_id
FROM sys_role_menu rm
JOIN sys_menu street ON street.menu_id = rm.menu_id AND street.path = 'streetOffice'
JOIN sys_menu m ON (m.menu_id = @opening_bank_menu_id OR m.parent_id = @opening_bank_menu_id)
WHERE @opening_bank_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = m.menu_id
  );
