package com.ruoyi.shebao.dto.historicalimport;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoricalImportBatchListResp
{
    private Long id;
    private String subsidyType;
    private String subsidyTypeLabel;
    private String fileName;
    private Integer totalRows;
    private Integer successRows;
    private Integer failureRows;
    private String importStatus;
    private Boolean hasFailureFile;

    /** 失败记录文件路径（库内存储） */
    private String failureFilePath;

    /** 失败记录下载文件名 */
    private String failureFileName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String createBy;
}
