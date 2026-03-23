alter table shebao_land_loss_resident add column land_requisition_batch varchar(50) comment '征地批次'
    , add column village_street varchar(50) comment '所在村街'
    , add column is_village_coop_member varchar(1) comment '是否村合作经济组织成员';
alter table shebao_subsidy_person
    add column subsidy_status varchar(1) comment '参保状态 0=在保,1=终止',
    add column person_status varchar(1) comment '人员状态 0=未享受,1=享受';