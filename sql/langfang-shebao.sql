create table shebao_demo
(
    id         bigint auto_increment comment 'id' primary key,
    name       varchar(30)               not null comment '用户账号',
    status          char         default '0'  null comment '账号状态（0正常 1停用）',
    del_flag        char         default '0'  null comment '删除标志（0代表存在 2代表删除）',
    create_by       varchar(64)  default ''   null comment '创建者',
    create_time     datetime     default current_timestamp comment '创建时间',
    update_by       varchar(64)  default ''   null comment '更新者',
    update_time     datetime     default current_timestamp on update current_timestamp comment '更新时间',
    remark          varchar(500)              null comment '备注'
)
    comment 'demo表';

-- 行政区划表（通用表，支持省市县乡村多级）
create table shebao_administrative_division
(
    id              bigint auto_increment comment 'id' primary key,
    division_code   varchar(50)           not null comment '行政区划编码',
    division_name   varchar(100)          not null comment '行政区划名称',
    parent_code     varchar(50)                    null comment '父级编码',
    division_level  tinyint               not null comment '行政级别（1省 2市 3县 4乡镇 5村）',
    full_code       varchar(500)                   null comment '全路径编码，用/分隔',
    full_name       varchar(500)                   null comment '全路径名称，用/分隔',
    contact_person  varchar(50)                    null comment '联系人',
    contact_phone   varchar(20)                    null comment '联系电话',
    address         varchar(200)                   null comment '详细地址',
    sort_order      int              default 0     null comment '排序',
    status          char             default '0'   null comment '状态（0正常 1停用）',
    del_flag        char             default '0'   null comment '删除标志（0代表存在 2代表删除）',
    create_by       varchar(64)      default ''    null comment '创建者',
    create_time     datetime         default current_timestamp comment '创建时间',
    update_by       varchar(64)      default ''    null comment '更新者',
    update_time     datetime         default current_timestamp on update current_timestamp comment '更新时间',
    remark          varchar(500)                   null comment '备注',
    unique key uk_division_code (division_code),
    index idx_parent_code (parent_code),
    index idx_division_level (division_level),
    index idx_full_code (full_code)
)
    comment '行政区划表';

-- 插入一些示例数据
insert into shebao_administrative_division (division_code, division_name, parent_code, division_level, full_code, full_name, sort_order) values
('130000', '河北省', null, 1, '130000', '河北省', 1),
('131000', '廊坊市', '130000', 2, '130000/131000', '河北省/廊坊市', 1),
('131001', '安次区', '131000', 3, '130000/131000/131001', '河北省/廊坊市/安次区', 1),
('131001001', '银河南路街道', '131001', 4, '130000/131000/131001/131001001', '河北省/廊坊市/安次区/银河南路街道', 1);

-- 村级单位管理表（简化版，主要存储村级特有信息）
create table shebao_village
(
    id              bigint auto_increment comment 'id' primary key,
    village_code    varchar(50)           not null comment '村编码（关联行政区划表）',
    village_name    varchar(100)          not null comment '村名',
    parent_code     varchar(50)           not null comment '父级编码（乡镇编码）',
    full_code       varchar(500)                   null comment '全路径编码，用/分隔',
    full_name       varchar(500)                   null comment '全路径名称，用/分隔',
    contact_person  varchar(50)                    null comment '联系人',
    contact_phone   varchar(20)                    null comment '联系电话',
    address         varchar(200)                   null comment '详细地址',
    sort_order      int              default 0     null comment '排序',
    status          char             default '0'   null comment '状态（0正常 1停用）',
    del_flag        char             default '0'   null comment '删除标志（0代表存在 2代表删除）',
    create_by       varchar(64)      default ''    null comment '创建者',
    create_time     datetime         default current_timestamp comment '创建时间',
    update_by       varchar(64)      default ''    null comment '更新者',
    update_time     datetime         default current_timestamp on update current_timestamp comment '更新时间',
    remark          varchar(500)                   null comment '备注',
    unique key uk_village_code (village_code),
    index idx_parent_code (parent_code),
    foreign key fk_village_division (village_code) references shebao_administrative_division(division_code)
)
    comment '村级单位表';

-- ----------------------------
-- 被补贴人信息管理相关表结构
-- ----------------------------

-- 被补贴人基础信息表
create table shebao_subsidy_person
(
    id                    bigint auto_increment comment '用户编号（系统生成，自增主键）' primary key,
    name                  varchar(20)          not null comment '姓名',
    gender                char                 not null comment '性别（1男 2女）',
    id_card_no            varchar(18)          not null comment '身份证号',
    birthday              date                 not null comment '生日',
    native_place          varchar(100)         not null comment '籍贯',
    phone                 varchar(20)          not null comment '联系电话',
    home_address          varchar(200)         not null comment '家庭住址',
    village_code          varchar(50)          not null comment '所属居委会/村委会编码',
    village_name          varchar(100)         not null comment '所属居委会/村委会名称',
    household_no          varchar(50)                   null comment '户别编号',
    has_employee_pension  char                 not null comment '是否已领取职工养老保险待遇（0否 1是）',
    status                char        default '0'       null comment '状态（0正常 1停用）',
    del_flag              char        default '0'       null comment '删除标志（0代表存在 2代表删除）',
    create_by             varchar(64) default ''        null comment '创建者',
    create_time           datetime    default current_timestamp comment '创建时间',
    update_by             varchar(64) default ''        null comment '更新者',
    update_time           datetime    default current_timestamp on update current_timestamp comment '更新时间',
    remark                varchar(500)                  null comment '备注',
    unique key uk_id_card_no (id_card_no),
    index idx_village_code (village_code),
    index idx_name (name),
    index idx_phone (phone)
)
    comment '被补贴人基础信息表';

-- 失地居民信息表
create table shebao_land_loss_resident
(
    id                          bigint auto_increment comment '主键ID' primary key,
    subsidy_person_id           bigint               not null comment '被补贴人ID',
    land_requisition_time       date                          null comment '征地时间',
    compensation_complete_time  date                          null comment '完成补偿时间',
    recognition_time            date                          null comment '认定为失地居民时间',
    is_under_16                 char        default '0'       null comment '是否16周岁以下人员（0否 1是）',
    subsidy_amount              decimal(10,2) default 0.00    null comment '补贴金额',
    distribution_status         char        default '0'       null comment '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
    status                      char        default '0'       null comment '状态（0正常 1停用）',
    del_flag                    char        default '0'       null comment '删除标志（0代表存在 2代表删除）',
    create_by                   varchar(64) default ''        null comment '创建者',
    create_time                 datetime    default current_timestamp comment '创建时间',
    update_by                   varchar(64) default ''        null comment '更新者',
    update_time                 datetime    default current_timestamp on update current_timestamp comment '更新时间',
    remark                      varchar(500)                  null comment '备注',
    index idx_subsidy_person_id (subsidy_person_id),
    index idx_recognition_time (recognition_time),
    foreign key fk_land_loss_person (subsidy_person_id) references shebao_subsidy_person(id)
)
    comment '失地居民信息表';

-- 被征地参保补贴账户表
create table shebao_expropriatee_subsidy
(
    id                           bigint auto_increment comment '主键ID' primary key,
    subsidy_person_id            bigint               not null comment '被补贴人ID',
    land_requisition_batch       varchar(100)                  null comment '征地批次',
    base_date                    date                          null comment '基准日',
    employee_pension_months      int         default 0         null comment '职工身份缴纳职工养老月数',
    flexible_employment_months   int         default 0         null comment '灵活就业身份缴纳职工养老保险月数',
    difficulty_subsidy_years     decimal(5,2) default 0.00     null comment '已领取困难人员社会保险补贴年限',
    age_at_base_date            int                           null comment '截至基准日的年龄',
    subsidy_years               decimal(5,2) default 0.00     null comment '被征地参保补贴年限',
    subsidy_amount              decimal(10,2) default 0.00    null comment '补贴金额',
    join_urban_rural_insurance  char        default '0'       null comment '选择参加城乡居保（0否 1是）',
    join_employee_pension       char        default '0'       null comment '选择参加职工养老（0否 1是）',
    distribution_status         char        default '0'       null comment '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
    status                      char        default '0'       null comment '状态（0正常 1停用）',
    del_flag                    char        default '0'       null comment '删除标志（0代表存在 2代表删除）',
    create_by                   varchar(64) default ''        null comment '创建者',
    create_time                 datetime    default current_timestamp comment '创建时间',
    update_by                   varchar(64) default ''        null comment '更新者',
    update_time                 datetime    default current_timestamp on update current_timestamp comment '更新时间',
    remark                      varchar(500)                  null comment '备注',
    index idx_subsidy_person_id (subsidy_person_id),
    index idx_batch (land_requisition_batch),
    index idx_base_date (base_date),
    foreign key fk_expropriatee_person (subsidy_person_id) references shebao_subsidy_person(id)
)
    comment '被征地参保补贴账户表';

-- 拆迁居民信息表
create table shebao_demolition_resident
(
    id                  bigint auto_increment comment '主键ID' primary key,
    subsidy_person_id   bigint               not null comment '被补贴人ID',
    demolition_reason   varchar(200)                  null comment '拆迁事由',
    demolition_time     date                          null comment '拆迁时间',
    recognition_time    date                          null comment '认定为拆迁居民时间',
    subsidy_amount      decimal(10,2) default 0.00    null comment '补贴金额',
    distribution_status char        default '0'       null comment '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
    status              char        default '0'       null comment '状态（0正常 1停用）',
    del_flag            char        default '0'       null comment '删除标志（0代表存在 2代表删除）',
    create_by           varchar(64) default ''        null comment '创建者',
    create_time         datetime    default current_timestamp comment '创建时间',
    update_by           varchar(64) default ''        null comment '更新者',
    update_time         datetime    default current_timestamp on update current_timestamp comment '更新时间',
    remark              varchar(500)                  null comment '备注',
    index idx_subsidy_person_id (subsidy_person_id),
    index idx_demolition_time (demolition_time),
    index idx_recognition_time (recognition_time),
    foreign key fk_demolition_person (subsidy_person_id) references shebao_subsidy_person(id)
)
    comment '拆迁居民信息表';

-- 村干部信息表
create table shebao_village_official
(
    id                   bigint auto_increment comment '主键ID' primary key,
    subsidy_person_id    bigint               not null comment '被补贴人ID',
    total_service_years  decimal(5,2) default 0.00     null comment '累计任职年限',
    has_violation        char        default '0'       null comment '是否违法乱纪或判刑（0否 1是）',
    subsidy_amount       decimal(10,2) default 0.00    null comment '补贴金额',
    distribution_status  char        default '0'       null comment '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
    status               char        default '0'       null comment '状态（0正常 1停用）',
    del_flag             char        default '0'       null comment '删除标志（0代表存在 2代表删除）',
    create_by            varchar(64) default ''        null comment '创建者',
    create_time          datetime    default current_timestamp comment '创建时间',
    update_by            varchar(64) default ''        null comment '更新者',
    update_time          datetime    default current_timestamp on update current_timestamp comment '更新时间',
    remark               varchar(500)                  null comment '备注',
    index idx_subsidy_person_id (subsidy_person_id),
    foreign key fk_village_official_person (subsidy_person_id) references shebao_subsidy_person(id)
)
    comment '村干部信息表';

-- 村干部任职信息表
create table shebao_village_official_position
(
    id                   bigint auto_increment comment '主键ID' primary key,
    village_official_id  bigint               not null comment '村干部信息ID',
    position             char                 not null comment '任职职位（1书记或主任 2书记兼主任 3村"两委"其他成员）',
    start_date           date                 not null comment '上任时间',
    end_date             date                          null comment '卸任时间',
    status               char        default '0'       null comment '状态（0正常 1停用）',
    del_flag             char        default '0'       null comment '删除标志（0代表存在 2代表删除）',
    create_by            varchar(64) default ''        null comment '创建者',
    create_time          datetime    default current_timestamp comment '创建时间',
    update_by            varchar(64) default ''        null comment '更新者',
    update_time          datetime    default current_timestamp on update current_timestamp comment '更新时间',
    remark               varchar(500)                  null comment '备注',
    index idx_village_official_id (village_official_id),
    index idx_position (position),
    index idx_start_date (start_date),
    foreign key fk_position_official (village_official_id) references shebao_village_official(id)
)
    comment '村干部任职信息表';

-- 补贴发放记录表
create table shebao_subsidy_distribution
(
    id                    bigint auto_increment comment '主键ID' primary key,
    subsidy_person_id     bigint               not null comment '被补贴人ID（关联shebao_subsidy_person.id）',
    subsidy_type          char(1)              not null comment '补贴类型（1失地居民补贴 2被征地居民补贴 3拆迁居民补贴 4村干部补贴）',
    subsidy_record_id     bigint               not null comment '补贴身份记录ID（关联具体补贴类型表的ID）',
    distribution_amount   decimal(10,2)        not null comment '发放金额',
    distribution_date     date                          null comment '发放日期',
    distribution_status   char(1)        default '1'   not null comment '发放状态（0未发放 1待审核 2待发放 3已驳回 4已发放）',
    review_remark         varchar(500)                  null comment '审核说明',
    status                char(1)        default '0'   null comment '状态（0正常 1停用）',
    del_flag              char(1)        default '0'   null comment '删除标志（0代表存在 2代表删除）',
    create_by             varchar(64)    default ''    null comment '创建者',
    create_time           datetime       default current_timestamp comment '创建时间',
    update_by             varchar(64)    default ''    null comment '更新者',
    update_time           datetime       default current_timestamp on update current_timestamp comment '更新时间',
    remark                varchar(500)                  null comment '备注',
    index idx_subsidy_person_id (subsidy_person_id),
    index idx_subsidy_type (subsidy_type),
    index idx_distribution_status (distribution_status),
    index idx_distribution_date (distribution_date),
    foreign key fk_distribution_person (subsidy_person_id) references shebao_subsidy_person(id)
)
    comment '补贴发放记录表';

-- 发放审核记录表
create table shebao_distribution_review
(
    id                    bigint auto_increment comment '主键ID' primary key,
    distribution_id       bigint               not null comment '发放记录ID（关联shebao_subsidy_distribution.id）',
    operation_type        char(1)              not null comment '操作类型（1提交审核 2审核通过 3审核驳回 4发放 5重新提交）',
    operation_remark      varchar(500)                  null comment '操作说明',
    operator_name         varchar(64)          not null comment '操作人',
    operation_time        datetime             not null comment '操作时间',
    status                char(1)        default '0'   null comment '状态（0正常 1停用）',
    del_flag              char(1)        default '0'   null comment '删除标志（0代表存在 2代表删除）',
    create_by             varchar(64)    default ''    null comment '创建者',
    create_time           datetime       default current_timestamp comment '创建时间',
    update_by             varchar(64)    default ''    null comment '更新者',
    update_time           datetime       default current_timestamp on update current_timestamp comment '更新时间',
    remark                varchar(500)                  null comment '备注',
    index idx_distribution_id (distribution_id),
    index idx_operation_type (operation_type),
    index idx_operation_time (operation_time),
    foreign key fk_review_distribution (distribution_id) references shebao_subsidy_distribution(id)
)
    comment '发放审核记录表';