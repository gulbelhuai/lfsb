package com.ruoyi.shebao.dto.historicalimport;

import lombok.Data;

@Data
public class HistoricalImportResult
{
    private Long batchId;
    private int totalRows;
    private int successRows;
    private int failureRows;
    private String message;
    private boolean hasFailureFile;
}
