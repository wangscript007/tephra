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
import org.lpw.tephra.poi.StreamWriter;
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
    private ParserHelper parserHelper;
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
                JSONObject style = col.getJSONObject("style");
                edges.forEach((key, edge) -> {
                    String name = "border" + key + "Width";
                    if (style.containsKey(name))
                        xslfTableCell.setBorderWidth(edge, style.getDoubleValue(name));
                    name = "border" + key + "Color";
                    if (style.containsKey(name))
                        xslfTableCell.setBorderColor(edge, parserHelper.getColor(style, name));
                });
                if (style.containsKey("backgroundColor"))
                    xslfTableCell.setFillColor(parserHelper.getColor(style, "backgroundColor"));
                xslfTableCell.setText(col.getJSONObject("data").getString("value"));
            }
        }

        return true;
    }

    @Override
    public boolean parse(JSONObject object, XSLFShape xslfShape, StreamWriter streamWriter) {
        return false;
    }
}
