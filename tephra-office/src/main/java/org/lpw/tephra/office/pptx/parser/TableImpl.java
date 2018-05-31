package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.MediaWriter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.table")
public class TableImpl implements Graphic {
    @Inject
    private OfficeHelper officeHelper;
    @Inject
    private Parser parser;

    @Override
    public int getSort() {
        return 11;
    }

    @Override
    public void parse(XSLFGraphicFrame xslfGraphicFrame, MediaWriter mediaWriter, JSONObject shape) {
        if (!(xslfGraphicFrame instanceof XSLFTable))
            return;

        XSLFTable xslfTable = (XSLFTable) xslfGraphicFrame;
        JSONArray rows = new JSONArray();
        xslfTable.getRows().forEach(xslfTableRow -> {
            JSONArray cells = new JSONArray();
            xslfTableRow.getCells().forEach(xslfTableCell -> {
                JSONObject cell = new JSONObject();
                parseSpan(xslfTableCell, cell);
                parser.parse(xslfTableCell, mediaWriter, cell, false);
                cells.add(cell);
            });
            if (cells.isEmpty())
                return;

            JSONObject row = new JSONObject();
            row.put("cells", cells);
            rows.add(row);
        });
        if (rows.isEmpty())
            return;

        JSONObject table = new JSONObject();
        table.put("rows", rows);
        shape.put("table", table);
    }

    private void parseSpan(XSLFTableCell xslfTableCell, JSONObject cell) {
        JSONObject span = new JSONObject();
        span.put("column", xslfTableCell.getGridSpan());
        span.put("row", xslfTableCell.getRowSpan());
        cell.put("span", span);
    }
}
