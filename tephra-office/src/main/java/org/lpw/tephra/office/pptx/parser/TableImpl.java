package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.lpw.tephra.office.MediaReader;
import org.lpw.tephra.office.MediaWriter;
import org.lpw.tephra.office.OfficeHelper;
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
    public void parseShape(XSLFSlide xslfSlide, XSLFGraphicFrame xslfGraphicFrame, MediaWriter mediaWriter, JSONObject shape) {
        if (!(xslfGraphicFrame instanceof XSLFTable))
            return;

        XSLFTable xslfTable = (XSLFTable) xslfGraphicFrame;
        JSONArray rows = new JSONArray();
        xslfTable.getRows().forEach(xslfTableRow -> {
            JSONArray cells = new JSONArray();
            xslfTableRow.getCells().forEach(xslfTableCell -> {
                JSONObject cell = new JSONObject();
                parseSpan(xslfTableCell, cell);
                parseBorder(xslfTableCell, cell);
                parser.parseShape(xslfTableCell, mediaWriter, cell, false);
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

    private void parseBorder(XSLFTableCell xslfTableCell, JSONObject cell) {
        JSONObject border = new JSONObject();
        for (TableCell.BorderEdge edge : TableCell.BorderEdge.values()) {
            Double width = xslfTableCell.getBorderWidth(edge);
            if (width == null || width <= 0.0D)
                continue;

            JSONObject object = new JSONObject();
            object.put("width", width);
            object.put("color", officeHelper.colorToJson(xslfTableCell.getBorderColor(edge)));
            object.put("style", xslfTableCell.getBorderStyle(edge));
            border.put(edge.name(), object);
            System.out.println("11:" + xslfTableCell.getBorderStyle(edge));
            System.out.println("22:" + xslfTableCell.getBorderStyle(edge).getLineCap());
            System.out.println("22:" + xslfTableCell.getBorderStyle(edge).getLineCap());
            System.out.println("33:" + xslfTableCell.getBorderStyle(edge).getLineDash());
            System.out.println("44:" + xslfTableCell.getBorderStyle(edge).getLineCompound());
        }

        if (!border.isEmpty())
            cell.put("border", border);
    }

    @Override
    public XSLFShape createShape(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, MediaReader mediaReader, JSONObject shape) {
        return null;
    }
}
