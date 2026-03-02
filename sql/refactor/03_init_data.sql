-- ========================================
-- 阶段一 任务3: 创建数据字典和初始化数据
-- 创建时间: 2026-01-19
-- 说明: 添加数据字典、角色、用户、菜单等初始化数据
-- ========================================

-- ========================================
-- 1. 数据字典 - 补贴类型（包含新增的教师补贴）
-- ========================================
DELETE FROM sys_dict_type WHERE dict_type = 'shebao_subsidy_type';
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) VALUES
(200, '补贴类型', 'shebao_subsidy_type', '0', 'admin', NOW(), '各类养老补贴类型');

DELETE FROM sys_dict_data WHERE dict_type = 'shebao_subsidy_type';
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark) VALUES
(1, '失地居民', 'land_loss', 'shebao_subsidy_type', '0', 'admin', NOW(), '失地居民养老补贴'),
(2, '被征地居民', 'expropriatee', 'shebao_subsidy_type', '0', 'admin', NOW(), '被征地居民养老补贴'),
(3, '拆迁居民', 'demolition', 'shebao_subsidy_type', '0', 'admin', NOW(), '拆迁居民养老补贴'),
(4, '村干部', 'village_official', 'shebao_subsidy_type', '0', 'admin', NOW(), '村干部养老补贴'),
(5, '教师补贴', 'teacher', 'shebao_subsidy_type', '0', 'admin', NOW(), '教师养老补贴');

-- ========================================
-- 2. 数据字典 - 审批状态
-- ========================================
DELETE FROM sys_dict_type WHERE dict_type = 'approval_status';
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) VALUES
(201, '审批状态', 'approval_status', '0', 'admin', NOW(), '审批流程状态');

DELETE FROM sys_dict_data WHERE dict_type = 'approval_status';
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark) VALUES
(1, '草稿', 'draft', 'approval_status', '0', 'admin', NOW(), '草稿状态'),
(2, '待复核', 'pending_review', 'approval_status', '0', 'admin', NOW(), '待复核状态'),
(3, '待审批', 'pending_approve', 'approval_status', '0', 'admin', NOW(), '待审批状态'),
(4, '待财务', 'pending_finance', 'approval_status', '0', 'admin', NOW(), '待财务处理状态'),
(5, '已提交银行', 'submitted_bank', 'approval_status', '0', 'admin', NOW(), '已提交银行状态'),
(6, '已发放', 'distributed', 'approval_status', '0', 'admin', NOW(), '已发放状态'),
(7, '已驳回', 'rejected', 'approval_status', '0', 'admin', NOW(), '已驳回状态');

-- ========================================
-- 3. 数据字典 - 批次类型
-- ========================================
DELETE FROM sys_dict_type WHERE dict_type = 'batch_type';
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) VALUES
(202, '批次类型', 'batch_type', '0', 'admin', NOW(), '发放批次类型');

DELETE FROM sys_dict_data WHERE dict_type = 'batch_type';
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark) VALUES
(1, '首次发放', 'first', 'batch_type', '0', 'admin', NOW(), '首次发放批次'),
(2, '二次发放', 'second', 'batch_type', '0', 'admin', NOW(), '二次发放批次'),
(3, '三次发放', 'third', 'batch_type', '0', 'admin', NOW(), '三次发放批次');

-- ========================================
-- 4. 数据字典 - 暂停原因
-- ========================================
DELETE FROM sys_dict_type WHERE dict_type = 'suspension_reason';
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) VALUES
(203, '暂停原因', 'suspension_reason', '0', 'admin', NOW(), '待遇暂停原因');

DELETE FROM sys_dict_data WHERE dict_type = 'suspension_reason';
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark) VALUES
(1, '死亡', 'death', 'suspension_reason', '0', 'admin', NOW(), '待遇人死亡'),
(2, '未认证', 'uncertified', 'suspension_reason', '0', 'admin', NOW(), '未完成年度认证'),
(3, '服刑', 'imprisonment', 'suspension_reason', '0', 'admin', NOW(), '正在服刑'),
(4, '失踪', 'missing', 'suspension_reason', '0', 'admin', NOW(), '人员失踪'),
(5, '其他', 'other', 'suspension_reason', '0', 'admin', NOW(), '其他原因');

-- ========================================
-- 5. 数据字典 - 认证状态
-- ========================================
DELETE FROM sys_dict_type WHERE dict_type = 'certification_status';
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) VALUES
(204, '认证状态', 'certification_status', '0', 'admin', NOW(), '待遇认证状态');

DELETE FROM sys_dict_data WHERE dict_type = 'certification_status';
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark) VALUES
(1, '未认证', 'uncertified', 'certification_status', '0', 'admin', NOW(), '未完成年度认证'),
(2, '已认证(4月)', 'april', 'certification_status', '0', 'admin', NOW(), '已完成4月认证'),
(3, '已认证(10月)', 'october', 'certification_status', '0', 'admin', NOW(), '已完成10月认证');

-- ========================================
-- 6. 数据字典 - 业务类型
-- ========================================
DELETE FROM sys_dict_type WHERE dict_type = 'business_type';
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) VALUES
(205, '业务类型', 'business_type', '0', 'admin', NOW(), '审批流程业务类型');

DELETE FROM sys_dict_data WHERE dict_type = 'business_type';
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark) VALUES
(1, '人员登记', 'person_register', 'business_type', '0', 'admin', NOW(), '人员登记审批'),
(2, '待遇核定', 'benefit_determination', 'business_type', '0', 'admin', NOW(), '待遇核定审批'),
(3, '支付计划', 'payment_plan', 'business_type', '0', 'admin', NOW(), '支付计划审批'),
(4, '信息修改', 'info_modify', 'business_type', '0', 'admin', NOW(), '信息修改审批');

-- ========================================
-- 7. 数据字典 - 审批操作类型
-- ========================================
DELETE FROM sys_dict_type WHERE dict_type = 'approval_operation';
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) VALUES
(206, '审批操作', 'approval_operation', '0', 'admin', NOW(), '审批流程操作类型');

DELETE FROM sys_dict_data WHERE dict_type = 'approval_operation';
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark) VALUES
(1, '提交', 'submit', 'approval_operation', '0', 'admin', NOW(), '提交审核'),
(2, '复核', 'review', 'approval_operation', '0', 'admin', NOW(), '复核操作'),
(3, '审批', 'approve', 'approval_operation', '0', 'admin', NOW(), '审批操作'),
(4, '驳回', 'reject', 'approval_operation', '0', 'admin', NOW(), '驳回操作'),
(5, '撤销', 'cancel', 'approval_operation', '0', 'admin', NOW(), '撤销操作');

-- ========================================
-- 8. 数据字典 - 失败原因
-- ========================================
DELETE FROM sys_dict_type WHERE dict_type = 'fail_reason';
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) VALUES
(207, '失败原因', 'fail_reason', '0', 'admin', NOW(), '银行发放失败原因');

DELETE FROM sys_dict_data WHERE dict_type = 'fail_reason';
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark) VALUES
(1, '账号不存在', 'account_not_exist', 'fail_reason', '0', 'admin', NOW(), '银行账户不存在'),
(2, '余额不足', 'insufficient_balance', 'fail_reason', '0', 'admin', NOW(), '账户余额不足'),
(3, '信息错误', 'info_error', 'fail_reason', '0', 'admin', NOW(), '账户信息错误'),
(4, '银行系统错误', 'bank_error', 'fail_reason', '0', 'admin', NOW(), '银行系统错误'),
(5, '其他', 'other', 'fail_reason', '0', 'admin', NOW(), '其他原因');

-- ========================================
-- 9. 创建角色
-- ========================================
-- 删除已存在的角色（如果存在）
DELETE FROM sys_role WHERE role_key IN ('operator', 'reviewer', 'approver', 'finance', 'auditor');

-- 插入新角色
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, status, del_flag, create_by, create_time, remark) VALUES
(101, '经办人', 'operator', 2, '0', '0', 'admin', NOW(), '业务经办人，负责数据录入和提交审核'),
(102, '复核人', 'reviewer', 3, '0', '0', 'admin', NOW(), '业务复核人，负责复核经办人提交的数据'),
(103, '审批人', 'approver', 4, '0', '0', 'admin', NOW(), '业务负责人（所长），负责最终审批'),
(104, '财务人员', 'finance', 5, '0', '0', 'admin', NOW(), '财务人员，负责发放和账户管理'),
(105, '审计员', 'auditor', 6, '0', '0', 'admin', NOW(), '审计员，负责日志查询和报表审计');

-- ========================================
-- 10. 创建测试用户
-- ========================================
-- 删除已存在的测试用户（如果存在）
DELETE FROM sys_user WHERE user_name IN ('operator01', 'reviewer01', 'approver01', 'finance01', 'auditor01');

-- 插入测试用户（密码都是admin123，加密后的值）
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, remark) VALUES
(101, 100, 'operator01', '经办人01', '00', 'operator01@shebao.com', '13800001001', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sTQJ7RZ2Qm2', '0', '0', '127.0.0.1', NOW(), 'admin', NOW(), '经办人测试账号'),
(102, 100, 'reviewer01', '复核人01', '00', 'reviewer01@shebao.com', '13800001002', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sTQJ7RZ2Qm2', '0', '0', '127.0.0.1', NOW(), 'admin', NOW(), '复核人测试账号'),
(103, 100, 'approver01', '审批人01', '00', 'approver01@shebao.com', '13800001003', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sTQJ7RZ2Qm2', '0', '0', '127.0.0.1', NOW(), 'admin', NOW(), '审批人测试账号'),
(104, 100, 'finance01', '财务01', '00', 'finance01@shebao.com', '13800001004', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sTQJ7RZ2Qm2', '0', '0', '127.0.0.1', NOW(), 'admin', NOW(), '财务人员测试账号'),
(105, 100, 'auditor01', '审计员01', '00', 'auditor01@shebao.com', '13800001005', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sTQJ7RZ2Qm2', '0', '0', '127.0.0.1', NOW(), 'admin', NOW(), '审计员测试账号');

-- ========================================
-- 11. 用户角色关联
-- ========================================
-- 清除已存在的关联
DELETE FROM sys_user_role WHERE user_id IN (101, 102, 103, 104, 105);

-- 插入用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(101, 101), -- operator01 -> operator
(102, 102), -- reviewer01 -> reviewer
(103, 103), -- approver01 -> approver
(104, 104), -- finance01 -> finance
(105, 105); -- auditor01 -> auditor

-- ========================================
-- 12. 初始化审批角色配置
-- ========================================
-- 清空现有配置
DELETE FROM approval_role_config;

-- 人员登记审批配置（2级：经办人→复核人）
INSERT INTO approval_role_config (business_type, approval_level, role_code, role_name, required_flag, order_num, create_by, remark) VALUES
('person_register', 1, 'operator', '经办人', '1', 1, 'admin', '人员登记：经办人提交'),
('person_register', 2, 'reviewer', '复核人', '1', 2, 'admin', '人员登记：复核人复核');

-- 待遇核定审批配置（2级：经办人→复核人）
INSERT INTO approval_role_config (business_type, approval_level, role_code, role_name, required_flag, order_num, create_by, remark) VALUES
('benefit_determination', 1, 'operator', '经办人', '1', 1, 'admin', '待遇核定：经办人提交'),
('benefit_determination', 2, 'reviewer', '复核人', '1', 2, 'admin', '待遇核定：复核人复核');

-- 支付计划审批配置（3级：经办人→复核人→审批人→财务）
INSERT INTO approval_role_config (business_type, approval_level, role_code, role_name, required_flag, order_num, create_by, remark) VALUES
('payment_plan', 1, 'operator', '经办人', '1', 1, 'admin', '支付计划：经办人提交'),
('payment_plan', 2, 'reviewer', '复核人', '1', 2, 'admin', '支付计划：复核人复核'),
('payment_plan', 3, 'approver', '审批人', '1', 3, 'admin', '支付计划：审批人审批'),
('payment_plan', 4, 'finance', '财务人员', '1', 4, 'admin', '支付计划：财务人员处理');

-- 关键信息修改审批配置（3级：经办人→复核人→审批人）
INSERT INTO approval_role_config (business_type, approval_level, role_code, role_name, required_flag, order_num, create_by, remark) VALUES
('info_modify', 1, 'operator', '经办人', '1', 1, 'admin', '关键信息修改：经办人提交'),
('info_modify', 2, 'reviewer', '复核人', '1', 2, 'admin', '关键信息修改：复核人复核'),
('info_modify', 3, 'approver', '审批人', '1', 3, 'admin', '关键信息修改：审批人审批');

-- ========================================
-- 13. 初始化财务账户
-- ========================================
-- 清空现有财务账户
DELETE FROM finance_account;

-- 插入5个补贴类型对应的财务账户
INSERT INTO finance_account (account_no, account_name, bank_name, subsidy_type, balance, status, create_by, remark) VALUES
('LF2025001', '失地居民养老补贴账户', '中国工商银行廊坊开发区支行', 'land_loss', 1000000.00, '1', 'admin', '失地居民养老补贴专户'),
('LF2025002', '被征地居民养老补贴账户', '中国建设银行廊坊开发区支行', 'expropriatee', 800000.00, '1', 'admin', '被征地居民养老补贴专户'),
('LF2025003', '拆迁居民养老补贴账户', '中国农业银行廊坊开发区支行', 'demolition', 500000.00, '1', 'admin', '拆迁居民养老补贴专户'),
('LF2025004', '村干部养老补贴账户', '中国银行廊坊开发区支行', 'village_official', 300000.00, '1', 'admin', '村干部养老补贴专户'),
('LF2025005', '教师养老补贴账户', '中国邮政储蓄银行廊坊开发区支行', 'teacher', 200000.00, '1', 'admin', '教师养老补贴专户');

-- ========================================
-- 初始化数据完成
-- ========================================
