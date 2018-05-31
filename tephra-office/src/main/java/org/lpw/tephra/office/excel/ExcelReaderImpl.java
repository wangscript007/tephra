package org.lpw.tephra.office.excel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.lpw.tephra.office.MediaWriter;
import org.lpw.tephra.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.InputStream;

/**
 * @author lpw
 */
@Component("tephra.office.excel.reader")
public class ExcelReaderImpl implements ExcelReader {
    @Inject
    private Logger logger;

    @Override
    public JSONObject read(InputStream inputStream, MediaWriter mediaWriter) {
        JSONObject object = new JSONObject();
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            JSONArray sheets = new JSONArray();
            workbook.forEach(sheet -> {
                JSONObject sheetJson = new JSONObject();
                sheetJson.put("name", sheet.getSheetName());
                sheetJson.put("first", sheet.getFirstRowNum());
                sheetJson.put("last", sheet.getLastRowNum());
                JSONArray rows = new JSONArray();
                sheet.forEach(row -> {
                    JSONObject rowJson = new JSONObject();
                    rowJson.put("first", row.getFirstCellNum());
                    rowJson.put("last", row.getLastCellNum());
                    JSONArray cells = new JSONArray();
                    row.forEach(cell -> {
                        JSONObject cellJson = new JSONObject();
                        cellJson.put("type", cell.getCellTypeEnum().name().toLowerCase());
                        switch (cell.getCellTypeEnum()) {
                            case STRING:
                                cellJson.put("value", cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                cellJson.put("value", cell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                cellJson.put("value", cell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                cellJson.put("formula", cell.getCellFormula());
                                break;
                        }
                        cells.add(cellJson);
                    });
                    rowJson.put("cells", cells);
                    rows.add(rowJson);
                });
                sheetJson.put("rows", rows);
                sheets.add(sheetJson);
            });
            object.put("sheets", sheets);
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            logger.warn(e, "读取并解析Excel数据时发生异常！");
        }

        return object;
    }
}
