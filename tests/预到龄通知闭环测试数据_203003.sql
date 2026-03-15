-- 预到龄通知闭环专项测试数据（2030-03）
-- 测试通知月份: 2030-03
-- 对应到龄月份: 2030-06
-- 正样本口径：失地居民 / 拆迁居民 / 村干部
-- 反样本口径：死亡 / 未复核 / 超出窗口

SET NAMES utf8mb4;

-- 先按本专项身份证清理子表数据，再清理主表，保证脚本可重复执行
DELETE vop
FROM shebao_village_official_position vop
JOIN shebao_village_official vo ON vo.id = vop.village_official_id
JOIN shebao_subsidy_person sp ON sp.id = vo.subsidy_person_id
WHERE sp.id_card_no IN (
  '130101197006050061',
  '130101197006180077',
  '13010119700625008X',
  '130101197006200082',
  '130101197006280094',
  '130101197007010109'
);

DELETE vo
FROM shebao_village_official vo
JOIN shebao_subsidy_person sp ON sp.id = vo.subsidy_person_id
WHERE sp.id_card_no IN (
  '130101197006050061',
  '130101197006180077',
  '13010119700625008X',
  '130101197006200082',
  '130101197006280094',
  '130101197007010109'
);

DELETE dr
FROM shebao_demolition_resident dr
JOIN shebao_subsidy_person sp ON sp.id = dr.subsidy_person_id
WHERE sp.id_card_no IN (
  '130101197006050061',
  '130101197006180077',
  '13010119700625008X',
  '130101197006200082',
  '130101197006280094',
  '130101197007010109'
);

DELETE ll
FROM shebao_land_loss_resident ll
JOIN shebao_subsidy_person sp ON sp.id = ll.subsidy_person_id
WHERE sp.id_card_no IN (
  '130101197006050061',
  '130101197006180077',
  '13010119700625008X',
  '130101197006200082',
  '130101197006280094',
  '130101197007010109'
);

DELETE FROM shebao_subsidy_person
WHERE id_card_no IN (
  '130101197006050061',
  '130101197006180077',
  '13010119700625008X',
  '130101197006200082',
  '130101197006280094',
  '130101197007010109'
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
('脚本正样本-失地', '1', '130101197006050061', '1970-06-05', '廊坊测试户籍-失地', '廊坊测试地址-失地', '13900030001', '1', NULL, '1', 1, 1, 'NT-203003-LL-01', '0', 'approved', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), 'NF3-POS-LL'),
('脚本正样本-拆迁', '2', '130101197006180077', '1970-06-18', '廊坊测试户籍-拆迁', '廊坊测试地址-拆迁', '13900030002', '1', NULL, '1', 1, 1, 'NT-203003-DR-01', '0', 'approved', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), 'NF3-POS-DR'),
('脚本正样本-村干', '1', '13010119700625008X', '1970-06-25', '廊坊测试户籍-村干', '廊坊测试地址-村干', '13900030003', '1', NULL, '1', 1, 1, 'NT-203003-VO-01', '0', 'approved', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), 'NF3-POS-VO'),
('脚本反样本-死亡', '1', '130101197006200082', '1970-06-20', '廊坊测试户籍-死亡', '廊坊测试地址-死亡', '13900030004', '0', '2029-11-18', '1', 1, 1, 'NT-203003-NEG-01', '0', 'approved', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), 'NF3-NEG-DEAD'),
('脚本反样本-未复核', '2', '130101197006280094', '1970-06-28', '廊坊测试户籍-未复核', '廊坊测试地址-未复核', '13900030005', '1', NULL, '1', 1, 1, 'NT-203003-NEG-02', '0', 'pending_review', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), 'NF3-NEG-PENDING'),
('脚本反样本-超窗', '1', '130101197007010109', '1970-07-01', '廊坊测试户籍-超窗', '廊坊测试地址-超窗', '13900030006', '1', NULL, '1', 1, 1, 'NT-203003-NEG-03', '0', 'approved', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), 'NF3-NEG-WINDOW');

INSERT INTO shebao_land_loss_resident (
  subsidy_person_id,
  land_requisition_time,
  compensation_complete_time,
  recognition_time,
  del_flag,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
SELECT id, '2024-03-01', '2024-03-15', '2024-03-20', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), '2030-03失地正样本'
FROM shebao_subsidy_person
WHERE id_card_no = '130101197006050061';

INSERT INTO shebao_demolition_resident (
  subsidy_person_id,
  demolition_reason,
  demolition_time,
  recognition_time,
  subsidy_amount,
  distribution_status,
  status,
  del_flag,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
SELECT id, '2030-03专项拆迁正样本', '2024-06-18', '2024-06-20', 0.00, '0', '0', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), '2030-03拆迁正样本'
FROM shebao_subsidy_person
WHERE id_card_no = '130101197006180077';

INSERT INTO shebao_village_official (
  subsidy_person_id,
  total_service_years,
  has_violation,
  subsidy_amount,
  distribution_status,
  status,
  del_flag,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
SELECT id, 8.00, '0', 0.00, '0', '0', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), '2030-03村干正样本'
FROM shebao_subsidy_person
WHERE id_card_no = '13010119700625008X';

INSERT INTO shebao_village_official_position (
  village_official_id,
  position,
  start_date,
  end_date,
  status,
  del_flag,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
SELECT vo.id, '1', '2015-01-01', '2029-12-31', '0', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), '2030-03村干任职样本'
FROM shebao_village_official vo
JOIN shebao_subsidy_person sp ON sp.id = vo.subsidy_person_id
WHERE sp.id_card_no = '13010119700625008X';

INSERT INTO shebao_land_loss_resident (
  subsidy_person_id,
  land_requisition_time,
  compensation_complete_time,
  recognition_time,
  del_flag,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
SELECT id, '2024-02-01', '2024-02-15', '2024-02-20', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), '2030-03死亡反样本'
FROM shebao_subsidy_person
WHERE id_card_no = '130101197006200082';

INSERT INTO shebao_demolition_resident (
  subsidy_person_id,
  demolition_reason,
  demolition_time,
  recognition_time,
  subsidy_amount,
  distribution_status,
  status,
  del_flag,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
SELECT id, '2030-03专项未复核反样本', '2024-05-18', '2024-05-20', 0.00, '0', '0', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), '2030-03未复核反样本'
FROM shebao_subsidy_person
WHERE id_card_no = '130101197006280094';

INSERT INTO shebao_village_official (
  subsidy_person_id,
  total_service_years,
  has_violation,
  subsidy_amount,
  distribution_status,
  status,
  del_flag,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
SELECT id, 5.00, '0', 0.00, '0', '0', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), '2030-03超窗反样本'
FROM shebao_subsidy_person
WHERE id_card_no = '130101197007010109';

INSERT INTO shebao_village_official_position (
  village_official_id,
  position,
  start_date,
  end_date,
  status,
  del_flag,
  create_by,
  create_time,
  update_by,
  update_time,
  remark
)
SELECT vo.id, '3', '2018-01-01', '2029-12-31', '0', '0', 'notice-flow-sql', NOW(), 'notice-flow-sql', NOW(), '2030-03超窗任职反样本'
FROM shebao_village_official vo
JOIN shebao_subsidy_person sp ON sp.id = vo.subsidy_person_id
WHERE sp.id_card_no = '130101197007010109';
