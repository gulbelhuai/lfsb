-- =============================================
-- 创建行政区划表（如果需要）
-- 说明: 该表用于管理多级行政区划（省/市/县/乡镇/村）
-- 注意: 当前系统使用 shebao_street_office 和 shebao_village_committee 表
--      本表为可选表，如果需要更完整的行政区划管理可以创建
-- =============================================

-- 检查表是否存在，如果不存在则创建
DROP TABLE IF EXISTS `shebao_administrative_division`;

CREATE TABLE `shebao_administrative_division` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `division_code` VARCHAR(50) NOT NULL COMMENT '行政区划编码',
    `division_name` VARCHAR(100) NOT NULL COMMENT '行政区划名称',
    `parent_code` VARCHAR(50) COMMENT '父级编码',
    `division_level` TINYINT NOT NULL COMMENT '行政级别（1省 2市 3县 4乡镇 5村）',
    `full_code` VARCHAR(500) COMMENT '全路径编码，用/分隔',
    `full_name` VARCHAR(500) COMMENT '全路径名称，用/分隔',
    `contact_person` VARCHAR(50) COMMENT '联系人',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `address` VARCHAR(200) COMMENT '详细地址',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_division_code` (`division_code`),
    INDEX `idx_parent_code` (`parent_code`),
    INDEX `idx_division_level` (`division_level`),
    INDEX `idx_full_code` (`full_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行政区划表';

-- 插入河北省廊坊市的行政区划数据
INSERT INTO `shebao_administrative_division`
    (`division_code`, `division_name`, `parent_code`, `division_level`, `full_code`, `full_name`, `sort_order`, `status`)
VALUES
    ('130000', '河北省', NULL, 1, '130000', '河北省', 1, '0'),
    ('131000', '廊坊市', '130000', 2, '130000/131000', '河北省/廊坊市', 1, '0'),
    ('131001', '安次区', '131000', 3, '130000/131000/131001', '河北省/廊坊市/安次区', 1, '0'),
    ('131002', '广阳区', '131000', 3, '130000/131000/131002', '河北省/廊坊市/广阳区', 2, '0'),
    ('131003', '开发区', '131000', 3, '130000/131000/131003', '河北省/廊坊市/开发区', 3, '0');

-- 插入街道办事处（示例）
INSERT INTO `shebao_administrative_division`
    (`division_code`, `division_name`, `parent_code`, `division_level`, `full_code`, `full_name`, `sort_order`, `status`)
VALUES
    ('131001001', '银河南路街道', '131001', 4, '130000/131000/131001/131001001', '河北省/廊坊市/安次区/银河南路街道', 1, '0'),
    ('131001002', '光明西道街道', '131001', 4, '130000/131000/131001/131001002', '河北省/廊坊市/安次区/光明西道街道', 2, '0'),
    ('131002001', '解放道街道', '131002', 4, '130000/131000/131002/131002001', '河北省/廊坊市/广阳区/解放道街道', 1, '0'),
    ('131002002', '新开路街道', '131002', 4, '130000/131000/131002/131002002', '河北省/廊坊市/广阳区/新开路街道', 2, '0');

-- 验证数据
SELECT '✅ 行政区划表创建完成！' AS 'Status';
SELECT
    division_level AS '级别',
    COUNT(*) AS '数量'
FROM shebao_administrative_division
GROUP BY division_level
ORDER BY division_level;

SELECT
    ad.division_name AS '名称',
    ad.division_level AS '级别',
    ad.full_name AS '全路径',
    p.division_name AS '上级名称'
FROM shebao_administrative_division ad
LEFT JOIN shebao_administrative_division p ON ad.parent_code = p.division_code
ORDER BY ad.full_code;
