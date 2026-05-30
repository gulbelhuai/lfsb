SET NAMES utf8mb4;

-- =====================================================================
-- 财务账户明细表：记录每次账户余额变动
-- transaction_type: fiscal_allocation=财政拨款, subsidy_distribution=补贴发放, benefit_recovery=待遇追回
-- amount: 正数=收入，负数=支出
-- =====================================================================

CREATE TABLE IF NOT EXISTS `finance_account_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_id` bigint NOT NULL COMMENT '财务账户ID',
  `account_name` varchar(100) NOT NULL COMMENT '账户名称(冗余)',
  `batch_no` varchar(32) DEFAULT NULL COMMENT '批次号(可为空)',
  `transaction_type` varchar(32) NOT NULL COMMENT '交易类型(fiscal_allocation/subsidy_distribution/benefit_recovery)',
  `amount` decimal(16,2) NOT NULL COMMENT '交易金额(正=收入,负=支出)',
  `balance` decimal(16,2) NOT NULL COMMENT '交易后余额',
  `transaction_time` datetime NOT NULL COMMENT '交易时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志(0正常 2删除)',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_fat_account_id` (`account_id`),
  KEY `idx_fat_transaction_time` (`transaction_time`),
  KEY `idx_fat_batch_no` (`batch_no`)
)  COMMENT='财务账户明细表';

-- 财政拨款权限（挂在「财务账户」菜单下，请将 @parentId 替换为实际菜单ID）
-- SET @parentId = (SELECT menu_id FROM sys_menu WHERE perms = 'shebao:finance:account:list' LIMIT 1);
-- INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
-- ('财政拨款', @parentId, 1, '', '', 1, 0, 'F', '0', '0', 'shebao:finance:account:allocate', '#', 'admin', sysdate(), '财务账户-财政拨款');
