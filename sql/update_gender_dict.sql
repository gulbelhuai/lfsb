-- 更新性别字典数据，删除"未知"选项
-- 只保留男(1)和女(2)两个选项

-- 删除"未知"选项
DELETE FROM sys_dict_data WHERE dict_code = 3 AND dict_type = 'sys_user_sex';

-- 更新性别字典数据，确保值正确
UPDATE sys_dict_data SET dict_value = '1' WHERE dict_code = 1 AND dict_type = 'sys_user_sex'; -- 男
UPDATE sys_dict_data SET dict_value = '2' WHERE dict_code = 2 AND dict_type = 'sys_user_sex'; -- 女

-- 验证更新结果
SELECT * FROM sys_dict_data WHERE dict_type = 'sys_user_sex' ORDER BY dict_sort;
