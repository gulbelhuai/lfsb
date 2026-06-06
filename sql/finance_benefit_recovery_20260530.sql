SET NAMES utf8mb4;

-- =====================================================================
-- 待遇追回：记录表（数据来源于待遇暂停明细中需追回项）
-- recovery_status: 0=未追回 1=已追回
-- =====================================================================

CREATE TABLE IF NOT EXISTS `shebao_finance_benefit_recovery` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `subsidy_person_id` bigint NOT NULL COMMENT '被补贴人ID',
  `id_card_no` varchar(18) NOT NULL COMMENT '身份证号快照',
  `subsidy_type` varchar(50) NOT NULL COMMENT '补贴类型',
  `suspension_id` bigint DEFAULT NULL COMMENT '关联暂停主表ID',
  `suspension_item_id` bigint DEFAULT NULL COMMENT '关联暂停明细ID',
  `recover_start_month` date DEFAULT NULL COMMENT '追回开始年月',
  `recover_end_month` date DEFAULT NULL COMMENT '追回终止年月',
  `recover_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '需追回金额',
  `recovery_status` char(1) NOT NULL DEFAULT '0' COMMENT '追回状态（0未追回 1已追回）',
  `recovery_time` datetime DEFAULT NULL COMMENT '追回时间',
  `account_transaction_id` bigint DEFAULT NULL COMMENT '关联财务账户明细ID',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_recovery_suspension_item` (`suspension_item_id`),
  KEY `idx_recovery_person` (`subsidy_person_id`),
  KEY `idx_recovery_suspension` (`suspension_id`),
  KEY `idx_recovery_status` (`recovery_status`),
  KEY `idx_recovery_subsidy_type` (`subsidy_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='财务-待遇追回记录';

-- 历史数据：从暂停明细同步需追回记录
INSERT INTO shebao_finance_benefit_recovery (
    subsidy_person_id, id_card_no, subsidy_type, suspension_id, suspension_item_id,
    recover_start_month, recover_end_month, recover_amount, recovery_status,
    del_flag, create_by, create_time, update_by, update_time
)
SELECT bs.subsidy_person_id,
       bs.id_card_no,
       bsi.subsidy_type,
       bs.id,
       bsi.id,
       bsi.recover_start_month,
       bsi.recover_end_month,
       IFNULL(bsi.recover_amount, 0),
       '0',
       '0',
       IFNULL(bsi.create_by, 'system'),
       IFNULL(bsi.create_time, NOW()),
       IFNULL(bsi.update_by, 'system'),
       IFNULL(bsi.update_time, NOW())
FROM shebao_benefit_suspension_item bsi
JOIN shebao_benefit_suspension bs ON bs.id = bsi.suspension_id AND bs.del_flag = '0'
WHERE bsi.del_flag = '0'
  AND bsi.need_recover = '1'
  AND NOT EXISTS (
      SELECT 1 FROM shebao_finance_benefit_recovery r
      WHERE r.suspension_item_id = bsi.id AND r.del_flag = '0'
  );

-- =====================================================================
-- 菜单（请手动添加，将 @finance_menu_id 替换为「财务管理」菜单ID）
-- =====================================================================
-- SET @finance_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '财务管理' AND parent_id = 0 LIMIT 1);
--
-- 目录菜单（C）：
-- menu_name: 待遇追回
-- parent_id: @finance_menu_id
-- order_num: 5
-- path: recovery
-- component: shebao/finance/recovery/index
-- menu_type: C
-- perms: shebao:finance:recovery:list
--
-- 按钮（F）：
-- menu_name: 确认已追回
-- parent_id: <待遇追回菜单ID>
-- perms: shebao:finance:recovery:confirm
-- menu_type: F
