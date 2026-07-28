-- 历史数据导入批次记录
CREATE TABLE IF NOT EXISTS `shebao_historical_import_batch` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `subsidy_type`    VARCHAR(50)  NOT NULL COMMENT '补贴类型：land_loss_resident/expropriatee/...',
    `file_name`       VARCHAR(255) NOT NULL COMMENT '导入文件名',
    `source_file_path` VARCHAR(500) DEFAULT NULL COMMENT '导入源文件路径（别名）',
    `total_rows`      INT          DEFAULT 0 COMMENT '导入行数',
    `success_rows`    INT          DEFAULT 0 COMMENT '成功行数',
    `failure_rows`    INT          DEFAULT 0 COMMENT '失败行数',
    `failure_file_path` VARCHAR(500) DEFAULT NULL COMMENT '失败记录Excel路径',
    `import_status`   VARCHAR(20)  DEFAULT 'completed' COMMENT 'completed/partial_failed/failed',
    `del_flag`        CHAR(1)      DEFAULT '0',
    `create_by`       VARCHAR(64)  DEFAULT '',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `update_by`       VARCHAR(64)  DEFAULT '',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `remark`          VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_subsidy_type` (`subsidy_type`),
    KEY `idx_create_time` (`create_time`)
)  COMMENT='历史数据导入批次';
