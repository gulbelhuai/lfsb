-- 若 service_years 曾为 int，改为两位小数（与 Java BigDecimal 一致）
-- 已建为 decimal 的库无需执行
ALTER TABLE shebao_village_official_position
    MODIFY COLUMN service_years decimal(14,2) DEFAULT NULL COMMENT '任职年限(整年+余天/365)';
