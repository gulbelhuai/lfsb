-- 村干部任职：任职年限（按上任/卸任日期周年取整，可由程序回填）
ALTER TABLE shebao_village_official_position
    ADD COLUMN service_years decimal(14,2) DEFAULT NULL COMMENT '任职年限' AFTER end_date;
