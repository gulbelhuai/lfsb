-- 村干部任职：任职年限 = 整周年数 + 余下天数/365，两位小数（程序回填）
ALTER TABLE shebao_village_official_position
    ADD COLUMN service_years decimal(14,2) DEFAULT NULL COMMENT '任职年限' AFTER end_date;

select * from shebao_village_official;
select * from shebao_village_official_position;