/*
 Navicat Premium Dump SQL

 Source Server         : 192.168.88.48
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : 192.168.88.48:5455
 Source Schema         : lfpm

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 02/03/2026 23:16:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for approval_log
-- ----------------------------
DROP TABLE IF EXISTS `approval_log`;
CREATE TABLE `approval_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '涓氬姟绫诲瀷(person_register/benefit_determination/payment_plan)',
  `business_id` bigint NOT NULL COMMENT '涓氬姟ID',
  `current_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '褰撳墠鐘舵?',
  `operation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '鎿嶄綔绫诲瀷(submit/review/approve/reject/cancel)',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '鎿嶄綔浜篒D',
  `operator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '鎿嶄綔浜哄?鍚',
  `operation_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鎿嶄綔璇存槑/椹冲洖鍘熷洜',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_business`(`business_type` ASC, `business_id` ASC) USING BTREE,
  INDEX `idx_operator`(`operator_id` ASC, `create_time` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '瀹℃壒娴佺▼璁板綍琛' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of approval_log
-- ----------------------------

-- ----------------------------
-- Table structure for approval_role_config
-- ----------------------------
DROP TABLE IF EXISTS `approval_role_config`;
CREATE TABLE `approval_role_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '涓氬姟绫诲瀷',
  `approval_level` int NOT NULL COMMENT '瀹℃壒灞傜骇(1/2/3)',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '瑙掕壊浠ｇ爜',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '瑙掕壊鍚嶇О',
  `required_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '鏄?惁蹇呴渶(0鍚?鏄?',
  `order_num` int NULL DEFAULT 0 COMMENT '鎺掑簭鍙',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0瀛樺湪1鍒犻櫎)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鍒涘缓鑰',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鏇存柊鑰',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_business_level`(`business_type` ASC, `approval_level` ASC) USING BTREE,
  INDEX `idx_business_type`(`business_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '瀹℃壒瑙掕壊閰嶇疆琛' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of approval_role_config
-- ----------------------------
INSERT INTO `approval_role_config` VALUES (1, 'person_register', 1, 'operator', '经办人', '1', 1, '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '人员登记：经办人提交');
INSERT INTO `approval_role_config` VALUES (2, 'person_register', 2, 'reviewer', '复核人', '1', 2, '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '人员登记：复核人复核');
INSERT INTO `approval_role_config` VALUES (3, 'benefit_determination', 1, 'operator', '经办人', '1', 1, '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '待遇核定：经办人提交');
INSERT INTO `approval_role_config` VALUES (4, 'benefit_determination', 2, 'reviewer', '复核人', '1', 2, '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '待遇核定：复核人复核');
INSERT INTO `approval_role_config` VALUES (5, 'payment_plan', 1, 'operator', '经办人', '1', 1, '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '支付计划：经办人提交');
INSERT INTO `approval_role_config` VALUES (6, 'payment_plan', 2, 'reviewer', '复核人', '1', 2, '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '支付计划：复核人复核');
INSERT INTO `approval_role_config` VALUES (7, 'payment_plan', 3, 'approver', '审批人', '1', 3, '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '支付计划：审批人审批');
INSERT INTO `approval_role_config` VALUES (8, 'payment_plan', 4, 'finance', '财务人员', '1', 4, '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '支付计划：财务人员处理');
INSERT INTO `approval_role_config` VALUES (9, 'info_modify', 1, 'operator', '经办人', '1', 1, '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '关键信息修改：经办人提交');
INSERT INTO `approval_role_config` VALUES (10, 'info_modify', 2, 'reviewer', '复核人', '1', 2, '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '关键信息修改：复核人复核');
INSERT INTO `approval_role_config` VALUES (11, 'info_modify', 3, 'approver', '审批人', '1', 3, '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '关键信息修改：审批人审批');

-- ----------------------------
-- Table structure for benefit_determination
-- ----------------------------
DROP TABLE IF EXISTS `benefit_determination`;
CREATE TABLE `benefit_determination`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '琚?ˉ璐翠汉ID',
  `subsidy_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '琛ヨ创绫诲瀷',
  `eligible_year` int NULL DEFAULT NULL COMMENT '鍒伴緞骞翠唤',
  `eligible_month` int NULL DEFAULT NULL COMMENT '鍒伴緞鏈堜唤',
  `benefit_start_year` int NULL DEFAULT NULL COMMENT '琛ヨ创浜?彈寮??骞翠唤',
  `benefit_start_month` int NULL DEFAULT NULL COMMENT '琛ヨ创浜?彈寮??鏈堜唤',
  `bank_id` bigint NULL DEFAULT NULL COMMENT '鍙戞斁閾惰?ID',
  `bank_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '閾惰?璐﹀彿',
  `bank_account_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '閾惰?璐︽埛濮撳悕',
  `subsidy_standard` decimal(10, 2) NULL DEFAULT NULL COMMENT '琛ヨ创鏍囧噯',
  `benefit_months` int NULL DEFAULT 0 COMMENT '琛ュ彂鏈堟暟',
  `benefit_amount` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '琛ュ彂閲戦?',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'draft' COMMENT '瀹℃壒鐘舵?(draft/pending_review/pending_approve/approved/rejected)',
  `approval_batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '瀹℃壒鎵规?鍙',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0瀛樺湪1鍒犻櫎)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鍒涘缓鑰',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鏇存柊鑰',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_person`(`subsidy_person_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_batch`(`approval_batch_no` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '寰呴亣鏍稿畾琛' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of benefit_determination
-- ----------------------------

-- ----------------------------
-- Table structure for distribution_batch
-- ----------------------------
DROP TABLE IF EXISTS `distribution_batch`;
CREATE TABLE `distribution_batch`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '鎵规?鍙',
  `batch_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '鎵规?绫诲瀷(first/second/third)',
  `subsidy_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '琛ヨ创绫诲瀷',
  `distribution_year` int NULL DEFAULT NULL COMMENT '鍙戞斁骞翠唤',
  `distribution_month` int NULL DEFAULT NULL COMMENT '鍙戞斁鏈堜唤',
  `total_count` int NULL DEFAULT 0 COMMENT '鎬讳汉鏁',
  `total_amount` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '鎬婚噾棰',
  `success_count` int NULL DEFAULT 0 COMMENT '鎴愬姛浜烘暟',
  `success_amount` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '鎴愬姛閲戦?',
  `fail_count` int NULL DEFAULT 0 COMMENT '澶辫触浜烘暟',
  `fail_amount` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '澶辫触閲戦?',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'draft' COMMENT '瀹℃壒鐘舵?(draft/pending_review/pending_approve/pending_finance/submitted_bank/distributed)',
  `finance_upload_time` datetime NULL DEFAULT NULL COMMENT '涓婁紶璐㈠姟绯荤粺鏃堕棿',
  `bank_submit_time` datetime NULL DEFAULT NULL COMMENT '鎻愪氦閾惰?鏃堕棿',
  `bank_result_time` datetime NULL DEFAULT NULL COMMENT '閾惰?杩斿洖缁撴灉鏃堕棿',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0瀛樺湪1鍒犻櫎)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鍒涘缓鑰',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鏇存柊鑰',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `batch_no`(`batch_no` ASC) USING BTREE,
  INDEX `idx_batch_no`(`batch_no` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_batch_type`(`batch_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '鍙戞斁鎵规?琛' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of distribution_batch
-- ----------------------------

-- ----------------------------
-- Table structure for finance_account
-- ----------------------------
DROP TABLE IF EXISTS `finance_account`;
CREATE TABLE `finance_account`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `account_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '璐﹀彿鍙风爜',
  `account_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '璐﹀彿鍚嶇О',
  `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '閾惰?鍚嶇О',
  `subsidy_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '琛ヨ创绫诲瀷(land_loss/expropriatee/demolition/village_official/teacher)',
  `balance` decimal(16, 2) NULL DEFAULT 0.00 COMMENT '璐︽埛浣欓?',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '鐘舵?(0鍋滅敤1姝ｅ父)',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0瀛樺湪1鍒犻櫎)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鍒涘缓鑰',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鏇存柊鑰',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account_no`(`account_no` ASC) USING BTREE,
  INDEX `idx_subsidy_type`(`subsidy_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '璐㈠姟璐︽埛琛' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of finance_account
-- ----------------------------
INSERT INTO `finance_account` VALUES (1, 'LF2025001', '失地居民养老补贴账户', '中国工商银行廊坊开发区支行', 'land_loss', 1000000.00, '1', '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '失地居民养老补贴专户');
INSERT INTO `finance_account` VALUES (2, 'LF2025002', '被征地居民养老补贴账户', '中国建设银行廊坊开发区支行', 'expropriatee', 800000.00, '1', '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '被征地居民养老补贴专户');
INSERT INTO `finance_account` VALUES (3, 'LF2025003', '拆迁居民养老补贴账户', '中国农业银行廊坊开发区支行', 'demolition', 500000.00, '1', '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '拆迁居民养老补贴专户');
INSERT INTO `finance_account` VALUES (4, 'LF2025004', '村干部养老补贴账户', '中国银行廊坊开发区支行', 'village_official', 300000.00, '1', '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '村干部养老补贴专户');
INSERT INTO `finance_account` VALUES (5, 'LF2025005', '教师养老补贴账户', '中国邮政储蓄银行廊坊开发区支行', 'teacher', 200000.00, '1', '0', 'admin', '2026-01-19 10:08:28', NULL, '2026-01-19 10:08:28', '教师养老补贴专户');

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------

-- ----------------------------
-- Table structure for person_key_info_modify
-- ----------------------------
DROP TABLE IF EXISTS `person_key_info_modify`;
CREATE TABLE `person_key_info_modify`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `id_card_no` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证号',
  `household_registration` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '户籍所在地',
  `street_office_id` bigint NULL DEFAULT NULL COMMENT '所属街道办ID',
  `village_committee_id` bigint NULL DEFAULT NULL COMMENT '所属村委会ID',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'draft' COMMENT '审批状态(draft-草稿 pending_review-待复核 pending_approve-待审批 approved-已通过 rejected-已驳回)',
  `submit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '提交人',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `review_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '复核人',
  `review_time` datetime NULL DEFAULT NULL COMMENT '复核时间',
  `approve_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '驳回原因',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_subsidy_person_id`(`subsidy_person_id` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_id_card_no`(`id_card_no` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '人员关键信息修改申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of person_key_info_modify
-- ----------------------------
INSERT INTO `person_key_info_modify` VALUES (1, 1000001, '刘三娘', '131131198411120021', '廊坊', 1, 2, 'approved', 'admin', '2026-02-03 18:53:15', 'admin', '2026-02-03 18:54:16', 'admin', '2026-02-03 18:54:29', NULL, '0', 'admin', '2026-02-03 18:53:14', 'admin', '2026-02-03 18:54:29', NULL);

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob NULL COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Blob类型的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '日历信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '已触发的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务组名',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob NULL COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '存储的悲观锁信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '暂停的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '调度器状态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint NULL DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint NULL DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int NULL DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint NULL DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint NULL DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob NULL COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  INDEX `sched_name`(`sched_name` ASC, `job_name` ASC, `job_group` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '触发器详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for shebao_administrative_division
-- ----------------------------
DROP TABLE IF EXISTS `shebao_administrative_division`;
CREATE TABLE `shebao_administrative_division`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `division_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '行政区划编码',
  `division_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '行政区划名称',
  `parent_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级编码',
  `division_level` tinyint NOT NULL COMMENT '行政级别（1省 2市 3县 4乡镇 5村）',
  `full_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '全路径编码，用/分隔',
  `full_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '全路径名称，用/分隔',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_division_code`(`division_code` ASC) USING BTREE,
  INDEX `idx_parent_code`(`parent_code` ASC) USING BTREE,
  INDEX `idx_division_level`(`division_level` ASC) USING BTREE,
  INDEX `idx_full_code`(`full_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '行政区划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_administrative_division
-- ----------------------------
INSERT INTO `shebao_administrative_division` VALUES (1, '130000', '河北省', NULL, 1, '130000', '河北省', NULL, NULL, NULL, 1, '0', '0', '', '2026-01-19 23:48:01', '', '2026-01-19 23:48:01', NULL);
INSERT INTO `shebao_administrative_division` VALUES (2, '131000', '廊坊市', '130000', 2, '130000/131000', '河北省/廊坊市', NULL, NULL, NULL, 1, '0', '0', '', '2026-01-19 23:48:01', '', '2026-01-19 23:48:01', NULL);
INSERT INTO `shebao_administrative_division` VALUES (3, '131001', '安次区', '131000', 3, '130000/131000/131001', '河北省/廊坊市/安次区', NULL, NULL, NULL, 1, '0', '0', '', '2026-01-19 23:48:01', '', '2026-01-19 23:48:01', NULL);
INSERT INTO `shebao_administrative_division` VALUES (4, '131002', '广阳区', '131000', 3, '130000/131000/131002', '河北省/廊坊市/广阳区', NULL, NULL, NULL, 2, '0', '0', '', '2026-01-19 23:48:01', '', '2026-01-19 23:48:01', NULL);
INSERT INTO `shebao_administrative_division` VALUES (5, '131003', '开发区', '131000', 3, '130000/131000/131003', '河北省/廊坊市/开发区', NULL, NULL, NULL, 3, '0', '0', '', '2026-01-19 23:48:01', '', '2026-01-19 23:48:01', NULL);
INSERT INTO `shebao_administrative_division` VALUES (6, '131001001', '银河南路街道', '131001', 4, '130000/131000/131001/131001001', '河北省/廊坊市/安次区/银河南路街道', NULL, NULL, NULL, 1, '0', '0', '', '2026-01-19 23:48:01', '', '2026-01-19 23:48:01', NULL);
INSERT INTO `shebao_administrative_division` VALUES (7, '131001002', '光明西道街道', '131001', 4, '130000/131000/131001/131001002', '河北省/廊坊市/安次区/光明西道街道', NULL, NULL, NULL, 2, '0', '0', '', '2026-01-19 23:48:01', '', '2026-01-19 23:48:01', NULL);
INSERT INTO `shebao_administrative_division` VALUES (8, '131002001', '解放道街道', '131002', 4, '130000/131000/131002/131002001', '河北省/廊坊市/广阳区/解放道街道', NULL, NULL, NULL, 1, '0', '0', '', '2026-01-19 23:48:01', '', '2026-01-19 23:48:01', NULL);
INSERT INTO `shebao_administrative_division` VALUES (9, '131002002', '新开路街道', '131002', 4, '130000/131000/131002/131002002', '河北省/廊坊市/广阳区/新开路街道', NULL, NULL, NULL, 2, '0', '0', '', '2026-01-19 23:48:01', '', '2026-01-19 23:48:01', NULL);

-- ----------------------------
-- Table structure for shebao_demo
-- ----------------------------
DROP TABLE IF EXISTS `shebao_demo`;
CREATE TABLE `shebao_demo`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_demo
-- ----------------------------
INSERT INTO `shebao_demo` VALUES (1, '123', '0', '0', '', '2025-09-26 23:54:56', '', '2025-09-26 23:54:56', NULL);

-- ----------------------------
-- Table structure for shebao_demolition_resident
-- ----------------------------
DROP TABLE IF EXISTS `shebao_demolition_resident`;
CREATE TABLE `shebao_demolition_resident`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `demolition_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拆迁事由',
  `demolition_time` date NULL DEFAULT NULL COMMENT '拆迁时间',
  `recognition_time` date NULL DEFAULT NULL COMMENT '认定为拆迁居民时间',
  `subsidy_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '补贴金额',
  `distribution_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_subsidy_person_id`(`subsidy_person_id` ASC) USING BTREE,
  INDEX `idx_demolition_time`(`demolition_time` ASC) USING BTREE,
  INDEX `idx_recognition_time`(`recognition_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '拆迁居民信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_demolition_resident
-- ----------------------------
INSERT INTO `shebao_demolition_resident` VALUES (1, 1000000, '修路', '2025-09-29', '2025-09-16', 0.00, '0', '0', '0', 'admin', '2025-09-28 22:10:57', '', '2025-09-28 22:38:01', NULL);
INSERT INTO `shebao_demolition_resident` VALUES (2, 1000016, '222', '2025-10-03', '2025-10-04', 0.00, '0', '0', '0', 'admin', '2025-10-19 17:29:22', 'admin', '2025-10-19 17:54:36', '555');

-- ----------------------------
-- Table structure for shebao_distribution_review
-- ----------------------------
DROP TABLE IF EXISTS `shebao_distribution_review`;
CREATE TABLE `shebao_distribution_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `distribution_id` bigint NOT NULL COMMENT '发放记录ID（关联shebao_subsidy_distribution.id）',
  `operation_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型（1提交审核 2审核通过 3审核驳回 4发放 5重新提交）',
  `operation_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作说明',
  `operator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作人',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_distribution_id`(`distribution_id` ASC) USING BTREE,
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_operation_time`(`operation_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '发放审核记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_distribution_review
-- ----------------------------
INSERT INTO `shebao_distribution_review` VALUES (1, 1, '1', '提交审核', 'admin', '2025-10-19 20:59:18', '0', '0', '', '2025-10-19 20:59:17', '', '2025-10-19 20:59:17', NULL);
INSERT INTO `shebao_distribution_review` VALUES (2, 2, '1', '提交审核', 'admin', '2025-10-19 21:16:55', '0', '0', '', '2025-10-19 21:16:55', '', '2025-10-19 21:16:55', NULL);
INSERT INTO `shebao_distribution_review` VALUES (3, 2, '2', '同意', 'admin', '2025-10-19 21:19:58', '0', '0', '', '2025-10-19 21:19:58', '', '2025-10-19 21:19:58', NULL);
INSERT INTO `shebao_distribution_review` VALUES (4, 3, '1', '提交审核', 'admin', '2025-10-19 21:20:41', '0', '0', '', '2025-10-19 21:20:40', '', '2025-10-19 21:20:40', NULL);
INSERT INTO `shebao_distribution_review` VALUES (5, 4, '1', '提交审核', 'admin', '2025-10-19 21:21:37', '0', '0', '', '2025-10-19 21:21:37', '', '2025-10-19 21:21:37', NULL);
INSERT INTO `shebao_distribution_review` VALUES (6, 4, '2', '同意', 'admin', '2025-10-19 21:21:43', '0', '0', '', '2025-10-19 21:21:43', '', '2025-10-19 21:21:43', NULL);
INSERT INTO `shebao_distribution_review` VALUES (7, 4, '4', '同意', 'admin', '2025-10-19 21:21:49', '0', '0', '', '2025-10-19 21:21:49', '', '2025-10-19 21:21:49', NULL);
INSERT INTO `shebao_distribution_review` VALUES (8, 5, '1', '提交审核', 'admin', '2025-10-19 22:04:58', '0', '0', '', '2025-10-19 22:04:58', '', '2025-10-19 22:04:58', NULL);
INSERT INTO `shebao_distribution_review` VALUES (9, 6, '1', '提交审核', 'admin', '2025-10-19 22:13:58', '0', '0', '', '2025-10-19 22:13:58', '', '2025-10-19 22:13:58', NULL);
INSERT INTO `shebao_distribution_review` VALUES (10, 7, '1', '提交审核', 'admin', '2025-10-19 22:15:01', '0', '0', '', '2025-10-19 22:15:00', '', '2025-10-19 22:15:00', NULL);
INSERT INTO `shebao_distribution_review` VALUES (11, 8, '1', '提交审核', 'admin', '2025-10-19 22:15:20', '0', '0', '', '2025-10-19 22:15:20', '', '2025-10-19 22:15:20', NULL);
INSERT INTO `shebao_distribution_review` VALUES (12, 9, '1', '提交审核', 'admin', '2025-10-19 22:33:03', '0', '0', '', '2025-10-19 22:33:03', '', '2025-10-19 22:33:03', NULL);
INSERT INTO `shebao_distribution_review` VALUES (13, 10, '1', '1231231231', 'admin', '2025-10-19 22:49:37', '0', '0', '', '2025-10-19 22:49:37', '', '2025-10-19 22:49:37', NULL);
INSERT INTO `shebao_distribution_review` VALUES (14, 10, '2', '', 'admin', '2025-10-19 22:52:00', '0', '0', '', '2025-10-19 22:52:00', '', '2025-10-19 22:52:00', NULL);

-- ----------------------------
-- Table structure for shebao_expropriatee_subsidy
-- ----------------------------
DROP TABLE IF EXISTS `shebao_expropriatee_subsidy`;
CREATE TABLE `shebao_expropriatee_subsidy`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `land_requisition_batch` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '征地批次',
  `base_date` date NULL DEFAULT NULL COMMENT '基准日',
  `employee_pension_months` int NULL DEFAULT 0 COMMENT '职工身份缴纳职工养老月数',
  `flexible_employment_months` int NULL DEFAULT 0 COMMENT '灵活就业身份缴纳职工养老保险月数',
  `difficulty_subsidy_years` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '已领取困难人员社会保险补贴年限',
  `age_at_base_date` int NULL DEFAULT NULL COMMENT '截至基准日的年龄',
  `subsidy_years` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '被征地参保补贴年限',
  `subsidy_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '补贴金额',
  `distribution_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
  `join_urban_rural_insurance` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '选择参加城乡居保（0否 1是）',
  `join_employee_pension` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '选择参加职工养老（0否 1是）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_subsidy_person_id`(`subsidy_person_id` ASC) USING BTREE,
  INDEX `idx_batch`(`land_requisition_batch` ASC) USING BTREE,
  INDEX `idx_base_date`(`base_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '被征地参保补贴账户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_expropriatee_subsidy
-- ----------------------------
INSERT INTO `shebao_expropriatee_subsidy` VALUES (1, 1000000, '20202', '2025-09-09', 120, 10, 0.00, 40, 0.00, 0.00, '0', '0', '0', '0', '0', 'admin', '2025-09-28 12:23:17', '', '2025-09-28 22:38:10', NULL);
INSERT INTO `shebao_expropriatee_subsidy` VALUES (2, 1000002, NULL, '2025-09-01', 0, 0, 0.00, 70, 15.00, 7147.00, '0', '1', '0', '0', '0', 'admin', '2025-09-28 22:59:15', '', '2025-09-28 22:59:15', NULL);
INSERT INTO `shebao_expropriatee_subsidy` VALUES (3, 1000003, '第一批', '2025-09-01', 0, 0, 0.00, 69, 15.00, 7147.22, '0', '0', '0', '0', '0', 'admin', '2025-09-28 23:43:51', '', '2025-09-28 23:43:51', NULL);
INSERT INTO `shebao_expropriatee_subsidy` VALUES (4, 1000000, NULL, '2025-10-01', 0, 0, 0.00, 40, 3.00, 1429.44, '0', '0', '0', '0', '0', 'admin', '2025-10-16 15:35:19', '', '2025-10-16 15:35:19', NULL);

-- ----------------------------
-- Table structure for shebao_land_loss_resident
-- ----------------------------
DROP TABLE IF EXISTS `shebao_land_loss_resident`;
CREATE TABLE `shebao_land_loss_resident`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `land_requisition_time` date NULL DEFAULT NULL COMMENT '征地时间',
  `compensation_complete_time` date NULL DEFAULT NULL COMMENT '完成补偿时间',
  `recognition_time` date NULL DEFAULT NULL COMMENT '认定为失地居民时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_subsidy_person_id`(`subsidy_person_id` ASC) USING BTREE,
  INDEX `idx_recognition_time`(`recognition_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '失地居民信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_land_loss_resident
-- ----------------------------
INSERT INTO `shebao_land_loss_resident` VALUES (1, 1000001, '2025-09-01', '2025-09-22', '2025-09-23', '0', 'admin', '2025-09-28 02:18:49', 'admin', '2025-10-19 22:20:46', '每月8号发');
INSERT INTO `shebao_land_loss_resident` VALUES (2, 1000005, '2025-10-01', '2025-09-29', '2025-10-01', '0', 'admin', '2025-10-11 16:28:26', '', '2025-10-11 16:28:26', NULL);
INSERT INTO `shebao_land_loss_resident` VALUES (3, 1000000, '2025-10-01', '2025-10-22', '2025-10-01', '0', 'admin', '2025-10-11 16:29:39', 'admin', '2025-10-19 20:52:52', NULL);
INSERT INTO `shebao_land_loss_resident` VALUES (4, 1000018, '2026-01-25', '2026-01-26', '2026-01-27', '0', 'admin', '2026-01-25 14:33:43', '', '2026-01-25 14:33:43', NULL);
INSERT INTO `shebao_land_loss_resident` VALUES (5, 1000019, '2025-12-01', '2025-12-01', '2026-01-02', '0', 'admin', '2026-01-30 09:20:23', 'admin', '2026-02-03 15:07:12', NULL);

-- ----------------------------
-- Table structure for shebao_person_cancel
-- ----------------------------
DROP TABLE IF EXISTS `shebao_person_cancel`;
CREATE TABLE `shebao_person_cancel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID（关联shebao_subsidy_person.id）',
  `death_date` date NOT NULL COMMENT '死亡时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_subsidy_person_id`(`subsidy_person_id` ASC) USING BTREE,
  INDEX `idx_death_date`(`death_date` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_person_cancel_person` FOREIGN KEY (`subsidy_person_id`) REFERENCES `shebao_subsidy_person` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '人员注销登记表（死亡登记）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_person_cancel
-- ----------------------------
INSERT INTO `shebao_person_cancel` VALUES (1, 1000018, '2026-01-19', '0', NULL, 'admin', '2026-01-25 14:35:37', '', '2026-01-25 14:35:37');
INSERT INTO `shebao_person_cancel` VALUES (2, 1000005, '2026-02-01', '0', NULL, 'admin', '2026-02-03 15:07:53', '', '2026-02-03 15:07:52');

-- ----------------------------
-- Table structure for shebao_street_office
-- ----------------------------
DROP TABLE IF EXISTS `shebao_street_office`;
CREATE TABLE `shebao_street_office`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `street_code` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '街道办编码（3位数字，如：001）',
  `street_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '街道办名称',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_street_code`(`street_code` ASC) USING BTREE,
  INDEX `idx_street_name`(`street_name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '街道办事处信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_street_office
-- ----------------------------
INSERT INTO `shebao_street_office` VALUES (1, '001', '银河南路街道', NULL, NULL, NULL, 1, '0', '0', 'admin', '2025-10-18 23:56:47', '', '2025-10-18 23:56:47', NULL);
INSERT INTO `shebao_street_office` VALUES (2, '002', '龙河路街道', NULL, NULL, NULL, 2, '0', '0', 'admin', '2025-10-18 23:56:47', '', '2025-10-18 23:56:47', NULL);
INSERT INTO `shebao_street_office` VALUES (3, '003', '光明路街道', NULL, NULL, NULL, 3, '0', '0', 'admin', '2025-10-18 23:56:47', '', '2025-10-18 23:56:47', NULL);
INSERT INTO `shebao_street_office` VALUES (4, '004', '麦子店街道', '黄', '13612345678', '河北廊坊', 0, '0', '0', '', '2025-10-19 11:02:13', '', '2025-10-19 11:02:13', '测试');

-- ----------------------------
-- Table structure for shebao_subsidy_distribution
-- ----------------------------
DROP TABLE IF EXISTS `shebao_subsidy_distribution`;
CREATE TABLE `shebao_subsidy_distribution`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鎵规?鍙',
  `batch_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'first' COMMENT '鎵规?绫诲瀷(first/second/third)',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'draft' COMMENT '瀹℃壒鐘舵?(draft/pending_review/pending_approve/pending_finance/distributed/rejected)',
  `rejection_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '椹冲洖鍘熷洜',
  `review_time` datetime NULL DEFAULT NULL COMMENT '澶嶆牳鏃堕棿',
  `review_user_id` bigint NULL DEFAULT NULL COMMENT '澶嶆牳浜篒D',
  `review_user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '澶嶆牳浜哄?鍚',
  `approve_time` datetime NULL DEFAULT NULL COMMENT '瀹℃壒鏃堕棿',
  `approve_user_id` bigint NULL DEFAULT NULL COMMENT '瀹℃壒浜篒D',
  `approve_user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '瀹℃壒浜哄?鍚',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID（关联shebao_subsidy_person.id）',
  `subsidy_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '补贴类型（1失地居民补贴 2被征地居民补贴 3拆迁居民补贴 4村干部补贴）',
  `subsidy_record_id` bigint NOT NULL COMMENT '补贴身份记录ID（关联具体补贴类型表的ID）',
  `distribution_amount` decimal(10, 2) NOT NULL COMMENT '发放金额',
  `distribution_date` date NULL DEFAULT NULL COMMENT '发放日期',
  `distribution_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
  `review_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核说明',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_subsidy_person_id`(`subsidy_person_id` ASC) USING BTREE,
  INDEX `idx_subsidy_type`(`subsidy_type` ASC) USING BTREE,
  INDEX `idx_distribution_status`(`distribution_status` ASC) USING BTREE,
  INDEX `idx_distribution_date`(`distribution_date` ASC) USING BTREE,
  INDEX `idx_batch_no`(`batch_no` ASC) USING BTREE,
  INDEX `idx_approval_status`(`approval_status` ASC) USING BTREE,
  INDEX `idx_batch_type`(`batch_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '补贴发放记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_subsidy_distribution
-- ----------------------------
INSERT INTO `shebao_subsidy_distribution` VALUES (1, NULL, 'first', 'draft', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1000000, '1', 3, 100.00, NULL, '1', NULL, '0', '0', 'admin', '2025-10-19 20:59:18', '', '2025-10-19 20:59:17', NULL);
INSERT INTO `shebao_subsidy_distribution` VALUES (2, NULL, 'first', 'draft', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1000000, '3', 1, 123.45, NULL, '2', '同意', '0', '0', 'admin', '2025-10-19 21:16:55', 'admin', '2025-10-19 21:19:58', NULL);
INSERT INTO `shebao_subsidy_distribution` VALUES (3, NULL, 'first', 'draft', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1000003, '2', 3, 100.00, NULL, '1', NULL, '0', '0', 'admin', '2025-10-19 21:20:41', '', '2025-10-19 21:20:40', NULL);
INSERT INTO `shebao_subsidy_distribution` VALUES (4, NULL, 'first', 'draft', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1000001, '1', 1, 111.00, '2025-10-19', '4', '同意', '0', '0', 'admin', '2025-10-19 21:21:37', 'admin', '2025-10-19 21:21:49', NULL);
INSERT INTO `shebao_subsidy_distribution` VALUES (5, NULL, 'first', 'draft', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1000001, '1', 1, 0.00, NULL, '1', NULL, '0', '0', 'admin', '2025-10-19 22:04:58', '', '2025-10-19 22:04:58', '111');
INSERT INTO `shebao_subsidy_distribution` VALUES (6, NULL, 'first', 'draft', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1000001, '1', 1, 100.00, NULL, '1', NULL, '0', '0', 'admin', '2025-10-19 22:13:58', '', '2025-10-19 22:13:58', '111');
INSERT INTO `shebao_subsidy_distribution` VALUES (7, NULL, 'first', 'draft', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1000001, '1', 1, 200.00, NULL, '1', NULL, '0', '0', 'admin', '2025-10-19 22:15:01', '', '2025-10-19 22:15:00', NULL);
INSERT INTO `shebao_subsidy_distribution` VALUES (8, NULL, 'first', 'draft', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1000001, '1', 1, 30.00, NULL, '1', NULL, '0', '0', 'admin', '2025-10-19 22:15:20', '', '2025-10-19 22:15:20', '333');
INSERT INTO `shebao_subsidy_distribution` VALUES (9, NULL, 'first', 'draft', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1000001, '1', 1, 0.01, NULL, '1', NULL, '0', '0', 'admin', '2025-10-19 22:33:03', '', '2025-10-19 22:33:03', '12321');
INSERT INTO `shebao_subsidy_distribution` VALUES (10, NULL, 'first', 'draft', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1000001, '1', 1, 0.01, NULL, '2', '', '0', '0', 'admin', '2025-10-19 22:49:37', 'admin', '2025-10-19 22:52:00', '1231231231');

-- ----------------------------
-- Table structure for shebao_subsidy_person
-- ----------------------------
DROP TABLE IF EXISTS `shebao_subsidy_person`;
CREATE TABLE `shebao_subsidy_person`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户编号（系统生成，自增主键）',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '性别（1男 2女）',
  `id_card_no` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证号',
  `certification_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'uncertified' COMMENT '璁よ瘉鐘舵?(uncertified/april/october)',
  `last_certification_date` date NULL DEFAULT NULL COMMENT '鏈?悗璁よ瘉鏃ユ湡',
  `suspension_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '鏆傚仠鐘舵?(0姝ｅ父1鏆傚仠)',
  `suspension_start_date` date NULL DEFAULT NULL COMMENT '鏆傚仠寮??鏃ユ湡',
  `suspension_reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鏆傚仠鍘熷洜(death/uncertified/imprisonment/missing/other)',
  `suspension_end_date` date NULL DEFAULT NULL COMMENT '鏆傚仠缁撴潫鏃ユ湡',
  `suspension_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '鏆傚仠澶囨敞',
  `birthday` date NOT NULL COMMENT '生日',
  `household_registration` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '户籍所在地',
  `home_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `is_alive` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否健在（0否 1是）',
  `death_date` date NULL DEFAULT NULL COMMENT '死亡时间',
  `is_village_coop_member` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否村合作经济组织成员（0否 1是）',
  `street_office_id` bigint NULL DEFAULT NULL COMMENT '所属街道办ID',
  `village_committee_id` bigint NULL DEFAULT NULL COMMENT '所属村委会ID',
  `user_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户编号（格式：001-002-0001）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_id_card_no`(`id_card_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_code`(`user_code` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_street_office_id`(`street_office_id` ASC) USING BTREE,
  INDEX `idx_village_committee_id`(`village_committee_id` ASC) USING BTREE,
  INDEX `idx_is_alive`(`is_alive` ASC) USING BTREE,
  INDEX `idx_certification_status`(`certification_status` ASC) USING BTREE,
  INDEX `idx_suspension_status`(`suspension_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1000020 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '被补贴人基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_subsidy_person
-- ----------------------------
INSERT INTO `shebao_subsidy_person` VALUES (1000000, '高老汉', '2', '131131193411120001', 'uncertified', NULL, '0', NULL, NULL, NULL, NULL, '1934-11-12', '河北廊坊', '12321', '13112345678', '1', NULL, '1', 1, 2, '0010020001', '0', '0', 'admin', '2025-09-28 01:49:29', 'admin', '2025-10-20 00:44:29', NULL);
INSERT INTO `shebao_subsidy_person` VALUES (1000001, '刘三娘', '2', '131131198411120021', 'uncertified', NULL, '0', NULL, NULL, NULL, NULL, '1984-11-12', '廊坊', '12321', '13012345678', '1', NULL, '1', 1, 2, '0010020002', '0', '0', 'admin', '2025-09-28 22:43:24', 'admin', '2026-02-03 18:54:29', NULL);
INSERT INTO `shebao_subsidy_person` VALUES (1000002, '刘大娘', '2', '131131195611120005', 'uncertified', NULL, '0', NULL, NULL, NULL, NULL, '1956-11-12', '廊坊', '三里屯', '13012345678', '1', NULL, '1', 2, 3, '0020010001', '0', '0', 'admin', '2025-09-28 22:59:15', 'admin', '2025-10-20 00:42:21', NULL);
INSERT INTO `shebao_subsidy_person` VALUES (1000003, '王老五', '2', '131131195411120000', 'uncertified', NULL, '0', NULL, NULL, NULL, NULL, '1955-11-12', '廊坊', NULL, '13012345678', '1', NULL, '1', 2, 3, '0020010002', '0', '0', 'admin', '2025-09-28 23:43:51', '', '2025-10-19 14:07:01', NULL);
INSERT INTO `shebao_subsidy_person` VALUES (1000004, '刘玉连', '1', '132801195611252658', 'uncertified', NULL, '0', NULL, NULL, NULL, NULL, '1956-11-25', '河北省廊坊市', 'asdfadsf', '130000000000', '1', NULL, '1', 1, 1, '0010010001', '0', '0', 'admin', '2025-10-11 10:58:40', 'admin', '2025-10-20 00:44:07', NULL);
INSERT INTO `shebao_subsidy_person` VALUES (1000005, '高老汉', '2', '13280119450112452X', 'uncertified', NULL, '0', NULL, NULL, NULL, NULL, '1945-01-12', '啊', '和平区', '13333333333', '0', '2026-02-01', '1', 1, 1, '0010010002', '0', '0', 'admin', '2025-10-11 16:28:26', 'admin', '2026-02-03 15:07:53', NULL);
INSERT INTO `shebao_subsidy_person` VALUES (1000006, '大毛', '2', '13280119650112422X', 'uncertified', NULL, '0', NULL, NULL, NULL, NULL, '1965-01-12', '河北', '家里', '13601243558', '1', '2025-10-20', '1', 1, 1, '0010010003', '0', '0', 'admin', '2025-10-19 13:55:31', 'admin', '2025-10-19 15:13:58', '123');
INSERT INTO `shebao_subsidy_person` VALUES (1000015, '刘二娘', '2', '131131198411120004', 'uncertified', NULL, '0', NULL, NULL, NULL, NULL, '1984-11-12', '廊坊', '111', '13012345678', '1', NULL, '1', 1, 1, '0010010004', '0', '0', 'admin', '2025-10-19 17:50:27', '', '2025-10-19 17:50:26', NULL);
INSERT INTO `shebao_subsidy_person` VALUES (1000016, '刘大娘', '1', '131131198411120012', 'uncertified', NULL, '0', NULL, NULL, NULL, NULL, '1984-11-12', '廊坊', '1111', '13012345678', '1', NULL, '1', 1, 1, '0010010005', '0', '0', 'admin', '2025-10-19 17:54:10', '', '2025-10-19 17:54:26', NULL);
INSERT INTO `shebao_subsidy_person` VALUES (1000018, '张老汉', '1', '142725198101111110', 'uncertified', NULL, '0', NULL, NULL, NULL, NULL, '2026-01-01', '廊坊', NULL, '188110948483', '0', '2026-01-19', '1', 1, 1, '0010010006', '0', '0', 'admin', '2026-01-25 14:33:43', 'admin', '2026-01-25 14:35:37', NULL);
INSERT INTO `shebao_subsidy_person` VALUES (1000019, '张一一', '1', '132801197209052622', 'uncertified', NULL, '0', NULL, NULL, NULL, NULL, '1972-09-05', '第一村', '第一村', '13555556666', '1', NULL, '1', 1, 1, '0010010007', '0', '0', 'admin', '2026-01-30 09:20:23', 'admin', '2026-02-03 15:07:12', NULL);

-- ----------------------------
-- Table structure for shebao_teacher_subsidy
-- ----------------------------
DROP TABLE IF EXISTS `shebao_teacher_subsidy`;
CREATE TABLE `shebao_teacher_subsidy`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID（关联shebao_subsidy_person.id）',
  `school_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学校名称',
  `teaching_years` int NULL DEFAULT NULL COMMENT '任教年限',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_subsidy_person_id`(`subsidy_person_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_teacher_subsidy_person` FOREIGN KEY (`subsidy_person_id`) REFERENCES `shebao_subsidy_person` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教龄补助登记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_teacher_subsidy
-- ----------------------------

-- ----------------------------
-- Table structure for shebao_village_committee
-- ----------------------------
DROP TABLE IF EXISTS `shebao_village_committee`;
CREATE TABLE `shebao_village_committee`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `street_office_id` bigint NOT NULL COMMENT '所属街道办ID',
  `village_code` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '村委会编码（3位数字，如：002）',
  `village_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '村委会名称',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序号',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_street_village_code`(`street_office_id` ASC, `village_code` ASC) USING BTREE,
  INDEX `idx_street_office_id`(`street_office_id` ASC) USING BTREE,
  INDEX `idx_village_name`(`village_name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '村委会信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_village_committee
-- ----------------------------
INSERT INTO `shebao_village_committee` VALUES (1, 1, '001', '第一村', NULL, NULL, NULL, 1, '0', '0', 'admin', '2025-10-18 23:56:54', '', '2025-10-18 23:56:54', NULL);
INSERT INTO `shebao_village_committee` VALUES (2, 1, '002', '第二村', NULL, NULL, NULL, 2, '0', '0', 'admin', '2025-10-18 23:56:54', '', '2025-10-18 23:56:54', NULL);
INSERT INTO `shebao_village_committee` VALUES (3, 2, '001', '东村', NULL, NULL, NULL, 1, '0', '0', 'admin', '2025-10-18 23:56:54', '', '2025-10-18 23:56:54', NULL);

-- ----------------------------
-- Table structure for shebao_village_official
-- ----------------------------
DROP TABLE IF EXISTS `shebao_village_official`;
CREATE TABLE `shebao_village_official`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `total_service_years` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '累计任职年限',
  `has_violation` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '是否违法乱纪或判刑（0否 1是）',
  `subsidy_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '补贴金额',
  `distribution_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_subsidy_person_id`(`subsidy_person_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '村干部信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_village_official
-- ----------------------------
INSERT INTO `shebao_village_official` VALUES (1, 1000000, 10.00, '0', 0.00, '0', '0', '0', 'admin', '2025-09-28 22:18:31', 'admin', '2025-09-28 23:50:52', NULL);
INSERT INTO `shebao_village_official` VALUES (2, 1000004, 0.00, '0', 0.00, '0', '0', '0', 'admin', '2025-10-11 10:58:40', '', '2025-10-11 10:58:39', NULL);
INSERT INTO `shebao_village_official` VALUES (3, 1000000, 0.00, '0', 0.00, '0', '0', '0', 'admin', '2025-10-16 15:37:18', '', '2025-10-16 15:37:18', NULL);

-- ----------------------------
-- Table structure for shebao_village_official_position
-- ----------------------------
DROP TABLE IF EXISTS `shebao_village_official_position`;
CREATE TABLE `shebao_village_official_position`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `village_official_id` bigint NOT NULL COMMENT '村干部信息ID',
  `position` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任职职位（1书记或主任 2书记兼主任 3村\"两委\"其他成员）',
  `start_date` date NOT NULL COMMENT '上任时间',
  `end_date` date NULL DEFAULT NULL COMMENT '卸任时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_village_official_id`(`village_official_id` ASC) USING BTREE,
  INDEX `idx_position`(`position` ASC) USING BTREE,
  INDEX `idx_start_date`(`start_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '村干部任职信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shebao_village_official_position
-- ----------------------------
INSERT INTO `shebao_village_official_position` VALUES (1, 1, '1', '2025-08-01', '2025-09-30', '0', '2', 'admin', '2025-09-28 22:18:31', '', '2025-09-28 23:50:52', NULL);
INSERT INTO `shebao_village_official_position` VALUES (2, 1, '2', '2025-09-30', '2026-09-30', '0', '2', 'admin', '2025-09-28 22:18:31', '', '2025-09-28 23:50:52', NULL);
INSERT INTO `shebao_village_official_position` VALUES (3, 1, '2', '2025-09-30', '2026-09-30', '0', '0', 'admin', '2025-09-28 23:50:52', '', '2025-09-28 23:50:52', '1');
INSERT INTO `shebao_village_official_position` VALUES (4, 1, '1', '2025-08-01', '2025-09-30', '0', '0', 'admin', '2025-09-28 23:50:52', '', '2025-09-28 23:50:52', '2');
INSERT INTO `shebao_village_official_position` VALUES (5, 2, '1', '2018-07-07', '2025-10-15', '0', '0', 'admin', '2025-10-11 10:58:40', '', '2025-10-11 10:58:39', NULL);

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2025-09-24 22:11:39', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2025-09-24 22:11:39', '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2025-09-24 22:11:39', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'false', 'Y', 'admin', '2025-09-24 22:11:39', 'admin', '2025-09-26 17:27:23', '是否开启验证码功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2025-09-24 22:11:39', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (6, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2025-09-24 22:11:39', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
INSERT INTO `sys_config` VALUES (7, '用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 'Y', 'admin', '2025-09-24 22:11:39', '', NULL, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES (8, '用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 'Y', 'admin', '2025-09-24 22:11:39', '', NULL, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES (100, '社保-年平均工资', 'shebao.average.annual.salary', '56724', 'N', 'admin', '2025-09-28 22:47:18', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 200 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', '社保局', 0, '', '', '', '0', '0', 'admin', '2025-09-24 22:11:35', 'admin', '2025-09-26 17:36:50');
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '办公室', 1, '', '', '', '0', '0', 'admin', '2025-09-24 22:11:35', 'admin', '2025-09-26 18:29:55');
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '长沙分公司', 2, '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2025-09-24 22:11:35', '', NULL);
INSERT INTO `sys_dept` VALUES (103, 101, '0,100,101', '研发部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2025-09-24 22:11:35', '', NULL);
INSERT INTO `sys_dept` VALUES (104, 101, '0,100,101', '市场部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2025-09-24 22:11:35', '', NULL);
INSERT INTO `sys_dept` VALUES (105, 101, '0,100,101', '测试部门', 3, '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2025-09-24 22:11:35', '', NULL);
INSERT INTO `sys_dept` VALUES (106, 101, '0,100,101', '财务部门', 4, '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2025-09-24 22:11:35', '', NULL);
INSERT INTO `sys_dept` VALUES (107, 101, '0,100,101', '运维部门', 5, '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2025-09-24 22:11:35', '', NULL);
INSERT INTO `sys_dept` VALUES (108, 102, '0,100,102', '市场部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2025-09-24 22:11:35', '', NULL);
INSERT INTO `sys_dept` VALUES (109, 102, '0,100,102', '财务部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '2', 'admin', '2025-09-24 22:11:35', '', NULL);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 165 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '1', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-09-24 22:11:39', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-09-24 22:11:39', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-09-24 22:11:39', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2025-09-24 22:11:39', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2025-09-24 22:11:39', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (100, 1, '失地居民补贴', '1', 'subsidy_type', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '失地居民补贴');
INSERT INTO `sys_dict_data` VALUES (101, 2, '被征地居民补贴', '2', 'subsidy_type', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '被征地居民补贴');
INSERT INTO `sys_dict_data` VALUES (102, 3, '拆迁居民补贴', '3', 'subsidy_type', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '拆迁居民补贴');
INSERT INTO `sys_dict_data` VALUES (103, 4, '村干部补贴', '4', 'subsidy_type', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '村干部补贴');
INSERT INTO `sys_dict_data` VALUES (104, 1, '未发放', '0', 'distribution_status', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '未发放');
INSERT INTO `sys_dict_data` VALUES (105, 2, '待审核', '1', 'distribution_status', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '待审核');
INSERT INTO `sys_dict_data` VALUES (106, 3, '待发放', '2', 'distribution_status', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '待发放');
INSERT INTO `sys_dict_data` VALUES (107, 4, '已驳回', '3', 'distribution_status', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '已驳回');
INSERT INTO `sys_dict_data` VALUES (108, 5, '已发放', '4', 'distribution_status', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '已发放');
INSERT INTO `sys_dict_data` VALUES (109, 1, '提交审核', '1', 'distribution_operation_type', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '提交审核');
INSERT INTO `sys_dict_data` VALUES (110, 2, '审核通过', '2', 'distribution_operation_type', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '审核通过');
INSERT INTO `sys_dict_data` VALUES (111, 3, '审核驳回', '3', 'distribution_operation_type', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '审核驳回');
INSERT INTO `sys_dict_data` VALUES (112, 4, '发放', '4', 'distribution_operation_type', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '发放');
INSERT INTO `sys_dict_data` VALUES (113, 5, '重新提交', '5', 'distribution_operation_type', NULL, NULL, 'N', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '重新提交');
INSERT INTO `sys_dict_data` VALUES (114, 1, '失地居民补贴', '1', 'subsidy_type', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:13', '', NULL, '失地居民补贴');
INSERT INTO `sys_dict_data` VALUES (115, 2, '被征地居民补贴', '2', 'subsidy_type', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:13', '', NULL, '被征地居民补贴');
INSERT INTO `sys_dict_data` VALUES (116, 3, '拆迁居民补贴', '3', 'subsidy_type', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:13', '', NULL, '拆迁居民补贴');
INSERT INTO `sys_dict_data` VALUES (117, 4, '村干部补贴', '4', 'subsidy_type', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:13', '', NULL, '村干部补贴');
INSERT INTO `sys_dict_data` VALUES (118, 1, '未发放', '0', 'distribution_status', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:14', '', NULL, '未发放');
INSERT INTO `sys_dict_data` VALUES (119, 2, '待审核', '1', 'distribution_status', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:14', '', NULL, '待审核');
INSERT INTO `sys_dict_data` VALUES (120, 3, '待发放', '2', 'distribution_status', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:14', '', NULL, '待发放');
INSERT INTO `sys_dict_data` VALUES (121, 4, '已驳回', '3', 'distribution_status', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:14', '', NULL, '已驳回');
INSERT INTO `sys_dict_data` VALUES (122, 5, '已发放', '4', 'distribution_status', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:14', '', NULL, '已发放');
INSERT INTO `sys_dict_data` VALUES (123, 1, '提交审核', '1', 'distribution_operation_type', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:14', '', NULL, '提交审核');
INSERT INTO `sys_dict_data` VALUES (124, 2, '审核通过', '2', 'distribution_operation_type', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:14', '', NULL, '审核通过');
INSERT INTO `sys_dict_data` VALUES (125, 3, '审核驳回', '3', 'distribution_operation_type', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:14', '', NULL, '审核驳回');
INSERT INTO `sys_dict_data` VALUES (126, 4, '发放', '4', 'distribution_operation_type', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:14', '', NULL, '发放');
INSERT INTO `sys_dict_data` VALUES (127, 5, '重新提交', '5', 'distribution_operation_type', NULL, NULL, 'N', '0', 'admin', '2025-10-18 21:32:14', '', NULL, '重新提交');
INSERT INTO `sys_dict_data` VALUES (128, 1, '失地居民', 'land_loss', 'shebao_subsidy_type', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '失地居民养老补贴');
INSERT INTO `sys_dict_data` VALUES (129, 2, '被征地居民', 'expropriatee', 'shebao_subsidy_type', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '被征地居民养老补贴');
INSERT INTO `sys_dict_data` VALUES (130, 3, '拆迁居民', 'demolition', 'shebao_subsidy_type', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '拆迁居民养老补贴');
INSERT INTO `sys_dict_data` VALUES (131, 4, '村干部', 'village_official', 'shebao_subsidy_type', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '村干部养老补贴');
INSERT INTO `sys_dict_data` VALUES (132, 5, '教师补贴', 'teacher', 'shebao_subsidy_type', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '教师养老补贴');
INSERT INTO `sys_dict_data` VALUES (133, 1, '草稿', 'draft', 'approval_status', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '草稿状态');
INSERT INTO `sys_dict_data` VALUES (134, 2, '待复核', 'pending_review', 'approval_status', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '待复核状态');
INSERT INTO `sys_dict_data` VALUES (135, 3, '待审批', 'pending_approve', 'approval_status', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '待审批状态');
INSERT INTO `sys_dict_data` VALUES (136, 4, '待财务', 'pending_finance', 'approval_status', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '待财务处理状态');
INSERT INTO `sys_dict_data` VALUES (137, 5, '已提交银行', 'submitted_bank', 'approval_status', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '已提交银行状态');
INSERT INTO `sys_dict_data` VALUES (138, 6, '已发放', 'distributed', 'approval_status', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '已发放状态');
INSERT INTO `sys_dict_data` VALUES (139, 7, '已驳回', 'rejected', 'approval_status', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '已驳回状态');
INSERT INTO `sys_dict_data` VALUES (140, 1, '首次发放', 'first', 'batch_type', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '首次发放批次');
INSERT INTO `sys_dict_data` VALUES (141, 2, '二次发放', 'second', 'batch_type', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '二次发放批次');
INSERT INTO `sys_dict_data` VALUES (142, 3, '三次发放', 'third', 'batch_type', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '三次发放批次');
INSERT INTO `sys_dict_data` VALUES (143, 1, '死亡', 'death', 'suspension_reason', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '待遇人死亡');
INSERT INTO `sys_dict_data` VALUES (144, 2, '未认证', 'uncertified', 'suspension_reason', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '未完成年度认证');
INSERT INTO `sys_dict_data` VALUES (145, 3, '服刑', 'imprisonment', 'suspension_reason', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '正在服刑');
INSERT INTO `sys_dict_data` VALUES (146, 4, '失踪', 'missing', 'suspension_reason', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '人员失踪');
INSERT INTO `sys_dict_data` VALUES (147, 5, '其他', 'other', 'suspension_reason', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '其他原因');
INSERT INTO `sys_dict_data` VALUES (148, 1, '未认证', 'uncertified', 'certification_status', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '未完成年度认证');
INSERT INTO `sys_dict_data` VALUES (149, 2, '已认证(4月)', 'april', 'certification_status', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '已完成4月认证');
INSERT INTO `sys_dict_data` VALUES (150, 3, '已认证(10月)', 'october', 'certification_status', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '已完成10月认证');
INSERT INTO `sys_dict_data` VALUES (151, 1, '人员登记', 'person_register', 'business_type', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '人员登记审批');
INSERT INTO `sys_dict_data` VALUES (152, 2, '待遇核定', 'benefit_determination', 'business_type', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '待遇核定审批');
INSERT INTO `sys_dict_data` VALUES (153, 3, '支付计划', 'payment_plan', 'business_type', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '支付计划审批');
INSERT INTO `sys_dict_data` VALUES (154, 4, '信息修改', 'info_modify', 'business_type', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '信息修改审批');
INSERT INTO `sys_dict_data` VALUES (155, 1, '提交', 'submit', 'approval_operation', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '提交审核');
INSERT INTO `sys_dict_data` VALUES (156, 2, '复核', 'review', 'approval_operation', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '复核操作');
INSERT INTO `sys_dict_data` VALUES (157, 3, '审批', 'approve', 'approval_operation', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '审批操作');
INSERT INTO `sys_dict_data` VALUES (158, 4, '驳回', 'reject', 'approval_operation', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '驳回操作');
INSERT INTO `sys_dict_data` VALUES (159, 5, '撤销', 'cancel', 'approval_operation', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '撤销操作');
INSERT INTO `sys_dict_data` VALUES (160, 1, '账号不存在', 'account_not_exist', 'fail_reason', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '银行账户不存在');
INSERT INTO `sys_dict_data` VALUES (161, 2, '余额不足', 'insufficient_balance', 'fail_reason', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '账户余额不足');
INSERT INTO `sys_dict_data` VALUES (162, 3, '信息错误', 'info_error', 'fail_reason', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '账户信息错误');
INSERT INTO `sys_dict_data` VALUES (163, 4, '银行系统错误', 'bank_error', 'fail_reason', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '银行系统错误');
INSERT INTO `sys_dict_data` VALUES (164, 5, '其他', 'other', 'fail_reason', NULL, NULL, 'N', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '其他原因');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 208 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2025-09-24 22:11:38', '', NULL, '登录状态列表');
INSERT INTO `sys_dict_type` VALUES (100, '补贴类型', 'subsidy_type', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '补贴类型列表');
INSERT INTO `sys_dict_type` VALUES (101, '发放状态', 'distribution_status', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '补贴发放状态列表');
INSERT INTO `sys_dict_type` VALUES (102, '发放操作类型', 'distribution_operation_type', '0', 'admin', '2025-10-15 21:31:43', '', NULL, '发放操作类型列表');
INSERT INTO `sys_dict_type` VALUES (200, '补贴类型', 'shebao_subsidy_type', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '各类养老补贴类型');
INSERT INTO `sys_dict_type` VALUES (201, '审批状态', 'approval_status', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '审批流程状态');
INSERT INTO `sys_dict_type` VALUES (202, '批次类型', 'batch_type', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '发放批次类型');
INSERT INTO `sys_dict_type` VALUES (203, '暂停原因', 'suspension_reason', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '待遇暂停原因');
INSERT INTO `sys_dict_type` VALUES (204, '认证状态', 'certification_status', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '待遇认证状态');
INSERT INTO `sys_dict_type` VALUES (205, '业务类型', 'business_type', '0', 'admin', '2026-01-19 10:08:27', '', NULL, '审批流程业务类型');
INSERT INTO `sys_dict_type` VALUES (206, '审批操作', 'approval_operation', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '审批流程操作类型');
INSERT INTO `sys_dict_type` VALUES (207, '失败原因', 'fail_reason', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '银行发放失败原因');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '定时任务调度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2025-09-24 22:11:39', '', NULL, '');
INSERT INTO `sys_job` VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2025-09-24 22:11:39', '', NULL, '');
INSERT INTO `sys_job` VALUES (3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2025-09-24 22:11:39', '', NULL, '');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '异常信息',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE,
  INDEX `idx_sys_logininfor_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_logininfor_lt`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 235 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (100, 'admin', '127.0.0.1', '内网IP', 'Safari', 'Mac OS X', '0', '登录成功', '2025-09-24 22:41:20');
INSERT INTO `sys_logininfor` VALUES (101, 'admin', '127.0.0.1', '内网IP', 'Safari', 'Mac OS X', '1', '验证码已失效', '2025-09-26 17:26:01');
INSERT INTO `sys_logininfor` VALUES (102, 'admin', '127.0.0.1', '内网IP', 'Safari', 'Mac OS X', '0', '登录成功', '2025-09-26 17:26:08');
INSERT INTO `sys_logininfor` VALUES (103, 'admin', '127.0.0.1', '内网IP', 'Safari', 'Mac OS X', '0', '退出成功', '2025-09-26 17:35:41');
INSERT INTO `sys_logininfor` VALUES (104, 'admin', '127.0.0.1', '内网IP', 'Safari', 'Mac OS X', '0', '登录成功', '2025-09-26 17:35:44');
INSERT INTO `sys_logininfor` VALUES (105, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-09-26 18:26:31');
INSERT INTO `sys_logininfor` VALUES (106, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-09-27 10:36:24');
INSERT INTO `sys_logininfor` VALUES (107, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-09-27 11:46:01');
INSERT INTO `sys_logininfor` VALUES (108, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-09-27 16:01:41');
INSERT INTO `sys_logininfor` VALUES (109, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-09-27 20:42:47');
INSERT INTO `sys_logininfor` VALUES (110, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-09-28 01:42:49');
INSERT INTO `sys_logininfor` VALUES (111, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-09-28 12:15:16');
INSERT INTO `sys_logininfor` VALUES (112, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-09-28 21:42:56');
INSERT INTO `sys_logininfor` VALUES (113, 'admin', '127.0.0.1', '内网IP', 'Safari', 'Mac OS X', '0', '登录成功', '2025-09-29 00:00:16');
INSERT INTO `sys_logininfor` VALUES (114, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-09-29 10:38:29');
INSERT INTO `sys_logininfor` VALUES (115, 'admin', '222.128.127.244', 'XX XX', 'Chrome 12', 'Mac OS X', '0', '登录成功', '2025-09-29 10:53:52');
INSERT INTO `sys_logininfor` VALUES (116, 'admin', '223.104.3.162', 'XX XX', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-09-29 11:04:30');
INSERT INTO `sys_logininfor` VALUES (117, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-09-29 11:21:35');
INSERT INTO `sys_logininfor` VALUES (118, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-09-29 20:55:43');
INSERT INTO `sys_logininfor` VALUES (119, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '1', '用户不存在/密码错误', '2025-10-10 10:20:22');
INSERT INTO `sys_logininfor` VALUES (120, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-10 10:20:26');
INSERT INTO `sys_logininfor` VALUES (121, 'admin', '101.29.186.209', 'XX XX', 'Chrome 10', 'Windows 10', '0', '登录成功', '2025-10-10 10:27:34');
INSERT INTO `sys_logininfor` VALUES (122, 'admin', '111.55.150.107', 'XX XX', 'Chrome Mobile', 'Android 1.x', '0', '登录成功', '2025-10-10 10:28:45');
INSERT INTO `sys_logininfor` VALUES (123, 'admin', '27.128.48.201', 'XX XX', 'Chrome Mobile', 'Android 1.x', '0', '登录成功', '2025-10-10 10:31:31');
INSERT INTO `sys_logininfor` VALUES (124, 'admin', '222.128.127.244', 'XX XX', 'Safari', 'Mac OS X', '0', '登录成功', '2025-10-10 10:41:15');
INSERT INTO `sys_logininfor` VALUES (125, 'admin', '60.10.47.214', 'XX XX', 'Chrome Mobile', 'Android 1.x', '0', '登录成功', '2025-10-10 10:41:36');
INSERT INTO `sys_logininfor` VALUES (126, 'admin', '60.10.47.214', 'XX XX', 'Apple WebKit', 'Mac OS X (iPhone)', '0', '登录成功', '2025-10-10 10:53:16');
INSERT INTO `sys_logininfor` VALUES (127, 'admin', '222.128.127.244', 'XX XX', 'Chrome 13', 'Mac OS X', '0', '登录成功', '2025-10-10 10:54:34');
INSERT INTO `sys_logininfor` VALUES (128, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-10 10:55:58');
INSERT INTO `sys_logininfor` VALUES (129, 'admin', '117.136.47.166', 'XX XX', 'Apple WebKit', 'Mac OS X (iPhone)', '0', '登录成功', '2025-10-10 10:59:59');
INSERT INTO `sys_logininfor` VALUES (130, 'admin', '223.160.137.16', 'XX XX', 'Chrome Mobile', 'Android 1.x', '0', '登录成功', '2025-10-10 11:00:01');
INSERT INTO `sys_logininfor` VALUES (131, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-10 11:00:52');
INSERT INTO `sys_logininfor` VALUES (132, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-10 11:01:57');
INSERT INTO `sys_logininfor` VALUES (133, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-10 12:19:01');
INSERT INTO `sys_logininfor` VALUES (134, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-10 16:09:09');
INSERT INTO `sys_logininfor` VALUES (135, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-10 16:17:13');
INSERT INTO `sys_logininfor` VALUES (136, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-11 10:30:12');
INSERT INTO `sys_logininfor` VALUES (137, 'admin', '60.10.59.193', 'XX XX', 'Chrome 12', 'Windows 10', '0', '登录成功', '2025-10-11 10:36:17');
INSERT INTO `sys_logininfor` VALUES (138, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-11 14:25:38');
INSERT INTO `sys_logininfor` VALUES (139, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-11 16:11:43');
INSERT INTO `sys_logininfor` VALUES (140, 'admin', '60.10.59.193', 'XX XX', 'Chrome 12', 'Windows 10', '0', '登录成功', '2025-10-11 16:20:15');
INSERT INTO `sys_logininfor` VALUES (141, 'admin', '60.10.59.193', 'XX XX', 'Chrome 12', 'Windows 10', '0', '登录成功', '2025-10-11 17:10:52');
INSERT INTO `sys_logininfor` VALUES (142, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-13 09:44:07');
INSERT INTO `sys_logininfor` VALUES (143, 'admin', '60.10.59.193', 'XX XX', 'Chrome 12', 'Windows 10', '0', '登录成功', '2025-10-13 10:00:56');
INSERT INTO `sys_logininfor` VALUES (144, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-13 15:33:31');
INSERT INTO `sys_logininfor` VALUES (145, 'admin', '120.244.162.197', 'XX XX', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-10-14 10:18:02');
INSERT INTO `sys_logininfor` VALUES (146, 'admin', '222.128.127.244', 'XX XX', 'Safari', 'Mac OS X', '0', '登录成功', '2025-10-14 10:47:45');
INSERT INTO `sys_logininfor` VALUES (147, 'admin', '222.128.127.244', 'XX XX', 'Safari', 'Mac OS X', '0', '退出成功', '2025-10-14 10:50:42');
INSERT INTO `sys_logininfor` VALUES (148, 'admin', '222.128.127.244', 'XX XX', 'Safari', 'Mac OS X', '0', '登录成功', '2025-10-14 10:51:08');
INSERT INTO `sys_logininfor` VALUES (149, 'admin', '222.128.127.244', 'XX XX', 'Safari', 'Mac OS X', '0', '退出成功', '2025-10-14 10:52:25');
INSERT INTO `sys_logininfor` VALUES (150, 'admin', '222.128.127.244', 'XX XX', 'Safari', 'Mac OS X', '0', '登录成功', '2025-10-14 10:52:34');
INSERT INTO `sys_logininfor` VALUES (151, 'admin', '27.128.21.83', 'XX XX', 'Apple WebKit', 'Mac OS X (iPhone)', '1', '用户不存在/密码错误', '2025-10-14 10:56:06');
INSERT INTO `sys_logininfor` VALUES (152, 'admin', '27.128.21.83', 'XX XX', 'Apple WebKit', 'Mac OS X (iPhone)', '1', '用户不存在/密码错误', '2025-10-14 10:56:15');
INSERT INTO `sys_logininfor` VALUES (153, 'admin', '27.128.21.83', 'XX XX', 'Apple WebKit', 'Mac OS X (iPhone)', '1', '用户不存在/密码错误', '2025-10-14 10:59:04');
INSERT INTO `sys_logininfor` VALUES (154, 'admin', '27.128.21.83', 'XX XX', 'Apple WebKit', 'Mac OS X (iPhone)', '0', '登录成功', '2025-10-14 11:01:56');
INSERT INTO `sys_logininfor` VALUES (155, 'admin', '60.10.47.214', 'XX XX', 'Apple WebKit', 'Mac OS X (iPhone)', '0', '登录成功', '2025-10-14 11:52:05');
INSERT INTO `sys_logininfor` VALUES (156, 'admin', '60.10.59.193', 'XX XX', 'Chrome Mobile', 'Android 1.x', '1', '用户不存在/密码错误', '2025-10-15 09:53:49');
INSERT INTO `sys_logininfor` VALUES (157, 'admin', '60.10.59.193', 'XX XX', 'Chrome Mobile', 'Android 1.x', '0', '登录成功', '2025-10-15 09:54:09');
INSERT INTO `sys_logininfor` VALUES (158, 'admin', '60.10.47.214', 'XX XX', 'Apple WebKit', 'Mac OS X (iPhone)', '0', '登录成功', '2025-10-15 09:55:48');
INSERT INTO `sys_logininfor` VALUES (159, 'admin', '60.10.47.214', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2025-10-15 15:12:56');
INSERT INTO `sys_logininfor` VALUES (160, 'admin', '60.10.47.214', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-15 15:16:11');
INSERT INTO `sys_logininfor` VALUES (161, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '1', '用户不存在/密码错误', '2025-10-15 21:42:01');
INSERT INTO `sys_logininfor` VALUES (162, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-15 21:44:24');
INSERT INTO `sys_logininfor` VALUES (163, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-16 10:43:58');
INSERT INTO `sys_logininfor` VALUES (164, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-16 14:14:07');
INSERT INTO `sys_logininfor` VALUES (165, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-16 15:34:38');
INSERT INTO `sys_logininfor` VALUES (166, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-18 21:08:05');
INSERT INTO `sys_logininfor` VALUES (167, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-19 00:26:07');
INSERT INTO `sys_logininfor` VALUES (168, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-19 10:36:54');
INSERT INTO `sys_logininfor` VALUES (169, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-19 12:38:41');
INSERT INTO `sys_logininfor` VALUES (170, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-19 13:48:54');
INSERT INTO `sys_logininfor` VALUES (171, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-19 15:13:26');
INSERT INTO `sys_logininfor` VALUES (172, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-19 17:15:27');
INSERT INTO `sys_logininfor` VALUES (173, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-19 18:27:47');
INSERT INTO `sys_logininfor` VALUES (174, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-19 20:25:41');
INSERT INTO `sys_logininfor` VALUES (175, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-20 00:25:09');
INSERT INTO `sys_logininfor` VALUES (176, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2025-10-20 01:04:28');
INSERT INTO `sys_logininfor` VALUES (177, 'admin', '36.98.90.0', 'XX XX', 'Chrome 13', 'Android 1.x', '0', '登录成功', '2025-10-20 08:59:56');
INSERT INTO `sys_logininfor` VALUES (178, 'admin', '60.10.47.214', 'XX XX', 'Apple WebKit', 'Mac OS X (iPhone)', '0', '登录成功', '2025-10-20 09:40:57');
INSERT INTO `sys_logininfor` VALUES (179, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-20 11:00:46');
INSERT INTO `sys_logininfor` VALUES (180, 'admin', '60.10.59.193', 'XX XX', 'Chrome 12', 'Windows 10', '0', '登录成功', '2025-10-20 13:51:35');
INSERT INTO `sys_logininfor` VALUES (181, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-20 14:23:08');
INSERT INTO `sys_logininfor` VALUES (182, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2025-10-20 16:19:49');
INSERT INTO `sys_logininfor` VALUES (183, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2025-10-20 16:49:03');
INSERT INTO `sys_logininfor` VALUES (184, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-20 16:50:26');
INSERT INTO `sys_logininfor` VALUES (185, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-21 15:13:47');
INSERT INTO `sys_logininfor` VALUES (186, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-10-22 15:00:27');
INSERT INTO `sys_logininfor` VALUES (187, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 09:06:30');
INSERT INTO `sys_logininfor` VALUES (188, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 21:28:49');
INSERT INTO `sys_logininfor` VALUES (189, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 22:01:26');
INSERT INTO `sys_logininfor` VALUES (190, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 22:57:19');
INSERT INTO `sys_logininfor` VALUES (191, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-20 02:09:22');
INSERT INTO `sys_logininfor` VALUES (192, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-20 02:44:09');
INSERT INTO `sys_logininfor` VALUES (193, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-24 22:59:37');
INSERT INTO `sys_logininfor` VALUES (194, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-24 23:39:02');
INSERT INTO `sys_logininfor` VALUES (195, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 01:40:08');
INSERT INTO `sys_logininfor` VALUES (196, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 14:30:36');
INSERT INTO `sys_logininfor` VALUES (197, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '1', '用户不存在/密码错误', '2026-01-28 11:55:17');
INSERT INTO `sys_logininfor` VALUES (198, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2026-01-28 11:55:27');
INSERT INTO `sys_logininfor` VALUES (199, 'admin', '183.241.78.6', 'XX XX', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-28 11:57:39');
INSERT INTO `sys_logininfor` VALUES (200, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '1', '用户不存在/密码错误', '2026-01-28 13:07:06');
INSERT INTO `sys_logininfor` VALUES (201, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2026-01-28 13:07:16');
INSERT INTO `sys_logininfor` VALUES (202, 'admin', '183.241.78.6', 'XX XX', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-28 13:20:01');
INSERT INTO `sys_logininfor` VALUES (203, 'admin', '183.241.78.6', 'XX XX', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-28 18:40:03');
INSERT INTO `sys_logininfor` VALUES (204, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-28 22:09:59');
INSERT INTO `sys_logininfor` VALUES (205, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2026-01-28 22:16:48');
INSERT INTO `sys_logininfor` VALUES (206, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2026-01-28 22:34:44');
INSERT INTO `sys_logininfor` VALUES (207, 'admin', '183.241.78.6', 'XX XX', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-29 01:04:59');
INSERT INTO `sys_logininfor` VALUES (208, 'admin', '60.10.59.193', 'XX XX', 'Chrome 12', 'Windows 10', '0', '登录成功', '2026-01-29 13:32:34');
INSERT INTO `sys_logininfor` VALUES (209, 'admin', '60.10.59.193', 'XX XX', 'Chrome 12', 'Windows 10', '0', '登录成功', '2026-01-29 14:59:44');
INSERT INTO `sys_logininfor` VALUES (210, 'admin', '60.175.255.125', 'XX XX', 'Firefox 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-29 16:44:18');
INSERT INTO `sys_logininfor` VALUES (211, 'admin', '60.10.59.193', 'XX XX', 'Chrome 12', 'Windows 10', '0', '登录成功', '2026-01-30 09:17:01');
INSERT INTO `sys_logininfor` VALUES (212, 'admin', '60.10.59.193', 'XX XX', 'Chrome 12', 'Windows 10', '0', '登录成功', '2026-02-02 11:29:47');
INSERT INTO `sys_logininfor` VALUES (213, 'admin', '60.10.59.193', 'XX XX', 'Chrome 12', 'Windows 10', '0', '登录成功', '2026-02-03 15:04:18');
INSERT INTO `sys_logininfor` VALUES (214, 'admin', '60.10.59.193', 'XX XX', 'Chrome 12', 'Windows 10', '0', '登录成功', '2026-02-03 16:44:33');
INSERT INTO `sys_logininfor` VALUES (215, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-02-03 18:38:06');
INSERT INTO `sys_logininfor` VALUES (216, 'admin', '111.55.210.93', 'XX XX', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-02-03 18:39:04');
INSERT INTO `sys_logininfor` VALUES (217, 'admin', '120.229.49.22', 'XX XX', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-02-04 09:57:06');
INSERT INTO `sys_logininfor` VALUES (218, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2026-02-04 10:28:27');
INSERT INTO `sys_logininfor` VALUES (219, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2026-02-04 10:28:36');
INSERT INTO `sys_logininfor` VALUES (220, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2026-02-04 10:29:16');
INSERT INTO `sys_logininfor` VALUES (221, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2026-02-25 15:12:17');
INSERT INTO `sys_logininfor` VALUES (222, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2026-02-25 15:12:19');
INSERT INTO `sys_logininfor` VALUES (223, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2026-02-25 15:12:30');
INSERT INTO `sys_logininfor` VALUES (224, 'lihongshan', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2026-02-25 15:12:39');
INSERT INTO `sys_logininfor` VALUES (225, 'admin', '222.128.127.244', 'XX XX', 'Chrome 14', 'Mac OS X', '0', '登录成功', '2026-02-25 15:58:06');
INSERT INTO `sys_logininfor` VALUES (226, 'admin', '39.144.79.134', 'XX XX', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-02-25 15:59:27');
INSERT INTO `sys_logininfor` VALUES (227, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2026-02-25 16:01:24');
INSERT INTO `sys_logininfor` VALUES (228, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2026-02-25 16:01:26');
INSERT INTO `sys_logininfor` VALUES (229, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2026-02-25 16:01:54');
INSERT INTO `sys_logininfor` VALUES (230, 'admin', '120.244.162.77', 'XX XX', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-02-25 20:24:45');
INSERT INTO `sys_logininfor` VALUES (231, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2026-02-26 14:30:55');
INSERT INTO `sys_logininfor` VALUES (232, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2026-02-26 14:30:58');
INSERT INTO `sys_logininfor` VALUES (233, 'admin', '60.10.59.193', 'XX XX', 'Chrome 13', 'Windows 10', '0', '登录成功', '2026-02-26 14:31:13');
INSERT INTO `sys_logininfor` VALUES (234, 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-03-02 23:12:00');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '路由名称',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2115 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 101, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2025-09-24 22:11:36', 'admin', '2025-09-27 10:44:48', '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 201, 'monitor', NULL, '', '', 1, 0, 'M', '1', '1', '', 'monitor', 'admin', '2025-09-24 22:11:36', 'admin', '2025-09-29 00:14:30', '系统监控目录');
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 202, 'tool', NULL, '', '', 1, 0, 'M', '1', '1', '', 'tool', 'admin', '2025-09-24 22:11:36', 'admin', '2025-09-29 00:13:11', '系统工具目录');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2025-09-24 22:11:36', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2025-09-24 22:11:36', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2025-09-24 22:11:36', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2025-09-24 22:11:36', '', NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', '', 1, 0, 'C', '1', '1', 'system:post:list', 'post', 'admin', '2025-09-24 22:11:36', 'admin', '2025-09-29 00:15:17', '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2025-09-24 22:11:36', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2025-09-24 22:11:36', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', '', 1, 0, 'C', '1', '1', 'system:notice:list', 'message', 'admin', '2025-09-24 22:11:36', 'admin', '2025-09-29 00:15:08', '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', '2025-09-24 22:11:36', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2025-09-24 22:11:36', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2025-09-24 22:11:36', '', NULL, '定时任务菜单');
INSERT INTO `sys_menu` VALUES (111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2025-09-24 22:11:36', '', NULL, '数据监控菜单');
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2025-09-24 22:11:36', '', NULL, '服务监控菜单');
INSERT INTO `sys_menu` VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2025-09-24 22:11:36', '', NULL, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES (114, '缓存列表', 2, 6, 'cacheList', 'monitor/cache/list', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis-list', 'admin', '2025-09-24 22:11:36', '', NULL, '缓存列表菜单');
INSERT INTO `sys_menu` VALUES (115, '表单构建', 3, 1, 'build', 'tool/build/index', '', '', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2025-09-24 22:11:36', '', NULL, '表单构建菜单');
INSERT INTO `sys_menu` VALUES (116, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2025-09-24 22:11:36', '', NULL, '代码生成菜单');
INSERT INTO `sys_menu` VALUES (117, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2025-09-24 22:11:36', '', NULL, '系统接口菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2025-09-24 22:11:36', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2025-09-24 22:11:36', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1000, '用户查询', 100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1001, '用户新增', 100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户修改', 100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户删除', 100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户导出', 100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导入', 100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '重置密码', 100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '角色查询', 101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色新增', 101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色修改', 101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色删除', 101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色导出', 101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '菜单查询', 102, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单新增', 102, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单修改', 102, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单删除', 102, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '部门查询', 103, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, '部门新增', 103, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, '部门修改', 103, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, '部门删除', 103, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, '岗位查询', 104, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, '岗位新增', 104, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, '岗位修改', 104, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '岗位删除', 104, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '岗位导出', 104, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '字典查询', 105, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典新增', 105, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典修改', 105, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典删除', 105, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典导出', 105, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '参数查询', 106, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数新增', 106, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数修改', 106, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数删除', 106, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数导出', 106, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '公告查询', 107, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告新增', 107, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告修改', 107, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告删除', 107, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '操作查询', 500, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作删除', 500, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '日志导出', 500, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '登录查询', 501, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录删除', 501, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '日志导出', 501, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '账户解锁', 501, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '批量强退', 109, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1049, '任务查询', 110, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1050, '任务新增', 110, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1051, '任务修改', 110, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1052, '任务删除', 110, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1053, '状态修改', 110, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1054, '任务导出', 110, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1055, '生成查询', 116, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1056, '生成修改', 116, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1057, '生成删除', 116, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1058, '导入代码', 116, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1059, '预览代码', 116, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1060, '生成代码', 116, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2025-09-24 22:11:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2000, '基础数据', 0, 1, 'base', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'dict', 'admin', '2025-09-27 10:41:46', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2002, '行政区划', 2000, 1, 'division', 'shebao/administrativeDivision/index', NULL, '行政区划', 1, 0, 'C', '1', '1', 'shebao:administrativeDivision:list', 'tree-table', 'admin', '2025-09-27 20:46:54', 'admin', '2025-10-19 22:36:45', '');
INSERT INTO `sys_menu` VALUES (2004, '人员信息', 2000, 0, 'baseinfo', 'shebao/subsidyPerson/index', NULL, '人员信息', 1, 0, 'C', '0', '0', '', 'people', 'admin', '2025-09-28 01:46:52', 'admin', '2025-09-29 11:30:50', '');
INSERT INTO `sys_menu` VALUES (2009, '补贴发放', 0, 4, 'assign', NULL, NULL, '', 1, 0, 'M', '0', '0', '', 'form', 'admin', '2025-09-29 11:15:25', 'admin', '2025-09-29 11:19:05', '');
INSERT INTO `sys_menu` VALUES (2011, '统计查询', 2009, 2, 'residentQuery', 'shebao/residentQuery/index', NULL, '统计查询', 1, 0, 'C', '0', '0', '', 'search', 'admin', '2025-09-29 11:18:05', 'admin', '2025-10-20 00:27:29', '');
INSERT INTO `sys_menu` VALUES (2012, '补贴设置', 2000, 3, 'settings', 'shebao/config/index', NULL, '补贴设置', 1, 0, 'C', '0', '0', '', 'dict', 'admin', '2025-09-29 20:59:55', 'admin', '2025-09-29 21:01:21', '');
INSERT INTO `sys_menu` VALUES (2013, '补贴发放记录', 2009, 1, 'subsidyDistribution', 'shebao/subsidyDistribution/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:subsidyDistribution:list', 'money', 'admin', '2025-10-15 21:31:43', 'admin', '2025-10-18 21:26:53', '补贴发放记录菜单');
INSERT INTO `sys_menu` VALUES (2014, '补贴发放记录查询', 2013, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:query', '#', 'admin', '2025-10-15 21:31:43', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2015, '补贴发放记录新增', 2013, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:add', '#', 'admin', '2025-10-15 21:31:43', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2016, '补贴发放记录删除', 2013, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:remove', '#', 'admin', '2025-10-15 21:31:43', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2017, '补贴发放审核', 2013, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:approve', '#', 'admin', '2025-10-15 21:31:43', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2018, '补贴发放驳回', 2013, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:reject', '#', 'admin', '2025-10-15 21:31:43', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2019, '补贴发放', 2013, 6, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:distribute', '#', 'admin', '2025-10-15 21:31:43', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2020, '补贴重新提交', 2013, 7, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:resubmit', '#', 'admin', '2025-10-15 21:31:43', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2021, '补贴发放记录导出', 2013, 8, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'shebao:subsidyDistribution:export', '#', 'admin', '2025-10-15 21:31:43', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2031, '街道办', 2000, 4, 'streetOffice', 'shebao/streetOffice/index', NULL, '街道办', 1, 0, 'C', '0', '0', 'shebao:streetOffice:list', 'tree-table', 'admin', '2025-10-19 10:38:24', 'admin', '2025-10-19 23:17:16', '');
INSERT INTO `sys_menu` VALUES (2032, '村委会', 2000, 6, 'villageCommittee', 'shebao/villageCommittee/index', NULL, '村委会', 1, 0, 'C', '0', '0', 'shebao:villageCommittee:list', 'tree-table', 'admin', '2025-10-19 10:39:40', 'admin', '2025-10-19 22:37:17', '');
INSERT INTO `sys_menu` VALUES (2078, '人员信息管理', 0, 1, 'person', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'peoples', 'admin', '2026-01-19 11:15:27', '', NULL, '人员信息管理菜单');
INSERT INTO `sys_menu` VALUES (2079, '待遇核定管理', 0, 2, 'benefit', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'documentation', 'admin', '2026-01-19 11:15:27', '', NULL, '待遇核定管理菜单');
INSERT INTO `sys_menu` VALUES (2080, '待遇管理', 0, 3, 'management', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'edit', 'admin', '2026-01-19 11:15:27', '', NULL, '待遇管理菜单');
INSERT INTO `sys_menu` VALUES (2081, '支付结算', 0, 4, 'payment', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'money', 'admin', '2026-01-19 11:15:27', '', NULL, '支付结算菜单');
INSERT INTO `sys_menu` VALUES (2082, '财务管理', 0, 5, 'finance', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'guide', 'admin', '2026-01-19 11:15:27', '', NULL, '财务管理菜单');
INSERT INTO `sys_menu` VALUES (2083, '统计管理', 0, 6, 'audit', NULL, NULL, '', 1, 0, 'M', '0', '0', '', 'chart', 'admin', '2026-01-19 11:15:27', 'admin', '2026-01-24 23:05:39', '审计报表菜单');
INSERT INTO `sys_menu` VALUES (2084, '失地居民', 2078, 1, 'lost-land', 'shebao/landLossResident/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:person:landloss:list', '#', 'admin', '2026-01-19 11:15:27', 'admin', '2026-01-20 00:15:12', '失地居民登记菜单');
INSERT INTO `sys_menu` VALUES (2085, '被征地居民', 2078, 2, 'expropriatee', 'shebao/expropriateeSubsidy/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:person:expropriatee:list', '#', 'admin', '2026-01-19 11:15:27', 'admin', '2026-01-20 00:15:37', '被征地居民登记菜单');
INSERT INTO `sys_menu` VALUES (2086, '拆迁居民', 2078, 3, 'demolition', 'shebao/expropriateeSubsidy/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:person:demolition:list', '#', 'admin', '2026-01-19 11:15:27', 'admin', '2026-01-20 00:15:59', '拆迁居民登记菜单');
INSERT INTO `sys_menu` VALUES (2087, '村干部', 2078, 4, 'village-official', 'shebao/villageOfficial/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:person:official:list', '#', 'admin', '2026-01-19 11:15:27', 'admin', '2026-01-20 00:16:43', '村干部登记菜单');
INSERT INTO `sys_menu` VALUES (2088, '教龄补助', 2078, 5, 'teacher', 'shebao/person/teacher/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:person:teacher:list', '#', 'admin', '2026-01-19 11:15:28', 'admin', '2026-01-24 23:01:43', '教师补贴登记菜单');
INSERT INTO `sys_menu` VALUES (2089, '人员登记复核', 2078, 6, 'review', 'shebao/person/review/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:person:review:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '人员登记复核菜单');
INSERT INTO `sys_menu` VALUES (2090, '信息修改审批', 2078, 7, 'modify', 'shebao/person/modify/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:person:modify:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '信息修改审批菜单');
INSERT INTO `sys_menu` VALUES (2091, '预到龄发放通知', 2079, 1, 'notice', 'shebao/benefit/notice/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:benefit:notice:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '预到龄发放通知菜单');
INSERT INTO `sys_menu` VALUES (2092, '待遇核定', 2079, 2, 'determination', 'shebao/benefit/determination/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:benefit:determination:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '待遇核定菜单');
INSERT INTO `sys_menu` VALUES (2093, '核定审核', 2079, 3, 'review', 'shebao/benefit/review/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:benefit:review:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '核定审核菜单');
INSERT INTO `sys_menu` VALUES (2094, '发放信息修改', 2080, 1, 'modify', 'shebao/management/modify/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:benefit:modify:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '发放信息修改菜单');
INSERT INTO `sys_menu` VALUES (2095, '待遇暂停恢复', 2080, 2, 'suspension', 'shebao/management/suspension/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:benefit:suspension:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '待遇暂停恢复菜单');
INSERT INTO `sys_menu` VALUES (2096, '待遇认证', 2080, 3, 'certification', 'shebao/management/certification/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:benefit:certification:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '待遇认证菜单');
INSERT INTO `sys_menu` VALUES (2097, '支付计划生成', 2081, 1, 'plan', 'shebao/payment/plan/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:payment:plan:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '支付计划生成菜单');
INSERT INTO `sys_menu` VALUES (2098, '支付计划复核', 2081, 2, 'review', 'shebao/payment/review/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:payment:batch:review', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '支付计划复核菜单');
INSERT INTO `sys_menu` VALUES (2099, '支付计划审批', 2081, 3, 'approve', 'shebao/payment/approve/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:payment:batch:approve', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '支付计划审批菜单');
INSERT INTO `sys_menu` VALUES (2100, '上传财务系统', 2081, 4, 'upload', 'shebao/payment/upload/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:payment:batch:upload', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '上传财务系统菜单');
INSERT INTO `sys_menu` VALUES (2101, '批次管理', 2082, 1, 'batch', 'shebao/finance/batch/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:finance:batch:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '批次管理菜单');
INSERT INTO `sys_menu` VALUES (2102, '银行发放', 2082, 2, 'bank', 'shebao/finance/bank/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:finance:bank:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '银行发放菜单');
INSERT INTO `sys_menu` VALUES (2103, '失败处理', 2082, 3, 'failure', 'shebao/finance/failure/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:finance:failure:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '失败处理菜单');
INSERT INTO `sys_menu` VALUES (2104, '财务账户', 2082, 4, 'account', 'shebao/finance/account/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:finance:account:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '财务账户菜单');
INSERT INTO `sys_menu` VALUES (2105, '操作日志', 2083, 1, 'operlog', 'shebao/audit/operlog/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:audit:operlog:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (2106, '审批记录', 2083, 2, 'approval', 'shebao/audit/approval/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:audit:approval:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '审批记录菜单');
INSERT INTO `sys_menu` VALUES (2107, '发放明细表', 2083, 3, 'detail', 'shebao/audit/detail/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:audit:detail:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '发放明细表菜单');
INSERT INTO `sys_menu` VALUES (2108, '统计汇总', 2083, 4, 'statistics', 'shebao/audit/statistics/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:audit:statistics:list', '#', 'admin', '2026-01-19 11:15:28', '', NULL, '统计汇总菜单');
INSERT INTO `sys_menu` VALUES (2110, '人员注销登记', 2078, 8, 'cancel', 'shebao/person/cancel/index', NULL, '', 1, 0, 'C', '0', '0', 'shebao:person:cancel:list', '#', 'admin', '2026-01-25 02:10:29', '', NULL, '人员注销登记菜单');
INSERT INTO `sys_menu` VALUES (2111, '人员注销登记查询', NULL, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'shebao:person:cancel:query', '#', 'admin', '2026-01-25 02:13:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2112, '人员注销登记新增', NULL, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'shebao:person:cancel:add', '#', 'admin', '2026-01-25 02:13:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2113, '人员注销登记修改', NULL, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'shebao:person:cancel:edit', '#', 'admin', '2026-01-25 02:13:31', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2114, '人员注销登记删除', NULL, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'shebao:person:cancel:remove', '#', 'admin', '2026-01-25 02:13:31', '', NULL, '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, '温馨提醒：2018-07-01 新版本发布啦', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2025-09-24 22:11:39', 'admin', '2025-09-29 00:12:20', '管理员');
INSERT INTO `sys_notice` VALUES (2, '维护通知：2018-07-01 系统凌晨维护', '1', 0x3C703EE7BBB4E68AA4E58685E5AEB93C2F703E, '0', 'admin', '2025-09-24 22:11:39', 'admin', '2025-09-29 00:12:26', '管理员');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  INDEX `idx_sys_oper_log_bt`(`business_type` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_ot`(`oper_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 329 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (287, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-09-28 01:43:38\",\"icon\":\"peoples\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2003,\"menuName\":\"补贴居民\",\"menuType\":\"M\",\"orderNum\":3,\"params\":{},\"parentId\":0,\"path\":\"personold\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-19 23:09:36', 160);
INSERT INTO `sys_oper_log` VALUES (288, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/person/landloss/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2084,\"menuName\":\"失地居民\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2078,\"path\":\"landloss\",\"perms\":\"shebao:person:landloss:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-19 23:55:42', 203);
INSERT INTO `sys_oper_log` VALUES (289, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/person/expropriatee/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2085,\"menuName\":\"被征地居民\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2078,\"path\":\"expropriatee\",\"perms\":\"shebao:person:expropriatee:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-19 23:55:53', 181);
INSERT INTO `sys_oper_log` VALUES (290, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/person/demolition/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2086,\"menuName\":\"拆迁居民\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2078,\"path\":\"demolition\",\"perms\":\"shebao:person:demolition:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-19 23:56:03', 140);
INSERT INTO `sys_oper_log` VALUES (291, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/person/official/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2087,\"menuName\":\"村干部\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2078,\"path\":\"official\",\"perms\":\"shebao:person:official:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-19 23:56:12', 142);
INSERT INTO `sys_oper_log` VALUES (292, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/person/teacher/index\",\"createTime\":\"2026-01-19 11:15:28\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2088,\"menuName\":\"教师补贴\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2078,\"path\":\"teacher\",\"perms\":\"shebao:person:teacher:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-19 23:56:23', 196);
INSERT INTO `sys_oper_log` VALUES (293, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/person/landloss/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2084,\"menuName\":\"失地居民\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2078,\"path\":\"lost-land\",\"perms\":\"shebao:person:landloss:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:14:51', 313);
INSERT INTO `sys_oper_log` VALUES (294, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/landLossResident/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2084,\"menuName\":\"失地居民\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2078,\"path\":\"lost-land\",\"perms\":\"shebao:person:landloss:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:15:12', 152);
INSERT INTO `sys_oper_log` VALUES (295, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/expropriateeSubsidy/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2085,\"menuName\":\"被征地居民\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2078,\"path\":\"expropriatee\",\"perms\":\"shebao:person:expropriatee:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:15:38', 86);
INSERT INTO `sys_oper_log` VALUES (296, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/expropriateeSubsidy/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2086,\"menuName\":\"拆迁居民\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2078,\"path\":\"demolition\",\"perms\":\"shebao:person:demolition:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:16:01', 337);
INSERT INTO `sys_oper_log` VALUES (297, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/villageOfficial/index\",\"createTime\":\"2025-09-28 12:22:16\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2008,\"menuName\":\"村干部\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2003,\"path\":\"village-official\",\"perms\":\"\",\"routeName\":\"村干部\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:16:17', 118);
INSERT INTO `sys_oper_log` VALUES (298, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/person/official/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2087,\"menuName\":\"村干部\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2078,\"path\":\"village-official\",\"perms\":\"shebao:person:official:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:16:26', 435);
INSERT INTO `sys_oper_log` VALUES (299, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/villageOfficial/index\",\"createTime\":\"2025-09-28 12:22:16\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2008,\"menuName\":\"村干部\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2003,\"path\":\"village-official\",\"perms\":\"\",\"routeName\":\"村干部\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:16:34', 213);
INSERT INTO `sys_oper_log` VALUES (300, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/villageOfficial/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2087,\"menuName\":\"村干部\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2078,\"path\":\"village-official\",\"perms\":\"shebao:person:official:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:16:43', 257);
INSERT INTO `sys_oper_log` VALUES (301, '菜单管理', 3, 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '社保局', '/api/system/menu/2003', '127.0.0.1', '内网IP', '2003', '{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}', 0, NULL, '2026-01-20 00:25:21', 40);
INSERT INTO `sys_oper_log` VALUES (302, '菜单管理', 3, 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '社保局', '/api/system/menu/2005', '127.0.0.1', '内网IP', '2005', '{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}', 0, NULL, '2026-01-20 00:25:30', 85);
INSERT INTO `sys_oper_log` VALUES (303, '角色管理', 2, 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-09-24 22:11:36\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2009,2004,2002,2078,2084,2085,2086,2087,2088,2089,2090,2011],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:25:56', 381);
INSERT INTO `sys_oper_log` VALUES (304, '菜单管理', 3, 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '社保局', '/api/system/menu/2005', '127.0.0.1', '内网IP', '2005', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:26:14', 104);
INSERT INTO `sys_oper_log` VALUES (305, '菜单管理', 3, 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '社保局', '/api/system/menu/2006', '127.0.0.1', '内网IP', '2006', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:26:19', 121);
INSERT INTO `sys_oper_log` VALUES (306, '菜单管理', 3, 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '社保局', '/api/system/menu/2007', '127.0.0.1', '内网IP', '2007', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:26:25', 167);
INSERT INTO `sys_oper_log` VALUES (307, '菜单管理', 3, 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '社保局', '/api/system/menu/2008', '127.0.0.1', '内网IP', '2008', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:26:29', 106);
INSERT INTO `sys_oper_log` VALUES (308, '菜单管理', 3, 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '社保局', '/api/system/menu/2003', '127.0.0.1', '内网IP', '2003', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-20 00:26:34', 159);
INSERT INTO `sys_oper_log` VALUES (309, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"shebao/person/teacher/index\",\"createTime\":\"2026-01-19 11:15:28\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2088,\"menuName\":\"教龄补助\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2078,\"path\":\"teacher\",\"perms\":\"shebao:person:teacher:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-24 23:01:44', 137);
INSERT INTO `sys_oper_log` VALUES (310, '菜单管理', 2, 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"chart\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2083,\"menuName\":\"统计管理\",\"menuType\":\"M\",\"orderNum\":6,\"params\":{},\"parentId\":0,\"path\":\"audit\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-24 23:05:39', 127);
INSERT INTO `sys_oper_log` VALUES (311, '失地居民信息', 1, 'com.ruoyi.shebao.controller.LandLossResidentController.add()', 'POST', 1, 'admin', '社保局', '/api/shebao/landLossResident', '127.0.0.1', '内网IP', '{\"birthday\":\"1934-11-12\",\"compensationCompleteTime\":\"2026-01-13\",\"gender\":\"1\",\"homeAddress\":\"廊坊地址\",\"householdRegistration\":\"河北廊坊\",\"idCardNo\":\"142729999999999922\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2026-01-23\",\"name\":\"高老汉\",\"personExists\":false,\"phone\":\"13112345679\",\"recognitionTime\":\"2026-01-27\",\"streetOfficeId\":1,\"userCode\":\"0010020001\",\"villageCommitteeId\":2}', NULL, 1, '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010020001\' for key \'shebao_subsidy_person.uk_user_code\'\r\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\r\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO shebao_subsidy_person  ( name, gender, id_card_no, birthday, household_registration,  phone, is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?,  ?, ?,  ?, ?, ?, ?, ?, ?,  ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010020001\' for key \'shebao_subsidy_person.uk_user_code\'\n; Duplicate entry \'0010020001\' for key \'shebao_subsidy_person.uk_user_code\'', '2026-01-25 14:32:26', 478);
INSERT INTO `sys_oper_log` VALUES (312, '失地居民信息', 1, 'com.ruoyi.shebao.controller.LandLossResidentController.add()', 'POST', 1, 'admin', '社保局', '/api/shebao/landLossResident', '127.0.0.1', '内网IP', '{\"birthday\":\"2026-01-01\",\"compensationCompleteTime\":\"2026-01-26\",\"gender\":\"1\",\"homeAddress\":\"北京\",\"householdRegistration\":\"廊坊\",\"idCardNo\":\"142725198101111110\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2026-01-25\",\"name\":\"张老汉\",\"personExists\":false,\"phone\":\"188110948483\",\"recognitionTime\":\"2026-01-27\",\"streetOfficeId\":1,\"villageCommitteeId\":1}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-25 14:33:44', 603);
INSERT INTO `sys_oper_log` VALUES (313, '人员注销登记', 1, 'com.ruoyi.shebao.controller.PersonCancelController.add()', 'POST', 1, 'admin', '社保局', '/api/shebao/personCancel', '127.0.0.1', '内网IP', '{\"deathDate\":\"2026-01-19\",\"idCardNo\":\"142725198101111110\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-25 14:35:38', 382);
INSERT INTO `sys_oper_log` VALUES (314, '角色管理', 2, 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', 1, 'admin', '社保局', '/api/system/role', '183.241.78.6', 'XX XX', '{\"admin\":false,\"createTime\":\"2026-01-19 10:08:28\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"remark\":\"审计员，负责日志查询和报表审计\",\"roleId\":105,\"roleKey\":\"auditor\",\"roleName\":\"统计管理员\",\"roleSort\":6,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-28 19:29:00', 110);
INSERT INTO `sys_oper_log` VALUES (315, '支付计划', 1, 'com.ruoyi.shebao.controller.PaymentPlanController.generate()', 'POST', 1, 'admin', '社保局', '/api/shebao/payment/plan/generate', '183.241.78.6', 'XX XX', '{}', NULL, 1, '补贴类型不存在', '2026-01-29 01:33:28', 85);
INSERT INTO `sys_oper_log` VALUES (316, '失地居民信息', 1, 'com.ruoyi.shebao.controller.LandLossResidentController.add()', 'POST', 1, 'admin', '社保局', '/api/shebao/landLossResident', '60.10.59.193', 'XX XX', '{\"birthday\":\"1972-09-05\",\"compensationCompleteTime\":\"2025-12-01\",\"gender\":\"2\",\"homeAddress\":\"第一村\",\"householdRegistration\":\"第一村\",\"idCardNo\":\"132801197209052622\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2025-12-01\",\"name\":\"张一一\",\"personExists\":false,\"phone\":\"13555556666\",\"recognitionTime\":\"2026-01-01\",\"streetOfficeId\":1,\"villageCommitteeId\":1}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-30 09:20:22', 169);
INSERT INTO `sys_oper_log` VALUES (317, '失地居民信息', 2, 'com.ruoyi.shebao.controller.LandLossResidentController.edit()', 'PUT', 1, 'admin', '社保局', '/api/shebao/landLossResident', '60.10.59.193', 'XX XX', '{\"birthday\":\"1972-09-05\",\"compensationCompleteTime\":\"2025-12-01\",\"gender\":\"2\",\"homeAddress\":\"第一村\",\"householdRegistration\":\"第一村\",\"id\":5,\"idCardNo\":\"132801197209052622\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2025-12-01\",\"name\":\"张一一\",\"personExists\":true,\"phone\":\"13555556666\",\"recognitionTime\":\"2026-01-02\",\"streetOfficeId\":1,\"subsidyPersonId\":1000019,\"userCode\":\"0010010007\",\"villageCommitteeId\":1}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-01-30 09:20:39', 83);
INSERT INTO `sys_oper_log` VALUES (318, '失地居民信息', 2, 'com.ruoyi.shebao.controller.LandLossResidentController.edit()', 'PUT', 1, 'admin', '社保局', '/api/shebao/landLossResident', '60.10.59.193', 'XX XX', '{\"birthday\":\"1972-09-05\",\"compensationCompleteTime\":\"2025-12-01\",\"gender\":\"1\",\"homeAddress\":\"第一村\",\"householdRegistration\":\"第一村\",\"id\":5,\"idCardNo\":\"132801197209052622\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2025-12-01\",\"name\":\"张一一\",\"personExists\":true,\"phone\":\"13555556666\",\"recognitionTime\":\"2026-01-02\",\"streetOfficeId\":1,\"subsidyPersonId\":1000019,\"userCode\":\"0010010007\",\"villageCommitteeId\":1}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-02-03 15:07:12', 74);
INSERT INTO `sys_oper_log` VALUES (319, '人员注销登记', 1, 'com.ruoyi.shebao.controller.PersonCancelController.add()', 'POST', 1, 'admin', '社保局', '/api/shebao/personCancel', '60.10.59.193', 'XX XX', '{\"deathDate\":\"2026-02-01\",\"idCardNo\":\"13280119450112452X\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-02-03 15:07:52', 73);
INSERT INTO `sys_oper_log` VALUES (320, '人员关键信息修改', 1, 'com.ruoyi.shebao.controller.PersonModifyController.add()', 'POST', 1, 'admin', '社保局', '/api/shebao/person/modify', '127.0.0.1', '内网IP', '{\"householdRegistration\":\"廊坊\",\"idCardNo\":\"131131198411120021\",\"name\":\"刘三娘\",\"streetOfficeId\":1,\"subsidyPersonId\":1000001,\"villageCommitteeId\":2}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":1}', 0, NULL, '2026-02-03 18:53:14', 924);
INSERT INTO `sys_oper_log` VALUES (321, '人员关键信息修改提交', 2, 'com.ruoyi.shebao.controller.PersonModifyController.submit()', 'POST', 1, 'admin', '社保局', '/api/shebao/person/modify/submit/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-02-03 18:53:16', 902);
INSERT INTO `sys_oper_log` VALUES (322, '人员关键信息修改复核', 2, 'com.ruoyi.shebao.controller.PersonModifyController.review()', 'POST', 1, 'admin', '社保局', '/api/shebao/person/modify/review/1', '127.0.0.1', '内网IP', '{\"approved\":\"true\",\"remark\":\"已复核\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-02-03 18:54:16', 473);
INSERT INTO `sys_oper_log` VALUES (323, '人员关键信息修改审批', 2, 'com.ruoyi.shebao.controller.PersonModifyController.approve()', 'POST', 1, 'admin', '社保局', '/api/shebao/person/modify/approve/1', '127.0.0.1', '内网IP', '{\"approved\":\"true\",\"remark\":\"已审批\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-02-03 18:54:29', 787);
INSERT INTO `sys_oper_log` VALUES (324, '支付计划', 1, 'com.ruoyi.shebao.controller.PaymentPlanController.generate()', 'POST', 1, 'admin', '社保局', '/api/shebao/payment/plan/generate', '127.0.0.1', '内网IP', '{\"subsidyType\":\"land_loss_resident\"}', NULL, 1, '补贴类型不存在', '2026-02-03 18:56:03', 194);
INSERT INTO `sys_oper_log` VALUES (325, '支付计划', 1, 'com.ruoyi.shebao.controller.PaymentPlanController.generate()', 'POST', 1, 'admin', '社保局', '/api/shebao/payment/plan/generate', '60.10.59.193', 'XX XX', '{\"subsidyType\":\"land_loss_resident\"}', NULL, 1, '补贴类型不存在', '2026-02-25 16:31:13', 81);
INSERT INTO `sys_oper_log` VALUES (326, '支付计划', 1, 'com.ruoyi.shebao.controller.PaymentPlanController.generate()', 'POST', 1, 'admin', '社保局', '/api/shebao/payment/plan/generate', '60.10.59.193', 'XX XX', '{\"subsidyType\":\"land_loss_resident\"}', NULL, 1, '补贴类型不存在', '2026-02-25 16:31:18', 14);
INSERT INTO `sys_oper_log` VALUES (327, '支付计划', 1, 'com.ruoyi.shebao.controller.PaymentPlanController.generate()', 'POST', 1, 'admin', '社保局', '/api/shebao/payment/plan/generate', '127.0.0.1', '内网IP', '{\"subsidyType\":\"land_loss_resident\"}', NULL, 1, '补贴类型不存在', '2026-03-02 23:13:26', 33);
INSERT INTO `sys_oper_log` VALUES (328, '支付计划', 1, 'com.ruoyi.shebao.controller.PaymentPlanController.generate()', 'POST', 1, 'admin', '社保局', '/api/shebao/payment/plan/generate', '127.0.0.1', '内网IP', '{\"subsidyType\":\"teacher\"}', NULL, 1, '补贴类型不存在', '2026-03-02 23:13:35', 6);

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_post` VALUES (3, 'hr', '人力资源', 3, '0', 'admin', '2025-09-24 22:11:36', '', NULL, '');
INSERT INTO `sys_post` VALUES (4, 'user', '普通员工', 4, '0', 'admin', '2025-09-24 22:11:36', '', NULL, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2025-09-24 22:11:36', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2025-09-24 22:11:36', 'admin', '2026-01-20 00:25:55', '普通角色');
INSERT INTO `sys_role` VALUES (101, '经办人', 'operator', 2, '1', 1, 1, '0', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '业务经办人，负责数据录入和提交审核');
INSERT INTO `sys_role` VALUES (102, '复核人', 'reviewer', 3, '1', 1, 1, '0', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '业务复核人，负责复核经办人提交的数据');
INSERT INTO `sys_role` VALUES (103, '审批人', 'approver', 4, '1', 1, 1, '0', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '业务负责人（所长），负责最终审批');
INSERT INTO `sys_role` VALUES (104, '财务人员', 'finance', 5, '1', 1, 1, '0', '0', 'admin', '2026-01-19 10:08:28', '', NULL, '财务人员，负责发放和账户管理');
INSERT INTO `sys_role` VALUES (105, '统计管理员', 'auditor', 6, '1', 1, 1, '0', '0', 'admin', '2026-01-19 10:08:28', 'admin', '2026-01-28 19:29:00', '审计员，负责日志查询和报表审计');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
INSERT INTO `sys_role_dept` VALUES (2, 100);
INSERT INTO `sys_role_dept` VALUES (2, 101);
INSERT INTO `sys_role_dept` VALUES (2, 105);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 2000);
INSERT INTO `sys_role_menu` VALUES (2, 2002);
INSERT INTO `sys_role_menu` VALUES (2, 2004);
INSERT INTO `sys_role_menu` VALUES (2, 2009);
INSERT INTO `sys_role_menu` VALUES (2, 2011);
INSERT INTO `sys_role_menu` VALUES (2, 2078);
INSERT INTO `sys_role_menu` VALUES (2, 2084);
INSERT INTO `sys_role_menu` VALUES (2, 2085);
INSERT INTO `sys_role_menu` VALUES (2, 2086);
INSERT INTO `sys_role_menu` VALUES (2, 2087);
INSERT INTO `sys_role_menu` VALUES (2, 2088);
INSERT INTO `sys_role_menu` VALUES (2, 2089);
INSERT INTO `sys_role_menu` VALUES (2, 2090);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime NULL DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 106 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 100, 'admin', '超级管理员', '00', 'ry@163.com', '15888888888', '1', '', '$2a$10$t/ws7qYp/wPm9b7jdNNaiuUTOmCmVTHo1mikBUM1zZ3NL3jfDha42', '0', '0', '127.0.0.1', '2026-03-02 23:11:28', '2025-10-14 10:52:21', 'admin', '2025-09-24 22:11:36', '', '2025-10-14 10:52:21', '管理员');
INSERT INTO `sys_user` VALUES (2, 105, 'ry', '若依', '00', 'ry@qq.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '2', '127.0.0.1', '2025-09-24 22:11:36', '2025-09-24 22:11:36', 'admin', '2025-09-24 22:11:36', '', NULL, '测试员');
INSERT INTO `sys_user` VALUES (101, 100, 'operator01', '经办人01', '00', 'operator01@shebao.com', '13800001001', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sTQJ7RZ2Qm2', '0', '0', '127.0.0.1', '2026-01-19 10:08:28', NULL, 'admin', '2026-01-19 10:08:28', '', NULL, '经办人测试账号');
INSERT INTO `sys_user` VALUES (102, 100, 'reviewer01', '复核人01', '00', 'reviewer01@shebao.com', '13800001002', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sTQJ7RZ2Qm2', '0', '0', '127.0.0.1', '2026-01-19 10:08:28', NULL, 'admin', '2026-01-19 10:08:28', '', NULL, '复核人测试账号');
INSERT INTO `sys_user` VALUES (103, 100, 'approver01', '审批人01', '00', 'approver01@shebao.com', '13800001003', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sTQJ7RZ2Qm2', '0', '0', '127.0.0.1', '2026-01-19 10:08:28', NULL, 'admin', '2026-01-19 10:08:28', '', NULL, '审批人测试账号');
INSERT INTO `sys_user` VALUES (104, 100, 'finance01', '财务01', '00', 'finance01@shebao.com', '13800001004', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sTQJ7RZ2Qm2', '0', '0', '127.0.0.1', '2026-01-19 10:08:28', NULL, 'admin', '2026-01-19 10:08:28', '', NULL, '财务人员测试账号');
INSERT INTO `sys_user` VALUES (105, 100, 'auditor01', '审计员01', '00', 'auditor01@shebao.com', '13800001005', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/sTQJ7RZ2Qm2', '0', '0', '127.0.0.1', '2026-01-19 10:08:28', NULL, 'admin', '2026-01-19 10:08:28', '', NULL, '审计员测试账号');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (1, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (101, 101);
INSERT INTO `sys_user_role` VALUES (102, 102);
INSERT INTO `sys_user_role` VALUES (103, 103);
INSERT INTO `sys_user_role` VALUES (104, 104);
INSERT INTO `sys_user_role` VALUES (105, 105);

SET FOREIGN_KEY_CHECKS = 1;
