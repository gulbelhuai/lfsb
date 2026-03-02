-- 修复 shebao_person_cancel 表中 del_flag 为 NULL 的记录
-- 问题原因：插入记录时未设置 del_flag 字段，导致查询时被 WHERE del_flag='0' 过滤掉

UPDATE shebao_person_cancel 
SET del_flag = '0' 
WHERE del_flag IS NULL;

-- 查看修复结果
SELECT 
    id,
    subsidy_person_id,
    death_date,
    del_flag,
    create_time
FROM shebao_person_cancel
ORDER BY create_time DESC;
