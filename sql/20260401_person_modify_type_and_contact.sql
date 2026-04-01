-- 人员信息变更：区分基本信息(2级)/关键信息(3级)，并增加家庭住址、联系电话
-- 在已有库上执行；新库请直接使用更新后的 sql/lfpm.sql

ALTER TABLE `person_key_info_modify`
  ADD COLUMN `modify_type` varchar(20) NOT NULL DEFAULT 'key' COMMENT '变更类型 basic-基本信息(2级) key-关键信息(3级)' AFTER `subsidy_person_id`,
  ADD COLUMN `home_address` varchar(500) DEFAULT NULL COMMENT '家庭住址' AFTER `village_committee_id`,
  ADD COLUMN `phone` varchar(32) DEFAULT NULL COMMENT '联系电话' AFTER `home_address`;
