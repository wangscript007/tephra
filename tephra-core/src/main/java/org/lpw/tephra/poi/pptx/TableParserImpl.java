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
        JSONArray rows = object.getJSONObject("table").getJSONArray("rows");
        for (int i = 0; i < rows.size(); i++) {
            XSLFTableRow xslfTableRow = xslfTable.addRow();
            JSONArray cols = rows.getJSONArray(i);
            for (int j = 0; j < cols.size(); j++) {
                XSLFTableCell xslfTableCell = xslfTableRow.addCell();
                JSONObject col = cols.getJSONObject(j);
                JSONObject data = col.getJSONObject("data");
                CTTableCell ctTableCell = (CTTableCell) xslfTableCell.getXmlObject();
                if (data.containsKey("rowSpan"))
                    ctTableCell.setRowSpan(data.getIntValue("rowSpan"));
                if (data.containsKey("colSpan"))
                    ctTableCell.setGridSpan(data.getIntValue("colSpan"));
                if (col.containsKey("width"))
                    xslfTable.setColumnWidth(j, col.getDoubleValue("width"));
                if (col.containsKey("height"))
                    xslfTable.setRowHeight(i, col.getDoubleValue("height"));

                JSONObject style = col.getJSONObject("style");
                edges.forEach((key, edge) -> {
                    String name = "border" + key + "Width";
                    if (style.containsKey(name))
                        xslfTableCell.setBorderWidth(edge, style.getDoubleValue(name));
                    name = "border" + key + "Color";
                    if (style.containsKey(name))
                        xslfTableCell.setBorderColor(edge, parserHelper.getColor(style, name));
                });
                if (style.containsKey("fillColor"))
                    xslfTableCell.setFillColor(parserHelper.getColor(style, "fillColor"));
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
                } else
                    textParser.newTextRun(xslfTableCell.addNewTextParagraph(), col, new JSONObject())
                            .setText(data.getString("value"));
            }
        }

        return true;
    }

    @Override
    public boolean parse(JSONObject object, XSLFShape xslfShape, StreamWriter streamWriter) {
        return false;
    }
}
