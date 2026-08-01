-- 废弃旧发放三表（代码已不再读写）
-- 【务必】先完成应用发版（含菜单 SQL），确认无进程再访问旧表后执行本脚本。
-- 测试库当前业务数据为空；生产请先自行备份。

SET NAMES utf8mb4;

-- 审核流水依赖发放主表，先删子表
DROP TABLE IF EXISTS shebao_distribution_review;
DROP TABLE IF EXISTS shebao_subsidy_distribution;
DROP TABLE IF EXISTS distribution_batch;
