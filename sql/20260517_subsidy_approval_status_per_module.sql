-- 四类补贴模块审核状态下沉至子表，并从主表 shebao_subsidy_person 移除 approval_status
-- 执行前请备份数据库。建议在测试环境验证后再上生产。

-- 1. 子表新增 approval_status
ALTER TABLE `shebao_land_loss_resident`
    ADD COLUMN `approval_status` varchar(20) NOT NULL DEFAULT 'draft'
        COMMENT '审批状态(draft/pending_review/approved/rejected)' AFTER `is_village_coop_member`;

ALTER TABLE `shebao_expropriatee_subsidy`
    ADD COLUMN `approval_status` varchar(20) NOT NULL DEFAULT 'draft'
        COMMENT '审批状态(draft/pending_review/approved/rejected)' AFTER `status`;

ALTER TABLE `shebao_demolition_resident`
    ADD COLUMN `approval_status` varchar(20) NOT NULL DEFAULT 'draft'
        COMMENT '审批状态(draft/pending_review/approved/rejected)' AFTER `village_street`;

ALTER TABLE `shebao_village_official`
    ADD COLUMN `approval_status` varchar(20) NOT NULL DEFAULT 'draft'
        COMMENT '审批状态(draft/pending_review/approved/rejected)' AFTER `status`;

-- 2. 将主表 approval_status 同步到各子表（仅同步未删除记录）
UPDATE `shebao_land_loss_resident` ll
    INNER JOIN `shebao_subsidy_person` sp ON ll.subsidy_person_id = sp.id
SET ll.approval_status = COALESCE(NULLIF(sp.approval_status, ''), 'approved')
WHERE ll.del_flag = '0';

UPDATE `shebao_expropriatee_subsidy` es
    INNER JOIN `shebao_subsidy_person` sp ON es.subsidy_person_id = sp.id
SET es.approval_status = COALESCE(NULLIF(sp.approval_status, ''), 'approved')
WHERE es.del_flag = '0';

UPDATE `shebao_demolition_resident` dr
    INNER JOIN `shebao_subsidy_person` sp ON dr.subsidy_person_id = sp.id
SET dr.approval_status = COALESCE(NULLIF(sp.approval_status, ''), 'approved')
WHERE dr.del_flag = '0';

UPDATE `shebao_village_official` vo
    INNER JOIN `shebao_subsidy_person` sp ON vo.subsidy_person_id = sp.id
SET vo.approval_status = COALESCE(NULLIF(sp.approval_status, ''), 'approved')
WHERE vo.del_flag = '0';

-- 3. 子表索引
ALTER TABLE `shebao_land_loss_resident` ADD INDEX `idx_approval_status` (`approval_status`);
ALTER TABLE `shebao_expropriatee_subsidy` ADD INDEX `idx_approval_status` (`approval_status`);
ALTER TABLE `shebao_demolition_resident` ADD INDEX `idx_approval_status` (`approval_status`);
ALTER TABLE `shebao_village_official` ADD INDEX `idx_approval_status` (`approval_status`);

-- 4. 教师子表（登记复核仍使用，与主表脱钩）
ALTER TABLE `shebao_teacher_subsidy`
    ADD COLUMN `approval_status` varchar(20) NOT NULL DEFAULT 'draft'
        COMMENT '审批状态(draft/pending_review/approved/rejected)' AFTER `teaching_years`;

UPDATE `shebao_teacher_subsidy` ts
    INNER JOIN `shebao_subsidy_person` sp ON ts.subsidy_person_id = sp.id
SET ts.approval_status = COALESCE(NULLIF(sp.approval_status, ''), 'approved')
WHERE ts.del_flag = '0';

ALTER TABLE `shebao_teacher_subsidy` ADD INDEX `idx_approval_status` (`approval_status`);

-- 5. 删除主表 approval_status（物理删除，便于遗漏编译/运行时报错）
ALTER TABLE `shebao_subsidy_person` DROP COLUMN `approval_status`;
