SET NAMES utf8mb4;

-- 支付计划主表
CREATE TABLE IF NOT EXISTS `shebao_payment_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `determination_type` varchar(20) NOT NULL COMMENT '核定方式(normal/second)',
  `business_period` date NOT NULL COMMENT '业务期(当月1号)',
  `total_count` int NOT NULL DEFAULT 0 COMMENT '发放人次',
  `total_amount` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
  `operator_name` varchar(64) DEFAULT NULL COMMENT '经办人',
  `operator_time` datetime DEFAULT NULL COMMENT '经办时间',
  `approval_status` varchar(20) NOT NULL DEFAULT 'pending_review' COMMENT '审批状态',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_plan_period` (`business_period`),
  KEY `idx_plan_status` (`approval_status`),
  KEY `idx_plan_type` (`determination_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付计划主表';

-- 支付计划汇总表
CREATE TABLE IF NOT EXISTS `shebao_payment_plan_summary` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plan_id` bigint NOT NULL COMMENT '计划ID',
  `business_period` date NOT NULL COMMENT '业务期(当月1号)',
  `subsidy_type` varchar(50) NOT NULL COMMENT '补贴类型',
  `grant_org` varchar(64) DEFAULT NULL COMMENT '发放机构',
  `total_count` int NOT NULL DEFAULT 0 COMMENT '发放人次',
  `total_amount` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_summary_plan_id` (`plan_id`),
  KEY `idx_summary_period` (`business_period`),
  KEY `idx_summary_type` (`subsidy_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付计划汇总表';

-- 支付计划明细表
CREATE TABLE IF NOT EXISTS `shebao_payment_plan_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plan_id` bigint NOT NULL COMMENT '计划ID',
  `determination_id` bigint DEFAULT NULL COMMENT '待遇核定主表ID',
  `determination_item_id` bigint DEFAULT NULL COMMENT '待遇核定明细ID',
  `subsidy_type` varchar(50) NOT NULL COMMENT '补贴类型',
  `street_name` varchar(100) DEFAULT NULL COMMENT '街道',
  `village_name` varchar(100) DEFAULT NULL COMMENT '村委会',
  `person_name` varchar(64) DEFAULT NULL COMMENT '姓名',
  `id_card_no` varchar(32) DEFAULT NULL COMMENT '身份证号',
  `business_period` date NOT NULL COMMENT '业务期(当月1号)',
  `payment_month` varchar(7) DEFAULT NULL COMMENT '发放月份(yyyy-MM)',
  `distribution_amount` decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '发放金额',
  `grant_org` varchar(64) DEFAULT NULL COMMENT '发放机构',
  `account_name` varchar(64) DEFAULT NULL COMMENT '开户名',
  `bank_account` varchar(64) DEFAULT NULL COMMENT '银行账号',
  `relation_to_insured` varchar(32) DEFAULT NULL COMMENT '与参保人关系',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_detail_plan_id` (`plan_id`),
  KEY `idx_detail_period` (`business_period`),
  KEY `idx_detail_item` (`determination_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付计划明细表';
