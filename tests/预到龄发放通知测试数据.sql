-- 预到龄发放通知专项测试数据
-- 适用日期：2026-03-15
-- 用途：为“预到龄发放通知”功能提供 2 条正样本、4 条反样本

SET NAMES utf8mb4;

-- 1. 清理历史样本，保证脚本可重复执行
DELETE FROM shebao_subsidy_person
WHERE id_card_no IN (
  '130101196509109871',
  '130101196503017246',
  '130101196506208433',
  '130101196506256228',
  '130101196507108415',
  '130101196412308430'
);

-- 2. 插入专项样本
INSERT INTO shebao_subsidy_person (
  name,
  gender,
  id_card_no,
  birthday,
  household_registration,
  home_address,
  phone,
  is_alive,
  death_date,
  is_village_coop_member,
  street_office_id,
  village_committee_id,
  user_code,
  status,
  approval_status,
  del_flag,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
) VALUES
-- BN-POS-01: 默认空月份查询命中；noticeMonth=2026-06 不命中
('张预到龄默认命中', '1', '130101196509109871', '1965-09-10', '廊坊市安次区银河南路街道第一村', '第一村测试地址1号', '13900010001', '1', NULL, '1', 1, 1, '901-001-0001', '0', 'approved', '0', 'notice-test', NOW(), 'notice-test', NOW(), 'BN-POS-01'),

-- BN-POS-02: noticeMonth=2026-06 命中；默认空月份查询不命中
('李预到龄指定月份命中', '2', '130101196503017246', '1965-03-01', '廊坊市安次区银河南路街道第一村', '第一村测试地址2号', '13900010002', '1', NULL, '1', 1, 1, '901-001-0002', '0', 'approved', '0', 'notice-test', NOW(), 'notice-test', NOW(), 'BN-POS-02'),

-- BN-NEG-01: 已死亡，不应命中
('王已死亡样本', '1', '130101196506208433', '1965-06-20', '廊坊市安次区银河南路街道第一村', '第一村测试地址3号', '13900010003', '0', '2026-01-10', '1', 1, 1, '901-001-0003', '0', 'approved', '0', 'notice-test', NOW(), 'notice-test', NOW(), 'BN-NEG-01'),

-- BN-NEG-02: 未审批通过，不应命中
('赵未审批样本', '2', '130101196506256228', '1965-06-25', '廊坊市安次区银河南路街道第一村', '第一村测试地址4号', '13900010004', '1', NULL, '1', 1, 1, '901-001-0004', '0', 'pending_review', '0', 'notice-test', NOW(), 'notice-test', NOW(), 'BN-NEG-02'),

-- BN-NEG-03: 逻辑删除，不应命中
('孙已删除样本', '1', '130101196507108415', '1965-07-10', '廊坊市安次区银河南路街道第一村', '第一村测试地址5号', '13900010005', '1', NULL, '1', 1, 1, '901-001-0005', '0', 'approved', '2', 'notice-test', NOW(), 'notice-test', NOW(), 'BN-NEG-03'),

-- BN-NEG-04: 超出窗口，不应命中
('周超窗样本', '1', '130101196412308430', '1964-12-30', '廊坊市安次区银河南路街道第一村', '第一村测试地址6号', '13900010006', '1', NULL, '1', 1, 1, '901-001-0006', '0', 'approved', '0', 'notice-test', NOW(), 'notice-test', NOW(), 'BN-NEG-04');

-- 3. 核对样本
SELECT id, name, id_card_no, birthday, is_alive, approval_status, del_flag, remark
FROM shebao_subsidy_person
WHERE id_card_no IN (
  '130101196509109871',
  '130101196503017246',
  '130101196506208433',
  '130101196506256228',
  '130101196507108415',
  '130101196412308430'
)
ORDER BY remark;
