-- 拆迁居民：认定时所在村街
ALTER TABLE shebao_demolition_resident
    ADD COLUMN village_street varchar(100) DEFAULT NULL COMMENT '认定时所在村街' AFTER recognition_time;

