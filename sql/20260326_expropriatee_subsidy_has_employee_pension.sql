-- 被征地参保补贴：是否已领取职工养老保险（与前端 hasEmployeePension 一致）
ALTER TABLE shebao_expropriatee_subsidy
    ADD COLUMN has_employee_pension char(1) DEFAULT '0' NULL COMMENT '是否已领取职工养老保险待遇（0未领取 1已领取）' AFTER join_employee_pension;

