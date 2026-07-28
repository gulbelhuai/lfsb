package com.ruoyi.shebao.service.historicalimport;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.Type;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.reflect.ReflectUtils;
import com.ruoyi.shebao.domain.StreetOffice;
import com.ruoyi.shebao.domain.VillageCommittee;
import com.ruoyi.shebao.dto.historicalimport.DemolitionHistoricalImportDto;
import com.ruoyi.shebao.dto.historicalimport.LandLossHistoricalImportDto;
import com.ruoyi.shebao.mapper.StreetOfficeMapper;
import com.ruoyi.shebao.mapper.VillageCommitteeMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 历史数据导入模板导出（含字典说明 Sheet）
 */
@Component
@RequiredArgsConstructor
public class HistoricalImportTemplateExporter
{
    private static final String DATA_SHEET_NAME = "导入数据";
    private static final String DICT_SHEET_NAME = "字典说明";
    private static final String IMPORT_INSTRUCTIONS = "导入说明：\n"
            + "1、村委会只需要填写/号之后的部分即可\n"
            + "2、年月的格式为yyyy-MM，日期的格式为yyyy-MM-dd\n"
            + "3、参保状态为「终止」时须填写注销时间与注销原因；注销时间不能晚于今天";

    private final StreetOfficeMapper streetOfficeMapper;
    private final VillageCommitteeMapper villageCommitteeMapper;

    public void exportLandLossTemplate(HttpServletResponse response) throws IOException
    {
        exportTemplate(response, LandLossHistoricalImportDto.class);
    }

    public void exportDemolitionTemplate(HttpServletResponse response) throws IOException
    {
        exportTemplate(response, DemolitionHistoricalImportDto.class);
    }

    private void exportTemplate(HttpServletResponse response, Class<?> dtoClass) throws IOException
    {
        try (Workbook workbook = new XSSFWorkbook())
        {
            createDataSheet(workbook, dtoClass);
            createDictSheet(workbook);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            workbook.write(response.getOutputStream());
        }
    }

    /**
     * 用 XSSF 写失败记录，避免 ExcelUtil/SXSSF 在无字体服务器上触发 AWT FontManager 崩溃。
     *
     * @return 相对 profile 的访问路径，如 /profile/historical-import/failures/failure_xxx.xlsx
     */
    public String exportLandLossFailureFile(List<LandLossHistoricalImportDto> failedRows) throws IOException
    {
        return exportFailureFile(LandLossHistoricalImportDto.class, failedRows);
    }

    public String exportDemolitionFailureFile(List<DemolitionHistoricalImportDto> failedRows) throws IOException
    {
        return exportFailureFile(DemolitionHistoricalImportDto.class, failedRows);
    }

    private <T> String exportFailureFile(Class<T> dtoClass, List<T> failedRows) throws IOException
    {
        File dir = new File(RuoYiConfig.getProfile(), "historical-import" + File.separator + "failures");
        if (!dir.exists() && !dir.mkdirs())
        {
            throw new IOException("无法创建失败记录目录：" + dir.getAbsolutePath());
        }
        String fileName = HistoricalImportFileNames.storedFailureFileName();
        File target = new File(dir, fileName);
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(target))
        {
            writeExportSheet(workbook, "失败记录", dtoClass, failedRows);
            workbook.write(out);
        }
        return "/profile/historical-import/failures/" + fileName;
    }

    /**
     * 保存导入源文件（磁盘用 ASCII 别名）。
     *
     * @return 相对 profile 的访问路径，如 /profile/historical-import/sources/import_xxx.xlsx
     */
    public String saveImportSourceFile(byte[] content, String originalFileName) throws IOException
    {
        if (content == null || content.length == 0)
        {
            throw new IOException("导入文件内容为空");
        }
        File dir = new File(RuoYiConfig.getProfile(), "historical-import" + File.separator + "sources");
        if (!dir.exists() && !dir.mkdirs())
        {
            throw new IOException("无法创建导入源文件目录：" + dir.getAbsolutePath());
        }
        String storedName = HistoricalImportFileNames.storedSourceFileName(originalFileName);
        File target = new File(dir, storedName);
        try (FileOutputStream out = new FileOutputStream(target))
        {
            out.write(content);
        }
        return "/profile/historical-import/sources/" + storedName;
    }

    private void createDataSheet(Workbook workbook, Class<?> dtoClass)
    {
        Sheet sheet = workbook.createSheet(DATA_SHEET_NAME);
        CellStyle headerStyle = buildHeaderStyle(workbook);
        Row headerRow = sheet.createRow(0);
        List<ExcelField> fields = resolveImportFields(dtoClass);
        for (int i = 0; i < fields.size(); i++)
        {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields.get(i).name());
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 18 * 256);
        }
        sheet.createFreezePane(0, 1);
    }

    private <T> void writeExportSheet(Workbook workbook, String sheetName, Class<T> dtoClass, List<T> rows)
    {
        Sheet sheet = workbook.createSheet(sheetName);
        CellStyle headerStyle = buildHeaderStyle(workbook);
        List<ExcelField> fields = resolveExportFields(dtoClass);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < fields.size(); i++)
        {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields.get(i).name());
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 18 * 256);
        }
        if (rows != null)
        {
            for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++)
            {
                T rowData = rows.get(rowIdx);
                Row row = sheet.createRow(rowIdx + 1);
                for (int col = 0; col < fields.size(); col++)
                {
                    Object value = ReflectUtils.invokeGetter(rowData, fields.get(col).fieldName());
                    if (value != null)
                    {
                        row.createCell(col).setCellValue(String.valueOf(value));
                    }
                }
            }
        }
        sheet.createFreezePane(0, 1);
    }

    private void createDictSheet(Workbook workbook)
    {
        Sheet sheet = workbook.createSheet(DICT_SHEET_NAME);
        CellStyle headerStyle = buildHeaderStyle(workbook);
        CellStyle instructionStyle = buildInstructionStyle(workbook);

        List<DictColumn> columns = buildLandLossDictColumns();
        int colCount = Math.max(columns.size(), 1);

        Row instructionRow = sheet.createRow(0);
        Cell instructionCell = instructionRow.createCell(0);
        instructionCell.setCellValue(IMPORT_INSTRUCTIONS);
        instructionCell.setCellStyle(instructionStyle);
        if (colCount > 1)
        {
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colCount - 1));
        }
        instructionRow.setHeightInPoints(68);

        Row headerRow = sheet.createRow(1);
        for (int col = 0; col < columns.size(); col++)
        {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(columns.get(col).title());
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(col, 22 * 256);
        }

        int maxRows = columns.stream().mapToInt(c -> c.values().size()).max().orElse(0);
        for (int rowIdx = 0; rowIdx < maxRows; rowIdx++)
        {
            Row row = sheet.createRow(rowIdx + 2);
            for (int col = 0; col < columns.size(); col++)
            {
                List<String> values = columns.get(col).values();
                if (rowIdx < values.size())
                {
                    row.createCell(col).setCellValue(values.get(rowIdx));
                }
            }
        }
        sheet.createFreezePane(0, 2);
    }

    private List<DictColumn> buildLandLossDictColumns()
    {
        List<DictColumn> columns = new ArrayList<>();
        columns.add(new DictColumn("参保状态", dictLabels("shebao_subsidy_status")));
        columns.add(new DictColumn("人员状态", dictLabels("shebao_person_status")));
        columns.add(new DictColumn("注销原因", dictLabels("cancel_reason")));
        columns.add(new DictColumn("是否村合作经济组织成员", List.of("是", "否")));
        columns.add(new DictColumn("发放机构", dictLabels("shebao_grant_org")));
        columns.add(new DictColumn("暂停原因", dictLabels("pause_reason")));
        columns.add(new DictColumn("所属街道办", loadStreetOfficeNames()));
        columns.add(new DictColumn("所属村委会", loadVillageCommitteeNames()));
        return columns;
    }

    private List<String> dictLabels(String dictType)
    {
        String labels = DictUtils.getDictLabels(dictType);
        if (StringUtils.isBlank(labels))
        {
            return List.of();
        }
        return Arrays.stream(labels.split(DictUtils.SEPARATOR))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    private List<String> loadStreetOfficeNames()
    {
        return streetOfficeMapper.selectList(new LambdaQueryWrapper<StreetOffice>()
                        .eq(StreetOffice::getDelFlag, "0")
                        .orderByAsc(StreetOffice::getStreetName))
                .stream()
                .map(StreetOffice::getStreetName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    private List<String> loadVillageCommitteeNames()
    {
        Map<Long, String> streetNameMap = streetOfficeMapper.selectList(new LambdaQueryWrapper<StreetOffice>()
                        .eq(StreetOffice::getDelFlag, "0"))
                .stream()
                .collect(Collectors.toMap(StreetOffice::getId, StreetOffice::getStreetName, (a, b) -> a));
        return villageCommitteeMapper.selectList(new LambdaQueryWrapper<VillageCommittee>()
                        .eq(VillageCommittee::getDelFlag, "0")
                        .orderByAsc(VillageCommittee::getStreetOfficeId)
                        .orderByAsc(VillageCommittee::getVillageName))
                .stream()
                .map(vc -> {
                    String street = streetNameMap.getOrDefault(vc.getStreetOfficeId(), "");
                    if (StringUtils.isBlank(street))
                    {
                        return vc.getVillageName();
                    }
                    return street + " / " + vc.getVillageName();
                })
                .collect(Collectors.toList());
    }

    private List<ExcelField> resolveImportFields(Class<?> dtoClass)
    {
        List<ExcelField> fields = new ArrayList<>();
        for (Field field : dtoClass.getDeclaredFields())
        {
            Excel excel = field.getAnnotation(Excel.class);
            if (excel == null)
            {
                continue;
            }
            if (excel.type() == Type.EXPORT)
            {
                continue;
            }
            fields.add(new ExcelField(excel.sort(), excel.name(), field.getName()));
        }
        fields.sort(Comparator.comparingInt(ExcelField::sort));
        return fields;
    }

    /** 失败导出：导入列 + 失败原因 */
    private List<ExcelField> resolveExportFields(Class<?> dtoClass)
    {
        List<ExcelField> fields = new ArrayList<>();
        for (Field field : dtoClass.getDeclaredFields())
        {
            Excel excel = field.getAnnotation(Excel.class);
            if (excel == null)
            {
                continue;
            }
            if (excel.type() == Type.IMPORT)
            {
                continue;
            }
            fields.add(new ExcelField(excel.sort(), excel.name(), field.getName()));
        }
        fields.sort(Comparator.comparingInt(ExcelField::sort));
        return fields;
    }

    private CellStyle buildHeaderStyle(Workbook workbook)
    {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle buildInstructionStyle(Workbook workbook)
    {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private record DictColumn(String title, List<String> values)
    {
    }

    private record ExcelField(int sort, String name, String fieldName)
    {
    }
}
