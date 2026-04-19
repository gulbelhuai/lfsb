-- 待遇核定：多补贴明细 + 发放方式字段（与 2026-04 需求对齐）
-- 执行前请备份数据库；建议在测试库验证后再上生产。

SET NAMES utf8mb4;

-- ---------------------------------------------------------------------------
-- 1) 核定明细表：一人多补贴，每类补贴一行
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `benefit_determination_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `determination_id` bigint NOT NULL COMMENT '核定主表ID',
  `subsidy_type` varchar(50) NOT NULL COMMENT '补贴类型 land_loss/demolition/village_official 等',
  `village_street` varchar(200) DEFAULT NULL COMMENT '认定时所在村街',
  `event_date` date DEFAULT NULL COMMENT '征地/拆迁时间（村干部可空）',
  `subsidy_standard` decimal(10,2) DEFAULT NULL COMMENT '补贴标准（月标准）',
  `benefit_start_year` int DEFAULT NULL COMMENT '享受开始年',
  `benefit_start_month` int DEFAULT NULL COMMENT '享受开始月',
  `benefit_months` int DEFAULT '0' COMMENT '补发月数',
  `benefit_amount` decimal(12,2) DEFAULT '0.00' COMMENT '补发金额',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_determination_id` (`determination_id`),
  KEY `idx_subsidy_type` (`subsidy_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='待遇核定明细（按补贴类型分条）';

-- ---------------------------------------------------------------------------
-- 2) 主表：发放方式字段（若已存在请跳过或手工调整）
-- ---------------------------------------------------------------------------
SET @db := DATABASE();

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='benefit_determination' AND COLUMN_NAME='grant_org');
SET @sql := IF(@c=0, 'ALTER TABLE benefit_determination ADD COLUMN grant_org varchar(64) DEFAULT NULL COMMENT ''发放机构(dict:shebao_grant_org)'' AFTER eligible_month', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='benefit_determination' AND COLUMN_NAME='account_name');
SET @sql := IF(@c=0, 'ALTER TABLE benefit_determination ADD COLUMN account_name varchar(64) DEFAULT NULL COMMENT ''开户名'' AFTER grant_org', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='benefit_determination' AND COLUMN_NAME='relation_to_insured');
SET @sql := IF(@c=0, 'ALTER TABLE benefit_determination ADD COLUMN relation_to_insured varchar(32) DEFAULT NULL COMMENT ''与参保人关系'' AFTER account_name', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='benefit_determination' AND COLUMN_NAME='grant_remark');
SET @sql := IF(@c=0, 'ALTER TABLE benefit_determination ADD COLUMN grant_remark varchar(500) DEFAULT NULL COMMENT ''发放备注'' AFTER bank_account', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- 开户名历史数据回填（旧字段 bank_account_name）
UPDATE benefit_determination
SET account_name = bank_account_name
WHERE (account_name IS NULL OR account_name = '')
  AND bank_account_name IS NOT NULL
  AND bank_account_name <> '';

-- ---------------------------------------------------------------------------
-- 3) 历史数据迁移：每条旧核定生成一条明细（便于支付计划按明细行去重）
-- ---------------------------------------------------------------------------
INSERT INTO benefit_determination_item (
  determination_id, subsidy_type, village_street, event_date,
  subsidy_standard, benefit_start_year, benefit_start_month, benefit_months, benefit_amount,
  del_flag, create_time, update_time
)
SELECT
  bd.id,
  CASE bd.subsidy_type
    WHEN 'land_loss_resident' THEN 'land_loss'
    WHEN 'demolition_resident' THEN 'demolition'
    WHEN 'village_official' THEN 'village_official'
    WHEN 'expropriatee_subsidy' THEN 'expropriatee'
    WHEN 'teacher_subsidy' THEN 'teacher'
    ELSE IFNULL(bd.subsidy_type, 'land_loss')
  END,
  NULL,
  NULL,
  bd.subsidy_standard,
  bd.benefit_start_year,
  bd.benefit_start_month,
  IFNULL(bd.benefit_months, 0),
  IFNULL(bd.benefit_amount, 0.00),
  '0',
  NOW(),
  NOW()
FROM benefit_determination bd
WHERE bd.del_flag = '0'
  AND NOT EXISTS (
    SELECT 1 FROM benefit_determination_item i
    WHERE i.determination_id = bd.id AND i.del_flag = '0'
  );

-- ---------------------------------------------------------------------------
-- 4) 系统参数（失地/拆迁月补贴标准）——若不存在则插入占位，请按实际金额修改
-- ---------------------------------------------------------------------------
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '失地居民月补贴标准', 'shebao.land.loss.subsidy', '0', 'N', 'admin', NOW(), '待遇核定：失地补贴标准（元/月）'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'shebao.land.loss.subsidy');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '拆迁居民月补贴标准', 'shebao.demolition.subsidy', '0', 'N', 'admin', NOW(), '待遇核定：拆迁补贴标准（元/月）'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'shebao.demolition.subsidy');

-- ---------------------------------------------------------------------------
-- 5) 可选：确认字典 shebao_grant_org 已在 sys_dict_type / sys_dict_data 中维护
-- （你方已创建则无需执行；此处仅作检查提示）
-- SELECT * FROM sys_dict_type WHERE dict_type = 'shebao_grant_org';
-- SELECT * FROM sys_dict_data WHERE dict_type = 'shebao_grant_org' ORDER BY dict_sort;

-- ---------------------------------------------------------------------------
-- 6) 可选：删除主表「通知批次」相关字段（确认无业务依赖后再执行）
-- ALTER TABLE benefit_determination DROP INDEX idx_notice_batch_no;
-- ALTER TABLE benefit_determination DROP INDEX idx_notice_detail_id;
-- ALTER TABLE benefit_determination DROP COLUMN notice_batch_no;
-- ALTER TABLE benefit_determination DROP COLUMN notice_detail_id;
--
-- 7) 可选：附件表移除通知批次字段（已改为按 determination_id 关联）
-- ALTER TABLE shebao_benefit_attachment DROP COLUMN notice_batch_no;
