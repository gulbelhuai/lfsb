-- 村干部补贴：认定时所在村街
ALTER TABLE shebao_village_official
    ADD COLUMN village_street varchar(100) DEFAULT NULL COMMENT '认定时所在村街' AFTER has_violation;
