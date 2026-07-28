package com.ruoyi.shebao.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 历史数据导入批次 shebao_historical_import_batch
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("shebao_historical_import_batch")
public class HistoricalImportBatch extends BaseDomain
{
    private Long id;

    /** 补贴类型 */
    private String subsidyType;

    /** 导入文件名（原始展示名） */
    private String fileName;

    /** 导入源文件路径（磁盘别名） */
    private String sourceFilePath;

    /** 导入行数 */
    private Integer totalRows;

    /** 成功行数 */
    private Integer successRows;

    /** 失败行数 */
    private Integer failureRows;

    /** 失败记录文件路径 */
    private String failureFilePath;

    /** 导入状态 */
    private String importStatus;
}
