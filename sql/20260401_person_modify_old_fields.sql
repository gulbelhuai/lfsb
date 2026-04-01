-- 人员信息变更：新增变更前字段快照（old_*）
-- 用于详情页“历史值 vs 新值”对比展示

ALTER TABLE `person_key_info_modify`
  ADD COLUMN `old_name` varchar(50) DEFAULT NULL COMMENT '变更前姓名' AFTER `name`,
  ADD COLUMN `old_id_card_no` varchar(18) DEFAULT NULL COMMENT '变更前身份证号' AFTER `id_card_no`,
  ADD COLUMN `old_household_registration` varchar(200) DEFAULT NULL COMMENT '变更前户籍所在地' AFTER `household_registration`,
  ADD COLUMN `old_street_office_id` bigint DEFAULT NULL COMMENT '变更前所属街道办ID' AFTER `street_office_id`,
  ADD COLUMN `old_village_committee_id` bigint DEFAULT NULL COMMENT '变更前所属村委会ID' AFTER `village_committee_id`,
  ADD COLUMN `old_home_address` varchar(500) DEFAULT NULL COMMENT '变更前家庭住址' AFTER `home_address`,
  ADD COLUMN `old_phone` varchar(32) DEFAULT NULL COMMENT '变更前联系电话' AFTER `phone`;

