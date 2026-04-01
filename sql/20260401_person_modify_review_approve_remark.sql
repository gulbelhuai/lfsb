-- 人员信息变更：补充复核意见、审批意见

ALTER TABLE `person_key_info_modify`
  ADD COLUMN `review_remark` varchar(500) DEFAULT NULL COMMENT '复核意见' AFTER `review_time`,
  ADD COLUMN `approve_remark` varchar(500) DEFAULT NULL COMMENT '审批意见' AFTER `approve_time`;

