-- ========================================
-- 阶段一 任务2: 修改现有表结构
-- 创建时间: 2026-01-19
-- 说明: 为现有表添加审批相关字段
-- ========================================

-- 1. 修改 shebao_subsidy_distribution 表，添加审批相关字段
ALTER TABLE shebao_subsidy_distribution
  ADD COLUMN batch_no VARCHAR(50) COMMENT '批次号' AFTER id,
  ADD COLUMN batch_type VARCHAR(20) DEFAULT 'first' COMMENT '批次类型(first/second/third)' AFTER batch_no,
  ADD COLUMN approval_status VARCHAR(20) DEFAULT 'draft' COMMENT '审批状态(draft/pending_review/pending_approve/pending_finance/distributed/rejected)' AFTER batch_type,
  ADD COLUMN rejection_reason VARCHAR(500) COMMENT '驳回原因' AFTER approval_status,
  ADD COLUMN review_time DATETIME COMMENT '复核时间' AFTER rejection_reason,
  ADD COLUMN review_user_id BIGINT COMMENT '复核人ID' AFTER review_time,
  ADD COLUMN review_user_name VARCHAR(64) COMMENT '复核人姓名' AFTER review_user_id,
  ADD COLUMN approve_time DATETIME COMMENT '审批时间' AFTER review_user_name,
  ADD COLUMN approve_user_id BIGINT COMMENT '审批人ID' AFTER approve_time,
  ADD COLUMN approve_user_name VARCHAR(64) COMMENT '审批人姓名' AFTER approve_user_id,
  ADD INDEX idx_batch_no (batch_no),
  ADD INDEX idx_approval_status (approval_status),
  ADD INDEX idx_batch_type (batch_type);

-- 2. 修改 shebao_subsidy_person 表，添加认证和暂停相关字段
ALTER TABLE shebao_subsidy_person
  ADD COLUMN certification_status VARCHAR(20) DEFAULT 'uncertified' COMMENT '认证状态(uncertified/april/october)' AFTER id_card_no,
  ADD COLUMN last_certification_date DATE COMMENT '最后认证日期' AFTER certification_status,
  ADD COLUMN suspension_status CHAR(1) DEFAULT '0' COMMENT '暂停状态(0正常1暂停)' AFTER last_certification_date,
  ADD COLUMN suspension_start_date DATE COMMENT '暂停开始日期' AFTER suspension_status,
  ADD COLUMN suspension_reason VARCHAR(50) COMMENT '暂停原因(death/uncertified/imprisonment/missing/other)' AFTER suspension_start_date,
  ADD COLUMN suspension_end_date DATE COMMENT '暂停结束日期' AFTER suspension_reason,
  ADD COLUMN suspension_remark VARCHAR(500) COMMENT '暂停备注' AFTER suspension_end_date,
  ADD INDEX idx_certification_status (certification_status),
  ADD INDEX idx_suspension_status (suspension_status);

-- ========================================
-- 表结构修改完成
-- ========================================
