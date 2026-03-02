-- MySQL dump 10.13  Distrib 8.0.43, for macos26.0 (arm64)
--
-- Host: rm-2zel3391a0me3m4iheo.mysql.rds.aliyuncs.com    Database: langfang_shebao_test
-- ------------------------------------------------------
-- Server version	8.0.36

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'b584a2f0-1c33-11f0-8ac4-00163e3c9843:1-1168510,
e218c217-1c25-11f0-b248-00163e388592:1-419';

--
-- Current Database: `langfang_shebao_test`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `langfang_shebao_test` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `langfang_shebao_test`;

--
-- Table structure for table `gen_table`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--


--
-- Table structure for table `gen_table_column`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--


--
-- Table structure for table `qrtz_blob_triggers`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Blob类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_blob_triggers`
--


--
-- Table structure for table `qrtz_calendars`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日历信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_calendars`
--


--
-- Table structure for table `qrtz_cron_triggers`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cron类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_cron_triggers`
--


--
-- Table structure for table `qrtz_fired_triggers`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) NOT NULL COMMENT '状态',
  `job_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='已触发的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_fired_triggers`
--


--
-- Table structure for table `qrtz_job_details`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) NOT NULL COMMENT '任务组名',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_job_details`
--


--
-- Table structure for table `qrtz_locks`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储的悲观锁信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_locks`
--


--
-- Table structure for table `qrtz_paused_trigger_grps`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='暂停的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--


--
-- Table structure for table `qrtz_scheduler_state`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调度器状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_scheduler_state`
--


--
-- Table structure for table `qrtz_simple_triggers`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简单触发器的信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simple_triggers`
--


--
-- Table structure for table `qrtz_simprop_triggers`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='同步机制的行锁表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simprop_triggers`
--


--
-- Table structure for table `qrtz_triggers`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='触发器详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_triggers`
--

--
-- Table structure for table `shebao_demo`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_demo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(30) NOT NULL COMMENT '用户账号',
  `status` char(1) DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_demo`
--

INSERT INTO `shebao_demo` VALUES (1,'123','0','0','','2025-09-26 23:54:56','','2025-09-26 23:54:56',NULL);

--
-- Table structure for table `shebao_demolition_resident`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_demolition_resident` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `demolition_reason` varchar(200) DEFAULT NULL COMMENT '拆迁事由',
  `demolition_time` date DEFAULT NULL COMMENT '拆迁时间',
  `recognition_time` date DEFAULT NULL COMMENT '认定为拆迁居民时间',
  `subsidy_amount` decimal(10,2) DEFAULT '0.00' COMMENT '补贴金额',
  `distribution_status` char(1) DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_subsidy_person_id` (`subsidy_person_id`),
  KEY `idx_demolition_time` (`demolition_time`),
  KEY `idx_recognition_time` (`recognition_time`),
  CONSTRAINT `shebao_demolition_resident_ibfk_1` FOREIGN KEY (`subsidy_person_id`) REFERENCES `shebao_subsidy_person` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='拆迁居民信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_demolition_resident`
--

INSERT INTO `shebao_demolition_resident` VALUES (1,1000000,'修路','2025-09-29','2025-09-16',0.00,'0','0','0','admin','2025-09-28 22:10:57','','2025-09-28 22:38:01',NULL),(2,1000016,'222','2025-10-03','2025-10-04',0.00,'0','0','0','admin','2025-10-19 17:29:22','admin','2025-10-19 17:54:36','555');

--
-- Table structure for table `shebao_distribution_review`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_distribution_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `distribution_id` bigint NOT NULL COMMENT '发放记录ID（关联shebao_subsidy_distribution.id）',
  `operation_type` char(1) NOT NULL COMMENT '操作类型（1提交审核 2审核通过 3审核驳回 4发放 5重新提交）',
  `operation_remark` varchar(500) DEFAULT NULL COMMENT '操作说明',
  `operator_name` varchar(64) NOT NULL COMMENT '操作人',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_distribution_id` (`distribution_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operation_time` (`operation_time`),
  CONSTRAINT `shebao_distribution_review_ibfk_1` FOREIGN KEY (`distribution_id`) REFERENCES `shebao_subsidy_distribution` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='发放审核记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_distribution_review`
--

INSERT INTO `shebao_distribution_review` VALUES (1,1,'1','提交审核','admin','2025-10-19 20:59:18','0','0','','2025-10-19 20:59:17','','2025-10-19 20:59:17',NULL),(2,2,'1','提交审核','admin','2025-10-19 21:16:55','0','0','','2025-10-19 21:16:55','','2025-10-19 21:16:55',NULL),(3,2,'2','同意','admin','2025-10-19 21:19:58','0','0','','2025-10-19 21:19:58','','2025-10-19 21:19:58',NULL),(4,3,'1','提交审核','admin','2025-10-19 21:20:41','0','0','','2025-10-19 21:20:40','','2025-10-19 21:20:40',NULL),(5,4,'1','提交审核','admin','2025-10-19 21:21:37','0','0','','2025-10-19 21:21:37','','2025-10-19 21:21:37',NULL),(6,4,'2','同意','admin','2025-10-19 21:21:43','0','0','','2025-10-19 21:21:43','','2025-10-19 21:21:43',NULL),(7,4,'4','同意','admin','2025-10-19 21:21:49','0','0','','2025-10-19 21:21:49','','2025-10-19 21:21:49',NULL),(8,5,'1','提交审核','admin','2025-10-19 22:04:58','0','0','','2025-10-19 22:04:58','','2025-10-19 22:04:58',NULL),(9,6,'1','提交审核','admin','2025-10-19 22:13:58','0','0','','2025-10-19 22:13:58','','2025-10-19 22:13:58',NULL),(10,7,'1','提交审核','admin','2025-10-19 22:15:01','0','0','','2025-10-19 22:15:00','','2025-10-19 22:15:00',NULL),(11,8,'1','提交审核','admin','2025-10-19 22:15:20','0','0','','2025-10-19 22:15:20','','2025-10-19 22:15:20',NULL),(12,9,'1','提交审核','admin','2025-10-19 22:33:03','0','0','','2025-10-19 22:33:03','','2025-10-19 22:33:03',NULL),(13,10,'1','1231231231','admin','2025-10-19 22:49:37','0','0','','2025-10-19 22:49:37','','2025-10-19 22:49:37',NULL),(14,10,'2','','admin','2025-10-19 22:52:00','0','0','','2025-10-19 22:52:00','','2025-10-19 22:52:00',NULL);

--
-- Table structure for table `shebao_expropriatee_subsidy`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_expropriatee_subsidy` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `land_requisition_batch` varchar(100) DEFAULT NULL COMMENT '征地批次',
  `base_date` date DEFAULT NULL COMMENT '基准日',
  `employee_pension_months` int DEFAULT '0' COMMENT '职工身份缴纳职工养老月数',
  `flexible_employment_months` int DEFAULT '0' COMMENT '灵活就业身份缴纳职工养老保险月数',
  `difficulty_subsidy_years` decimal(5,2) DEFAULT '0.00' COMMENT '已领取困难人员社会保险补贴年限',
  `age_at_base_date` int DEFAULT NULL COMMENT '截至基准日的年龄',
  `subsidy_years` decimal(5,2) DEFAULT '0.00' COMMENT '被征地参保补贴年限',
  `subsidy_amount` decimal(10,2) DEFAULT '0.00' COMMENT '补贴金额',
  `distribution_status` char(1) DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
  `join_urban_rural_insurance` char(1) DEFAULT '0' COMMENT '选择参加城乡居保（0否 1是）',
  `join_employee_pension` char(1) DEFAULT '0' COMMENT '选择参加职工养老（0否 1是）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_subsidy_person_id` (`subsidy_person_id`),
  KEY `idx_batch` (`land_requisition_batch`),
  KEY `idx_base_date` (`base_date`),
  CONSTRAINT `shebao_expropriatee_subsidy_ibfk_1` FOREIGN KEY (`subsidy_person_id`) REFERENCES `shebao_subsidy_person` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='被征地参保补贴账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_expropriatee_subsidy`
--

INSERT INTO `shebao_expropriatee_subsidy` VALUES (1,1000000,'20202','2025-09-09',120,10,0.00,40,0.00,0.00,'0','0','0','0','0','admin','2025-09-28 12:23:17','','2025-09-28 22:38:10',NULL),(2,1000002,NULL,'2025-09-01',0,0,0.00,70,15.00,7147.00,'0','1','0','0','0','admin','2025-09-28 22:59:15','','2025-09-28 22:59:15',NULL),(3,1000003,'第一批','2025-09-01',0,0,0.00,69,15.00,7147.22,'0','0','0','0','0','admin','2025-09-28 23:43:51','','2025-09-28 23:43:51',NULL),(4,1000000,NULL,'2025-10-01',0,0,0.00,40,3.00,1429.44,'0','0','0','0','0','admin','2025-10-16 15:35:19','','2025-10-16 15:35:19',NULL);

--
-- Table structure for table `shebao_land_loss_resident`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_land_loss_resident` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `land_requisition_time` date DEFAULT NULL COMMENT '征地时间',
  `compensation_complete_time` date DEFAULT NULL COMMENT '完成补偿时间',
  `recognition_time` date DEFAULT NULL COMMENT '认定为失地居民时间',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_subsidy_person_id` (`subsidy_person_id`),
  KEY `idx_recognition_time` (`recognition_time`),
  CONSTRAINT `shebao_land_loss_resident_ibfk_1` FOREIGN KEY (`subsidy_person_id`) REFERENCES `shebao_subsidy_person` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='失地居民信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_land_loss_resident`
--

INSERT INTO `shebao_land_loss_resident` VALUES (1,1000001,'2025-09-01','2025-09-22','2025-09-23','0','admin','2025-09-28 02:18:49','admin','2025-10-19 22:20:46','每月8号发'),(2,1000005,'2025-10-01','2025-09-29','2025-10-01','0','admin','2025-10-11 16:28:26','','2025-10-11 16:28:26',NULL),(3,1000000,'2025-10-01','2025-10-22','2025-10-01','0','admin','2025-10-11 16:29:39','admin','2025-10-19 20:52:52',NULL);

--
-- Table structure for table `shebao_street_office`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_street_office` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `street_code` varchar(3) NOT NULL COMMENT '街道办编码（3位数字，如：001）',
  `street_name` varchar(50) NOT NULL COMMENT '街道办名称',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_street_code` (`street_code`),
  KEY `idx_street_name` (`street_name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='街道办事处信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_street_office`
--

INSERT INTO `shebao_street_office` VALUES (1,'001','银河南路街道',NULL,NULL,NULL,1,'0','0','admin','2025-10-18 23:56:47','','2025-10-18 23:56:47',NULL),(2,'002','龙河路街道',NULL,NULL,NULL,2,'0','0','admin','2025-10-18 23:56:47','','2025-10-18 23:56:47',NULL),(3,'003','光明路街道',NULL,NULL,NULL,3,'0','0','admin','2025-10-18 23:56:47','','2025-10-18 23:56:47',NULL),(4,'004','麦子店街道','黄','13612345678','河北廊坊',0,'0','0','','2025-10-19 11:02:13','','2025-10-19 11:02:13','测试');

--
-- Table structure for table `shebao_subsidy_distribution`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_subsidy_distribution` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID（关联shebao_subsidy_person.id）',
  `subsidy_type` char(1) NOT NULL COMMENT '补贴类型（1失地居民补贴 2被征地居民补贴 3拆迁居民补贴 4村干部补贴）',
  `subsidy_record_id` bigint NOT NULL COMMENT '补贴身份记录ID（关联具体补贴类型表的ID）',
  `distribution_amount` decimal(10,2) NOT NULL COMMENT '发放金额',
  `distribution_date` date DEFAULT NULL COMMENT '发放日期',
  `distribution_status` char(1) NOT NULL DEFAULT '1' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
  `review_remark` varchar(500) DEFAULT NULL COMMENT '审核说明',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_subsidy_person_id` (`subsidy_person_id`),
  KEY `idx_subsidy_type` (`subsidy_type`),
  KEY `idx_distribution_status` (`distribution_status`),
  KEY `idx_distribution_date` (`distribution_date`),
  CONSTRAINT `shebao_subsidy_distribution_ibfk_1` FOREIGN KEY (`subsidy_person_id`) REFERENCES `shebao_subsidy_person` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='补贴发放记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_subsidy_distribution`
--

INSERT INTO `shebao_subsidy_distribution` VALUES (1,1000000,'1',3,100.00,NULL,'1',NULL,'0','0','admin','2025-10-19 20:59:18','','2025-10-19 20:59:17',NULL),(2,1000000,'3',1,123.45,NULL,'2','同意','0','0','admin','2025-10-19 21:16:55','admin','2025-10-19 21:19:58',NULL),(3,1000003,'2',3,100.00,NULL,'1',NULL,'0','0','admin','2025-10-19 21:20:41','','2025-10-19 21:20:40',NULL),(4,1000001,'1',1,111.00,'2025-10-19','4','同意','0','0','admin','2025-10-19 21:21:37','admin','2025-10-19 21:21:49',NULL),(5,1000001,'1',1,0.00,NULL,'1',NULL,'0','0','admin','2025-10-19 22:04:58','','2025-10-19 22:04:58','111'),(6,1000001,'1',1,100.00,NULL,'1',NULL,'0','0','admin','2025-10-19 22:13:58','','2025-10-19 22:13:58','111'),(7,1000001,'1',1,200.00,NULL,'1',NULL,'0','0','admin','2025-10-19 22:15:01','','2025-10-19 22:15:00',NULL),(8,1000001,'1',1,30.00,NULL,'1',NULL,'0','0','admin','2025-10-19 22:15:20','','2025-10-19 22:15:20','333'),(9,1000001,'1',1,0.01,NULL,'1',NULL,'0','0','admin','2025-10-19 22:33:03','','2025-10-19 22:33:03','12321'),(10,1000001,'1',1,0.01,NULL,'2','','0','0','admin','2025-10-19 22:49:37','admin','2025-10-19 22:52:00','1231231231');

--
-- Table structure for table `shebao_subsidy_person`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_subsidy_person` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户编号（系统生成，自增主键）',
  `name` varchar(20) NOT NULL COMMENT '姓名',
  `gender` char(1) NOT NULL COMMENT '性别（1男 2女）',
  `id_card_no` varchar(18) NOT NULL COMMENT '身份证号',
  `birthday` date NOT NULL COMMENT '生日',
  `household_registration` varchar(100) NOT NULL COMMENT '户籍所在地',
  `home_address` varchar(255) DEFAULT NULL,
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `is_alive` char(1) DEFAULT '1' COMMENT '是否健在（0否 1是）',
  `death_date` date DEFAULT NULL COMMENT '死亡时间',
  `is_village_coop_member` char(1) DEFAULT '1' COMMENT '是否村合作经济组织成员（0否 1是）',
  `street_office_id` bigint DEFAULT NULL COMMENT '所属街道办ID',
  `village_committee_id` bigint DEFAULT NULL COMMENT '所属村委会ID',
  `user_code` varchar(50) DEFAULT NULL COMMENT '用户编号（格式：001-002-0001）',
  `status` varchar(20) DEFAULT NULL,
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_id_card_no` (`id_card_no`),
  UNIQUE KEY `uk_user_code` (`user_code`),
  KEY `idx_name` (`name`),
  KEY `idx_phone` (`phone`),
  KEY `idx_street_office_id` (`street_office_id`),
  KEY `idx_village_committee_id` (`village_committee_id`),
  KEY `idx_is_alive` (`is_alive`)
) ENGINE=InnoDB AUTO_INCREMENT=1000017 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='被补贴人基础信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_subsidy_person`
--

INSERT INTO `shebao_subsidy_person` VALUES (1000000,'高老汉','2','131131193411120001','1934-11-12','河北廊坊','12321','13112345678','1',NULL,'1',1,2,'0010020001','0','0','admin','2025-09-28 01:49:29','admin','2025-10-20 00:44:29',NULL),(1000001,'刘三娘','2','131131198411120020','1984-11-12','廊坊','12321','13012345678','1',NULL,'1',1,2,'0010020002','0','0','admin','2025-09-28 22:43:24','admin','2025-10-20 00:45:35',NULL),(1000002,'刘大娘','2','131131195611120005','1956-11-12','廊坊','三里屯','13012345678','1',NULL,'1',2,3,'0020010001','0','0','admin','2025-09-28 22:59:15','admin','2025-10-20 00:42:21',NULL),(1000003,'王老五','2','131131195411120000','1955-11-12','廊坊',NULL,'13012345678','1',NULL,'1',2,3,'0020010002','0','0','admin','2025-09-28 23:43:51','','2025-10-19 14:07:01',NULL),(1000004,'刘玉连','1','132801195611252658','1956-11-25','河北省廊坊市','asdfadsf','130000000000','1',NULL,'1',1,1,'0010010001','0','0','admin','2025-10-11 10:58:40','admin','2025-10-20 00:44:07',NULL),(1000005,'高老汉','2','13280119450112452X','1945-01-12','啊','和平区','13333333333','1',NULL,'1',1,1,'0010010002','0','0','admin','2025-10-11 16:28:26','admin','2025-10-20 00:43:43',NULL),(1000006,'大毛','2','13280119650112422X','1965-01-12','河北','家里','13601243558','1','2025-10-20','1',1,1,'0010010003','0','0','admin','2025-10-19 13:55:31','admin','2025-10-19 15:13:58','123'),(1000015,'刘二娘','2','131131198411120004','1984-11-12','廊坊','111','13012345678','1',NULL,'1',1,1,'0010010004','0','0','admin','2025-10-19 17:50:27','','2025-10-19 17:50:26',NULL),(1000016,'刘大娘','1','131131198411120012','1984-11-12','廊坊','1111','13012345678','1',NULL,'1',1,1,'0010010005','0','0','admin','2025-10-19 17:54:10','','2025-10-19 17:54:26',NULL);

--
-- Table structure for table `shebao_village_committee`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_village_committee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `street_office_id` bigint NOT NULL COMMENT '所属街道办ID',
  `village_code` varchar(3) NOT NULL COMMENT '村委会编码（3位数字，如：002）',
  `village_name` varchar(50) NOT NULL COMMENT '村委会名称',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_street_village_code` (`street_office_id`,`village_code`),
  KEY `idx_street_office_id` (`street_office_id`),
  KEY `idx_village_name` (`village_name`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_village_committee_street_office` FOREIGN KEY (`street_office_id`) REFERENCES `shebao_street_office` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='村委会信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_village_committee`
--

INSERT INTO `shebao_village_committee` VALUES (1,1,'001','第一村',NULL,NULL,NULL,1,'0','0','admin','2025-10-18 23:56:54','','2025-10-18 23:56:54',NULL),(2,1,'002','第二村',NULL,NULL,NULL,2,'0','0','admin','2025-10-18 23:56:54','','2025-10-18 23:56:54',NULL),(3,2,'001','东村',NULL,NULL,NULL,1,'0','0','admin','2025-10-18 23:56:54','','2025-10-18 23:56:54',NULL);

--
-- Table structure for table `shebao_village_official`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_village_official` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `total_service_years` decimal(5,2) DEFAULT '0.00' COMMENT '累计任职年限',
  `has_violation` char(1) DEFAULT '0' COMMENT '是否违法乱纪或判刑（0否 1是）',
  `subsidy_amount` decimal(10,2) DEFAULT '0.00' COMMENT '补贴金额',
  `distribution_status` char(1) DEFAULT '0' COMMENT '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_subsidy_person_id` (`subsidy_person_id`),
  CONSTRAINT `shebao_village_official_ibfk_1` FOREIGN KEY (`subsidy_person_id`) REFERENCES `shebao_subsidy_person` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='村干部信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_village_official`
--

INSERT INTO `shebao_village_official` VALUES (1,1000000,10.00,'0',0.00,'0','0','0','admin','2025-09-28 22:18:31','admin','2025-09-28 23:50:52',NULL),(2,1000004,0.00,'0',0.00,'0','0','0','admin','2025-10-11 10:58:40','','2025-10-11 10:58:39',NULL),(3,1000000,0.00,'0',0.00,'0','0','0','admin','2025-10-16 15:37:18','','2025-10-16 15:37:18',NULL);

--
-- Table structure for table `shebao_village_official_position`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shebao_village_official_position` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `village_official_id` bigint NOT NULL COMMENT '村干部信息ID',
  `position` char(1) NOT NULL COMMENT '任职职位（1书记或主任 2书记兼主任 3村"两委"其他成员）',
  `start_date` date NOT NULL COMMENT '上任时间',
  `end_date` date DEFAULT NULL COMMENT '卸任时间',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_village_official_id` (`village_official_id`),
  KEY `idx_position` (`position`),
  KEY `idx_start_date` (`start_date`),
  CONSTRAINT `shebao_village_official_position_ibfk_1` FOREIGN KEY (`village_official_id`) REFERENCES `shebao_village_official` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='村干部任职信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shebao_village_official_position`
--

INSERT INTO `shebao_village_official_position` VALUES (1,1,'1','2025-08-01','2025-09-30','0','2','admin','2025-09-28 22:18:31','','2025-09-28 23:50:52',NULL),(2,1,'2','2025-09-30','2026-09-30','0','2','admin','2025-09-28 22:18:31','','2025-09-28 23:50:52',NULL),(3,1,'2','2025-09-30','2026-09-30','0','0','admin','2025-09-28 23:50:52','','2025-09-28 23:50:52','1'),(4,1,'1','2025-08-01','2025-09-30','0','0','admin','2025-09-28 23:50:52','','2025-09-28 23:50:52','2'),(5,2,'1','2018-07-07','2025-10-15','0','0','admin','2025-10-11 10:58:40','','2025-10-11 10:58:39',NULL);

--
-- Table structure for table `sys_config`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2025-09-24 22:11:39','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2025-09-24 22:11:39','',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2025-09-24 22:11:39','',NULL,'深色主题theme-dark，浅色主题theme-light'),(4,'账号自助-验证码开关','sys.account.captchaEnabled','false','Y','admin','2025-09-24 22:11:39','admin','2025-09-26 17:27:23','是否开启验证码功能（true开启，false关闭）'),(5,'账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y','admin','2025-09-24 22:11:39','',NULL,'是否开启注册用户功能（true开启，false关闭）'),(6,'用户登录-黑名单列表','sys.login.blackIPList','','Y','admin','2025-09-24 22:11:39','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),(7,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1','Y','admin','2025-09-24 22:11:39','',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框'),(8,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0','Y','admin','2025-09-24 22:11:39','',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框'),(100,'社保-年平均工资','shebao.average.annual.salary','56724','N','admin','2025-09-28 22:47:18','',NULL,NULL);

--
-- Table structure for table `sys_dept`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

INSERT INTO `sys_dept` VALUES (100,0,'0','社保局',0,'','','','0','0','admin','2025-09-24 22:11:35','admin','2025-09-26 17:36:50'),(101,100,'0,100','办公室',1,'','','','0','0','admin','2025-09-24 22:11:35','admin','2025-09-26 18:29:55'),(102,100,'0,100','长沙分公司',2,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(103,101,'0,100,101','研发部门',1,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(104,101,'0,100,101','市场部门',2,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(105,101,'0,100,101','测试部门',3,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(106,101,'0,100,101','财务部门',4,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(107,101,'0,100,101','运维部门',5,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(108,102,'0,100,102','市场部门',1,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL),(109,102,'0,100,102','财务部门',2,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-24 22:11:35','',NULL);

--
-- Table structure for table `sys_dict_data`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

INSERT INTO `sys_dict_data` VALUES (1,1,'男','1','sys_user_sex','','','Y','0','admin','2025-09-24 22:11:38','',NULL,'性别男'),(2,2,'女','2','sys_user_sex','','','N','0','admin','2025-09-24 22:11:38','',NULL,'性别女'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2025-09-24 22:11:38','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2025-09-24 22:11:38','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2025-09-24 22:11:38','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2025-09-24 22:11:38','',NULL,'停用状态'),(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2025-09-24 22:11:38','',NULL,'正常状态'),(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2025-09-24 22:11:38','',NULL,'停用状态'),(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2025-09-24 22:11:38','',NULL,'默认分组'),(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2025-09-24 22:11:38','',NULL,'系统分组'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2025-09-24 22:11:38','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2025-09-24 22:11:38','',NULL,'系统默认否'),(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2025-09-24 22:11:38','',NULL,'通知'),(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2025-09-24 22:11:38','',NULL,'公告'),(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2025-09-24 22:11:38','',NULL,'正常状态'),(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2025-09-24 22:11:38','',NULL,'关闭状态'),(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2025-09-24 22:11:38','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2025-09-24 22:11:38','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2025-09-24 22:11:38','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2025-09-24 22:11:38','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2025-09-24 22:11:38','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2025-09-24 22:11:38','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2025-09-24 22:11:38','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2025-09-24 22:11:39','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2025-09-24 22:11:39','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2025-09-24 22:11:39','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2025-09-24 22:11:39','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2025-09-24 22:11:39','',NULL,'停用状态'),(100,1,'失地居民补贴','1','subsidy_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'失地居民补贴'),(101,2,'被征地居民补贴','2','subsidy_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'被征地居民补贴'),(102,3,'拆迁居民补贴','3','subsidy_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'拆迁居民补贴'),(103,4,'村干部补贴','4','subsidy_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'村干部补贴'),(104,1,'未发放','0','distribution_status',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'未发放'),(105,2,'待审核','1','distribution_status',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'待审核'),(106,3,'待发放','2','distribution_status',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'待发放'),(107,4,'已驳回','3','distribution_status',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'已驳回'),(108,5,'已发放','4','distribution_status',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'已发放'),(109,1,'提交审核','1','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'提交审核'),(110,2,'审核通过','2','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'审核通过'),(111,3,'审核驳回','3','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'审核驳回'),(112,4,'发放','4','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'发放'),(113,5,'重新提交','5','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-15 21:31:43','',NULL,'重新提交'),(114,1,'失地居民补贴','1','subsidy_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:13','',NULL,'失地居民补贴'),(115,2,'被征地居民补贴','2','subsidy_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:13','',NULL,'被征地居民补贴'),(116,3,'拆迁居民补贴','3','subsidy_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:13','',NULL,'拆迁居民补贴'),(117,4,'村干部补贴','4','subsidy_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:13','',NULL,'村干部补贴'),(118,1,'未发放','0','distribution_status',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'未发放'),(119,2,'待审核','1','distribution_status',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'待审核'),(120,3,'待发放','2','distribution_status',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'待发放'),(121,4,'已驳回','3','distribution_status',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'已驳回'),(122,5,'已发放','4','distribution_status',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'已发放'),(123,1,'提交审核','1','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'提交审核'),(124,2,'审核通过','2','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'审核通过'),(125,3,'审核驳回','3','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'审核驳回'),(126,4,'发放','4','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'发放'),(127,5,'重新提交','5','distribution_operation_type',NULL,NULL,'N','0','admin','2025-10-18 21:32:14','',NULL,'重新提交');

--
-- Table structure for table `sys_dict_type`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex','0','admin','2025-09-24 22:11:38','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','admin','2025-09-24 22:11:38','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','admin','2025-09-24 22:11:38','',NULL,'系统开关列表'),(4,'任务状态','sys_job_status','0','admin','2025-09-24 22:11:38','',NULL,'任务状态列表'),(5,'任务分组','sys_job_group','0','admin','2025-09-24 22:11:38','',NULL,'任务分组列表'),(6,'系统是否','sys_yes_no','0','admin','2025-09-24 22:11:38','',NULL,'系统是否列表'),(7,'通知类型','sys_notice_type','0','admin','2025-09-24 22:11:38','',NULL,'通知类型列表'),(8,'通知状态','sys_notice_status','0','admin','2025-09-24 22:11:38','',NULL,'通知状态列表'),(9,'操作类型','sys_oper_type','0','admin','2025-09-24 22:11:38','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','admin','2025-09-24 22:11:38','',NULL,'登录状态列表'),(100,'补贴类型','subsidy_type','0','admin','2025-10-15 21:31:43','',NULL,'补贴类型列表'),(101,'发放状态','distribution_status','0','admin','2025-10-15 21:31:43','',NULL,'补贴发放状态列表'),(102,'发放操作类型','distribution_operation_type','0','admin','2025-10-15 21:31:43','',NULL,'发放操作类型列表');

--
-- Table structure for table `sys_job`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job`
--

INSERT INTO `sys_job` VALUES (1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','admin','2025-09-24 22:11:39','',NULL,''),(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(\'ry\')','0/15 * * * * ?','3','1','1','admin','2025-09-24 22:11:39','',NULL,''),(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','admin','2025-09-24 22:11:39','',NULL,'');

--
-- Table structure for table `sys_job_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job_log`
--


--
-- Table structure for table `sys_logininfor`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=187 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_logininfor`
--

INSERT INTO `sys_logininfor` VALUES (100,'admin','127.0.0.1','内网IP','Safari','Mac OS X','0','登录成功','2025-09-24 22:41:20'),(101,'admin','127.0.0.1','内网IP','Safari','Mac OS X','1','验证码已失效','2025-09-26 17:26:01'),(102,'admin','127.0.0.1','内网IP','Safari','Mac OS X','0','登录成功','2025-09-26 17:26:08'),(103,'admin','127.0.0.1','内网IP','Safari','Mac OS X','0','退出成功','2025-09-26 17:35:41'),(104,'admin','127.0.0.1','内网IP','Safari','Mac OS X','0','登录成功','2025-09-26 17:35:44'),(105,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-26 18:26:31'),(106,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-27 10:36:24'),(107,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-27 11:46:01'),(108,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-27 16:01:41'),(109,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-27 20:42:47'),(110,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-28 01:42:49'),(111,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-28 12:15:16'),(112,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-28 21:42:56'),(113,'admin','127.0.0.1','内网IP','Safari','Mac OS X','0','登录成功','2025-09-29 00:00:16'),(114,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2025-09-29 10:38:29'),(115,'admin','222.128.127.244','XX XX','Chrome 12','Mac OS X','0','登录成功','2025-09-29 10:53:52'),(116,'admin','223.104.3.162','XX XX','Chrome 14','Windows 10','0','登录成功','2025-09-29 11:04:30'),(117,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-29 11:21:35'),(118,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-09-29 20:55:43'),(119,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','1','用户不存在/密码错误','2025-10-10 10:20:22'),(120,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2025-10-10 10:20:26'),(121,'admin','101.29.186.209','XX XX','Chrome 10','Windows 10','0','登录成功','2025-10-10 10:27:34'),(122,'admin','111.55.150.107','XX XX','Chrome Mobile','Android 1.x','0','登录成功','2025-10-10 10:28:45'),(123,'admin','27.128.48.201','XX XX','Chrome Mobile','Android 1.x','0','登录成功','2025-10-10 10:31:31'),(124,'admin','222.128.127.244','XX XX','Safari','Mac OS X','0','登录成功','2025-10-10 10:41:15'),(125,'admin','60.10.47.214','XX XX','Chrome Mobile','Android 1.x','0','登录成功','2025-10-10 10:41:36'),(126,'admin','60.10.47.214','XX XX','Apple WebKit','Mac OS X (iPhone)','0','登录成功','2025-10-10 10:53:16'),(127,'admin','222.128.127.244','XX XX','Chrome 13','Mac OS X','0','登录成功','2025-10-10 10:54:34'),(128,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-10 10:55:58'),(129,'admin','117.136.47.166','XX XX','Apple WebKit','Mac OS X (iPhone)','0','登录成功','2025-10-10 10:59:59'),(130,'admin','223.160.137.16','XX XX','Chrome Mobile','Android 1.x','0','登录成功','2025-10-10 11:00:01'),(131,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-10 11:00:52'),(132,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-10 11:01:57'),(133,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-10 12:19:01'),(134,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2025-10-10 16:09:09'),(135,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2025-10-10 16:17:13'),(136,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-11 10:30:12'),(137,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2025-10-11 10:36:17'),(138,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-11 14:25:38'),(139,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-11 16:11:43'),(140,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2025-10-11 16:20:15'),(141,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2025-10-11 17:10:52'),(142,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-13 09:44:07'),(143,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2025-10-13 10:00:56'),(144,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2025-10-13 15:33:31'),(145,'admin','120.244.162.197','XX XX','Chrome 14','Windows 10','0','登录成功','2025-10-14 10:18:02'),(146,'admin','222.128.127.244','XX XX','Safari','Mac OS X','0','登录成功','2025-10-14 10:47:45'),(147,'admin','222.128.127.244','XX XX','Safari','Mac OS X','0','退出成功','2025-10-14 10:50:42'),(148,'admin','222.128.127.244','XX XX','Safari','Mac OS X','0','登录成功','2025-10-14 10:51:08'),(149,'admin','222.128.127.244','XX XX','Safari','Mac OS X','0','退出成功','2025-10-14 10:52:25'),(150,'admin','222.128.127.244','XX XX','Safari','Mac OS X','0','登录成功','2025-10-14 10:52:34'),(151,'admin','27.128.21.83','XX XX','Apple WebKit','Mac OS X (iPhone)','1','用户不存在/密码错误','2025-10-14 10:56:06'),(152,'admin','27.128.21.83','XX XX','Apple WebKit','Mac OS X (iPhone)','1','用户不存在/密码错误','2025-10-14 10:56:15'),(153,'admin','27.128.21.83','XX XX','Apple WebKit','Mac OS X (iPhone)','1','用户不存在/密码错误','2025-10-14 10:59:04'),(154,'admin','27.128.21.83','XX XX','Apple WebKit','Mac OS X (iPhone)','0','登录成功','2025-10-14 11:01:56'),(155,'admin','60.10.47.214','XX XX','Apple WebKit','Mac OS X (iPhone)','0','登录成功','2025-10-14 11:52:05'),(156,'admin','60.10.59.193','XX XX','Chrome Mobile','Android 1.x','1','用户不存在/密码错误','2025-10-15 09:53:49'),(157,'admin','60.10.59.193','XX XX','Chrome Mobile','Android 1.x','0','登录成功','2025-10-15 09:54:09'),(158,'admin','60.10.47.214','XX XX','Apple WebKit','Mac OS X (iPhone)','0','登录成功','2025-10-15 09:55:48'),(159,'admin','60.10.47.214','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2025-10-15 15:12:56'),(160,'admin','60.10.47.214','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-15 15:16:11'),(161,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','1','用户不存在/密码错误','2025-10-15 21:42:01'),(162,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-15 21:44:24'),(163,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-16 10:43:58'),(164,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-16 14:14:07'),(165,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-16 15:34:38'),(166,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-18 21:08:05'),(167,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 00:26:07'),(168,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 10:36:54'),(169,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 12:38:41'),(170,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 13:48:54'),(171,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 15:13:26'),(172,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 17:15:27'),(173,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 18:27:47'),(174,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-19 20:25:41'),(175,'admin','127.0.0.1','内网IP','Chrome 14','Mac OS X','0','登录成功','2025-10-20 00:25:09'),(176,'admin','222.128.127.244','XX XX','Chrome 14','Mac OS X','0','登录成功','2025-10-20 01:04:28'),(177,'admin','36.98.90.0','XX XX','Chrome 13','Android 1.x','0','登录成功','2025-10-20 08:59:56'),(178,'admin','60.10.47.214','XX XX','Apple WebKit','Mac OS X (iPhone)','0','登录成功','2025-10-20 09:40:57'),(179,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-20 11:00:46'),(180,'admin','60.10.59.193','XX XX','Chrome 12','Windows 10','0','登录成功','2025-10-20 13:51:35'),(181,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-20 14:23:08'),(182,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2025-10-20 16:19:49'),(183,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','1','用户不存在/密码错误','2025-10-20 16:49:03'),(184,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-20 16:50:26'),(185,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-21 15:13:47'),(186,'admin','60.10.59.193','XX XX','Chrome 13','Windows 10','0','登录成功','2025-10-22 15:00:27');

--
-- Table structure for table `sys_menu`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2033 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

INSERT INTO `sys_menu` VALUES (1,'系统管理',0,101,'system',NULL,'','',1,0,'M','0','0','','system','admin','2025-09-24 22:11:36','admin','2025-09-27 10:44:48','系统管理目录'),(2,'系统监控',0,201,'monitor',NULL,'','',1,0,'M','1','1','','monitor','admin','2025-09-24 22:11:36','admin','2025-09-29 00:14:30','系统监控目录'),(3,'系统工具',0,202,'tool',NULL,'','',1,0,'M','1','1','','tool','admin','2025-09-24 22:11:36','admin','2025-09-29 00:13:11','系统工具目录'),(100,'用户管理',1,1,'user','system/user/index','','',1,0,'C','0','0','system:user:list','user','admin','2025-09-24 22:11:36','',NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','system/role/index','','',1,0,'C','0','0','system:role:list','peoples','admin','2025-09-24 22:11:36','',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','system/menu/index','','',1,0,'C','0','0','system:menu:list','tree-table','admin','2025-09-24 22:11:36','',NULL,'菜单管理菜单'),(103,'部门管理',1,4,'dept','system/dept/index','','',1,0,'C','0','0','system:dept:list','tree','admin','2025-09-24 22:11:36','',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','system/post/index','','',1,0,'C','1','1','system:post:list','post','admin','2025-09-24 22:11:36','admin','2025-09-29 00:15:17','岗位管理菜单'),(105,'字典管理',1,6,'dict','system/dict/index','','',1,0,'C','0','0','system:dict:list','dict','admin','2025-09-24 22:11:36','',NULL,'字典管理菜单'),(106,'参数设置',1,7,'config','system/config/index','','',1,0,'C','0','0','system:config:list','edit','admin','2025-09-24 22:11:36','',NULL,'参数设置菜单'),(107,'通知公告',1,8,'notice','system/notice/index','','',1,0,'C','1','1','system:notice:list','message','admin','2025-09-24 22:11:36','admin','2025-09-29 00:15:08','通知公告菜单'),(108,'日志管理',1,9,'log','','','',1,0,'M','0','0','','log','admin','2025-09-24 22:11:36','',NULL,'日志管理菜单'),(109,'在线用户',2,1,'online','monitor/online/index','','',1,0,'C','0','0','monitor:online:list','online','admin','2025-09-24 22:11:36','',NULL,'在线用户菜单'),(110,'定时任务',2,2,'job','monitor/job/index','','',1,0,'C','0','0','monitor:job:list','job','admin','2025-09-24 22:11:36','',NULL,'定时任务菜单'),(111,'数据监控',2,3,'druid','monitor/druid/index','','',1,0,'C','0','0','monitor:druid:list','druid','admin','2025-09-24 22:11:36','',NULL,'数据监控菜单'),(112,'服务监控',2,4,'server','monitor/server/index','','',1,0,'C','0','0','monitor:server:list','server','admin','2025-09-24 22:11:36','',NULL,'服务监控菜单'),(113,'缓存监控',2,5,'cache','monitor/cache/index','','',1,0,'C','0','0','monitor:cache:list','redis','admin','2025-09-24 22:11:36','',NULL,'缓存监控菜单'),(114,'缓存列表',2,6,'cacheList','monitor/cache/list','','',1,0,'C','0','0','monitor:cache:list','redis-list','admin','2025-09-24 22:11:36','',NULL,'缓存列表菜单'),(115,'表单构建',3,1,'build','tool/build/index','','',1,0,'C','0','0','tool:build:list','build','admin','2025-09-24 22:11:36','',NULL,'表单构建菜单'),(116,'代码生成',3,2,'gen','tool/gen/index','','',1,0,'C','0','0','tool:gen:list','code','admin','2025-09-24 22:11:36','',NULL,'代码生成菜单'),(117,'系统接口',3,3,'swagger','tool/swagger/index','','',1,0,'C','0','0','tool:swagger:list','swagger','admin','2025-09-24 22:11:36','',NULL,'系统接口菜单'),(500,'操作日志',108,1,'operlog','monitor/operlog/index','','',1,0,'C','0','0','monitor:operlog:list','form','admin','2025-09-24 22:11:36','',NULL,'操作日志菜单'),(501,'登录日志',108,2,'logininfor','monitor/logininfor/index','','',1,0,'C','0','0','monitor:logininfor:list','logininfor','admin','2025-09-24 22:11:36','',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'','','','',1,0,'F','0','0','system:user:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1001,'用户新增',100,2,'','','','',1,0,'F','0','0','system:user:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1002,'用户修改',100,3,'','','','',1,0,'F','0','0','system:user:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1003,'用户删除',100,4,'','','','',1,0,'F','0','0','system:user:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1004,'用户导出',100,5,'','','','',1,0,'F','0','0','system:user:export','#','admin','2025-09-24 22:11:36','',NULL,''),(1005,'用户导入',100,6,'','','','',1,0,'F','0','0','system:user:import','#','admin','2025-09-24 22:11:36','',NULL,''),(1006,'重置密码',100,7,'','','','',1,0,'F','0','0','system:user:resetPwd','#','admin','2025-09-24 22:11:36','',NULL,''),(1007,'角色查询',101,1,'','','','',1,0,'F','0','0','system:role:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1008,'角色新增',101,2,'','','','',1,0,'F','0','0','system:role:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1009,'角色修改',101,3,'','','','',1,0,'F','0','0','system:role:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1010,'角色删除',101,4,'','','','',1,0,'F','0','0','system:role:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1011,'角色导出',101,5,'','','','',1,0,'F','0','0','system:role:export','#','admin','2025-09-24 22:11:36','',NULL,''),(1012,'菜单查询',102,1,'','','','',1,0,'F','0','0','system:menu:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1013,'菜单新增',102,2,'','','','',1,0,'F','0','0','system:menu:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1014,'菜单修改',102,3,'','','','',1,0,'F','0','0','system:menu:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1015,'菜单删除',102,4,'','','','',1,0,'F','0','0','system:menu:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1016,'部门查询',103,1,'','','','',1,0,'F','0','0','system:dept:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1017,'部门新增',103,2,'','','','',1,0,'F','0','0','system:dept:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1018,'部门修改',103,3,'','','','',1,0,'F','0','0','system:dept:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1019,'部门删除',103,4,'','','','',1,0,'F','0','0','system:dept:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1020,'岗位查询',104,1,'','','','',1,0,'F','0','0','system:post:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1021,'岗位新增',104,2,'','','','',1,0,'F','0','0','system:post:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1022,'岗位修改',104,3,'','','','',1,0,'F','0','0','system:post:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1023,'岗位删除',104,4,'','','','',1,0,'F','0','0','system:post:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1024,'岗位导出',104,5,'','','','',1,0,'F','0','0','system:post:export','#','admin','2025-09-24 22:11:36','',NULL,''),(1025,'字典查询',105,1,'#','','','',1,0,'F','0','0','system:dict:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1026,'字典新增',105,2,'#','','','',1,0,'F','0','0','system:dict:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1027,'字典修改',105,3,'#','','','',1,0,'F','0','0','system:dict:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1028,'字典删除',105,4,'#','','','',1,0,'F','0','0','system:dict:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1029,'字典导出',105,5,'#','','','',1,0,'F','0','0','system:dict:export','#','admin','2025-09-24 22:11:36','',NULL,''),(1030,'参数查询',106,1,'#','','','',1,0,'F','0','0','system:config:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1031,'参数新增',106,2,'#','','','',1,0,'F','0','0','system:config:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1032,'参数修改',106,3,'#','','','',1,0,'F','0','0','system:config:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1033,'参数删除',106,4,'#','','','',1,0,'F','0','0','system:config:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1034,'参数导出',106,5,'#','','','',1,0,'F','0','0','system:config:export','#','admin','2025-09-24 22:11:36','',NULL,''),(1035,'公告查询',107,1,'#','','','',1,0,'F','0','0','system:notice:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1036,'公告新增',107,2,'#','','','',1,0,'F','0','0','system:notice:add','#','admin','2025-09-24 22:11:36','',NULL,''),(1037,'公告修改',107,3,'#','','','',1,0,'F','0','0','system:notice:edit','#','admin','2025-09-24 22:11:36','',NULL,''),(1038,'公告删除',107,4,'#','','','',1,0,'F','0','0','system:notice:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1039,'操作查询',500,1,'#','','','',1,0,'F','0','0','monitor:operlog:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1040,'操作删除',500,2,'#','','','',1,0,'F','0','0','monitor:operlog:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1041,'日志导出',500,3,'#','','','',1,0,'F','0','0','monitor:operlog:export','#','admin','2025-09-24 22:11:36','',NULL,''),(1042,'登录查询',501,1,'#','','','',1,0,'F','0','0','monitor:logininfor:query','#','admin','2025-09-24 22:11:36','',NULL,''),(1043,'登录删除',501,2,'#','','','',1,0,'F','0','0','monitor:logininfor:remove','#','admin','2025-09-24 22:11:36','',NULL,''),(1044,'日志导出',501,3,'#','','','',1,0,'F','0','0','monitor:logininfor:export','#','admin','2025-09-24 22:11:37','',NULL,''),(1045,'账户解锁',501,4,'#','','','',1,0,'F','0','0','monitor:logininfor:unlock','#','admin','2025-09-24 22:11:37','',NULL,''),(1046,'在线查询',109,1,'#','','','',1,0,'F','0','0','monitor:online:query','#','admin','2025-09-24 22:11:37','',NULL,''),(1047,'批量强退',109,2,'#','','','',1,0,'F','0','0','monitor:online:batchLogout','#','admin','2025-09-24 22:11:37','',NULL,''),(1048,'单条强退',109,3,'#','','','',1,0,'F','0','0','monitor:online:forceLogout','#','admin','2025-09-24 22:11:37','',NULL,''),(1049,'任务查询',110,1,'#','','','',1,0,'F','0','0','monitor:job:query','#','admin','2025-09-24 22:11:37','',NULL,''),(1050,'任务新增',110,2,'#','','','',1,0,'F','0','0','monitor:job:add','#','admin','2025-09-24 22:11:37','',NULL,''),(1051,'任务修改',110,3,'#','','','',1,0,'F','0','0','monitor:job:edit','#','admin','2025-09-24 22:11:37','',NULL,''),(1052,'任务删除',110,4,'#','','','',1,0,'F','0','0','monitor:job:remove','#','admin','2025-09-24 22:11:37','',NULL,''),(1053,'状态修改',110,5,'#','','','',1,0,'F','0','0','monitor:job:changeStatus','#','admin','2025-09-24 22:11:37','',NULL,''),(1054,'任务导出',110,6,'#','','','',1,0,'F','0','0','monitor:job:export','#','admin','2025-09-24 22:11:37','',NULL,''),(1055,'生成查询',116,1,'#','','','',1,0,'F','0','0','tool:gen:query','#','admin','2025-09-24 22:11:37','',NULL,''),(1056,'生成修改',116,2,'#','','','',1,0,'F','0','0','tool:gen:edit','#','admin','2025-09-24 22:11:37','',NULL,''),(1057,'生成删除',116,3,'#','','','',1,0,'F','0','0','tool:gen:remove','#','admin','2025-09-24 22:11:37','',NULL,''),(1058,'导入代码',116,4,'#','','','',1,0,'F','0','0','tool:gen:import','#','admin','2025-09-24 22:11:37','',NULL,''),(1059,'预览代码',116,5,'#','','','',1,0,'F','0','0','tool:gen:preview','#','admin','2025-09-24 22:11:37','',NULL,''),(1060,'生成代码',116,6,'#','','','',1,0,'F','0','0','tool:gen:code','#','admin','2025-09-24 22:11:37','',NULL,''),(2000,'基础数据',0,1,'base',NULL,NULL,'',1,0,'M','0','0',NULL,'dict','admin','2025-09-27 10:41:46','',NULL,''),(2002,'行政区划',2000,1,'division','shebao/administrativeDivision/index',NULL,'行政区划',1,0,'C','1','1','shebao:administrativeDivision:list','tree-table','admin','2025-09-27 20:46:54','admin','2025-10-19 22:36:45',''),(2003,'补贴居民',0,3,'person',NULL,NULL,'',1,0,'M','0','0','','peoples','admin','2025-09-28 01:43:38','admin','2025-09-29 11:17:03',''),(2004,'人员信息',2000,0,'baseinfo','shebao/subsidyPerson/index',NULL,'人员信息',1,0,'C','0','0','','people','admin','2025-09-28 01:46:52','admin','2025-09-29 11:30:50',''),(2005,'失地居民',2003,2,'lost-land','shebao/landLossResident/index',NULL,'失地居民',1,0,'C','0','0',NULL,'#','admin','2025-09-28 02:16:59','',NULL,''),(2006,'被征地居民',2003,3,'expropriatee','shebao/expropriateeSubsidy/index',NULL,'被征地居民',1,0,'C','0','0',NULL,'#','admin','2025-09-28 12:20:09','',NULL,''),(2007,'拆迁居民',2003,4,'demolition','shebao/demolitionResident/index',NULL,'拆迁居民',1,0,'C','0','0',NULL,'#','admin','2025-09-28 12:21:34','',NULL,''),(2008,'村干部',2003,5,'village-official','shebao/villageOfficial/index',NULL,'村干部',1,0,'C','0','0',NULL,'#','admin','2025-09-28 12:22:16','',NULL,''),(2009,'补贴发放',0,4,'assign',NULL,NULL,'',1,0,'M','0','0','','form','admin','2025-09-29 11:15:25','admin','2025-09-29 11:19:05',''),(2011,'统计查询',2009,2,'residentQuery','shebao/residentQuery/index',NULL,'统计查询',1,0,'C','0','0','','search','admin','2025-09-29 11:18:05','admin','2025-10-20 00:27:29',''),(2012,'补贴设置',2000,3,'settings','shebao/config/index',NULL,'补贴设置',1,0,'C','0','0','','dict','admin','2025-09-29 20:59:55','admin','2025-09-29 21:01:21',''),(2013,'补贴发放记录',2009,1,'subsidyDistribution','shebao/subsidyDistribution/index',NULL,'',1,0,'C','0','0','shebao:subsidyDistribution:list','money','admin','2025-10-15 21:31:43','admin','2025-10-18 21:26:53','补贴发放记录菜单'),(2014,'补贴发放记录查询',2013,1,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:query','#','admin','2025-10-15 21:31:43','',NULL,''),(2015,'补贴发放记录新增',2013,2,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:add','#','admin','2025-10-15 21:31:43','',NULL,''),(2016,'补贴发放记录删除',2013,3,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:remove','#','admin','2025-10-15 21:31:43','',NULL,''),(2017,'补贴发放审核',2013,4,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:approve','#','admin','2025-10-15 21:31:43','',NULL,''),(2018,'补贴发放驳回',2013,5,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:reject','#','admin','2025-10-15 21:31:43','',NULL,''),(2019,'补贴发放',2013,6,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:distribute','#','admin','2025-10-15 21:31:43','',NULL,''),(2020,'补贴重新提交',2013,7,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:resubmit','#','admin','2025-10-15 21:31:43','',NULL,''),(2021,'补贴发放记录导出',2013,8,'#','',NULL,'',1,0,'F','0','0','shebao:subsidyDistribution:export','#','admin','2025-10-15 21:31:43','',NULL,''),(2031,'街道办',2000,4,'streetOffice','shebao/streetOffice/index',NULL,'街道办',1,0,'C','0','0','shebao:streetOffice:list','tree-table','admin','2025-10-19 10:38:24','admin','2025-10-19 23:17:16',''),(2032,'村委会',2000,6,'villageCommittee','shebao/villageCommittee/index',NULL,'村委会',1,0,'C','0','0','shebao:villageCommittee:list','tree-table','admin','2025-10-19 10:39:40','admin','2025-10-19 22:37:17','');

--
-- Table structure for table `sys_notice`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

INSERT INTO `sys_notice` VALUES (1,'温馨提醒：2018-07-01 新版本发布啦','2',_binary '新版本内容','0','admin','2025-09-24 22:11:39','admin','2025-09-29 00:12:20','管理员'),(2,'维护通知：2018-07-01 系统凌晨维护','1',_binary '<p>维护内容</p>','0','admin','2025-09-24 22:11:39','admin','2025-09-29 00:12:26','管理员');

--
-- Table structure for table `sys_oper_log`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=287 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

INSERT INTO `sys_oper_log` VALUES (100,'参数管理',2,'com.ruoyi.web.controller.system.SysConfigController.edit()','PUT',1,'admin','研发部门','/system/config','127.0.0.1','内网IP','{\"configId\":4,\"configKey\":\"sys.account.captchaEnabled\",\"configName\":\"账号自助-验证码开关\",\"configType\":\"Y\",\"configValue\":\"false\",\"createBy\":\"admin\",\"createTime\":\"2025-09-24 22:11:39\",\"params\":{},\"remark\":\"是否开启验证码功能（true开启，false关闭）\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-26 17:27:23',41),(101,'部门管理',2,'com.ruoyi.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0\",\"children\":[],\"deptId\":100,\"deptName\":\"社保局\",\"email\":\"\",\"leader\":\"\",\"orderNum\":0,\"params\":{},\"parentId\":0,\"phone\":\"\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-26 17:36:50',56),(102,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/102','127.0.0.1','内网IP','102','{\"msg\":\"存在下级部门,不允许删除\",\"code\":601}',0,NULL,'2025-09-26 17:36:58',11),(103,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/108','127.0.0.1','内网IP','108','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-26 17:37:01',31),(104,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/109','127.0.0.1','内网IP','109','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-26 17:37:03',29),(105,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/102','127.0.0.1','内网IP','102','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-26 17:37:05',29),(106,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/107','127.0.0.1','内网IP','107','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-26 17:37:08',29),(107,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/106','127.0.0.1','内网IP','106','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-26 17:37:11',29),(108,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/105','127.0.0.1','内网IP','105','{\"msg\":\"部门存在用户,不允许删除\",\"code\":601}',0,NULL,'2025-09-26 17:37:13',17),(109,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/104','127.0.0.1','内网IP','104','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-26 17:37:15',28),(110,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/103','127.0.0.1','内网IP','103','{\"msg\":\"部门存在用户,不允许删除\",\"code\":601}',0,NULL,'2025-09-26 17:37:18',15),(111,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/101','127.0.0.1','内网IP','101','{\"msg\":\"存在下级部门,不允许删除\",\"code\":601}',0,NULL,'2025-09-26 17:37:20',10),(112,'用户管理',3,'com.ruoyi.web.controller.system.SysUserController.remove()','DELETE',1,'admin','研发部门','/system/user/2','127.0.0.1','内网IP','[2]','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-26 17:37:32',66),(113,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/103','127.0.0.1','内网IP','103','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-26 18:29:21',30),(114,'部门管理',3,'com.ruoyi.web.controller.system.SysDeptController.remove()','DELETE',1,'admin','研发部门','/system/dept/105','127.0.0.1','内网IP','105','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-26 18:29:23',28),(115,'部门管理',2,'com.ruoyi.web.controller.system.SysDeptController.edit()','PUT',1,'admin','研发部门','/system/dept','127.0.0.1','内网IP','{\"ancestors\":\"0,100\",\"children\":[],\"deptId\":101,\"deptName\":\"办公室\",\"email\":\"\",\"leader\":\"\",\"orderNum\":1,\"params\":{},\"parentId\":100,\"parentName\":\"社保局\",\"phone\":\"\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-26 18:29:55',72),(116,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"dict\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"基础数据\",\"menuType\":\"M\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"base\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 10:41:46',38),(117,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/village/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"村级单位管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2000,\"path\":\"village\",\"routeName\":\"村级单位管理\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 10:43:11',29),(118,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/village/index\",\"createTime\":\"2025-09-27 10:43:11\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"村级单位管理\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2000,\"path\":\"village\",\"perms\":\"shebao:village:list\",\"routeName\":\"村级单位管理\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 10:44:37',25),(119,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2025-09-24 22:11:36\",\"icon\":\"system\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":1,\"menuName\":\"系统管理\",\"menuType\":\"M\",\"orderNum\":101,\"params\":{},\"parentId\":0,\"path\":\"system\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 10:44:48',22),(120,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2025-09-24 22:11:36\",\"icon\":\"monitor\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2,\"menuName\":\"系统监控\",\"menuType\":\"M\",\"orderNum\":201,\"params\":{},\"parentId\":0,\"path\":\"monitor\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 10:44:56',24),(121,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2025-09-24 22:11:36\",\"icon\":\"tool\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3,\"menuName\":\"系统工具\",\"menuType\":\"M\",\"orderNum\":202,\"params\":{},\"parentId\":0,\"path\":\"tool\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 10:45:02',21),(122,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/system/menu/4','127.0.0.1','内网IP','4','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2025-09-27 10:45:05',14),(123,'角色管理',2,'com.ruoyi.web.controller.system.SysRoleController.edit()','PUT',1,'admin','社保局','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2025-09-24 22:11:36\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2001,1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,114,3,115,116,1055,1056,1057,1058,1059,1060,117],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 10:45:19',80),(124,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/system/menu/4','127.0.0.1','内网IP','4','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 10:45:25',23),(125,'村级单位',1,'com.ruoyi.shebao.controller.VillageController.add()','POST',1,'admin','社保局','/shebao/village','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-27 11:51:24\",\"fullCode\":\"130000/131000/131001/131001001/123\",\"fullName\":\"河北省/廊坊市/安次区/银河南路街道/大老刘村\",\"params\":{},\"parentCode\":\"131001001\",\"sortOrder\":0,\"status\":\"0\",\"villageCode\":\"123\",\"villageName\":\"大老刘村\"}',NULL,1,'\n### Error updating database.  Cause: java.lang.IllegalStateException: Type handler was null on parameter mapping for property \'params\'. It was either not specified and/or could not be found for the javaType (java.util.Map) : jdbcType (null) combination.\n### The error may exist in com/ruoyi/shebao/mapper/VillageMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.VillageMapper.insert\n### The error occurred while executing an update\n### Cause: java.lang.IllegalStateException: Type handler was null on parameter mapping for property \'params\'. It was either not specified and/or could not be found for the javaType (java.util.Map) : jdbcType (null) combination.','2025-09-27 11:51:24',26),(126,'村级单位',1,'com.ruoyi.shebao.controller.VillageController.add()','POST',1,'admin','社保局','/shebao/village','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-27 11:51:29\",\"fullCode\":\"130000/131000/131001/131001001/123\",\"fullName\":\"河北省/廊坊市/安次区/银河南路街道/大老刘村\",\"params\":{},\"parentCode\":\"131001001\",\"sortOrder\":0,\"status\":\"0\",\"villageCode\":\"123\",\"villageName\":\"大老刘村\"}',NULL,1,'\n### Error updating database.  Cause: java.lang.IllegalStateException: Type handler was null on parameter mapping for property \'params\'. It was either not specified and/or could not be found for the javaType (java.util.Map) : jdbcType (null) combination.\n### The error may exist in com/ruoyi/shebao/mapper/VillageMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.VillageMapper.insert\n### The error occurred while executing an update\n### Cause: java.lang.IllegalStateException: Type handler was null on parameter mapping for property \'params\'. It was either not specified and/or could not be found for the javaType (java.util.Map) : jdbcType (null) combination.','2025-09-27 11:51:29',16),(127,'村级单位',1,'com.ruoyi.shebao.controller.VillageController.add()','POST',1,'admin','社保局','/shebao/village','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-27 11:54:45\",\"fullCode\":\"130000/131000/131001/131001001/123\",\"fullName\":\"河北省/廊坊市/安次区/银河南路街道/大老刘村\",\"params\":{},\"parentCode\":\"131001001\",\"sortOrder\":0,\"status\":\"0\",\"villageCode\":\"123\",\"villageName\":\"大老刘村\"}',NULL,1,'\n### Error updating database.  Cause: java.lang.IllegalStateException: Type handler was null on parameter mapping for property \'params\'. It was either not specified and/or could not be found for the javaType (java.util.Map) : jdbcType (null) combination.\n### The error may exist in com/ruoyi/shebao/mapper/VillageMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.VillageMapper.insert\n### The error occurred while executing an update\n### Cause: java.lang.IllegalStateException: Type handler was null on parameter mapping for property \'params\'. It was either not specified and/or could not be found for the javaType (java.util.Map) : jdbcType (null) combination.','2025-09-27 11:54:45',27),(128,'村级单位',1,'com.ruoyi.shebao.controller.VillageController.add()','POST',1,'admin','社保局','/shebao/village','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-27 12:10:36.243198\",\"fullCode\":\"130000/131000/131001/131001001/1231\",\"fullName\":\"河北省/廊坊市/安次区/银河南路街道/大老王村\",\"parentCode\":\"131001001\",\"sortOrder\":0,\"status\":\"0\",\"villageCode\":\"1231\",\"villageName\":\"大老王村\"}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`langfang_shebao_test`.`shebao_village`, CONSTRAINT `shebao_village_ibfk_1` FOREIGN KEY (`village_code`) REFERENCES `shebao_administrative_division` (`division_code`))\n### The error may exist in com/ruoyi/shebao/mapper/VillageMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.VillageMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_village  ( village_code, village_name, parent_code, full_code, full_name,    sort_order, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?,    ?, ?, ?,  ?  )\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails (`langfang_shebao_test`.`shebao_village`, CONSTRAINT `shebao_village_ibfk_1` FOREIGN KEY (`village_code`) REFERENCES `shebao_administrative_division` (`division_code`))\n; Cannot add or update a child row: a foreign key constraint fails (`langfang_shebao_test`.`shebao_village`, CONSTRAINT `shebao_village_ibfk_1` FOREIGN KEY (`village_code`) REFERENCES `shebao_administrative_division` (`division_code`))','2025-09-27 12:10:36',162),(129,'村级单位',1,'com.ruoyi.shebao.controller.VillageController.add()','POST',1,'admin','社保局','/shebao/village','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-27 12:11:29.370281\",\"fullCode\":\"130000/131000/131001/131001001/1231\",\"fullName\":\"河北省/廊坊市/安次区/银河南路街道/大老王村\",\"id\":2,\"parentCode\":\"131001001\",\"sortOrder\":0,\"status\":\"0\",\"villageCode\":\"1231\",\"villageName\":\"大老王村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 12:11:29',39),(130,'村级单位',2,'com.ruoyi.shebao.controller.VillageController.changeStatus()','PUT',1,'admin','社保局','/shebao/village/changeStatus','127.0.0.1','内网IP','{\"id\":2,\"status\":\"1\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 16:02:00.289064\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 16:02:00',30),(131,'村级单位',2,'com.ruoyi.shebao.controller.VillageController.changeStatus()','PUT',1,'admin','社保局','/shebao/village/changeStatus','127.0.0.1','内网IP','{\"id\":2,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 16:02:04.756299\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 16:02:04',16),(132,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/village/index\",\"createTime\":\"2025-09-27 10:43:11\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"村级单位\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2000,\"path\":\"village\",\"perms\":\"shebao:village:list\",\"routeName\":\"村级单位管理\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 20:45:04',37),(133,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/administrativeDivision/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"行政区划\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2000,\"path\":\"division\",\"perms\":\"shebao:administrativeDivision:list\",\"routeName\":\"行政区划\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 20:46:54',36),(134,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/village/index\",\"createTime\":\"2025-09-27 10:43:11\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"村级单位\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2000,\"path\":\"village\",\"perms\":\"shebao:village:list\",\"routeName\":\"村级单位管理\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 20:46:58',27),(135,'行政区划',1,'com.ruoyi.shebao.controller.AdministrativeDivisionController.add()','POST',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"contactPerson\":\"111\",\"contactPhone\":\"222\",\"createBy\":\"admin\",\"createTime\":\"2025-09-27 20:49:47.557012\",\"divisionCode\":\"131001001001\",\"divisionLevel\":5,\"divisionName\":\"大老刘村\",\"fullCode\":\"130000/131000/131001/131001001/131001001001\",\"fullName\":\"河北省/廊坊市/安次区/银河南路街道/大老刘村\",\"id\":5,\"parentCode\":\"131001001\",\"sortOrder\":0,\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 20:49:47',40),(136,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"\",\"createTime\":\"2025-09-27 11:45:21\",\"delFlag\":\"0\",\"divisionCode\":\"131001\",\"divisionLevel\":3,\"divisionName\":\"安次区1\",\"fullCode\":\"130000/131000/131001\",\"fullName\":\"河北省/廊坊市/安次区1\",\"id\":3,\"parentCode\":\"131000\",\"sortOrder\":1,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 20:49:57.180056\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 20:49:57',34),(137,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"\",\"createTime\":\"2025-09-27 11:45:21\",\"delFlag\":\"0\",\"divisionCode\":\"131002\",\"divisionLevel\":3,\"divisionName\":\"安次区1\",\"fullCode\":\"130000/131000/131002\",\"fullName\":\"河北省/廊坊市/安次区1\",\"id\":3,\"parentCode\":\"131000\",\"sortOrder\":1,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 20:50:09.700773\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 20:50:09',28),(138,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"\",\"createTime\":\"2025-09-27 11:45:21\",\"delFlag\":\"0\",\"divisionCode\":\"131001\",\"divisionLevel\":3,\"divisionName\":\"安次区1\",\"fullCode\":\"130000/131000/131001\",\"fullName\":\"河北省/廊坊市/安次区1\",\"id\":3,\"parentCode\":\"131000\",\"sortOrder\":1,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 20:50:22.424380\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 20:50:22',28),(139,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"\",\"createTime\":\"2025-09-27 11:45:21\",\"delFlag\":\"0\",\"divisionCode\":\"131001\",\"divisionLevel\":3,\"divisionName\":\"安次区\",\"fullCode\":\"130000/131000/131001\",\"fullName\":\"河北省/廊坊市/安次区\",\"id\":3,\"parentCode\":\"131000\",\"sortOrder\":1,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 20:50:29.177835\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 20:50:29',33),(140,'行政区划',1,'com.ruoyi.shebao.controller.AdministrativeDivisionController.add()','POST',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"divisionCode\":\"131001001001\",\"divisionName\":\"刘老三村\",\"parentCode\":\"131001001\",\"sortOrder\":0,\"status\":\"0\"}','{\"msg\":\"新增行政区划\'刘老三村\'失败，行政区划编码已存在\",\"code\":500}',0,NULL,'2025-09-27 21:32:25',20),(141,'行政区划',1,'com.ruoyi.shebao.controller.AdministrativeDivisionController.add()','POST',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-27 21:32:37.885202\",\"divisionCode\":\"131001001002\",\"divisionLevel\":5,\"divisionName\":\"刘老三村\",\"fullCode\":\"130000/131000/131001/131001001/131001001002\",\"fullName\":\"河北省/廊坊市/安次区/银河南路街道/刘老三村\",\"id\":6,\"parentCode\":\"131001001\",\"sortOrder\":0,\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 21:32:38',51),(142,'行政区划',1,'com.ruoyi.shebao.controller.AdministrativeDivisionController.add()','POST',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-27 21:32:58.731153\",\"divisionCode\":\"131001002\",\"divisionLevel\":1,\"divisionName\":\"刘乡\",\"fullCode\":\"131001002\",\"fullName\":\"刘乡\",\"id\":7,\"sortOrder\":0,\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 21:32:58',23),(143,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-27 21:32:59\",\"delFlag\":\"0\",\"divisionCode\":\"131001002\",\"divisionLevel\":4,\"divisionName\":\"刘乡\",\"fullCode\":\"130000/131000/131001/131001002\",\"fullName\":\"河北省/廊坊市/安次区/刘乡\",\"id\":7,\"parentCode\":\"131001\",\"sortOrder\":0,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 21:33:10.453932\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 21:33:10',73),(144,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-27 21:32:59\",\"delFlag\":\"0\",\"divisionCode\":\"131001003\",\"divisionLevel\":4,\"divisionName\":\"刘乡2\",\"fullCode\":\"130000/131000/131001/131001003\",\"fullName\":\"河北省/廊坊市/安次区/刘乡2\",\"id\":7,\"parentCode\":\"131001\",\"sortOrder\":0,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 21:49:57.767458\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 21:49:57',59),(145,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"\",\"createTime\":\"2025-09-27 11:45:21\",\"delFlag\":\"0\",\"divisionCode\":\"131001\",\"divisionLevel\":3,\"divisionName\":\"安次区2\",\"fullCode\":\"130000/131000/131001\",\"fullName\":\"河北省/廊坊市/安次区2\",\"id\":3,\"parentCode\":\"131000\",\"sortOrder\":1,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 21:50:14.481110\"}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'UPDATE shebao_administrative_division \n            SET full_code = \'130000/13100\' at line 8\n### The error may exist in file [/Users/roy/data/projects/langfang-shebao/langfang-shebao/langfang-shebao/target/classes/mapper/AdministrativeDivisionMapper.xml]\n### The error may involve com.ruoyi.shebao.mapper.AdministrativeDivisionMapper.batchUpdateFullPath-Inline\n### The error occurred while setting parameters\n### SQL: UPDATE shebao_administrative_division              SET full_code = ?,                  full_name = ?,                 update_time = ?,                 update_by = ?             WHERE id = ?          ;              UPDATE shebao_administrative_division              SET full_code = ?,                  full_name = ?,                 update_time = ?,                 update_by = ?             WHERE id = ?          ;              UPDATE shebao_administrative_division              SET full_code = ?,                  full_name = ?,                 update_time = ?,                 update_by = ?             WHERE id = ?          ;              UPDATE shebao_administrative_division              SET full_code = ?,                  full_name = ?,                 update_time = ?,                 update_by = ?             WHERE id = ?\n### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near \'UPDATE shebao_administrative_division \n            SET full_code = \'130000/13100\' at line 8\n; bad SQL grammar []','2025-09-27 21:50:14',207),(146,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"\",\"createTime\":\"2025-09-27 11:45:21\",\"delFlag\":\"0\",\"divisionCode\":\"131001\",\"divisionLevel\":3,\"divisionName\":\"安次区2\",\"fullCode\":\"130000/131000/131001\",\"fullName\":\"河北省/廊坊市/安次区2\",\"id\":3,\"parentCode\":\"131000\",\"sortOrder\":1,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 21:54:59.991653\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 21:55:00',62),(147,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"\",\"createTime\":\"2025-09-27 11:45:21\",\"delFlag\":\"0\",\"divisionCode\":\"131001\",\"divisionLevel\":3,\"divisionName\":\"安次区\",\"fullCode\":\"130000/131000/131001\",\"fullName\":\"河北省/廊坊市/安次区\",\"id\":3,\"parentCode\":\"131000\",\"sortOrder\":1,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 21:55:16.944497\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 21:55:17',119),(148,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-27 21:32:59\",\"delFlag\":\"0\",\"divisionCode\":\"131001002\",\"divisionLevel\":4,\"divisionName\":\"刘乡\",\"fullCode\":\"130000/131000/131001/131001002\",\"fullName\":\"河北省/廊坊市/安次区/刘乡\",\"id\":7,\"parentCode\":\"131001\",\"sortOrder\":0,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 21:55:25.584551\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 21:55:25',44),(149,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"\",\"createTime\":\"2025-09-27 11:45:21\",\"delFlag\":\"0\",\"divisionCode\":\"131001\",\"divisionLevel\":3,\"divisionName\":\"安次区2\",\"fullCode\":\"130000/131000/131001\",\"fullName\":\"河北省/廊坊市/安次区2\",\"id\":3,\"parentCode\":\"131000\",\"sortOrder\":1,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 21:56:55.368839\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 21:56:55',86),(150,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"\",\"createTime\":\"2025-09-27 11:45:21\",\"delFlag\":\"0\",\"divisionCode\":\"131001\",\"divisionLevel\":3,\"divisionName\":\"安次区3\",\"fullCode\":\"130000/131000/131001\",\"fullName\":\"河北省/廊坊市/安次区3\",\"id\":3,\"parentCode\":\"131000\",\"sortOrder\":1,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 22:11:32.609176\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 22:11:32',72),(151,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"\",\"createTime\":\"2025-09-27 11:45:21\",\"delFlag\":\"0\",\"divisionCode\":\"131001\",\"divisionLevel\":3,\"divisionName\":\"安次区\",\"fullCode\":\"130000/131000/131001\",\"fullName\":\"河北省/廊坊市/安次区\",\"id\":3,\"parentCode\":\"131000\",\"sortOrder\":1,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 22:11:39.466844\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 22:11:39',57),(152,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"\",\"createTime\":\"2025-09-27 11:45:21\",\"delFlag\":\"0\",\"divisionCode\":\"131001\",\"divisionLevel\":3,\"divisionName\":\"安次区2\",\"fullCode\":\"130000/131000/131001\",\"fullName\":\"河北省/廊坊市/安次区2\",\"id\":3,\"parentCode\":\"131000\",\"sortOrder\":1,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 22:11:47.163969\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 22:11:47',58),(153,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"\",\"createTime\":\"2025-09-27 11:45:21\",\"delFlag\":\"0\",\"divisionCode\":\"131001\",\"divisionLevel\":3,\"divisionName\":\"安次区\",\"fullCode\":\"130000/131000/131001\",\"fullName\":\"河北省/廊坊市/安次区\",\"id\":3,\"parentCode\":\"131000\",\"sortOrder\":1,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-09-27 22:11:59.720357\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 22:11:59',54),(154,'行政区划',1,'com.ruoyi.shebao.controller.AdministrativeDivisionController.add()','POST',1,'admin','社保局','/shebao/administrativeDivision','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-27 22:33:11.975964\",\"divisionCode\":\"131002\",\"divisionLevel\":3,\"divisionName\":\"三河县\",\"fullCode\":\"130000/131000/131002\",\"fullName\":\"河北省/廊坊市/三河县\",\"id\":8,\"parentCode\":\"131000\",\"sortOrder\":0,\"status\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-27 22:33:12',52),(155,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"people\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"补贴人员\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":0,\"path\":\"person\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 01:43:38',30),(156,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/subsidyPerson/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"人员信息\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2003,\"path\":\"baseinfo\",\"routeName\":\"人员信息\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 01:46:52',42),(157,'被补贴人基础信息',1,'com.ruoyi.shebao.controller.SubsidyPersonController.add()','POST',1,'admin','社保局','/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"createBy\":\"admin\",\"createTime\":\"2025-09-28 01:49:29.396708\",\"gender\":\"0\",\"hasEmployeePension\":\"0\",\"homeAddress\":\"河北廊坊\",\"id\":1971995624308015105,\"idCardNo\":\"131131198411120000\",\"name\":\"高老汉\",\"nativePlace\":\"河北廊坊\",\"phone\":\"13112345678\",\"status\":\"0\",\"villageCode\":\"131001001001\",\"villageName\":\"大老刘村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 01:49:29',44),(158,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/landLossResident/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"失地居民\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2003,\"path\":\"lost-land\",\"routeName\":\"失地居民\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 02:16:59',100),(159,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'admin','社保局','/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"compensationCompleteTime\":\"2025-09-22\",\"gender\":\"0\",\"hasEmployeePension\":\"1\",\"homeAddress\":\"河北廊坊\",\"idCardNo\":\"131131198411120000\",\"isUnder16\":\"0\",\"landRequisitionTime\":\"2025-09-01\",\"name\":\"高老汉\",\"nativePlace\":\"河北廊坊\",\"personExists\":true,\"phone\":\"13112345678\",\"recognitionTime\":\"2025-09-23\",\"status\":\"0\",\"subsidyPersonId\":1971995624308015000,\"villageCode\":\"131001001001\",\"villageName\":\"大老刘村\"}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'is_under16\' in \'field list\'\n### The error may exist in com/ruoyi/shebao/mapper/LandLossResidentMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.LandLossResidentMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_land_loss_resident  ( id, subsidy_person_id, land_requisition_time, compensation_complete_time, recognition_time, is_under16, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'is_under16\' in \'field list\'\n; bad SQL grammar []','2025-09-28 02:17:39',158),(160,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'admin','社保局','/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"compensationCompleteTime\":\"2025-09-22\",\"gender\":\"0\",\"hasEmployeePension\":\"1\",\"homeAddress\":\"河北廊坊\",\"idCardNo\":\"131131198411120000\",\"isUnder16\":\"0\",\"landRequisitionTime\":\"2025-09-01\",\"name\":\"高老汉\",\"nativePlace\":\"河北廊坊\",\"personExists\":true,\"phone\":\"13112345678\",\"recognitionTime\":\"2025-09-23\",\"status\":\"0\",\"subsidyPersonId\":1971995624308015000,\"villageCode\":\"131001001001\",\"villageName\":\"大老刘村\"}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'is_under16\' in \'field list\'\n### The error may exist in com/ruoyi/shebao/mapper/LandLossResidentMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.LandLossResidentMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_land_loss_resident  ( id, subsidy_person_id, land_requisition_time, compensation_complete_time, recognition_time, is_under16, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'is_under16\' in \'field list\'\n; bad SQL grammar []','2025-09-28 02:17:45',66),(161,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'admin','社保局','/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"compensationCompleteTime\":\"2025-09-22\",\"gender\":\"0\",\"hasEmployeePension\":\"1\",\"homeAddress\":\"河北廊坊\",\"idCardNo\":\"131131198411120000\",\"isUnder16\":\"0\",\"landRequisitionTime\":\"2025-09-01\",\"name\":\"高老汉\",\"nativePlace\":\"河北廊坊\",\"personExists\":true,\"phone\":\"13112345678\",\"recognitionTime\":\"2025-09-23\",\"status\":\"0\",\"subsidyPersonId\":1971995624308015000,\"villageCode\":\"131001001001\",\"villageName\":\"大老刘村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 02:18:49',136),(162,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/expropriateeSubsidy/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"被征地居民\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2003,\"path\":\"expropriatee\",\"routeName\":\"被征地居民\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 12:20:09',42),(163,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/demolitionResident/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"拆迁居民\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2003,\"path\":\"demolition\",\"routeName\":\"拆迁居民\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 12:21:34',54),(164,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/villageOfficial/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"村干部\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2003,\"path\":\"village-official\",\"routeName\":\"村干部\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 12:22:17',22),(165,'被征地参保补贴',1,'com.ruoyi.shebao.controller.ExpropriateeSubsidyController.add()','POST',1,'admin','社保局','/shebao/expropriateeSubsidy','127.0.0.1','内网IP','{\"ageAtBaseDate\":40,\"baseDate\":\"2025-09-09\",\"birthday\":\"1984-11-12\",\"difficultySubsidyYears\":0,\"employeePensionMonths\":120,\"flexibleEmploymentMonths\":10,\"gender\":\"0\",\"hasEmployeePension\":\"0\",\"homeAddress\":\"河北廊坊\",\"idCardNo\":\"131131198411120000\",\"joinEmployeePension\":\"0\",\"joinUrbanRuralInsurance\":\"0\",\"landRequisitionBatch\":\"20202\",\"name\":\"高老汉\",\"nativePlace\":\"河北廊坊\",\"personExists\":true,\"phone\":\"13112345678\",\"status\":\"0\",\"subsidyAmount\":0,\"subsidyPersonId\":1971995624308015000,\"subsidyYears\":0,\"villageCode\":\"131001001001\",\"villageName\":\"大老刘村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 12:23:17',82),(166,'拆迁居民信息',1,'com.ruoyi.shebao.controller.DemolitionResidentController.add()','POST',1,'admin','社保局','/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"demolitionReason\":\"修路\",\"demolitionTime\":\"2025-09-29\",\"gender\":\"0\",\"hasEmployeePension\":\"0\",\"homeAddress\":\"河北廊坊\",\"idCardNo\":\"131131198411120000\",\"name\":\"高老汉\",\"nativePlace\":\"河北廊坊\",\"personExists\":true,\"phone\":\"13112345678\",\"recognitionTime\":\"2025-09-16\",\"status\":\"0\",\"subsidyPersonId\":1971995624308015000,\"villageCode\":\"131001001001\",\"villageName\":\"大老刘村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 22:10:57',152),(167,'村干部信息',1,'com.ruoyi.shebao.controller.VillageOfficialController.add()','POST',1,'admin','社保局','/shebao/villageOfficial','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"gender\":\"0\",\"hasEmployeePension\":\"0\",\"hasViolation\":\"0\",\"homeAddress\":\"河北廊坊\",\"idCardNo\":\"131131198411120000\",\"name\":\"高老汉\",\"nativePlace\":\"河北廊坊\",\"personExists\":true,\"phone\":\"13112345678\",\"positionList\":[{\"endDate\":\"2025-09-30\",\"position\":\"1\",\"startDate\":\"2025-08-01\",\"status\":\"0\"},{\"endDate\":\"2026-09-30\",\"position\":\"2\",\"startDate\":\"2025-09-30\",\"status\":\"0\"}],\"status\":\"0\",\"subsidyPersonId\":1971995624308015000,\"totalServiceYears\":10,\"villageCode\":\"131001001001\",\"villageName\":\"大老刘村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 22:18:31',67),(168,'被补贴人基础信息',1,'com.ruoyi.shebao.controller.SubsidyPersonController.add()','POST',1,'admin','社保局','/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"createBy\":\"admin\",\"createTime\":\"2025-09-28 22:43:24.020244\",\"gender\":\"0\",\"hasEmployeePension\":\"0\",\"homeAddress\":\"刘老三村一大队\",\"id\":1000001,\"idCardNo\":\"131131198411120001\",\"name\":\"刘三娘\",\"nativePlace\":\"廊坊\",\"phone\":\"13012345678\",\"status\":\"0\",\"villageCode\":\"131001001002\",\"villageName\":\"刘老三村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 22:43:24',106),(169,'参数管理',1,'com.ruoyi.web.controller.system.SysConfigController.add()','POST',1,'admin','社保局','/system/config','127.0.0.1','内网IP','{\"configKey\":\"shebao.average.annual.salary\",\"configName\":\"社保-年平均工资\",\"configType\":\"N\",\"configValue\":\"56724\",\"createBy\":\"admin\",\"params\":{}}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 22:47:18',35),(170,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/village/index\",\"createTime\":\"2025-09-27 10:43:11\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2001,\"menuName\":\"村级单位\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2000,\"path\":\"village\",\"perms\":\"shebao:village:list\",\"routeName\":\"村级单位管理\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 22:57:05',36),(171,'被征地参保补贴',1,'com.ruoyi.shebao.controller.ExpropriateeSubsidyController.add()','POST',1,'admin','社保局','/shebao/expropriateeSubsidy','127.0.0.1','内网IP','{\"ageAtBaseDate\":70,\"baseDate\":\"2025-09-01\",\"birthday\":\"1954-11-12\",\"difficultySubsidyYears\":0,\"employeePensionMonths\":0,\"flexibleEmploymentMonths\":0,\"gender\":\"1\",\"hasEmployeePension\":\"0\",\"homeAddress\":\"刘老三村一大队\",\"idCardNo\":\"131131195411120001\",\"joinEmployeePension\":\"0\",\"joinUrbanRuralInsurance\":\"1\",\"name\":\"刘大娘\",\"nativePlace\":\"廊坊\",\"personExists\":false,\"phone\":\"13012345678\",\"status\":\"0\",\"subsidyAmount\":7147,\"subsidyYears\":15,\"villageCode\":\"131001001002\",\"villageName\":\"刘老三村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 22:59:15',80),(172,'被征地参保补贴',1,'com.ruoyi.shebao.controller.ExpropriateeSubsidyController.add()','POST',1,'admin','社保局','/shebao/expropriateeSubsidy','127.0.0.1','内网IP','{\"ageAtBaseDate\":69,\"baseDate\":\"2025-09-01\",\"birthday\":\"1955-11-12\",\"difficultySubsidyYears\":0,\"employeePensionMonths\":0,\"flexibleEmploymentMonths\":0,\"gender\":\"0\",\"hasEmployeePension\":\"1\",\"homeAddress\":\"刘老三村一大队\",\"idCardNo\":\"131131195411120000\",\"joinEmployeePension\":\"0\",\"joinUrbanRuralInsurance\":\"0\",\"landRequisitionBatch\":\"第一批\",\"name\":\"王老五\",\"nativePlace\":\"廊坊\",\"personExists\":false,\"phone\":\"13012345678\",\"status\":\"0\",\"subsidyAmount\":7147.22,\"subsidyYears\":15,\"villageCode\":\"131001001002\",\"villageName\":\"刘老三村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 23:43:51',141),(173,'村干部信息',2,'com.ruoyi.shebao.controller.VillageOfficialController.edit()','PUT',1,'admin','社保局','/shebao/villageOfficial','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"gender\":\"0\",\"hasEmployeePension\":\"0\",\"hasViolation\":\"0\",\"homeAddress\":\"河北廊坊\",\"id\":1,\"idCardNo\":\"131131198411120000\",\"name\":\"高老汉\",\"nativePlace\":\"河北廊坊\",\"personExists\":true,\"phone\":\"13112345678\",\"positionList\":[{\"endDate\":\"2026-09-30\",\"id\":2,\"position\":\"2\",\"remark\":\"1\",\"startDate\":\"2025-09-30\",\"status\":\"0\",\"villageOfficialId\":1},{\"endDate\":\"2025-09-30\",\"id\":1,\"position\":\"1\",\"remark\":\"2\",\"startDate\":\"2025-08-01\",\"status\":\"0\",\"villageOfficialId\":1}],\"status\":\"0\",\"subsidyPersonId\":1000000,\"totalServiceYears\":10,\"villageCode\":\"131001001001\",\"villageName\":\"大老刘村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-28 23:50:52',68),(174,'通知公告',2,'com.ruoyi.web.controller.system.SysNoticeController.edit()','PUT',1,'admin','社保局','/system/notice','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-24 22:11:39\",\"noticeContent\":\"新版本内容\",\"noticeId\":1,\"noticeTitle\":\"温馨提醒：2018-07-01 新版本发布啦\",\"noticeType\":\"2\",\"params\":{},\"remark\":\"管理员\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 00:12:20',16),(175,'通知公告',2,'com.ruoyi.web.controller.system.SysNoticeController.edit()','PUT',1,'admin','社保局','/system/notice','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-09-24 22:11:39\",\"noticeContent\":\"<p>维护内容</p>\",\"noticeId\":2,\"noticeTitle\":\"维护通知：2018-07-01 系统凌晨维护\",\"noticeType\":\"1\",\"params\":{},\"remark\":\"管理员\",\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 00:12:26',12),(176,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2025-09-24 22:11:36\",\"icon\":\"tool\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3,\"menuName\":\"系统工具\",\"menuType\":\"M\",\"orderNum\":202,\"params\":{},\"parentId\":0,\"path\":\"tool\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 00:13:11',30),(177,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"createTime\":\"2025-09-24 22:11:36\",\"icon\":\"monitor\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2,\"menuName\":\"系统监控\",\"menuType\":\"M\",\"orderNum\":201,\"params\":{},\"parentId\":0,\"path\":\"monitor\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 00:14:30',23),(178,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"system/notice/index\",\"createTime\":\"2025-09-24 22:11:36\",\"icon\":\"message\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":107,\"menuName\":\"通知公告\",\"menuType\":\"C\",\"orderNum\":8,\"params\":{},\"parentId\":1,\"path\":\"notice\",\"perms\":\"system:notice:list\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 00:15:08',20),(179,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"system/post/index\",\"createTime\":\"2025-09-24 22:11:36\",\"icon\":\"post\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":104,\"menuName\":\"岗位管理\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":1,\"path\":\"post\",\"perms\":\"system:post:list\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 00:15:17',22),(180,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"component\":\"shebao/subsidyPerson/index\",\"createTime\":\"2025-09-28 01:46:52\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2004,\"menuName\":\"人员信息\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":0,\"path\":\"baseinfo\",\"perms\":\"\",\"routeName\":\"人员信息\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:14:38',34),(181,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"createTime\":\"2025-09-28 01:43:38\",\"icon\":\"people\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2003,\"menuName\":\"补贴身份\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":0,\"path\":\"person\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:14:47',16),(182,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"createBy\":\"admin\",\"icon\":\"form\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"补贴发放\",\"menuType\":\"M\",\"orderNum\":4,\"params\":{},\"parentId\":0,\"path\":\"send\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:15:25',27),(183,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"component\":\"shebao/subsidyPerson/index\",\"createTime\":\"2025-09-28 01:46:52\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2004,\"menuName\":\"人员信息\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":0,\"path\":\"baseinfo\",\"perms\":\"\",\"routeName\":\"人员信息\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:15:32',16),(184,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"createTime\":\"2025-09-28 01:43:38\",\"icon\":\"people\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2003,\"menuName\":\"补贴身份\",\"menuType\":\"M\",\"orderNum\":3,\"params\":{},\"parentId\":0,\"path\":\"person\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:15:37',25),(185,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"component\":\"shebao/subsidyPerson/index\",\"createTime\":\"2025-09-28 01:46:52\",\"icon\":\"people\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2004,\"menuName\":\"人员信息\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":0,\"path\":\"baseinfo\",\"perms\":\"\",\"routeName\":\"人员信息\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:15:52',19),(186,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"createTime\":\"2025-09-28 01:43:38\",\"icon\":\"peoples\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2003,\"menuName\":\"补贴身份\",\"menuType\":\"M\",\"orderNum\":3,\"params\":{},\"parentId\":0,\"path\":\"person\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:16:04',25),(187,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"createTime\":\"2025-09-28 01:43:38\",\"icon\":\"peoples\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2003,\"menuName\":\"补贴居民\",\"menuType\":\"M\",\"orderNum\":3,\"params\":{},\"parentId\":0,\"path\":\"person\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:17:03',21),(188,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"component\":\"shebao/subsidyPerson/index\",\"createTime\":\"2025-09-28 01:46:52\",\"icon\":\"people\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2004,\"menuName\":\"人员信息\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":1,\"path\":\"baseinfo\",\"perms\":\"\",\"routeName\":\"人员信息\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:17:20',25),(189,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"发放记录\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2009,\"path\":\"record\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:17:42',25),(190,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"发放统计\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":2009,\"path\":\"stat\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:18:05',22),(191,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"createTime\":\"2025-09-29 11:15:25\",\"icon\":\"form\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2009,\"menuName\":\"补贴发放\",\"menuType\":\"M\",\"orderNum\":4,\"params\":{},\"parentId\":0,\"path\":\"assign\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:19:06',21),(192,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"createTime\":\"2025-09-29 11:17:42\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2010,\"menuName\":\"发放记录\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2009,\"path\":\"history\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:20:02',21),(193,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"createTime\":\"2025-09-29 11:17:42\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2010,\"menuName\":\"发放记录\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2009,\"path\":\"list\",\"perms\":\"\",\"routeName\":\"发放记录\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:20:53',20),(194,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"component\":\"index\",\"createTime\":\"2025-09-29 11:17:42\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2010,\"menuName\":\"发放记录\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2009,\"path\":\"list\",\"perms\":\"\",\"routeName\":\"发放记录\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:23:11',29),(195,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"component\":\"index\",\"createTime\":\"2025-09-29 11:18:05\",\"icon\":\"#\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2011,\"menuName\":\"发放统计\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2009,\"path\":\"stat\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:23:27',20),(196,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"component\":\"shebao/subsidyPerson/index\",\"createTime\":\"2025-09-28 01:46:52\",\"icon\":\"people\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2004,\"menuName\":\"人员信息\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":1,\"path\":\"baseinfo\",\"perms\":\"\",\"routeName\":\"人员信息\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:24:06',23),(197,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"component\":\"shebao/subsidyPerson/index\",\"createTime\":\"2025-09-28 01:46:52\",\"icon\":\"people\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2004,\"menuName\":\"人员信息\",\"menuType\":\"C\",\"orderNum\":0,\"params\":{},\"parentId\":2000,\"path\":\"baseinfo\",\"perms\":\"\",\"routeName\":\"人员信息\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:30:50',26),(198,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','222.128.127.244','XX XX','{\"children\":[],\"component\":\"shebao/administrativeDivision/index\",\"createTime\":\"2025-09-27 20:46:54\",\"icon\":\"tree-table\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2002,\"menuName\":\"行政区划\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2000,\"path\":\"division\",\"perms\":\"shebao:administrativeDivision:list\",\"routeName\":\"行政区划\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 11:31:36',24),(199,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/system/menu/2001','127.0.0.1','内网IP','2001','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2025-09-29 20:56:00',20),(200,'角色管理',2,'com.ruoyi.web.controller.system.SysRoleController.edit()','PUT',1,'admin','社保局','/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2025-09-24 22:11:36\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2004,2002,2003,2005,2006,2007,2008,2009,2010,2011],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 20:56:39',90),(201,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/system/menu/2001','127.0.0.1','内网IP','2001','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 20:56:45',28),(202,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/config/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"补贴设置\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2000,\"path\":\"settings\",\"routeName\":\"补贴设置\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 20:59:55',32),(203,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/config/index\",\"createTime\":\"2025-09-29 20:59:55\",\"icon\":\"dict\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2012,\"menuName\":\"补贴设置\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":2000,\"path\":\"settings\",\"perms\":\"\",\"routeName\":\"补贴设置\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-09-29 21:01:21',37),(204,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','222.128.127.244','XX XX','{\"createBy\":\"admin\",\"createTime\":\"2025-09-27 21:32:38\",\"delFlag\":\"0\",\"divisionCode\":\"131001001002\",\"divisionLevel\":5,\"divisionName\":\"刘庄子村\",\"fullCode\":\"130000/131000/131001/131001001/131001001002\",\"fullName\":\"河北省/廊坊市/安次区/银河南路街道/刘庄子村\",\"id\":6,\"parentCode\":\"131001001\",\"sortOrder\":0,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-10-10 10:45:14.589127\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-10 10:45:14',63),(205,'行政区划',2,'com.ruoyi.shebao.controller.AdministrativeDivisionController.edit()','PUT',1,'admin','社保局','/shebao/administrativeDivision','222.128.127.244','XX XX','{\"contactPerson\":\"111\",\"contactPhone\":\"222\",\"createBy\":\"admin\",\"createTime\":\"2025-09-27 20:49:48\",\"delFlag\":\"0\",\"divisionCode\":\"131001001001\",\"divisionLevel\":5,\"divisionName\":\"王庄子村\",\"fullCode\":\"130000/131000/131001/131001001/131001001001\",\"fullName\":\"河北省/廊坊市/安次区/银河南路街道/王庄子村\",\"id\":5,\"parentCode\":\"131001001\",\"sortOrder\":0,\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-10-10 10:45:23.778241\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-10 10:45:23',68),(206,'拆迁居民信息',5,'com.ruoyi.shebao.controller.DemolitionResidentController.export()','POST',1,'admin','社保局','/shebao/demolitionResident/export','60.10.59.193','XX XX','{\"pageSize\":\"10\",\"pageNum\":\"1\"}',NULL,0,NULL,'2025-10-10 11:05:25',892),(207,'拆迁居民信息',5,'com.ruoyi.shebao.controller.DemolitionResidentController.export()','POST',1,'admin','社保局','/shebao/demolitionResident/export','60.10.59.193','XX XX','{\"pageSize\":\"10\",\"pageNum\":\"1\"}',NULL,0,NULL,'2025-10-10 11:05:31',49),(208,'失地居民信息',5,'com.ruoyi.shebao.controller.LandLossResidentController.export()','POST',1,'admin','社保局','/shebao/landLossResident/export','60.10.59.193','XX XX','{\"pageSize\":\"10\",\"pageNum\":\"1\"}',NULL,0,NULL,'2025-10-10 11:05:32',50),(209,'村干部信息',1,'com.ruoyi.shebao.controller.VillageOfficialController.add()','POST',1,'admin','社保局','/api/shebao/villageOfficial','60.10.59.193','XX XX','{\"birthday\":\"1956-11-25\",\"gender\":\"1\",\"hasEmployeePension\":\"0\",\"hasViolation\":\"0\",\"homeAddress\":\"王庄子村\",\"idCardNo\":\"132801195611252658\",\"name\":\"刘玉连\",\"nativePlace\":\"河北省廊坊市\",\"personExists\":false,\"phone\":\"130000000000\",\"positionList\":[{\"endDate\":\"2025-10-15\",\"position\":\"1\",\"startDate\":\"2018-07-07\",\"status\":\"0\"}],\"status\":\"0\",\"totalServiceYears\":0,\"villageCode\":\"131001001001\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-11 10:58:39',115),(210,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'admin','社保局','/api/shebao/landLossResident','60.10.59.193','XX XX','{\"birthday\":\"1965-01-12\",\"compensationCompleteTime\":\"2025-09-29\",\"gender\":\"0\",\"hasEmployeePension\":\"1\",\"homeAddress\":\"啊\",\"householdNo\":\"啊\",\"idCardNo\":\"132801196501124221\",\"isUnder16\":\"0\",\"landRequisitionTime\":\"2025-10-01\",\"name\":\"高老汉\",\"nativePlace\":\"啊\",\"personExists\":false,\"phone\":\"13333333333\",\"recognitionTime\":\"2025-10-01\",\"status\":\"0\",\"villageCode\":\"131001001001\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-11 16:28:26',20),(211,'失地居民信息',1,'com.ruoyi.shebao.controller.LandLossResidentController.add()','POST',1,'admin','社保局','/api/shebao/landLossResident','60.10.59.193','XX XX','{\"birthday\":\"1984-11-12\",\"compensationCompleteTime\":\"2025-10-22\",\"gender\":\"0\",\"hasEmployeePension\":\"0\",\"homeAddress\":\"河北廊坊\",\"idCardNo\":\"131131198411120000\",\"isUnder16\":\"0\",\"name\":\"高老汉\",\"nativePlace\":\"河北廊坊\",\"personExists\":true,\"phone\":\"13112345678\",\"status\":\"0\",\"subsidyPersonId\":1000000,\"villageCode\":\"131001001001\",\"villageName\":\"王庄子村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-11 16:29:38',11),(212,'个人信息',2,'com.ruoyi.web.controller.system.SysProfileController.updatePwd()','PUT',1,'admin','社保局','/api/system/user/profile/updatePwd','222.128.127.244','XX XX','{}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-14 10:52:21',235),(213,'被征地参保补贴',1,'com.ruoyi.shebao.controller.ExpropriateeSubsidyController.add()','POST',1,'admin','社保局','/api/shebao/expropriateeSubsidy','60.10.59.193','XX XX','{\"ageAtBaseDate\":40,\"baseDate\":\"2025-10-01\",\"birthday\":\"1984-11-12\",\"difficultySubsidyYears\":0,\"employeePensionMonths\":0,\"flexibleEmploymentMonths\":0,\"gender\":\"0\",\"hasEmployeePension\":\"0\",\"homeAddress\":\"河北廊坊\",\"idCardNo\":\"131131198411120000\",\"joinEmployeePension\":\"0\",\"joinUrbanRuralInsurance\":\"0\",\"name\":\"高老汉\",\"nativePlace\":\"河北廊坊\",\"personExists\":true,\"phone\":\"13112345678\",\"status\":\"0\",\"subsidyAmount\":1429.44,\"subsidyPersonId\":1000000,\"subsidyYears\":3,\"villageCode\":\"131001001001\",\"villageName\":\"王庄子村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-16 15:35:19',16),(214,'村干部信息',1,'com.ruoyi.shebao.controller.VillageOfficialController.add()','POST',1,'admin','社保局','/api/shebao/villageOfficial','60.10.59.193','XX XX','{\"birthday\":\"1984-11-12\",\"gender\":\"0\",\"hasEmployeePension\":\"0\",\"hasViolation\":\"0\",\"homeAddress\":\"河北廊坊\",\"idCardNo\":\"131131198411120000\",\"name\":\"高老汉\",\"nativePlace\":\"河北廊坊\",\"personExists\":true,\"phone\":\"13112345678\",\"positionList\":[],\"status\":\"0\",\"subsidyPersonId\":1000000,\"totalServiceYears\":0,\"villageCode\":\"131001001001\",\"villageName\":\"王庄子村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-16 15:37:18',12),(215,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/subsidyDistribution/index\",\"createTime\":\"2025-10-15 21:31:43\",\"icon\":\"money\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2013,\"menuName\":\"补贴发放记录\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":2009,\"path\":\"subsidyDistribution\",\"perms\":\"shebao:subsidyDistribution:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-18 21:10:13',33),(216,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/api/system/menu/2010','127.0.0.1','内网IP','2010','{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}',0,NULL,'2025-10-18 21:26:22',38),(217,'角色管理',2,'com.ruoyi.web.controller.system.SysRoleController.edit()','PUT',1,'admin','社保局','/api/system/role','127.0.0.1','内网IP','{\"admin\":false,\"createTime\":\"2025-09-24 22:11:36\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2009,2004,2002,2003,2005,2006,2007,2008,2011],\"params\":{},\"remark\":\"普通角色\",\"roleId\":2,\"roleKey\":\"common\",\"roleName\":\"普通角色\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"admin\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-18 21:26:40',71),(218,'菜单管理',3,'com.ruoyi.web.controller.system.SysMenuController.remove()','DELETE',1,'admin','社保局','/api/system/menu/2010','127.0.0.1','内网IP','2010','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-18 21:26:48',32),(219,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/subsidyDistribution/index\",\"createTime\":\"2025-10-15 21:31:43\",\"icon\":\"money\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2013,\"menuName\":\"补贴发放记录\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2009,\"path\":\"subsidyDistribution\",\"perms\":\"shebao:subsidyDistribution:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-18 21:26:53',28),(220,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/streetOffice/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"街道办事处\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2000,\"path\":\"streetOffice\",\"perms\":\"shebao:streetOffice:list\",\"routeName\":\"街道办事处\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 10:38:24',45),(221,'菜单管理',1,'com.ruoyi.web.controller.system.SysMenuController.add()','POST',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/villageCommittee/index\",\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"村委会\",\"menuType\":\"C\",\"orderNum\":6,\"params\":{},\"parentId\":2000,\"path\":\"villageCommittee\",\"perms\":\"shebao:villageCommittee:list\",\"routeName\":\"村委会\",\"status\":\"0\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 10:39:40',34),(222,'街道办事处信息',1,'com.ruoyi.shebao.controller.StreetOfficeController.add()','POST',1,'admin','社保局','/api/shebao/streetOffice','127.0.0.1','内网IP','{\"address\":\"河北廊坊\",\"contactPerson\":\"黄\",\"contactPhone\":\"13612345678\",\"id\":4,\"remark\":\"测试\",\"status\":\"0\",\"streetCode\":\"004\",\"streetName\":\"麦子店街道\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 11:02:13',116),(223,'街道办事处信息',2,'com.ruoyi.shebao.controller.StreetOfficeController.edit()','PUT',1,'admin','社保局','/api/shebao/streetOffice','127.0.0.1','内网IP','{\"address\":\"河北廊坊\",\"contactPerson\":\"黄\",\"contactPhone\":\"13612345678\",\"createBy\":\"\",\"createTime\":\"2025-10-19 11:02:13\",\"delFlag\":\"0\",\"id\":4,\"remark\":\"测试\",\"status\":\"0\",\"streetCode\":\"004\",\"streetName\":\"麦子店街道\",\"updateBy\":\"\",\"updateTime\":\"2025-10-19 11:02:13\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 11:02:16',40),(224,'街道办事处信息',5,'com.ruoyi.shebao.controller.StreetOfficeController.export()','POST',1,'admin','社保局','/api/shebao/streetOffice/export','127.0.0.1','内网IP','{\"pageSize\":\"10\",\"pageNum\":\"1\"}',NULL,0,NULL,'2025-10-19 11:03:57',906),(225,'村委会信息',2,'com.ruoyi.shebao.controller.VillageCommitteeController.edit()','PUT',1,'admin','社保局','/api/shebao/villageCommittee','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2025-10-18 23:56:54\",\"delFlag\":\"0\",\"id\":2,\"status\":\"0\",\"streetOfficeId\":1,\"updateBy\":\"\",\"updateTime\":\"2025-10-18 23:56:54\",\"villageCode\":\"002\",\"villageName\":\"第二村\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 11:24:25',44),(226,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1965-01-12\",\"createBy\":\"admin\",\"createTime\":\"2025-10-11 16:28:26\",\"delFlag\":\"0\",\"gender\":\"2\",\"householdRegistration\":\"啊\",\"id\":1000005,\"idCardNo\":\"132801196501124221\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"高老汉\",\"phone\":\"13333333333\",\"streetOfficeId\":3,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-19 13:54:00.950462\",\"userCode\":\"001-001-0002\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 13:54:01',44),(227,'被补贴人基础信息',1,'com.ruoyi.shebao.controller.SubsidyPersonController.add()','POST',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1965-01-12\",\"createBy\":\"admin\",\"createTime\":\"2025-10-19 13:55:31.368957\",\"gender\":\"2\",\"householdRegistration\":\"河北\",\"id\":1000006,\"idCardNo\":\"13280119650112422X\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"大毛\",\"phone\":\"13601243558\",\"remark\":\"123\",\"status\":\"0\",\"streetOfficeId\":1,\"userCode\":\"001-001--001\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 13:55:31',31),(228,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1965-01-12\",\"createBy\":\"admin\",\"createTime\":\"2025-10-19 13:55:31\",\"deathDate\":\"2025-10-20\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"家里\",\"householdRegistration\":\"河北\",\"id\":1000006,\"idCardNo\":\"13280119650112422X\",\"isAlive\":\"0\",\"isVillageCoopMember\":\"1\",\"name\":\"大毛\",\"phone\":\"13601243558\",\"remark\":\"123\",\"status\":\"0\",\"streetOfficeId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-19 15:13:53.416037\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 15:13:53',40),(229,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1965-01-12\",\"createBy\":\"admin\",\"createTime\":\"2025-10-19 13:55:31\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"家里\",\"householdRegistration\":\"河北\",\"id\":1000006,\"idCardNo\":\"13280119650112422X\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"大毛\",\"phone\":\"13601243558\",\"remark\":\"123\",\"status\":\"0\",\"streetOfficeId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-19 15:13:58.191544\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 15:13:58',23),(230,'失地居民信息',2,'com.ruoyi.shebao.controller.LandLossResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"compensationCompleteTime\":\"2025-09-22\",\"gender\":\"2\",\"homeAddress\":\"1232\",\"householdRegistration\":\"河北廊坊\",\"id\":1,\"idCardNo\":\"131131198411120000\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2025-09-01\",\"name\":\"高老汉\",\"personExists\":true,\"phone\":\"13112345678\",\"recognitionTime\":\"2025-09-23\",\"streetOfficeId\":1,\"subsidyPersonId\":1000000,\"userCode\":\"001-001-0001\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 15:54:46',78),(231,'失地居民信息',2,'com.ruoyi.shebao.controller.LandLossResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"compensationCompleteTime\":\"2025-09-22\",\"gender\":\"2\",\"homeAddress\":\"12313\",\"householdRegistration\":\"河北廊坊\",\"id\":1,\"idCardNo\":\"131131198411120000\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2025-09-01\",\"name\":\"高老汉\",\"personExists\":true,\"phone\":\"13112345678\",\"recognitionTime\":\"2025-09-23\",\"streetOfficeId\":1,\"subsidyPersonId\":1000000,\"userCode\":\"001-001-0001\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 15:56:39',58),(232,'失地居民信息',2,'com.ruoyi.shebao.controller.LandLossResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"compensationCompleteTime\":\"2025-09-22\",\"gender\":\"2\",\"homeAddress\":\"12321\",\"householdRegistration\":\"河北廊坊\",\"id\":1,\"idCardNo\":\"131131198411120000\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2025-09-01\",\"name\":\"高老汉\",\"personExists\":true,\"phone\":\"13112345678\",\"recognitionTime\":\"2025-09-23\",\"streetOfficeId\":1,\"subsidyPersonId\":1000000,\"userCode\":\"0010020001\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 16:05:41',73),(233,'失地居民信息',2,'com.ruoyi.shebao.controller.LandLossResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"compensationCompleteTime\":\"2025-09-22\",\"gender\":\"2\",\"homeAddress\":\"12321\",\"householdRegistration\":\"廊坊\",\"id\":1,\"idCardNo\":\"131131198411120001\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2025-09-01\",\"name\":\"刘三娘\",\"personExists\":true,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-09-23\",\"streetOfficeId\":1,\"subsidyPersonId\":1000001,\"userCode\":\"001-001-0001\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 16:07:20',59),(234,'失地居民信息',5,'com.ruoyi.shebao.controller.LandLossResidentController.export()','POST',1,'admin','社保局','/api/shebao/landLossResident/export','127.0.0.1','内网IP','{\"pageSize\":\"10\",\"pageNum\":\"1\"}',NULL,0,NULL,'2025-10-19 16:17:34',1000),(235,'失地居民信息',5,'com.ruoyi.shebao.controller.LandLossResidentController.export()','POST',1,'admin','社保局','/api/shebao/landLossResident/export','127.0.0.1','内网IP','{\"pageSize\":\"10\",\"pageNum\":\"1\"}',NULL,0,NULL,'2025-10-19 16:25:09',65),(236,'被补贴人基础信息',5,'com.ruoyi.shebao.controller.SubsidyPersonController.export()','POST',1,'admin','社保局','/api/shebao/subsidyPerson/export','127.0.0.1','内网IP','{\"pageSize\":\"10\",\"pageNum\":\"1\"}',NULL,0,NULL,'2025-10-19 16:25:27',93),(237,'拆迁居民信息',1,'com.ruoyi.shebao.controller.DemolitionResidentController.add()','POST',1,'admin','社保局','/api/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"gender\":\"2\",\"homeAddress\":\"啊啊啊\",\"householdRegistration\":\"廊坊\",\"idCardNo\":\"131131198411120001\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘三娘\",\"personExists\":true,\"phone\":\"13012345678\",\"streetOfficeId\":1,\"subsidyPersonId\":1000001,\"userCode\":\"001-001-0001\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 17:29:21',137),(238,'拆迁居民信息',2,'com.ruoyi.shebao.controller.DemolitionResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"demolitionReason\":\"1111\",\"demolitionTime\":\"2025-10-01\",\"gender\":\"2\",\"homeAddress\":\"3333\",\"householdRegistration\":\"廊坊\",\"id\":2,\"idCardNo\":\"131131198411120004\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘二娘\",\"personExists\":false,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-10-31\",\"remark\":\"2222\",\"streetOfficeId\":1,\"userCode\":\"0010020002\",\"villageCommitteeId\":1}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010020002\' for key \'shebao_subsidy_person.uk_user_code\'\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_subsidy_person  ( name, gender, id_card_no, birthday, household_registration,  phone, is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?,  ?, ?,  ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010020002\' for key \'shebao_subsidy_person.uk_user_code\'\n; Duplicate entry \'0010020002\' for key \'shebao_subsidy_person.uk_user_code\'','2025-10-19 17:30:12',162),(239,'拆迁居民信息',2,'com.ruoyi.shebao.controller.DemolitionResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"demolitionReason\":\"1111\",\"demolitionTime\":\"2025-10-01\",\"gender\":\"2\",\"homeAddress\":\"3333\",\"householdRegistration\":\"廊坊\",\"id\":2,\"idCardNo\":\"131131198411120004\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘二娘\",\"personExists\":false,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-10-31\",\"remark\":\"2222\",\"streetOfficeId\":1,\"userCode\":\"0010020002\",\"villageCommitteeId\":1}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010020002\' for key \'shebao_subsidy_person.uk_user_code\'\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_subsidy_person  ( name, gender, id_card_no, birthday, household_registration,  phone, is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?,  ?, ?,  ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010020002\' for key \'shebao_subsidy_person.uk_user_code\'\n; Duplicate entry \'0010020002\' for key \'shebao_subsidy_person.uk_user_code\'','2025-10-19 17:30:42',38),(240,'拆迁居民信息',2,'com.ruoyi.shebao.controller.DemolitionResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"demolitionReason\":\"1111\",\"demolitionTime\":\"2025-10-01\",\"gender\":\"2\",\"homeAddress\":\"3333\",\"householdRegistration\":\"廊坊\",\"id\":2,\"idCardNo\":\"131131198411120004\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘二娘\",\"personExists\":false,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-10-31\",\"remark\":\"2222\",\"streetOfficeId\":1,\"userCode\":\"0010020002\",\"villageCommitteeId\":1}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010020002\' for key \'shebao_subsidy_person.uk_user_code\'\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_subsidy_person  ( name, gender, id_card_no, birthday, household_registration,  phone, is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?,  ?, ?,  ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010020002\' for key \'shebao_subsidy_person.uk_user_code\'\n; Duplicate entry \'0010020002\' for key \'shebao_subsidy_person.uk_user_code\'','2025-10-19 17:32:04',13909),(241,'拆迁居民信息',2,'com.ruoyi.shebao.controller.DemolitionResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"demolitionReason\":\"111\",\"demolitionTime\":\"2025-10-31\",\"gender\":\"2\",\"homeAddress\":\"12321\",\"householdRegistration\":\"廊坊\",\"id\":2,\"idCardNo\":\"131131198411120004\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘三娘\",\"personExists\":false,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-10-01\",\"remark\":\"222\",\"streetOfficeId\":1,\"userCode\":\"0010020002\",\"villageCommitteeId\":1}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010020002\' for key \'shebao_subsidy_person.uk_user_code\'\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_subsidy_person  ( name, gender, id_card_no, birthday, household_registration, home_address, phone, is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010020002\' for key \'shebao_subsidy_person.uk_user_code\'\n; Duplicate entry \'0010020002\' for key \'shebao_subsidy_person.uk_user_code\'','2025-10-19 17:40:25',163),(242,'拆迁居民信息',2,'com.ruoyi.shebao.controller.DemolitionResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"demolitionReason\":\"222\",\"demolitionTime\":\"2025-10-03\",\"gender\":\"2\",\"homeAddress\":\"111\",\"householdRegistration\":\"廊坊\",\"id\":2,\"idCardNo\":\"131131198411120004\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘二娘\",\"personExists\":false,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-10-04\",\"remark\":\"555\",\"streetOfficeId\":1,\"userCode\":\"0010020002\",\"villageCommitteeId\":1}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010010003\' for key \'shebao_subsidy_person.uk_user_code\'\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_subsidy_person  ( name, gender, id_card_no, birthday, household_registration, home_address, phone, is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010010003\' for key \'shebao_subsidy_person.uk_user_code\'\n; Duplicate entry \'0010010003\' for key \'shebao_subsidy_person.uk_user_code\'','2025-10-19 17:45:16',213),(243,'拆迁居民信息',2,'com.ruoyi.shebao.controller.DemolitionResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"demolitionReason\":\"222\",\"demolitionTime\":\"2025-10-03\",\"gender\":\"2\",\"homeAddress\":\"111\",\"householdRegistration\":\"廊坊\",\"id\":2,\"idCardNo\":\"131131198411120004\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘二娘\",\"personExists\":false,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-10-04\",\"remark\":\"555\",\"streetOfficeId\":1,\"userCode\":\"0010020002\",\"villageCommitteeId\":1}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010010003\' for key \'shebao_subsidy_person.uk_user_code\'\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_subsidy_person  ( name, gender, id_card_no, birthday, household_registration, home_address, phone, is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010010003\' for key \'shebao_subsidy_person.uk_user_code\'\n; Duplicate entry \'0010010003\' for key \'shebao_subsidy_person.uk_user_code\'','2025-10-19 17:45:25',66),(244,'拆迁居民信息',2,'com.ruoyi.shebao.controller.DemolitionResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"demolitionReason\":\"222\",\"demolitionTime\":\"2025-10-03\",\"gender\":\"2\",\"homeAddress\":\"111\",\"householdRegistration\":\"廊坊\",\"id\":2,\"idCardNo\":\"131131198411120004\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘二娘\",\"personExists\":false,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-10-04\",\"remark\":\"555\",\"streetOfficeId\":1,\"userCode\":\"0010020002\",\"villageCommitteeId\":1}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010010003\' for key \'shebao_subsidy_person.uk_user_code\'\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_subsidy_person  ( name, gender, id_card_no, birthday, household_registration, home_address, phone, is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010010003\' for key \'shebao_subsidy_person.uk_user_code\'\n; Duplicate entry \'0010010003\' for key \'shebao_subsidy_person.uk_user_code\'','2025-10-19 17:45:42',63),(245,'拆迁居民信息',2,'com.ruoyi.shebao.controller.DemolitionResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"demolitionReason\":\"222\",\"demolitionTime\":\"2025-10-03\",\"gender\":\"2\",\"homeAddress\":\"111\",\"householdRegistration\":\"廊坊\",\"id\":2,\"idCardNo\":\"131131198411120004\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘二娘\",\"personExists\":false,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-10-04\",\"remark\":\"555\",\"streetOfficeId\":1,\"userCode\":\"0010020002\",\"villageCommitteeId\":1}',NULL,1,'\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010010003\' for key \'shebao_subsidy_person.uk_user_code\'\n### The error may exist in com/ruoyi/shebao/mapper/SubsidyPersonMapper.java (best guess)\n### The error may involve com.ruoyi.shebao.mapper.SubsidyPersonMapper.insert-Inline\n### The error occurred while setting parameters\n### SQL: INSERT INTO shebao_subsidy_person  ( name, gender, id_card_no, birthday, household_registration, home_address, phone, is_alive,  is_village_coop_member, street_office_id, village_committee_id, user_code, status, create_time,  create_by )  VALUES (  ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,  ?  )\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'0010010003\' for key \'shebao_subsidy_person.uk_user_code\'\n; Duplicate entry \'0010010003\' for key \'shebao_subsidy_person.uk_user_code\'','2025-10-19 17:49:01',54765),(246,'拆迁居民信息',2,'com.ruoyi.shebao.controller.DemolitionResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"demolitionReason\":\"222\",\"demolitionTime\":\"2025-10-03\",\"gender\":\"2\",\"homeAddress\":\"111\",\"householdRegistration\":\"廊坊\",\"id\":2,\"idCardNo\":\"131131198411120004\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘二娘\",\"personExists\":false,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-10-04\",\"remark\":\"555\",\"streetOfficeId\":1,\"userCode\":\"0010020002\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 17:50:26',82),(247,'拆迁居民信息',2,'com.ruoyi.shebao.controller.DemolitionResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"demolitionReason\":\"222\",\"demolitionTime\":\"2025-10-03\",\"gender\":\"1\",\"homeAddress\":\"1111\",\"householdRegistration\":\"廊坊\",\"id\":2,\"idCardNo\":\"131131198411120012\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘大娘\",\"personExists\":false,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-10-04\",\"remark\":\"555\",\"streetOfficeId\":1,\"userCode\":\"0010010004\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 17:54:26',16972),(248,'拆迁居民信息',2,'com.ruoyi.shebao.controller.DemolitionResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/demolitionResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"demolitionReason\":\"222\",\"demolitionTime\":\"2025-10-03\",\"gender\":\"1\",\"homeAddress\":\"1111\",\"householdRegistration\":\"廊坊\",\"id\":2,\"idCardNo\":\"131131198411120012\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘大娘\",\"personExists\":false,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-10-04\",\"remark\":\"555\",\"streetOfficeId\":1,\"userCode\":\"0010010004\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 17:54:35',42),(249,'失地居民信息',2,'com.ruoyi.shebao.controller.LandLossResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"compensationCompleteTime\":\"2025-10-22\",\"gender\":\"2\",\"homeAddress\":\"12321\",\"householdRegistration\":\"河北廊坊\",\"id\":3,\"idCardNo\":\"131131198411120000\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2025-10-01\",\"name\":\"高老汉\",\"personExists\":true,\"phone\":\"13112345678\",\"recognitionTime\":\"2025-10-01\",\"streetOfficeId\":1,\"subsidyPersonId\":1000000,\"userCode\":\"0010020001\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 20:52:52',69),(250,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'admin','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":100,\"subsidyPersonId\":1000000,\"subsidyRecordId\":3,\"subsidyType\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 20:59:17',78),(251,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'admin','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":123.45,\"subsidyPersonId\":1000000,\"subsidyRecordId\":1,\"subsidyType\":\"3\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 21:16:55',126),(252,'补贴发放审核通过',2,'com.ruoyi.shebao.controller.SubsidyDistributionController.approve()','PUT',1,'admin','社保局','/api/shebao/subsidyDistribution/approve','127.0.0.1','内网IP','{\"id\":2,\"remark\":\"同意\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 21:19:58',65),(253,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'admin','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":100,\"subsidyPersonId\":1000003,\"subsidyRecordId\":3,\"subsidyType\":\"2\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 21:20:40',55),(254,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'admin','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":111,\"subsidyPersonId\":1000001,\"subsidyRecordId\":1,\"subsidyType\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 21:21:37',91),(255,'补贴发放审核通过',2,'com.ruoyi.shebao.controller.SubsidyDistributionController.approve()','PUT',1,'admin','社保局','/api/shebao/subsidyDistribution/approve','127.0.0.1','内网IP','{\"id\":4,\"remark\":\"同意\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 21:21:43',68),(256,'补贴发放',2,'com.ruoyi.shebao.controller.SubsidyDistributionController.distribute()','PUT',1,'admin','社保局','/api/shebao/subsidyDistribution/distribute','127.0.0.1','内网IP','{\"id\":4,\"remark\":\"同意\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 21:21:49',63),(257,'补贴发放记录',3,'com.ruoyi.shebao.controller.SubsidyDistributionController.remove()','DELETE',1,'admin','社保局','/api/shebao/subsidyDistribution/4','127.0.0.1','内网IP','[4]',NULL,1,'只有待审核状态的记录才能删除','2025-10-19 21:48:46',47),(258,'补贴发放记录',3,'com.ruoyi.shebao.controller.SubsidyDistributionController.remove()','DELETE',1,'admin','社保局','/api/shebao/subsidyDistribution/4','127.0.0.1','内网IP','[4]',NULL,1,'只有待审核状态的记录才能删除','2025-10-19 21:49:00',29),(259,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'admin','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":0,\"remark\":\"111\",\"subsidyPersonId\":1000001,\"subsidyRecordId\":1,\"subsidyType\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 22:04:58',87),(260,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'admin','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":100,\"remark\":\"111\",\"subsidyPersonId\":1000001,\"subsidyRecordId\":1,\"subsidyType\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 22:13:58',147),(261,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'admin','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":200,\"subsidyPersonId\":1000001,\"subsidyRecordId\":1,\"subsidyType\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 22:15:00',65),(262,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'admin','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":30,\"remark\":\"333\",\"subsidyPersonId\":1000001,\"subsidyRecordId\":1,\"subsidyType\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 22:15:20',65),(263,'失地居民信息',2,'com.ruoyi.shebao.controller.LandLossResidentController.edit()','PUT',1,'admin','社保局','/api/shebao/landLossResident','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"compensationCompleteTime\":\"2025-09-22\",\"gender\":\"2\",\"homeAddress\":\"12321\",\"householdRegistration\":\"廊坊\",\"id\":1,\"idCardNo\":\"131131198411120001\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"landRequisitionTime\":\"2025-09-01\",\"name\":\"刘三娘\",\"personExists\":true,\"phone\":\"13012345678\",\"recognitionTime\":\"2025-09-23\",\"remark\":\"每月8号发\",\"streetOfficeId\":1,\"subsidyPersonId\":1000001,\"userCode\":\"0010020002\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 22:20:46',56),(264,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'admin','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":0.01,\"remark\":\"12321\",\"subsidyPersonId\":1000001,\"subsidyRecordId\":1,\"subsidyType\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 22:33:03',63),(265,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/administrativeDivision/index\",\"createTime\":\"2025-09-27 20:46:54\",\"icon\":\"tree-table\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2002,\"menuName\":\"行政区划\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":2000,\"path\":\"division\",\"perms\":\"shebao:administrativeDivision:list\",\"routeName\":\"行政区划\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 22:36:45',27),(266,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/streetOffice/index\",\"createTime\":\"2025-10-19 10:38:24\",\"icon\":\"tree-table\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2031,\"menuName\":\"街道办事处\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2000,\"path\":\"streetOffice\",\"perms\":\"shebao:streetOffice:list\",\"routeName\":\"街道办事处\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 22:37:01',21),(267,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/villageCommittee/index\",\"createTime\":\"2025-10-19 10:39:40\",\"icon\":\"tree-table\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2032,\"menuName\":\"村委会\",\"menuType\":\"C\",\"orderNum\":6,\"params\":{},\"parentId\":2000,\"path\":\"villageCommittee\",\"perms\":\"shebao:villageCommittee:list\",\"routeName\":\"村委会\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 22:37:17',19),(268,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"index\",\"createTime\":\"2025-09-29 11:18:05\",\"icon\":\"example\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2011,\"menuName\":\"发放统计\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2009,\"path\":\"stat\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 22:37:50',19),(269,'补贴发放记录',1,'com.ruoyi.shebao.controller.SubsidyDistributionController.add()','POST',1,'admin','社保局','/api/shebao/subsidyDistribution','127.0.0.1','内网IP','{\"distributionAmount\":0.01,\"remark\":\"1231231231\",\"subsidyPersonId\":1000001,\"subsidyRecordId\":1,\"subsidyType\":\"1\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 22:49:37',92),(270,'补贴发放审核通过',2,'com.ruoyi.shebao.controller.SubsidyDistributionController.approve()','PUT',1,'admin','社保局','/api/shebao/subsidyDistribution/approve','127.0.0.1','内网IP','{\"id\":10,\"remark\":\"\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 22:52:00',62),(271,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/streetOffice/index\",\"createTime\":\"2025-10-19 10:38:24\",\"icon\":\"tree-table\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2031,\"menuName\":\"街道办\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":2000,\"path\":\"streetOffice\",\"perms\":\"shebao:streetOffice:list\",\"routeName\":\"街道办\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-19 23:17:16',30),(272,'失地居民信息',3,'com.ruoyi.shebao.controller.LandLossResidentController.remove()','DELETE',1,'admin','社保局','/api/shebao/landLossResident/1','127.0.0.1','内网IP','[1]',NULL,1,'该失地居民存在未删除的补贴发放记录，无法删除','2025-10-19 23:30:18',19),(273,'失地居民信息',3,'com.ruoyi.shebao.controller.LandLossResidentController.remove()','DELETE',1,'admin','社保局','/api/shebao/landLossResident/1','127.0.0.1','内网IP','[1]',NULL,1,'该失地居民存在未删除的补贴发放记录，无法删除','2025-10-19 23:30:23',11),(274,'菜单管理',2,'com.ruoyi.web.controller.system.SysMenuController.edit()','PUT',1,'admin','社保局','/api/system/menu','127.0.0.1','内网IP','{\"children\":[],\"component\":\"shebao/residentQuery/index\",\"createTime\":\"2025-09-29 11:18:05\",\"icon\":\"search\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2011,\"menuName\":\"统计查询\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2009,\"path\":\"residentQuery\",\"perms\":\"\",\"routeName\":\"统计查询\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-20 00:27:29',47),(275,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1965-01-12\",\"createBy\":\"admin\",\"createTime\":\"2025-10-11 16:28:26\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"和平区\",\"householdRegistration\":\"啊\",\"id\":1000005,\"idCardNo\":\"13280119650112422X\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"高老汉\",\"phone\":\"13333333333\",\"status\":\"0\",\"streetOfficeId\":3,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-19 14:07:01\",\"userCode\":\"0010010002\",\"villageCommitteeId\":1}','{\"msg\":\"修改被补贴人\'高老汉\'失败，身份证号已存在\",\"code\":500}',0,NULL,'2025-10-20 00:41:25',22),(276,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1945-01-12\",\"createBy\":\"admin\",\"createTime\":\"2025-10-11 16:28:26\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"和平区\",\"householdRegistration\":\"啊\",\"id\":1000005,\"idCardNo\":\"13280119450112452X\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"高老汉\",\"phone\":\"13333333333\",\"status\":\"0\",\"streetOfficeId\":3,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-20 00:41:39.438751\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-20 00:41:39',38),(277,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1956-11-12\",\"createBy\":\"admin\",\"createTime\":\"2025-09-28 22:59:15\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"三里屯\",\"householdRegistration\":\"廊坊\",\"id\":1000002,\"idCardNo\":\"131131195611120005\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘大娘\",\"phone\":\"13012345678\",\"status\":\"0\",\"streetOfficeId\":2,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-20 00:42:21.465458\",\"villageCommitteeId\":3}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-20 00:42:21',25),(278,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"createBy\":\"admin\",\"createTime\":\"2025-09-28 22:43:24\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"12321\",\"householdRegistration\":\"廊坊\",\"id\":1000001,\"idCardNo\":\"131131198411120004\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘三娘\",\"phone\":\"13012345678\",\"status\":\"0\",\"streetOfficeId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-19 17:29:22\",\"userCode\":\"0010020002\",\"villageCommitteeId\":1}','{\"msg\":\"修改被补贴人\'刘三娘\'失败，身份证号已存在\",\"code\":500}',0,NULL,'2025-10-20 00:42:37',18),(279,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"createBy\":\"admin\",\"createTime\":\"2025-09-28 22:43:24\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"12321\",\"householdRegistration\":\"廊坊\",\"id\":1000001,\"idCardNo\":\"131131198411120021\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘三娘\",\"phone\":\"13012345678\",\"status\":\"0\",\"streetOfficeId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-20 00:42:48.290320\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-20 00:42:48',21),(280,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"createBy\":\"admin\",\"createTime\":\"2025-09-28 01:49:29\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"12321\",\"householdRegistration\":\"河北廊坊\",\"id\":1000000,\"idCardNo\":\"131131198411120000\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"高老汉\",\"phone\":\"13112345678\",\"status\":\"0\",\"streetOfficeId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-20 00:42:55.703785\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-20 00:42:55',26),(281,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1934-11-12\",\"createBy\":\"admin\",\"createTime\":\"2025-09-28 01:49:29\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"12321\",\"householdRegistration\":\"河北廊坊\",\"id\":1000000,\"idCardNo\":\"131131193411120001\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"高老汉\",\"phone\":\"13112345678\",\"status\":\"0\",\"streetOfficeId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-20 00:43:10.577466\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-20 00:43:10',27),(282,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1945-01-12\",\"createBy\":\"admin\",\"createTime\":\"2025-10-11 16:28:26\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"和平区\",\"householdRegistration\":\"啊\",\"id\":1000005,\"idCardNo\":\"13280119450112452X\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"高老汉\",\"phone\":\"13333333333\",\"status\":\"0\",\"streetOfficeId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-20 00:43:43.239740\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-20 00:43:43',26),(283,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1956-11-25\",\"createBy\":\"admin\",\"createTime\":\"2025-10-11 10:58:40\",\"delFlag\":\"0\",\"gender\":\"1\",\"homeAddress\":\"asdfadsf\",\"householdRegistration\":\"河北省廊坊市\",\"id\":1000004,\"idCardNo\":\"132801195611252658\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘玉连\",\"phone\":\"130000000000\",\"status\":\"0\",\"streetOfficeId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-20 00:44:07.148648\",\"villageCommitteeId\":1}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-20 00:44:07',23),(284,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"createBy\":\"admin\",\"createTime\":\"2025-09-28 22:43:24\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"12321\",\"householdRegistration\":\"廊坊\",\"id\":1000001,\"idCardNo\":\"131131198411120021\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘三娘\",\"phone\":\"13012345678\",\"status\":\"0\",\"streetOfficeId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-20 00:44:20.545987\",\"villageCommitteeId\":2}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-20 00:44:20',26),(285,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1934-11-12\",\"createBy\":\"admin\",\"createTime\":\"2025-09-28 01:49:29\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"12321\",\"householdRegistration\":\"河北廊坊\",\"id\":1000000,\"idCardNo\":\"131131193411120001\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"高老汉\",\"phone\":\"13112345678\",\"status\":\"0\",\"streetOfficeId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-20 00:44:29.252153\",\"villageCommitteeId\":2}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-20 00:44:29',23),(286,'被补贴人基础信息',2,'com.ruoyi.shebao.controller.SubsidyPersonController.edit()','PUT',1,'admin','社保局','/api/shebao/subsidyPerson','127.0.0.1','内网IP','{\"birthday\":\"1984-11-12\",\"createBy\":\"admin\",\"createTime\":\"2025-09-28 22:43:24\",\"delFlag\":\"0\",\"gender\":\"2\",\"homeAddress\":\"12321\",\"householdRegistration\":\"廊坊\",\"id\":1000001,\"idCardNo\":\"131131198411120020\",\"isAlive\":\"1\",\"isVillageCoopMember\":\"1\",\"name\":\"刘三娘\",\"phone\":\"13012345678\",\"status\":\"0\",\"streetOfficeId\":1,\"updateBy\":\"admin\",\"updateTime\":\"2025-10-20 00:45:34.834834\",\"villageCommitteeId\":2}','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2025-10-20 00:45:34',27);

--
-- Table structure for table `sys_post`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_post`
--

INSERT INTO `sys_post` VALUES (1,'ceo','董事长',1,'0','admin','2025-09-24 22:11:36','',NULL,''),(2,'se','项目经理',2,'0','admin','2025-09-24 22:11:36','',NULL,''),(3,'hr','人力资源',3,'0','admin','2025-09-24 22:11:36','',NULL,''),(4,'user','普通员工',4,'0','admin','2025-09-24 22:11:36','',NULL,'');

--
-- Table structure for table `sys_role`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1',1,1,'0','0','admin','2025-09-24 22:11:36','',NULL,'超级管理员'),(2,'普通角色','common',2,'2',1,1,'0','0','admin','2025-09-24 22:11:36','admin','2025-10-18 21:26:40','普通角色');

--
-- Table structure for table `sys_role_dept`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_dept`
--

INSERT INTO `sys_role_dept` VALUES (2,100),(2,101),(2,105);

--
-- Table structure for table `sys_role_menu`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

INSERT INTO `sys_role_menu` VALUES (2,2000),(2,2002),(2,2003),(2,2004),(2,2005),(2,2006),(2,2007),(2,2008),(2,2009),(2,2011);

--
-- Table structure for table `sys_user`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

INSERT INTO `sys_user` VALUES (1,100,'admin','超级管理员','00','ry@163.com','15888888888','1','','$2a$10$t/ws7qYp/wPm9b7jdNNaiuUTOmCmVTHo1mikBUM1zZ3NL3jfDha42','0','0','60.10.59.193','2025-10-22 15:00:28','2025-10-14 10:52:21','admin','2025-09-24 22:11:36','','2025-10-14 10:52:21','管理员'),(2,105,'ry','若依','00','ry@qq.com','15666666666','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','2','127.0.0.1','2025-09-24 22:11:36','2025-09-24 22:11:36','admin','2025-09-24 22:11:36','',NULL,'测试员');

--
-- Table structure for table `sys_user_post`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_post`
--

INSERT INTO `sys_user_post` VALUES (1,1);

--
-- Table structure for table `sys_user_role`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

INSERT INTO `sys_user_role` VALUES (1,1);
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-24 13:45:54
