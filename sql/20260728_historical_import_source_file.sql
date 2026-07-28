-- 历史数据导入：保存原始导入文件路径（磁盘存 ASCII 别名）
ALTER TABLE `shebao_historical_import_batch`
    ADD COLUMN `source_file_path` VARCHAR(500) DEFAULT NULL COMMENT '导入源文件路径（别名）' AFTER `file_name`;
