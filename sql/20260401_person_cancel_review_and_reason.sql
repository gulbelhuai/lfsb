-- 人员注销登记：新增注销原因 + 二级审核字段
ALTER TABLE `shebao_person_cancel`
  ADD COLUMN  `cancel_reason` varchar(64) DEFAULT NULL COMMENT '注销原因（字典 cancel_reason）' AFTER `death_date`,
  ADD COLUMN  `approval_status` varchar(20) NOT NULL DEFAULT 'pending_review' COMMENT '审核状态(pending_review/approved/rejected)' AFTER `cancel_reason`,
  ADD COLUMN  `review_by` varchar(64) DEFAULT NULL COMMENT '复核人' AFTER `approval_status`,
  ADD COLUMN  `review_time` datetime DEFAULT NULL COMMENT '复核时间' AFTER `review_by`,
  ADD COLUMN  `review_remark` varchar(500) DEFAULT NULL COMMENT '复核意见' AFTER `review_time`,
  ADD COLUMN  `reject_reason` varchar(500) DEFAULT NULL COMMENT '驳回原因' AFTER `review_remark`;

UPDATE `shebao_person_cancel`
SET `approval_status` = 'pending_review'
WHERE `approval_status` IS NULL OR `approval_status` = '';
