package org.lpw.tephra.office.pptx.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableStyle;
import org.apache.poi.xslf.usermodel.XSLFTheme;
import org.lpw.tephra.office.OfficeHelper;
import org.lpw.tephra.office.pptx.ReaderContext;
import org.lpw.tephra.office.pptx.WriterContext;
import org.lpw.tephra.util.Validator;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableBackgroundStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyle;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Color;

/**
 * @author lpw
 */
@Component("tephra.office.pptx.parser.table")
public class TableImpl implements Graphic {
    @Inject
    private Validator validator;
    @Inject
    private OfficeHelper officeHelper;
    @Inject
    private Parser parser;

    @Override
    public int getSort() {
        return 11;
    }

    @Override
    public void parseShape(ReaderContext readerContext, XSLFGraphicFrame xslfGraphicFrame, JSONObject shape) {
        if (!(xslfGraphicFrame instanceof XSLFTable))
            return;

        XSLFTable xslfTable = (XSLFTable) xslfGraphicFrame;
        JSONObject table = new JSONObject();
        XSLFTheme xslfTheme = readerContext.getTheme();
        CTTableStyle ctTableStyle = findTableStyle(readerContext, xslfTable);
        if (xslfTheme != null && ctTableStyle != null) {
            parseFill(xslfTheme, ctTableStyle, table);
            System.out.println("#######################");
            System.out.println(ctTableStyle.getBand1V());
            System.out.println(ctTableStyle.getBand1H());
        }
        JSONArray rows = new JSONArray();
        xslfTable.getRows().forEach(xslfTableRow -> {
            JSONArray cells = new JSONArray();
            xslfTableRow.getCells().forEach(xslfTableCell -> {
                JSONObject cell = new JSONObject();
                parseSpan(xslfTableCell, cell);
                parseBorder(xslfTableCell, cell);
                parser.parseShape(readerContext, xslfTableCell, cell);
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

        table.put("rows", rows);
        shape.put("table", table);
    }

    private CTTableStyle findTableStyle(ReaderContext readerContext, XSLFTable xslfTable) {
        for (XSLFTableStyle xslfTableStyle : readerContext.getXmlSlideShow().getTableStyles())
            if (xslfTableStyle.getStyleId().equals(xslfTable.getCTTable().getTblPr().getTableStyleId()))
                return xslfTableStyle.getXmlObject();

        return null;
    }

    private void parseFill(XSLFTheme xslfTheme, CTTableStyle ctTableStyle, JSONObject object) {
        CTTableBackgroundStyle ctTableBackgroundStyle = ctTableStyle.getTblBg();
        if (ctTableBackgroundStyle == null)
            return;

        CTStyleMatrixReference ctStyleMatrixReference = ctTableBackgroundStyle.getFillRef();
        if (ctStyleMatrixReference == null)
            return;

        Color color = getSrgbClr(xslfTheme, ctStyleMatrixReference.getSchemeClr().getVal().toString());
        if (color == null)
            return;

        JSONObject fill = new JSONObject();
        fill.put("color", officeHelper.colorToJson(color));
        object.put("fill", fill);
    }

    private Color getSrgbClr(XSLFTheme xslfTheme, String name) {
        if (xslfTheme == null || validator.isEmpty(name))
            return null;

        byte[] bytes = xslfTheme.getCTColor(name).getSrgbClr().getVal();
        if (bytes.length != 3)
            return null;

        return new Color(bytes[0] & 0xff, bytes[1] & 0xff, bytes[2] & 0xff);
    }

    private void parseSpan(XSLFTableCell xslfTableCell, JSONObject cell) {
        JSONObject span = new JSONObject();
        span.put("column", xslfTableCell.getGridSpan());
        span.put("row", xslfTableCell.getRowSpan());
        cell.put("span", span);
        xslfTableCell.getGridSpan();
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
    public XSLFShape createShape(WriterContext writerContext, JSONObject shape) {
        return null;
    }
}
