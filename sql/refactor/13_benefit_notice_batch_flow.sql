-- 预到龄通知批次化、按批次核定、附件预览与支付计划联动
-- 幂等执行版：允许目标库已存在部分字段/索引/表

USE `lfpm`;

ALTER TABLE `benefit_determination`
  ADD COLUMN IF NOT EXISTS `notice_batch_no` varchar(50) NULL COMMENT '预到龄通知批次号' AFTER `approval_batch_no`,
  ADD COLUMN IF NOT EXISTS `notice_detail_id` bigint NULL COMMENT '预到龄通知明细ID' AFTER `notice_batch_no`,
  ADD COLUMN IF NOT EXISTS `id_card_no` varchar(18) NULL COMMENT '身份证号快照' AFTER `notice_detail_id`,
  ADD COLUMN IF NOT EXISTS `submit_by` varchar(64) NULL COMMENT '提交人' AFTER `id_card_no`,
  ADD COLUMN IF NOT EXISTS `submit_time` datetime NULL COMMENT '提交时间' AFTER `submit_by`,
  ADD COLUMN IF NOT EXISTS `review_by` varchar(64) NULL COMMENT '复核人' AFTER `submit_time`,
  ADD COLUMN IF NOT EXISTS `review_time` datetime NULL COMMENT '复核时间' AFTER `review_by`,
  ADD COLUMN IF NOT EXISTS `review_remark` varchar(500) NULL COMMENT '复核意见' AFTER `review_time`,
  ADD COLUMN IF NOT EXISTS `material_zip_path` varchar(255) NULL COMMENT '材料ZIP路径' AFTER `review_remark`,
  ADD COLUMN IF NOT EXISTS `material_extract_dir` varchar(255) NULL COMMENT '材料解压目录' AFTER `material_zip_path`,
  ADD COLUMN IF NOT EXISTS `material_image_paths` varchar(2000) NULL COMMENT '材料图片路径集合' AFTER `material_extract_dir`,
  ADD COLUMN IF NOT EXISTS `material_status` varchar(20) DEFAULT 'pending_upload' COMMENT '材料状态(pending_upload/uploaded/verified)' AFTER `material_image_paths`,
  ADD COLUMN IF NOT EXISTS `payment_plan_generated` char(1) DEFAULT '0' COMMENT '是否已进入支付计划(0否1是)' AFTER `material_status`;

SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'benefit_determination'
        AND index_name = 'idx_notice_batch_no'
    ),
    'SELECT ''idx_notice_batch_no exists''',
    'CREATE INDEX idx_notice_batch_no ON benefit_determination (notice_batch_no)'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'benefit_determination'
        AND index_name = 'idx_notice_detail_id'
    ),
    'SELECT ''idx_notice_detail_id exists''',
    'CREATE INDEX idx_notice_detail_id ON benefit_determination (notice_detail_id)'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = 'benefit_determination'
        AND index_name = 'idx_payment_plan_generated'
    ),
    'SELECT ''idx_payment_plan_generated exists''',
    'CREATE INDEX idx_payment_plan_generated ON benefit_determination (payment_plan_generated)'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS `shebao_benefit_notice_batch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_no` varchar(50) NOT NULL COMMENT '批次号',
  `notice_month` varchar(7) NOT NULL COMMENT '通知月份',
  `retirement_month` varchar(7) NOT NULL COMMENT '到龄月份',
  `age_threshold` int DEFAULT 60 COMMENT '年龄阈值',
  `total_count` int DEFAULT 0 COMMENT '总人数',
  `pending_review_count` int DEFAULT 0 COMMENT '待复核人数',
  `approved_count` int DEFAULT 0 COMMENT '已通过人数',
  `rejected_count` int DEFAULT 0 COMMENT '已驳回人数',
  `batch_status` varchar(20) DEFAULT 'generated' COMMENT '批次状态(generated/processing/completed)',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`),
  UNIQUE KEY `uk_notice_month` (`notice_month`),
  KEY `idx_batch_status` (`batch_status`)
) COMMENT='预到龄通知批次表';

CREATE TABLE IF NOT EXISTS `shebao_benefit_notice_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `notice_batch_id` bigint NOT NULL COMMENT '通知批次ID',
  `batch_no` varchar(50) NOT NULL COMMENT '批次号',
  `subsidy_person_id` bigint NOT NULL COMMENT '人员ID',
  `user_code` varchar(50) DEFAULT NULL COMMENT '用户编号',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `id_card_no` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `subsidy_type` varchar(50) DEFAULT NULL COMMENT '补贴类型',
  `birthday` date DEFAULT NULL COMMENT '出生日期',
  `retirement_date` date DEFAULT NULL COMMENT '到龄日期',
  `determination_id` bigint DEFAULT NULL COMMENT '待遇核定记录ID',
  `determination_status` varchar(20) DEFAULT 'draft' COMMENT '核定状态(draft/pending_review/approved/rejected/payment_generated)',
  `material_status` varchar(20) DEFAULT 'pending_upload' COMMENT '材料状态(pending_upload/uploaded/verified)',
  `review_status` varchar(20) DEFAULT 'draft' COMMENT '复核状态',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_batch_person` (`batch_no`, `subsidy_person_id`),
  KEY `idx_notice_batch_id` (`notice_batch_id`),
  KEY `idx_determination_status` (`determination_status`)
) COMMENT='预到龄通知明细表';

CREATE TABLE IF NOT EXISTS `shebao_benefit_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_type` varchar(50) NOT NULL COMMENT '业务类型',
  `business_id` bigint NOT NULL COMMENT '业务ID',
  `notice_batch_no` varchar(50) DEFAULT NULL COMMENT '通知批次号',
  `subsidy_person_id` bigint NOT NULL COMMENT '人员ID',
  `original_file_name` varchar(255) DEFAULT NULL COMMENT '原始文件名',
  `zip_file_path` varchar(255) DEFAULT NULL COMMENT 'ZIP路径',
  `extract_dir` varchar(255) DEFAULT NULL COMMENT '解压目录',
  `preview_image_paths` varchar(2000) DEFAULT NULL COMMENT '预览图片路径',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_business` (`business_type`, `business_id`),
  KEY `idx_notice_batch_no` (`notice_batch_no`),
  KEY `idx_subsidy_person_id` (`subsidy_person_id`)
) COMMENT='待遇核定附件表';
