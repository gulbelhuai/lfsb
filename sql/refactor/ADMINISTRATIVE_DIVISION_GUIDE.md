# 行政区划表说明

## 📋 表名：`shebao_administrative_division`

### 🎯 用途

该表用于管理多级行政区划结构（省/市/县/乡镇/村），提供完整的行政区划树形结构管理。

### ⚠️ 重要说明

**当前系统实际使用的是以下两个简化表：**
1. `shebao_street_office` - 街道办事处表
2. `shebao_village_committee` - 村委会表

这两个表已经足够满足当前业务需求，`shebao_administrative_division` 表是**可选的增强功能表**。

### 📊 表结构

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| division_code | VARCHAR(50) | 行政区划编码（唯一） |
| division_name | VARCHAR(100) | 行政区划名称 |
| parent_code | VARCHAR(50) | 父级编码 |
| division_level | TINYINT | 行政级别（1省 2市 3县 4乡镇 5村） |
| full_code | VARCHAR(500) | 全路径编码，用/分隔 |
| full_name | VARCHAR(500) | 全路径名称，用/分隔 |
| contact_person | VARCHAR(50) | 联系人 |
| contact_phone | VARCHAR(20) | 联系电话 |
| address | VARCHAR(200) | 详细地址 |
| sort_order | INT | 排序 |
| status | CHAR(1) | 状态（0正常 1停用） |
| del_flag | CHAR(1) | 删除标志 |
| create_by | VARCHAR(64) | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR(64) | 更新者 |
| update_time | DATETIME | 更新时间 |
| remark | VARCHAR(500) | 备注 |

### 🔍 与现有表的关系

```
行政区划层级结构：
河北省（省级）
  └── 廊坊市（市级）
        ├── 安次区（县级）
        │     ├── 银河南路街道（街道办）→ shebao_street_office
        │     │     └── XX村委会 → shebao_village_committee
        │     └── 光明西道街道（街道办）→ shebao_street_office
        │           └── XX村委会 → shebao_village_committee
        └── 广阳区（县级）
              └── ...
```

### 🚀 创建步骤

1. **执行 SQL 脚本：**
   ```bash
   mysql -h www.htmisoft.net -P 36522 -u root -p lfpm < sql/refactor/05_create_administrative_division.sql
   ```

2. **输入密码：** `infini_rag_flow`

3. **验证结果：**
   ```sql
   -- 查看表结构
   DESC shebao_administrative_division;

   -- 查看数据
   SELECT * FROM shebao_administrative_division ORDER BY full_code;
   ```

### 💡 使用建议

#### 如果**不需要**完整的行政区划树：
- ✅ 继续使用现有的 `shebao_street_office` 和 `shebao_village_committee` 表
- ✅ 这两个表已经满足业务需求，结构更简单

#### 如果**需要**完整的行政区划树：
- ✅ 创建 `shebao_administrative_division` 表
- ✅ 可以通过外键关联 `shebao_street_office.street_code` 到 `shebao_administrative_division.division_code`
- ✅ 可以通过外键关联 `shebao_village_committee.village_code` 到 `shebao_administrative_division.division_code`

### 📝 示例数据

脚本中已包含以下示例数据：

**省级（1个）：**
- 河北省

**市级（1个）：**
- 廊坊市

**县级（3个）：**
- 安次区
- 广阳区
- 开发区

**街道办（4个）：**
- 银河南路街道
- 光明西道街道
- 解放道街道
- 新开路街道

### ⚙️ 扩展建议

如果决定使用完整的行政区划表，可以考虑以下扩展：

1. **添加村委会数据**
   ```sql
   INSERT INTO shebao_administrative_division
   (division_code, division_name, parent_code, division_level, full_code, full_name, sort_order)
   VALUES
   ('131001001001', 'XX村委会', '131001001', 5,
    '130000/131000/131001/131001001/131001001001',
    '河北省/廊坊市/安次区/银河南路街道/XX村委会', 1);
   ```

2. **同步现有的街道办和村委会数据**
   ```sql
   -- 从 shebao_street_office 同步
   INSERT INTO shebao_administrative_division
   (division_code, division_name, parent_code, division_level, full_code, full_name, sort_order, status)
   SELECT
       street_code,
       street_name,
       '131001', -- 假设属于安次区
       4, -- 街道办级别
       CONCAT('130000/131000/131001/', street_code),
       CONCAT('河北省/廊坊市/安次区/', street_name),
       sort_order,
       status
   FROM shebao_street_office;
   ```

### 🔗 相关文件

- SQL 脚本：`sql/refactor/05_create_administrative_division.sql`
- 原始设计：`sql/langfang-shebao.sql`（第16-48行）
- 当前街道办表：`shebao_street_office`
- 当前村委会表：`shebao_village_committee`

### ❓ 常见问题

**Q: 是否必须创建这个表？**
A: 不是必须的。当前系统使用 `shebao_street_office` 和 `shebao_village_committee` 已经足够。

**Q: 什么情况下需要创建？**
A: 如果需要管理完整的省/市/县/乡镇/村五级行政区划，或者需要跨区域的行政区划查询和统计。

**Q: 创建后会影响现有功能吗？**
A: 不会。这是一个独立的表，不影响现有的业务逻辑。

**Q: 如何在后端使用？**
A: 如果需要使用，可以创建对应的 Entity、Mapper、Service 和 Controller，参考现有的 `StreetOffice` 和 `VillageCommittee` 实现。

---

**创建日期：** 2025-01-19
**最后更新：** 2025-01-19
