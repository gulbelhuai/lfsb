-- =====================================================
-- 失地居民信息修改 - 组织架构和基础信息优化
-- 创建时间：2025-10-18
-- 说明：新增街道办和村委会管理，优化基础人员信息字段
-- =====================================================

-- =====================================================
-- 第一部分：创建新表
-- =====================================================

-- 1. 创建街道办表
CREATE TABLE IF NOT EXISTS `shebao_street_office` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `street_code` VARCHAR(3) NOT NULL COMMENT '街道办编码（3位数字，如：001）',
    `street_name` VARCHAR(50) NOT NULL COMMENT '街道办名称',
    `contact_person` VARCHAR(50) NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) NULL COMMENT '联系电话',
    `address` VARCHAR(200) NULL COMMENT '详细地址',
    `sort_order` INT DEFAULT 0 NULL COMMENT '排序号',
    `status` CHAR(1) DEFAULT '0' NULL COMMENT '状态（0正常 1停用）',
    `del_flag` CHAR(1) DEFAULT '0' NULL COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by` VARCHAR(64) DEFAULT '' NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_street_code` (`street_code`),
    INDEX `idx_street_name` (`street_name`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='街道办事处信息表';

-- 2. 创建村委会表
CREATE TABLE IF NOT EXISTS `shebao_village_committee` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `street_office_id` BIGINT NOT NULL COMMENT '所属街道办ID',
    `village_code` VARCHAR(3) NOT NULL COMMENT '村委会编码（3位数字，如：002）',
    `village_name` VARCHAR(50) NOT NULL COMMENT '村委会名称',
    `contact_person` VARCHAR(50) NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) NULL COMMENT '联系电话',
    `address` VARCHAR(200) NULL COMMENT '详细地址',
    `sort_order` INT DEFAULT 0 NULL COMMENT '排序号',
    `status` CHAR(1) DEFAULT '0' NULL COMMENT '状态（0正常 1停用）',
    `del_flag` CHAR(1) DEFAULT '0' NULL COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by` VARCHAR(64) DEFAULT '' NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    INDEX `idx_street_office_id` (`street_office_id`),
    INDEX `idx_village_name` (`village_name`),
    INDEX `idx_status` (`status`),
    UNIQUE KEY `uk_street_village_code` (`street_office_id`, `village_code`),
    CONSTRAINT `fk_village_committee_street_office` FOREIGN KEY (`street_office_id`) 
        REFERENCES `shebao_street_office`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='村委会信息表';

-- =====================================================
-- 第二部分：修改基础人员信息表（shebao_subsidy_person）
-- =====================================================

-- 1. 添加新字段
ALTER TABLE `shebao_subsidy_person` 
    ADD COLUMN `is_alive` CHAR(1) DEFAULT '1' NULL COMMENT '是否健在（0否 1是）' AFTER `has_employee_pension`,
    ADD COLUMN `death_date` DATE NULL COMMENT '死亡时间' AFTER `is_alive`,
    ADD COLUMN `is_village_coop_member` CHAR(1) DEFAULT '1' NULL COMMENT '是否村合作经济组织成员（0否 1是）' AFTER `death_date`,
    ADD COLUMN `street_office_id` BIGINT NULL COMMENT '所属街道办ID' AFTER `is_village_coop_member`,
    ADD COLUMN `village_committee_id` BIGINT NULL COMMENT '所属村委会ID' AFTER `street_office_id`,
    ADD COLUMN `user_code` VARCHAR(10) NULL COMMENT '用户编号（格式：001-002-0001）' AFTER `village_committee_id`;

-- 2. 修改籍贯字段名称（需要保留数据）
ALTER TABLE `shebao_subsidy_person` 
    CHANGE COLUMN `native_place` `household_registration` VARCHAR(100) NOT NULL COMMENT '户籍所在地';

-- 3. 添加索引
ALTER TABLE `shebao_subsidy_person`
    ADD INDEX `idx_street_office_id` (`street_office_id`),
    ADD INDEX `idx_village_committee_id` (`village_committee_id`),
    ADD UNIQUE INDEX `uk_user_code` (`user_code`),
    ADD INDEX `idx_is_alive` (`is_alive`);

-- 4. 添加外键约束（数据迁移后再执行）
-- ALTER TABLE `shebao_subsidy_person`
--     ADD CONSTRAINT `fk_subsidy_person_street_office` FOREIGN KEY (`street_office_id`) 
--         REFERENCES `shebao_street_office`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
--     ADD CONSTRAINT `fk_subsidy_person_village_committee` FOREIGN KEY (`village_committee_id`) 
--         REFERENCES `shebao_village_committee`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- 5. 备份待删除字段的数据（可选，建议在删除前备份）
-- CREATE TABLE IF NOT EXISTS `shebao_subsidy_person_backup_20251018` 
-- SELECT id, home_address, household_no, has_employee_pension, village_code, village_name, status 
-- FROM `shebao_subsidy_person`;

-- 6. 删除不再使用的字段（数据迁移和确认后再执行）
-- ALTER TABLE `shebao_subsidy_person`
--     DROP COLUMN `home_address`,
--     DROP COLUMN `household_no`,
--     DROP COLUMN `has_employee_pension`,
--     DROP COLUMN `village_code`,
--     DROP COLUMN `village_name`,
--     DROP COLUMN `status`;

-- =====================================================
-- 第三部分：修改失地居民信息表（shebao_land_loss_resident）
-- =====================================================

-- 1. 备份待删除字段的数据（可选，建议在删除前备份）
-- CREATE TABLE IF NOT EXISTS `shebao_land_loss_resident_backup_20251018`
-- SELECT id, is_under_16, subsidy_amount, distribution_status, status 
-- FROM `shebao_land_loss_resident`;

-- 2. 删除不再使用的字段（数据迁移和确认后再执行）
-- ALTER TABLE `shebao_land_loss_resident`
--     DROP COLUMN `is_under_16`,
--     DROP COLUMN `subsidy_amount`,
--     DROP COLUMN `distribution_status`,
--     DROP COLUMN `status`;

-- =====================================================
-- 第四部分：数据迁移脚本
-- =====================================================

-- 注意：以下脚本需要根据实际数据情况调整
-- 建议在测试环境先执行，确认无误后再在生产环境执行

-- 1. 插入示例街道办数据（请根据实际情况修改）
-- INSERT INTO `shebao_street_office` (`street_code`, `street_name`, `sort_order`, `create_by`) VALUES
-- ('001', '银河南路街道', 1, 'admin'),
-- ('002', '龙河路街道', 2, 'admin'),
-- ('003', '光明路街道', 3, 'admin');

-- 2. 插入示例村委会数据（请根据实际情况修改）
-- INSERT INTO `shebao_village_committee` (`street_office_id`, `village_code`, `village_name`, `sort_order`, `create_by`) 
-- SELECT so.id, '001', '第一村', 1, 'admin' FROM shebao_street_office so WHERE so.street_code = '001'
-- UNION ALL
-- SELECT so.id, '002', '第二村', 2, 'admin' FROM shebao_street_office so WHERE so.street_code = '001'
-- UNION ALL
-- SELECT so.id, '001', '东村', 1, 'admin' FROM shebao_street_office so WHERE so.street_code = '002';

-- 3. 从行政区划表迁移街道办数据（如果需要）
-- 根据实际的行政区划数据结构调整
-- INSERT INTO `shebao_street_office` (`street_code`, `street_name`, `sort_order`, `create_by`)
-- SELECT 
--     RIGHT(division_code, 3) as street_code,
--     division_name as street_name,
--     sort_order,
--     'admin' as create_by
-- FROM `shebao_administrative_division`
-- WHERE division_level = 4  -- 假设4级是街道办
-- AND status = '0';

-- 4. 从行政区划表迁移村委会数据（如果需要）
-- INSERT INTO `shebao_village_committee` (`street_office_id`, `village_code`, `village_name`, `sort_order`, `create_by`)
-- SELECT 
--     so.id as street_office_id,
--     RIGHT(ad.division_code, 3) as village_code,
--     ad.division_name as village_name,
--     ad.sort_order,
--     'admin' as create_by
-- FROM `shebao_administrative_division` ad
-- INNER JOIN `shebao_administrative_division` parent ON ad.parent_code = parent.division_code
-- INNER JOIN `shebao_street_office` so ON RIGHT(parent.division_code, 3) = so.street_code
-- WHERE ad.division_level = 5  -- 假设5级是村委会
-- AND ad.status = '0';

-- 5. 更新基础人员信息表的街道办和村委会关联（根据原village_code）
-- 这个脚本需要根据实际的village_code与新的village_committee的映射关系来编写
-- UPDATE `shebao_subsidy_person` sp
-- INNER JOIN `shebao_village_committee` vc ON sp.village_code = CONCAT(某种映射规则)
-- INNER JOIN `shebao_street_office` so ON vc.street_office_id = so.id
-- SET sp.street_office_id = so.id,
--     sp.village_committee_id = vc.id
-- WHERE sp.street_office_id IS NULL;

-- 6. 生成用户编号
-- 此脚本为每个村委会下的居民按创建时间顺序生成编号
-- SET @row_num = 0;
-- SET @current_village = 0;
-- 
-- UPDATE `shebao_subsidy_person` sp
-- INNER JOIN `shebao_village_committee` vc ON sp.village_committee_id = vc.id
-- INNER JOIN `shebao_street_office` so ON vc.street_office_id = so.id
-- INNER JOIN (
--     SELECT 
--         sp2.id,
--         so2.street_code,
--         vc2.village_code,
--         @row_num := IF(@current_village = vc2.id, @row_num + 1, 1) AS seq_num,
--         @current_village := vc2.id AS current_village
--     FROM `shebao_subsidy_person` sp2
--     INNER JOIN `shebao_village_committee` vc2 ON sp2.village_committee_id = vc2.id
--     INNER JOIN `shebao_street_office` so2 ON vc2.street_office_id = so2.id
--     ORDER BY vc2.id, sp2.create_time
-- ) seq ON sp.id = seq.id
-- SET sp.user_code = CONCAT(
--     so.street_code, 
--     '-', 
--     vc.village_code, 
--     '-', 
--     LPAD(seq.seq_num, 4, '0')
-- )
-- WHERE sp.user_code IS NULL;

-- =====================================================
-- 第五部分：性别字段调整（如果需要修改字典值）
-- =====================================================

-- 注意：如果系统中性别使用的是字典，需要确保字典中去掉"未知"选项
-- 确认没有记录使用了"未知"值
-- SELECT COUNT(*) FROM shebao_subsidy_person WHERE gender NOT IN ('1', '2');

-- 如果有"未知"值，需要先处理这些数据
-- UPDATE shebao_subsidy_person SET gender = '1' WHERE gender = '0' AND 身份证号第17位为奇数;
-- UPDATE shebao_subsidy_person SET gender = '2' WHERE gender = '0' AND 身份证号第17位为偶数;

-- =====================================================
-- 第六部分：验证脚本
-- =====================================================

-- 1. 检查街道办数据
-- SELECT COUNT(*) as street_office_count FROM shebao_street_office WHERE del_flag = '0';

-- 2. 检查村委会数据
-- SELECT COUNT(*) as village_committee_count FROM shebao_village_committee WHERE del_flag = '0';

-- 3. 检查人员关联情况
-- SELECT 
--     COUNT(*) as total_persons,
--     SUM(CASE WHEN street_office_id IS NOT NULL THEN 1 ELSE 0 END) as has_street_office,
--     SUM(CASE WHEN village_committee_id IS NOT NULL THEN 1 ELSE 0 END) as has_village_committee,
--     SUM(CASE WHEN user_code IS NOT NULL THEN 1 ELSE 0 END) as has_user_code
-- FROM shebao_subsidy_person
-- WHERE del_flag = '0';

-- 4. 检查用户编号唯一性
-- SELECT user_code, COUNT(*) as count 
-- FROM shebao_subsidy_person 
-- WHERE user_code IS NOT NULL 
-- GROUP BY user_code 
-- HAVING count > 1;

-- 5. 检查是否健在和死亡时间的逻辑
-- SELECT COUNT(*) as error_count
-- FROM shebao_subsidy_person
-- WHERE is_alive = '0' AND death_date IS NULL;

-- =====================================================
-- 注意事项
-- =====================================================
-- 1. 本脚本中被注释的部分（DELETE、DROP等危险操作）需要在数据迁移完成并验证无误后手动执行
-- 2. 建议在测试环境先执行，确认无误后再在生产环境执行
-- 3. 执行前请备份相关表的数据
-- 4. 数据迁移脚本需要根据实际的数据情况进行调整
-- 5. 外键约束建议在数据迁移完成后再添加
-- 6. 用户编号生成脚本较复杂，建议通过程序批量生成或人工审核
-- =====================================================

