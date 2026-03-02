-- =============================================
-- 人员关键信息修改申请表 person_key_info_modify
-- 关键信息：姓名、身份证号、户籍所在地、所属街道办、所属村委会
-- 审批流程：经办-复核-审批 三级
-- =============================================

-- 若依库名可能是 ry_vue 或项目实际库名，执行前请替换
-- USE ry_vue;

DROP TABLE IF EXISTS `person_key_info_modify`;

CREATE TABLE `person_key_info_modify` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `subsidy_person_id` BIGINT NOT NULL COMMENT '被补贴人ID',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `id_card_no` VARCHAR(18) NOT NULL COMMENT '身份证号',
    `household_registration` VARCHAR(200) COMMENT '户籍所在地',
    `street_office_id` BIGINT COMMENT '所属街道办ID',
    `village_committee_id` BIGINT COMMENT '所属村委会ID',
    `approval_status` VARCHAR(20) DEFAULT 'draft' COMMENT '审批状态(draft-草稿 pending_review-待复核 pending_approve-待审批 approved-已通过 rejected-已驳回)',
    `submit_by` VARCHAR(64) COMMENT '提交人',
    `submit_time` DATETIME COMMENT '提交时间',
    `review_by` VARCHAR(64) COMMENT '复核人',
    `review_time` DATETIME COMMENT '复核时间',
    `approve_by` VARCHAR(64) COMMENT '审批人',
    `approve_time` DATETIME COMMENT '审批时间',
    `reject_reason` VARCHAR(500) COMMENT '驳回原因',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    INDEX `idx_subsidy_person_id` (`subsidy_person_id`),
    INDEX `idx_approval_status` (`approval_status`),
    INDEX `idx_id_card_no` (`id_card_no`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员关键信息修改申请表';
