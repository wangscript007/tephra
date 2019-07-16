package org.lpw.tephra.office.excel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.OutputStream;

/**
 * @author lpw
 */
@Component("tephra.office.excel")
public class ExcelWriterImpl implements ExcelWriter {
    @Inject
    private Logger logger;

    @Override
    public boolean write(JSONObject object, OutputStream outputStream) {
        try {
            Workbook workbook = new XSSFWorkbook();
            JSONArray sheets = object.getJSONArray("sheets");
            for (int sheetIndex = 0, sheetSize = sheets.size(); sheetIndex < sheetSize; sheetIndex++) {
                JSONObject sheetJson = sheets.getJSONObject(sheetIndex);
                Sheet sheet = workbook.createSheet(sheetJson.containsKey("name") ? sheetJson.getString("name") : ("sheet " + sheetIndex));
                int firstRow = sheetJson.containsKey("first") ? sheetJson.getIntValue("first") : 0;
                JSONArray rows = sheetJson.getJSONArray("rows");
                for (int rowIndex = 0, rowSize = rows.size(); rowIndex < rowSize; rowIndex++) {
                    JSONObject rowJson = rows.getJSONObject(rowIndex);
                    int firstCol = rowJson.containsKey("first") ? rowJson.getIntValue("first") : 0;
                    Row row = sheet.createRow(firstRow + rowIndex);
                    JSONArray cells = rowJson.getJSONArray("cells");
                    for (int cellIndex = 0, cellSize = cells.size(); cellIndex < cellSize; cellIndex++) {
                        JSONObject cellJson = cells.getJSONObject(cellIndex);
                        Cell cell = row.createCell(firstCol + cellIndex);
                        cell.setCellValue(cellJson.getString("value"));
                    }
                }
            }
            workbook.write(outputStream);
            outputStream.close();

            return true;
        } catch (Throwable throwable) {
            logger.warn(throwable, "输出Excel时发生异常！");

            return false;
        }
    }
}
