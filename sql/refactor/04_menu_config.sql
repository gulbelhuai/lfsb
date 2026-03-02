-- 菜单配置SQL - 6个角色菜单
-- 执行前请确保sys_menu表已存在

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET autocommit = 1;

-- ==================== 清理旧数据（如果存在）====================

-- 先删除子菜单
DELETE FROM sys_menu WHERE parent_id IN (
  SELECT menu_id FROM (
    SELECT menu_id FROM sys_menu WHERE menu_name IN ('人员信息管理', '待遇核定管理', '待遇管理', '支付结算', '财务管理', '统计管理') AND parent_id = 0
  ) AS temp_menu
);

-- 再删除一级菜单
DELETE FROM sys_menu WHERE menu_name IN ('人员信息管理', '待遇核定管理', '待遇管理', '支付结算', '财务管理', '统计管理') AND parent_id = 0;

-- ==================== 一级菜单 ====================

-- 1. 人员信息管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('人员信息管理', 0, 1, 'person', NULL, 1, 0, 'M', '0', '0', NULL, 'peoples', 'admin', NOW(), '', NULL, '人员信息管理菜单');

-- 2. 待遇核定管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('待遇核定管理', 0, 2, 'benefit', NULL, 1, 0, 'M', '0', '0', NULL, 'documentation', 'admin', NOW(), '', NULL, '待遇核定管理菜单');

-- 3. 待遇管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('待遇管理', 0, 3, 'management', NULL, 1, 0, 'M', '0', '0', NULL, 'edit', 'admin', NOW(), '', NULL, '待遇管理菜单');

-- 4. 支付结算
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('支付结算', 0, 4, 'payment', NULL, 1, 0, 'M', '0', '0', NULL, 'money', 'admin', NOW(), '', NULL, '支付结算菜单');

-- 5. 财务管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('财务管理', 0, 5, 'finance', NULL, 1, 0, 'M', '0', '0', NULL, 'guide', 'admin', NOW(), '', NULL, '财务管理菜单');

-- 6. 统计管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('统计管理', 0, 6, 'audit', NULL, 1, 0, 'M', '0', '0', NULL, 'chart', 'admin', NOW(), '', NULL, '统计管理菜单');

-- ==================== 人员信息管理 子菜单 ====================

-- 获取一级菜单ID
SET @person_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '人员信息管理' AND parent_id = 0 LIMIT 1);

-- 失地居民登记
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('失地居民登记', @person_menu_id, 1, 'landloss', 'shebao/person/landloss/index', 1, 0, 'C', '0', '0', 'shebao:person:landloss:list', '#', 'admin', NOW(), '', NULL, '失地居民登记菜单');

-- 被征地居民登记
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('被征地居民登记', @person_menu_id, 2, 'expropriatee', 'shebao/person/expropriatee/index', 1, 0, 'C', '0', '0', 'shebao:person:expropriatee:list', '#', 'admin', NOW(), '', NULL, '被征地居民登记菜单');

-- 拆迁居民登记
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('拆迁居民登记', @person_menu_id, 3, 'demolition', 'shebao/person/demolition/index', 1, 0, 'C', '0', '0', 'shebao:person:demolition:list', '#', 'admin', NOW(), '', NULL, '拆迁居民登记菜单');

-- 村干部登记
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('村干部登记', @person_menu_id, 4, 'official', 'shebao/person/official/index', 1, 0, 'C', '0', '0', 'shebao:person:official:list', '#', 'admin', NOW(), '', NULL, '村干部登记菜单');

-- 教龄补助登记
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('教龄补助登记', @person_menu_id, 5, 'teacher', 'shebao/person/teacher/index', 1, 0, 'C', '0', '0', 'shebao:person:teacher:list', '#', 'admin', NOW(), '', NULL, '教龄补助登记菜单');

-- 人员登记复核
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('人员登记复核', @person_menu_id, 6, 'review', 'shebao/person/review/index', 1, 0, 'C', '0', '0', 'shebao:person:review:list', '#', 'admin', NOW(), '', NULL, '人员登记复核菜单');

-- 信息修改审批
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('信息修改审批', @person_menu_id, 7, 'modify', 'shebao/person/modify/index', 1, 0, 'C', '0', '0', 'shebao:person:modify:list', '#', 'admin', NOW(), '', NULL, '信息修改审批菜单');

-- 人员注销登记（独立列表与增删改）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('人员注销登记', @person_menu_id, 8, 'cancel', 'shebao/person/cancel/index', 1, 0, 'C', '0', '0', 'shebao:person:cancel:list', '#', 'admin', NOW(), '', NULL, '人员注销登记菜单');

-- 人员注销登记按钮权限
SET @person_cancel_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '人员注销登记' AND parent_id = @person_menu_id LIMIT 1);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('人员注销登记查询', @person_cancel_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'shebao:person:cancel:query', '#', 'admin', NOW(), '', NULL, ''),
       ('人员注销登记新增', @person_cancel_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'shebao:person:cancel:add', '#', 'admin', NOW(), '', NULL, ''),
       ('人员注销登记修改', @person_cancel_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'shebao:person:cancel:edit', '#', 'admin', NOW(), '', NULL, ''),
       ('人员注销登记删除', @person_cancel_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'shebao:person:cancel:remove', '#', 'admin', NOW(), '', NULL, ''),
       ('人员注销登记导出', @person_cancel_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'shebao:person:cancel:export', '#', 'admin', NOW(), '', NULL, '');

-- ==================== 待遇核定管理 子菜单 ====================

SET @benefit_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '待遇核定管理' AND parent_id = 0 LIMIT 1);

-- 预到龄发放通知
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('预到龄发放通知', @benefit_menu_id, 1, 'notice', 'shebao/benefit/notice/index', 1, 0, 'C', '0', '0', 'shebao:benefit:notice:list', '#', 'admin', NOW(), '', NULL, '预到龄发放通知菜单');

-- 待遇核定
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('待遇核定', @benefit_menu_id, 2, 'determination', 'shebao/benefit/determination/index', 1, 0, 'C', '0', '0', 'shebao:benefit:determination:list', '#', 'admin', NOW(), '', NULL, '待遇核定菜单');

-- 核定审核
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('核定审核', @benefit_menu_id, 3, 'review', 'shebao/benefit/review/index', 1, 0, 'C', '0', '0', 'shebao:benefit:review:list', '#', 'admin', NOW(), '', NULL, '核定审核菜单');

-- ==================== 待遇管理 子菜单 ====================

SET @management_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '待遇管理' AND parent_id = 0 LIMIT 1);

-- 发放信息修改
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('发放信息修改', @management_menu_id, 1, 'modify', 'shebao/management/modify/index', 1, 0, 'C', '0', '0', 'shebao:benefit:modify:list', '#', 'admin', NOW(), '', NULL, '发放信息修改菜单');

-- 待遇暂停恢复
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('待遇暂停恢复', @management_menu_id, 2, 'suspension', 'shebao/management/suspension/index', 1, 0, 'C', '0', '0', 'shebao:benefit:suspension:list', '#', 'admin', NOW(), '', NULL, '待遇暂停恢复菜单');

-- 待遇认证
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('待遇认证', @management_menu_id, 3, 'certification', 'shebao/management/certification/index', 1, 0, 'C', '0', '0', 'shebao:benefit:certification:list', '#', 'admin', NOW(), '', NULL, '待遇认证菜单');

-- ==================== 支付结算 子菜单 ====================

SET @payment_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '支付结算' AND parent_id = 0 LIMIT 1);

-- 支付计划生成
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('支付计划生成', @payment_menu_id, 1, 'plan', 'shebao/payment/plan/index', 1, 0, 'C', '0', '0', 'shebao:payment:plan:list', '#', 'admin', NOW(), '', NULL, '支付计划生成菜单');

-- 支付计划复核
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('支付计划复核', @payment_menu_id, 2, 'review', 'shebao/payment/review/index', 1, 0, 'C', '0', '0', 'shebao:payment:batch:review', '#', 'admin', NOW(), '', NULL, '支付计划复核菜单');

-- 支付计划审批
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('支付计划审批', @payment_menu_id, 3, 'approve', 'shebao/payment/approve/index', 1, 0, 'C', '0', '0', 'shebao:payment:batch:approve', '#', 'admin', NOW(), '', NULL, '支付计划审批菜单');

-- 上传财务系统
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('上传财务系统', @payment_menu_id, 4, 'upload', 'shebao/payment/upload/index', 1, 0, 'C', '0', '0', 'shebao:payment:batch:upload', '#', 'admin', NOW(), '', NULL, '上传财务系统菜单');

-- ==================== 财务管理 子菜单 ====================

SET @finance_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '财务管理' AND parent_id = 0 LIMIT 1);

-- 批次管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('批次管理', @finance_menu_id, 1, 'batch', 'shebao/finance/batch/index', 1, 0, 'C', '0', '0', 'shebao:finance:batch:list', '#', 'admin', NOW(), '', NULL, '批次管理菜单');

-- 银行发放
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('银行发放', @finance_menu_id, 2, 'bank', 'shebao/finance/bank/index', 1, 0, 'C', '0', '0', 'shebao:finance:bank:list', '#', 'admin', NOW(), '', NULL, '银行发放菜单');

-- 失败处理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('失败处理', @finance_menu_id, 3, 'failure', 'shebao/finance/failure/index', 1, 0, 'C', '0', '0', 'shebao:finance:failure:list', '#', 'admin', NOW(), '', NULL, '失败处理菜单');

-- 财务账户
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('财务账户', @finance_menu_id, 4, 'account', 'shebao/finance/account/index', 1, 0, 'C', '0', '0', 'shebao:finance:account:list', '#', 'admin', NOW(), '', NULL, '财务账户菜单');

-- ==================== 审计报表 子菜单 ====================

SET @audit_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '统计管理' AND parent_id = 0 LIMIT 1);

-- 操作日志
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('操作日志', @audit_menu_id, 1, 'operlog', 'shebao/audit/operlog/index', 1, 0, 'C', '0', '0', 'shebao:audit:operlog:list', '#', 'admin', NOW(), '', NULL, '操作日志菜单');

-- 审批记录
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('审批记录', @audit_menu_id, 2, 'approval', 'shebao/audit/approval/index', 1, 0, 'C', '0', '0', 'shebao:audit:approval:list', '#', 'admin', NOW(), '', NULL, '审批记录菜单');

-- 发放明细表
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('发放明细表', @audit_menu_id, 3, 'detail', 'shebao/audit/detail/index', 1, 0, 'C', '0', '0', 'shebao:audit:detail:list', '#', 'admin', NOW(), '', NULL, '发放明细表菜单');

-- 统计汇总
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('统计汇总', @audit_menu_id, 4, 'statistics', 'shebao/audit/statistics/index', 1, 0, 'C', '0', '0', 'shebao:audit:statistics:list', '#', 'admin', NOW(), '', NULL, '统计汇总菜单');

-- ==================== 角色菜单关联 ====================

-- 获取角色ID
SET @role_admin = (SELECT role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1);
SET @role_operator = (SELECT role_id FROM sys_role WHERE role_key = 'operator' LIMIT 1);
SET @role_reviewer = (SELECT role_id FROM sys_role WHERE role_key = 'reviewer' LIMIT 1);
SET @role_approver = (SELECT role_id FROM sys_role WHERE role_key = 'approver' LIMIT 1);
SET @role_finance = (SELECT role_id FROM sys_role WHERE role_key = 'finance' LIMIT 1);
SET @role_auditor = (SELECT role_id FROM sys_role WHERE role_key = 'auditor' LIMIT 1);

-- 经办人：人员信息管理、待遇核定管理、待遇管理、支付结算（计划生成）
-- 复核人：人员登记复核、核定审核、支付计划复核
-- 审批人：信息修改审批、支付计划审批、上传财务系统
-- 财务人员：财务管理
-- 审计员：审计报表
-- 系统管理员：全部

-- 注：由于sys_role_menu表的具体关联需要获取每个菜单的ID，
-- 这部分建议在系统运行后通过管理界面进行配置，
-- 或者编写专门的初始化脚本
