-- 待遇暂停明细：按暂停批次标记是否仍生效（与核定子表全局 benefit_status 解耦）
SET NAMES utf8mb4;

SET @db := DATABASE();

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'shebao_benefit_suspension_item' AND COLUMN_NAME = 'pause_active');
SET @sql := IF(@c = 0,
    'ALTER TABLE shebao_benefit_suspension_item ADD COLUMN pause_active char(1) DEFAULT ''1'' COMMENT ''暂停是否仍生效（1是 0已恢复）'' AFTER need_recover',
    'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

UPDATE shebao_benefit_suspension_item
SET pause_active = '1'
WHERE pause_active IS NULL;

-- 历史数据：仅当前仍暂停的补贴，在其最近一次暂停批次上保留 pause_active=1
UPDATE shebao_benefit_suspension_item bsi
SET pause_active = '0'
WHERE bsi.del_flag = '0';

UPDATE shebao_benefit_suspension_item bsi
JOIN (
    SELECT bsi2.determination_item_id,
           MAX(bs2.id) AS max_suspension_id
    FROM shebao_benefit_suspension_item bsi2
    JOIN shebao_benefit_suspension bs2 ON bs2.id = bsi2.suspension_id AND bs2.del_flag = '0'
    JOIN benefit_determination_item bdi ON bdi.id = bsi2.determination_item_id
        AND bdi.del_flag = '0'
        AND bdi.benefit_status = '1'
    WHERE bsi2.del_flag = '0'
    GROUP BY bsi2.determination_item_id
) latest ON latest.determination_item_id = bsi.determination_item_id
    AND latest.max_suspension_id = bsi.suspension_id
SET bsi.pause_active = '1'
WHERE bsi.del_flag = '0';
