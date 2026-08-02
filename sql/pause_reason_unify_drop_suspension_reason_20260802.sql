-- ========================================
-- ISS-016：清理遗留 suspension_reason（针对当前测试库）
-- 日期：2026-08-02
-- 【由用户执行】Agent 禁止连库执行本脚本
-- 说明：测试库已有正确的 pause_reason，本脚本只删多余字典、DROP 人员表死列。
-- ========================================

SET NAMES utf8mb4;

-- 1) 删除遗留字典 suspension_reason（与业务无关）
DELETE FROM sys_dict_data WHERE dict_type = 'suspension_reason';
DELETE FROM sys_dict_type WHERE dict_type = 'suspension_reason';

-- 2) 人员表死列 DROP（暂停原因在 shebao_benefit_suspension.pause_reason）
ALTER TABLE shebao_subsidy_person DROP COLUMN suspension_reason;
