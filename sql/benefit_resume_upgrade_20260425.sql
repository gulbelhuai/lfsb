-- 待遇恢复一期：主子表

CREATE TABLE IF NOT EXISTS `shebao_benefit_resume` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `determination_id` bigint NOT NULL COMMENT '待遇核定主表ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `id_card_no` varchar(18) default NULL COMMENT '身份证号快照',
  `resume_month` date default NULL COMMENT '恢复年月',
  `resume_reason` varchar(200) DEFAULT NULL COMMENT '恢复原因（汇总）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_resume_person` (`subsidy_person_id`),
  KEY `idx_resume_determination` (`determination_id`),
  KEY `idx_resume_month` (`resume_month`)
)  COMMENT='待遇恢复主表';

CREATE TABLE IF NOT EXISTS `shebao_benefit_resume_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resume_id` bigint NOT NULL COMMENT '恢复主表ID',
  `determination_item_id` bigint NOT NULL COMMENT '待遇核定子表ID',
  `subsidy_type` varchar(50) default NULL COMMENT '补贴类型',
  `pause_start_month` date DEFAULT NULL COMMENT '暂停年月（快照）',
  `need_resume` char(1) DEFAULT '1' COMMENT '是否需要恢复（0否 1是）',
  `resume_reason` varchar(200) DEFAULT NULL COMMENT '恢复原因',
  `resume_month` date DEFAULT NULL COMMENT '恢复年月',
  `supplement_start_month` date DEFAULT NULL COMMENT '补发开始年月',
  `supplement_end_month` date DEFAULT NULL COMMENT '补发终止年月',
  `supplement_months` int DEFAULT '0' COMMENT '补发月数',
  `supplement_amount` decimal(12,2) DEFAULT '0.00' COMMENT '补发金额',
  `subsidy_standard` decimal(10,2) DEFAULT '0.00' COMMENT '补贴标准（快照）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_resume_id` (`resume_id`),
  KEY `idx_resume_item_determination` (`determination_item_id`)
) COMMENT='待遇恢复明细表';
