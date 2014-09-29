/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.dashboard.displayer.table;

import au.com.bytecode.opencsv.CSVWriter;
import org.jboss.dashboard.domain.Interval;
import org.jboss.dashboard.LocaleManager;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.DateFormatConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExportTool {

    public static final String FORMAT_EXCEL = "xlsx";
    public static final String FORMAT_CSV = "csv";
    public static final String DEFAULT_SEPARATOR_CHAR = ";";
    public static final String DEFAULT_QUOTE_CHAR = "\"";
    public static final String DEFAULT_ESCAPE_CHAR = "\\";
    public static final String DEFAULT_DATE_PATTERN = "MM-dd-yyyy HH:mm:ss";
    public static final String DEFAULT_NUMBER_PATTERN = "#,###.##";

    protected String dateFormatPattern = "dd/MM/yyyy HH:mm:ss";
    protected String numberFormatPattern = "#,###.##########";

    private transient static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExportTool.class);
    private DecimalFormat decf = new DecimalFormat(numberFormatPattern);
    private DateFormat datef = new SimpleDateFormat(dateFormatPattern);

    public InputStream exportCSV(TableModel tableModel) throws Exception {
        if (tableModel == null) throw new IllegalArgumentException("Invalid tabla data model!");
        int columnCount = tableModel.getColumnCount();
        int rowCount = tableModel.getRowCount();

        List<String[]> lines = new ArrayList<String[]>(rowCount+1);

        String[] line = new String[columnCount];
        for (int cc = 0; cc < columnCount; cc++) {
            line[cc] = tableModel.getColumnName(cc);
        }
        lines.add(line);

        for (int rc = 0; rc < rowCount; rc++) {
            line = new String[columnCount];
            for (int cc = 0; cc < columnCount; cc++) {
                line[cc] = formatAsString(tableModel.getValueAt(rc, cc));
            }
            lines.add(line);
        }

        StringWriter swriter = new StringWriter();
        CSVWriter writer = new CSVWriter(swriter,   DEFAULT_SEPARATOR_CHAR.charAt(0),
                                                    DEFAULT_QUOTE_CHAR.charAt(0),
                                                    DEFAULT_ESCAPE_CHAR.charAt(0));

        writer.writeAll(lines);
        writer.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(baos));
        bWriter.write(swriter.toString());
        bWriter.close();

        return new ByteArrayInputStream(baos.toByteArray());
    }

    protected String formatAsString(Object value) {
        if (value instanceof Number) return decf.format(value);
        else if (value instanceof Date) return datef.format(value);
        else if (value instanceof Interval) return ((Interval)value).getDescription(LocaleManager.currentLocale());
        else return value.toString();
    }

    public InputStream exportExcel(TableModel tableModel) {
        // TODO?: Excel 2010 limits: 1,048,576 rows by 16,384 columns; row width 255 characters
        if (tableModel == null) throw new IllegalArgumentException("Invalid tabla data model!");
        int columnCount = tableModel.getColumnCount();
        int rowCount = tableModel.getRowCount() + 1; //Include header row
        int row = 0;

        SXSSFWorkbook wb = new SXSSFWorkbook(100); // keep 100 rows in memory, exceeding rows will be flushed to disk
        Map<String, CellStyle> styles = createStyles(wb);
        Sheet sh = wb.createSheet("Sheet 1");

        // General setup
        sh.setDisplayGridlines(true);
        sh.setPrintGridlines(false);
        sh.setFitToPage(true);
        sh.setHorizontallyCenter(true);
        PrintSetup printSetup = sh.getPrintSetup();
        printSetup.setLandscape(true);

        // Create header
        Row header = sh.createRow(row++);
        header.setHeightInPoints(20f);
        for (int i = 0; i < columnCount; i++) {
            Cell cell = header.createCell(i);
            cell.setCellStyle(styles.get("header"));
            cell.setCellValue(tableModel.getColumnName(i));
        }

        // Create data rows
        for(; row < rowCount; row++){
            Row _row = sh.createRow(row);
            for(int cellnum = 0; cellnum < columnCount; cellnum++){
                Cell cell = _row.createCell(cellnum);
                Object value = tableModel.getValueAt(row-1, cellnum);
                if (value instanceof Short || value instanceof Long || value instanceof Integer || value instanceof BigInteger) {
                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                    cell.setCellStyle(styles.get("integer_number_cell"));
                    if (value!= null) cell.setCellValue( ((Number)value).doubleValue());
                } else if (value instanceof Float || value instanceof Double || value instanceof BigDecimal) {
                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                    cell.setCellStyle(styles.get("decimal_number_cell"));
                    if (value!= null) cell.setCellValue( ((Number)value).doubleValue());
                } else if (value instanceof Date) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cell.setCellStyle(styles.get("date_cell"));
                    if (value!= null) cell.setCellValue( (Date)value );
                } else if (value instanceof Interval) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cell.setCellStyle(styles.get("text_cell"));
                    if (value!= null) cell.setCellValue( ((Interval)value).getDescription(LocaleManager.currentLocale()) );
                } else {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cell.setCellStyle(styles.get("text_cell"));
                    if (value!= null) cell.setCellValue( value.toString() );
                }
            }

        }

        // Adjust column size
        for (int i = 0; i < columnCount; i++) {
            sh.autoSizeColumn(i);
        }

        ByteArrayInputStream bis = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            bis = new ByteArrayInputStream(bos.toByteArray());
            bos.close();
        } catch (IOException e) {
            log.error("Data export error: ", e);
        }

        // Dispose of temporary files backing this workbook on disk
        if (!wb.dispose()) log.warn("Could not dispose of temporary file associated to data export!");

        return bis;
    }

    private Map<String, CellStyle> createStyles(Workbook wb){
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        CellStyle style;

        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short)12);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(titleFont);
        style.setWrapText(false);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
        styles.put("header", style);

        Font cellFont = wb.createFont();
        cellFont.setFontHeightInPoints((short)10);
        cellFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        style.setFont(cellFont);
        style.setWrapText(false);
        style.setDataFormat(wb.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(3)));
        styles.put("integer_number_cell", style);

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        style.setFont(cellFont);
        style.setWrapText(false);
        style.setDataFormat(wb.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(4)));
        styles.put("decimal_number_cell", style);

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        style.setFont(cellFont);
        style.setWrapText(false);
        style.setDataFormat( (short) BuiltinFormats.getBuiltinFormat("text") );
        styles.put("text_cell", style);

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_BOTTOM);
        style.setFont(cellFont);
        style.setWrapText(false);
        style.setDataFormat(wb.createDataFormat().getFormat(DateFormatConverter.convert(LocaleManager.currentLocale(), dateFormatPattern)));
        styles.put("date_cell", style);
        return styles;
    }
}
