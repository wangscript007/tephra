package org.lpw.tephra.office.pptx.parser

import com.alibaba.fastjson.JSONObject
import org.springframework.stereotype.Component

import scala.xml.XML

/**
  * @author lpw
  */
@Component("tephra.office.pptx.parser.fill.xml")
class FillXmlParserImpl extends FillXmlParser {
    private val map = Map("l" -> "left", "t" -> "top", "r" -> "right", "b" -> "bottom")

    override def putFillRect(xml: String, texture: JSONObject): Unit = {
        (XML.loadString(xml) \ "blipFill" \ "stretch" \ "fillRect").foreach(node => {
            node.attributes.foreach(md => {
                texture.put(map(md.key), md.value.text.toInt / 100000.0D)
            })
        })
    }
}
