-- 待遇暂停一期：主子表 + 核定子表状态字段
SET NAMES utf8mb4;

-- 1) 待遇核定子表新增状态字段
SET @db := DATABASE();

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='benefit_determination_item' AND COLUMN_NAME='benefit_status');
SET @sql := IF(@c=0, 'ALTER TABLE benefit_determination_item ADD COLUMN benefit_status char(1) DEFAULT ''0'' COMMENT ''待遇状态（0正常 1暂停）'' AFTER benefit_amount', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='benefit_determination_item' AND COLUMN_NAME='pause_start_month');
SET @sql := IF(@c=0, 'ALTER TABLE benefit_determination_item ADD COLUMN pause_start_month date DEFAULT NULL COMMENT ''暂停开始年月'' AFTER benefit_status', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

UPDATE benefit_determination_item
SET benefit_status = '0'
WHERE benefit_status IS NULL;

-- 2) 待遇暂停主表
CREATE TABLE IF NOT EXISTS `shebao_benefit_suspension` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `determination_id` bigint NOT NULL COMMENT '待遇核定主表ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `id_card_no` varchar(18) NOT NULL COMMENT '身份证号快照',
  `pause_month` date NOT NULL COMMENT '暂停年月',
  `pause_reason` varchar(50) default NULL COMMENT '暂停原因（字典：pause_reason）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_person_id` (`subsidy_person_id`),
  KEY `idx_determination_id` (`determination_id`),
  KEY `idx_pause_month` (`pause_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='待遇暂停主表';

-- 3) 待遇暂停追回明细表
CREATE TABLE IF NOT EXISTS `shebao_benefit_suspension_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `suspension_id` bigint NOT NULL COMMENT '暂停主表ID',
  `determination_item_id` bigint NOT NULL COMMENT '待遇核定子表ID',
  `subsidy_type` varchar(50) NOT NULL COMMENT '补贴类型',
  `benefit_start_month` date DEFAULT NULL COMMENT '享受开始年月',
  `subsidy_standard` decimal(10,2) DEFAULT '0.00' COMMENT '补贴标准',
  `need_recover` char(1) DEFAULT '0' COMMENT '是否需要追回（0否 1是）',
  `recover_start_month` date DEFAULT NULL COMMENT '追回开始年月',
  `recover_end_month` date DEFAULT NULL COMMENT '追回结束年月',
  `recover_months` int DEFAULT '0' COMMENT '追回月数',
  `recover_amount` decimal(12,2) DEFAULT '0.00' COMMENT '追回金额',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_suspension_id` (`suspension_id`),
  KEY `idx_determination_item_id` (`determination_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='待遇暂停追回明细表';
