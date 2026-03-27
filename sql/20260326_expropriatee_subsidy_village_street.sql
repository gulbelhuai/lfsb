-- 被征地参保补贴：征地时所在村街
ALTER TABLE shebao_expropriatee_subsidy
    ADD COLUMN village_street varchar(100) DEFAULT NULL COMMENT '征地时所在村街' AFTER land_requisition_batch;

