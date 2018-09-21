package org.lpw.tephra.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

/**
 * @author lpw
 */
public interface TextParser {
    XSLFTextRun newTextRun(XSLFTextParagraph xslfTextParagraph, JSONObject object, JSONObject child);
}
