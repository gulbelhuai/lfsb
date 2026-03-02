-- ===========================
-- 补贴发放功能数据库修改脚本
-- 日期: 2025-10-13
-- ===========================

-- 1. 修改四种补贴身份表，增加发放状态和补贴金额字段

-- 1.1 失地居民信息表 shebao_land_loss_resident
ALTER TABLE shebao_land_loss_resident
ADD COLUMN subsidy_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '补贴金额' AFTER is_under_16,
ADD COLUMN distribution_status CHAR(1) DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）' AFTER subsidy_amount;

-- 1.2 被征地参保补贴账户表 shebao_expropriatee_subsidy
-- 注意：该表已有subsidy_amount字段，只需要增加distribution_status字段
ALTER TABLE shebao_expropriatee_subsidy
ADD COLUMN distribution_status CHAR(1) DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）' AFTER subsidy_amount;

-- 1.3 拆迁居民信息表 shebao_demolition_resident
ALTER TABLE shebao_demolition_resident
ADD COLUMN subsidy_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '补贴金额' AFTER recognition_time,
ADD COLUMN distribution_status CHAR(1) DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）' AFTER subsidy_amount;

-- 1.4 村干部信息表 shebao_village_official
ALTER TABLE shebao_village_official
ADD COLUMN subsidy_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '补贴金额' AFTER has_violation,
ADD COLUMN distribution_status CHAR(1) DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）' AFTER subsidy_amount;

-- 2. 创建补贴发放记录表
CREATE TABLE shebao_subsidy_distribution
(
    id                    BIGINT AUTO_INCREMENT COMMENT '主键ID' PRIMARY KEY,
    subsidy_person_id     BIGINT               NOT NULL COMMENT '被补贴人ID（关联shebao_subsidy_person.id）',
    subsidy_type          CHAR(1)              NOT NULL COMMENT '补贴类型（1失地居民补贴 2被征地居民补贴 3拆迁居民补贴 4村干部补贴）',
    subsidy_record_id     BIGINT               NOT NULL COMMENT '补贴身份记录ID（关联具体补贴类型表的ID）',
    distribution_amount   DECIMAL(10,2)        NOT NULL COMMENT '发放金额',
    distribution_date     DATE                          NULL COMMENT '发放日期',
    distribution_status   CHAR(1)        DEFAULT '1'   NOT NULL COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
    review_remark         VARCHAR(500)                  NULL COMMENT '审核说明',
    status                CHAR(1)        DEFAULT '0'   NULL COMMENT '状态（0正常 1停用）',
    del_flag              CHAR(1)        DEFAULT '0'   NULL COMMENT '删除标志（0代表存在 2代表删除）',
    create_by             VARCHAR(64)    DEFAULT ''    NULL COMMENT '创建者',
    create_time           DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by             VARCHAR(64)    DEFAULT ''    NULL COMMENT '更新者',
    update_time           DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark                VARCHAR(500)                  NULL COMMENT '备注',
    INDEX idx_subsidy_person_id (subsidy_person_id),
    INDEX idx_subsidy_type (subsidy_type),
    INDEX idx_distribution_status (distribution_status),
    INDEX idx_distribution_date (distribution_date),
    FOREIGN KEY fk_distribution_person (subsidy_person_id) REFERENCES shebao_subsidy_person(id)
)
COMMENT '补贴发放记录表';

-- 3. 创建发放审核记录表
CREATE TABLE shebao_distribution_review
(
    id                    BIGINT AUTO_INCREMENT COMMENT '主键ID' PRIMARY KEY,
    distribution_id       BIGINT               NOT NULL COMMENT '发放记录ID（关联shebao_subsidy_distribution.id）',
    operation_type        CHAR(1)              NOT NULL COMMENT '操作类型（1提交审核 2审核通过 3审核驳回 4发放 5重新提交）',
    operation_remark      VARCHAR(500)                  NULL COMMENT '操作说明',
    operator_name         VARCHAR(64)          NOT NULL COMMENT '操作人',
    operation_time        DATETIME             NOT NULL COMMENT '操作时间',
    status                CHAR(1)        DEFAULT '0'   NULL COMMENT '状态（0正常 1停用）',
    del_flag              CHAR(1)        DEFAULT '0'   NULL COMMENT '删除标志（0代表存在 2代表删除）',
    create_by             VARCHAR(64)    DEFAULT ''    NULL COMMENT '创建者',
    create_time           DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by             VARCHAR(64)    DEFAULT ''    NULL COMMENT '更新者',
    update_time           DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark                VARCHAR(500)                  NULL COMMENT '备注',
    INDEX idx_distribution_id (distribution_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_operation_time (operation_time),
    FOREIGN KEY fk_review_distribution (distribution_id) REFERENCES shebao_subsidy_distribution(id)
)
COMMENT '发放审核记录表';

-- 4. 创建补贴类型字典数据
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) 
VALUES ('补贴类型', 'subsidy_type', '0', 'admin', NOW(), '补贴类型列表');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark) 
VALUES 
(1, '失地居民补贴', '1', 'subsidy_type', '0', 'admin', NOW(), '失地居民补贴'),
(2, '被征地居民补贴', '2', 'subsidy_type', '0', 'admin', NOW(), '被征地居民补贴'),
(3, '拆迁居民补贴', '3', 'subsidy_type', '0', 'admin', NOW(), '拆迁居民补贴'),
(4, '村干部补贴', '4', 'subsidy_type', '0', 'admin', NOW(), '村干部补贴');

-- 5. 创建发放状态字典数据
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) 
VALUES ('发放状态', 'distribution_status', '0', 'admin', NOW(), '补贴发放状态列表');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark) 
VALUES 
(1, '未发放', '0', 'distribution_status', '0', 'admin', NOW(), '未发放'),
(2, '待审核', '1', 'distribution_status', '0', 'admin', NOW(), '待审核'),
(3, '待发放', '2', 'distribution_status', '0', 'admin', NOW(), '待发放'),
(4, '已驳回', '3', 'distribution_status', '0', 'admin', NOW(), '已驳回'),
(5, '已发放', '4', 'distribution_status', '0', 'admin', NOW(), '已发放');

-- 6. 创建操作类型字典数据
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) 
VALUES ('发放操作类型', 'distribution_operation_type', '0', 'admin', NOW(), '发放操作类型列表');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark) 
VALUES 
(1, '提交审核', '1', 'distribution_operation_type', '0', 'admin', NOW(), '提交审核'),
(2, '审核通过', '2', 'distribution_operation_type', '0', 'admin', NOW(), '审核通过'),
(3, '审核驳回', '3', 'distribution_operation_type', '0', 'admin', NOW(), '审核驳回'),
(4, '发放', '4', 'distribution_operation_type', '0', 'admin', NOW(), '发放'),
(5, '重新提交', '5', 'distribution_operation_type', '0', 'admin', NOW(), '重新提交');

-- 7. 添加菜单权限（需要根据实际的父菜单ID调整parent_id值）
-- 假设社保管理菜单的ID为2000
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('补贴发放记录', 2000, 5, 'subsidyDistribution', 'shebao/subsidyDistribution/index', 1, 0, 'C', '0', '0', 'shebao:subsidyDistribution:list', 'money', 'admin', NOW(), '补贴发放记录菜单');

-- 获取刚插入的菜单ID（需要在实际执行时根据实际ID调整）
SET @menu_id = LAST_INSERT_ID();

-- 添加按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES 
('补贴发放记录查询', @menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:query', '#', 'admin', NOW(), ''),
('补贴发放记录新增', @menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:add', '#', 'admin', NOW(), ''),
('补贴发放记录删除', @menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:remove', '#', 'admin', NOW(), ''),
('补贴发放审核', @menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:approve', '#', 'admin', NOW(), ''),
('补贴发放驳回', @menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:reject', '#', 'admin', NOW(), ''),
('补贴发放', @menu_id, 6, '#', '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:distribute', '#', 'admin', NOW(), ''),
('补贴重新提交', @menu_id, 7, '#', '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:resubmit', '#', 'admin', NOW(), ''),
('补贴发放记录导出', @menu_id, 8, '#', '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:export', '#', 'admin', NOW(), '');

select * from sys_menu order by menu_id desc;