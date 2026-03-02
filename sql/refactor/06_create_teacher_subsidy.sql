-- =============================================
-- 人员信息管理 - 教龄补助登记（原“教师补贴”）
-- 创建表：shebao_teacher_subsidy
-- 说明：与 BaseDomain 字段保持一致（del_flag/remark/create_time/update_time等）
-- 创建日期: 2026-01-24
-- =============================================

USE ry_vue;

DROP TABLE IF EXISTS `shebao_teacher_subsidy`;

CREATE TABLE `shebao_teacher_subsidy` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` BIGINT NOT NULL COMMENT '被补贴人ID（关联shebao_subsidy_person.id）',
  `school_name` VARCHAR(200) DEFAULT NULL COMMENT '学校名称',
  `teaching_years` INT DEFAULT NULL COMMENT '任教年限',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_subsidy_person_id` (`subsidy_person_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_teacher_subsidy_person`
    FOREIGN KEY (`subsidy_person_id`)
    REFERENCES `shebao_subsidy_person` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教龄补助登记表（原教师补贴）';

SELECT '✅ shebao_teacher_subsidy 创建成功' AS 'Status';

