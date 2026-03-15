-- 预到龄通知闭环专项测试数据（第二轮）
-- 测试通知月份: 2030-03
-- 对应到龄月份: 2030-06

SET NAMES utf8mb4;

DELETE FROM shebao_subsidy_person
WHERE id_card_no IN (
  '130101197006050061',
  '130101197006180072',
  '130101197006200083',
  '130101197006250094',
  '130101197007010105'
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
('第二轮正样本甲', '1', '130101197006050061', '1970-06-05', '廊坊测试户籍6', '廊坊测试地址6号', '13900030001', '1', NULL, '1', 1, 1, 'NT-203003-001', '0', 'approved', '0', 'notice-flow-test-2', NOW(), 'notice-flow-test-2', NOW(), 'NF2-POS-01'),
('第二轮正样本乙', '2', '130101197006180072', '1970-06-18', '廊坊测试户籍7', '廊坊测试地址7号', '13900030002', '1', NULL, '1', 1, 1, 'NT-203003-002', '0', 'approved', '0', 'notice-flow-test-2', NOW(), 'notice-flow-test-2', NOW(), 'NF2-POS-02'),
('第二轮反样本-死亡', '1', '130101197006200083', '1970-06-20', '廊坊测试户籍8', '廊坊测试地址8号', '13900030003', '0', '2029-11-18', '1', 1, 1, 'NT-203003-003', '0', 'approved', '0', 'notice-flow-test-2', NOW(), 'notice-flow-test-2', NOW(), 'NF2-NEG-01'),
('第二轮反样本-未审核', '2', '130101197006250094', '1970-06-25', '廊坊测试户籍9', '廊坊测试地址9号', '13900030004', '1', NULL, '1', 1, 1, 'NT-203003-004', '0', 'pending_review', '0', 'notice-flow-test-2', NOW(), 'notice-flow-test-2', NOW(), 'NF2-NEG-02'),
('第二轮反样本-超窗', '1', '130101197007010105', '1970-07-01', '廊坊测试户籍10', '廊坊测试地址10号', '13900030005', '1', NULL, '1', 1, 1, 'NT-203003-005', '0', 'approved', '0', 'notice-flow-test-2', NOW(), 'notice-flow-test-2', NOW(), 'NF2-NEG-03');
