-- 预到龄通知闭环专项测试数据
-- 测试通知月份: 2030-02
-- 对应到龄月份: 2030-05

SET NAMES utf8mb4;

DELETE FROM shebao_subsidy_person
WHERE id_card_no IN (
  '130101197005050011',
  '130101197005180022',
  '130101197005200033',
  '130101197005250044',
  '130101197006010055'
);

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
('闭环正样本甲', '1', '130101197005050011', '1970-05-05', '廊坊测试户籍1', '廊坊测试地址1号', '13900020001', '1', NULL, '1', 1, 1, 'NT-203002-001', '0', 'approved', '0', 'notice-flow-test', NOW(), 'notice-flow-test', NOW(), 'NF-POS-01'),
('闭环正样本乙', '2', '130101197005180022', '1970-05-18', '廊坊测试户籍2', '廊坊测试地址2号', '13900020002', '1', NULL, '1', 1, 1, 'NT-203002-002', '0', 'approved', '0', 'notice-flow-test', NOW(), 'notice-flow-test', NOW(), 'NF-POS-02'),
('闭环反样本-死亡', '1', '130101197005200033', '1970-05-20', '廊坊测试户籍3', '廊坊测试地址3号', '13900020003', '0', '2029-12-01', '1', 1, 1, 'NT-203002-003', '0', 'approved', '0', 'notice-flow-test', NOW(), 'notice-flow-test', NOW(), 'NF-NEG-01'),
('闭环反样本-未审核', '2', '130101197005250044', '1970-05-25', '廊坊测试户籍4', '廊坊测试地址4号', '13900020004', '1', NULL, '1', 1, 1, 'NT-203002-004', '0', 'pending_review', '0', 'notice-flow-test', NOW(), 'notice-flow-test', NOW(), 'NF-NEG-02'),
('闭环反样本-超窗', '1', '130101197006010055', '1970-06-01', '廊坊测试户籍5', '廊坊测试地址5号', '13900020005', '1', NULL, '1', 1, 1, 'NT-203002-005', '0', 'approved', '0', 'notice-flow-test', NOW(), 'notice-flow-test', NOW(), 'NF-NEG-03');
