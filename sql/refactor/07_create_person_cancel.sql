-- =============================================
-- 人员信息管理 - 人员注销登记（死亡登记）业务表
-- 创建表：shebao_person_cancel
-- 说明：记录注销登记历史（可增删改查）
-- 创建日期: 2026-01-24
-- =============================================

USE ry_vue;

DROP TABLE IF EXISTS `shebao_person_cancel`;

CREATE TABLE `shebao_person_cancel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` BIGINT NOT NULL COMMENT '被补贴人ID（关联shebao_subsidy_person.id）',
  `death_date` DATE NOT NULL COMMENT '死亡时间',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_subsidy_person_id` (`subsidy_person_id`),
  KEY `idx_death_date` (`death_date`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_person_cancel_person`
    FOREIGN KEY (`subsidy_person_id`)
    REFERENCES `shebao_subsidy_person` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员注销登记表（死亡登记）';

SELECT '✅ shebao_person_cancel 创建成功' AS 'Status';

