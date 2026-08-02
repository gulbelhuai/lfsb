-- 待遇核定字段补齐 + 附件表（历史脚本）
-- 2026-08-02 ISS-013：预到龄仅即时查询导出，不再创建/依赖
--   shebao_benefit_notice_batch / shebao_benefit_notice_detail。
-- 存量库清理见：sql/drop_benefit_notice_batch_tables_20260802.sql
-- 幂等执行版：允许目标库已存在部分字段/索引/表

USE `lfpm`;

ALTER TABLE `benefit_determination`
  ADD COLUMN IF NOT EXISTS `id_card_no` varchar(18) NULL COMMENT '身份证号快照' AFTER `approval_batch_no`,
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
        AND index_name = 'idx_notice_detail_id'
    ),
    'SELECT ''idx_notice_detail_id exists (legacy, ignore)''',
    'SELECT ''ok'''
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

CREATE TABLE IF NOT EXISTS `shebao_benefit_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_type` varchar(50) NOT NULL COMMENT '业务类型',
  `business_id` bigint NOT NULL COMMENT '业务ID',
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
  KEY `idx_subsidy_person_id` (`subsidy_person_id`)
) COMMENT='待遇核定附件表';
