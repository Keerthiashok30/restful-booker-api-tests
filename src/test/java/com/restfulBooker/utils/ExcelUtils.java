package com.restfulBooker.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelUtils
{
    public static List<Map<String, String>> getData(String filePath, String sheetName) throws IOException
    {
        List<Map<String, String>> result = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis))
        {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (int c = 0; c < headerRow.getLastCellNum(); c++)
            {
                headers.add(formatter.formatCellValue(headerRow.getCell(c)).trim());
            }

            int flagCol = headers.indexOf("Enabled");
            if (flagCol == -1)
            {
                throw new IllegalStateException("'Enabled' column not found. Headers: " + headers);
            }

            for (int r = 1; r <= sheet.getLastRowNum(); r++)
            {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String flag = formatter.formatCellValue(row.getCell(flagCol)).trim();
                if (!"Y".equalsIgnoreCase(flag)) continue;

                Map<String, String> rowData = new LinkedHashMap<>();
                for (int c = 0; c < headers.size(); c++)
                {
                    if (headers.get(c).isEmpty()) continue;
                    rowData.put(headers.get(c), formatter.formatCellValue(row.getCell(c)).trim());
                }
                result.add(rowData);
            }
        }
        return result;
    }
}
