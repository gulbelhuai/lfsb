-- 存量修复：已核定通过人员的 person_status 置为「享受」(1)
-- 登记默认未享受(0)；核定复核通过后应为享受(1)
-- 执行前请备份；可重复执行

UPDATE shebao_subsidy_person sp
INNER JOIN (
    SELECT DISTINCT subsidy_person_id AS pid
    FROM benefit_determination
    WHERE del_flag = '0'
      AND approval_status = 'approved'
      AND subsidy_person_id IS NOT NULL
) d ON d.pid = sp.id
SET sp.person_status = '1',
    sp.update_time = NOW()
WHERE sp.del_flag = '0'
  AND (sp.person_status IS NULL OR sp.person_status = '' OR sp.person_status = '0');

-- 仅有身份证关联、无 subsidy_person_id 的历史核定（兜底）
UPDATE shebao_subsidy_person sp
INNER JOIN (
    SELECT DISTINCT id_card_no
    FROM benefit_determination
    WHERE del_flag = '0'
      AND approval_status = 'approved'
      AND (subsidy_person_id IS NULL OR subsidy_person_id = 0)
      AND id_card_no IS NOT NULL
      AND id_card_no != ''
) d ON d.id_card_no = sp.id_card_no
SET sp.person_status = '1',
    sp.update_time = NOW()
WHERE sp.del_flag = '0'
  AND (sp.person_status IS NULL OR sp.person_status = '' OR sp.person_status = '0');
