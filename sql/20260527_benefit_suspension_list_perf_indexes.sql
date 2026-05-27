-- 待遇暂停列表过滤与性能优化索引
-- 用途：支持“仅显示仍有暂停子项”的 EXISTS 查询，以及按原因/时间分页检索

ALTER TABLE `shebao_benefit_suspension`
    ADD INDEX `idx_del_reason_ctime_id` (`del_flag`, `pause_reason`, `create_time`, `id`);

ALTER TABLE `shebao_benefit_suspension_item`
    ADD INDEX `idx_sid_del_did_need` (`suspension_id`, `del_flag`, `determination_item_id`, `need_recover`);

ALTER TABLE `benefit_determination_item`
    ADD INDEX `idx_did_del_status_id` (`determination_id`, `del_flag`, `benefit_status`, `id`);
