-- =============================================
-- 廊坊社保管理系统 - 数据库重构脚本
-- 阶段一: 创建新表
-- 创建日期: 2026-01-19
-- 说明: 创建审批流程、批次管理、财务管理等核心表
-- =============================================

USE ry_vue;

-- =============================================
-- 1. 审批流程记录表 (approval_log)
-- 说明: 记录所有审批操作的历史记录
-- =============================================

DROP TABLE IF EXISTS `approval_log`;

CREATE TABLE `approval_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型(person_registration/info_modify/benefit_determination/payment_plan)',
    `business_id` BIGINT NOT NULL COMMENT '业务ID',
    `current_status` VARCHAR(20) COMMENT '当前状态',
    `operation_type` VARCHAR(20) NOT NULL COMMENT '操作类型(submit/review/approve/reject/distribute/cancel)',
    `operator_id` BIGINT COMMENT '操作人ID',
    `operator_name` VARCHAR(64) NOT NULL COMMENT '操作人姓名',
    `operation_remark` VARCHAR(500) COMMENT '操作说明',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_business` (`business_type`, `business_id`),
    INDEX `idx_operator` (`operator_id`, `create_time`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批流程记录表';

-- =============================================
-- 2. 发放批次表 (distribution_batch)
-- 说明: 管理发放批次信息,支持首次、二次、三次发放
-- =============================================

DROP TABLE IF EXISTS `distribution_batch`;

CREATE TABLE `distribution_batch` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `batch_no` VARCHAR(50) NOT NULL COMMENT '批次号(格式:YYYYMMDD-XXX)',
    `subsidy_type` CHAR(1) NOT NULL COMMENT '补贴类型(1失地 2被征地 3拆迁 4村干部 5教师)',
    `batch_type` TINYINT NOT NULL DEFAULT 1 COMMENT '批次类型(1首次 2二次 3三次)',
    `parent_batch_id` BIGINT COMMENT '父批次ID(二次/三次发放关联首次批次)',
    `total_count` INT DEFAULT 0 COMMENT '总人数',
    `total_amount` DECIMAL(15,2) DEFAULT 0.00 COMMENT '总金额',
    `success_count` INT DEFAULT 0 COMMENT '成功人数',
    `success_amount` DECIMAL(15,2) DEFAULT 0.00 COMMENT '成功金额',
    `fail_count` INT DEFAULT 0 COMMENT '失败人数',
    `fail_amount` DECIMAL(15,2) DEFAULT 0.00 COMMENT '失败金额',
    `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态(draft/pending_review/pending_approve/pending_finance/submitted_bank/distributed)',
    `submit_by` VARCHAR(64) COMMENT '提交人',
    `submit_time` DATETIME COMMENT '提交时间',
    `review_by` VARCHAR(64) COMMENT '复核人',
    `review_time` DATETIME COMMENT '复核时间',
    `approve_by` VARCHAR(64) COMMENT '审批人',
    `approve_time` DATETIME COMMENT '审批时间',
    `distribute_by` VARCHAR(64) COMMENT '发放人',
    `distribute_time` DATETIME COMMENT '发放时间',
    `remark` VARCHAR(500) COMMENT '备注',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_batch_no` (`batch_no`),
    INDEX `idx_subsidy_type` (`subsidy_type`),
    INDEX `idx_batch_type` (`batch_type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_parent_batch` (`parent_batch_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发放批次表';

-- =============================================
-- 3. 审批角色配置表 (approval_role_config)
-- 说明: 配置不同业务类型的审批层级和角色
-- =============================================

DROP TABLE IF EXISTS `approval_role_config`;

CREATE TABLE `approval_role_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型',
    `approval_level` TINYINT NOT NULL COMMENT '审批层级(1经办 2复核 3审批 4财务)',
    `role_id` BIGINT COMMENT '角色ID',
    `role_key` VARCHAR(100) COMMENT '角色标识',
    `role_name` VARCHAR(100) COMMENT '角色名称',
    `is_required` CHAR(1) DEFAULT '1' COMMENT '是否必填(0否 1是)',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_business_level` (`business_type`, `approval_level`),
    INDEX `idx_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批角色配置表';

-- =============================================
-- 4. 财务账户表 (finance_account)
-- 说明: 管理5个补贴类型对应的财务账号
-- =============================================

DROP TABLE IF EXISTS `finance_account`;

CREATE TABLE `finance_account` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `subsidy_type` CHAR(1) NOT NULL COMMENT '补贴类型(1失地 2被征地 3拆迁 4村干部 5教师)',
    `account_name` VARCHAR(100) NOT NULL COMMENT '账号名称',
    `bank_name` VARCHAR(100) COMMENT '银行名称',
    `bank_code` VARCHAR(50) COMMENT '银行代码',
    `account_number` VARCHAR(50) COMMENT '账号号码',
    `balance` DECIMAL(15,2) DEFAULT 0.00 COMMENT '账户余额',
    `total_in` DECIMAL(15,2) DEFAULT 0.00 COMMENT '累计转入',
    `total_out` DECIMAL(15,2) DEFAULT 0.00 COMMENT '累计转出',
    `last_update_time` DATETIME COMMENT '余额更新时间',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    `remark` VARCHAR(500) COMMENT '备注',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_subsidy_type` (`subsidy_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务账户表';

-- =============================================
-- 5. 待遇核定表 (benefit_determination)
-- 说明: 记录待遇核定信息,包含到龄信息、补贴标准、补发信息
-- =============================================

DROP TABLE IF EXISTS `benefit_determination`;

CREATE TABLE `benefit_determination` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `subsidy_person_id` BIGINT NOT NULL COMMENT '被补贴人ID',
    `subsidy_type` CHAR(1) NOT NULL COMMENT '补贴类型',
    `eligible_month` DATE COMMENT '到龄年月',
    `benefit_start_month` DATE COMMENT '补贴享受开始年月',
    `bank_id` BIGINT COMMENT '发放银行ID',
    `bank_name` VARCHAR(100) COMMENT '银行名称',
    `bank_account` VARCHAR(50) COMMENT '银行账号',
    `subsidy_standard` DECIMAL(10,2) COMMENT '补贴标准',
    `benefit_months` INT COMMENT '补发月数',
    `benefit_amount` DECIMAL(10,2) COMMENT '补发金额',
    `supplement_type` CHAR(1) COMMENT '补发类型(1应补发年月 2应补发金额)',
    `supplement_start_month` DATE COMMENT '补发开始年月',
    `supplement_end_month` DATE COMMENT '补发终止年月',
    `supplement_amount` DECIMAL(10,2) COMMENT '补发金额',
    `file_path` VARCHAR(500) COMMENT '附件路径',
    `approval_status` VARCHAR(20) DEFAULT 'draft' COMMENT '审批状态',
    `submit_by` VARCHAR(64) COMMENT '提交人',
    `submit_time` DATETIME COMMENT '提交时间',
    `review_by` VARCHAR(64) COMMENT '复核人',
    `review_time` DATETIME COMMENT '复核时间',
    `review_remark` VARCHAR(500) COMMENT '复核意见',
    `reject_reason` VARCHAR(500) COMMENT '驳回原因',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_person_id` (`subsidy_person_id`),
    INDEX `idx_subsidy_type` (`subsidy_type`),
    INDEX `idx_approval_status` (`approval_status`),
    INDEX `idx_eligible_month` (`eligible_month`),
    INDEX `idx_create_time` (`create_time`),
    CONSTRAINT `fk_benefit_person` FOREIGN KEY (`subsidy_person_id`)
        REFERENCES `shebao_subsidy_person`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待遇核定表';

-- =============================================
-- 创建表完成提示
-- =============================================

SELECT '✅ 5张新表创建成功!' AS 'Status';
SELECT COUNT(*) AS 'Total Tables' FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'ry_vue'
AND TABLE_NAME IN ('approval_log', 'distribution_batch', 'approval_role_config', 'finance_account', 'benefit_determination');
