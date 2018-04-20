package org.lpw.tephra.office.pptx

import com.alibaba.fastjson.JSONObject
import javax.inject.Inject
import org.lpw.tephra.office.OfficeHelper
import org.springframework.stereotype.Component

import scala.xml.XML

/**
  * @author lpw
  */
@Component("tephra.office.pptx.parser.presentation")
class PresentationParserImpl extends Parser {
    @Inject private val officeHelper: OfficeHelper = null

    override def getName: String = "presentation"

    override def parse(path: String, rels: JSONObject): JSONObject = {
        val size: JSONObject = new JSONObject()
        val sldSz = XML.loadFile(path) \ "sldSz"
        size.put("width", officeHelper.emuToPixel((sldSz \ "@cx").text.toInt))
        size.put("height", officeHelper.emuToPixel((sldSz \ "@cy").text.toInt))

        size
    }
}
