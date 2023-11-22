package com.example.taskmanager.util;

import com.example.taskmanager.entity.Task;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

public class ExportTaskExcel {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Task> listTasks;

    public ExportTaskExcel(List<Task> listTasks) {
        this.listTasks = listTasks;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Tasks");
    }

    private void writeHeaderLine() {
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "ID zadania", style);
        createCell(row, 1, "ID właściciela zadania", style);
        createCell(row, 2, "Właściciel zadania", style);
        createCell(row, 3, "Nazwa zadania", style);
        createCell(row, 4, "Opis zadania", style);
        createCell(row, 5, "Priorytet zadania", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Task task : listTasks) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, task.getId(), style);
            createCell(row, columnCount++, task.getUser().getId(), style);
            createCell(row, columnCount++, task.getUser().getEmail(), style);
            createCell(row, columnCount++, task.getTaskName(), style);
            createCell(row, columnCount++, task.getTaskDescription(), style);
            createCell(row, columnCount++, task.getPriority(), style);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

}
