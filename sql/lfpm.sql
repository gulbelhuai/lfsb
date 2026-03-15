-- MySQL dump 10.13  Distrib 8.0.45, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: lfpm
-- ------------------------------------------------------
-- Server version	8.0.45-0ubuntu0.24.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `approval_log`
--

DROP TABLE IF EXISTS `approval_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approval_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '涓氬姟绫诲瀷(person_register/benefit_determination/payment_plan)',
  `business_id` bigint NOT NULL COMMENT '涓氬姟ID',
  `current_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '褰撳墠鐘舵?',
  `operation_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '鎿嶄綔绫诲瀷(submit/review/approve/reject/cancel)',
  `operator_id` bigint DEFAULT NULL COMMENT '鎿嶄綔浜篒D',
  `operator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '鎿嶄綔浜哄?鍚',
  `operation_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '鎿嶄綔璇存槑/椹冲洖鍘熷洜',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_business` (`business_type`,`business_id`) USING BTREE,
  KEY `idx_operator` (`operator_id`,`create_time`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='瀹℃壒娴佺▼璁板綍琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_log`
--

LOCK TABLES `approval_log` WRITE;
/*!40000 ALTER TABLE `approval_log` DISABLE KEYS */;
INSERT INTO `approval_log` VALUES (1,'person_register',7,'pending_review','submit',101,'operator01','\"提交审核测试\"','2026-03-03 00:55:23'),(2,'person_register',7,'approved','approve',102,'reviewer01','approved','2026-03-03 00:56:07'),(3,'person_register',8,'pending_review','submit',101,'operator01','\"submit\"','2026-03-03 00:57:09'),(4,'person_register',8,'approved','approve',102,'reviewer01','ok','2026-03-03 00:57:09'),(5,'person_register',9,'pending_review','submit',101,'operator01','\"test\"','2026-03-03 01:01:39'),(6,'person_register',9,'approved','approve',102,'reviewer01','ok','2026-03-03 01:01:39'),(7,'person_register',10,'pending_review','submit',101,'operator01','\"submit\"','2026-03-03 01:07:04'),(8,'person_register',10,'approved','approve',102,'reviewer01','ok','2026-03-03 01:07:04'),(9,'person_register',11,'pending_review','submit',101,'operator01','\"submit\"','2026-03-03 01:40:36'),(10,'person_register',11,'approved','approve',102,'reviewer01','信息核实无误，符合失地居民认定条件，同意通过','2026-03-03 01:56:17'),(11,'person_register',12,'pending_review','submit',101,'operator01','\"submit\"','2026-03-03 04:03:59'),(12,'person_register',12,'rejected','reject',102,'reviewer01','身份证号与户籍信息不一致,请核实后重新提交','2026-03-03 04:07:47');
/*!40000 ALTER TABLE `approval_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `approval_role_config`
--

DROP TABLE IF EXISTS `approval_role_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `approval_role_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '涓氬姟绫诲瀷',
  `approval_level` int NOT NULL COMMENT '瀹℃壒灞傜骇(1/2/3)',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '瑙掕壊浠ｇ爜',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '瑙掕壊鍚嶇О',
  `required_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' COMMENT '鏄?惁蹇呴渶(0鍚?鏄?',
  `order_num` int DEFAULT '0' COMMENT '鎺掑簭鍙',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0瀛樺湪1鍒犻櫎)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_business_level` (`business_type`,`approval_level`) USING BTREE,
  KEY `idx_business_type` (`business_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='瀹℃壒瑙掕壊閰嶇疆琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `approval_role_config`
--

LOCK TABLES `approval_role_config` WRITE;
/*!40000 ALTER TABLE `approval_role_config` DISABLE KEYS */;
INSERT INTO `approval_role_config` VALUES (1,'person_register',1,'operator','经办人','1',1,'0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','人员登记：经办人提交'),(2,'person_register',2,'reviewer','复核人','1',2,'0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','人员登记：复核人复核'),(3,'benefit_determination',1,'operator','经办人','1',1,'0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','待遇核定：经办人提交'),(4,'benefit_determination',2,'reviewer','复核人','1',2,'0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','待遇核定：复核人复核'),(5,'payment_plan',1,'operator','经办人','1',1,'0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','支付计划：经办人提交'),(6,'payment_plan',2,'reviewer','复核人','1',2,'0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','支付计划：复核人复核'),(7,'payment_plan',3,'approver','审批人','1',3,'0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','支付计划：审批人审批'),(8,'payment_plan',4,'finance','财务人员','1',4,'0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','支付计划：财务人员处理'),(9,'info_modify',1,'operator','经办人','1',1,'0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','关键信息修改：经办人提交'),(10,'info_modify',2,'reviewer','复核人','1',2,'0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','关键信息修改：复核人复核'),(11,'info_modify',3,'approver','审批人','1',3,'0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','关键信息修改：审批人审批');
/*!40000 ALTER TABLE `approval_role_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `benefit_determination`
--

DROP TABLE IF EXISTS `benefit_determination`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `benefit_determination` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '琚?ˉ璐翠汉ID',
  `subsidy_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '琛ヨ创绫诲瀷',
  `eligible_year` int DEFAULT NULL COMMENT '鍒伴緞骞翠唤',
  `eligible_month` int DEFAULT NULL COMMENT '鍒伴緞鏈堜唤',
  `benefit_start_year` int DEFAULT NULL COMMENT '琛ヨ创浜?彈寮??骞翠唤',
  `benefit_start_month` int DEFAULT NULL COMMENT '琛ヨ创浜?彈寮??鏈堜唤',
  `bank_id` bigint DEFAULT NULL COMMENT '鍙戞斁閾惰?ID',
  `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '银行名称',
  `bank_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '閾惰?璐﹀彿',
  `bank_account_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '閾惰?璐︽埛濮撳悕',
  `subsidy_standard` decimal(10,2) DEFAULT NULL COMMENT '琛ヨ创鏍囧噯',
  `benefit_months` int DEFAULT '0' COMMENT '琛ュ彂鏈堟暟',
  `benefit_amount` decimal(12,2) DEFAULT '0.00' COMMENT '琛ュ彂閲戦?',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'draft' COMMENT '瀹℃壒鐘舵?(draft/pending_review/pending_approve/approved/rejected)',
  `approval_batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '瀹℃壒鎵规?鍙',
  `notice_batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '预到龄通知批次号',
  `notice_detail_id` bigint DEFAULT NULL COMMENT '预到龄通知明细ID',
  `id_card_no` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '身份证号快照',
  `submit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '提交人',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `review_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '复核人',
  `review_time` datetime DEFAULT NULL COMMENT '复核时间',
  `review_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '复核意见',
  `material_zip_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '材料ZIP路径',
  `material_extract_dir` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '材料解压目录',
  `material_image_paths` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '材料图片路径集合',
  `material_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'pending_upload' COMMENT '材料状态(pending_upload/uploaded/verified)',
  `payment_plan_generated` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '是否已进入支付计划(0否1是)',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0瀛樺湪1鍒犻櫎)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_person` (`subsidy_person_id`) USING BTREE,
  KEY `idx_approval_status` (`approval_status`) USING BTREE,
  KEY `idx_batch` (`approval_batch_no`) USING BTREE,
  KEY `idx_notice_batch_no` (`notice_batch_no`) USING BTREE,
  KEY `idx_notice_detail_id` (`notice_detail_id`) USING BTREE,
  KEY `idx_payment_plan_generated` (`payment_plan_generated`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='寰呴亣鏍稿畾琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `benefit_determination`
--

LOCK TABLES `benefit_determination` WRITE;
/*!40000 ALTER TABLE `benefit_determination` DISABLE KEYS */;
INSERT INTO `benefit_determination` VALUES (1,1000024,'land_loss_resident',2053,1,2053,2,NULL,NULL,'6222009876543210','全流程测试李四',600.00,0,0.00,'approved',NULL,'0',NULL,'2026-03-03 09:07:04',NULL,'2026-03-03 01:07:04',NULL),(2,1000025,'land_loss_resident',2028,3,2026,3,NULL,NULL,'6222001234567890',NULL,500.00,0,0.00,'approved',NULL,'0',NULL,'2026-03-03 13:12:27',NULL,'2026-03-03 05:16:05',NULL);
/*!40000 ALTER TABLE `benefit_determination` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_benefit_notice_batch`
--

DROP TABLE IF EXISTS `shebao_benefit_notice_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_benefit_notice_batch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '批次号',
  `notice_month` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知月份',
  `retirement_month` varchar(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '到龄月份',
  `age_threshold` int DEFAULT 60 COMMENT '年龄阈值',
  `total_count` int DEFAULT 0 COMMENT '总人数',
  `pending_review_count` int DEFAULT 0 COMMENT '待复核人数',
  `approved_count` int DEFAULT 0 COMMENT '已通过人数',
  `rejected_count` int DEFAULT 0 COMMENT '已驳回人数',
  `batch_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'generated' COMMENT '批次状态(generated/processing/completed)',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_batch_no` (`batch_no`) USING BTREE,
  UNIQUE KEY `uk_notice_month` (`notice_month`) USING BTREE,
  KEY `idx_batch_status` (`batch_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='预到龄通知批次表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shebao_benefit_notice_detail`
--

DROP TABLE IF EXISTS `shebao_benefit_notice_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_benefit_notice_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `notice_batch_id` bigint NOT NULL COMMENT '通知批次ID',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '批次号',
  `subsidy_person_id` bigint NOT NULL COMMENT '人员ID',
  `user_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户编号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `id_card_no` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '身份证号',
  `subsidy_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '补贴类型',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `retirement_date` date DEFAULT NULL COMMENT '到龄日期',
  `determination_id` bigint DEFAULT NULL COMMENT '待遇核定记录ID',
  `determination_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'draft' COMMENT '核定状态(draft/pending_review/approved/rejected/payment_generated)',
  `material_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'pending_upload' COMMENT '材料状态(pending_upload/uploaded/verified)',
  `review_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'draft' COMMENT '复核状态',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_batch_person` (`batch_no`,`subsidy_person_id`) USING BTREE,
  KEY `idx_notice_batch_id` (`notice_batch_id`) USING BTREE,
  KEY `idx_determination_status` (`determination_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='预到龄通知明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shebao_benefit_attachment`
--

DROP TABLE IF EXISTS `shebao_benefit_attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_benefit_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型',
  `business_id` bigint NOT NULL COMMENT '业务ID',
  `notice_batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通知批次号',
  `subsidy_person_id` bigint NOT NULL COMMENT '人员ID',
  `original_file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '原始文件名',
  `zip_file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'ZIP路径',
  `extract_dir` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '解压目录',
  `preview_image_paths` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '预览图片路径',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_business` (`business_type`,`business_id`) USING BTREE,
  KEY `idx_notice_batch_no` (`notice_batch_no`) USING BTREE,
  KEY `idx_subsidy_person_id` (`subsidy_person_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='待遇核定附件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `distribution_batch`
--

DROP TABLE IF EXISTS `distribution_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `distribution_batch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '鎵规?鍙',
  `batch_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '鎵规?绫诲瀷(first/second/third)',
  `subsidy_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '琛ヨ创绫诲瀷',
  `distribution_year` int DEFAULT NULL COMMENT '鍙戞斁骞翠唤',
  `distribution_month` int DEFAULT NULL COMMENT '鍙戞斁鏈堜唤',
  `total_count` int DEFAULT '0' COMMENT '鎬讳汉鏁',
  `total_amount` decimal(12,2) DEFAULT '0.00' COMMENT '鎬婚噾棰',
  `success_count` int DEFAULT '0' COMMENT '鎴愬姛浜烘暟',
  `success_amount` decimal(12,2) DEFAULT '0.00' COMMENT '鎴愬姛閲戦?',
  `fail_count` int DEFAULT '0' COMMENT '澶辫触浜烘暟',
  `fail_amount` decimal(12,2) DEFAULT '0.00' COMMENT '澶辫触閲戦?',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'draft' COMMENT '瀹℃壒鐘舵?(draft/pending_review/pending_approve/pending_finance/submitted_bank/distributed)',
  `finance_upload_time` datetime DEFAULT NULL COMMENT '涓婁紶璐㈠姟绯荤粺鏃堕棿',
  `bank_submit_time` datetime DEFAULT NULL COMMENT '鎻愪氦閾惰?鏃堕棿',
  `bank_result_time` datetime DEFAULT NULL COMMENT '閾惰?杩斿洖缁撴灉鏃堕棿',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0瀛樺湪1鍒犻櫎)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `batch_no` (`batch_no`) USING BTREE,
  KEY `idx_batch_no` (`batch_no`) USING BTREE,
  KEY `idx_approval_status` (`approval_status`) USING BTREE,
  KEY `idx_batch_type` (`batch_type`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='鍙戞斁鎵规?琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribution_batch`
--

LOCK TABLES `distribution_batch` WRITE;
/*!40000 ALTER TABLE `distribution_batch` DISABLE KEYS */;
INSERT INTO `distribution_batch` VALUES (1,'20260303-054','first',NULL,NULL,NULL,0,0.00,0,0.00,0,0.00,'uploaded',NULL,NULL,NULL,'0',NULL,'2026-03-03 17:04:34',NULL,'2026-03-03 17:04:34','ok');
/*!40000 ALTER TABLE `distribution_batch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `finance_account`
--

DROP TABLE IF EXISTS `finance_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `finance_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `account_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '璐﹀彿鍙风爜',
  `account_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '璐﹀彿鍚嶇О',
  `bank_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '閾惰?鍚嶇О',
  `subsidy_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '琛ヨ创绫诲瀷(land_loss/expropriatee/demolition/village_official/teacher)',
  `balance` decimal(16,2) DEFAULT '0.00' COMMENT '璐︽埛浣欓?',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' COMMENT '鐘舵?(0鍋滅敤1姝ｅ父)',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '鍒犻櫎鏍囧織(0瀛樺湪1鍒犻櫎)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `account_no` (`account_no`) USING BTREE,
  KEY `idx_subsidy_type` (`subsidy_type`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='璐㈠姟璐︽埛琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `finance_account`
--

LOCK TABLES `finance_account` WRITE;
/*!40000 ALTER TABLE `finance_account` DISABLE KEYS */;
INSERT INTO `finance_account` VALUES (1,'LF2025001','失地居民养老补贴账户','中国工商银行廊坊开发区支行','land_loss',1000000.00,'1','0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','失地居民养老补贴专户'),(2,'LF2025002','被征地居民养老补贴账户','中国建设银行廊坊开发区支行','expropriatee',800000.00,'1','0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','被征地居民养老补贴专户'),(3,'LF2025003','拆迁居民养老补贴账户','中国农业银行廊坊开发区支行','demolition',500000.00,'1','0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','拆迁居民养老补贴专户'),(4,'LF2025004','村干部养老补贴账户','中国银行廊坊开发区支行','village_official',300000.00,'1','0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','村干部养老补贴专户'),(5,'LF2025005','教师养老补贴账户','中国邮政储蓄银行廊坊开发区支行','teacher',200000.00,'1','0','admin','2026-01-19 10:08:28',NULL,'2026-01-19 10:08:28','教师养老补贴专户'),(6,'95588001234','测试账户','工商银行','land_loss',0.00,'0','0',NULL,'2026-03-02 17:24:02',NULL,'2026-03-02 17:24:02',NULL);
/*!40000 ALTER TABLE `finance_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--

LOCK TABLES `gen_table` WRITE;
/*!40000 ALTER TABLE `gen_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `gen_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--

LOCK TABLES `gen_table_column` WRITE;
/*!40000 ALTER TABLE `gen_table_column` DISABLE KEYS */;
/*!40000 ALTER TABLE `gen_table_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_key_info_modify`
--

DROP TABLE IF EXISTS `person_key_info_modify`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `person_key_info_modify` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `id_card_no` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证号',
  `household_registration` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '户籍所在地',
  `street_office_id` bigint DEFAULT NULL COMMENT '所属街道办ID',
  `village_committee_id` bigint DEFAULT NULL COMMENT '所属村委会ID',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'draft' COMMENT '审批状态(draft-草稿 pending_review-待复核 pending_approve-待审批 approved-已通过 rejected-已驳回)',
  `submit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '提交人',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `review_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '复核人',
  `review_time` datetime DEFAULT NULL COMMENT '复核时间',
  `approve_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `reject_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '驳回原因',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_subsidy_person_id` (`subsidy_person_id`) USING BTREE,
  KEY `idx_approval_status` (`approval_status`) USING BTREE,
  KEY `idx_id_card_no` (`id_card_no`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='人员关键信息修改申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_key_info_modify`
--

LOCK TABLES `person_key_info_modify` WRITE;
/*!40000 ALTER TABLE `person_key_info_modify` DISABLE KEYS */;
INSERT INTO `person_key_info_modify` VALUES (1,1000001,'刘三娘','131131198411120021','廊坊',1,2,'approved','admin','2026-02-03 18:53:15','admin','2026-02-03 18:54:16','admin','2026-02-03 18:54:29',NULL,'0','admin','2026-02-03 18:53:14','admin','2026-02-03 18:54:29',NULL),(2,1000025,'陈建国','130105196803152847','河北省廊坊市安次区光明路街道(更正)',3,1,'pending_approve','operator01','2026-03-03 16:18:48','reviewer01','2026-03-03 16:18:48',NULL,NULL,NULL,'0','operator01','2026-03-03 16:18:48','reviewer01','2026-03-03 16:18:48','户籍信息更正'),(3,1000025,'陈建国','130105196803152847','河北省坊市安次区光明路街道(更正后)',2,3,'pending_review','operator01','2026-03-03 16:29:07',NULL,NULL,NULL,NULL,NULL,'0','operator01','2026-03-03 16:29:07','operator01','2026-03-03 16:29:07','户籍地址更正申请');
/*!40000 ALTER TABLE `person_key_info_modify` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='Blob类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_blob_triggers`
--

LOCK TABLES `qrtz_blob_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_blob_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_blob_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='日历信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_calendars`
--

LOCK TABLES `qrtz_calendars` WRITE;
/*!40000 ALTER TABLE `qrtz_calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='已触发的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_fired_triggers`
--

LOCK TABLES `qrtz_fired_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_fired_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_fired_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务组名',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='任务详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_job_details`
--

LOCK TABLES `qrtz_job_details` WRITE;
/*!40000 ALTER TABLE `qrtz_job_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='存储的悲观锁信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_locks`
--

LOCK TABLES `qrtz_locks` WRITE;
/*!40000 ALTER TABLE `qrtz_locks` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='暂停的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--

LOCK TABLES `qrtz_paused_trigger_grps` WRITE;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='调度器状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_scheduler_state`
--

LOCK TABLES `qrtz_scheduler_state` WRITE;
/*!40000 ALTER TABLE `qrtz_scheduler_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_scheduler_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='触发器详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_triggers`
--

LOCK TABLES `qrtz_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_administrative_division`
--

DROP TABLE IF EXISTS `shebao_administrative_division`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_administrative_division` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `division_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '行政区划编码',
  `division_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '行政区划名称',
  `parent_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '父级编码',
  `division_level` tinyint NOT NULL COMMENT '行政级别（1省 2市 3县 4乡镇 5村）',
  `full_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '全路径编码，用/分隔',
  `full_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '全路径名称，用/分隔',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '详细地址',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_division_code` (`division_code`) USING BTREE,
  KEY `idx_parent_code` (`parent_code`) USING BTREE,
  KEY `idx_division_level` (`division_level`) USING BTREE,
  KEY `idx_full_code` (`full_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='行政区划表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_administrative_division`
--

LOCK TABLES `shebao_administrative_division` WRITE;
/*!40000 ALTER TABLE `shebao_administrative_division` DISABLE KEYS */;
INSERT INTO `shebao_administrative_division` VALUES (1,'130000','河北省',NULL,1,'130000','河北省',NULL,NULL,NULL,1,'0','0','','2026-01-19 23:48:01','','2026-01-19 23:48:01',NULL),(2,'131000','廊坊市','130000',2,'130000/131000','河北省/廊坊市',NULL,NULL,NULL,1,'0','0','','2026-01-19 23:48:01','','2026-01-19 23:48:01',NULL),(3,'131001','安次区','131000',3,'130000/131000/131001','河北省/廊坊市/安次区',NULL,NULL,NULL,1,'0','0','','2026-01-19 23:48:01','','2026-01-19 23:48:01',NULL),(4,'131002','广阳区','131000',3,'130000/131000/131002','河北省/廊坊市/广阳区',NULL,NULL,NULL,2,'0','0','','2026-01-19 23:48:01','','2026-01-19 23:48:01',NULL),(5,'131003','开发区','131000',3,'130000/131000/131003','河北省/廊坊市/开发区',NULL,NULL,NULL,3,'0','0','','2026-01-19 23:48:01','','2026-01-19 23:48:01',NULL),(6,'131001001','银河南路街道','131001',4,'130000/131000/131001/131001001','河北省/廊坊市/安次区/银河南路街道',NULL,NULL,NULL,1,'0','0','','2026-01-19 23:48:01','','2026-01-19 23:48:01',NULL),(7,'131001002','光明西道街道','131001',4,'130000/131000/131001/131001002','河北省/廊坊市/安次区/光明西道街道',NULL,NULL,NULL,2,'0','0','','2026-01-19 23:48:01','','2026-01-19 23:48:01',NULL),(8,'131002001','解放道街道','131002',4,'130000/131000/131002/131002001','河北省/廊坊市/广阳区/解放道街道',NULL,NULL,NULL,1,'0','0','','2026-01-19 23:48:01','','2026-01-19 23:48:01',NULL),(9,'131002002','新开路街道','131002',4,'130000/131000/131002/131002002','河北省/廊坊市/广阳区/新开路街道',NULL,NULL,NULL,2,'0','0','','2026-01-19 23:48:01','','2026-01-19 23:48:01',NULL);
/*!40000 ALTER TABLE `shebao_administrative_division` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_demo`
--

DROP TABLE IF EXISTS `shebao_demo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_demo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_demo`
--

LOCK TABLES `shebao_demo` WRITE;
/*!40000 ALTER TABLE `shebao_demo` DISABLE KEYS */;
INSERT INTO `shebao_demo` VALUES (1,'123','0','0','','2025-09-26 23:54:56','','2025-09-26 23:54:56',NULL);
/*!40000 ALTER TABLE `shebao_demo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_demolition_resident`
--

DROP TABLE IF EXISTS `shebao_demolition_resident`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_demolition_resident` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `demolition_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '拆迁事由',
  `demolition_time` date DEFAULT NULL COMMENT '拆迁时间',
  `recognition_time` date DEFAULT NULL COMMENT '认定为拆迁居民时间',
  `subsidy_amount` decimal(10,2) DEFAULT '0.00' COMMENT '补贴金额',
  `distribution_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_subsidy_person_id` (`subsidy_person_id`) USING BTREE,
  KEY `idx_demolition_time` (`demolition_time`) USING BTREE,
  KEY `idx_recognition_time` (`recognition_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='拆迁居民信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_demolition_resident`
--

LOCK TABLES `shebao_demolition_resident` WRITE;
/*!40000 ALTER TABLE `shebao_demolition_resident` DISABLE KEYS */;
INSERT INTO `shebao_demolition_resident` VALUES (1,1000000,'修路','2025-09-29','2025-09-16',0.00,'0','0','0','admin','2025-09-28 22:10:57','','2025-09-28 22:38:01',NULL),(2,1000016,'222','2025-10-03','2025-10-04',0.00,'0','0','0','admin','2025-10-19 17:29:22','admin','2025-10-19 17:54:36','555');
/*!40000 ALTER TABLE `shebao_demolition_resident` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_distribution_review`
--

DROP TABLE IF EXISTS `shebao_distribution_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_distribution_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `distribution_id` bigint NOT NULL COMMENT '发放记录ID（关联shebao_subsidy_distribution.id）',
  `operation_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型（1提交审核 2审核通过 3审核驳回 4发放 5重新提交）',
  `operation_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作说明',
  `operator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作人',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_distribution_id` (`distribution_id`) USING BTREE,
  KEY `idx_operation_type` (`operation_type`) USING BTREE,
  KEY `idx_operation_time` (`operation_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='发放审核记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_distribution_review`
--

LOCK TABLES `shebao_distribution_review` WRITE;
/*!40000 ALTER TABLE `shebao_distribution_review` DISABLE KEYS */;
INSERT INTO `shebao_distribution_review` VALUES (1,1,'1','提交审核','admin','2025-10-19 20:59:18','0','0','','2025-10-19 20:59:17','','2025-10-19 20:59:17',NULL),(2,2,'1','提交审核','admin','2025-10-19 21:16:55','0','0','','2025-10-19 21:16:55','','2025-10-19 21:16:55',NULL),(3,2,'2','同意','admin','2025-10-19 21:19:58','0','0','','2025-10-19 21:19:58','','2025-10-19 21:19:58',NULL),(4,3,'1','提交审核','admin','2025-10-19 21:20:41','0','0','','2025-10-19 21:20:40','','2025-10-19 21:20:40',NULL),(5,4,'1','提交审核','admin','2025-10-19 21:21:37','0','0','','2025-10-19 21:21:37','','2025-10-19 21:21:37',NULL),(6,4,'2','同意','admin','2025-10-19 21:21:43','0','0','','2025-10-19 21:21:43','','2025-10-19 21:21:43',NULL),(7,4,'4','同意','admin','2025-10-19 21:21:49','0','0','','2025-10-19 21:21:49','','2025-10-19 21:21:49',NULL),(8,5,'1','提交审核','admin','2025-10-19 22:04:58','0','0','','2025-10-19 22:04:58','','2025-10-19 22:04:58',NULL),(9,6,'1','提交审核','admin','2025-10-19 22:13:58','0','0','','2025-10-19 22:13:58','','2025-10-19 22:13:58',NULL),(10,7,'1','提交审核','admin','2025-10-19 22:15:01','0','0','','2025-10-19 22:15:00','','2025-10-19 22:15:00',NULL),(11,8,'1','提交审核','admin','2025-10-19 22:15:20','0','0','','2025-10-19 22:15:20','','2025-10-19 22:15:20',NULL),(12,9,'1','提交审核','admin','2025-10-19 22:33:03','0','0','','2025-10-19 22:33:03','','2025-10-19 22:33:03',NULL),(13,10,'1','1231231231','admin','2025-10-19 22:49:37','0','0','','2025-10-19 22:49:37','','2025-10-19 22:49:37',NULL),(14,10,'2','','admin','2025-10-19 22:52:00','0','0','','2025-10-19 22:52:00','','2025-10-19 22:52:00',NULL),(15,10,'4','distributed','admin','2026-03-03 00:57:10','0','0','','2026-03-03 00:57:09','','2026-03-03 00:57:09',NULL),(16,11,'1','提交审核','operator01','2026-03-03 01:01:40','0','0','','2026-03-03 01:01:40','','2026-03-03 01:01:40',NULL),(17,11,'2','ok','admin','2026-03-03 01:01:40','0','0','','2026-03-03 01:01:40','','2026-03-03 01:01:40',NULL),(18,11,'4','ok','admin','2026-03-03 01:01:40','0','0','','2026-03-03 01:01:40','','2026-03-03 01:01:40',NULL),(19,12,'1','提交审核','operator01','2026-03-03 01:07:05','0','0','','2026-03-03 01:07:04','','2026-03-03 01:07:04',NULL),(20,12,'2','ok','admin','2026-03-03 01:07:05','0','0','','2026-03-03 01:07:04','','2026-03-03 01:07:04',NULL),(21,12,'4','ok','admin','2026-03-03 01:07:05','0','0','','2026-03-03 01:07:04','','2026-03-03 01:07:04',NULL),(22,9,'2','审核通过','admin','2026-03-03 14:10:03','0','0','','2026-03-03 14:10:02','','2026-03-03 14:10:02',NULL),(23,9,'4','审核通过','admin','2026-03-03 14:12:09','0','0','','2026-03-03 14:12:08','','2026-03-03 14:12:08',NULL);
/*!40000 ALTER TABLE `shebao_distribution_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_expropriatee_subsidy`
--

DROP TABLE IF EXISTS `shebao_expropriatee_subsidy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_expropriatee_subsidy` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `land_requisition_batch` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '征地批次',
  `base_date` date DEFAULT NULL COMMENT '基准日',
  `employee_pension_months` int DEFAULT '0' COMMENT '职工身份缴纳职工养老月数',
  `flexible_employment_months` int DEFAULT '0' COMMENT '灵活就业身份缴纳职工养老保险月数',
  `difficulty_subsidy_years` decimal(5,2) DEFAULT '0.00' COMMENT '已领取困难人员社会保险补贴年限',
  `age_at_base_date` int DEFAULT NULL COMMENT '截至基准日的年龄',
  `subsidy_years` decimal(5,2) DEFAULT '0.00' COMMENT '被征地参保补贴年限',
  `subsidy_amount` decimal(10,2) DEFAULT '0.00' COMMENT '补贴金额',
  `distribution_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
  `join_urban_rural_insurance` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '选择参加城乡居保（0否 1是）',
  `join_employee_pension` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '选择参加职工养老（0否 1是）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_subsidy_person_id` (`subsidy_person_id`) USING BTREE,
  KEY `idx_batch` (`land_requisition_batch`) USING BTREE,
  KEY `idx_base_date` (`base_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='被征地参保补贴账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_expropriatee_subsidy`
--

LOCK TABLES `shebao_expropriatee_subsidy` WRITE;
/*!40000 ALTER TABLE `shebao_expropriatee_subsidy` DISABLE KEYS */;
INSERT INTO `shebao_expropriatee_subsidy` VALUES (1,1000000,'20202','2025-09-09',120,10,0.00,40,0.00,0.00,'0','0','0','0','0','admin','2025-09-28 12:23:17','','2025-09-28 22:38:10',NULL),(2,1000002,NULL,'2025-09-01',0,0,0.00,70,15.00,7147.00,'0','1','0','0','0','admin','2025-09-28 22:59:15','','2025-09-28 22:59:15',NULL),(3,1000003,'第一批','2025-09-01',0,0,0.00,69,15.00,7147.22,'0','0','0','0','0','admin','2025-09-28 23:43:51','','2025-09-28 23:43:51',NULL),(4,1000000,NULL,'2025-10-01',0,0,0.00,40,3.00,1429.44,'0','0','0','0','0','admin','2025-10-16 15:35:19','','2025-10-16 15:35:19',NULL);
/*!40000 ALTER TABLE `shebao_expropriatee_subsidy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_land_loss_resident`
--

DROP TABLE IF EXISTS `shebao_land_loss_resident`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_land_loss_resident` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `land_requisition_time` date DEFAULT NULL COMMENT '征地时间',
  `compensation_complete_time` date DEFAULT NULL COMMENT '完成补偿时间',
  `recognition_time` date DEFAULT NULL COMMENT '认定为失地居民时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_subsidy_person_id` (`subsidy_person_id`) USING BTREE,
  KEY `idx_recognition_time` (`recognition_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='失地居民信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_land_loss_resident`
--

LOCK TABLES `shebao_land_loss_resident` WRITE;
/*!40000 ALTER TABLE `shebao_land_loss_resident` DISABLE KEYS */;
INSERT INTO `shebao_land_loss_resident` VALUES (1,1000001,'2025-09-01','2025-09-22','2025-09-23','0','admin','2025-09-28 02:18:49','admin','2025-10-19 22:20:46','每月8号发'),(2,1000005,'2025-10-01','2025-09-29','2025-10-01','0','admin','2025-10-11 16:28:26','','2025-10-11 16:28:26',NULL),(3,1000000,'2025-10-01','2025-10-22','2025-10-01','0','admin','2025-10-11 16:29:39','admin','2025-10-19 20:52:52',NULL),(4,1000018,'2026-01-25','2026-01-26','2026-01-27','0','admin','2026-01-25 14:33:43','','2026-01-25 14:33:43',NULL),(5,1000019,'2025-12-01','2025-12-01','2026-01-02','0','admin','2026-01-30 09:20:23','admin','2026-02-03 15:07:12',NULL),(6,1000020,NULL,NULL,NULL,'0','operator01','2026-03-02 16:36:54','','2026-03-02 16:36:54',NULL),(7,1000021,NULL,NULL,NULL,'0','operator01','2026-03-03 00:55:23','','2026-03-03 00:55:23',NULL),(8,1000022,NULL,NULL,NULL,'0','operator01','2026-03-03 00:57:09','','2026-03-03 00:57:08',NULL),(9,1000023,NULL,NULL,NULL,'0','operator01','2026-03-03 01:01:39','','2026-03-03 01:01:39',NULL),(10,1000024,NULL,NULL,NULL,'0','operator01','2026-03-03 01:07:04','','2026-03-03 01:07:03',NULL),(11,1000025,'2019-06-15','2019-08-01',NULL,'0','operator01','2026-03-03 01:39:19','','2026-03-03 01:39:19',NULL),(12,1000026,NULL,NULL,NULL,'0','operator01','2026-03-03 04:03:59','','2026-03-03 04:03:58',NULL);
/*!40000 ALTER TABLE `shebao_land_loss_resident` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_person_cancel`
--

DROP TABLE IF EXISTS `shebao_person_cancel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_person_cancel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID（关联shebao_subsidy_person.id）',
  `death_date` date NOT NULL COMMENT '死亡时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_subsidy_person_id` (`subsidy_person_id`) USING BTREE,
  KEY `idx_death_date` (`death_date`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_person_cancel_person` FOREIGN KEY (`subsidy_person_id`) REFERENCES `shebao_subsidy_person` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='人员注销登记表（死亡登记）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_person_cancel`
--

LOCK TABLES `shebao_person_cancel` WRITE;
/*!40000 ALTER TABLE `shebao_person_cancel` DISABLE KEYS */;
INSERT INTO `shebao_person_cancel` VALUES (1,1000018,'2026-01-19','0',NULL,'admin','2026-01-25 14:35:37','','2026-01-25 14:35:37'),(2,1000005,'2026-02-01','0',NULL,'admin','2026-02-03 15:07:53','','2026-02-03 15:07:52');
/*!40000 ALTER TABLE `shebao_person_cancel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_street_office`
--

DROP TABLE IF EXISTS `shebao_street_office`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_street_office` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `street_code` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '街道办编码（3位数字，如：001）',
  `street_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '街道办名称',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '详细地址',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_street_code` (`street_code`) USING BTREE,
  KEY `idx_street_name` (`street_name`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='街道办事处信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_street_office`
--

LOCK TABLES `shebao_street_office` WRITE;
/*!40000 ALTER TABLE `shebao_street_office` DISABLE KEYS */;
INSERT INTO `shebao_street_office` VALUES (1,'001','银河南路街道',NULL,NULL,NULL,1,'0','0','admin','2025-10-18 23:56:47','','2025-10-18 23:56:47',NULL),(2,'002','龙河路街道',NULL,NULL,NULL,2,'0','0','admin','2025-10-18 23:56:47','','2025-10-18 23:56:47',NULL),(3,'003','光明路街道',NULL,NULL,NULL,3,'0','0','admin','2025-10-18 23:56:47','','2025-10-18 23:56:47',NULL),(4,'004','麦子店街道','黄','13612345678','河北廊坊',0,'0','0','','2025-10-19 11:02:13','','2025-10-19 11:02:13','测试');
/*!40000 ALTER TABLE `shebao_street_office` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_subsidy_distribution`
--

DROP TABLE IF EXISTS `shebao_subsidy_distribution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_subsidy_distribution` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '鎵规?鍙',
  `batch_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'first' COMMENT '鎵规?绫诲瀷(first/second/third)',
  `approval_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'draft' COMMENT '瀹℃壒鐘舵?(draft/pending_review/pending_approve/pending_finance/distributed/rejected)',
  `rejection_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '椹冲洖鍘熷洜',
  `review_time` datetime DEFAULT NULL COMMENT '澶嶆牳鏃堕棿',
  `review_user_id` bigint DEFAULT NULL COMMENT '澶嶆牳浜篒D',
  `review_user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '澶嶆牳浜哄?鍚',
  `approve_time` datetime DEFAULT NULL COMMENT '瀹℃壒鏃堕棿',
  `approve_user_id` bigint DEFAULT NULL COMMENT '瀹℃壒浜篒D',
  `approve_user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '瀹℃壒浜哄?鍚',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID（关联shebao_subsidy_person.id）',
  `subsidy_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '补贴类型（1失地居民补贴 2被征地居民补贴 3拆迁居民补贴 4村干部补贴）',
  `subsidy_record_id` bigint NOT NULL COMMENT '补贴身份记录ID（关联具体补贴类型表的ID）',
  `distribution_amount` decimal(10,2) NOT NULL COMMENT '发放金额',
  `distribution_date` date DEFAULT NULL COMMENT '发放日期',
  `distribution_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '1' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
  `review_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '审核说明',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_subsidy_person_id` (`subsidy_person_id`) USING BTREE,
  KEY `idx_subsidy_type` (`subsidy_type`) USING BTREE,
  KEY `idx_distribution_status` (`distribution_status`) USING BTREE,
  KEY `idx_distribution_date` (`distribution_date`) USING BTREE,
  KEY `idx_batch_no` (`batch_no`) USING BTREE,
  KEY `idx_approval_status` (`approval_status`) USING BTREE,
  KEY `idx_batch_type` (`batch_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='补贴发放记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_subsidy_distribution`
--

LOCK TABLES `shebao_subsidy_distribution` WRITE;
/*!40000 ALTER TABLE `shebao_subsidy_distribution` DISABLE KEYS */;
INSERT INTO `shebao_subsidy_distribution` VALUES (1,NULL,'first','draft',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1000000,'1',3,100.00,NULL,'1',NULL,'0','0','admin','2025-10-19 20:59:18','','2025-10-19 20:59:17',NULL),(2,NULL,'first','draft',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1000000,'3',1,123.45,NULL,'2','同意','0','0','admin','2025-10-19 21:16:55','admin','2025-10-19 21:19:58',NULL),(3,NULL,'first','draft',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1000003,'2',3,100.00,NULL,'1',NULL,'0','0','admin','2025-10-19 21:20:41','','2025-10-19 21:20:40',NULL),(4,NULL,'first','draft',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1000001,'1',1,111.00,'2025-10-19','4','同意','0','0','admin','2025-10-19 21:21:37','admin','2025-10-19 21:21:49',NULL),(5,NULL,'first','draft',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1000001,'1',1,0.00,NULL,'1',NULL,'0','0','admin','2025-10-19 22:04:58','','2025-10-19 22:04:58','111'),(6,NULL,'first','draft',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1000001,'1',1,100.00,NULL,'1',NULL,'0','0','admin','2025-10-19 22:13:58','','2025-10-19 22:13:58','111'),(7,NULL,'first','draft',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1000001,'1',1,200.00,NULL,'1',NULL,'0','0','admin','2025-10-19 22:15:01','','2025-10-19 22:15:00',NULL),(8,NULL,'first','draft',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1000001,'1',1,30.00,NULL,'1',NULL,'0','0','admin','2025-10-19 22:15:20','','2025-10-19 22:15:20','333'),(9,NULL,'first','draft',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1000001,'1',1,0.01,'2026-03-03','4','审核通过','0','0','admin','2025-10-19 22:33:03','admin','2026-03-03 14:12:09','12321'),(10,NULL,'first','draft',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1000001,'1',1,0.01,'2026-03-03','4','','0','0','admin','2025-10-19 22:49:37','admin','2026-03-03 00:57:10','1231231231'),(11,NULL,'first','draft',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1000023,'1',9,500.00,'2026-03-03','4','ok','0','0','operator01','2026-03-03 01:01:40','admin','2026-03-03 01:01:40',NULL),(12,NULL,'first','draft',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1000024,'1',10,600.00,'2026-03-03','4','ok','0','0','operator01','2026-03-03 01:07:05','admin','2026-03-03 01:07:05',NULL);
/*!40000 ALTER TABLE `shebao_subsidy_distribution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_subsidy_person`
--

DROP TABLE IF EXISTS `shebao_subsidy_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_subsidy_person` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户编号（系统生成，自增主键）',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '性别（1男 2女）',
  `id_card_no` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证号',
  `certification_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'uncertified' COMMENT '璁よ瘉鐘舵?(uncertified/april/october)',
  `last_certification_date` date DEFAULT NULL COMMENT '鏈?悗璁よ瘉鏃ユ湡',
  `suspension_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '鏆傚仠鐘舵?(0姝ｅ父1鏆傚仠)',
  `suspension_start_date` date DEFAULT NULL COMMENT '鏆傚仠寮??鏃ユ湡',
  `suspension_reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '鏆傚仠鍘熷洜(death/uncertified/imprisonment/missing/other)',
  `suspension_end_date` date DEFAULT NULL COMMENT '鏆傚仠缁撴潫鏃ユ湡',
  `suspension_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '鏆傚仠澶囨敞',
  `birthday` date NOT NULL COMMENT '生日',
  `household_registration` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '户籍所在地',
  `home_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `is_alive` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' COMMENT '是否健在（0否 1是）',
  `death_date` date DEFAULT NULL COMMENT '死亡时间',
  `is_village_coop_member` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' COMMENT '是否村合作经济组织成员（0否 1是）',
  `street_office_id` bigint DEFAULT NULL COMMENT '所属街道办ID',
  `village_committee_id` bigint DEFAULT NULL COMMENT '所属村委会ID',
  `user_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户编号（格式：001-002-0001）',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `approval_status` varchar(20) DEFAULT 'approved' COMMENT '审批状态(draft/pending_review/approved/rejected)',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_id_card_no` (`id_card_no`) USING BTREE,
  UNIQUE KEY `uk_user_code` (`user_code`) USING BTREE,
  KEY `idx_name` (`name`) USING BTREE,
  KEY `idx_phone` (`phone`) USING BTREE,
  KEY `idx_street_office_id` (`street_office_id`) USING BTREE,
  KEY `idx_village_committee_id` (`village_committee_id`) USING BTREE,
  KEY `idx_is_alive` (`is_alive`) USING BTREE,
  KEY `idx_certification_status` (`certification_status`) USING BTREE,
  KEY `idx_suspension_status` (`suspension_status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1000028 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='被补贴人基础信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_subsidy_person`
--

LOCK TABLES `shebao_subsidy_person` WRITE;
/*!40000 ALTER TABLE `shebao_subsidy_person` DISABLE KEYS */;
INSERT INTO `shebao_subsidy_person` VALUES (1000000,'高老汉','2','131131193411120001','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1934-11-12','河北廊坊','12321','13112345678','1',NULL,'1',1,2,'0010020001','0','approved','0','admin','2025-09-28 01:49:29','admin','2025-10-20 00:44:29',NULL),(1000001,'刘三娘','2','131131198411120021','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1984-11-12','廊坊','12321','13012345678','1',NULL,'1',1,2,'0010020002','0','approved','0','admin','2025-09-28 22:43:24','admin','2026-02-03 18:54:29',NULL),(1000002,'刘大娘','2','131131195611120005','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1956-11-12','廊坊','三里屯','13012345678','1',NULL,'1',2,3,'0020010001','0','approved','0','admin','2025-09-28 22:59:15','admin','2025-10-20 00:42:21',NULL),(1000003,'王老五','2','131131195411120000','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1955-11-12','廊坊',NULL,'13012345678','1',NULL,'1',2,3,'0020010002','0','approved','0','admin','2025-09-28 23:43:51','','2025-10-19 14:07:01',NULL),(1000004,'刘玉连','1','132801195611252658','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1956-11-25','河北省廊坊市','asdfadsf','130000000000','1',NULL,'1',1,1,'0010010001','0','approved','0','admin','2025-10-11 10:58:40','admin','2025-10-20 00:44:07',NULL),(1000005,'高老汉','2','13280119450112452X','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1945-01-12','啊','和平区','13333333333','0','2026-02-01','1',1,1,'0010010002','0','approved','0','admin','2025-10-11 16:28:26','admin','2026-02-03 15:07:53',NULL),(1000006,'大毛','2','13280119650112422X','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1965-01-12','河北','家里','13601243558','1','2025-10-20','1',1,1,'0010010003','0','approved','0','admin','2025-10-19 13:55:31','admin','2025-10-19 15:13:58','123'),(1000015,'刘二娘','2','131131198411120004','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1984-11-12','廊坊','111','13012345678','1',NULL,'1',1,1,'0010010004','0','approved','0','admin','2025-10-19 17:50:27','','2025-10-19 17:50:26',NULL),(1000016,'刘大娘','1','131131198411120012','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1984-11-12','廊坊','1111','13012345678','1',NULL,'1',1,1,'0010010005','0','approved','0','admin','2025-10-19 17:54:10','','2025-10-19 17:54:26',NULL),(1000018,'张老汉','1','142725198101111110','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'2026-01-01','廊坊',NULL,'188110948483','0','2026-01-19','1',1,1,'0010010006','0','approved','0','admin','2026-01-25 14:33:43','admin','2026-01-25 14:35:37',NULL),(1000019,'张一一','1','132801197209052622','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1972-09-05','第一村','第一村','13555556666','1',NULL,'1',1,1,'0010010007','0','approved','0','admin','2026-01-30 09:20:23','admin','2026-02-03 15:07:12',NULL),(1000020,'测试张三','1','130102199001011234','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1990-01-01','河北省廊坊市银河南路街道第一村',NULL,'13800138000','1',NULL,'1',1,1,'0010010008','0','approved','0','operator01','2026-03-02 16:36:54','','2026-03-02 16:36:54',NULL),(1000021,'测试审核流程','1','130102199501011234','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1995-01-01','河北廊坊',NULL,'13900139000','1',NULL,'1',1,1,'0010010009','0','approved','0','operator01','2026-03-03 00:55:23','','2026-03-03 00:55:23',NULL),(1000022,'王五测试','1','130102198801011234','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1988-01-01','河北廊坊',NULL,'13700137000','1',NULL,'1',1,1,'0010010010','0','approved','0','operator01','2026-03-03 00:57:09','','2026-03-03 00:57:08',NULL),(1000023,'赵六全流程','1','130102199201011234','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1992-01-01','河北廊坊',NULL,'13600136000','1',NULL,'1',1,1,'0010010011','0','approved','0','operator01','2026-03-03 01:01:39','','2026-03-03 01:01:39',NULL),(1000024,'全流程测试李四','2','130102199301011234','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1993-01-01','河北廊坊',NULL,'13500135000','1',NULL,'1',2,3,'0020010003','0','approved','0','operator01','2026-03-03 01:07:04','','2026-03-03 01:07:03',NULL),(1000025,'陈建国','1','130105196803152847','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1968-03-15','河北省廊坊市安次区明路街道',NULL,'13912345678','1',NULL,'1',2,1,'0020010004','0','approved','0','operator01','2026-03-03 01:39:19','','2026-03-03 01:39:19',NULL),(1000026,'刘美华','2','130102197506283924','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1975-06-28','河北省廊坊',NULL,'13698765432','1',NULL,'1',2,3,'0020010005','0','rejected','0','operator01','2026-03-03 04:03:59','','2026-03-03 04:03:58',NULL),(1000027,'王德明','1','130102196607152847','uncertified',NULL,'0',NULL,NULL,NULL,NULL,'1966-07-15','河北省廊坊市',NULL,'13700001111','1',NULL,'1',1,1,NULL,'0','approved','0','','2026-03-03 16:18:22','','2026-03-03 16:18:22',NULL);
/*!40000 ALTER TABLE `shebao_subsidy_person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_teacher_subsidy`
--

DROP TABLE IF EXISTS `shebao_teacher_subsidy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_teacher_subsidy` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID（关联shebao_subsidy_person.id）',
  `school_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '学校名称',
  `teaching_years` int DEFAULT NULL COMMENT '任教年限',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_subsidy_person_id` (`subsidy_person_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  CONSTRAINT `fk_teacher_subsidy_person` FOREIGN KEY (`subsidy_person_id`) REFERENCES `shebao_subsidy_person` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='教龄补助登记表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_teacher_subsidy`
--

LOCK TABLES `shebao_teacher_subsidy` WRITE;
/*!40000 ALTER TABLE `shebao_teacher_subsidy` DISABLE KEYS */;
/*!40000 ALTER TABLE `shebao_teacher_subsidy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_village_committee`
--

DROP TABLE IF EXISTS `shebao_village_committee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_village_committee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `street_office_id` bigint NOT NULL COMMENT '所属街道办ID',
  `village_code` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '村委会编码（3位数字，如：002）',
  `village_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '村委会名称',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '详细地址',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_street_village_code` (`street_office_id`,`village_code`) USING BTREE,
  KEY `idx_street_office_id` (`street_office_id`) USING BTREE,
  KEY `idx_village_name` (`village_name`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='村委会信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_village_committee`
--

LOCK TABLES `shebao_village_committee` WRITE;
/*!40000 ALTER TABLE `shebao_village_committee` DISABLE KEYS */;
INSERT INTO `shebao_village_committee` VALUES (1,1,'001','第一村',NULL,NULL,NULL,1,'0','0','admin','2025-10-18 23:56:54','','2025-10-18 23:56:54',NULL),(2,1,'002','第二村',NULL,NULL,NULL,2,'0','0','admin','2025-10-18 23:56:54','','2025-10-18 23:56:54',NULL),(3,2,'001','东村',NULL,NULL,NULL,1,'0','0','admin','2025-10-18 23:56:54','','2025-10-18 23:56:54',NULL);
/*!40000 ALTER TABLE `shebao_village_committee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_village_official`
--

DROP TABLE IF EXISTS `shebao_village_official`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_village_official` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `total_service_years` decimal(5,2) DEFAULT '0.00' COMMENT '累计任职年限',
  `has_violation` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '是否违法乱纪或判刑（0否 1是）',
  `subsidy_amount` decimal(10,2) DEFAULT '0.00' COMMENT '补贴金额',
  `distribution_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_subsidy_person_id` (`subsidy_person_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='村干部信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_village_official`
--

LOCK TABLES `shebao_village_official` WRITE;
/*!40000 ALTER TABLE `shebao_village_official` DISABLE KEYS */;
INSERT INTO `shebao_village_official` VALUES (1,1000000,10.00,'0',0.00,'0','0','0','admin','2025-09-28 22:18:31','admin','2025-09-28 23:50:52',NULL),(2,1000004,0.00,'0',0.00,'0','0','0','admin','2025-10-11 10:58:40','','2025-10-11 10:58:39',NULL),(3,1000000,0.00,'0',0.00,'0','0','0','admin','2025-10-16 15:37:18','','2025-10-16 15:37:18',NULL);
/*!40000 ALTER TABLE `shebao_village_official` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shebao_village_official_position`
--

DROP TABLE IF EXISTS `shebao_village_official_position`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_village_official_position` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `village_official_id` bigint NOT NULL COMMENT '村干部信息ID',
  `position` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任职职位（1书记或主任 2书记兼主任 3村"两委"其他成员）',
  `start_date` date NOT NULL COMMENT '上任时间',
  `end_date` date DEFAULT NULL COMMENT '卸任时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_village_official_id` (`village_official_id`) USING BTREE,
  KEY `idx_position` (`position`) USING BTREE,
  KEY `idx_start_date` (`start_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='村干部任职信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_village_official_position`
--

LOCK TABLES `shebao_village_official_position` WRITE;
/*!40000 ALTER TABLE `shebao_village_official_position` DISABLE KEYS */;
INSERT INTO `shebao_village_official_position` VALUES (1,1,'1','2025-08-01','2025-09-30','0','2','admin','2025-09-28 22:18:31','','2025-09-28 23:50:52',NULL),(2,1,'2','2025-09-30','2026-09-30','0','2','admin','2025-09-28 22:18:31','','2025-09-28 23:50:52',NULL),(3,1,'2','2025-09-30','2026-09-30','0','0','admin','2025-09-28 23:50:52','','2025-09-28 23:50:52','1'),(4,1,'1','2025-08-01','2025-09-30','0','0','admin','2025-09-28 23:50:52','','2025-09-28 23:50:52','2'),(5,2,'1','2018-07-07','2025-10-15','0','0','admin','2025-10-11 10:58:40','','2025-10-11 10:58:39',NULL);
/*!40000 ALTER TABLE `shebao_village_official_position` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2025-09-24 22:11:39','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2025-09-24 22:11:39','',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2025-09-24 22:11:39','',NULL,'深色主题theme-dark，浅色主题theme-light'),(4,'账号自助-验证码开关','sys.account.captchaEnabled','false','Y','admin','2025-09-24 22:11:39','admin','2025-09-26 17:27:23','是否开启验证码功能（true开启，false关闭）'),(5,'账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y','admin','2025-09-24 22:11:39','',NULL,'是否开启注册用户功能（true开启，false关闭）'),(6,'用户登录-黑名单列表','sys.login.blackIPList','','Y','admin','2025-09-24 22:11:39','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),(7,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1','Y','admin','2025-09-24 22:11:39','',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框'),(8,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0','Y','admin','2025-09-24 22:11:39','',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框'),(100,'社保-年平均工资','shebao.average.annual.salary','56724','N','admin','2025-09-28 22:47:18','',NULL,NULL);
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (100,0,'0','社保局',0,'','','','0','0','admin','2025-09-24 22:11:35','admin','2025-09-26 17:36:50'),(101,100,'0,100','办公室',1,'','','','0','0','admin','2025-09-24 22:11:35','admin','2025-09-26 18:29:55'),(102,100,'0,100','长沙分公司',2,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(103,101,'0,100,101','研发部门',1,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(104,101,'0,100,101','市场部门',2,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(105,101,'0,100,101','测试部门',3,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(106,101,'0,100,101','财务部门',4,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(107,101,'0,100,101','运维部门',5,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(108,102,'0,100,102','市场部门',1,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(109,102,'0,100,102','财务部门',2,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=165 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,1,'男','1','sys_user_sex','','','Y','0','admin','2025-09-24 22:11:38','',NULL,'性别男'),(2,2,'女','2','sys_user_sex','','','N','0','admin','2025-09-24 22:11:38','',NULL,'性别女'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2025-09-24 22:11:38','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2025-09-24 22:11:38','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2025-09-24 22:11:38','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2025-09-24 22:11:38','',NULL,'停用状态'),(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2025-09-24 22:11:38','',NULL,'正常状态'),(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2025-09-24 22:11:38','',NULL,'停用状态'),(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2025-09-24 22:11:38','',NULL,'默认分组'),(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2025-09-24 22:11:38','',NULL,'系统分组'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2025-09-24 22:11:38','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2025-09-24 22:11:38','',NULL,'系统默认否'),(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2025-09-24 22:11:38','',NULL,'通知'),(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2025-09-24 22:11:38','',NULL,'公告'),(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2025-09-24 22:11:38','',NULL,'正常状态'),(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2025-09-24 22:11:38','',NULL,'关闭状态'),(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2025-09-24 22:11:38','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2025-09-24 22:11:38','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2025-09-24 22:11:38','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2025-09-24 22:11:38','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2025-09-24 22:11:38','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2025-09-24 22:11:38','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2025-09-24 22:11:38','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2025-09-24 22:11:39','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2025-09-24 22:11:39','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2025-09-24 22:11:39','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2025-09-24 22:11:39','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2025-09-24 22:11:39','',NULL,'停用状态'),(100,1,'失地居民补贴','1','subsidy_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'失地居民补贴'),(101,2,'被征地居民补贴','2','subsidy_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'被征地居民补贴'),(102,3,'拆迁居民补贴','3','subsidy_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'拆迁居民补贴'),(103,4,'村干部补贴','4','subsidy_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'村干部补贴'),(104,1,'未发放','0','distribution_status',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'未发放'),(105,2,'待审核','1','distribution_status',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'待审核'),(106,3,'待发放','2','distribution_status',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'待发放'),(107,4,'已驳回','3','distribution_status',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'已驳回'),(108,5,'已发放','4','distribution_status',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'已发放'),(109,1,'提交审核','1','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'提交审核'),(110,2,'审核通过','2','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'审核通过'),(111,3,'审核驳回','3','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'审核驳回'),(112,4,'发放','4','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'发放'),(113,5,'重新提交','5','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'重新提交'),(114,1,'失地居民补贴','1','subsidy_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:13','',NULL,'失地居民补贴'),(115,2,'被征地居民补贴','2','subsidy_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:13','',NULL,'被征地居民补贴'),(116,3,'拆迁居民补贴','3','subsidy_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:13','',NULL,'拆迁居民补贴'),(117,4,'村干部补贴','4','subsidy_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:13','',NULL,'村干部补贴'),(118,1,'未发放','0','distribution_status',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'未发放'),(119,2,'待审核','1','distribution_status',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'待审核'),(120,3,'待发放','2','distribution_status',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'待发放'),(121,4,'已驳回','3','distribution_status',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'已驳回'),(122,5,'已发放','4','distribution_status',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'已发放'),(123,1,'提交审核','1','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'提交审核'),(124,2,'审核通过','2','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'审核通过'),(125,3,'审核驳回','3','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'审核驳回'),(126,4,'发放','4','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'发放'),(127,5,'重新提交','5','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'重新提交'),(128,1,'失地居民','land_loss','shebao_subsidy_type',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'失地居民养老补贴'),(129,2,'被征地居民','expropriatee','shebao_subsidy_type',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'被征地居民养老补贴'),(130,3,'拆迁居民','demolition','shebao_subsidy_type',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'拆迁居民养老补贴'),(131,4,'村干部','village_official','shebao_subsidy_type',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'村干部养老补贴'),(132,5,'教师补贴','teacher','shebao_subsidy_type',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'教师养老补贴'),(133,1,'草稿','draft','approval_status',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'草稿状态'),(134,2,'待复核','pending_review','approval_status',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'待复核状态'),(135,3,'待审批','pending_approve','approval_status',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'待审批状态'),(136,4,'待财务','pending_finance','approval_status',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'待财务处理状态'),(137,5,'已提交银行','submitted_bank','approval_status',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'已提交银行状态'),(138,6,'已发放','distributed','approval_status',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'已发放状态'),(139,7,'已驳回','rejected','approval_status',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'已驳回状态'),(140,1,'首次发放','first','batch_type',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'首次发放批次'),(141,2,'二次发放','second','batch_type',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'二次发放批次'),(142,3,'三次发放','third','batch_type',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'三次发放批次'),(143,1,'死亡','death','suspension_reason',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'待遇人死亡'),(144,2,'未认证','uncertified','suspension_reason',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'未完成年度认证'),(145,3,'服刑','imprisonment','suspension_reason',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'正在服刑'),(146,4,'失踪','missing','suspension_reason',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'人员失踪'),(147,5,'其他','other','suspension_reason',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'其他原因'),(148,1,'未认证','uncertified','certification_status',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'未完成年度认证'),(149,2,'已认证(4月)','april','certification_status',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'已完成4月认证'),(150,3,'已认证(10月)','october','certification_status',NULL,NULL,'N','0','admin','2026-01-19 10:08:27','',NULL,'已完成10月认证'),(151,1,'人员登记','person_register','business_type',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'人员登记审批'),(152,2,'待遇核定','benefit_determination','business_type',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'待遇核定审批'),(153,3,'支付计划','payment_plan','business_type',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'支付计划审批'),(154,4,'信息修改','info_modify','business_type',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'信息修改审批'),(155,1,'提交','submit','approval_operation',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'提交审核'),(156,2,'复核','review','approval_operation',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'复核操作'),(157,3,'审批','approve','approval_operation',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'审批操作'),(158,4,'驳回','reject','approval_operation',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'驳回操作'),(159,5,'撤销','cancel','approval_operation',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'撤销操作'),(160,1,'账号不存在','account_not_exist','fail_reason',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'银行账户不存在'),(161,2,'余额不足','insufficient_balance','fail_reason',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'账户余额不足'),(162,3,'信息错误','info_error','fail_reason',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'账户信息错误'),(163,4,'银行系统错误','bank_error','fail_reason',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'银行系统错误'),(164,5,'其他','other','fail_reason',NULL,NULL,'N','0','admin','2026-01-19 10:08:28','',NULL,'其他原因');
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE KEY `dict_type` (`dict_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=208 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex','0','admin','2025-09-24 22:11:38','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','admin','2025-09-24 22:11:38','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','admin','2025-09-24 22:11:38','',NULL,'系统开关列表'),(4,'任务状态','sys_job_status','0','admin','2025-09-24 22:11:38','',NULL,'任务状态列表'),(5,'任务分组','sys_job_group','0','admin','2025-09-24 22:11:38','',NULL,'任务分组列表'),(6,'系统是否','sys_yes_no','0','admin','2025-09-24 22:11:38','',NULL,'系统是否列表'),(7,'通知类型','sys_notice_type','0','admin','2025-09-24 22:11:38','',NULL,'通知类型列表'),(8,'通知状态','sys_notice_status','0','admin','2025-09-24 22:11:38','',NULL,'通知状态列表'),(9,'操作类型','sys_oper_type','0','admin','2025-09-24 22:11:38','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','admin','2025-09-24 22:11:38','',NULL,'登录状态列表'),(100,'补贴类型','subsidy_type','0','admin','2025-10-15 21:31:43','',NULL,'补贴类型列表'),(101,'发放状态','distribution_status','0','admin','2025-10-15 21:31:43','',NULL,'补贴发放状态列表'),(102,'发放操作类型','distribution_operation_type','0','admin','2025-10-15 21:31:43','',NULL,'发放操作类型列表'),(200,'补贴类型','shebao_subsidy_type','0','admin','2026-01-19 10:08:27','',NULL,'各类养老补贴类型'),(201,'审批状态','approval_status','0','admin','2026-01-19 10:08:27','',NULL,'审批流程状态'),(202,'批次类型','batch_type','0','admin','2026-01-19 10:08:27','',NULL,'发放批次类型'),(203,'暂停原因','suspension_reason','0','admin','2026-01-19 10:08:27','',NULL,'待遇暂停原因'),(204,'认证状态','certification_status','0','admin','2026-01-19 10:08:27','',NULL,'待遇认证状态'),(205,'业务类型','business_type','0','admin','2026-01-19 10:08:27','',NULL,'审批流程业务类型'),(206,'审批操作','approval_operation','0','admin','2026-01-19 10:08:28','',NULL,'审批流程操作类型'),(207,'失败原因','fail_reason','0','admin','2026-01-19 10:08:28','',NULL,'银行发放失败原因');
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job`
--

LOCK TABLES `sys_job` WRITE;
/*!40000 ALTER TABLE `sys_job` DISABLE KEYS */;
INSERT INTO `sys_job` VALUES (1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','admin','2025-09-24 22:11:39','',NULL,''),(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(\'ry\')','0/15 * * * * ?','3','1','1','admin','2025-09-24 22:11:39','',NULL,''),(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','admin','2025-09-24 22:11:39','',NULL,'');
/*!40000 ALTER TABLE `sys_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='定时任务调度日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job_log`
--

LOCK TABLES `sys_job_log` WRITE;
/*!40000 ALTER TABLE `sys_job_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_logininfor`
--

DROP TABLE IF EXISTS `sys_logininfor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE,
  KEY `idx_sys_logininfor_s` (`status`) USING BTREE,
  KEY `idx_sys_logininfor_lt` (`login_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=360 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_logininfor`
--

LOCK TABLES `sys_logininfor` WRITE;
/*!40000 ALTER TABLE `sys_logininfor` DISABLE KEYS */;
INSERT INTO `sys_logininfor` VALUES (100,'admin','127.0.0.1','内网IP','Safari','Mac OS X','0','登录成功','2025-09-24 22:41:20'),(101,'admin','127.0.0.1','内网IP','Safari','Mac OS X','1','验证码已失效','2025-09-26 17:26:01'),(102,'admin','127.0.0.1','内网IP','Safari','Mac OS X','0','登录成功','2025-09-26 17:26:08'),(103,'admin','127.0.0.1','内网IP','Safari','Mac OS X','0','退出成功','2025-09-26 17:35:41'),(104,'admin','127.0.0.1','内网IP','Safari','Mac OS X','0','登录成功','2025-09-26 17:35:44'),(105,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-26 18:26:31'),(106,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-27 10:36:24'),(107,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-27 11:46:01'),(108,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-27 16:01:41'),(109,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-27 20:42:47'),(110,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-28 01:42:49'),(111,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-28 12:15:16'),(112,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-28 21:42:56'),(113,'admin','127.0.0.1','内网IP','Safari','Mac OS X','0','登录成功','2025-09-29 00:00:16'),(114,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2025-09-29 10:38:29'),(115,'admin','222.128.127.244','XX XX','Chrome 12','Mac OS X','0','登录成功','2025-09-29 10:53:52'),(116,'admin','223.104.3.162','XX XX','Chrome 14','Windows 10','0','登录成功','2025-09-29 11:04:30'),(117,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-29 11:21:35'),(118,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-29 20:55:43'),(119,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','1','用户不存在/密码错误','2025-10-10 10:20:22'),(120,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2025-10-10 10:20:26'),(121,'admin','101.29.186.209','XX XX','Chrome 10','Windows 10','0','登录成功','2025-10-10 10:27:34'),(122,'admin','111.55.150.107','XX XX','Chrome Mobile','Android 1.x','0','登录成功','2025-10-10 10:28:45'),(123,'admin','27.128.48.201','XX XX','Chrome Mobile','Android 1.x','0','登录成功','2025-10-10 10:31:31'),(124,'admin','222.128.127.244','XX XX','Safari','Mac OS X','0','登录成功','2025-10-10 10:41:15'),(125,'admin','60.10.47.214','XX XX','Chrome Mobile','Android 1.x','0','登录成功','2025-10-10 10:41:36'),(126,'admin','60.10.47.214','XX XX','Apple WebKit','Mac OS X (iPhone)','0','登录成功','2025-10-10 10:53:16'),(127,'admin','222.128.127.244','XX XX','Chrome 13','Mac OS X','0','登录成功','2025-10-10 10:54:34'),(128,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-10 10:55:58'),(129,'admin','117.136.47.166','XX XX','Apple WebKit','Mac OS X (iPhone)','0','登录成功','2025-10-10 10:59:59'),(130,'admin','223.160.137.16','XX XX','Chrome Mobile','Android 1.x','0','登录成功','2025-10-10 11:00:01'),(131,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-10 11:00:52'),(132,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-10 11:01:57'),(133,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-10 12:19:01'),(134,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2025-10-10 16:09:09'),(135,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2025-10-10 16:17:13'),(136,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-11 10:30:12'),(137,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2025-10-11 10:36:17'),(138,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-11 14:25:38'),(139,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-11 16:11:43'),(140,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2025-10-11 16:20:15'),(141,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2025-10-11 17:10:52'),(142,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-13 09:44:07'),(143,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2025-10-13 10:00:56'),(144,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2025-10-13 15:33:31'),(145,'admin','120.244.162.197','XX XX','Chrome 14','Windows 10','0','登录成功','2025-10-14 10:18:02'),(146,'admin','222.128.127.244','XX XX','Safari','Mac OS X','0','登录成功','2025-10-14 10:47:45'),(147,'admin','222.128.127.244','XX XX','Safari','Mac OS X','0','退出成功','2025-10-14 10:50:42'),(148,'admin','222.128.127.244','XX XX','Safari','Mac OS X','0','登录成功','2025-10-14 10:51:08'),(149,'admin','222.128.127.244','XX XX','Safari','Mac OS X','0','退出成功','2025-10-14 10:52:25'),(150,'admin','222.128.127.244','XX XX','Safari','Mac OS X','0','登录成功','2025-10-14 10:52:34'),(151,'admin','27.128.21.83','XX XX','Apple WebKit','Mac OS X (iPhone)','1','用户不存在/密码错误','2025-10-14 10:56:06'),(152,'admin','27.128.21.83','XX XX','Apple WebKit','Mac OS X (iPhone)','1','用户不存在/密码错误','2025-10-14 10:56:15'),(153,'admin','27.128.21.83','XX XX','Apple WebKit','Mac OS X (iPhone)','1','用户不存在/密码错误','2025-10-14 10:59:04'),(154,'admin','27.128.21.83','XX XX','Apple WebKit','Mac OS X (iPhone)','0','登录成功','2025-10-14 11:01:56'),(155,'admin','60.10.47.214','XX XX','Apple WebKit','Mac OS X (iPhone)','0','登录成功','2025-10-14 11:52:05'),(156,'admin','60.10.59.193','XX XX','Chrome Mobile','Android 1.x','1','用户不存在/密码错误','2025-10-15 09:53:49'),(157,'admin','60.10.59.193','XX XX','Chrome Mobile','Android 1.x','0','登录成功','2025-10-15 09:54:09'),(158,'admin','60.10.47.214','XX XX','Apple WebKit','Mac OS X (iPhone)','0','登录成功','2025-10-15 09:55:48'),(159,'admin','60.10.47.214','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2025-10-15 15:12:56'),(160,'admin','60.10.47.214','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-15 15:16:11'),(161,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','1','用户不存在/密码错误','2025-10-15 21:42:01'),(162,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-15 21:44:24'),(163,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-16 10:43:58'),(164,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-16 14:14:07'),(165,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-16 15:34:38'),(166,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-18 21:08:05'),(167,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 00:26:07'),(168,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 10:36:54'),(169,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 12:38:41'),(170,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 13:48:54'),(171,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 15:13:26'),(172,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 17:15:27'),(173,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 18:27:47'),(174,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 20:25:41'),(175,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-20 00:25:09'),(176,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2025-10-20 01:04:28'),(177,'admin','36.98.90.0','XX XX','Chrome 13','Android 1.x','0','登录成功','2025-10-20 08:59:56'),(178,'admin','60.10.47.214','XX XX','Apple WebKit','Mac OS X (iPhone)','0','登录成功','2025-10-20 09:40:57'),(179,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-20 11:00:46'),(180,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2025-10-20 13:51:35'),(181,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-20 14:23:08'),(182,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2025-10-20 16:19:49'),(183,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2025-10-20 16:49:03'),(184,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-20 16:50:26'),(185,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-21 15:13:47'),(186,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-22 15:00:27'),(187,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-01-19 09:06:30'),(188,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-01-19 21:28:49'),(189,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-01-19 22:01:26'),(190,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-01-19 22:57:19'),(191,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-01-20 02:09:22'),(192,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-01-20 02:44:09'),(193,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-01-24 22:59:37'),(194,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-01-24 23:39:02'),(195,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-01-25 01:40:08'),(196,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-01-25 14:30:36'),(197,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','1','用户不存在/密码错误','2026-01-28 11:55:17'),(198,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2026-01-28 11:55:27'),(199,'admin','183.241.78.6','XX XX','Chrome 14','Windows 10','0','登录成功','2026-01-28 11:57:39'),(200,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','1','用户不存在/密码错误','2026-01-28 13:07:06'),(201,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2026-01-28 13:07:16'),(202,'admin','183.241.78.6','XX XX','Chrome 14','Windows 10','0','登录成功','2026-01-28 13:20:01'),(203,'admin','183.241.78.6','XX XX','Chrome 14','Windows 10','0','登录成功','2026-01-28 18:40:03'),(204,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-01-28 22:09:59'),(205,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2026-01-28 22:16:48'),(206,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2026-01-28 22:34:44'),(207,'admin','183.241.78.6','XX XX','Chrome 14','Windows 10','0','登录成功','2026-01-29 01:04:59'),(208,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2026-01-29 13:32:34'),(209,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2026-01-29 14:59:44'),(210,'admin','60.175.255.125','XX XX','Firefox 14','Windows 10','1','用户不存在/密码错误','2026-01-29 16:44:18'),(211,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2026-01-30 09:17:01'),(212,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2026-02-02 11:29:47'),(213,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2026-02-03 15:04:18'),(214,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2026-02-03 16:44:33'),(215,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-02-03 18:38:06'),(216,'admin','111.55.210.93','XX XX','Chrome 14','Windows 10','0','登录成功','2026-02-03 18:39:04'),(217,'admin','120.229.49.22','XX XX','Chrome 14','Windows 10','0','登录成功','2026-02-04 09:57:06'),(218,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2026-02-04 10:28:27'),(219,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2026-02-04 10:28:36'),(220,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2026-02-04 10:29:16'),(221,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2026-02-25 15:12:17'),(222,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2026-02-25 15:12:19'),(223,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2026-02-25 15:12:30'),(224,'lihongshan','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2026-02-25 15:12:39'),(225,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2026-02-25 15:58:06'),(226,'admin','39.144.79.134','XX XX','Chrome 14','Windows 10','0','登录成功','2026-02-25 15:59:27'),(227,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2026-02-25 16:01:24'),(228,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2026-02-25 16:01:26'),(229,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2026-02-25 16:01:54'),(230,'admin','120.244.162.77','XX XX','Chrome 14','Windows 10','0','登录成功','2026-02-25 20:24:45'),(231,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2026-02-26 14:30:55'),(232,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2026-02-26 14:30:58'),(233,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2026-02-26 14:31:13'),(234,'admin','127.0.0.1','内网IP','Chrome 14','Windows 10','0','登录成功','2026-03-02 23:12:00'),(235,'admin','127.0.0.1','内网IP','Chrome 14','Linux','1','用户不存在/密码错误','2026-03-02 15:48:57'),(236,'admin','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-02 15:50:09'),(237,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:00:46'),(238,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','用户不存在/密码错误','2026-03-02 16:00:49'),(239,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:02:07'),(240,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','用户不存在/密码错误','2026-03-02 16:02:09'),(241,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','1','用户不存在/密码错误','2026-03-02 16:06:08'),(242,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','1','用户不存在/密码错误','2026-03-02 16:06:08'),(243,'approver01','127.0.0.1','内网IP','Downloading Tool','Unknown','1','用户不存在/密码错误','2026-03-02 16:06:08'),(244,'finance01','127.0.0.1','内网IP','Downloading Tool','Unknown','1','用户不存在/密码错误','2026-03-02 16:06:08'),(245,'auditor01','127.0.0.1','内网IP','Downloading Tool','Unknown','1','用户不存在/密码错误','2026-03-02 16:06:08'),(246,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','1','用户不存在/密码错误','2026-03-02 16:06:15'),(247,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:06:28'),(248,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:06:36'),(249,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:06:36'),(250,'approver01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:06:36'),(251,'finance01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:06:36'),(252,'auditor01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:06:37'),(253,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:06:45'),(254,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:07:01'),(255,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:07:10'),(256,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:07:23'),(257,'admin','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-02 16:11:14'),(258,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-02 16:12:00'),(259,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:18:57'),(260,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:23:37'),(261,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-02 16:26:34'),(262,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:50:18'),(263,'approver01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:50:19'),(264,'finance01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:50:19'),(265,'auditor01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 16:50:19'),(266,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-02 16:51:42'),(267,'reviewer01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-02 16:52:46'),(268,'reviewer01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-02 17:00:35'),(269,'approver01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-02 17:01:25'),(270,'approver01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-02 17:03:00'),(271,'finance01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-02 17:03:58'),(272,'finance01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-02 17:05:18'),(273,'auditor01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-02 17:06:21'),(274,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 17:21:51'),(275,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 17:22:53'),(276,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','用户不存在/密码错误','2026-03-02 17:22:55'),(277,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 17:23:07'),(278,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 17:24:02'),(279,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-02 17:25:21'),(280,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','1','用户不存在/密码错误','2026-03-02 17:25:23'),(281,'auditor01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-02 17:26:50'),(282,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-02 17:29:15'),(283,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-02 17:30:38'),(284,'reviewer01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-02 17:31:30'),(285,'reviewer01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-02 17:32:31'),(286,'auditor01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-02 17:33:48'),(287,'auditor01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-02 17:34:54'),(288,'admin','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-02 17:36:10'),(289,'admin','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 00:36:23'),(290,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 00:55:23'),(291,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 00:55:23'),(292,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 00:55:41'),(293,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 00:56:07'),(294,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 00:57:08'),(295,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 00:57:08'),(296,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 00:57:08'),(297,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 00:57:09'),(298,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 00:57:09'),(299,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 00:57:09'),(300,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:01:39'),(301,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:01:39'),(302,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:01:39'),(303,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:01:39'),(304,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:01:40'),(305,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:01:40'),(306,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:07:03'),(307,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:07:03'),(308,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:07:04'),(309,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:07:04'),(310,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:07:04'),(311,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:07:04'),(312,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 01:30:05'),(313,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:40:36'),(314,'reviewer01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 01:43:57'),(315,'reviewer01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-03 01:46:35'),(316,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 01:47:49'),(317,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:49:30'),(318,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 01:52:02'),(319,'reviewer01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 01:54:33'),(320,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 03:54:26'),(321,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 04:03:29'),(322,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 04:03:29'),(323,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 04:03:58'),(324,'reviewer01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 04:06:39'),(325,'reviewer01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-03 04:08:19'),(326,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 04:09:52'),(327,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-03 04:36:48'),(328,'auditor01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 04:37:54'),(329,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 05:08:41'),(330,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-03 05:13:21'),(331,'reviewer01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 05:14:59'),(332,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 13:58:35'),(333,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-03 14:06:16'),(334,'admin','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 14:07:19'),(335,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 14:31:06'),(336,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 14:32:53'),(337,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 14:39:21'),(338,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 14:55:41'),(339,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 16:17:53'),(340,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 16:17:53'),(341,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 16:18:22'),(342,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 16:18:47'),(343,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 16:18:48'),(344,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 16:20:50'),(345,'operator01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 16:24:17'),(346,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 16:58:10'),(347,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 16:58:11'),(348,'approver01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 16:58:11'),(349,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 16:58:11'),(350,'finance01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 16:58:11'),(351,'operator01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 17:04:34'),(352,'reviewer01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 17:04:34'),(353,'approver01','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 17:04:34'),(354,'admin','127.0.0.1','内网IP','Downloading Tool','Unknown','0','登录成功','2026-03-03 17:04:34'),(355,'reviewer01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 17:08:44'),(356,'reviewer01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-03 17:10:17'),(357,'approver01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 17:11:53'),(358,'approver01','127.0.0.1','内网IP','Chrome 14','Linux','0','退出成功','2026-03-03 17:13:35'),(359,'finance01','127.0.0.1','内网IP','Chrome 14','Linux','0','登录成功','2026-03-03 17:14:54');
/*!40000 ALTER TABLE `sys_logininfor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '路由名称',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3299 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,101,'system',NULL,'','',1,0,'M','0','0','','system','admin','2025-09-24 22:11:36','admin','2025-09-27 10:44:48','系统管理目录'),(2,'系统监控',0,201,'monitor',NULL,'','',1,0,'M','1','1','','monitor','admin','2025-09-24 22:11:36','admin','2025-09-29 00:14:30','系统监控目录'),(3,'系统工具',0,202,'tool',NULL,'','',1,0,'M','1','1','','tool','admin','2025-09-24 22:11:36','admin','2025-09-29 00:13:11','系统工具目录'),(100,'用户管理',1,1,'user','system/user/index','','',1,0,'C','0','0','system:user:list','user','admin','2025-09-24 22:11:36','',NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','system/role/index','','',1,0,'C','0','0','system:role:list','peoples','admin','2025-09-24 22:11:36','',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','system/menu/index','','',1,0,'C','0','0','system:menu:list','tree-table','admin','2025-09-24 22:11:36','',NULL,'菜单管理菜单'),(103,'部门管理',1,4,'dept','system/dept/index','','',1,0,'C','0','0','system:dept:list','tree','admin','2025-09-24 22:11:36','',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','system/post/index','','',1,0,'C','1','1','system:post:list','post','admin','2025-09-24 22:11:36','admin','2025-09-29 00:15:17','岗位管理菜单'),(105,'字典管理',1,6,'dict','system/dict/index','','',1,0,'C','0','0','system:dict:list','dict','admin','2025-09-24 22:11:36','',NULL,'字典管理菜单'),(106,'参数设置',1,7,'config','system/config/index','','',1,0,'C','0','0','system:config:list','edit','admin','2025-09-24 22:11:36','',NULL,'参数设置菜单'),(107,'通知公告',1,8,'notice','system/notice/index','','',1,0,'C','1','1','system:notice:list','message','admin','2025-09-24 22:11:36','admin','2025-09-29 00:15:08','通知公告菜单'),(108,'日志管理',1,9,'log','','','',1,0,'M','0','0','','log','admin','2025-09-24 22:11:36','',NULL,'日志管理菜单'),(109,'在线用户',2,1,'online','monitor/online/index','','',1,0,'C','0','0','monitor:online:list','online','admin','2025-09-24 22:11:36','',NULL,'在线用户菜单'),(110,'定时任务',2,2,'job','monitor/job/index','','',1,0,'C','0','0','monitor:job:list','job','admin','2025-09-24 22:11:36','',NULL,'定时任务菜单'),(111,'数据监控',2,3,'druid','monitor/druid/index','','',1,0,'C','0','0','monitor:druid:list','druid','admin','2025-09-24 22:11:36','',NULL,'数据监控菜单'),(112,'服务监控',2,4,'server','monitor/server/index','','',1,0,'C','0','0','monitor:server:list','server','admin','2025-09-24 22:11:36','',NULL,'服务监控菜单'),(113,'缓存监控',2,5,'cache','monitor/cache/index','','',1,0,'C','0','0','monitor:cache:list','redis','admin','2025-09-24 22:11:36','',NULL,'缓存监控菜单'),(114,'缓存列表',2,6,'cacheList','monitor/cache/list','','',1,0,'C','0','0','monitor:cache:list','redis-list','admin','2025-09-24 22:11:36','',NULL,'缓存列表菜单'),(115,'表单构建',3,1,'build','tool/build/index','','',1,0,'C','0','0','tool:build:list','build','admin','2025-09-24 22:11:36','',NULL,'表单构建菜单'),(116,'代码生成',3,2,'gen','tool/gen/index','','',1,0,'C','0','0','tool:gen:list','code','admin','2025-09-24 22:11:36','',NULL,'代码生成菜单'),(117,'系统接口',3,3,'swagger','tool/swagger/index','','',1,0,'C','0','0','tool:swagger:list','swagger','admin','2025-09-24 22:11:36','',NULL,'系统接口菜单'),(500,'操作日志',108,1,'operlog','monitor/operlog/index','','',1,0,'C','0','0','monitor:operlog:list','form','admin','2025-09-24 22:11:36','',NULL,'操作日志菜单'),(501,'登录日志',108,2,'logininfor','monitor/logininfor/index','','',1,0,'C','0','0','monitor:logininfor:list','logininfor','admin','2025-09-24 22:11:36','',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'','','','',1,0,'F','0','0','system:user:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1001,'用户新增',100,2,'','','','',1,0,'F','0','0','system:user:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1002,'用户修改',100,3,'','','','',1,0,'F','0','0','system:user:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1003,'用户删除',100,4,'','','','',1,0,'F','0','0','system:user:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1004,'用户导出',100,5,'','','','',1,0,'F','0','0','system:user:export','#','admin','2025-09-24 22:11:36','',NULL,''),(1005,'用户导入',100,6,'','','','',1,0,'F','0','0','system:user:import','#','admin','2025-09-24 22:11:36','',NULL,''),(1006,'重置密码',100,7,'','','','',1,0,'F','0','0','system:user:resetPwd','#','admin','2025-09-24 22:11:36','',NULL,''),(1007,'角色查询',101,1,'','','','',1,0,'F','0','0','system:role:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1008,'角色新增',101,2,'','','','',1,0,'F','0','0','system:role:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1009,'角色修改',101,3,'','','','',1,0,'F','0','0','system:role:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1010,'角色删除',101,4,'','','','',1,0,'F','0','0','system:role:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1011,'角色导出',101,5,'','','','',1,0,'F','0','0','system:role:export','#','admin','2025-09-24 22:11:36','',NULL,''),(1012,'菜单查询',102,1,'','','','',1,0,'F','0','0','system:menu:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1013,'菜单新增',102,2,'','','','',1,0,'F','0','0','system:menu:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1014,'菜单修改',102,3,'','','','',1,0,'F','0','0','system:menu:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1015,'菜单删除',102,4,'','','','',1,0,'F','0','0','system:menu:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1016,'部门查询',103,1,'','','','',1,0,'F','0','0','system:dept:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1017,'部门新增',103,2,'','','','',1,0,'F','0','0','system:dept:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1018,'部门修改',103,3,'','','','',1,0,'F','0','0','system:dept:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1019,'部门删除',103,4,'','','','',1,0,'F','0','0','system:dept:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1020,'岗位查询',104,1,'','','','',1,0,'F','0','0','system:post:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1021,'岗位新增',104,2,'','','','',1,0,'F','0','0','system:post:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1022,'岗位修改',104,3,'','','','',1,0,'F','0','0','system:post:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1023,'岗位删除',104,4,'','','','',1,0,'F','0','0','system:post:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1024,'岗位导出',104,5,'','','','',1,0,'F','0','0','system:post:export','#','admin','2025-09-24 22:11:36','',NULL,''),(1025,'字典查询',105,1,'#','','','',1,0,'F','0','0','system:dict:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1026,'字典新增',105,2,'#','','','',1,0,'F','0','0','system:dict:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1027,'字典修改',105,3,'#','','','',1,0,'F','0','0','system:dict:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1028,'字典删除',105,4,'#','','','',1,0,'F','0','0','system:dict:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1029,'字典导出',105,5,'#','','','',1,0,'F','0','0','system:dict:export','#','admin','2025-09-24 22:11:36','',NULL,''),(1030,'参数查询',106,1,'#','','','',1,0,'F','0','0','system:config:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1031,'参数新增',106,2,'#','','','',1,0,'F','0','0','system:config:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1032,'参数修改',106,3,'#','','','',1,0,'F','0','0','system:config:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1033,'参数删除',106,4,'#','','','',1,0,'F','0','0','system:config:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1034,'参数导出',106,5,'#','','','',1,0,'F','0','0','system:config:export','#','admin','2025-09-24 22:11:36','',NULL,''),(1035,'公告查询',107,1,'#','','','',1,0,'F','0','0','system:notice:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1036,'公告新增',107,2,'#','','','',1,0,'F','0','0','system:notice:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1037,'公告修改',107,3,'#','','','',1,0,'F','0','0','system:notice:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1038,'公告删除',107,4,'#','','','',1,0,'F','0','0','system:notice:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1039,'操作查询',500,1,'#','','','',1,0,'F','0','0','monitor:operlog:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1040,'操作删除',500,2,'#','','','',1,0,'F','0','0','monitor:operlog:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1041,'日志导出',500,3,'#','','','',1,0,'F','0','0','monitor:operlog:export','#','admin','2025-09-24 22:11:36','',NULL,''),(1042,'登录查询',501,1,'#','','','',1,0,'F','0','0','monitor:logininfor:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1043,'登录删除',501,2,'#','','','',1,0,'F','0','0','monitor:logininfor:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1044,'日志导出',501,3,'#','','','',1,0,'F','0','0','monitor:logininfor:export','#','admin','2025-09-24 22:11:37','',NULL,''),(1045,'账户解锁',501,4,'#','','','',1,0,'F','0','0','monitor:logininfor:unlock','#','admin','2025-09-24 22:11:37','',NULL,''),(1046,'在线查询',109,1,'#','','','',1,0,'F','0','0','monitor:online:query','#','admin','2025-09-24 22:11:37','',NULL,''),(1047,'批量强退',109,2,'#','','','',1,0,'F','0','0','monitor:online:batchLogout','#','admin','2025-09-24 22:11:37','',NULL,''),(1048,'单条强退',109,3,'#','','','',1,0,'F','0','0','monitor:online:forceLogout','#','admin','2025-09-24 22:11:37','',NULL,''),(1049,'任务查询',110,1,'#','','','',1,0,'F','0','0','monitor:job:query','#','admin','2025-09-24 22:11:37','',NULL,''),(1050,'任务新增',110,2,'#','','','',1,0,'F','0','0','monitor:job:add','#','admin','2025-09-24 22:11:37','',NULL,''),(1051,'任务修改',110,3,'#','','','',1,0,'F','0','0','monitor:job:edit','#','admin','2025-09-24 22:11:37','',NULL,''),(1052,'任务删除',110,4,'#','','','',1,0,'F','0','0','monitor:job:remove','#','admin','2025-09-24 22:11:37','',NULL,''),(1053,'状态修改',110,5,'#','','','',1,0,'F','0','0','monitor:job:changeStatus','#','admin','2025-09-24 22:11:37','',NULL,''),(1054,'任务导出',110,6,'#','','','',1,0,'F','0','0','monitor:job:export','#','admin','2025-09-24 22:11:37','',NULL,''),(1055,'生成查询',116,1,'#','','','',1,0,'F','0','0','tool:gen:query','#','admin','2025-09-24 22:11:37','',NULL,''),(1056,'生成修改',116,2,'#','','','',1,0,'F','0','0','tool:gen:edit','#','admin','2025-09-24 22:11:37','',NULL,''),(1057,'生成删除',116,3,'#','','','',1,0,'F','0','0','tool:gen:remove','#','admin','2025-09-24 22:11:37','',NULL,''),(1058,'导入代码',116,4,'#','','','',1,0,'F','0','0','tool:gen:import','#','admin','2025-09-24 22:11:37','',NULL,''),(1059,'预览代码',116,5,'#','','','',1,0,'F','0','0','tool:gen:preview','#','admin','2025-09-24 22:11:37','',NULL,''),(1060,'生成代码',116,6,'#','','','',1,0,'F','0','0','tool:gen:code','#','admin','2025-09-24 22:11:37','',NULL,''),(2000,'基础数据',0,1,'base',NULL,NULL,'',1,0,'M','0','0',NULL,'dict','admin','2025-09-27 10:41:46','',NULL,''),(2002,'行政区划',2000,1,'division','shebao/administrativeDivision/index',NULL,'行政区划',1,0,'C','1','1','shebao:administrativeDivision:list','tree-table','admin','2025-09-27 20:46:54','admin','2025-10-19 22:36:45',''),(2004,'人员信息',2000,0,'baseinfo','shebao/subsidyPerson/index',NULL,'人员信息',1,0,'C','0','0','','people','admin','2025-09-28 01:46:52','admin','2025-09-29 11:30:50',''),(2009,'补贴发放',0,4,'assign',NULL,NULL,'',1,0,'M','0','0','','form','admin','2025-09-29 11:15:25','admin','2025-09-29 11:19:05',''),(2011,'统计查询',2009,2,'residentQuery','shebao/residentQuery/index',NULL,'统计查询',1,0,'C','0','0','','search','admin','2025-09-29 11:18:05','admin','2025-10-20 00:27:29',''),(2012,'补贴设置',2000,3,'settings','shebao/config/index',NULL,'补贴设置',1,0,'C','0','0','','dict','admin','2025-09-29 20:59:55','admin','2025-09-29 21:01:21',''),(2013,'补贴发放记录',2009,1,'subsidyDistribution','shebao/subsidyDistribution/index',NULL,'',1,0,'C','0','0','shebao:subsidyDistribution:list','money','admin','2025-10-15 21:31:43','admin','2025-10-18 21:26:53','补贴发放记录菜单'),(2014,'补贴发放记录查询',2013,1,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:query','#','admin','2025-10-15 21:31:43','',NULL,''),(2015,'补贴发放记录新增',2013,2,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:add','#','admin','2025-10-15 21:31:43','',NULL,''),(2016,'补贴发放记录删除',2013,3,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:remove','#','admin','2025-10-15 21:31:43','',NULL,''),(2017,'补贴发放审核',2013,4,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:approve','#','admin','2025-10-15 21:31:43','',NULL,''),(2018,'补贴发放驳回',2013,5,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:reject','#','admin','2025-10-15 21:31:43','',NULL,''),(2019,'补贴发放',2013,6,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:distribute','#','admin','2025-10-15 21:31:43','',NULL,''),(2020,'补贴重新提交',2013,7,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:resubmit','#','admin','2025-10-15 21:31:43','',NULL,''),(2021,'补贴发放记录导出',2013,8,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:export','#','admin','2025-10-15 21:31:43','',NULL,''),(2031,'街道办',2000,4,'streetOffice','shebao/streetOffice/index',NULL,'街道办',1,0,'C','0','0','shebao:streetOffice:list','tree-table','admin','2025-10-19 10:38:24','admin','2025-10-19 23:17:16',''),(2032,'村委会',2000,6,'villageCommittee','shebao/villageCommittee/index',NULL,'村委会',1,0,'C','0','0','shebao:villageCommittee:list','tree-table','admin','2025-10-19 10:39:40','admin','2025-10-19 22:37:17',''),(2078,'人员信息管理',0,1,'person',NULL,NULL,'',1,0,'M','0','0',NULL,'peoples','admin','2026-01-19 11:15:27','',NULL,'人员信息管理菜单'),(2079,'待遇核定管理',0,2,'benefit',NULL,NULL,'',1,0,'M','0','0',NULL,'documentation','admin','2026-01-19 11:15:27','',NULL,'待遇核定管理菜单'),(2080,'待遇管理',0,3,'management',NULL,NULL,'',1,0,'M','0','0',NULL,'edit','admin','2026-01-19 11:15:27','',NULL,'待遇管理菜单'),(2081,'支付结算',0,4,'payment',NULL,NULL,'',1,0,'M','0','0',NULL,'money','admin','2026-01-19 11:15:27','',NULL,'支付结算菜单'),(2082,'财务管理',0,5,'finance',NULL,NULL,'',1,0,'M','0','0',NULL,'guide','admin','2026-01-19 11:15:27','',NULL,'财务管理菜单'),(2083,'统计管理',0,6,'audit',NULL,NULL,'',1,0,'M','0','0','','chart','admin','2026-01-19 11:15:27','admin','2026-01-24 23:05:39','审计报表菜单'),(2084,'失地居民',2078,1,'lost-land','shebao/landLossResident/index',NULL,'',1,0,'C','0','0','shebao:person:landloss:list','#','admin','2026-01-19 11:15:27','admin','2026-01-20 00:15:12','失地居民登记菜单'),(2085,'被征地居民',2078,2,'expropriatee','shebao/expropriateeSubsidy/index',NULL,'',1,0,'C','0','0','shebao:person:expropriatee:list','#','admin','2026-01-19 11:15:27','admin','2026-01-20 00:15:37','被征地居民登记菜单'),(2086,'拆迁居民',2078,3,'demolition','shebao/expropriateeSubsidy/index',NULL,'',1,0,'C','0','0','shebao:person:demolition:list','#','admin','2026-01-19 11:15:27','admin','2026-01-20 00:15:59','拆迁居民登记菜单'),(2087,'村干部',2078,4,'village-official','shebao/villageOfficial/index',NULL,'',1,0,'C','0','0','shebao:person:official:list','#','admin','2026-01-19 11:15:27','admin','2026-01-20 00:16:43','村干部登记菜单'),(2088,'教龄补助',2078,5,'teacher','shebao/person/teacher/index',NULL,'',1,0,'C','0','0','shebao:person:teacher:list','#','admin','2026-01-19 11:15:28','admin','2026-01-24 23:01:43','教师补贴登记菜单'),(2089,'人员登记复核',2078,6,'review','shebao/person/review/index',NULL,'',1,0,'C','0','0','shebao:person:review:list','#','admin','2026-01-19 11:15:28','',NULL,'人员登记复核菜单'),(2090,'信息修改审批',2078,7,'modify','shebao/person/modify/index',NULL,'',1,0,'C','0','0','shebao:person:modify:list','#','admin','2026-01-19 11:15:28','',NULL,'信息修改审批菜单'),(2091,'预到龄发放通知',2079,1,'notice','shebao/benefit/notice/index',NULL,'',1,0,'C','0','0','shebao:benefit:notice:list','#','admin','2026-01-19 11:15:28','',NULL,'预到龄发放通知菜单'),(2092,'待遇核定',2079,2,'determination','shebao/benefit/determination/index',NULL,'',1,0,'C','0','0','shebao:benefit:determination:list','#','admin','2026-01-19 11:15:28','',NULL,'待遇核定菜单'),(2093,'核定审核',2079,3,'review','shebao/benefit/review/index',NULL,'',1,0,'C','0','0','shebao:benefit:review:list','#','admin','2026-01-19 11:15:28','',NULL,'核定审核菜单'),(2094,'发放信息修改',2080,1,'modify','shebao/management/modify/index',NULL,'',1,0,'C','0','0','shebao:benefit:modify:list','#','admin','2026-01-19 11:15:28','',NULL,'发放信息修改菜单'),(2095,'待遇暂停恢复',2080,2,'suspension','shebao/management/suspension/index',NULL,'',1,0,'C','0','0','shebao:benefit:suspension:list','#','admin','2026-01-19 11:15:28','',NULL,'待遇暂停恢复菜单'),(2096,'待遇认证',2080,3,'certification','shebao/management/certification/index',NULL,'',1,0,'C','0','0','shebao:benefit:certification:list','#','admin','2026-01-19 11:15:28','',NULL,'待遇认证菜单'),(2097,'支付计划生成',2081,1,'plan','shebao/payment/plan/index',NULL,'',1,0,'C','0','0','shebao:payment:plan:list','#','admin','2026-01-19 11:15:28','',NULL,'支付计划生成菜单'),(2098,'支付计划复核',2081,2,'review','shebao/payment/review/index',NULL,'',1,0,'C','0','0','shebao:payment:batch:review','#','admin','2026-01-19 11:15:28','',NULL,'支付计划复核菜单'),(2099,'支付计划审批',2081,3,'approve','shebao/payment/approve/index',NULL,'',1,0,'C','0','0','shebao:payment:batch:approve','#','admin','2026-01-19 11:15:28','',NULL,'支付计划审批菜单'),(2100,'上传财务系统',2081,4,'upload','shebao/payment/upload/index',NULL,'',1,0,'C','0','0','shebao:payment:batch:upload','#','admin','2026-01-19 11:15:28','',NULL,'上传财务系统菜单'),(2101,'批次管理',2082,1,'batch','shebao/finance/batch/index',NULL,'',1,0,'C','0','0','shebao:finance:batch:list','#','admin','2026-01-19 11:15:28','',NULL,'批次管理菜单'),(2102,'银行发放',2082,2,'bank','shebao/finance/bank/index',NULL,'',1,0,'C','0','0','shebao:finance:bank:list','#','admin','2026-01-19 11:15:28','',NULL,'银行发放菜单'),(2103,'失败处理',2082,3,'failure','shebao/finance/failure/index',NULL,'',1,0,'C','0','0','shebao:finance:failure:list','#','admin','2026-01-19 11:15:28','',NULL,'失败处理菜单'),(2104,'财务账户',2082,4,'account','shebao/finance/account/index',NULL,'',1,0,'C','0','0','shebao:finance:account:list','#','admin','2026-01-19 11:15:28','',NULL,'财务账户菜单'),(2105,'操作日志',2083,1,'operlog','shebao/audit/operlog/index',NULL,'',1,0,'C','0','0','shebao:audit:operlog:list','#','admin','2026-01-19 11:15:28','',NULL,'操作日志菜单'),(2106,'审批记录',2083,2,'approval','shebao/audit/approval/index',NULL,'',1,0,'C','0','0','shebao:audit:approval:list','#','admin','2026-01-19 11:15:28','',NULL,'审批记录菜单'),(2107,'发放明细表',2083,3,'detail','shebao/audit/detail/index',NULL,'',1,0,'C','0','0','shebao:audit:detail:list','#','admin','2026-01-19 11:15:28','',NULL,'发放明细表菜单'),(2108,'统计汇总',2083,4,'statistics','shebao/audit/statistics/index',NULL,'',1,0,'C','0','0','shebao:audit:statistics:list','#','admin','2026-01-19 11:15:28','',NULL,'统计汇总菜单'),(2110,'人员注销登记',2078,8,'cancel','shebao/person/cancel/index',NULL,'',1,0,'C','0','0','shebao:person:cancel:list','#','admin','2026-01-25 02:10:29','',NULL,'人员注销登记菜单'),(2111,'人员注销登记查询',2110,1,'#','',NULL,'',1,0,'F','0','0','shebao:person:cancel:query','#','admin','2026-01-25 02:13:31','',NULL,''),(2112,'人员注销登记新增',2110,2,'#','',NULL,'',1,0,'F','0','0','shebao:person:cancel:add','#','admin','2026-01-25 02:13:31','',NULL,''),(2113,'人员注销登记修改',2110,3,'#','',NULL,'',1,0,'F','0','0','shebao:person:cancel:edit','#','admin','2026-01-25 02:13:31','',NULL,''),(2114,'人员注销登记删除',2110,4,'#','',NULL,'',1,0,'F','0','0','shebao:person:cancel:remove','#','admin','2026-01-25 02:13:31','',NULL,''),(3001,'失地居民查询',2084,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:landLossResident:list','#','',NULL,'',NULL,''),(3002,'失地居民新增',2084,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:landLossResident:add','#','',NULL,'',NULL,''),(3003,'失地居民修改',2084,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:landLossResident:edit','#','',NULL,'',NULL,''),(3004,'失地居民删除',2084,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:landLossResident:remove','#','',NULL,'',NULL,''),(3005,'失地居民导出',2084,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:landLossResident:export','#','',NULL,'',NULL,''),(3006,'失地居民导入',2084,6,'',NULL,NULL,'',1,0,'F','0','0','shebao:landLossResident:import','#','',NULL,'',NULL,''),(3007,'失地居民详情',2084,7,'',NULL,NULL,'',1,0,'F','0','0','shebao:landLossResident:query','#','',NULL,'',NULL,''),(3011,'被征地居民查询',2085,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:expropriateeSubsidy:list','#','',NULL,'',NULL,''),(3012,'被征地居民新增',2085,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:expropriateeSubsidy:add','#','',NULL,'',NULL,''),(3013,'被征地居民修改',2085,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:expropriateeSubsidy:edit','#','',NULL,'',NULL,''),(3014,'被征地居民删除',2085,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:expropriateeSubsidy:remove','#','',NULL,'',NULL,''),(3015,'被征地居民导出',2085,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:expropriateeSubsidy:export','#','',NULL,'',NULL,''),(3016,'被征地居民导入',2085,6,'',NULL,NULL,'',1,0,'F','0','0','shebao:expropriateeSubsidy:import','#','',NULL,'',NULL,''),(3017,'被征地居民详情',2085,7,'',NULL,NULL,'',1,0,'F','0','0','shebao:expropriateeSubsidy:query','#','',NULL,'',NULL,''),(3021,'拆迁居民查询',2086,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:demolitionResident:list','#','',NULL,'',NULL,''),(3022,'拆迁居民新增',2086,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:demolitionResident:add','#','',NULL,'',NULL,''),(3023,'拆迁居民修改',2086,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:demolitionResident:edit','#','',NULL,'',NULL,''),(3024,'拆迁居民删除',2086,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:demolitionResident:remove','#','',NULL,'',NULL,''),(3025,'拆迁居民导出',2086,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:demolitionResident:export','#','',NULL,'',NULL,''),(3026,'拆迁居民导入',2086,6,'',NULL,NULL,'',1,0,'F','0','0','shebao:demolitionResident:import','#','',NULL,'',NULL,''),(3027,'拆迁居民详情',2086,7,'',NULL,NULL,'',1,0,'F','0','0','shebao:demolitionResident:query','#','',NULL,'',NULL,''),(3031,'村干部查询',2087,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:villageOfficial:list','#','',NULL,'',NULL,''),(3032,'村干部新增',2087,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:villageOfficial:add','#','',NULL,'',NULL,''),(3033,'村干部修改',2087,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:villageOfficial:edit','#','',NULL,'',NULL,''),(3034,'村干部删除',2087,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:villageOfficial:remove','#','',NULL,'',NULL,''),(3035,'村干部导出',2087,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:villageOfficial:export','#','',NULL,'',NULL,''),(3036,'村干部导入',2087,6,'',NULL,NULL,'',1,0,'F','0','0','shebao:villageOfficial:import','#','',NULL,'',NULL,''),(3037,'村干部详情',2087,7,'',NULL,NULL,'',1,0,'F','0','0','shebao:villageOfficial:query','#','',NULL,'',NULL,''),(3041,'教龄补助查询',2088,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:teacher:list','#','',NULL,'',NULL,''),(3042,'教龄补助新增',2088,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:teacher:add','#','',NULL,'',NULL,''),(3043,'教龄补助修改',2088,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:teacher:edit','#','',NULL,'',NULL,''),(3044,'教龄补助删除',2088,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:teacher:remove','#','',NULL,'',NULL,''),(3045,'教龄补助详情',2088,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:teacher:query','#','',NULL,'',NULL,''),(3046,'教龄补助提交',2088,6,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:teacher:submit','#','',NULL,'',NULL,''),(3051,'人员登记复核列表',2089,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:review:list','#','',NULL,'',NULL,''),(3052,'人员登记复核通过',2089,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:review:approve','#','',NULL,'',NULL,''),(3053,'人员登记复核驳回',2089,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:review:reject','#','',NULL,'',NULL,''),(3054,'人员登记复核详情',2089,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:review:query','#','',NULL,'',NULL,''),(3061,'信息修改列表',2090,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:modify:list','#','',NULL,'',NULL,''),(3062,'信息修改新增',2090,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:modify:add','#','',NULL,'',NULL,''),(3063,'信息修改提交',2090,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:modify:submit','#','',NULL,'',NULL,''),(3064,'信息修改复核',2090,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:modify:review','#','',NULL,'',NULL,''),(3065,'信息修改审批',2090,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:modify:approve','#','',NULL,'',NULL,''),(3066,'信息修改驳回',2090,6,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:modify:reject','#','',NULL,'',NULL,''),(3067,'信息修改详情',2090,7,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:modify:query','#','',NULL,'',NULL,''),(3071,'人员登记列表',2078,10,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:list','#','',NULL,'',NULL,''),(3072,'人员登记新增',2078,11,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:add','#','',NULL,'',NULL,''),(3073,'人员登记修改',2078,12,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:edit','#','',NULL,'',NULL,''),(3074,'人员登记删除',2078,13,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:remove','#','',NULL,'',NULL,''),(3075,'人员登记导出',2078,14,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:export','#','',NULL,'',NULL,''),(3076,'人员登记详情',2078,15,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:query','#','',NULL,'',NULL,''),(3077,'人员登记提交',2078,16,'',NULL,NULL,'',1,0,'F','0','0','shebao:person:submit','#','',NULL,'',NULL,''),(3081,'预到龄通知列表',2091,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:notice:list','#','',NULL,'',NULL,''),(3082,'预到龄通知生成',2091,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:notice:generate','#','',NULL,'',NULL,''),(3091,'待遇核定列表',2092,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:determination:list','#','',NULL,'',NULL,''),(3092,'待遇核定新增',2092,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:determination:add','#','',NULL,'',NULL,''),(3093,'待遇核定修改',2092,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:determination:edit','#','',NULL,'',NULL,''),(3094,'待遇核定删除',2092,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:determination:remove','#','',NULL,'',NULL,''),(3095,'待遇核定详情',2092,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:determination:query','#','',NULL,'',NULL,''),(3096,'待遇核定提交',2092,6,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:determination:submit','#','',NULL,'',NULL,''),(3097,'待遇核定打印',2092,7,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:determination:print','#','',NULL,'',NULL,''),(3101,'核定审核列表',2093,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:review:list','#','',NULL,'',NULL,''),(3102,'核定审核通过',2093,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:review:approve','#','',NULL,'',NULL,''),(3103,'核定审核驳回',2093,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:review:reject','#','',NULL,'',NULL,''),(3111,'发放信息修改列表',2094,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:modify:list','#','',NULL,'',NULL,''),(3112,'发放信息修改操作',2094,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:benefit:modify:edit','#','',NULL,'',NULL,''),(3121,'待遇暂停恢复列表',2095,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:management:suspension:list','#','',NULL,'',NULL,''),(3122,'待遇暂停',2095,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:management:suspension:suspend','#','',NULL,'',NULL,''),(3123,'待遇恢复',2095,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:management:suspension:resume','#','',NULL,'',NULL,''),(3124,'待遇暂停详情',2095,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:management:suspension:query','#','',NULL,'',NULL,''),(3131,'待遇认证列表',2096,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:management:certification:list','#','',NULL,'',NULL,''),(3132,'待遇认证提交',2096,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:management:certification:submit','#','',NULL,'',NULL,''),(3133,'待遇认证批量',2096,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:management:certification:batch','#','',NULL,'',NULL,''),(3134,'待遇认证详情',2096,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:management:certification:query','#','',NULL,'',NULL,''),(3135,'待遇认证历史',2096,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:management:certification:history','#','',NULL,'',NULL,''),(3141,'支付计划列表',2097,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:plan:list','#','',NULL,'',NULL,''),(3142,'支付计划生成',2097,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:plan:generate','#','',NULL,'',NULL,''),(3143,'支付计划删除',2097,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:plan:remove','#','',NULL,'',NULL,''),(3144,'支付计划详情',2097,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:plan:query','#','',NULL,'',NULL,''),(3145,'支付计划审批',2097,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:plan:approve','#','',NULL,'',NULL,''),(3146,'支付计划驳回',2097,6,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:plan:reject','#','',NULL,'',NULL,''),(3147,'支付计划批量',2097,7,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:plan:batchGenerate','#','',NULL,'',NULL,''),(3151,'支付复核列表',2098,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:review:list','#','',NULL,'',NULL,''),(3152,'支付复核通过',2098,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:review:approve','#','',NULL,'',NULL,''),(3153,'支付复核驳回',2098,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:review:reject','#','',NULL,'',NULL,''),(3161,'支付审批列表',2099,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:approve:list','#','',NULL,'',NULL,''),(3162,'支付审批通过',2099,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:approve:approve','#','',NULL,'',NULL,''),(3163,'支付审批驳回',2099,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:approve:reject','#','',NULL,'',NULL,''),(3171,'上传财务系统',2100,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:batch:upload','#','',NULL,'',NULL,''),(3181,'批次管理列表',2101,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:batch:list','#','',NULL,'',NULL,''),(3182,'批次管理详情',2101,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:batch:query','#','',NULL,'',NULL,''),(3183,'批次提交银行',2101,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:batch:submit','#','',NULL,'',NULL,''),(3184,'批次导入结果',2101,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:batch:import','#','',NULL,'',NULL,''),(3185,'批次完成',2101,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:batch:complete','#','',NULL,'',NULL,''),(3191,'银行发放列表',2102,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:bank:list','#','',NULL,'',NULL,''),(3192,'银行发放提交',2102,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:bank:submit','#','',NULL,'',NULL,''),(3193,'银行发放导入',2102,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:bank:import','#','',NULL,'',NULL,''),(3201,'失败处理列表',2103,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:failure:list','#','',NULL,'',NULL,''),(3202,'失败处理详情',2103,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:failure:query','#','',NULL,'',NULL,''),(3203,'失败重新发放',2103,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:failure:retry','#','',NULL,'',NULL,''),(3204,'失败批量重发',2103,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:failure:batchRetry','#','',NULL,'',NULL,''),(3205,'失败手动处理',2103,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:failure:manual','#','',NULL,'',NULL,''),(3211,'财务账户列表',2104,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:account:list','#','',NULL,'',NULL,''),(3212,'财务账户新增',2104,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:account:add','#','',NULL,'',NULL,''),(3213,'财务账户修改',2104,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:account:edit','#','',NULL,'',NULL,''),(3214,'财务账户删除',2104,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:account:remove','#','',NULL,'',NULL,''),(3215,'财务账户详情',2104,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:finance:account:query','#','',NULL,'',NULL,''),(3221,'操作日志列表',2105,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:operlog:list','#','',NULL,'',NULL,''),(3222,'操作日志详情',2105,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:operlog:query','#','',NULL,'',NULL,''),(3223,'操作日志清空',2105,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:operlog:remove','#','',NULL,'',NULL,''),(3231,'审批记录列表',2106,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:approval:list','#','',NULL,'',NULL,''),(3232,'审批记录详情',2106,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:approval:query','#','',NULL,'',NULL,''),(3241,'发放明细列表',2107,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:detail:list','#','',NULL,'',NULL,''),(3242,'发放明细导出',2107,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:detail:export','#','',NULL,'',NULL,''),(3251,'统计概览',2108,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:statistics:overview','#','',NULL,'',NULL,''),(3252,'统计按类型',2108,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:statistics:byType','#','',NULL,'',NULL,''),(3253,'统计按街道',2108,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:statistics:byStreet','#','',NULL,'',NULL,''),(3254,'统计按村',2108,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:statistics:byVillage','#','',NULL,'',NULL,''),(3255,'统计趋势',2108,5,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:statistics:trend','#','',NULL,'',NULL,''),(3256,'统计列表',2108,6,'',NULL,NULL,'',1,0,'F','0','0','shebao:audit:statistics:list','#','',NULL,'',NULL,''),(3261,'街道办查询',2031,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:streetOffice:list','#','',NULL,'',NULL,''),(3262,'街道办详情',2031,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:streetOffice:query','#','',NULL,'',NULL,''),(3263,'街道办下拉',2031,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:streetOffice:selectList','#','',NULL,'',NULL,''),(3271,'村委会查询',2032,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:villageCommittee:list','#','',NULL,'',NULL,''),(3272,'村委会详情',2032,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:villageCommittee:query','#','',NULL,'',NULL,''),(3273,'村委会下拉',2032,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:villageCommittee:selectList','#','',NULL,'',NULL,''),(3281,'被补贴人查询',2004,1,'',NULL,NULL,'',1,0,'F','0','0','shebao:subsidyPerson:list','#','',NULL,'',NULL,''),(3282,'被补贴人详情',2004,2,'',NULL,NULL,'',1,0,'F','0','0','shebao:subsidyPerson:query','#','',NULL,'',NULL,''),(3283,'被补贴人新增',2004,3,'',NULL,NULL,'',1,0,'F','0','0','shebao:subsidyPerson:add','#','',NULL,'',NULL,''),(3284,'被补贴人修改',2004,4,'',NULL,NULL,'',1,0,'F','0','0','shebao:subsidyPerson:edit','#','',NULL,'',NULL,''),(3291,'支付批次列表',2081,10,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:batch:list','#','',NULL,'',NULL,''),(3292,'支付批次详情',2081,11,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:batch:query','#','',NULL,'',NULL,''),(3293,'支付批次新增',2081,12,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:batch:add','#','',NULL,'',NULL,''),(3294,'支付批次修改',2081,13,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:batch:edit','#','',NULL,'',NULL,''),(3295,'支付批次删除',2081,14,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:batch:remove','#','',NULL,'',NULL,''),(3296,'支付批次提交',2081,15,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:batch:submit','#','',NULL,'',NULL,''),(3297,'支付批次复核',2081,16,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:batch:review','#','',NULL,'',NULL,''),(3298,'支付批次审批',2081,17,'',NULL,NULL,'',1,0,'F','0','0','shebao:payment:batch:approve','#','',NULL,'',NULL,'');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (1,'温馨提醒：2018-07-01 新版本发布啦','2',_binary '新版本内容','0','admin','2025-09-24 22:11:39','admin','2025-09-29 00:12:20','管理员'),(2,'维护通知：2018-07-01 系统凌晨维护','1',_binary '<p>维护内容</p>','0','admin','2025-09-24 22:11:39','admin','2025-09-29 00:12:26','管理员');
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  KEY `idx_sys_oper_log_bt` (`business_type`) USING BTREE,
  KEY `idx_sys_oper_log_s` (`status`) USING BTREE,
  KEY `idx_sys_oper_log_ot` (`oper_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=399 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
INSERT INTO `sys_oper_log` VALUES (287,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2025-09-28 01:43:38\",\"icon\":\"peoples\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2003,\"menuName\":\"补贴居民\",\"menuType\":\"M\",\"orderNum\":3,\"params\":{},\"parentId\":0,\"path\":\"personold\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-19 23:09:36',160),(288,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/person/landloss/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2084,\"menuName\":\"失地居民\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2078,\"path\":\"landloss\",\"perms\":\"shebao:person:landloss:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-19 23:55:42',203),(289,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/person/expropriatee/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2085,\"menuName\":\"被征地居民\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2078,\"path\":\"expropriatee\",\"perms\":\"shebao:person:expropriatee:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-19 23:55:53',181),(290,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/person/demolition/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2086,\"menuName\":\"拆迁居民\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2078,\"path\":\"demolition\",\"perms\":\"shebao:person:demolition:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-19 23:56:03',140),(291,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/person/official/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2087,\"menuName\":\"村干部\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2078,\"path\":\"official\",\"perms\":\"shebao:person:official:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-19 23:56:12',142),(292,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/person/teacher/index\",\"createTime\":\"2026-01-19 11:15:28\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2088,\"menuName\":\"教师补贴\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2078,\"path\":\"teacher\",\"perms\":\"shebao:person:teacher:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-19 23:56:23',196),(293,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/person/landloss/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2084,\"menuName\":\"失地居民\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2078,\"path\":\"lost-land\",\"perms\":\"shebao:person:landloss:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:14:51',313),(294,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/landLossResident/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2084,\"menuName\":\"失地居民\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2078,\"path\":\"lost-land\",\"perms\":\"shebao:person:landloss:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:15:12',152),(295,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/expropriateeSubsidy/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2085,\"menuName\":\"被征地居民\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2078,\"path\":\"expropriatee\",\"perms\":\"shebao:person:expropriatee:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:15:38',86),(296,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/expropriateeSubsidy/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2086,\"menuName\":\"拆迁居民\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2078,\"path\":\"demolition\",\"perms\":\"shebao:person:demolition:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:16:01',337),(297,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/villageOfficial/index\",\"createTime\":\"2025-09-28 12:22:16\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2008,\"menuName\":\"村干部\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2003,\"path\":\"village-official\",\"perms\":\"\",\"routeName\":\"村干部\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:16:17',118),(298,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/person/official/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2087,\"menuName\":\"村干部\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2078,\"path\":\"village-official\",\"perms\":\"shebao:person:official:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:16:26',435),(299,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/villageOfficial/index\",\"createTime\":\"2025-09-28 12:22:16\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2008,\"menuName\":\"村干部\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2003,\"path\":\"village-official\",\"perms\":\"\",\"routeName\":\"村干部\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:16:34',213),(300,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/villageOfficial/index\",\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2087,\"menuName\":\"村干部\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2078,\"path\":\"village-official\",\"perms\":\"shebao:person:official:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:16:43',257),(301,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/api/system/menu/2003','127.0.0.1','内网IP','2003','{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}',0,NULL,'2026-01-20 00:25:21',40),(302,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/api/system/menu/2005','127.0.0.1','内网IP','2005','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2026-01-20 00:25:30',85),(303,'角色管理',2,'com.ruoyi.web.controller.system.SysRoleController.edit()','PUT',1,'admin','社保局','/api/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2025-09-24 22:11:36\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2009,2004,2002,2078,2084,2085,2086,2087,2088,2089,2090,2011],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:25:56',381),(304,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/api/system/menu/2005','127.0.0.1','内网IP','2005','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:26:14',104),(305,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/api/system/menu/2006','127.0.0.1','内网IP','2006','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:26:19',121),(306,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/api/system/menu/2007','127.0.0.1','内网IP','2007','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:26:25',167),(307,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/api/system/menu/2008','127.0.0.1','内网IP','2008','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:26:29',106),(308,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/api/system/menu/2003','127.0.0.1','内网IP','2003','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-20 00:26:34',159),(309,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/person/teacher/index\",\"createTime\":\"2026-01-19 11:15:28\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2088,\"menuName\":\"教龄补助\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2078,\"path\":\"teacher\",\"perms\":\"shebao:person:teacher:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-24 23:01:44',137),(310,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2026-01-19 11:15:27\",\"icon\":\"chart\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2083,\"menuName\":\"统计管理\",\"menuType\":\"M\",\"orderNum\":6,\"params\":{},\"parentId\":0,\"path\":\"audit\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-24 23:05:39',127),(311,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'admin','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1934-11-12\",\"compensationCompleteTime\":\"2026-01-13\",\"gender\":\"1\",\"homeAddress\":\"廊坊地址\",\"householdRegistration\":\"河北廊坊\",\"idCardNo\":\"142729999999999922\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2026-01-23\",\"name\":\"高老汉\",\"personExists\":false,\"phone\":\"13112345679\",\"recognitionTime\":\"2026-01-27\",\"streetOfficeId\":1,\"userCode\":\"0010020001\",\"villageCommitteeId\":2}',NULL,1,'\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010020001\' for key \'shebao_subsidy_person.uk_user_code\'\r\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\r\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\r\n### The error occurred while setting parameters\r\n### SQL: INSERT INTO shebao_subsidy_person  ( name, gender, id_card_no, birthday, household_registration,  phone, is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?,  ?, ?,  ?, ?, ?, ?, ?, ?,  ?  )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010020001\' for key \'shebao_subsidy_person.uk_user_code\'\n; Duplicate entry \'0010020001\' for key \'shebao_subsidy_person.uk_user_code\'','2026-01-25 14:32:26',478),(312,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'admin','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"2026-01-01\",\"compensationCompleteTime\":\"2026-01-26\",\"gender\":\"1\",\"homeAddress\":\"北京\",\"householdRegistration\":\"廊坊\",\"idCardNo\":\"142725198101111110\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2026-01-25\",\"name\":\"张老汉\",\"personExists\":false,\"phone\":\"188110948483\",\"recognitionTime\":\"2026-01-27\",\"streetOfficeId\":1,\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-25 14:33:44',603),(313,'人员注销登记',1,'com.ruoyi.shebao.controller.PersonCancelController.add()','POST',1,'admin','社保局','/api/shebao/personCancel','127.0.0.1','内网IP','{\"deathDate\":\"2026-01-19\",\"idCardNo\":\"142725198101111110\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-25 14:35:38',382),(314,'角色管理',2,'com.ruoyi.web.controller.system.SysRoleController.edit()','PUT',1,'admin','社保局','/api/system/role','183.241.78.6','XX XX','{\"admin\":false,\"createTime\":\"2026-01-19 10:08:28\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[],\"params\":{},\"remark\":\"审计员，负责日志查询和报表审计\",\"roleId\":105,\"roleKey\":\"auditor\",\"roleName\":\"统计管理员\",\"roleSort\":6,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-28 19:29:00',110),(315,'支付计划',1,'com.ruoyi.shebao.controller.PaymentPlanController.generate()','POST',1,'admin','社保局','/api/shebao/payment/plan/generate','183.241.78.6','XX XX','{}',NULL,1,'补贴类型不存在','2026-01-29 01:33:28',85),(316,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'admin','社保局','/api/shebao/landLossResident','60.10.59.193','XX XX','{\"birthday\":\"1972-09-05\",\"compensationCompleteTime\":\"2025-12-01\",\"gender\":\"2\",\"homeAddress\":\"第一村\",\"householdRegistration\":\"第一村\",\"idCardNo\":\"132801197209052622\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2025-12-01\",\"name\":\"张一一\",\"personExists\":false,\"phone\":\"13555556666\",\"recognitionTime\":\"2026-01-01\",\"streetOfficeId\":1,\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-30 09:20:22',169),(317,'失地居民信息',2,'com.ruoyi.shebao.controller.LandLossResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/landLossResident','60.10.59.193','XX XX','{\"birthday\":\"1972-09-05\",\"compensationCompleteTime\":\"2025-12-01\",\"gender\":\"2\",\"homeAddress\":\"第一村\",\"householdRegistration\":\"第一村\",\"id\":5,\"idCardNo\":\"132801197209052622\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2025-12-01\",\"name\":\"张一一\",\"personExists\":true,\"phone\":\"13555556666\",\"recognitionTime\":\"2026-01-02\",\"streetOfficeId\":1,\"subsidyPersonId\":1000019,\"userCode\":\"0010010007\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-01-30 09:20:39',83),(318,'失地居民信息',2,'com.ruoyi.shebao.controller.LandLossResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/landLossResident','60.10.59.193','XX XX','{\"birthday\":\"1972-09-05\",\"compensationCompleteTime\":\"2025-12-01\",\"gender\":\"1\",\"homeAddress\":\"第一村\",\"householdRegistration\":\"第一村\",\"id\":5,\"idCardNo\":\"132801197209052622\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2025-12-01\",\"name\":\"张一一\",\"personExists\":true,\"phone\":\"13555556666\",\"recognitionTime\":\"2026-01-02\",\"streetOfficeId\":1,\"subsidyPersonId\":1000019,\"userCode\":\"0010010007\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-02-03 15:07:12',74),(319,'人员注销登记',1,'com.ruoyi.shebao.controller.PersonCancelController.add()','POST',1,'admin','社保局','/api/shebao/personCancel','60.10.59.193','XX XX','{\"deathDate\":\"2026-02-01\",\"idCardNo\":\"13280119450112452X\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-02-03 15:07:52',73),(320,'人员关键信息修改',1,'com.ruoyi.shebao.controller.PersonModifyController.add()','POST',1,'admin','社保局','/api/shebao/person/modify','127.0.0.1','内网IP','{\"householdRegistration\":\"廊坊\",\"idCardNo\":\"131131198411120021\",\"name\":\"刘三娘\",\"streetOfficeId\":1,\"subsidyPersonId\":1000001,\"villageCommitteeId\":2}','{\"msg\":\"操作成功\",\"code\":200,\"data\":1}',0,NULL,'2026-02-03 18:53:14',924),(321,'人员关键信息修改提交',2,'com.ruoyi.shebao.controller.PersonModifyController.submit()','POST',1,'admin','社保局','/api/shebao/person/modify/submit/1','127.0.0.1','内网IP','1','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-02-03 18:53:16',902),(322,'人员关键信息修改复核',2,'com.ruoyi.shebao.controller.PersonModifyController.review()','POST',1,'admin','社保局','/api/shebao/person/modify/review/1','127.0.0.1','内网IP','{\"approved\":\"true\",\"remark\":\"已复核\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-02-03 18:54:16',473),(323,'人员关键信息修改审批',2,'com.ruoyi.shebao.controller.PersonModifyController.approve()','POST',1,'admin','社保局','/api/shebao/person/modify/approve/1','127.0.0.1','内网IP','{\"approved\":\"true\",\"remark\":\"已审批\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-02-03 18:54:29',787),(324,'支付计划',1,'com.ruoyi.shebao.controller.PaymentPlanController.generate()','POST',1,'admin','社保局','/api/shebao/payment/plan/generate','127.0.0.1','内网IP','{\"subsidyType\":\"land_loss_resident\"}',NULL,1,'补贴类型不存在','2026-02-03 18:56:03',194),(325,'支付计划',1,'com.ruoyi.shebao.controller.PaymentPlanController.generate()','POST',1,'admin','社保局','/api/shebao/payment/plan/generate','60.10.59.193','XX XX','{\"subsidyType\":\"land_loss_resident\"}',NULL,1,'补贴类型不存在','2026-02-25 16:31:13',81),(326,'支付计划',1,'com.ruoyi.shebao.controller.PaymentPlanController.generate()','POST',1,'admin','社保局','/api/shebao/payment/plan/generate','60.10.59.193','XX XX','{\"subsidyType\":\"land_loss_resident\"}',NULL,1,'补贴类型不存在','2026-02-25 16:31:18',14),(327,'支付计划',1,'com.ruoyi.shebao.controller.PaymentPlanController.generate()','POST',1,'admin','社保局','/api/shebao/payment/plan/generate','127.0.0.1','内网IP','{\"subsidyType\":\"land_loss_resident\"}',NULL,1,'补贴类型不存在','2026-03-02 23:13:26',33),(328,'支付计划',1,'com.ruoyi.shebao.controller.PaymentPlanController.generate()','POST',1,'admin','社保局','/api/shebao/payment/plan/generate','127.0.0.1','内网IP','{\"subsidyType\":\"teacher\"}',NULL,1,'补贴类型不存在','2026-03-02 23:13:35',6),(329,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'admin','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"gender\":\"1\",\"idCardNo\":\"130102199001017249\",\"streetOfficeId\":1,\"villageCommitteeId\":1}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLException: Field \'name\' doesn\'t have a default value\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_subsidy_person  ( gender, id_card_no, birthday,    is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?,    ?,  ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLException: Field \'name\' doesn\'t have a default value\n; Field \'name\' doesn\'t have a default value','2026-03-02 16:00:49',28),(330,'财务账户',1,'com.ruoyi.shebao.controller.FinanceAccountController.add()','POST',1,'admin','社保局','/api/shebao/finance/account','127.0.0.1','内网IP','{\"accountName\":\"测试财务账户\",\"accountType\":\"bank\",\"bankName\":\"中国工商银行\",\"remark\":\"自动化测试创建\",\"status\":\"0\"}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'account_type\' in \'field list\'\n### The error may exist in com/ruoyi/shebao/mapper/FinanceAccountMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.FinanceAccountMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO finance_account  ( account_name,  account_type, bank_name,   status,      remark )  VALUES (  ?,  ?, ?,   ?,      ?  )\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'account_type\' in \'field list\'\n; bad SQL grammar []','2026-03-02 16:00:49',5),(331,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'admin','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"gender\":\"1\",\"idCardNo\":\"130102199001017329\",\"streetOfficeId\":1,\"villageCommitteeId\":1}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLException: Field \'name\' doesn\'t have a default value\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_subsidy_person  ( gender, id_card_no, birthday,    is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?,    ?,  ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLException: Field \'name\' doesn\'t have a default value\n; Field \'name\' doesn\'t have a default value','2026-03-02 16:02:09',11),(332,'财务账户',1,'com.ruoyi.shebao.controller.FinanceAccountController.add()','POST',1,'admin','社保局','/api/shebao/finance/account','127.0.0.1','内网IP','{\"accountName\":\"测试财务账户\",\"bankName\":\"中国工商银行\",\"remark\":\"自动化测试创建\",\"status\":\"0\"}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLException: Field \'account_no\' doesn\'t have a default value\n### The error may exist in com/ruoyi/shebao/mapper/FinanceAccountMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.FinanceAccountMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO finance_account  ( account_name,   bank_name,   status,      remark )  VALUES (  ?,   ?,   ?,      ?  )\n### Cause: java.sql.SQLException: Field \'account_no\' doesn\'t have a default value\n; Field \'account_no\' doesn\'t have a default value','2026-03-02 16:02:09',2),(333,'用户管理',2,'com.ruoyi.web.controller.system.SysUserController.resetPwd()','PUT',1,'admin','社保局','/api/system/user/resetPwd','127.0.0.1','内网IP','{\"admin\":false,\"params\":{},\"updateBy\":\"admin\",\"userId\":101}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-02 16:06:28',78),(334,'用户管理',2,'com.ruoyi.web.controller.system.SysUserController.resetPwd()','PUT',1,'admin','社保局','/api/system/user/resetPwd','127.0.0.1','内网IP','{\"admin\":false,\"params\":{},\"updateBy\":\"admin\",\"userId\":102}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-02 16:06:28',74),(335,'用户管理',2,'com.ruoyi.web.controller.system.SysUserController.resetPwd()','PUT',1,'admin','社保局','/api/system/user/resetPwd','127.0.0.1','内网IP','{\"admin\":false,\"params\":{},\"updateBy\":\"admin\",\"userId\":103}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-02 16:06:29',78),(336,'用户管理',2,'com.ruoyi.web.controller.system.SysUserController.resetPwd()','PUT',1,'admin','社保局','/api/system/user/resetPwd','127.0.0.1','内网IP','{\"admin\":false,\"params\":{},\"updateBy\":\"admin\",\"userId\":104}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-02 16:06:29',74),(337,'用户管理',2,'com.ruoyi.web.controller.system.SysUserController.resetPwd()','PUT',1,'admin','社保局','/api/system/user/resetPwd','127.0.0.1','内网IP','{\"admin\":false,\"params\":{},\"updateBy\":\"admin\",\"userId\":105}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-02 16:06:29',74),(338,'角色管理',2,'com.ruoyi.web.controller.system.SysRoleController.edit()','PUT',1,'admin','社保局','/api/system/role','127.0.0.1','内网IP','{\"admin\":false,\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"menuIds\":[2078,2084,2085,2086,2087,2088,2110,2079,2091,2092,2000,2031,2032,2004,2009,2013,2011],\"params\":{},\"roleId\":101,\"roleKey\":\"operator\",\"roleName\":\"经办人\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-02 16:07:23',21),(339,'角色管理',2,'com.ruoyi.web.controller.system.SysRoleController.edit()','PUT',1,'admin','社保局','/api/system/role','127.0.0.1','内网IP','{\"admin\":false,\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"menuIds\":[2078,2089,2090,2079,2093,2081,2098,2000,2031,2032],\"params\":{},\"roleId\":102,\"roleKey\":\"reviewer\",\"roleName\":\"复核人\",\"roleSort\":4,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-02 16:07:23',12),(340,'角色管理',2,'com.ruoyi.web.controller.system.SysRoleController.edit()','PUT',1,'admin','社保局','/api/system/role','127.0.0.1','内网IP','{\"admin\":false,\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"menuIds\":[2081,2099,2100,2080,2094,2095,2096],\"params\":{},\"roleId\":103,\"roleKey\":\"approver\",\"roleName\":\"审批人\",\"roleSort\":5,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-02 16:07:23',12),(341,'角色管理',2,'com.ruoyi.web.controller.system.SysRoleController.edit()','PUT',1,'admin','社保局','/api/system/role','127.0.0.1','内网IP','{\"admin\":false,\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"menuIds\":[2082,2101,2102,2103,2104,2009,2013],\"params\":{},\"roleId\":104,\"roleKey\":\"finance\",\"roleName\":\"财务人员\",\"roleSort\":6,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-02 16:07:23',11),(342,'角色管理',2,'com.ruoyi.web.controller.system.SysRoleController.edit()','PUT',1,'admin','社保局','/api/system/role','127.0.0.1','内网IP','{\"admin\":false,\"deptCheckStrictly\":false,\"flag\":false,\"menuCheckStrictly\":false,\"menuIds\":[2083,2105,2106,2107,2108,2009,2013,2011],\"params\":{},\"roleId\":105,\"roleKey\":\"auditor\",\"roleName\":\"统计管理员\",\"roleSort\":7,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-02 16:07:23',9),(343,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'operator01','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1990-01-01\",\"gender\":\"1\",\"homeAddress\":\"河省廊坊市银河南路街道第一村123号\",\"householdRegistration\":\"河北省廊坊市银河南路街道第一村\",\"idCardNo\":\"130102199001011234\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"测试张三\",\"personExists\":false,\"phone\":\"13800138000\",\"streetOfficeId\":1,\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-02 16:36:54',34),(344,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'admin','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"gender\":\"1\",\"idCardNo\":\"130102199001012175\",\"streetOfficeId\":1,\"villageCommitteeId\":1}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLException: Field \'name\' doesn\'t have a default value\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_subsidy_person  ( gender, id_card_no, birthday,    is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?,    ?,  ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLException: Field \'name\' doesn\'t have a default value\n; Field \'name\' doesn\'t have a default value','2026-03-02 17:22:55',122),(345,'财务账户',1,'com.ruoyi.shebao.controller.FinanceAccountController.add()','POST',1,'admin','社保局','/api/shebao/finance/account','127.0.0.1','内网IP','{\"accountName\":\"测试财务账户\",\"bankName\":\"中国工商银行\",\"remark\":\"自动化测试创建\",\"status\":\"0\"}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLException: Field \'account_no\' doesn\'t have a default value\n### The error may exist in com/ruoyi/shebao/mapper/FinanceAccountMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.FinanceAccountMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO finance_account  ( account_name,   bank_name,  status,      remark )  VALUES (  ?,   ?,  ?,      ?  )\n### Cause: java.sql.SQLException: Field \'account_no\' doesn\'t have a default value\n; Field \'account_no\' doesn\'t have a default value','2026-03-02 17:22:55',6),(346,'财务账户',1,'com.ruoyi.shebao.controller.FinanceAccountController.add()','POST',1,'admin','社保局','/api/shebao/finance/account','127.0.0.1','内网IP','{\"accountName\":\"测试账户\",\"bankName\":\"工商银行\",\"status\":\"0\"}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLException: Field \'account_no\' doesn\'t have a default value\n### The error may exist in com/ruoyi/shebao/mapper/FinanceAccountMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.FinanceAccountMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO finance_account  ( account_name,   bank_name,  status )  VALUES (  ?,   ?,  ?  )\n### Cause: java.sql.SQLException: Field \'account_no\' doesn\'t have a default value\n; Field \'account_no\' doesn\'t have a default value','2026-03-02 17:23:07',2),(347,'财务账户',1,'com.ruoyi.shebao.controller.FinanceAccountController.add()','POST',1,'admin','社保局','/api/shebao/finance/account','127.0.0.1','内网IP','{\"accountCode\":\"95588001234\",\"accountName\":\"测试账户\",\"accountType\":\"land_loss\",\"bankName\":\"工商银行\",\"id\":6,\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-02 17:24:02',5),(348,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'admin','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"gender\":\"1\",\"idCardNo\":\"130102199001012322\",\"streetOfficeId\":1,\"villageCommitteeId\":1}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLException: Field \'name\' doesn\'t have a default value\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_subsidy_person  ( gender, id_card_no, birthday,    is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?,    ?,  ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLException: Field \'name\' doesn\'t have a default value\n; Field \'name\' doesn\'t have a default value','2026-03-02 17:25:23',10),(349,'财务账户',1,'com.ruoyi.shebao.controller.FinanceAccountController.add()','POST',1,'admin','社保局','/api/shebao/finance/account','127.0.0.1','内网IP','{\"accountName\":\"测试财务账户\",\"bankName\":\"中国工商银行\",\"remark\":\"自动化测试创建\",\"status\":\"0\"}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLException: Field \'account_no\' doesn\'t have a default value\n### The error may exist in com/ruoyi/shebao/mapper/FinanceAccountMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.FinanceAccountMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO finance_account  ( account_name,   bank_name,  status,      remark )  VALUES (  ?,   ?,  ?,      ?  )\n### Cause: java.sql.SQLException: Field \'account_no\' doesn\'t have a default value\n; Field \'account_no\' doesn\'t have a default value','2026-03-02 17:25:23',2),(350,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'operator01','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1995-01-01\",\"gender\":\"1\",\"homeAddress\":\"测试地址\",\"householdRegistration\":\"河北廊坊\",\"idCardNo\":\"130102199501011234\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"测试审核流程\",\"phone\":\"13900139000\",\"streetOfficeId\":1,\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 00:55:23',48),(351,'人员登记',2,'com.ruoyi.shebao.controller.PersonRegistrationController.submit()','POST',1,'operator01','社保局','/api/shebao/person/registration/submit/7','127.0.0.1','内网IP','7 \"\\\"提交审核测试\\\"\"','{\"msg\":\"提交审核成功\",\"code\":200}',0,NULL,'2026-03-03 00:55:23',17),(352,'人员登记复核',2,'com.ruoyi.shebao.controller.PersonReviewController.approve()','POST',1,'reviewer01','社保局','/api/shebao/person/review/approve/7','127.0.0.1','内网IP','{\"remark\":\"approved\"}','{\"msg\":\"复核通过\",\"code\":200}',0,NULL,'2026-03-03 00:56:07',17),(353,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'operator01','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1988-01-01\",\"gender\":\"1\",\"homeAddress\":\"银河南路1号\",\"householdRegistration\":\"河北廊坊\",\"idCardNo\":\"130102198801011234\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"王五测试\",\"phone\":\"13700137000\",\"streetOfficeId\":1,\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 00:57:08',17),(354,'人员登记',2,'com.ruoyi.shebao.controller.PersonRegistrationController.submit()','POST',1,'operator01','社保局','/api/shebao/person/registration/submit/8','127.0.0.1','内网IP','8 \"\\\"submit\\\"\"','{\"msg\":\"提交审核成功\",\"code\":200}',0,NULL,'2026-03-03 00:57:08',11),(355,'人员登记复核',2,'com.ruoyi.shebao.controller.PersonReviewController.approve()','POST',1,'reviewer01','社保局','/api/shebao/person/review/approve/8','127.0.0.1','内网IP','{\"remark\":\"ok\"}','{\"msg\":\"复核通过\",\"code\":200}',0,NULL,'2026-03-03 00:57:08',12),(356,'待遇核定',1,'com.ruoyi.shebao.controller.BenefitDeterminationController.add()','POST',1,'operator01','社保局','/api/shebao/benefit/determination','127.0.0.1','内网IP','{\"approvalStatus\":\"draft\",\"bankName\":\"工商银行\",\"benefitStartMonth\":\"2048-02\",\"createTime\":\"2026-03-03 00:57:08\",\"delFlag\":\"0\",\"eligibleMonth\":\"2048-01\",\"params\":{},\"subsidyPersonId\":1000022,\"subsidyType\":\"land_loss_resident\"}',NULL,1,'\n### Error updating database.  Cause: java.lang.IllegalStateException: Type handler was null on parameter mapping for property \'params\'. It was either not specified and/or could not be found for the javaType (java.util.Map) : jdbcType (null) combination.\n### The error may exist in com/ruoyi/shebao/mapper/BenefitDeterminationMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.BenefitDeterminationMapper.insert\n### The error occurred while executing an update\n### Cause: java.lang.IllegalStateException: Type handler was null on parameter mapping for property \'params\'. It was either not specified and/or could not be found for the javaType (java.util.Map) : jdbcType (null) combination.','2026-03-03 00:57:08',13),(357,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'operator01','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":500.00,\"subsidyPersonId\":1000022,\"subsidyRecordId\":8,\"subsidyType\":\"land_loss_resident\"}',NULL,1,'补贴类型不存在','2026-03-03 00:57:09',6),(358,'补贴发放审核通过',2,'com.ruoyi.shebao.controller.SubsidyDistributionController.approve()','PUT',1,'admin','社保局','/api/shebao/subsidyDistribution/approve','127.0.0.1','内网IP','{\"id\":10,\"remark\":\"approved\"}',NULL,1,'只有待审核状态的记录才能审核','2026-03-03 00:57:09',5),(359,'补贴发放',2,'com.ruoyi.shebao.controller.SubsidyDistributionController.distribute()','PUT',1,'admin','社保局','/api/shebao/subsidyDistribution/distribute','127.0.0.1','内网IP','{\"id\":10,\"remark\":\"distributed\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 00:57:09',11),(360,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'operator01','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1992-01-01\",\"gender\":\"1\",\"homeAddress\":\"银河路2号\",\"householdRegistration\":\"河北廊坊\",\"idCardNo\":\"130102199201011234\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"赵六全流程\",\"phone\":\"13600136000\",\"streetOfficeId\":1,\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 01:01:39',45),(361,'人员登记',2,'com.ruoyi.shebao.controller.PersonRegistrationController.submit()','POST',1,'operator01','社保局','/api/shebao/person/registration/submit/9','127.0.0.1','内网IP','9 \"\\\"test\\\"\"','{\"msg\":\"提交审核成功\",\"code\":200}',0,NULL,'2026-03-03 01:01:39',15),(362,'人员登记复核',2,'com.ruoyi.shebao.controller.PersonReviewController.approve()','POST',1,'reviewer01','社保局','/api/shebao/person/review/approve/9','127.0.0.1','内网IP','{\"remark\":\"ok\"}','{\"msg\":\"复核通过\",\"code\":200}',0,NULL,'2026-03-03 01:01:39',14),(363,'待遇核定',1,'com.ruoyi.shebao.controller.BenefitDeterminationController.add()','POST',1,'operator01','社保局','/api/shebao/benefit/determination','127.0.0.1','内网IP','{\"approvalStatus\":\"draft\",\"bankAccount\":\"6222001234567890\",\"bankName\":\"ICB\",\"benefitStartMonth\":\"2026-03\",\"createTime\":\"2026-03-03 01:01:39\",\"delFlag\":\"0\",\"eligibleMonth\":\"2052-01\",\"params\":{},\"subsidyPersonId\":1000023,\"subsidyStandard\":500.00,\"subsidyType\":\"land_loss_resident\"}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'bank_name\' in \'field list\'\n### The error may exist in com/ruoyi/shebao/mapper/BenefitDeterminationMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.BenefitDeterminationMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO benefit_determination  ( subsidy_person_id, subsidy_type, eligible_month, benefit_start_month,  bank_name, bank_account, subsidy_standard,        approval_status,       del_flag,  create_time )  VALUES (  ?, ?, ?, ?,  ?, ?, ?,        ?,       ?,  ?  )\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'bank_name\' in \'field list\'\n; bad SQL grammar []','2026-03-03 01:01:39',72),(364,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'operator01','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":500.00,\"subsidyPersonId\":1000023,\"subsidyRecordId\":9,\"subsidyType\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 01:01:40',16),(365,'补贴发放审核通过',2,'com.ruoyi.shebao.controller.SubsidyDistributionController.approve()','PUT',1,'admin','社保局','/api/shebao/subsidyDistribution/approve','127.0.0.1','内网IP','{\"id\":11,\"remark\":\"ok\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 01:01:40',12),(366,'补贴发放',2,'com.ruoyi.shebao.controller.SubsidyDistributionController.distribute()','PUT',1,'admin','社保局','/api/shebao/subsidyDistribution/distribute','127.0.0.1','内网IP','{\"id\":11,\"remark\":\"ok\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 01:01:40',10),(367,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'operator01','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1993-01-01\",\"gender\":\"2\",\"homeAddress\":\"龙河路1号\",\"householdRegistration\":\"河北廊坊\",\"idCardNo\":\"130102199301011234\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"全流程测试李四\",\"phone\":\"13500135000\",\"streetOfficeId\":2,\"villageCommitteeId\":3}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 01:07:03',47),(368,'人员登记',2,'com.ruoyi.shebao.controller.PersonRegistrationController.submit()','POST',1,'operator01','社保局','/api/shebao/person/registration/submit/10','127.0.0.1','内网IP','10 \"\\\"submit\\\"\"','{\"msg\":\"提交审核成功\",\"code\":200}',0,NULL,'2026-03-03 01:07:03',25),(369,'人员登记复核',2,'com.ruoyi.shebao.controller.PersonReviewController.approve()','POST',1,'reviewer01','社保局','/api/shebao/person/review/approve/10','127.0.0.1','内网IP','{\"remark\":\"ok\"}','{\"msg\":\"复核通过\",\"code\":200}',0,NULL,'2026-03-03 01:07:03',16),(370,'待遇核定',1,'com.ruoyi.shebao.controller.BenefitDeterminationController.add()','POST',1,'operator01','社保局','/api/shebao/benefit/determination','127.0.0.1','内网IP','{\"approvalStatus\":\"draft\",\"bankAccount\":\"6222009876543210\",\"bankAccountName\":\"全流程测试李四\",\"benefitStartMonth\":2,\"benefitStartYear\":2053,\"createTime\":\"2026-03-03 01:07:04\",\"delFlag\":\"0\",\"eligibleMonth\":1,\"eligibleYear\":2053,\"id\":1,\"params\":{},\"subsidyPersonId\":1000024,\"subsidyStandard\":600.00,\"subsidyType\":\"land_loss_resident\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 01:07:04',14),(371,'待遇核定',2,'com.ruoyi.shebao.controller.BenefitDeterminationController.submit()','POST',1,'operator01','社保局','/api/shebao/benefit/determination/submit/1','127.0.0.1','内网IP','1','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 01:07:04',7),(372,'待遇核定复核',2,'com.ruoyi.shebao.controller.BenefitReviewController.approve()','POST',1,'reviewer01','社保局','/api/shebao/benefit/review/approve/1','127.0.0.1','内网IP','{\"remark\":\"ok\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 01:07:04',6),(373,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'operator01','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":600.00,\"subsidyPersonId\":1000024,\"subsidyRecordId\":10,\"subsidyType\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 01:07:04',15),(374,'补贴发放审核通过',2,'com.ruoyi.shebao.controller.SubsidyDistributionController.approve()','PUT',1,'admin','社保局','/api/shebao/subsidyDistribution/approve','127.0.0.1','内网IP','{\"id\":12,\"remark\":\"ok\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 01:07:04',12),(375,'补贴发放',2,'com.ruoyi.shebao.controller.SubsidyDistributionController.distribute()','PUT',1,'admin','社保局','/api/shebao/subsidyDistribution/distribute','127.0.0.1','内网IP','{\"id\":12,\"remark\":\"ok\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 01:07:04',10),(376,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'operator01','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1968-03-15\",\"compensationCompleteTime\":\"2019-08-01\",\"gender\":\"1\",\"homeAddress\":\"光明路街道幸福里小区3号201\",\"householdRegistration\":\"河北省廊坊市安次区明路街道\",\"idCardNo\":\"130105196803152847\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2019-06-15\",\"name\":\"陈建国\",\"personExists\":false,\"phone\":\"13912345678\",\"streetOfficeId\":2,\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 01:39:19',33),(377,'人员登记',2,'com.ruoyi.shebao.controller.PersonRegistrationController.submit()','POST',1,'operator01','社保局','/api/shebao/person/registration/submit/11','127.0.0.1','内网IP','11 \"\\\"submit\\\"\"','{\"msg\":\"提交审核成功\",\"code\":200}',0,NULL,'2026-03-03 01:40:36',11),(378,'人员登记复核',2,'com.ruoyi.shebao.controller.PersonReviewController.approve()','POST',1,'reviewer01','社保局','/api/shebao/person/review/approve/11','127.0.0.1','内网IP','{\"remark\":\"信息核实无误，符合失地居民认定条件，同意通过\"}','{\"msg\":\"复核通过\",\"code\":200}',0,NULL,'2026-03-03 01:56:17',45),(379,'被征地参保补贴',1,'com.ruoyi.shebao.controller.ExpropriateeSubsidyController.add()','POST',1,'operator01','社保局','/api/shebao/expropriateeSubsidy','127.0.0.1','内网IP','{\"birthday\":\"1975-06-28\",\"gender\":\"2\",\"idCardNo\":\"130102197506283924\",\"name\":\"刘美华\",\"phone\":\"13698765432\",\"villageCommitteeId\":1}','{\"msg\":\"操作失败\",\"code\":500}',0,NULL,'2026-03-03 04:03:29',9),(380,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'operator01','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1975-06-28\",\"gender\":\"2\",\"homeAddress\":\"龙河路街道5号\",\"householdRegistration\":\"河北省廊坊\",\"idCardNo\":\"130102197506283924\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘美华\",\"phone\":\"13698765432\",\"streetOfficeId\":2,\"villageCommitteeId\":3}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 04:03:58',66),(381,'人员登记',2,'com.ruoyi.shebao.controller.PersonRegistrationController.submit()','POST',1,'operator01','社保局','/api/shebao/person/registration/submit/12','127.0.0.1','内网IP','12 \"\\\"submit\\\"\"','{\"msg\":\"提交审核成功\",\"code\":200}',0,NULL,'2026-03-03 04:03:58',14),(382,'人员登记复核',2,'com.ruoyi.shebao.controller.PersonReviewController.reject()','POST',1,'reviewer01','社保局','/api/shebao/person/review/reject/12','127.0.0.1','内网IP','{\"reason\":\"身份证号与户籍信息不一致,请核实后重新提交\"}','{\"msg\":\"复核驳回成功\",\"code\":200}',0,NULL,'2026-03-03 04:07:46',19),(383,'待遇核定',1,'com.ruoyi.shebao.controller.BenefitDeterminationController.add()','POST',1,'operator01','社保局','/api/shebao/benefit/determination','127.0.0.1','内网IP','{\"approvalStatus\":\"draft\",\"bankName\":\"中国工商银行\",\"createTime\":\"2026-03-03 04:16:04\",\"delFlag\":\"0\",\"params\":{}}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLException: Field \'subsidy_person_id\' doesn\'t have a default value\n### The error may exist in com/ruoyi/shebao/mapper/BenefitDeterminationMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.BenefitDeterminationMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO benefit_determination  ( approval_status,  del_flag,  create_time )  VALUES (  ?,  ?,  ?  )\n### Cause: java.sql.SQLException: Field \'subsidy_person_id\' doesn\'t have a default value\n; Field \'subsidy_person_id\' doesn\'t have a default value','2026-03-03 04:16:05',141),(384,'待遇核定',1,'com.ruoyi.shebao.controller.BenefitDeterminationController.add()','POST',1,'operator01','社保局','/api/shebao/benefit/determination','127.0.0.1','内网IP','{\"approvalStatus\":\"draft\",\"bankName\":\"中国工商银行\",\"benefitStartMonth\":4,\"benefitStartYear\":2028,\"createTime\":\"2026-03-03 04:35:26\",\"delFlag\":\"0\",\"eligibleMonth\":3,\"eligibleYear\":2028,\"idCardNo\":\"130105196803152847\",\"params\":{},\"subsidyPersonId\":1000025}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLException: Field \'subsidy_type\' doesn\'t have a default value\n### The error may exist in com/ruoyi/shebao/mapper/BenefitDeterminationMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.BenefitDeterminationMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO benefit_determination  ( subsidy_person_id,  eligible_year, eligible_month, benefit_start_year, benefit_start_month,       approval_status,  del_flag,  create_time )  VALUES (  ?,  ?, ?, ?, ?,       ?,  ?,  ?  )\n### Cause: java.sql.SQLException: Field \'subsidy_type\' doesn\'t have a default value\n; Field \'subsidy_type\' doesn\'t have a default value','2026-03-03 04:35:26',198),(385,'待遇核定',1,'com.ruoyi.shebao.controller.BenefitDeterminationController.add()','POST',1,'operator01','社保局','/api/shebao/benefit/determination','127.0.0.1','内网IP','{\"approvalStatus\":\"draft\",\"bankAccount\":\"6222001234567890\",\"bankName\":\"中国工商银行\",\"benefitStartMonth\":3,\"benefitStartYear\":2026,\"createTime\":\"2026-03-03 05:12:27\",\"delFlag\":\"0\",\"eligibleMonth\":3,\"eligibleYear\":2028,\"id\":2,\"idCardNo\":\"130105196803152847\",\"params\":{},\"subsidyPersonId\":1000025,\"subsidyStandard\":500,\"subsidyType\":\"land_loss_resident\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 05:12:27',48),(386,'待遇核定',2,'com.ruoyi.shebao.controller.BenefitDeterminationController.submit()','POST',1,'operator01','社保局','/api/shebao/benefit/determination/submit/2','127.0.0.1','内网IP','2','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 05:12:51',13),(387,'待遇核定复核',2,'com.ruoyi.shebao.controller.BenefitReviewController.approve()','POST',1,'reviewer01','社保局','/api/shebao/benefit/review/approve/2','127.0.0.1','内网IP','{\"remark\":\"核定标准合理，同意通过\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 05:16:05',30),(388,'补贴发放审核通过',2,'com.ruoyi.shebao.controller.SubsidyDistributionController.approve()','PUT',1,'admin','社保局','/api/shebao/subsidyDistribution/approve','127.0.0.1','内网IP','{\"id\":9,\"remark\":\"审核通过\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 14:10:02',37),(389,'补贴发放',2,'com.ruoyi.shebao.controller.SubsidyDistributionController.distribute()','PUT',1,'admin','社保局','/api/shebao/subsidyDistribution/distribute','127.0.0.1','内网IP','{\"id\":9,\"remark\":\"审核通过\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 14:12:08',28),(390,'人员关键信息修改复核',2,'com.ruoyi.shebao.controller.PersonModifyController.review()','POST',1,'reviewer01','社保局','/api/shebao/person/modify/review/1','127.0.0.1','内网IP','{\"approved\":\"true\",\"remark\":\"ok\"}',NULL,1,'当前状态不允许复核','2026-03-03 16:17:53',19),(391,'人员关键信息修改',1,'com.ruoyi.shebao.controller.PersonModifyController.add()','POST',1,'operator01','社保局','/api/shebao/person/modify','127.0.0.1','内网IP','{\"householdRegistration\":\"河北省廊坊市安次区光明路街道(更正)\",\"idCardNo\":\"130105196803152847\",\"name\":\"陈建国\",\"remark\":\"户籍信息更正\",\"streetOfficeId\":3,\"subsidyPersonId\":1000025,\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200,\"data\":2}',0,NULL,'2026-03-03 16:18:47',19),(392,'人员关键信息修改提交',2,'com.ruoyi.shebao.controller.PersonModifyController.submit()','POST',1,'operator01','社保局','/api/shebao/person/modify/submit/2','127.0.0.1','内网IP','2','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 16:18:47',12),(393,'人员关键信息修改复核',2,'com.ruoyi.shebao.controller.PersonModifyController.review()','POST',1,'reviewer01','社保局','/api/shebao/person/modify/review/2','127.0.0.1','内网IP','{\"approved\":\"true\",\"remark\":\"ok\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 16:18:48',7),(394,'人员关键信息修改',1,'com.ruoyi.shebao.controller.PersonModifyController.add()','POST',1,'operator01','社保局','/api/shebao/person/modify','127.0.0.1','内网IP','{\"householdRegistration\":\"河北省坊市安次区光明路街道(更正后)\",\"idCardNo\":\"130105196803152847\",\"name\":\"陈建国\",\"remark\":\"户籍地址更正申请\",\"streetOfficeId\":2,\"subsidyPersonId\":1000025,\"villageCommitteeId\":3}','{\"msg\":\"操作成功\",\"code\":200,\"data\":3}',0,NULL,'2026-03-03 16:29:07',80),(395,'人员关键信息修改提交',2,'com.ruoyi.shebao.controller.PersonModifyController.submit()','POST',1,'operator01','社保局','/api/shebao/person/modify/submit/3','127.0.0.1','内网IP','3','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 16:29:07',19),(396,'批次管理',2,'com.ruoyi.shebao.controller.BatchManageController.submit()','POST',1,'operator01','社保局','/api/shebao/payment/batch/submit/1','127.0.0.1','内网IP','1','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 17:04:34',16),(397,'支付计划复核',2,'com.ruoyi.shebao.controller.PaymentReviewController.approve()','POST',1,'reviewer01','社保局','/api/shebao/payment/review/approve/1','127.0.0.1','内网IP','{\"remark\":\"ok\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 17:04:34',9),(398,'支付计划审批',2,'com.ruoyi.shebao.controller.PaymentApproveController.approve()','POST',1,'approver01','社保局','/api/shebao/payment/approve/approve/1','127.0.0.1','内网IP','{\"remark\":\"ok\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-03-03 17:04:34',5);
/*!40000 ALTER TABLE `sys_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_post`
--

LOCK TABLES `sys_post` WRITE;
/*!40000 ALTER TABLE `sys_post` DISABLE KEYS */;
INSERT INTO `sys_post` VALUES (1,'ceo','董事长',1,'0','admin','2025-09-24 22:11:36','',NULL,''),(2,'se','项目经理',2,'0','admin','2025-09-24 22:11:36','',NULL,''),(3,'hr','人力资源',3,'0','admin','2025-09-24 22:11:36','',NULL,''),(4,'user','普通员工',4,'0','admin','2025-09-24 22:11:36','',NULL,'');
/*!40000 ALTER TABLE `sys_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1',1,1,'0','0','admin','2025-09-24 22:11:36','',NULL,'超级管理员'),(2,'普通角色','common',2,'2',1,1,'0','0','admin','2025-09-24 22:11:36','admin','2026-01-20 00:25:55','普通角色'),(101,'经办人','operator',3,'1',0,0,'0','0','admin','2026-01-19 10:08:28','admin','2026-03-02 16:07:23','业务经办人，负责数据录入和提交审核'),(102,'复核人','reviewer',4,'1',0,0,'0','0','admin','2026-01-19 10:08:28','admin','2026-03-02 16:07:23','业务复核人，负责复核经办人提交的数据'),(103,'审批人','approver',5,'1',0,0,'0','0','admin','2026-01-19 10:08:28','admin','2026-03-02 16:07:23','业务负责人（所长），负责最终审批'),(104,'财务人员','finance',6,'1',0,0,'0','0','admin','2026-01-19 10:08:28','admin','2026-03-02 16:07:23','财务人员，负责发放和账户管理'),(105,'统计管理员','auditor',7,'1',0,0,'0','0','admin','2026-01-19 10:08:28','admin','2026-03-02 16:07:23','审计员，负责日志查询和报表审计');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_dept`
--

LOCK TABLES `sys_role_dept` WRITE;
/*!40000 ALTER TABLE `sys_role_dept` DISABLE KEYS */;
INSERT INTO `sys_role_dept` VALUES (2,100),(2,101),(2,105);
/*!40000 ALTER TABLE `sys_role_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (2,2000),(2,2002),(2,2004),(2,2009),(2,2011),(2,2078),(2,2084),(2,2085),(2,2086),(2,2087),(2,2088),(2,2089),(2,2090),(101,2000),(101,2004),(101,2009),(101,2011),(101,2013),(101,2014),(101,2015),(101,2021),(101,2031),(101,2032),(101,2078),(101,2079),(101,2081),(101,2084),(101,2085),(101,2086),(101,2087),(101,2088),(101,2090),(101,2091),(101,2092),(101,2110),(101,2111),(101,2112),(101,2113),(101,2114),(101,3001),(101,3002),(101,3003),(101,3004),(101,3005),(101,3006),(101,3007),(101,3011),(101,3012),(101,3013),(101,3014),(101,3015),(101,3016),(101,3017),(101,3021),(101,3022),(101,3023),(101,3024),(101,3025),(101,3026),(101,3027),(101,3031),(101,3032),(101,3033),(101,3034),(101,3035),(101,3036),(101,3037),(101,3041),(101,3042),(101,3043),(101,3044),(101,3045),(101,3046),(101,3061),(101,3062),(101,3063),(101,3067),(101,3071),(101,3072),(101,3073),(101,3074),(101,3075),(101,3076),(101,3077),(101,3081),(101,3082),(101,3091),(101,3092),(101,3093),(101,3094),(101,3095),(101,3096),(101,3097),(101,3141),(101,3142),(101,3143),(101,3144),(101,3145),(101,3146),(101,3147),(101,3261),(101,3262),(101,3263),(101,3271),(101,3272),(101,3273),(101,3281),(101,3282),(101,3283),(101,3284),(101,3291),(101,3292),(101,3293),(101,3294),(101,3295),(101,3296),(102,2000),(102,2031),(102,2032),(102,2078),(102,2079),(102,2081),(102,2089),(102,2090),(102,2093),(102,2098),(102,3051),(102,3052),(102,3053),(102,3054),(102,3061),(102,3062),(102,3063),(102,3064),(102,3065),(102,3066),(102,3067),(102,3071),(102,3076),(102,3101),(102,3102),(102,3103),(102,3151),(102,3152),(102,3153),(102,3261),(102,3262),(102,3263),(102,3271),(102,3272),(102,3273),(102,3291),(102,3292),(102,3297),(103,2080),(103,2081),(103,2094),(103,2095),(103,2096),(103,2099),(103,2100),(103,3111),(103,3112),(103,3121),(103,3122),(103,3123),(103,3124),(103,3131),(103,3132),(103,3133),(103,3134),(103,3135),(103,3161),(103,3162),(103,3163),(103,3171),(103,3291),(103,3292),(103,3298),(104,2009),(104,2013),(104,2014),(104,2021),(104,2082),(104,2101),(104,2102),(104,2103),(104,2104),(104,3181),(104,3182),(104,3183),(104,3184),(104,3185),(104,3191),(104,3192),(104,3193),(104,3201),(104,3202),(104,3203),(104,3204),(104,3205),(104,3211),(104,3212),(104,3213),(104,3214),(104,3215),(105,2009),(105,2011),(105,2013),(105,2014),(105,2083),(105,2105),(105,2106),(105,2107),(105,2108),(105,3221),(105,3222),(105,3223),(105,3231),(105,3232),(105,3241),(105,3242),(105,3251),(105,3252),(105,3253),(105,3254),(105,3255),(105,3256);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,100,'admin','超级管理员','00','ry@163.com','15888888888','1','','$2a$10$t/ws7qYp/wPm9b7jdNNaiuUTOmCmVTHo1mikBUM1zZ3NL3jfDha42','0','0','127.0.0.1','2026-03-04 01:04:35','2025-10-14 10:52:21','admin','2025-09-24 22:11:36','','2025-10-14 10:52:21','管理员'),(2,105,'ry','若依','00','ry@qq.com','15666666666','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','2','127.0.0.1','2025-09-24 22:11:36','2025-09-24 22:11:36','admin','2025-09-24 22:11:36','',NULL,'测试员'),(101,100,'operator01','经办人01','00','operator01@shebao.com','13800001001','0','','$2a$10$lzdKrbXy5Sm4XuOdjPO64u.41aaIsBsxSDfyBqF56htAujEqzG09S','0','0','127.0.0.1','2026-03-04 01:04:34','2026-03-02 16:06:28','admin','2026-01-19 10:08:28','','2026-03-02 16:06:28','经办人测试账号'),(102,100,'reviewer01','复核人01','00','reviewer01@shebao.com','13800001002','0','','$2a$10$V.GHUG0Pa4MeOk4oB9Yf9.ZrOTc.e7iQD2px0IKUJ7fKoxnNWjg0G','0','0','127.0.0.1','2026-03-04 01:08:44','2026-03-02 16:06:28','admin','2026-01-19 10:08:28','','2026-03-02 16:06:28','复核人测试账号'),(103,100,'approver01','审批人01','00','approver01@shebao.com','13800001003','0','','$2a$10$0UIkw8NxUh4lMYoLPpInle8zJGI6/Gb9U6g1wjVzMoOXTXSwBkwkK','0','0','127.0.0.1','2026-03-04 01:11:53','2026-03-02 16:06:29','admin','2026-01-19 10:08:28','','2026-03-02 16:06:29','审批人测试账号'),(104,100,'finance01','财务01','00','finance01@shebao.com','13800001004','0','','$2a$10$BjVUQ6xXFqdfVLDPt.QOZevWspj8p4Ug0oguDLDnB/YaOsz.5vJ66','0','0','127.0.0.1','2026-03-04 01:14:55','2026-03-02 16:06:29','admin','2026-01-19 10:08:28','','2026-03-02 16:06:29','财务人员测试账号'),(105,100,'auditor01','审计员01','00','auditor01@shebao.com','13800001005','0','','$2a$10$Af7ZvT/3p2WyqGF3zrIsMeS4A8vzHt8s2G4X3uj43XP2tjgGj4Btq','0','0','127.0.0.1','2026-03-03 12:37:54','2026-03-02 16:06:29','admin','2026-01-19 10:08:28','','2026-03-02 16:06:29','审计员测试账号');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_post`
--

LOCK TABLES `sys_user_post` WRITE;
/*!40000 ALTER TABLE `sys_user_post` DISABLE KEYS */;
INSERT INTO `sys_user_post` VALUES (1,1);
/*!40000 ALTER TABLE `sys_user_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(101,101),(102,102),(103,103),(104,104),(105,105);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'lfpm'
--

--
-- Dumping routines for database 'lfpm'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-03 18:14:05
