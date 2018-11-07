package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.lpw.tephra.poi.StreamWriter;
import org.lpw.tephra.util.Validator;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCell;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Component("tephra.poi.pptx.table")
public class TableParserImpl implements Parser {
    @Inject
    private Validator validator;
    @Inject
    private ParserHelper parserHelper;
    @Inject
    private TextParser textParser;
    private Map<String, TableCell.BorderEdge> edges;

    public TableParserImpl() {
        edges = new HashMap<>();
        edges.put("Top", TableCell.BorderEdge.top);
        edges.put("Bottom", TableCell.BorderEdge.bottom);
        edges.put("Left", TableCell.BorderEdge.left);
        edges.put("Right", TableCell.BorderEdge.right);
    }

    @Override
    public String getType() {
        return TYPE_TABLE;
    }

    @Override
    public boolean parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object) {
        XSLFTable xslfTable = xslfSlide.createTable();
        xslfTable.setAnchor(parserHelper.getRectangle(object));
        xslfTable.getCTTable().getTblPr().setTableStyleId("{5940675A-B579-460E-94D1-54222C63F5DA}");
        JSONArray rows = object.getJSONObject("table").getJSONArray("rows");
        Map<String, Integer> spans = new HashMap<>();
        Color borderColor = null;
        for (int i = 0; i < rows.size(); i++) {
            XSLFTableRow xslfTableRow = xslfTable.addRow();
            JSONArray cols = rows.getJSONArray(i);
            for (int j = 0; j < cols.size(); j++) {
                XSLFTableCell xslfTableCell = xslfTableRow.addCell();
                JSONObject col = cols.getJSONObject(j);
                if (borderColor == null)
                    borderColor = findBorderColor(col.getJSONObject("style"));
                if (borderColor != null) {
                    for (String key : edges.keySet()) {
                        xslfTableCell.setBorderWidth(edges.get(key), 1.0D);
                        xslfTableCell.setBorderColor(edges.get(key), borderColor);
                    }
                }

                CTTableCell ctTableCell = (CTTableCell) xslfTableCell.getXmlObject();
                JSONObject data = col.getJSONObject("data");
                setSpan(spans, ctTableCell, data, i, j);
                if (col.containsKey("width"))
                    xslfTable.setColumnWidth(j, col.getDoubleValue("width"));
                if (col.containsKey("height"))
                    xslfTable.setRowHeight(i, col.getDoubleValue("height"));

                if (col.containsKey("fillColor"))
                    xslfTableCell.setFillColor(parserHelper.getColor(col, "fillColor"));
                if (col.containsKey("elements")) {
                    JSONArray elements = col.getJSONArray("elements");
                    XSLFTextParagraph xslfTextParagraph = xslfTableCell.addNewTextParagraph();
                    for (int k = 0, size = elements.size(); k < size; k++) {
                        JSONObject element = elements.getJSONObject(k);
                        String value = element.getJSONObject("data").getString("value");
                        if (validator.isEmpty(value)) {
                            textParser.newTextRun(xslfTextParagraph, col, element).setText("");
                            xslfTextParagraph = xslfTableCell.addNewTextParagraph();
                        } else
                            textParser.newTextRun(xslfTextParagraph, col, element).setText(value);
                    }
                } else if (data.containsKey("value"))
                    textParser.newTextRun(xslfTableCell.addNewTextParagraph(), col, new JSONObject()).setText(data.getString("value"));
            }
        }

        return true;
    }

    private Color findBorderColor(JSONObject style) {
        for (String key : edges.keySet()) {
            String name = "border" + key + "Color";
            if (style.containsKey(name))
                return parserHelper.getColor(style, name);
        }

        return null;
    }

    private void setSpan(Map<String, Integer> spans, CTTableCell ctTableCell, JSONObject data, int i, int j) {
        String key = "col-" + i + "-" + j;
        if (spans.containsKey(key)) {
            int span = spans.get(key);
            if (span == 1)
                ctTableCell.setHMerge(true);
            else
                ctTableCell.setGridSpan(span);
        }
        key = "row-" + i + "-" + j;
        if (spans.containsKey(key)) {
            int span = spans.get(key);
            if (span == 1)
                ctTableCell.setVMerge(true);
            else
                ctTableCell.setRowSpan(span);
        }

        int rowSpan = data.containsKey("rowSpan") ? data.getIntValue("rowSpan") : 1;
        int colSpan = data.containsKey("colSpan") ? data.getIntValue("colSpan") : 1;
        if (rowSpan <= 1 && colSpan <= 1)
            return;

        if (rowSpan == 1) {
            ctTableCell.setGridSpan(colSpan);
            for (int k = 1; k < colSpan; k++)
                spans.put("col-" + i + "-" + (j + k), 1);

            return;
        }

        if (colSpan == 1) {
            ctTableCell.setRowSpan(rowSpan);
            for (int k = 1; k < rowSpan; k++)
                spans.put("row-" + (i + k) + "-" + j, 1);

            return;
        }

        ctTableCell.setRowSpan(rowSpan);
        ctTableCell.setGridSpan(colSpan);
        for (int k = 1; k < rowSpan; k++) {
            spans.put("col-" + (i + k) + "-" + j, colSpan);
            spans.put("row-" + (i + k) + "-" + j, 1);
        }
        for (int k = 1; k < colSpan; k++) {
            spans.put("row-" + i + "-" + (j + k), rowSpan);
            spans.put("col-" + i + "-" + (j + k), 1);
        }

        for (int k = 1; k < rowSpan; k++) {
            for (int l = 1; l < colSpan; l++) {
                spans.put("row-" + (i + k) + "-" + (j + l), 1);
                spans.put("col-" + (i + k) + "-" + (j + l), 1);
            }
        }
    }

    @Override
    public boolean parse(JSONObject object, XSLFShape xslfShape, StreamWriter streamWriter) {
        return false;
    }
}
